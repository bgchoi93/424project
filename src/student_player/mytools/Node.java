package student_player.mytools;
import java.util.ArrayList;

import hus.HusBoardState;
import hus.HusMove;

public class Node{

	private HusBoardState CurrentState;
	//private HusMove BestMove;
	private HusMove Move;
	//private int numberW;
	//private int numberP;
	private Node Parent;
	private ArrayList<Node> Children;
	private double Score;
	private int numSim; // number of simulation
	//private Node next;
	
	public Node(HusBoardState state){
		this.CurrentState = state;
		this.Parent = null;
		this.Children = null;
		this.Move = null;
		//this.numberW = 0;
		//this.numberP = 0;
		this.Score = 0.0;
		this.numSim = 0;
		//this.next = null;
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
	/*
	public double getStat(){
		double w = (double)this.numberW;
		double p = (double)this.numberP;
		return w/p;
	}
	public void printStat(){
		System.out.println("Played: " + this.numberP);
		System.out.println("Won: " + this.numberW);
	}
	public Node getNext(){
		return this.next;
	}
	
	public ArrayList<Node> getSibling(){
		ArrayList<Node> siblings = new ArrayList<Node>();
		Node parent = this.Parent;
		for(int i = 0; i < parent.getChildren().size(); i++){
			if (parent.getChildren().get(i) != this){
				siblings.add(parent.getChildren().get(i));
			}
		}
		return siblings;
	}
	*/
	
	//Setters
	/*
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
	
	public void setBestMove(Node p){
	
		HusMove bestMove = null;
		double highestProb = 0.0;
		for(Node c: Children){
			if(c.getStat() > highestProb){
				highestProb = c.getStat();
				bestMove = c.getMove();
			}
		}
		this.BestMove = bestMove;
	}
	 */
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
			//child.setStat(0, 0);
			this.Children.add(child);
			
		}
	}
	/*
	public void setScore(double score){
		this.Score = score;
	}
	
	public void setNext(Node a){
		if(this.next == null){
			this.next = a;
			a.next = null;
		}
		else{
			Node tmp = this.next;
			this.next = a;
			a.next = tmp;
		}
	}
	*/
	
	// Evaluation of state
	public void evaluateState(int player_id, int opponent_id){
		HusBoardState current = (HusBoardState) this.CurrentState.clone();
		// won: score = 100
		if (current.gameOver() && current.getWinner() == player_id){
			this.Score = 100.0;
		}
		// lost: score = 0
		else if (current.gameOver() && current.getWinner() == opponent_id){
			this.Score = 0.0;
		}
		// current state: opponent's turn => get the number of safe seeds, and then compute the score
		else if(current.getTurnPlayer() == opponent_id){
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
		int bestDiff = -100;
		int me;
		int op;
		if(this.CurrentState.getTurnPlayer() == pid){
			me = pid;
			op = oid;
		}
		else{
			me = oid;
			op = pid;
		}
		for (int i = 0; i < this.CurrentState.getLegalMoves().size(); i++){
			HusBoardState tmp = (HusBoardState) this.CurrentState.clone();
			tmp.move(tmp.getLegalMoves().get(i));
			int [][] tmpPits = tmp.getPits();
			int[] myPits = tmpPits[me];
			int[] opPits = tmpPits[op];
			
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
		return bestMove;
	}
	// policy for Monte Carlo Simulation
	public HusMove evalPolicy(int player_id, int opponent_id){
		double maxScore = 0.0;
		HusMove bestMove = null;
		for(HusMove m: this.CurrentState.getLegalMoves()){
			HusBoardState tmp = (HusBoardState) this.CurrentState.clone();
			tmp.move(m);
			Node tempNode = new Node(tmp);
			tempNode.evaluateState(player_id, opponent_id);
			double score = tempNode.Score;
			if (score > maxScore){
				maxScore = score;
				bestMove = m;
			}
		}
		return bestMove;
	}
	// Monte Carlo Simulation for a node
	public void simulate(int pid, int oid){
		HusBoardState tmp = (HusBoardState) this.CurrentState.clone();
		while((tmp.gameOver() != true) && (tmp.getTurnNumber() <= 5000)){
			Node tempNode = new Node(tmp);
			tmp.move(tempNode.evalPolicy(pid, oid));
			if((tmp.gameOver() == true) || (tmp.getTurnNumber() == 5000)){
				Node finalNode = new Node(tmp);
				finalNode.evaluateState(pid, oid);
				this.calculateScore(finalNode.Score);
			}
		}
	}
	
	// update score and number of simulations on this node	
	public void calculateScore(double newScore){
		double x = this.Score * (double)this.numSim;
		this.Score = (x + newScore)/(double)(numSim + 1);
		this.numSim += 1;
	}
	
	/*
	public HusMove getBestMove(Node p){
		Node bestChild = null;
		for(Node i: p.Children){
			if (bestChild == null || bestChild.getStat() < i.getStat()){
				bestChild = i;
			}
		}
		HusMove bestMove = bestChild.BestMove;
		return bestMove;
	}
	*/
	
	
}