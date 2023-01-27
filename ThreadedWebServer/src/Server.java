import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static String indexFilePath = "./resource/index.html";
    public static String errorFile = "./resource/404error.html"; // haven't added this back yet.

//    public synchronized ArrayList<Thread> sockets = new ArrayList<>();


    public static void getNextClient() throws IOException {
        ServerSocket newServer = new ServerSocket(8080); //opens a socket on port 8080


        while (true) { //maintains open server.
            Socket newSocket = newServer.accept(); // listens for clients.

            myRunnable newRun = new myRunnable(newSocket); // starts a new runnable
            Thread newThread = new Thread(newRun); //creates a new thread with that runnable.
            newThread.start(); // starts the thread.
        }
    }


    public static void main(String[] args) throws IOException {
        getNextClient(); // starts the server.
    }
}