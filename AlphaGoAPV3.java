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
import java.util.Iterator;
import java.util.Stack;

import speco.array.Array;
import uniltiranyu.Action;
import uniltiranyu.AgentProgram;
import uniltiranyu.Percept;

/**
 *
 * @author AndresDiaz
 */

public class AlphaGoAPV3 implements AgentProgram {
    // Player's color
    protected String color;
    // May use any structure as memory..
    
    /**
     * Creates a Dummy agent for playing squares
     * @param color Player's color
     */
    
    public AlphaGoAPV3( String color ){
        this.color = color;        
    }
    
    public Board copyBoard(Board board, Percept p, int opc) {
        /*
        Realiza una copia profunda de un Board
          opc 1 -> trabaja con Percept p
          opc 2 -> trabaja con Board board
        */
        Board bcopy;
        
        if ( opc == 1 ) { //Percept p
            
            boolean isWhite = p.get(Squares.TURN).equals(Squares.WHITE);
            int size = Integer.parseInt((String)p.get(Squares.SIZE));
            bcopy = new Board(size);
            
            for ( int i = 0; i < size; i++ ) {
                for ( int j = 0; j < size; j++ ) {
                    if(((String)p.get(i+":"+j)).equals(Squares.SPACE)) {
                        bcopy.values[i][j] |= 0;
                    }
                    else if(((String)p.get(i+":"+j)).equals(Squares.BLACK)) {
                        bcopy.values[i][j] |= Board.BLACK;
                    }
                    else {
                        bcopy.values[i][j] |= Board.WHITE;
                    }
                        
                    if(((String)p.get(i+":"+j+":"+Squares.LEFT)).equals(Squares.TRUE))
                        bcopy.values[i][j] |= Board.LEFT;
                    if(((String)p.get(i+":"+j+":"+Squares.TOP)).equals(Squares.TRUE))
                        bcopy.values[i][j] |= Board.TOP;
                    if(((String)p.get(i+":"+j+":"+Squares.BOTTOM)).equals(Squares.TRUE))
                        bcopy.values[i][j] |= Board.BOTTOM;
                    if(((String)p.get(i+":"+j+":"+Squares.RIGHT)).equals(Squares.TRUE))
                        bcopy.values[i][j] |= Board.RIGHT;
                     
                    
                }
            }
            
        } else { // Board board
            
            int size = board.values.length;
            bcopy = new Board(size);
            
            for ( int i = 0; i < size; i++ ) {
                for ( int j = 0; j < size; j++ ) {
                    bcopy.values[i][j] = board.values[i][j];
                }
            }
        }
        
        return bcopy;
    }
    
    public TreeNode createGameTreeRoot(Percept p) {
        /**
        * Crea la raiz del Arbol del juego
        * @param p Percept
        */
        boolean maxPlayer = p.get(Squares.TURN).equals(color);
        Board board = copyBoard(null, p, 1);
        int[] move = {0,0,0,0};
        TreeNode rootNode = new TreeNode(board, move, maxPlayer, 0);
        return rootNode;
    }
    
    public ArrayList<int[]> possibleMovements(Board rootBoard, boolean playerColor){
        /**
        * @param rootBoard board 
        * @param playerColor Player's color
        * return all possible movements
        */
        
        ArrayList<int[]> possibleMovements = new ArrayList<>();
        
        for ( int row = 0; row < rootBoard.values.length; row++ ) {
            for ( int col = 0; col < rootBoard.values.length; col++ ) {
                //LEFT:1, TOP:2, RIGTH:4, BOTTOM:8, WHITE:16, BLACK:32
                // Comento izquierda y arrbia asumiendo que esas posiciones 
                // ya están revisadas
                if((rootBoard.values[row][col] & Board.RIGHT)!=Board.RIGHT) {
                    int[] posMove = new int[4];
                    posMove[0] = (playerColor)? 1:0;
                    posMove[1] = row;
                    posMove[2] = col;
                    posMove[3] = Board.RIGHT; 
                    possibleMovements.add(posMove);
                }
                if((rootBoard.values[row][col] & Board.BOTTOM)!=Board.BOTTOM) {
                    int[] posMove = new int[4];
                    posMove[0] = (playerColor)? 1:0;
                    posMove[1] = row;
                    posMove[2] = col;
                    posMove[3] = Board.BOTTOM;
                    possibleMovements.add(posMove);
                }
            }
        }
        
        return possibleMovements;
    }
    
    public boolean whoPlay(boolean maxPlayer) {
        if(maxPlayer) {
            if(color.equals(Squares.WHITE)) {
                return true;
            } else {
                return false;
            }
        } else {
            if(color.equals(Squares.WHITE)){
                return false;
            } else {
                return true;
            }
        }
    }
    
    public TreeNode createGameTree(TreeNode root, int maxDepth, int size){
        Board rootBoard = root.board;
        boolean rootMaxPlayer = root.maxPlayer;
        int rootDepth = root.depth;
        
        if (rootDepth >= maxDepth) {
            return root;
        }
        
        boolean isWhite = whoPlay(rootMaxPlayer);    
        
        ArrayList<int[]> possibleMovements = possibleMovements(rootBoard, isWhite);
        if ( possibleMovements.size() == 0) {
            return root;
        }
        
        int cont = 0;
                
        for( int i = 0; i < possibleMovements.size(); i++) {
            int[] pm = possibleMovements.get(i);
            // create TreeNodes
            Board boardCopy = copyBoard(rootBoard, null, 2);
            if(boardCopy.play(isWhite,pm[1],pm[2],pm[3])) {
                root.addChild(boardCopy, pm, !rootMaxPlayer, rootDepth+1);
                TreeNode child = root.children.get(cont);
                child = createGameTree(child, maxDepth,size);
                cont ++;
            }
        }
        
        return root;
    }
    
    // Heuristic Function
    public int evaluate(TreeNode node) {
        Board board = node.board;
        int scoreWhite = board.white_count();
        int scoreBlack = board.black_count();
        int score = scoreWhite - scoreBlack;
        if( color.equals(Squares.WHITE) )
            score = scoreWhite - scoreBlack;
        else 
            score = scoreBlack - scoreWhite;
        return score;
    }
    
    public int[] bestMove(TreeNode node, int depth, int alpha, int beta, 
            boolean maxPlayer) {
        int[] move = new int[4];
        int bestScore = -2147483648;
        boolean isWhite = whoPlay(maxPlayer);
        ArrayList<int[]> possibleMovements = possibleMovements(node.board, isWhite);
        
        for(int[] posMove : possibleMovements) {
            Board childBoard = copyBoard(node.board, null, 2);
            if(childBoard.play(isWhite,posMove[1],posMove[2],posMove[3])) {
                TreeNode childNode = node.addChild(childBoard, posMove, 
                            maxPlayer, node.depth + 1);
                int score = decisionAlphaBeta(childNode, depth, alpha, beta, maxPlayer);
                if(score > bestScore) {
                    bestScore = score;
                    move = childNode.move;
                }
            }
        }
            
        return move;
    }
    
    // Alpha Beta Algorithm  
    public int decisionAlphaBeta(TreeNode node, int depth, int alpha, 
            int beta, boolean maxPlayer) {
        //Stack<int[]> moveStack = oldStack;
        Board rootBoard = node.board;
        int currentDepth = node.depth;
        int value;
        
        boolean isWhite = whoPlay(maxPlayer);
        ArrayList<int[]> possibleMovements = possibleMovements(rootBoard, isWhite);
        
        if (depth == 0 || possibleMovements.size() == 0) {
            value = evaluate(node);
            return value;
        }
        else if (maxPlayer) {
            value = -214748364; // - infinite
            for (int[] posMove : possibleMovements) {
                Board childBoard = copyBoard(rootBoard, null, 2);
                if(childBoard.play(isWhite,posMove[1],posMove[2],posMove[3])) {
                    TreeNode childNode = node.addChild(childBoard, posMove, 
                            maxPlayer, currentDepth + 1);
                    int oldValue = value;
                    int newValue = decisionAlphaBeta(childNode, depth-1, alpha, beta, false);
                    value = max(oldValue, newValue);
                    alpha = max(alpha, newValue);
                    if (alpha >= beta) {
                        break;
                    }
                }
            }
            return value;
        }
        
        else {
            value = 214748364; // + infinite
            for (int[] posMove : possibleMovements) {
                Board childBoard = copyBoard(rootBoard, null, 2);
                if(childBoard.play(isWhite,posMove[1],posMove[2],posMove[3])) {
                    TreeNode childNode = node.addChild(childBoard, posMove, 
                            maxPlayer, currentDepth + 1);
                    int oldValue = value;
                    int newValue = decisionAlphaBeta(childNode, depth-1, alpha, beta, false);
                    value = min(oldValue, newValue);
                    beta = min(beta, newValue);
                    if (beta <= alpha) {
                        break;
                    }
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
    public int[] getAdjacent(Percept p, int row, int col, ArrayList<String> pMovements) {
        
        int[] adjacents = new int[4];
        int size = Integer.parseInt((String)p.get(Squares.SIZE));
        int linesLeft = 5, linesBottom = 5, linesRight = 5, linesTop = 5;
        
        for (String pm: pMovements) {
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
    
    public TreeNode prueba(Percept p) {
        System.out.println("PRUEBA ");
        TreeNode root = createGameTreeRoot(p);
        int maxDepth = 3;
        int size = Integer.parseInt((String)p.get(Squares.SIZE));
        root = createGameTree(root, maxDepth, size);
        System.out.println("ROOT"+root.depth);
        System.out.println(root.board.toString());
        StringBuilder buffer = new StringBuilder(100);
        String prefix = "|"; 
        String childrenPrefix = "-";
        System.out.println(root.printTree(buffer, prefix, childrenPrefix));
        
        //int[] av = new int[5];
        //av = decisionAlphaBeta(root, 1, -2147483646, 2147483647,true);
        
        //System.out.println("ACTIONVALUE "+av[0]+" "+av[1]+" "+av[2]+" "+av[3]+" "+av[4]);
        return root;
        
    }
    
    @Override
    /**
     * Computes an action to be carried by the agent according to the perception
     * of the environment
     * @param p Perception received by the agent program     * 
     */
    public Action compute(Percept p) {
        // Determines if it is the agents turn
        //TreeNode r = prueba(p);
        //long time = (long)(3000);
        //try{
        //   Thread.sleep(time);
        //}catch(Exception e){}
        if(Integer.parseInt((String)p.get(Squares.SIZE)) <= 5) {
            if (p.get(Squares.TURN).equals(color)) {
                
                int size = Integer.parseInt((String)p.get(Squares.SIZE));
                int b = 2*size*(size-1);
                int O = 7000000;
                int m = (int) (Math.log(O)/Math.log(b));
                int[] ij = new int[2];
                String v = new String();
                TreeNode root = createGameTreeRoot(p);
                int maxDepth = m;
                int[] av;
                System.out.println("Maximum Depth : " + maxDepth);
                av = bestMove(root, 4, -2147483646, 2147483647,true);
                System.out.println(color + " " + av[1] + av[2] + av[3]);
                
                ij[0] = av[1];
                ij[1] = av[2];
                switch(av[3]) {
                    case 1:
                        v = Squares.LEFT;
                        break;
                    case 2:
                        v = Squares.TOP;
                        break;
                    case 4: 
                        v = Squares.RIGHT;
                        break;
                    case 8:
                        v = Squares.BOTTOM;
                        break;
                    default:
                        v = Squares.PASS;
                        System.out.println("No quiero que pase :'c");
                        break;
                }
                /*System.out.println(color + "FASE 2");
                StringBuilder buffer = new StringBuilder(100);
                String prefix = "|"; 
                String childrenPrefix = "-";
                System.out.println(root.printTree(buffer, prefix, childrenPrefix));*/
                try{
                String move = v;
                int i = ij[0];
                int j = ij[1];
                // Action( i+":"+j+":"+Squares.BOTTOM ) 
                // draws bottom border of square (i,j)
                System.out.println("AG MINIMAX: "+color+" "+i+":"+j+":"+move);
            return new Action( i+":"+j+":"+move);
            }catch(Exception e){}
            }
        }
    
        /*else if ( p.get(Squares.TURN).equals(color) ){
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
                    System.out.println(color + " Count: "+count+"| pm:"+pm+"| i:"+i+"| j:"+j);
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
                                v = Squares.PASS;
                                System.out.println("NO POSIBLE MOVEMENTS");
                                break;
                        }
                        System.out.println(pm);

                        ij[0] = i;
                        ij[1] = j;
                        pos = -1;
                        break inicio;
                    }
                    //else {
                        
                        //ij[0] = -1;
                        //ij[1] = -1;
                        //v = "fase2";
                    //}
                }
                
                boolean fase2 = (v.equals(Squares.PASS))? false:true;
                
                if(fase2) {
                    // FASE 2
                    TreeNode root = createGameTreeRoot(p);
                    int maxDepth = 3;
                    root = createGameTree(root, maxDepth, size);
                    int[] av;
                    av = bestMove(root, 1, -2147483646, 2147483647,true);
                    System.out.println(color + " " + av[1] + av[2] + av[3]);
                    ij[0] = av[1];
                    ij[1] = av[2];
                    switch(av[3]) {
                        case 1:
                            v = Squares.LEFT;
                            break;
                        case 2:
                            v = Squares.TOP;
                            break;
                        case 4: 
                            v = Squares.RIGHT;
                            break;
                        case 8:
                            v = Squares.BOTTOM;
                            break;
                        default:
                            v = Squares.PASS;
                            System.out.println("No quiero que pase :'c");
                            break;
                    }
                    System.out.println(color + "FASE 2");
                    pos = -1;
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
        }*/
        return new Action(Squares.PASS);
    }

    @Override
    public void init() {
    }
}

