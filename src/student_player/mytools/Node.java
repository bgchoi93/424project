package student_player.mytools;
import java.util.ArrayList;

import hus.HusBoardState;
import hus.HusMove;
import java.util.Random;

public class Node{

	private HusBoardState CurrentState;
	//private HusMove BestMove;
	private HusMove Move;
	private int numberW;
	private int numberP;
	private Node Parent;
	private ArrayList<Node> Children;
	private double Score;
	private double Stat;
	private int numSim; // number of simulation
	//private Node next;
	
	public Node(HusBoardState state){
		this.CurrentState = state;
		this.Parent = null;
		this.Children = null;
		this.Move = null;
		this.numberW = 0;
		this.numberP = 0;
		this.Score = 0.0;
		this.Stat = 0.0;
		this.numSim = 0;
	}
	public Node getNode(){
		return this;
	}
	//Getters
	public HusBoardState getState(){
		return this.CurrentState;
	}
	public HusMove getMove(){
		return this.Move;
	}
	public double getScore(){
		return this.Score;
	}
	
	public Node getParent(){
		return this.Parent;
	}
	public ArrayList<Node> getChildren(){
		return this.Children;
	}
	public int getNumSim(){
		return this.numberP;
	}
	public int getNumWins(){
		return this.numberW;
	}
	public double getStat(){
		double w = (double)this.numberW;
		double p = (double)this.numberP;
		return w/p;
	}
	
	//Setters
	
	public void win(){
		this.numberP += 1;
		this.numberW += 1;
	}
	public void lose(){
		this.numberP += 1;
	}
	
	public void setStat(int w, int p){
		this.numberW = w;
		this.numberP = p;
	}
	
	public void setParent(Node p){
		this.Parent = p;
	}
	public void addChildren(){
		this.Children = new ArrayList<Node>();
		for (HusMove move: this.CurrentState.getLegalMoves()){
			HusBoardState tmp = (HusBoardState) this.CurrentState.clone();
			tmp.move(move);
			Node child = new Node(tmp);
			child.Move = move;
			child.Parent = this;
			child.setStat(0, 0);
			this.Children.add(child);
			
		}
	}

	
	// Evaluation of state
	public void evaluateState(int player_id, int opponent_id){
		// won: score = 100
		if (this.CurrentState.getWinner() == player_id){
			this.Score = 100.0;
		}
		// lost: score = 0
		else if (this.CurrentState.getWinner() == opponent_id){
			this.Score = 0.0;
		}
		// current state: opponent's turn => get the number of safe seeds, and then compute the score
		else if(this.CurrentState.getTurnPlayer() == opponent_id){
			HusBoardState current = (HusBoardState) this.CurrentState.clone();
			int[][] pits = current.getPits();
			int[] myPits = pits[player_id];
			
			int total = 0;
			for(int i = 0; i < myPits.length; i++){
				total += myPits[i];
			}
			int maxCaptured = 0;
			// for each opponent's move, get the number of safe seeds (not able to be captured)
			for(HusMove move: current.getLegalMoves()){
				HusBoardState tmp = (HusBoardState) current.clone();
				tmp.move(move);
				int afterOpMove = 0;
				for(int i = 0; i < myPits.length; i++){
					afterOpMove += myPits[i];
				}
				if(maxCaptured > (total - afterOpMove)){
					maxCaptured = (total - afterOpMove);
				}
			}
			// score = percentage of min{safe seeds}/max{seeds}
			double score = (double)(total - maxCaptured);
			score = (score/96.0)*100.0;
			this.Score = score;
		}
		// current state: my turn => for each of my move, get the number of safe seeds, and then compute the maximum score
		else{
			HusBoardState current = (HusBoardState) this.CurrentState.clone();
			double bestScore = 0.0;
			for(HusMove myMove: current.getLegalMoves()){
				HusBoardState tmp = (HusBoardState) current.clone();
				tmp.move(myMove);
				int[][] pits = current.getPits();
				int[] myPits = pits[player_id];
				
				int total = 0;
				for(int i = 0; i < myPits.length; i++){
					total += myPits[i];
				}
				int maxCaptured = 0;
				// for each opponent's move, get the number of safe seeds (not able to be captured)
				for(HusMove opMove: current.getLegalMoves()){
					HusBoardState afterMyMove = (HusBoardState) current.clone();
					afterMyMove.move(opMove);
					int afterOpMove = 0;
					for(int i = 0; i < myPits.length; i++){
						afterOpMove += myPits[i];
					}
					if(maxCaptured > (total - afterOpMove)){
						maxCaptured = (total - afterOpMove);
					}
				}
				double score = (double)(total - maxCaptured);
				score = (score/96.0)*100.0;
				if (score > bestScore){
					bestScore = score;
				}
			}
			this.Score = bestScore;
		}
	}
	
	// simple policy: return the move which maximizes (# player's seeds - # opponent's seeds)
	public HusMove simplePolicy(int pid, int oid){
		HusMove bestMove = null;
		int bestDiff = -97;
		// player's turn: maximize the difference
		if (this.getState().getTurnPlayer() == pid){
			for (int i = 0; i < this.CurrentState.getLegalMoves().size(); i++){
				HusBoardState tmp = (HusBoardState) this.CurrentState.clone();
				tmp.move(tmp.getLegalMoves().get(i));
				int [][] tmpPits = tmp.getPits();
				int[] myPits = tmpPits[pid];
				int[] opPits = tmpPits[oid];
				
				int mySeeds = 0;
				int opSeeds = 0;
				for(int j = 0; j < myPits.length; j++){
					mySeeds += myPits[j];
				}
				for(int j = 0; j < opPits.length; j++){
					opSeeds += opPits[j];
				}
				int difference = (mySeeds - opSeeds);
				if (bestDiff < difference){
					bestDiff = difference;
					bestMove = tmp.getLegalMoves().get(i);
				}
			}
		}
		else{
			
		}
		
		return bestMove;
	}
	
	// attack score: how many opponent's seeds does the move capture
	public int getAttScore(int player){
		int beforeMove = this.getParent().seedNumber(player);
		int afterMove = this.seedNumber(player);
		int score = afterMove - beforeMove;
		return score;
	}
	// defense score: how many player's seeds become un-capture-able after the move
	public int getDefScore(int player){
		int beforeMove = this.getParent().safeSeeds(player);
		int afterMove = this.safeSeeds(player);
		int score = afterMove - beforeMove;
		return score;
	}
	// set att + def combined score for the given node;
	public void setCombinedScore(int player){
		int opponent = 0;
		if (player == 0){
			opponent = 1;
		}
		//System.out.println("Attack: " + this.getAttScore(player));
		//System.out.println("Defense: " + this.getDefScore(player));
		double aScore = (double) this.getAttScore(player);
		double dScore = (double) this.getDefScore(player);

		int playerSeeds = this.seedNumber(player);
		int opponentSeeds = this.seedNumber(opponent);
		
		// advantage: offensive
		double score = 0.0;
		if((playerSeeds - opponentSeeds) > 8){
			score = (0.7 * aScore) + (0.3 * dScore);
		}
		// disadvantage: defensive
		else if((opponentSeeds - playerSeeds) > 8){
			score = (0.3 * aScore) + (0.7 * dScore);
		}
		// neutral
		else{
			score = (0.5 * aScore) + (0.5 * dScore);
		}
		this.Score = score;
	}
	
	public HusMove minimaxPolicy(int player){
		HusMove bestMove = null;
		double bestScore = -25; // possible minimum score is -24: 0 captured, 48 became unsafe 
		for(Node child: this.Children){
			child.setCombinedScore(player);
			if (bestScore < child.Score){
				bestScore = child.Score;
				bestMove = child.Move;
			}
		}
		//System.out.println("Best Score: " + bestScore);
		Random randGenerator = new Random();
		double randNumber = randGenerator.nextDouble();
		if(randNumber < 0.7){
			return bestMove;
		}
		else{
			return (HusMove) this.CurrentState.getRandomMove();
		}
		
	}
	
	// get total number of seeds in the player's pit
	public int seedNumber(int player){
		int numSeeds = 0;
		int[] pits = this.CurrentState.getPits()[player];
		for(int i = 0; i < pits.length; i++){
			numSeeds += pits[i];
		}
		return numSeeds;
	}
	// get number of un-capture-able seeds
	public int safeSeeds(int player){
		int numSeeds = 0;
		int[] pits = this.CurrentState.getPits()[player];
		for(int i = 16; i < pits.length; i++){
			if (pits[i] != 0){
				numSeeds = pits[i] + pits[31-i];
			} 
		}
		return numSeeds;
	}
	
	
	// update score and number of simulations on this node	
	public void calculateScore(double newScore){
		double n = (double)(this.numSim + 1);
		this.Score = (this.Score*(double)this.numSim) + newScore;
		this.Score = (this.Score)/n;
		this.numSim += 1;
	}
	
	
}