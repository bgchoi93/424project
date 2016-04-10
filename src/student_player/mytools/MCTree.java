package student_player.mytools;

import java.util.ArrayList;

import hus.HusBoardState;
import hus.HusMove;
import student_player.StudentPlayer;

public class MCTree extends StudentPlayer {
		private Node root;
		public MCTree(){
			Node root = null;
		}
		public Node getRoot(){
			return this.root;
		}
		// build search tree given a state, and initialized time
		public void buildTree(HusBoardState state){
			Node rootNode = new Node(state);
			this.root = rootNode;
		}
		public HusMove getBestMove(HusBoardState state, long initTime){
			 this.root.setStat(0, 0);
		     this.root.addChildren();
		     this.simulate(initTime, state.getTurnNumber());
		     HusMove bestMove = null;
		     double bestProb = 0.0;
		     for (Node child: this.root.getChildren()){
		    	 if(child.getStat() > bestProb){
		    		 bestProb = child.getStat();
		    		 bestMove = child.getMove();
		    	 }
		     }
		     
		     System.out.println("Probability of winning: " + bestProb);
		     return bestMove;
		}

		// Monte Carlo Simulation
		public double simulate(long initTime, int initialTurn){
			long timeAllowed;
			if(initialTurn == 0){
				timeAllowed = 29000;
			}
			else{
				timeAllowed = 1000;
			}
			while(System.currentTimeMillis() < initTime + timeAllowed){
				for(Node child: this.root.getChildren()){
					HusBoardState tmp = (HusBoardState) this.root.getState().clone();
					while((tmp.gameOver() != true) && tmp.getTurnNumber() < 5000){
						tmp.move(tmp.getRandomMove());
						if(tmp.gameOver() || tmp.getTurnNumber() == 5000){
							if(tmp.getWinner() == player_id){
								child.win();
							}
							else{
								child.lose();
							}
						}
					}
				}
			}
			return this.root.getStat();
		}
		
		
}
