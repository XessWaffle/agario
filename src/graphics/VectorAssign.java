package graphics;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import core.Player;
import coreutils.Vector;

public class VectorAssign implements MouseMotionListener, MouseWheelListener, MouseListener{
	
	private Player toAssign;
	
	double zoom = 1;
	
	public VectorAssign(Player p) {
		this.toAssign = p;
	}
	
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		Dimension size = toAssign.getSize(); 
		
		toAssign.setVect(new Vector(-1 * (int)(size.getWidth()/2 - e.getX()),-1 * (int)( size.getHeight()/2 - e.getY())));
	}


	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		int notches = e.getWheelRotation();
		
		toAssign.setZoom(toAssign.getZoom() + notches * 0.2);
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		toAssign.launch();
	}


	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	
}