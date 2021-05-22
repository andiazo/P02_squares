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
    
    public TreeNode createGameTreeRoot(Percept p) {
        
        boolean maxPlayer = p.get(Squares.TURN).equals(color);
        // Build de board
        boolean isWhite = p.get(Squares.TURN).equals(Squares.WHITE);
        int size = Integer.parseInt((String)p.get(Squares.SIZE));
        Board board = new Board(size);
        
        for ( int i = 0; i < size; i++ ) {
            for ( int j = 0; j < size; j++ ) {       
                if(((String)p.get(i+":"+j+":"+Squares.LEFT)).equals(Squares.TRUE))
                    board.play(isWhite, i, j, Board.LEFT);             
                if(((String)p.get(i+":"+j+":"+Squares.TOP)).equals(Squares.TRUE))
                    board.play(isWhite, i, j, Board.TOP);
                if(((String)p.get(i+":"+j+":"+Squares.BOTTOM)).equals(Squares.TRUE))
                    board.play(isWhite, i, j, Board.BOTTOM);
                if(((String)p.get(i+":"+j+":"+Squares.RIGHT)).equals(Squares.TRUE))
                    board.play(isWhite, i, j, Board.RIGHT);
            }
        }
        TreeNode node = new TreeNode(board, maxPlayer, 0);
        return node;
    }
    
    public TreeNode createGameTree(TreeNode root, int maxDepth, int size) {
        Board rootBoard = root.board;
        boolean rootMaxPlayer = root.maxPlayer;
        int currentDepth = root.depth;
        if (currentDepth >= maxDepth) {
            return root;
        }
        int ccolor;
        if(rootMaxPlayer) {
            if(Squares.WHITE.equals(color)){
                ccolor = 0;
            }
            else {
                ccolor = 1;
            }
        }
        else {
            if(Squares.WHITE.equals(color)){
                ccolor = 1;
            }
            else {
                ccolor = 0;
            }
        }
        
        int[] mBoard = new int[4]; // color, row, col, side
        Board cBoard = new Board(size);
        
        
        ArrayList<int[]> possibleMovements = new ArrayList<>();
        int[] posMove = new int[4];
        for (int row = 0; row < rootBoard.values.length; row++) {
            for (int col = 0; col < rootBoard.values.length; col++) {
                //LEFT:1, TOP:2, RIGTH:4, BOTTOM:8, WHITE:16, BLACK:32
                posMove[0] = ccolor;
                posMove[1] = row;
                posMove[2] = col;
                if((rootBoard.values[row][col] & Board.LEFT)==Board.LEFT) {
                    posMove[2] = Board.LEFT; // LEFT
                    possibleMovements.add(posMove);
                }
                if((rootBoard.values[row][col] & Board.TOP)==Board.TOP) {
                    posMove[2] = Board.TOP; // TOP
                    possibleMovements.add(posMove);
                }
                if((rootBoard.values[row][col] & Board.RIGHT)==Board.RIGHT) {
                    posMove[2] = Board.RIGHT; // RIGTH
                    possibleMovements.add(posMove);
                }
                if((rootBoard.values[row][col] & Board.BOTTOM)==Board.BOTTOM) {
                    posMove[2] = Board.BOTTOM; // BOTTOM
                    possibleMovements.add(posMove);
                }
            }
        }
        
        if ( possibleMovements.size() == 0) {
            return root;
        }
        
        boolean isWhite = (ccolor==1)? true:false;
        int cont = 0;
        for( int[] pm : possibleMovements) {
            // create TreeNodes
            Board boardCopy = cBoard;
            boardCopy.play(isWhite,pm[1],pm[2],pm[3]);
            System.out.println("PLAY"+pm[1]+pm[2]+pm[3]);
            System.out.println(boardCopy.toString());
            root.addChild(boardCopy, pm, true, currentDepth+1);
            TreeNode child = root.children.get(cont);
            cont ++;
        }
        return root;
    }
    
    // Heuristic Function
    public int evaluate(TreeNode node) {
        Board board = node.board;
        int scoreWhite = board.white_count();
        int scoreBlack = board.black_count();
        int score = scoreWhite-scoreBlack;
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
        }
        if(((String)p.get(row+":"+col+":"+Squares.TOP)).equals(Squares.FALSE)) {
            possibleMovements.add(Squares.TOP);
        }
        if(((String)p.get(row+":"+col+":"+Squares.BOTTOM)).equals(Squares.FALSE)) {
            possibleMovements.add(Squares.BOTTOM);
        }
        if(((String)p.get(row+":"+col+":"+Squares.RIGHT)).equals(Squares.FALSE)) {
            possibleMovements.add(Squares.RIGHT);
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
    
    public void prueba(Percept p) {
        TreeNode root = createGameTreeRoot(p);
        int maxDepth = 2;
        int size = Integer.parseInt((String)p.get(Squares.SIZE));
        root = createGameTree(root, maxDepth, size);
        System.out.println("Root: "+root.depth);
        System.out.println(root.board.toString());
        System.out.println("Children Size: "+root.children.size());
        for(TreeNode r : root.children) {
            System.out.println("Child:" + r.depth);
            System.out.println(r.board.toString());
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
        prueba(p);
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
                }
            }
            
            if (v == null) {
                    v = "";
                    int i1=0,j1=0;
                    while(v==""){
                        i1 = (int)(size*Math.random());
                        j1 = (int)(size*Math.random());
                        pm = linesOnBox(p, i1, j1);
                        if(pm.size()>0){
                            v=pm.get(0);
                        }
                    }
                    ij[0] = i1;
                    ij[1] = j1;
                    System.out.println("Si es ESTE");
                    pos = -1; 
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
    Board board;
    int[] move;
    int depth;
    boolean maxPlayer;
    TreeNode parent;
    List<TreeNode> children;
    
    public TreeNode(Board board, int[] move, boolean maxPlayer, int depth) {
        this.depth = depth;
        this.board = board;
        this.move = move;
        this.children = new LinkedList<TreeNode>();
    }
    
    public TreeNode(Board board, boolean maxPlayer, int depth) {
        this.depth = depth;
        this.board = board;
        this.maxPlayer = maxPlayer;
        this.children = new LinkedList<TreeNode>();
    }

    public TreeNode addChild(Board cBoard, int[] mBoard, boolean maxPlayer, int depth) {
        TreeNode childNode = new TreeNode(cBoard, mBoard, maxPlayer, depth);
        childNode.parent = this;
        this.children.add(childNode);
        return childNode;
    }

    // other features ..
}