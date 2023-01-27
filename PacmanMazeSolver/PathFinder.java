package assignment07;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class PathFinder {

    static Graph newGraph;

    public static void solveMaze(String inputFile, String outputFile){ //maze solving driver function


        try {
            newGraph = new Graph(inputFile); // turns input file into a Graph.
        }
        catch (FileNotFoundException e){
            System.out.println("file not found"); // if input file can not be found throws exception.
        }

        breadthFirstSearch(newGraph.startNode, newGraph.endNode); // calls bredthFirstSearch to solve and mark graph with solution

        PrintWriter outputStream; // new PrintWriter to write out to a file.
        try {
            outputStream = new PrintWriter(new FileWriter(outputFile)); // indicates where we want the file to be written out to
            newGraph.printGraph(outputStream); // calls helper function to print out the Graph.
        }
        catch (IOException e){
            System.out.println(e); // cathes input output exception.
        }



    }

    public static void breadthFirstSearch (Node startNode, Node goalNode){ // helper function to run breadthFirstSearch.
        startNode.wasVisited = true; // marks the starting node as visited so that it won't be visited again
        Queue<Node> searchList = new LinkedList<>(); // starts a queue to store nodes in as we search
        searchList.add(startNode);

        while (!searchList.isEmpty()){ // continues the while loop while the queue isn't empty.  It should cycle through every node.
            Node current = searchList.remove(); // removes the next node in the queue
            if (current == goalNode){ // base case for if we find the goalNode
                Node pathMarker = goalNode.pathNode; //back tracking to mark the solution path
                while (pathMarker.pathNode != null){ //cycles through each pathNode until it reaches start which should have a null pathNode
                    pathMarker.value = '.'; // marks the value of the path node as '.'
                    pathMarker = pathMarker.pathNode; // sets pathMarker to the next pathNode.
                }
                return; // leaves the function
            }

            for (Node n : current.neighbors){ //for each loop cycling through all the neighbors of the current node.
                if (!n.wasVisited){ //checks if it was visited or not.  this prevents us from revisiting nodes.
                    if (n.value == 'X'){ // ignores 'X' marking walls that can't be part of the solution.
                        continue;
                    }
                    else {
                        n.wasVisited = true; //marks wasVisited as true so we don't go back to the node
                        n.pathNode = current; // sets the pathNode for the node as current.
                        searchList.add(n); // adds the node to the search queue.
                    }
                }
            }
        }
    }

    public static void countLength (String inputFile) throws FileNotFoundException { // test code to count the length of solutions.
        Graph newGraph = new Graph(inputFile); //turns the solved file into a graph.

        int counts = 0; // tracks the number of '.' characters that mark the solution path.
        for (int i = 0; i < newGraph.height; i++){ // nested for each loops go through each node in the graph and check if it's value is a '.'
            for (int j = 0; j < newGraph.width; j++) {
                if (newGraph.maze[i][j].value == '.') {
                    counts++; // if a '.' is found increments counts.
                }
            }
        }
        System.out.println(counts); // print the value of counts to the consol.

    }
}
