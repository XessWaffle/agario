package botutils;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

public abstract class Zone {
	
	protected int numZones;
	protected Rectangle in;
	protected Point prevCenter;
	protected int radius;
	
	protected ArrayList<Point[]> zones;
	protected double[] scores;
	
	public Zone(Rectangle r, int zones) {
		this.setIn(r);
		this.setNumZones(zones);
		
		this.radius = in.height/2;
		
		this.zones = new ArrayList<>();
		this.scores = new double[zones];
		
		prevCenter = new Point((int)r.getCenterX(), (int)r.getCenterY());
	}
	
	public abstract int contains(Point p);
	public abstract void divide(int zones);

	public int getNumZones() {
		return numZones;
	}

	public void setNumZones(int numZones) {
		this.numZones = numZones;
	}

	public Rectangle getIn() {
		return in;
	}

	public void setIn(Rectangle in) {
		this.in = in;
	}
	
	public ArrayList<Point[]> getZones() {
		// TODO Auto-generated method stub
		return this.zones;
	}
	
	public void addToScore(int zone, int score) {
		scores[zone] += score;
	}
	
	public double getScore(int zone) {
		return scores[zone];
	}
	
	public double[] getAllScores() {
		return scores;
	}
	
	public void move(Point center) {
		
		
		for(int i = 0; i < getZones().size(); i++) {
			for(int j = 0; j < 3; j++) {
				zones.get(i)[j].translate(center.x - prevCenter.x, center.y - prevCenter.y);
			}
		}
		
		prevCenter = center;
	}
	
	public void resetScores() {
		for(int i = 0; i < scores.length; i++) {
			scores[i] = 0;
		}
	}
	

	public void resize(int rad) {
		// TODO Auto-generated method stub
		for(int i = 0; i < zones.size(); i++) {
			for(int j = 0; j < 3; j++) {
				int nX = (int) (((double)(zones.get(i)[j].getX() - prevCenter.getX())) * (double)rad/radius);
				int nY = (int) (((double)(zones.get(i)[j].getY() - prevCenter.getY())) * (double)rad/radius);
				
				zones.get(i)[j] = new Point((int)(nX + prevCenter.getX()), (int)(nY + prevCenter.getY()));
				
				
			}
		}
		
		radius = rad;
		
	}
	
	public String toString() {
		return "Zone: " + this.getNumZones();
	}
}
