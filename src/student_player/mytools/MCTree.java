package student_player.mytools;

import java.util.ArrayList;

import hus.HusBoardState;
import hus.HusMove;
import student_player.StudentPlayer;

public class MCTree extends StudentPlayer {
		private Node root;
		public MCTree(HusBoardState state){
			HusBoardState current = state;
			Node root = null;
			ArrayList<Node> children;
		}
		public Node getRoot(){
			return this.root;
		}
		public void setRoot(Node r){
			this.root = r;
		}
		// build search tree given a state, and initialized time
		public MCTree buildTree(HusBoardState state, long initTime){
			HusBoardState copy_state = (HusBoardState) state.clone();
			int firstPlayer = copy_state.firstPlayer();
			
			/*
			int[][] pits = copy_state.getPits();
			int[] my_pits = pits[player_id];
	        int[] op_pits = pits[opponent_id];
	        int depth = 0;
	        */
			
			int turnNum =  copy_state.getTurnNumber();
	        MCTree tree = new MCTree(copy_state);
	        Node rootNode = new Node(copy_state);
	        tree.setRoot(rootNode);
	        
	        ArrayList<HusMove> moves = copy_state.getLegalMoves();
	        root.addChildren();
	        
	        
	        
	        /*
	        if(turnNum == 0){
	        	 if (firstPlayer == player_id){
	 	        	while(System.currentTimeMillis() > (initTime + 29500) || depth < 5000){
	 	    			
	 	    		}
	 	        }
	 	        else{
	 	        	while(System.currentTimeMillis() > (initTime + 29000)){
	 	    			
	 	    		}
	 	        }
	        }
	        */
	       
			return null;
		}
		public int attScore(HusBoardState state, HusMove move){
			HusBoardState tmpState = (HusBoardState) state.clone();
			int [][] tmpPits = tmpState.getPits();
			int[] opPits = tmpPits[opponent_id];
			int score = 0;
			//compute the number of seeds in opponent's pits
			int beforeMove = 0;
			for(int pit: opPits){
				beforeMove += pit;
			}
			//make a move
			tmpState.move(move);
			//compute the number of seeds in opponent's pits
			int afterMove = 0;
			for(int pit: opPits){
				afterMove += pit;
			}
			//score = number of seeds won
			score = afterMove - beforeMove;
			return score;
		}
		public int defScore(HusBoardState state, HusMove move){
			HusBoardState tmpState = (HusBoardState) state.clone();
			int [][] tmpPits = tmpState.getPits();
			int[] myPits = tmpPits[player_id];
			int score = 0;
			
			//compute the number of seeds in myPits in the inner row, and get the number of seeds in the safe zone(outer row)
			int beforeMoveUnsafe = 0;
			for(int i = 16; i < myPits.length; i++){
				if(myPits[i] != 0){
					beforeMoveUnsafe = myPits[i] + myPits[31-i];
				}
			}
			
			//make a move
			tmpState.move(move);
			
			//compute the number of seeds in myPits in the inner row, and get the number of seeds in the safe zone(outer row)
			int afterMoveUnsafe = 0;
			for(int i = 16; i < myPits.length; i++){
				if(myPits[i] != 0){
					afterMoveUnsafe = myPits[i] + myPits[31-i];
				}
			}
			
			//score = how many seeds are became safe
			score = beforeMoveUnsafe - afterMoveUnsafe;
			/*
			//after making a move, simulate opponents moves and get the minimum number of remaining seeds in myPits
			int minNum = 0;
			int mySeeds = 0;
			for(HusMove opMove: tmpState.getLegalMoves()){
				HusBoardState tmp = (HusBoardState) tmpState.clone();
				tmp.move(opMove);
				for(int pit: myPits){
					mySeeds += pit;
				}
				if(minNum > mySeeds){
					minNum = mySeeds;
				}
			}
			// score = number of seeds before move - number of seeds left after my, opponents moves, 
			// i.e. how many seed lost from opponent's attack after the move
			score = numSeeds - minNum;
			*/
			return score;
		}
		public double evalFunctionAtt(HusBoardState state, HusMove move){
			double score = 0.0;
			double attack = (double)attScore(state, move);
			double defense = (double)defScore(state, move);
			score = 0.7*attack + 0.3*defense;
			return score;
		}
		public double evalFunctionDef(HusBoardState state, HusMove move){
			double score = 0.0;
			double attack = (double)attScore(state, move);
			double defense = (double)defScore(state, move);
			score = 0.3*attack + 0.7*defense;
			return score;
		}
		public double evalFunctionNeutral(HusBoardState state, HusMove move){
			double score = 0.0;
			double attack = (double)attScore(state, move);
			double defense = (double)defScore(state, move);
			score = 0.5*attack + 0.5*defense;
			return score;
		}
		
		public void simulate(Node a){
			for(Node i: a.getChildren()){
				HusBoardState tmp = (HusBoardState) a.getState().clone();
				tmp.move(a.getMove());
			}
		}
}
