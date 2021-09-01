package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;

import javax.swing.JFrame;

import bot.Bot;
import bot.MassBot;
import bot.SimpleBot;
import bot.SurvivorBot;
import bot.TargetBot;
import core.Cell;
import core.Player;
import core.World;
import coreutils.Vector;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.Random;
import java.awt.event.ActionEvent;

public class Main {

	private JFrame play1;
	private World world;
	
	private int playerPosition = 0;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.play1.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		world = new World();
		
		Player me = new Player(world, "ME", Color.BLUE);
		Random ran = new Random();
		
		world.addPlayer(me);
		
		for(int i = 0; i < 60; i++) {
			world.addPlayer(new Bot(world, "BOT" + i, new Color(ran.nextFloat(), ran.nextFloat(), ran.nextFloat())));
		}
		
		for(int i = 0; i < 60; i++) {
			world.addPlayer(new MassBot(world, "MBOT" + i, new Color(ran.nextFloat(), ran.nextFloat(), ran.nextFloat())));
		}
		
		for(int i = 0; i < 60; i++) {
			world.addPlayer(new SurvivorBot(world, "SuBOT" + i, new Color(ran.nextFloat(), ran.nextFloat(), ran.nextFloat())));
		}
		
		
		/*for(int i = 0; i < 15; i++) {
			world.addPlayer(new TargetBot(world, "TARGET" + i, Color.RED));
		}*/

		
		play1 = new JFrame();
		play1.setBounds(100, 100, 500, 500);
		play1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		play1.setContentPane(world.getPlayers().get(0));
		
		play1.setExtendedState(JFrame.MAXIMIZED_BOTH);
		play1.setUndecorated(false);
		
		JButton prevBot = new JButton("Previous");
		prevBot.setBounds(play1.getWidth() - 70, play1.getHeight() - 45, 60, 20);
		JButton nextBot = new JButton("Next");
		nextBot.setBounds(play1.getWidth() - 70, play1.getHeight() - 70, 60, 20);
		JButton targetControl = new JButton("Reset");
		targetControl.setBounds(play1.getWidth() - 70, play1.getHeight() - 95, 60, 20);
		
		nextBot.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				Player view = world.getPlayers().get(++playerPosition);
				Zoom list = new Zoom(view);
				
				play1.setVisible(false);
				play1.setContentPane(view);
				
				view.addMouseWheelListener(list);
				
				play1.setVisible(true);
				
				if(playerPosition == world.getPlayers().size() - 1) {
					playerPosition = 0;
				}
				
				play1.add(prevBot);
				play1.add(nextBot);
				play1.add(targetControl);
				
			}
			
		});
		
		prevBot.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				Player view = world.getPlayers().get(--playerPosition);
				Zoom list = new Zoom(view);
				
				play1.setVisible(false);
				play1.setContentPane(view);
				
				view.addMouseWheelListener(list);
				
				play1.setVisible(true);
				
				if(playerPosition == 0) {
					playerPosition = world.getPlayers().size() - 1;
				}
				
				play1.add(prevBot);
				play1.add(nextBot);
				play1.add(targetControl);
				
			}
			
		});
		
		targetControl.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				((Bot)world.getPlayers().get(playerPosition)).resetController(); 
			}
			
		});
		
		play1.add(prevBot);
		play1.add(nextBot);
		play1.add(targetControl);
		
		play1.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				String toCheck = e.getKeyChar() + "";
				
				if(toCheck.equals("e")) {
					play1.setVisible(true);
				} else if(toCheck.equals("l")) {
					play1.setVisible(false);
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		
		
		VectorAssign list = new VectorAssign(me);
		me.addMouseMotionListener(list);
		me.addMouseWheelListener(list);
		me.addMouseListener(list);

		me.addMouseMotionListener(list);
		play1.addMouseWheelListener(list);
		
		Thread run = new Thread(){
			
			public void run() {
				while(true) {
					world.step();
					
					try {
						Thread.sleep(0);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if(Math.random()>0.75) {
						world.generatePellets();
					}
					
				}

			}
			
		};
		
		run.start();
	}
}


