package student_player.mytools;

import java.util.ArrayList;

import hus.HusBoardState;
import hus.HusMove;
import student_player.StudentPlayer;

public class MCTree{
		private Node root;
		private ArrayList<Double> AccumStats;
		private double randParameter;
		public MCTree(){
			Node root = null;
			ArrayList<Double> AccumStats = new ArrayList<Double>();
			double randParameter = 0.75;
		}
		public Node getRoot(){
			return this.root;
		}
		// build search tree given a state, and initialized time
		public void buildTree(HusBoardState state){
			Node rootNode = new Node(state);
			this.root = rootNode;
			
		}
		// Monte Carlo Simulation using random moves
		public HusMove simulateRandom(long initTime, int pid, int oid){
			long timeAllowed = 1500;
			HusMove bestMove = null;
			/*
			if(this.root.getState().getTurnNumber() == 0){
				timeAllowed = 29500;
			}
			else{
				timeAllowed = 1500;
			}
			*/
			while(System.currentTimeMillis() < initTime + timeAllowed){
				//System.out.println("Player: " + player_id);
				for(int i = 0; i < this.root.getChildren().size(); i++){
					HusBoardState tmp = (HusBoardState) this.root.getState().clone();
					tmp.move(this.root.getChildren().get(i).getMove());
					while((tmp.gameOver() != true) && (tmp.getTurnNumber() <= 5000)){
						//Node tmpNode = new Node(tmp);
						//tmp.move(tmpNode.minimaxPolicy(pid, oid));
						tmp.move(tmp.getRandomMove());
					}
					if(tmp.gameOver() || (tmp.getTurnNumber() == 5000)){
						if(tmp.getWinner() == pid){
							this.root.getChildren().get(i).win();
						}
						else{
							this.root.getChildren().get(i).lose();
						}
					}
				}
			}
			int numSim = 0;
			double bestScore = 0.0;
			for(int i = 0; i < this.root.getChildren().size(); i++){
				double score = this.root.getChildren().get(i).getStat();
				if(score > bestScore){
					bestScore = score;
					numSim = this.root.getChildren().get(i).getNumSim();
					bestMove = this.root.getChildren().get(i).getMove();
				}
			}
			System.out.println("Number of simulation: " + numSim);
			System.out.println("Probability of winning: "+ bestScore);
			return bestMove;
		}
		// Monte Carlo Simulation using minimax policy
		public HusMove simulateMinimax(long initTime, int pid, int oid){
			long timeAllowed = 1700;
			HusMove bestMove = null;
			/*
			if(this.root.getState().getTurnNumber() == 0){
				timeAllowed = 29500;
			}
			else{
				timeAllowed = 1500;
			}
			*/
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
		
		// if probability of wins changes drastically, change the policy parameter; increase/decrease portion of random moves
		public void parameterSwitcher(){
			double[] lastThreeStats = new double[3];
			for(int i = 0; i < lastThreeStats.length; i++){
				lastThreeStats[2-i] = this.AccumStats.get(this.AccumStats.size() - i - 1);
			}
			if(((lastThreeStats[0] - lastThreeStats[1]) > 0) && ((lastThreeStats[1] - lastThreeStats[2]) > 0)){
				this.randParameter = this.randParameter - (lastThreeStats[0] - lastThreeStats[2]);
			}
			else if(((lastThreeStats[0] - lastThreeStats[1]) > 0.2) || (lastThreeStats[1] - lastThreeStats[2]) > 0.2){
				this.randParameter -= 0.2;
			}
			/*
			else{
				if(((lastThreeStats[0] - lastThreeStats[1]) < 0) && ((lastThreeStats[1] - lastThreeStats[2]) < 0)){
					this.randParameter = this.randParameter + (lastThreeStats[0] - lastThreeStats[2]);
				}
				else if(((lastThreeStats[1] - lastThreeStats[0]) > 0.2) || (lastThreeStats[2] - lastThreeStats[1]) > 0.2){
					this.randParameter += 0.2;
				}
			}
			
			// bound the parameter in [0.0, 1.0]
			if (this.randParameter > 1.0){
				this.randParameter = 1.0;
			}
			*/
			else if(this.randParameter < 0.0){
				this.randParameter = 0.0;
			}
			
		}
		
}
