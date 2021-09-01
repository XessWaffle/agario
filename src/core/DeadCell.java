package core;

import java.awt.Color;
import java.awt.Point;

import coreutils.Eatable;
import coreutils.Mass;
import coreutils.Vector;

public class DeadCell extends Cell{
	
	
	private int stepCnt = 0;
	
	public DeadCell() {
		super(new Point(0,0));
		this.getMass().setMass(-50);
	}
	
	
	public void step() {
		
		this.move();
		
		/*if(this.getMass().getMass() < 0) {
			this.setEaten(true);
		}*/
		
		/*if(stepCnt > 50) {
			this.setVect(new Vector(0,0));
		}*/		
		stepCnt++;
		
		
		if(stepCnt > 500) {
			this.eat();
		}
		
		//System.out.println(this.mass.getMass());
	}
	
	public void move() {
		int curLocX = (int) this.getVect().getX();
		int curLocY = (int) this.getVect().getY();
		
		double multiplier = Math.exp(-1 * this.getMass().getMass()/(double)Mass.MAX_MASS);
		
		if(this.getVect().speed() > MAX_SPD * 2 * multiplier) {
			
			double delta = this.getVect().speed()/(MAX_SPD * 2 * multiplier);
			
			int dX = (int)(this.getVect().getX()/delta);
			int dY = (int)(this.getVect().getY()/delta);
			
			this.setLocation(new Point(this.getX() + dX, this.getY() + dY));
		} else {
			this.setLocation(new Point(this.getX() + curLocX, this.getY() + curLocY));
		}
		
		detectEdgeCollisionZero();
	}
	
}
