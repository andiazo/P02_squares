/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uniltiranyu.examples.games.squares;

import static java.lang.Integer.max;
import static java.lang.Integer.min;
import java.util.ArrayList;
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
    
    public TreeNode createGameTree(Percept p) {
        
        boolean maxPlayer = p.get(Squares.TURN).equals(color);
        
        // Build de board
        int size = Integer.parseInt((String)p.get(Squares.SIZE));
        int[][][] board = new int[size][size][4];
        
        for ( int i = 0; i < size; i++ ) {
            for ( int j = 0; j < size; j++ ) {
                
                if(((String)p.get(i+":"+j+":"+Squares.LEFT)).equals(Squares.TRUE))
                    board[i][j][0] = 1;
                else
                    board[i][j][0] = 0;
                
                if(((String)p.get(i+":"+j+":"+Squares.TOP)).equals(Squares.TRUE))
                    board[i][j][1] = 1;
                else
                    board[i][j][1] = 0;
                
                if(((String)p.get(i+":"+j+":"+Squares.BOTTOM)).equals(Squares.TRUE))
                    board[i][j][2] = 1;
                else
                    board[i][j][2] = 0;
                
                if(((String)p.get(i+":"+j+":"+Squares.RIGHT)).equals(Squares.TRUE))
                    board[i][j][3] = 1;
                else
                    board[i][j][3] = 0;
                
            }
        }

        TreeNode node = new TreeNode(board);
        return node;
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
        if (depth == 0 || node.children == null) {
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
    
    public ArrayList<String> linesOnBox(Percept p, int row, int col){
        ArrayList<String> possibleMovements = new ArrayList<String>();
        if(((String)p.get(row+":"+col+":"+Squares.LEFT)).equals(Squares.FALSE)) {
            possibleMovements.add(Squares.LEFT);
            System.out.println("|||||| left ||||||||");
        }
        if(((String)p.get(row+":"+col+":"+Squares.TOP)).equals(Squares.FALSE)) {
            possibleMovements.add(Squares.TOP);
            System.out.println("|||||| top ||||||||");
        }
        if(((String)p.get(row+":"+col+":"+Squares.BOTTOM)).equals(Squares.FALSE)) {
            possibleMovements.add(Squares.BOTTOM);
            System.out.println("|||||| bottom ||||||||");
        }
        if(((String)p.get(row+":"+col+":"+Squares.RIGHT)).equals(Squares.FALSE)) {
            possibleMovements.add(Squares.RIGHT);
            System.out.println("|||||| right ||||||||");
        }
        return possibleMovements;
    }
    
    //
    public int[] getAdjacent(Percept p, int row, int col, ArrayList<String> possibleMovements) {
        
        int[] adjacents = new int[4];
        int size = Integer.parseInt((String)p.get(Squares.SIZE));
        int linesLeft = 5, linesBottom = 5, linesRight = 5, linesTop = 5;
        
        for (String pm: possibleMovements) {
            switch(pm) {
                case "left":
                    if (col - 1 >= 0) {
                        linesLeft = 4-linesOnBox(p, row, col-1).size();
                    }
                    break;
                case "top":
                    if (row - 1 >= 0) {
                        linesTop = 4-linesOnBox(p, row-1, col).size();
                    }   
                    break;
                case "right":
                    if (col + 1 < size) {
                        linesRight = 4-linesOnBox(p, row, col+1).size();
                    }
                    break;
                case "bottom":
                    if (row + 1 < size) {
                        linesBottom = 4-linesOnBox(p, row+1, col).size();
                    }
                    break;
                default:
                    break;
            }
        }
        
        adjacents[0] = linesLeft;
        adjacents[1] = linesBottom;
        adjacents[2] = linesRight;
        adjacents[3] = linesTop;
        
        return adjacents;
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
            // Gets an square randomly and try to draw a border if possible
            String v = new String();
            ArrayList<String> pm = new ArrayList<String>();
            int[] adjs = new int[4];
            int count = 0;
            int pos = -1;
            int[] ij = new int[2];
                    
            inicio:
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    pm = linesOnBox(p, i, j);
                    count = 4-pm.size();
                    System.out.println("Count: "+count+"| pm:"+pm+"| i:"+i+"| j:"+j);
                    if (count < 2) {
                        adjs = getAdjacent(p, i, j, pm);
                        for(int k = 0; k < 4; k++) {
                            if (adjs[k] < 2) {
                                pos = k;
                                break;
                            }
                        }
                        
                       
                        switch(pos) {
                            case 0:
                                if(((String)p.get(i+":"+j+":"+Squares.LEFT)).equals(Squares.FALSE))
                                    v = Squares.LEFT;
                                else 
                                    v = Squares.TOP;
                                break;
                            case 1:
                                if(((String)p.get(i+":"+j+":"+Squares.BOTTOM)).equals(Squares.FALSE))
                                    v = Squares.BOTTOM;
                                else 
                                    v = Squares.TOP;
                                break;
                            case 2:
                                if(((String)p.get(i+":"+j+":"+Squares.LEFT)).equals(Squares.FALSE))
                                    v = Squares.RIGHT;
                                else {
                                    v = Squares.TOP;
                                }
                                break;
                            case 3:
                                v = Squares.TOP;
                                break;

                            default:
                                v = Squares.TOP;
                                System.out.println("NO POSIBLE MOVEMENTS");
                                break;
                        }
                        System.out.println(pm);

                        ij[0] = i;
                        ij[1] = j;
                        pos = -1;
                        break inicio;
                    }
                    else {
                        ij[0] = 0;
                        ij[1] = 1;
                        v = Squares.TOP;
                        System.out.println("Si es ESTE");
                        pos = -1;
                    }
                }
            }
            try{
            	String move = v;
                int i = ij[0];
                int j = ij[1];
            	// Action( i+":"+j+":"+Squares.BOTTOM ) 
            	// draws bottom border of square (i,j)
                System.out.println("AG: "+color+" "+i+":"+j+":"+move);
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
    
    public TreeNode(int[][][] board) {
        this.board = board;
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