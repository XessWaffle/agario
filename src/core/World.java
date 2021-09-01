package core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.JFrame;

import bot.TargetBot;

public class World{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int MAX_X_SIZE = 25000;
	public static final int MAX_Y_SIZE = 25000;
	
	private ArrayList<Player> players, leaderboard, specializedLeaderBoard;
	private ArrayList<Pellet> pellets;
	private ArrayList<Virus> viruses;
	private ArrayList<DeadCell> deadcells;
	
	public World() {
		setPlayers(new ArrayList<>());
		setPellets(new ArrayList<>());
		setViruses(new ArrayList<>());
		setDeadcells(new ArrayList<>());
		
		initialize();
	}

	private void initialize() {
		// TODO Auto-generated method stub
		
		int pellets = (int)(Math.random() * Pellet.MAX_PELLETS);
		int viruses = (int)(Math.random() * Virus.MAX_VIRUSES);
		
		
		//System.out.println(pellets);
		
		
		for(int i = 0; i < pellets; i++) {
			this.addPellet(new Pellet(new Point((int)(Math.random() * MAX_X_SIZE), (int) (Math.random() * MAX_Y_SIZE))));
			
			if(this.pellets.size() > Pellet.MAX_PELLETS) {
				return;
			}
			
		}
		
		for(int i = 0; i < viruses; i++) {
			this.addVirus(new Virus(new Point((int)(Math.random() * MAX_X_SIZE), (int) (Math.random() * MAX_Y_SIZE))));
			
			if(this.viruses.size() > Virus.MAX_VIRUSES) {
				return;
			}
			
		}
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<Player> cells) {
		this.players = cells;
	}
	
	public void addPlayer(Player player) {
		this.players.add(player);
	}

	public ArrayList<Pellet> getPellets() {
		return pellets;
	}

	public void setPellets(ArrayList<Pellet> pellets) {
		this.pellets = pellets;
	}
	
	public void addPellet(Pellet p) {
		this.pellets.add(p);
	}
	
	public ArrayList<Virus> getViruses() {
		return viruses;
	}

	public void setViruses(ArrayList<Virus> viruses) {
		this.viruses = viruses;
	}
	
	public void addVirus(Virus v) {
		this.viruses.add(v);
	}
	
	
	public void step() {
		
		createLeaderboard();
		
		for(Player p:players) {
			
			if(p.isDead()) {
				p.initialize();
			}
			
			p.step();
		}
		
		for(DeadCell d: deadcells) {
			d.step();
		}
		
	}
	
	private void createLeaderboard() {
		// TODO Auto-generated method stub
		
		
		int playerBotSize = 0;
		
		for(Player p: this.getPlayers()){
			if(!(p instanceof TargetBot)) {
				playerBotSize++;
			}
		}
		
		int[] masses = new int[playerBotSize];
		
		int ind = 0;
		
		for(Player p: this.getPlayers()) {
			
			if(!(p instanceof TargetBot)) {
				masses[ind++] = p.getTotalMass();
			}
			
		}
		
		quickSort(masses, 0, masses.length - 1);
		
		ArrayList<Player> ret = new ArrayList<Player>();
		
		for(int i = 0; i < masses.length; i++) {
			for(int j = 0; j < players.size(); j++)
				if(masses[i] == players.get(j).getTotalMass() && !ret.contains(players.get(j)) && !(players.get(j) instanceof TargetBot)) {
					ret.add(players.get(j));
				}
		}
		
		this.leaderboard = ret;
		
		ArrayList<Player> nRet = new ArrayList<>();
		
		int specializedSize = this.players.size() - playerBotSize;
	
		if(specializedSize > 0) {
			masses = new int[specializedSize];
			
			ind = 0;
			
			for(Player p: this.getPlayers()) {
				
				if((p instanceof TargetBot)) {
					masses[ind++] = ((TargetBot) p).getKillCount();
				}
				
			}
			
			quickSort(masses, 0, masses.length - 1);
			
			for(int i = 0; i < masses.length; i++) {
				for(int j = 0; j < players.size(); j++)
					if((players.get(j) instanceof TargetBot) && masses[i] == ((TargetBot) players.get(j)).getKillCount() && !nRet.contains(players.get(j))) {
						nRet.add(players.get(j));
					}
			}
			
			this.specializedLeaderBoard = nRet;
		}
		/*ArrayList<Player> ret = new ArrayList<Player>();
		
		for(int i = 0; i < 5; i++) {
			
			int maxInd = Integer.MIN_VALUE, maxMass = Integer.MIN_VALUE;
			int ind = 0;
			
			for(int j = 0; j < players.size(); j++) {
				if(players.get(j).getTotalMass() > maxMass) {
					
					boolean pass = true;
					
					for(int k = 0; k < ret.size(); k++) {
						if(Player.equals(ret.get(k), players.get(j))) {
							pass = false;
						}
					}
					
					if(pass) {
						maxInd = j;
						maxMass = players.get(j).getTotalMass();
					}
				}
				
				
			}
			
			ret.add(players.get(maxInd));
			
		}*/
		
		
	}
	
	public static void quickSort(int[] array, int lowerIndex, int higherIndex) {
        
        int i = lowerIndex;
        int j = higherIndex;
        // calculate pivot number, I am taking pivot as middle index number
        int pivot = array[lowerIndex+(higherIndex-lowerIndex)/2];
        // Divide into two arrays
        while (i <= j) {
            /**
             * In each iteration, we will identify a number from left side which 
             * is greater then the pivot value, and also we will identify a number 
             * from right side which is less then the pivot value. Once the search 
             * is done, then we exchange both numbers.
             */
            while (array[i] < pivot) {
                i++;
            }
            while (array[j] > pivot) {
                j--;
            }
            if (i <= j) {
                exchangeNumbers(array, i, j);
                //move index to next position on both sides
                i++;
                j--;
            }
        }
        // call quickSort() method recursively
        if (lowerIndex < j)
            quickSort(array, lowerIndex, j);
        if (i < higherIndex)
            quickSort(array, i, higherIndex);
    }
 
    private static void exchangeNumbers(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
	
	public void generatePellets() {
		
		for(int i = 0; i < deadcells.size(); i++) {
			if(deadcells.get(i).isEaten()) {
				deadcells.remove(i);
			}
		}
		
		for(int i = 0; i < pellets.size(); i++) {
			if(pellets.get(i).isEaten()) {
				pellets.remove(i);
			}
		}
		
		for(int i = 0; i < viruses.size(); i++) {
			if(viruses.get(i).isEaten()) {
				viruses.remove(i);
			}
		}
		
		this.initialize();
	}

	public ArrayList<Player> getLeaderboard() {
		ArrayList<Player> board = new ArrayList<Player>();
		
		for(int i = 0; i < 5; i++) {
			board.add(leaderboard.get(leaderboard.size() - 1 - i));
		}
		
		return board;
	}

	public void setLeaderboard(ArrayList<Player> leaderboard) {
		this.leaderboard = leaderboard;
	}
	
	public int getPosition(Player p) {
		for(int i = 0; i < leaderboard.size(); i++) {
			if(Player.equals(p, leaderboard.get(i))) {
				return leaderboard.size() - 1 - i;
			}
		}
		
		return -1;
	}

	public ArrayList<Player> getSpecializedLeaderBoard() {
		ArrayList<Player> board = new ArrayList<Player>();
		
		for(int i = 0; i < 5; i++) {
			board.add(this.specializedLeaderBoard.get(this.specializedLeaderBoard.size() - 1 - i));
		}
		
		return board;
	}

	public void setSpecializedLeaderBoard(ArrayList<Player> specializedLeaderBoard) {
		this.specializedLeaderBoard = specializedLeaderBoard;
	}

	public ArrayList<DeadCell> getDeadcells() {
		return deadcells;
	}

	public void setDeadcells(ArrayList<DeadCell> deadcells) {
		this.deadcells = deadcells;
	}

	public void addDeadCell(DeadCell toLaunch) {
		// TODO Auto-generated method stub
		this.deadcells.add(toLaunch);
	}
	
}
