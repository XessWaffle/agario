package graphics;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import core.Player;

public class Zoom implements MouseWheelListener{
	
	
	private Player toWatch;
	
	public Zoom(Player tW) {
		toWatch = tW;
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		int notches = e.getWheelRotation();
		
		if(e.getButton() == MouseEvent.BUTTON2) {
			toWatch.setZoom(toWatch.getZoom() + notches * 0.01);
		} else {
			toWatch.setZoom(toWatch.getZoom() + notches * 0.01);
		}
	}
}
