package core;

import java.awt.Point;

import coreutils.Eatable;
import coreutils.Mass;

public class Virus implements Eatable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static final int MAX_VIRUSES = 250;
	
	private static int virusNum = 0;
	
	private String label;
	private Point location;
	private Mass mass;
	
	private boolean eaten = false;

	private int diameter;
	
	public Virus(Point location) {
		
		
		this.label = "Virus " + virusNum++;
		
		this.setLocation(location);
		
		//System.out.println(this.getLocation());
		
		this.mass = new Mass();
		this.mass.setMass((int)(-(Math.random() * 100)));

		this.setDiameter((int)(Math.random() * 50));
	}
	
	public int eat() {
		int ret = this.mass.getMass();
		this.mass.setMass(0);
		
		this.setEaten(true);
		
		return ret;
	}
	
	public Point getCenterLocation() {
		return new Point((int)(this.getLocation().getX() + 2), (int)(this.getLocation().getY() + 2));
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
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isEaten() {
		return eaten;
	}

	public void setEaten(boolean eaten) {
		this.eaten = eaten;
	}

	public int getDiameter() {
		return diameter;
	}

	public void setDiameter(int diameter) {
		this.diameter = diameter;
	}
	
	public int getMass() {
		return this.mass.getMass();
	}
}

