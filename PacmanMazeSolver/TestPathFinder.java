package assignment07;

import java.io.FileNotFoundException;
import java.nio.file.Path;

public class TestPathFinder {

  public static void main(String[] args) {

    /*
     * The below code assumes you have a file "tinyMaze.txt" in your project folder.
     * If PathFinder.solveMaze is implemented, it will create a file "tinyMazeOutput.txt" in your project folder.
     * 
     * REMEMBER - You have to refresh your project to see the output file in your package explorer. 
     * You are still required to make JUnit tests...just lookin' at text files ain't gonna fly. 
     */
    PathFinder.solveMaze("/Users/bkirkham/myGithubRepo/cs6012/src/assignment07/mazes/turn.txt", "turnOutput.txt");

    //The test below will count the length of two solutions and print it out for you to compare.  but the code currently commented out
    //in the graph constructor allowing for '.' to be a value needs to be put back in for the test to work.
//    try {
//      PathFinder.countLength("/Users/bkirkham/myGithubRepo/cs6012/src/assignment07/mazes/randomMazeSol.txt");
//    }
//    catch (FileNotFoundException e){
//      System.out.println("error file not found");
//    }
  }
}

