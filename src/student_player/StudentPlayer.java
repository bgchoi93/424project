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
    	/*
        // Get the contents of the pits so we can use it to make decisions.
        int[][] pits = board_state.getPits();
        
        // Use ``player_id`` and ``opponent_id`` to get my pits and opponent pits.
        int[] my_pits = pits[player_id];
        int[] op_pits = pits[opponent_id];
        */
        HusBoardState copy_state = (HusBoardState) board_state.clone();
    	MCTree mctree = new MCTree();
        mctree.buildTree(copy_state);
        mctree.getRoot().addChildren();
        //HusMove move = mctree.getBestMove(copy_state, initTime);
        HusMove move = mctree.simulateRandom(initTime, player_id, opponent_id);
        /*
        // We can see the effects of a move like this...
        HusBoardState cloned_board_state = (HusBoardState) board_state.clone();
        cloned_board_state.move(move);
		*/
        
        // But since this is a placeholder algorithm, we won't act on that information.
        return move;
    }
    
}
