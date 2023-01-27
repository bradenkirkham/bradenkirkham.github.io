package assignment07;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Graph {

    Node[][] maze; // underlying data structure of the Graph is a matrix of Nodes.
    int height; // height and width of the matrix matching the height and width of the maze
    int width;
    Node startNode; // node to keep track of the start position.
    Node endNode; // node to keep track of the goal.

    public int getHeight(){
        return height;
    }

    public int getWidth(){
        return width;
    }

    public Graph (String inputFile) throws FileNotFoundException { // constructor for the graph requires an input file string.
        File newFile = new File(inputFile); // new File variable for the input file.
        Scanner input = new Scanner(newFile); // scanner to read in the file.
        height = input.nextInt(); // Grab the height and width first
//        System.out.println(height);
        width = input.nextInt();
//        System.out.println(width);

        maze = new Node[height][width]; // initialize the matrix

        input.nextLine(); // skips a line. not sure why this is needed to be honest, but it doesn't work without it...

        String line = input.nextLine(); //String variable to hold the next line of the text document
//        System.out.println(line);
        int row = 0; // counter to keep track of the rows.

        while (line != null){ // while loop that keeps going until line == null meaning the end of the document.
            for (int column = 0; column < line.length(); column++){ // for loop cycling through the columns.
                if (line.charAt(column) == 'X'){ // each if statement checks the value of the character at each index position.
                    maze[row][column] = new Node(row, column, 'X');
                }
                else if (line.charAt(column) == 'S'){
                    startNode = new Node(row, column, 'S'); // special case for S. we set it as the startNode and then set the maze position equal to startNode
                    maze[row][column] = startNode;

                }
                else if (line.charAt(column) == 'G'){
                    endNode = new Node(row, column, 'G'); // same case for G as with S but for the endNode.
                    maze[row][column] = endNode;

                }
                else if (line.charAt(column) == ' '){
                    maze[row][column] = new Node(row, column, ' ');
                }
//                else if (line.charAt(column) == '.'){ // this line of code is needed for checking the solution length of solved mazes.
//                    maze[row][column] = new Node(row, column, '.');
//                }

            }
            row ++; // increment row.
            if (input.hasNextLine() == false){ // manually setting line to null if there is no next line.
                line = null;
                continue;
            }
            else { // if there is a next line set it and keep going.
                line = input.nextLine();
//                System.out.println(line);
            }
        }
        for (int i = 1; i < height - 1; i++){ // nested for loop cycling through each node and setting the neighbors.
            for (int j = 1; j < width - 1; j++){ // this is designed to ignore the edges of the maze and start in position 1, 1.
                maze[i][j].setNeighbors( maze[i + 1][j] ); // each line sets either the up, down, left, or right neighbor.

                maze[i][j].setNeighbors( maze[i - 1][j] );

                maze[i][j].setNeighbors( maze[i][j + 1] );

                maze[i][j].setNeighbors( maze[i][j - 1] );
            }
        }
    }

    public void printGraph (PrintWriter writeOut){ // helper function for printing the graph to file. takes a PrintWriter variable.
        writeOut.println(height + " " + width); // Firsts prints out the height and width at the top of the maze.

        for (int i = 0; i < height; i++){ //nested for loops cycling through each node.
            for (int j = 0; j < width; j++){ // this one does not ignore the edges and starts at 0, 0 instead.
                writeOut.print(maze[i][j].value); // prints the value
            }
            writeOut.println(); // goes to the next line for the next row.
        }
        writeOut.flush(); // flushes the output stream
        writeOut.close(); // closes the output stream
    }
}
