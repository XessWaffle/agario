package botutils;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.Arrays;

public class CircularZone extends Zone {

	public CircularZone(Rectangle r, int zones) {
		super(r, zones);
		// TODO Auto-generated constructor stub
		this.divide(zones);
	}

	@Override
	public int contains(Point p) {
		
		int ind = 0;
		
		for(Point[] area: this.getZones()) {
			
			Polygon check = new Polygon();
			
			for(Point xy: area) {
				check.addPoint((int)xy.getX(), (int)xy.getY());
			}	
			
			//System.out.println(Arrays.toString(check.xpoints) + " " + p.getX());
			//System.out.println(Arrays.toString(check.ypoints) + " " + p.getY());
			
			if(check.contains(p)) {
				return ind;
			} else {
				ind++;
			}
		}
		
		return -1;
		
	}

	@Override
	public void divide(int zones) {
		// TODO Auto-generated method stub
		double dDeg = (2 * Math.PI)/zones;
		double circPos = 0;
		int radius = (int) Math.max(in.getWidth()/2, in.getHeight()/2);
		
		
		while (circPos < 2 * Math.PI) {
			
			int x1, x2, y1, y2;
			
			Point[] pts = new Point[3];
			
			x1 = (int)(radius * Math.cos(circPos) + in.getX() + (in.getWidth())/2);
			y1 = (int)(radius * Math.sin(circPos) + in.getY() + (in.getHeight())/2);

			pts[0] = new Point(x1, y1);
			
			circPos += dDeg;
			
			x2 = (int)(radius * Math.cos(circPos) + in.getX() + (in.getWidth())/2);
			y2 = (int)(radius * Math.sin(circPos) + in.getY() + (in.getHeight())/2);
			
			pts[1] = new Point(x2, y2);
			
			pts[2] = new Point((int)(in.getX() + (in.getWidth())/2), (int)(in.getY() + (in.getHeight())/2));
			
			
			this.zones.add(pts);
		}
		
	}
	
	public int getZoneAt(double degree) {
		
		while(degree > 360.0) {
			degree -= 360;
		}
		
		int zone = (int)(degree/360.0) * this.getNumZones();
		
		
		
		return zone;
	}

}
