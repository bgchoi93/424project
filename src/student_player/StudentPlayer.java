package student_player;

import hus.HusBoardState;
import hus.HusPlayer;
import hus.HusMove;

import student_player.mytools.MCTree;

/** A Hus player submitted by a student. */
public class StudentPlayer extends HusPlayer {

    /** You must modify this constructor to return your student number.
     * This is important, because this is what the code that runs the
     * competition uses to associate you with your agent.
     * The constructor should do nothing else. */
    public StudentPlayer() { super("260526672"); }

    /** This is the primary method that you need to implement.
     * The ``board_state`` object contains the current state of the game,
     * which your agent can use to make decisions. See the class hus.RandomHusPlayer
     * for another example agent. */
    public HusMove chooseMove(HusBoardState board_state)
    {
    	long initTime = System.currentTimeMillis();
    
        HusBoardState copy_state = (HusBoardState) board_state.clone();
    	MCTree mctree = new MCTree();
        mctree.buildTree(copy_state);
        mctree.getRoot().addChildren();
        HusMove move = mctree.simulateMinimax(initTime, player_id, opponent_id);
        return move;
    }
    
}
