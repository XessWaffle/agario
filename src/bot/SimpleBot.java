package bot;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;

import botutils.CircularZone;
import botutils.Zone;
import core.NeuralNetwork;
import core.Pellet;
import core.Player;
import core.Virus;
import core.World;
import coreutils.Eatable;
import coreutils.Vector;
import network.DeepResidualNetwork;
import network.MultilayerPerceptron;

public class SimpleBot extends Player {
	
	public static final int EXTENDED_VIEW = 250;
	
	private int position = 0;
	
	private CircularZone toEval;
	
	private int steps = 1, maxAlive = 1;
	
	private int mult;
	
	public SimpleBot(World in, String name, Color cellColor) {
		super(in, name, cellColor);
		// TODO Auto-generated constructor stub
		
		toEval = new CircularZone(new Rectangle(this.getControlling().get(0).getCenterX() - this.getControlling().get(0).getRadius() * 4,
				this.getControlling().get(0).getCenterY() - this.getControlling().get(0).getRadius() * 4, 
				this.getControlling().get(0).getRadius() * 8, this.getControlling().get(0).getRadius() * 8), 36);
		mult = 4;
	}
	
	public void step() {
		
		steps++;
		
		int[] zones = determineActiveZones();
		
		double[] scores = checkZones(zones);
		
		this.setVect(determineVect(scores));
		
		toEval.move(this.getControlling().get(0).getCenterLocation());
		
		toEval.resetScores();

		toEval.resize(this.getControlling().get(0).getRadius() * mult);
		
		super.step();
		
	}
	
	private Vector determineVect(double[] zones) {
		// TODO Auto-generated method stub
		double degDiff = 360/zones.length;
		
		double deg = 0.0;
		
		double zoneMin = Integer.MAX_VALUE;
		double zoneMax = Integer.MIN_VALUE;
		
		
		Vector away = null, to = null;
		
		for(double zone: zones) {
			if(zone < zoneMin) {
				zoneMin = zone;
			}
			
			if(zone > zoneMax) {
				zoneMax = zone;
			}
		}
		
		for(double zone: zones) {
			
			deg += degDiff;
			
			if(zoneMin == zone && zoneMin < 0) {
				away = new Vector(deg, 20);
			} else if(zoneMin == zone){
				away  = new Vector(0,0);
			}
			
			
			if(zoneMax == zone) {
				to = new Vector(deg, 20);
			}
			
		}
		
		return new Vector((int)(to.getX() * 2 + away.getX()), (int)(to.getY() * 2 + away.getX()));
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
	
	public int getAliveTime() {
		return steps;
	}
	
	public int getMaxAlive() {
		return maxAlive;
	}
	
	public double getTimeProportion() {
		return (double)this.steps/this.maxAlive;
	}
}
