package coreutils;

public abstract class Sensor {
	private Vector sensed;

	public Sensor() {
		sensed = new Vector(0,0);
	}
	
	public Vector getSensed() {
		return sensed;
	}

	public void setSensed(Vector sensed) {
		this.sensed = sensed;
	}
	
	public abstract void sense();
	
}
