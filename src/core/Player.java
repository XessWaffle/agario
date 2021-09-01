package core;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import bot.Bot;
import bot.MassBot;
import bot.SurvivorBot;
import bot.TargetBot;
import botutils.CircularZone;
import coreutils.Mass;
import coreutils.Sensor;
import coreutils.Vector;

public class Player extends JPanel implements Comparable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ArrayList<Cell> controlling;
	private ArrayList<Sensor> sensors;
	
	protected World in;
	private Vector vect;
	private Point location;
	private Color cellColor;
	private String name;
	
	private boolean isDead;
	
	private double zoomLevel = 1;
	
	
	public Player(World in, String name, Color cellColor) {
		this.in = in;
		
		this.vect = new Vector(1,1);
		
		setControlling(new ArrayList<Cell>());
		setSensors(new ArrayList<Sensor>());
		
		initialize();
		
		this.name = name;
		this.setCellColor(cellColor);
	}

	public void initialize() {
		// TODO Auto-generated method stub
		this.setDead(false);
		
		this.controlling.add(new Cell(new Point((int)(Math.random() * World.MAX_X_SIZE),(int)(Math.random()*World.MAX_Y_SIZE))));
		
		this.controlling.get(0).setWorld(in);
		
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getTotalMass() {
		int tm = 0;
		
		for(Cell control: controlling) {
			tm += control.getMass().getMass();
		}
		
		return tm;
	}
	
	public void setZoom(double zoom) {
		this.zoomLevel = zoom;
	}
	
	public double getZoom() {
		// TODO Auto-generated method stub
		return zoomLevel;
	}
	
	public void setLocation(Point p) {
		this.location = p;
	}
	
	public Point getLocation() {
		return location;
	}

	public Color getCellColor() {
		return cellColor;
	}

	public void setCellColor(Color cellColor) {
		this.cellColor = cellColor;
	}
	
	public ArrayList<Cell> getControlling() {
		return controlling;
	}

	public void setControlling(ArrayList<Cell> controlling) {
		this.controlling = controlling;
	}

	public ArrayList<Sensor> getSensors() {
		return sensors;
	}

	public void setSensors(ArrayList<Sensor> sensors) {
		this.sensors = sensors;
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
	
	public boolean isDead() {
		return isDead;
	}
	
	public void setDead(boolean dead) {
		this.isDead = dead;
	}
	
	public void step() {
		for(int i = 0; i < this.controlling.size(); i++) {
			
			if(!this.controlling.get(i).isEaten()) {
				this.controlling.get(i).setVect(vect);
				this.controlling.get(i).step();
			} else {
				this.controlling.remove(i);
			}
		}
		
		if(this.controlling.size() == 0) {
			this.setDead(true);
		}
		
		//this.setZoom(Math.max((1.0 - (double)(2 * this.getTotalMass())/Mass.MAX_MASS), 0.15));
		
		this.repaint();
	}
	
	public void launch() {
		for(Cell control: this.controlling) {
			//control.getMass().addMass(-50);
			
			DeadCell toLaunch = new DeadCell();
			
			int xLoc = (int) ((control.getRadius() + 20)/vect.speed() * vect.getX()) + control.getCenterX();
			int yLoc = (int) ((control.getRadius() + 20)/vect.speed() * vect.getY()) + control.getCenterY();
			
			toLaunch.setLocation(new Point(xLoc, yLoc));
			toLaunch.setVect(new Vector(this.getVect().getX() * 2, this.getVect().getY() * 2));
			toLaunch.setColor(control.getColor());
			
			this.getWorld().addDeadCell(toLaunch);
		}
	}
	
	public void kill() {
		this.getControlling().get(0).setEaten(true);
		
		for(int i = 0; i < 500; i++) {
			DeadCell toLaunch = new DeadCell();
			
			int xLoc = (int) ((this.getControlling().get(0).getRadius() + 20)/vect.speed() * vect.getX()) + this.getControlling().get(0).getCenterX();
			int yLoc = (int) ((this.getControlling().get(0).getRadius() + 20)/vect.speed() * vect.getY()) + this.getControlling().get(0).getCenterY();
			
			toLaunch.setLocation(new Point(xLoc, yLoc));
			toLaunch.setVect(new Vector((int)((Math.random() - 0.5) * 40), (int)((Math.random()- 0.5) * 40)));
			toLaunch.setColor(this.getControlling().get(0).getColor());
			
			this.getWorld().addDeadCell(toLaunch);
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		if(!isDead()) {
			paintEnvironment(g2d);
			paintOtherCells(g2d);
			paintDeadCells(g2d);
			paintLeaderboard(g2d);
			paintVectors(g2d);
			paintCell(g2d);
			
		}
		
	}

	private void paintVectors(Graphics2D g2d) {
		// TODO Auto-generated method stub
		for(Player p: in.getPlayers()) {
			
			for(Cell control: p.getControlling()) {
				if(control.getCenterLocation().distance(this.controlling.get(0).getCenterLocation()) < (this.getWidth() * 1.0/zoomLevel) && !Player.equals(this, p)) {
					Point cellLoc = this.determineScreenLocation(control);
					
					g2d.setColor(p.getCellColor().brighter());
					
					g2d.drawLine((int)cellLoc.getX(), (int)cellLoc.getY(), (int)(control.getVect().getX() * control.getRadius()/10 + cellLoc.getX()), (int)(control.getVect().getY() * control.getRadius()/10 + cellLoc.getY()));
					
				}
				
				if((p instanceof Bot) && control.getCenterLocation().distance(this.controlling.get(0).getCenterLocation()) < (this.getWidth() * 1.0/zoomLevel)) {
					CircularZone eval = (CircularZone) ((Bot) p).getZone();
					
					int zone = 0;
					
					for(Point[] tD: eval.getZones()) {
						Polygon conv = new Polygon();
						
						for(Point toAdd: tD) {
							
							Point screenLoc = this.determineScreenLocation(toAdd);
							
							conv.addPoint((int)screenLoc.getX(), (int)screenLoc.getY());
						}
						
						int score;
						if(zone < eval.getNumZones()) {
							score = (int)eval.getScore(zone++);
						} else {
							score = 0;
						}
						
						int grad;
						if(score < 0) {
							grad = map(score, -100, 0, 0, 255);
							
							int redVal = 255 - Math.abs(grad);
							int greenVal = Math.abs(grad);
							
							if(grad > 255) {
								redVal = 0;
								greenVal = 255;
							} else if(grad < 0) {
								redVal = 255;
								greenVal = 0;
							}
							
							
							g2d.setColor(new Color(redVal, greenVal, 0));
							
						} else {
							grad = map(score, 0, 100, 0, 255);
							
							int redVal = 255 - Math.abs(grad);
							int greenVal = Math.abs(grad);
							
							if(grad > 255) {
								redVal = 0;
								greenVal = 255;
							} else if(grad < 0) {
								redVal = 255;
								greenVal = 0;
							}
							
							g2d.setColor(new Color(greenVal, redVal, 0));
						}
						
						System.out.println(grad);
						
						
						g2d.draw(conv);
					
					}
					
				}
			}
			
		
		}
	}

	

	private void paintDeadCells(Graphics2D g2d) {
		// TODO Auto-generated method stub
		for(DeadCell control : in.getDeadcells()) {
			if(control.getCenterLocation().distance(this.controlling.get(0).getCenterLocation()) < (this.getWidth() * 1.0/zoomLevel)) {
				Point cellLoc = this.determineScreenLocation(control);
				
				g2d.setColor(Color.YELLOW);
				
				g2d.fillOval((int)( (cellLoc.getX() - 20 * zoomLevel)), (int)((cellLoc.getY() - 20 * zoomLevel)), 
						(int)(zoomLevel * (20*2)), (int)(zoomLevel * 20*2));
			}
		}
	}

	public void paintLeaderboard(Graphics2D g2d) {
		// TODO Auto-generated method stub
		//30/20
		
		Rectangle rect = new Rectangle(this.getWidth() - 160, 0, 80, 20);
		
		int pos = 1;
		
		for(Player p: in.getLeaderboard()) {
			this.drawCenteredString(g2d, (pos++) + ". " + p.getName(), rect, new Font("sansserif", Font.BOLD, 16));
			rect.setLocation((int)(rect.getX() + 80), (int)rect.getY());
			this.drawCenteredString(g2d, p.getTotalMass() + "", rect, new Font("sansserif", Font.BOLD, 16));
			rect.setLocation((int)(rect.getX() - 80), (int)(rect.getY() + 20));
			//rect.setLocation((int)(rect.getX()), (int)(rect.getY() + 20));
		}
		
		
		g2d.setColor(Color.YELLOW);
		this.drawCenteredString(g2d, (in.getPosition(this) + 1) + ". " + this.getName(), rect,  new Font("sansserif", Font.BOLD, 16));
		rect.setLocation((int)(rect.getX() + 80), (int)rect.getY());
		this.drawCenteredString(g2d, this.getTotalMass() + "", rect, new Font("sansserif", Font.BOLD, 16));
		
		
		rect.setBounds((int)(rect.getX() - 80), (int)(rect.getY() + 20), 160, 20);
		
		this.drawCenteredString(g2d,"(" + this.getVect().getX() + ", " + this.getVect().getY() + ")", rect, new Font("sansserif", Font.BOLD, 16));
	
	
	
	}

	public void paintEnvironment(Graphics2D g2d) {
		// TODO Auto-generated method stub
		
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		
		Random ran = new Random();
		
		for(Pellet toPaint: in.getPellets()) {
			if(toPaint.getCenterLocation().distance(this.controlling.get(0).getCenterLocation()) < (this.getWidth() * 1.0/zoomLevel) && !toPaint.isEaten()) {
				Point relLoc = this.determineScreenLocation(toPaint);
				
				g2d.setColor(new Color(ran.nextFloat(), ran.nextFloat(), ran.nextFloat()));
				
				
				g2d.fillOval((int)relLoc.getX(), (int)relLoc.getY(), (int)(toPaint.getDiameter() * zoomLevel),(int) (toPaint.getDiameter() * zoomLevel));
			}
		}
		
		for(Virus toPaint: in.getViruses()) {
			if(toPaint.getCenterLocation().distance(this.controlling.get(0).getCenterLocation()) < (this.getWidth() * 1.0/zoomLevel) && !toPaint.isEaten()) {
				Point relLoc = this.determineScreenLocation(toPaint);
				
				g2d.setColor(Color.GREEN);
				
				
				g2d.fillOval((int)relLoc.getX(), (int)relLoc.getY(), (int)(toPaint.getDiameter() * zoomLevel),(int) (toPaint.getDiameter() * zoomLevel));
			}
		}
	}

	private Point determineScreenLocation(Virus toPaint) {
		// TODO Auto-generated method stub
		int zX = (int) ((this.controlling.get(0).getCenterX() - (this.getWidth() * 1.0/zoomLevel)/2));
		int zY = (int) ((this.controlling.get(0).getCenterY() - (this.getHeight() * 1.0/zoomLevel)/2));
		
		int xLoc = map(toPaint.getCenterX() - zX, 0, (int)(this.getWidth() * 1.0/zoomLevel), 0, this.getWidth());
		int yLoc = map(toPaint.getCenterY() - zY, 0, (int)(this.getHeight() * 1.0/zoomLevel), 0, this.getHeight());
		
		return new Point(xLoc,yLoc);
	}

	public Point determineScreenLocation(Pellet toPaint) {
		// TODO Auto-generated method stub
		
		int zX = (int) ((this.controlling.get(0).getCenterX() - (this.getWidth() * 1.0/zoomLevel)/2));
		int zY = (int) ((this.controlling.get(0).getCenterY() - (this.getHeight() * 1.0/zoomLevel)/2));
		
		int xLoc = map(toPaint.getCenterX() - zX, 0, (int)(this.getWidth() * 1.0/zoomLevel), 0, this.getWidth());
		int yLoc = map(toPaint.getCenterY() - zY, 0, (int)(this.getHeight() * 1.0/zoomLevel), 0, this.getHeight());
		
		return new Point(xLoc,yLoc);
	}
	
	public Point determineScreenLocation(Cell toPaint) {
		// TODO Auto-generated method stub
		
		int zX = (int) ((this.controlling.get(0).getCenterX() - (this.getWidth() * 1.0/zoomLevel)/2));
		int zY = (int) ((this.controlling.get(0).getCenterY() - (this.getHeight() * 1.0/zoomLevel)/2));
		
		int xLoc = map(toPaint.getCenterX() - zX, 0, (int)(this.getWidth() * 1.0/zoomLevel), 0, this.getWidth());
		int yLoc = map(toPaint.getCenterY() - zY, 0, (int)(this.getHeight() * 1.0/zoomLevel), 0, this.getHeight());
		
		return new Point(xLoc, yLoc);
	}
	
	private Point determineScreenLocation(Point toAdd) {
		// TODO Auto-generated method stub
		int zX = (int) ((this.controlling.get(0).getCenterX() - (this.getWidth() * 1.0/zoomLevel)/2));
		int zY = (int) ((this.controlling.get(0).getCenterY() - (this.getHeight() * 1.0/zoomLevel)/2));
		
		int xLoc = map((int)toAdd.getX() - zX, 0, (int)(this.getWidth() * 1.0/zoomLevel), 0, this.getWidth());
		int yLoc = map((int)toAdd.getY() - zY, 0, (int)(this.getHeight() * 1.0/zoomLevel), 0, this.getHeight());
		
		return new Point(xLoc, yLoc);
	}

	public void paintCell(Graphics2D g2d) {
		// TODO Auto-generated method stub
			
		for(Cell control: controlling) {
			g2d.setColor(cellColor);
			
			
			Point screenLoc = this.determineScreenLocation(control);
			
			g2d.fillOval((int)(screenLoc.getX()- control.getRadius() * zoomLevel), (int)(screenLoc.getY() - control.getRadius() * zoomLevel),
					(int) (zoomLevel * (control.getRadius()*2)), (int) (zoomLevel * (control.getRadius()*2)));
			
			Rectangle rect = new Rectangle();
			
			rect.setBounds((int)((screenLoc.getX() - control.getRadius() * zoomLevel)), (int)((screenLoc.getY() - control.getRadius() * zoomLevel)),
					(int) (zoomLevel * (control.getRadius()*2)), (int) (zoomLevel * (control.getRadius()*2)));
			
			this.drawCenteredString(g2d, this.getName(), rect, new Font("sansserif", Font.BOLD, (int)(32 * zoomLevel)));
		}
		
		
	}
	
	public void paintOtherCells(Graphics2D g2d) {
		for(Player p: in.getPlayers()) {
			
			for(Cell control: p.getControlling()) {
				if(control.getCenterLocation().distance(this.controlling.get(0).getCenterLocation()) < (this.getWidth() * 1.0/zoomLevel) && !Player.equals(this, p)) {
					Point cellLoc = this.determineScreenLocation(control);
					
					g2d.setColor(p.getCellColor());
					
					g2d.fillOval((int)( (cellLoc.getX() - control.getRadius() * zoomLevel)), (int)((cellLoc.getY() - control.getRadius() * zoomLevel)), 
							(int)(zoomLevel * (control.getRadius()*2)), (int)(zoomLevel * control.getRadius()*2));
					
					
					
					Rectangle rect = new Rectangle();
					
					rect.setBounds((int)((cellLoc.getX() - control.getRadius() * zoomLevel)), (int)( (cellLoc.getY() - control.getRadius() * zoomLevel)), 
							(int)(zoomLevel * (control.getRadius()*2)), (int)(zoomLevel * control.getRadius()*2));
					
					this.drawCenteredString(g2d, p.getName(), rect, new Font("sansserif", Font.BOLD, (int)(32 * zoomLevel)));
					
					
						
					
				}
			}
		}
	}
	
	public void drawCenteredString(Graphics2D g, String text, Rectangle rect, Font font) {
	    // Get the FontMetrics
	    FontMetrics metrics = g.getFontMetrics(font);
	    // Determine the X coordinate for the text
	    int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
	    // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
	    int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
	    // Set the font
	    g.setFont(font);
	    // Draw the String
	    g.setColor(Color.GRAY);
	    
	    g.drawString(text, x, y);
	}
	
	public static boolean equals(Player one, Player two) {
		return (one.getName()).equals(two.getName());
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return this.getTotalMass() - ((Player) o).getTotalMass();
	}
	
	private int map(int toMap, int bScaleMin, int bScaleMax, int nScaleMin, int nScaleMax) {
		return (int)((((double)toMap/(bScaleMax - bScaleMin)) * (nScaleMax - nScaleMin)) + nScaleMin);
	}



	
	

}
