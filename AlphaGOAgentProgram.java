/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uniltiranyu.examples.games.squares;

import static java.lang.Integer.max;
import static java.lang.Integer.min;
import java.util.LinkedList;
import java.util.List;

import speco.array.Array;
import uniltiranyu.Action;
import uniltiranyu.AgentProgram;
import uniltiranyu.Percept;

/**
 *
 * @author AndresDiaz
 */
public class AlphaGOAgentProgram implements AgentProgram {
    // Player's color
    protected String color;
    // May use any structure as memory..
    
    /**
     * Creates a Dummy agent for playing squares
     * @param color Player's color
     */
    public AlphaGOAgentProgram( String color ){
        this.color = color;        
    }
    
    // Who get more points function
    public int[] howPoints(TreeNode node) {
        int[][][] b = node.board;
        int[] points = new int[2];
        return points;
    }
    
    // Heuristic Function
    public int evaluate(TreeNode node) {
        int score = 0;
        return score;
    }
    
    // Alpha Beta Algorithm  
    public int decisionAlphaBeta(TreeNode node, int depth, int alpha, int beta, boolean maxPlayer) {
        int value;
        if (depth == 0) {
            return evaluate(node);
        }
        if (maxPlayer) {
            value = -2147483648; // - infinite 
            for (TreeNode childNode : node.children) {
                value = max(value, decisionAlphaBeta(childNode, depth-1, alpha, beta, false));
                alpha = max(alpha, value);
                if (alpha >= beta) {
                    break;
                }
            }
            return value;
        }
        
        else {
            value = 2147483647; // + infinite
            for (TreeNode childNode : node.children) {
                value = min(value, decisionAlphaBeta(childNode, depth-1, alpha, beta, true));
                beta = min(beta, value);
                if (beta <= alpha) {
                    break;
                }
            }
            return value;
        }
    }
    
    @Override
    /**
     * Computes an action to be carried by the agent according to the perception
     * of the environment
     * @param p Perception received by the agent program     * 
     */
    public Action compute(Percept p) {
        // Determines if it is the agents turn
        if( p.get(Squares.TURN).equals(color) ){
            // Gets the size of the board
            int size = Integer.parseInt((String)p.get(Squares.SIZE));
            int i = 0;
            int j = 0;
            // Gets an square randomly and try to draw a border if possible
            Array<String> v = new Array<String>();
            while(v.size()==0){
              i = (int)(size*Math.random());
              j = (int)(size*Math.random());
              // p.get(i+":"+j+":"+Squares.LEFT)
              // gets "true" if the left border of square (i,j) is set, "false" otherwise
              if(((String)p.get(i+":"+j+":"+Squares.LEFT)).equals(Squares.FALSE))
                v.add(Squares.LEFT);
              if(((String)p.get(i+":"+j+":"+Squares.TOP)).equals(Squares.FALSE))
                v.add(Squares.TOP);
              if(((String)p.get(i+":"+j+":"+Squares.BOTTOM)).equals(Squares.FALSE))
                v.add(Squares.BOTTOM);
              if(((String)p.get(i+":"+j+":"+Squares.RIGHT)).equals(Squares.FALSE))
                v.add(Squares.RIGHT);
            }
            try{
            	String move = v.get((int)(Math.random()*v.size()));
            	// Action( i+":"+j+":"+Squares.BOTTOM ) 
            	// draws bottom border of square (i,j)
                return new Action( i+":"+j+":"+move);
            }catch(Exception e){}
        }
        return new Action(Squares.PASS);
    }

    @Override
    public void init() {
    }
}

// -----------------------------------------------------------------------------
// Data Structures Required
class TreeNode {
    int[][][] board;
    int[] move;
    TreeNode parent;
    List<TreeNode> children;

    public TreeNode(int[][][] board, int[] move) {
        this.board = board;
        this.move = move;
        this.children = new LinkedList<TreeNode>();
    }

    public TreeNode addChild(int[][][] cBoard, int[] mBoard) {
        TreeNode childNode = new TreeNode(cBoard, mBoard);
        childNode.parent = this;
        this.children.add(childNode);
        return childNode;
    }

    // other features ..
}