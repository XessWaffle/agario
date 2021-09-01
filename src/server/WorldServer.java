package server;

import java.io.IOException;
import java.net.ServerSocket;

import core.World;

public class WorldServer extends Thread{
	
	private World toDisplay;
	private ServerSocket worldServer;
	
	public WorldServer(int port) {
		try {
			worldServer = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() {
		
	}
	
}
