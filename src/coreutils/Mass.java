package coreutils;

public class Mass {
	
	public static final int STARTING_MASS = 100;
	public static final int MAX_MASS = 500000;
	public static final int THRESHOLD_MASS = 20000;
	
	
	private int mass;
	
	public Mass() {
		addMass((int)(Mass.STARTING_MASS));
	}

	public int getMass() {
		return mass;
	}

	public void addMass(int mass) {
		this.mass += mass;
	}
	
	public void setMass(int mass) {
		this.mass = mass;
	}
	
}
