package bot;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;

import botutils.CircularZone;
import botutils.Zone;
import core.DeadCell;
import core.NeuralNetwork;
import core.Pellet;
import core.Player;
import core.Virus;
import core.World;
import coreutils.Eatable;
import network.DeepResidualNetwork;
import network.ExtremeLearningMachine;
import network.MultilayerPerceptron;

public class SurvivorBot extends Player {
	
	public static final int EXTENDED_VIEW = 250;
	private NeuralNetwork controller;
	
	private int position = 0;
	
	private CircularZone toEval;
	
	private int steps = 1, maxAlive = 1;
	
	private int mult;
	
	public SurvivorBot(World in, String name, Color cellColor) {
		super(in, name, cellColor);
		// TODO Auto-generated constructor stub
		
		int[] vect = {10,6,3};
		controller = new MultilayerPerceptron(vect);
		
		toEval = new CircularZone(new Rectangle(this.getControlling().get(0).getCenterX() - this.getControlling().get(0).getRadius() * 10 + Bot.EXTENDED_VIEW,
				this.getControlling().get(0).getCenterY() - this.getControlling().get(0).getRadius() * 10 + Bot.EXTENDED_VIEW, 
				this.getControlling().get(0).getRadius() * 20, this.getControlling().get(0).getRadius() * 20), 10);
		mult = 4;
	}
	
	public void step() {
		
		steps++;
		
		int[] zones = determineActiveZones();
		
		double[] scores = checkZones(zones);
		
		if(isDead()) {
			controller.update();
			
			steps = 1;
			
		} else {
			try {
				this.position = in.getPosition(this);
				controller.setError((1 - (double)steps/maxAlive) + (2 * in.getPosition(this)/(double)in.getPlayers().size()));
				//controller.setError((10000 - this.getTotalMass())/10000);
			
			} catch(Exception e) {
				//controller.setError(-1);
			}	
		}
		
		System.out.println(Arrays.toString(scores));
		
		this.setVect(((MultilayerPerceptron) controller).getNextVect(scores));
		
		if(((MultilayerPerceptron) controller).doLaunch() > 0.5 && steps % 12 == 0) {
			super.launch();
		}
		
		toEval.move(this.getControlling().get(0).getCenterLocation());
		
		toEval.resetScores();

		toEval.resize(this.getControlling().get(0).getRadius() * mult);
		
		if(steps > maxAlive)
			maxAlive = steps;
		
		super.step();
		
		
		
		
		
	}
	
	private int[] determineActiveZones() {
		// TODO Auto-generated method stub
		
		double deg = this.getVect().degree();
		
		int lower = ((CircularZone) this.toEval).getZoneAt(deg - (360/this.toEval.getNumZones() * 2));
		int upper = ((CircularZone) this.toEval).getZoneAt(deg + (360/this.toEval.getNumZones() * 2));
		
		int[] ret = new int[4];
		
		int ind = 0;
		
		for(int i = lower; i <= upper; i++) {
			
			if(i >= this.toEval.getNumZones()) {
				i = 0;
			} else if(i <= 0) {
				i = this.toEval.getNumZones() - 1;
			}
			
			ret[ind++] = i;
		}
		
		return ret;
	}

	private double[] checkZones(int[] zones) {
		// TODO Auto-generated method stub
		
		ArrayList<Player> targets = new ArrayList<Player>();
		ArrayList<Pellet> pellets = new ArrayList<Pellet>();
		ArrayList<Virus> viruses = new ArrayList<Virus>();
		ArrayList<DeadCell> deadcells = new ArrayList<DeadCell>();
		
		for(Player p: this.in.getPlayers()) {
			if(!p.isDead() && p.getControlling().get(0).getCenterLocation().distance(this.getControlling().get(0).getCenterLocation()) < this.getControlling().get(0).getRadius() + Bot.EXTENDED_VIEW && !Player.equals(this, p)) {
				targets.add(p);
			}
		}
		
		for(Pellet p: this.in.getPellets()) {
			if(p.getCenterLocation().distance(this.getControlling().get(0).getCenterLocation()) < this.getControlling().get(0).getRadius() + Bot.EXTENDED_VIEW) {
				pellets.add(p);
			}
		}
		
		for(Virus v: this.in.getViruses()) {
			if(v.getCenterLocation().distance(this.getControlling().get(0).getCenterLocation()) < this.getControlling().get(0).getRadius() + Bot.EXTENDED_VIEW) {
				viruses.add(v);
			}
		}
		
		for(int i = 0; i < this.in.getDeadcells().size(); i++) {
			if(this.getWorld().getDeadcells().get(i).getCenterLocation().distance(this.getControlling().get(0).getCenterLocation()) < this.getControlling().get(0).getRadius() + Bot.EXTENDED_VIEW) {
				deadcells.add(this.getWorld().getDeadcells().get(i));
			}
		}
		
		
		for(Pellet p: pellets) {
			
			int zone = toEval.contains(p.getCenterLocation());
			
			boolean isActive = true;
			
			for(int z: zones) {
				if(zone == z) {
					isActive = true;
				}
			}
			
			
			if(zone != -1) {
				toEval.addToScore(zone, heuristicScore(p));
				
				//System.out.println("ADDED");
			}
		}
		
		for(Virus v: viruses) {
			
			int zone = toEval.contains(v.getCenterLocation());
			
			boolean isActive = true;
			
			for(int z: zones) {
				if(zone == z) {
					isActive = true;
				}
			}
			
			
			if(zone != -1) {
				toEval.addToScore(zone, heuristicScore(v));
				
				//System.out.println("ADDED");
			}
		}
		
		for(DeadCell d: deadcells) {
			
			int zone = toEval.contains(d.getCenterLocation());
			
			boolean isActive = true;
			
			for(int z: zones) {
				if(zone == z) {
					isActive = true;
				}
			}
			
			
			if(zone != -1) {
				toEval.addToScore(zone, heuristicScore(d));
				
				//System.out.println("ADDED");
			}
		}
		
		for(Player p: targets) {
			
			int zone = toEval.contains(p.getControlling().get(0).getCenterLocation());
			
			boolean isActive = true;
			
			for(int z: zones) {
				if(zone == z) {
					isActive = true;
				}
			}
			
			
			if(zone != -1) {
				toEval.addToScore(zone, heuristicScore(p));
				
				//System.out.println("ADDED");
			}
		}
		
		/*double[] activeScores = new double[zones.length];
		
		for(int i = 0; i < zones.length; i++) {
			activeScores[i] = toEval.getScore(zones[i]);
		}*/
		
		return toEval.getAllScores();
	}

	private int heuristicScore(DeadCell toScore) {
		// TODO Auto-generated method stub
		return (int) (toScore.getMass().getMass() * (this.getControlling().get(0).getRadius() + Bot.EXTENDED_VIEW - this.getControlling().get(0).getCenterLocation().distance(toScore.getCenterLocation())));
	}

	private int heuristicScore(Virus toScore) {
		// TODO Auto-generated method stub
		return (int) (toScore.getMass() * (this.getControlling().get(0).getRadius() + Bot.EXTENDED_VIEW - this.getControlling().get(0).getCenterLocation().distance(toScore.getCenterLocation())));
	}

	public int heuristicScore(Pellet toScore) {
		return (int) (((Pellet) toScore).getMass() * (this.getControlling().get(0).getRadius() + Bot.EXTENDED_VIEW - this.getControlling().get(0).getCenterLocation().distance(((Pellet) toScore).getCenterLocation())));
	}
	
	public int heuristicScore(Player toScore) {
		return (int) ((toScore.getTotalMass() * ((this.getControlling().get(0).getRadius() + Bot.EXTENDED_VIEW - toScore.getControlling().get(0).getCenterLocation().distance(this.getControlling().get(0).getCenterLocation()))) - toScore.getControlling().get(0).getRadius()) - this.getTotalMass() * this.getControlling().get(0).getRadius() + Bot.EXTENDED_VIEW);
	}
	
	public NeuralNetwork getController() {
		return this.controller;
	}
	
	public int getAliveTime() {
		return steps;
	}
	
	public int getMaxAlive() {
		return maxAlive;
	}
	
	public double getTimeProportion() {
		return (double)this.steps/this.maxAlive;
	}
	
	public void resetController() {
		int[] vect = {10, 6, 2};
		
		this.controller = new ExtremeLearningMachine(vect);
	}

}
