package student_player.mytools;
import java.util.ArrayList;

import hus.HusBoardState;
import hus.HusMove;

public class Node {
	private HusMove move;
	private int numberW;
	private int numberP;
	private Node Parent;
	private ArrayList<Node> Children;
	
	public Node(){
		this.move = move;
		this.numberW = numberW;
		this.numberP = numberP;
		this.Parent = Parent;
		this.Children = Children;
	}
	//Getters
	public HusMove getNodeMove(){
		return this.move;
	}
	public double getStat(){
		double w = (double)this.numberW;
		double p = (double)this.numberP;
		return w/p;
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
	public void setNode(HusMove m, int w, int p){
		this.move = m;
		this.numberW = w;
		this.numberP = p;
	}
	public void setParent(Node p){
		this.Parent = p;
	}
	public void addChildren(Node p){
		this.Children.add(p);
	}
}