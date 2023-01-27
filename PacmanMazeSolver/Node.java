package assignment07;

import java.util.LinkedList;

public class Node {

    public Node pathNode; //Node variable to help keep track of the solution path
    char value;
    int i; // i and j are the location of the node in the graph
    int j;
    LinkedList<Node> neighbors; // a list of all neighboring nodes.
    boolean wasVisited = false; // a boolean to keep track of whether the node has been visited or not.

    public Node(int i, int j, char value){ // constructor of Node
        this.i = i;
        this.j = j;
        this.value = value;
        neighbors = new LinkedList<>();
    }

    public void setNeighbors(Node newNeighbor){ // helper function for setting neighbors.
        neighbors.add(newNeighbor);
    }
}
