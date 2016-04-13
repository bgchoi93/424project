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
	
	public Node(HusBoardState state){
		this.CurrentState = state;
		this.Parent = null;
		this.Children = null;
		this.Move = null;
		this.numberW = 0;
		this.numberP = 0;
		this.Score = 0.0;
	}
	
	//Getters
	public HusBoardState getState(){
		return this.CurrentState;
	}
	public HusMove getMove(){
		return this.Move;
	}
	
	public Node getParent(){
		return this.Parent;
	}
	public ArrayList<Node> getChildren(){
		return this.Children;
	}
	public double getStat(){
		double w = (double)this.numberW;
		double p = (double)this.numberP;
		return w/p;
	}
	
	// to be deleted: getNumSim(), getNumWins()
	public int getNumSim(){
		return this.numberP;
	}
	public int getNumWins(){
		return this.numberW;
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
	// set att + def combined score for the given node according to the current state;
	public void setCombinedScore(int player){
		int opponent = 0;
		if (player == 0){
			opponent = 1;
		}
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
	
}