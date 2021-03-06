package coreutils;

public class Vector {
	private int x, y;
	
	public Vector(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector(double degree, int radius) {
		this.x = (int)(radius * Math.cos(degree));
		this.y = (int)(radius * Math.sin(degree));
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public double speed() {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}
	
	public double degree() {
		return Math.atan2(y, x);
	}
	
}
