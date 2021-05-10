/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uniltiranyu.examples.games.squares;

import java.util.ArrayList;
import java.util.Arrays;
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
    
    public Graph createGameTree() {
        Graph gameTree = new Graph();
        return gameTree;
    }
    
    // Who get more points function
    public int[] howPoints(Percept p) {
        int[] points = new int[2];
        return points;
    }
    
    // Heuristic Function
    public int evaluate(int node) {
        int score = 0;
        return score;
    }
    
    // Alpha Beta Algorithm
    public int decisionAlphaBeta() {
        return 0;
    }
    
    public int maxValue() {
        return 0;
    }
    
    public int minValue() {
        return 0;   
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

// Graph for the Game Tree
class Graph {
    private List<Node> nodes;
    
    public void addNode(Node node) {
        if (nodes == null) {
            nodes = new ArrayList<>();
        }
        nodes.add(node);
    }
 
    public List<Node> getNodes() {
        return nodes;
    }
 
    @Override
    public String toString() {
        return "Graph [ nodes = (" + nodes + ") ]";
    }
}
    
// Node
class Node {
    private int[][][] board;
    private boolean player;    
    private List<Edge> edges;
 
    public Node(int[][][] board) {
        this.board = board;
    }
 
    public int[][][] getBoard() {
        return board;
    }
 
    public void setBoard(int[][][] board) {
        this.board = board;
    }
 
    public List<Edge> getEdges() {
        return edges;
    }
 
    public void addEdge(Edge edge) {
        if (edges == null) {
            edges = new ArrayList<>();
        }
        edges.add(edge);
    }
    
    public void setPlayer(boolean player) {
        this.player = player;
    }
    
    public boolean getPlayer() {
        return player;
    }
    
    @Override
    public String toString() {
        return "\n \tNode [city=" + Arrays.toString(board) + ", edges=" + edges + "]";
    }
}

class Edge {
    private Node origin;
    private Node destination;
    private int[] move = new int[3]; //[0] -> i | [1] -> j | [2] -> position
 
    public Edge(Node origin, Node destination, int[] move) {
        this.origin = origin;
        this.destination = destination;
        this.move = move;
    }
 
    public Node getOrigin() {
        return origin;
    }
 
    public void setOrigin(Node origin) {
        this.origin = origin;
    }
 
    public Node getDestination() {
        return destination;
    }
 
    public void setDestination(Node destination) {
        this.destination = destination;
    }
 
    public int[] getDistance() {
        return move;
    }
 
    public void setDistance(int[] move) {
        this.move = move;
    }
 
    @Override
    public String toString() {
        return "\n Move: " + Arrays.toString(move) + "]";
    }
 
}