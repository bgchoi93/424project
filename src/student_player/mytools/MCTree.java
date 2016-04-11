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
			long timeAllowed = 0;
			if(state.getTurnNumber() == 0){
				timeAllowed = 29500;
			}
			else{
				timeAllowed = 1500;
			}
			this.root.addChildren();
			// simulate for each child
			while(System.currentTimeMillis() < (initTime + timeAllowed)){
				for (int i = 0; i < this.root.getChildren().size(); i++){
					this.root.getChildren().get(i).simulate(player_id, opponent_id);
				}
			}
			double bestScore = 0.0;
			HusMove bestMove = null;
			for (int i = 0; i < this.root.getChildren().size(); i++){
				double score = this.root.getChildren().get(i).getScore();
				if(score > bestScore){
					bestScore = score;
					bestMove = this.root.getChildren().get(i).getMove();
				}
			}
			return bestMove;
		   
		}
}
