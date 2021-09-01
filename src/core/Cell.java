package core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JComponent;

import coreutils.Eatable;
import coreutils.Mass;
import coreutils.Vector;

public class Cell implements Eatable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int MAX_SPD = 10;

	private static final int MIN_RAD = 30;
	
	private static int cellNum = 0;
	
	private int killCnt;
	
	private String label;
	
	private Point location;
	private Mass mass;
	private Vector vect;
	private World in;

	private Color color;
	
	private boolean isEaten;
	private boolean gotKill;
	
	public Cell(Point initialLoc) {
		
		this.setLabel("Cell " + ++cellNum);
		
		this.setLocation(initialLoc);
		this.setMass(new Mass());
		this.setVect(new Vector(0,0));
		
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Mass getMass() {
		return mass;
	}

	public void setMass(Mass mass) {
		this.mass = mass;
	}

	public Vector getVect() {
		return vect;
	}

	public void setVect(Vector vect) {
		this.vect = vect;
	}
	
	public World getWorld() {
		return in;
	}
	
	public void setWorld(World in) {
		this.in = in;
	}
	
	public int getRadius() {	
		return mass.getMass() > Mass.STARTING_MASS*2 ? (int)Math.sqrt(((mass.getMass() - Mass.STARTING_MASS * 2)/2)) + 50: mass.getMass()/4;
	}
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	public Point getCenterLocation() {
		return new Point((int)(this.getLocation().getX() + this.getRadius()), (int)(this.getLocation().getY()+this.getRadius()));
	}
	
	public Point getLocation() {
		return this.location;
	}
	
	public int getX() {
		return (int)this.location.getX();
	}
	
	public int getCenterX() {
		return (int)this.getCenterLocation().getX();
	}
	
	public int getY() {
		return (int)this.location.getY();
	}
	
	public int getCenterY() {
		return (int)this.getCenterLocation().getY();
	}
	
	public void setLocation(Point loc) {
		this.location = loc;
	}
	
	public int getKillCount() {
		return killCnt;
	}

	public void setKillCount(int killCnt) {
		this.killCnt = killCnt;
	}
	
	public void step() {
		
		this.setGotKill(false);
		
		this.eatAround();
		this.move();
		
		if(this.getMass().getMass() <= 0) {
			this.setEaten(true);
		}
		
		//System.out.println(this.mass.getMass());
	}
	
	@Override
	public int eat() {
		// TODO Auto-generated method stub
		
		this.setEaten(true);
		
		return this.getMass().getMass();
	}
	
	public boolean isEaten() {
		return isEaten;
	}

	public void setEaten(boolean isEaten) {
		this.isEaten = isEaten;
	}
	
	public void eatAround() {	
		try {
			for(Pellet p: in.getPellets()) {	
				
				Point toCheckFrom = this.getCenterLocation();
				
				if(toCheckFrom.distance(p.getCenterLocation()) < (int)this.getRadius() && this.mass.getMass() < Mass.MAX_MASS) {
					this.mass.addMass(p.eat());
				}
				
			}
			
			for(Virus v: in.getViruses()) {	
				
				Point toCheckFrom = this.getCenterLocation();
				
				if(toCheckFrom.distance(v.getCenterLocation()) < (int)this.getRadius() && this.mass.getMass() < Mass.MAX_MASS) {
					this.mass.addMass(v.eat());
				}
				
			}
			
			for(Player p: in.getPlayers()) {
				for(Cell potEat : p.getControlling()) {
					if(potEat.mass.getMass() < (int)(this.mass.getMass() * 0.9) && potEat.getCenterLocation().distance(this.getCenterLocation()) < (int)(this.getRadius())) {
						this.mass.addMass(potEat.eat());
						
						this.killCnt++;
						this.setGotKill(true);
						
					}
				}
			}
			
			for(DeadCell d: in.getDeadcells()) {	
				
				Point toCheckFrom = this.getCenterLocation();
				
				if(toCheckFrom.distance(d.getCenterLocation()) < (int)this.getRadius() && this.mass.getMass() < Mass.MAX_MASS && !d.isEaten()) {
					this.mass.addMass(d.eat());
				}
				
			}
			
			
			if(this.getMass().getMass() > Mass.THRESHOLD_MASS && Math.random() > 0.5) {
				this.getMass().addMass((int)(-10 * Math.random() * (double)this.getMass().getMass()/Mass.MAX_MASS));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void move() {
		int curLocX = (int) vect.getX();
		int curLocY = (int) vect.getY();
		
		double multiplier = Math.exp(-1 * this.getMass().getMass()/(double)Mass.MAX_MASS);
		
		if(vect.speed() > MAX_SPD * multiplier) {
			
			double delta = vect.speed()/(MAX_SPD * multiplier);
			
			int dX = (int)(vect.getX()/delta);
			int dY = (int)(vect.getY()/delta);
			
			this.setLocation(new Point(this.getX() + dX, this.getY() + dY));
		} else {
			this.setLocation(new Point(this.getX() + curLocX, this.getY() + curLocY));
		}
		
		detectEdgeCollisionZero();
	}

	protected boolean detectEdgeCollisionZero() {
		// TODO Auto-generated method stub
		boolean ret = false;
		
		Point origin = this.getLocation();
		Point bR = new Point(this.getX() + this.getRadius() * 2, this.getY() + this.getRadius() * 2);
		
		
		if(bR.getX() < 0) {
			
			ret = true;
			
			this.setLocation(new Point(World.MAX_X_SIZE - this.getRadius() * 2, this.getY()));
		}
		
		if(bR.getY() < 0) {
			
			ret = true;
			
			this.setLocation(new Point(this.getX(),World.MAX_Y_SIZE - this.getRadius() * 2));
		}
		
		if(origin.getX() > World.MAX_X_SIZE) {
			
			ret = true;
			
			this.setLocation(new Point(0, this.getY()));
		}
		
		if(origin.getY() > World.MAX_Y_SIZE) {
			
			ret = true;
			
			this.setLocation(new Point(this.getX(), 0));
		}
		
		return ret;
	}

	private boolean detectEdgeCollision() {
		// TODO Auto-generated method stub
		
		boolean ret = false;
		
		Point origin = this.getLocation();
		Point bR = new Point(this.getX() + this.getRadius() * 2, this.getY() + this.getRadius() * 2);
		
		
		if(bR.getX() > World.MAX_X_SIZE) {
			
			ret = true;
			
			this.setLocation(new Point(World.MAX_X_SIZE - this.getRadius() * 2, this.getY()));
		}
		
		if(bR.getY() > World.MAX_Y_SIZE) {
			
			ret = true;
			
			this.setLocation(new Point(this.getX(),World.MAX_Y_SIZE - this.getRadius() * 2));
		}
		
		if(origin.getX() < 0) {
			
			ret = true;
			
			this.setLocation(new Point(0, this.getY()));
		}
		
		if(origin.getY() < 0) {
			
			ret = true;
			
			this.setLocation(new Point(this.getX(), 0));
		}
		
		return ret;
		
	}

	public boolean gotKill() {
		return gotKill;
	}

	public void setGotKill(boolean gotKill) {
		this.gotKill = gotKill;
	}

	

	

	
	
}
