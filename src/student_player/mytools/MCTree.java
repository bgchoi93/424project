package student_player.mytools;

import hus.HusBoardState;
import hus.HusMove;

public class MCTree{
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
		
		// Monte Carlo Simulation using minimax policy and return the best move according to the statistic
		public HusMove simulateMinimax(long initTime, int pid, int oid){
			long timeAllowed = 1700;
			HusMove bestMove = null;
			if(this.root.getState().getTurnNumber() == 0){
				timeAllowed = 29500;
			}
			else{
				timeAllowed = 1500;
			}
			while(System.currentTimeMillis() < initTime + timeAllowed){
				for(int i = 0; i < this.root.getChildren().size(); i++){
					HusBoardState tmpState = (HusBoardState) this.root.getState().clone();
					tmpState.move(this.root.getChildren().get(i).getMove());
					while((tmpState.gameOver() != true) && (tmpState.getTurnNumber() <= 5000)){
						Node tmpNode = new Node(tmpState);
						tmpNode.addChildren();
						tmpState.move(tmpNode.minimaxPolicy(tmpState.getTurnPlayer()));
					}
					if(tmpState.gameOver() || (tmpState.getTurnNumber() == 5000)){
						if(tmpState.getWinner() == pid){
							this.root.getChildren().get(i).win();
						}
						else{
							this.root.getChildren().get(i).lose();
						}
					}
				}
			}
			// return best move according to the statistic stored in each node
			int numSim = 0;
			int numWin = 0; 
			double bestScore = 0.0;
			for(int i = 0; i < this.root.getChildren().size(); i++){
				double score = this.root.getChildren().get(i).getStat();
				if(score > bestScore){
					bestScore = score;
					numSim = this.root.getChildren().get(i).getNumSim();
					numWin = this.root.getChildren().get(i).getNumWins();
					bestMove = this.root.getChildren().get(i).getMove();
				}
			}
			System.out.println("Number of simulation: " + numSim);
			System.out.println("Number of wins: " + numWin);
			System.out.println("Probability of winning: "+ bestScore);
			return bestMove;
		}
}
