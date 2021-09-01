package server;

import java.net.Socket;

import javax.swing.JFrame;

import core.Player;

public class PlayerClient extends Thread{
	
	private Player client;
	private JFrame view;
	private Socket toJoin;
	
	public PlayerClient(Player client) {
		
		this.setClient(client);
		
		initialize();
	}

	private void initialize() {
		// TODO Auto-generated method stub
		view = new JFrame();
		view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		view.setContentPane(client);
		view.setVisible(true);
		
		toJoin = new Socket();
		
	}

	public Player getClient() {
		return client;
	}

	public void setClient(Player client) {
		this.client = client;
	}
	
	public void run() {
		while(true) {
			
		}
	}
	
}
