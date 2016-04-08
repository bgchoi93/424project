package student_player.mytools;
import java.util.ArrayList;

import hus.HusBoardState;
import hus.HusMove;

public class Node {
	private HusBoardState CurrentState;
	private HusMove BestMove;
	private HusMove Move;
	private int numberW;
	private int numberP;
	private Node Parent;
	private ArrayList<Node> Children;
	private double Score;
	
	public Node(HusBoardState state){
		this.CurrentState = state;
		this.Move = Move;
		this.BestMove = BestMove;
		this.numberW = numberW;
		this.numberP = numberP;
		this.Parent = Parent;
		this.Children = Children;
	}
	//Getters
	public HusBoardState getState(){
		return this.CurrentState;
	}
	public HusMove getMove(){
		return this.Move;
	}
	public double getStat(){
		double w = (double)this.numberW;
		double p = (double)this.numberP;
		return w/p;
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
	//Setters
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
	public void setParent(Node p){
		this.Parent = p;
	}
	public void addChildren(){
		for (HusMove move: this.CurrentState.getLegalMoves()){
			HusBoardState tmp = (HusBoardState) this.CurrentState.clone();
			tmp.move(move);
			Node child = new Node(tmp);
			this.Children.add(child);
		}
	}
	public void setScore(double score){
		this.Score = score;
	}
	
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
}