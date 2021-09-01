package bot;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import botutils.CircularZone;
import botutils.Zone;
import core.Cell;
import core.NeuralNetwork;
import core.Pellet;
import core.Player;
import core.World;
import coreutils.Mass;
import coreutils.Vector;
import network.DeepResidualNetwork;
import network.MultilayerPerceptron;


public class TargetBot extends Player {
	
	private NeuralNetwork controller;
	
	private int target;
	private int steps = 0;
	
	private CircularZone toEval;
	
	private int killCnt = 0;
	
	private int mult;
	
	public TargetBot(World in, String name, Color cellColor) {
		super(in, name, cellColor);
		// TODO Auto-generated constructor stub
		
		setTarget((int)(this.getWorld().getPlayers().size() * Math.random()));
		
		this.getControlling().get(0).getMass().setMass(1000);
		
		int[] vect = {10,4,4,2,3};
		
		controller = new DeepResidualNetwork(vect);
		
		toEval = new CircularZone(new Rectangle(this.getControlling().get(0).getCenterX() - this.getControlling().get(0).getRadius() * 10 + Bot.EXTENDED_VIEW,
				this.getControlling().get(0).getCenterY() - this.getControlling().get(0).getRadius() * 10 + Bot.EXTENDED_VIEW, 
				this.getControlling().get(0).getRadius() * 20, this.getControlling().get(0).getRadius() * 20), 10);
		
		mult = 10;
		
	}
	
	public void initialize() {
		super.initialize();
		
		this.getControlling().get(0).getMass().setMass(1000);
	}

	
	/*
	public void step() {
		
		steps++;
		
		double[] inputs = new double[4];
		
		//-1 * (this.getControlling().get(0).getCenterY() - this.getWorld().getPlayers().get(target).getControlling().get(0).getCenterY());
		
		try {
			
			inputs[0] = ((World.MAX_X_SIZE/2 - this.getControlling().get(0).getCenterX())/(World.MAX_X_SIZE/2.0));
			inputs[1] = ((World.MAX_Y_SIZE/2 - this.getControlling().get(0).getCenterY())/(World.MAX_Y_SIZE/2.0));
			inputs[2] = ((World.MAX_X_SIZE/2 - this.getWorld().getPlayers().get(target).getControlling().get(0).getCenterX())/(World.MAX_X_SIZE/2.0));
			inputs[3] = ((World.MAX_Y_SIZE/2 - this.getWorld().getPlayers().get(target).getControlling().get(0).getCenterY())/(World.MAX_Y_SIZE/2.0));
			
			this.controller.setError(this.getWorld().getPlayers().get(target).getControlling().get(0).getCenterLocation().distance(this.getControlling().get(0).getCenterLocation())/Math.max(World.MAX_X_SIZE, World.MAX_Y_SIZE));
			
			Vector vect = ((MultilayerPerceptron) controller).getNextVect(inputs);
			
			//vect.setX(vect.getX() + (int)(-0.5 * (this.getControlling().get(0).getCenterX() - this.getWorld().getPlayers().get(target).getControlling().get(0).getCenterX())));
			//vect.setY(vect.getY() + (int)(-0.5 * (this.getControlling().get(0).getCenterY() - this.getWorld().getPlayers().get(target).getControlling().get(0).getCenterY())));
			
			this.setVect(vect);
		} catch(Exception e) {
			System.out.println(this.getName() + " did not find a target");
		}
		
		
		super.step();
		
		if(this.getWorld().getPlayers().get(target).isDead()) {
			this.target = (int)(this.getWorld().getPlayers().size() * Math.random());
			
			while(this.getWorld().getPlayers().get(target) instanceof TargetBot) {
				this.target = (int)(this.getWorld().getPlayers().size() * Math.random());
			}
			
		}
		
		if(steps % 50 == 0) {
			controller.update();
		}
		
	}*/
	
	public void step() {
		
		if(steps == 1){
			toEval = new CircularZone(new Rectangle(this.getControlling().get(0).getCenterX() - this.getControlling().get(0).getRadius() * 10 + Bot.EXTENDED_VIEW,
					this.getControlling().get(0).getCenterY() - this.getControlling().get(0).getRadius() * 10 + Bot.EXTENDED_VIEW, 
					this.getControlling().get(0).getRadius() * 20, this.getControlling().get(0).getRadius() * 20), 10);
		}
		
		double[] scores = checkZones();
		
		for(Cell kill: this.getControlling()) {
			this.killCnt = kill.getKillCount();
			
			steps++;
		}
		
		if(isDead()) {
			controller.update();
			
			steps = 0;
			
			this.killCnt = 0;
		} else {
			try {
				
				controller.setError((1.0 - (double)(killCnt/((TargetBot)this.getWorld().getSpecializedLeaderBoard().get(this.getWorld().getSpecializedLeaderBoard().size() - 1)).getKillCount())));
			
			} catch(Exception e) {
				//controller.setError(-1);
			}
			
			if(steps % 50 == 0) {
				controller.update();
			}
		}
		
		this.setVect(((DeepResidualNetwork) controller).getNextVect(scores));
		
		
		
		if(this.getTotalMass() > 20000) {
			this.getControlling().get(0).getMass().setMass(20000);
		}
		
		if(((DeepResidualNetwork) controller).doLaunch() > 0.97) {
			super.launch();
		}
		
		super.step();
		
	}
	
	private double[] checkZones() {
		// TODO Auto-generated method stub
		
		ArrayList<Player> targets = new ArrayList<Player>();
		
		
		for(Player p: this.in.getPlayers()) {
			if(!p.isDead() && p.getControlling().get(0).getCenterLocation().distance(this.getControlling().get(0).getCenterLocation()) < this.getControlling().get(0).getRadius() + Bot.EXTENDED_VIEW && !Player.equals(this, p)) {
				targets.add(p);
			}
		}
		
		for(Player p: targets) {
			
			int zone = toEval.contains(p.getControlling().get(0).getCenterLocation());
			
			if(zone != -1) {
				toEval.addToScore(zone, heuristicScore(p));
				
				//System.out.println("ADDED");
			}
		}
		
		return toEval.getAllScores();
	}

	public int heuristicScore(Pellet toScore) {
		return (int) (((Pellet) toScore).getMass() * (this.getControlling().get(0).getRadius() + Bot.EXTENDED_VIEW - this.getControlling().get(0).getCenterLocation().distance(((Pellet) toScore).getCenterLocation())));
	}
	
	public int heuristicScore(Player toScore) {
		return (int) ((toScore.getTotalMass() * (this.getControlling().get(0).getRadius() + Bot.EXTENDED_VIEW - toScore.getControlling().get(0).getCenterLocation().distance(this.getControlling().get(0).getCenterLocation()))) - this.getTotalMass() * this.getControlling().get(0).getRadius() + Bot.EXTENDED_VIEW) * (this.getWorld().getPlayers().size() - this.getWorld().getPosition(toScore));
	}
	

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	public int getKillCount() {
		return killCnt;
	}

	public void setKillCount(int killCnt) {
		this.killCnt = killCnt;
	}
	
	public void paintLeaderboard(Graphics2D g2d) {
		Rectangle rect = new Rectangle(this.getWidth() - 160, 0, 80, 20);
		
		int pos = 1;
		
		for(Player p: in.getSpecializedLeaderBoard()) {
			this.drawCenteredString(g2d, (pos++) + ". " + p.getName(), rect, new Font("sansserif", Font.BOLD, 16));
			rect.setLocation((int)(rect.getX() + 80), (int)rect.getY());
			this.drawCenteredString(g2d, ((TargetBot) p).getKillCount() + "", rect, new Font("sansserif", Font.BOLD, 16));
			rect.setLocation((int)(rect.getX() - 80), (int)(rect.getY() + 20));
			//rect.setLocation((int)(rect.getX()), (int)(rect.getY() + 20));
		}
		
		int playerPos = 0;
		for(Player p: in.getSpecializedLeaderBoard()) {
			if(!Player.equals(p, this)) {
				playerPos++;
			} else {
				return;
			}
		}
		
		
		this.drawCenteredString(g2d, (++playerPos) + ". " + this.getName(), rect,  new Font("sansserif", Font.BOLD, 16));
		rect.setLocation((int)(rect.getX() + 80), (int)rect.getY());
		this.drawCenteredString(g2d, ((TargetBot) this).getKillCount() + "", rect, new Font("sansserif", Font.BOLD, 16));
		
		
		rect.setBounds((int)(rect.getX() - 80), (int)(rect.getY() + 20), 160, 20);
		
		this.drawCenteredString(g2d,"(" + this.getVect().getX() + ", " + this.getVect().getY() + ")", rect, new Font("sansserif", Font.BOLD, 16));
	}
	
	public void resetController() {
		int[] vect = {10, 6, 2};
		
		this.controller = new DeepResidualNetwork(vect);
	}
}
