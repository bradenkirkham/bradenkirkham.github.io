import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Scanner;

public class myRunnable implements Runnable {

    private final Socket newSocket_;
    private boolean isWebSocket;
    private Room clientRoom_;


    public myRunnable (Socket mySocket){ //creates a socket
        newSocket_ = mySocket;
        isWebSocket = false; //sets boolian to false
    }

    public Socket getSocket(){ // getter function for socket variable
        return newSocket_;
    }

    public void setRoom(Room userRoom){ // setter function for setting the room
        clientRoom_ = userRoom;
    }

    public Room getRoom(){ // getter function for getting the room, could be useful if code was added in the future.
        return clientRoom_;
    }

    @Override
    public void run() {
        InputStream input = null; //creates null input stream.

        try {
            input = newSocket_.getInputStream(); // uses above declared input stream to get input from socket.
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scanner myScanner = new Scanner(input); // wraps input with scanner

        HttpRequest request = new HttpRequest(); // creates new HTTP request class.
        Map<String, String> clientRequests = request.parseRequest(myScanner); // creates string string map and fills it useing the parse request function of the httprequest that was made.
        String fileName = request.getRealFilename(clientRequests.get("fileName")); // gets the file name from the map and turns it to a useable name.
//        System.out.println(fileName);

        OutputStream writeOut = null; //creates a new output stream

        try {
            writeOut = newSocket_.getOutputStream(); //sets the output stream to write to the socket.
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        PrintWriter myPrint = new PrintWriter(writeOut); //creates a new print writer that writes to the output stream
        HttpResponse response = new HttpResponse();
//        System.out.println(clientRequests.get("Sec-WebSocket-Key"));
//        System.out.println(clientRequests.containsKey("Sec-WebSocket-Key"));

        if (clientRequests.containsKey("Sec-WebSocket-Key")) { // checks for unique string in header for web sockets.
            try {
                System.out.println("got to ws try");
                response.wsResponse(clientRequests, myPrint); // sends websocket response
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            while (true){ // while loop to continue listening while websocket is open.
                try {
                    isWebSocket = true; // sets websocket to true to continue while loop
                    DataInputStream userInput = new DataInputStream(input); // creates data input stream to read incoming websocket messages.
//                    DataOutputStream serverOutput = new DataOutputStream(writeOut); // creates data output stream to write out to the websocket.

                    String message = wsStuff.decodeMessage(userInput); // calls the decode message function and reads the decoded message into a string
                    String[] messageArray = message.split(" ", 2); // splits the message into user name and message
//        System.out.println(messageArray[0] + messageArray[1]);
                    if (messageArray[0].equals("join")){ //checks message code, if join does the following.
                        Room.getRoom(messageArray[1], this); // calls get room function on user thread.  creates new room if no room exists, joins existing room otherwise.

                    }
                    else { // what to do for any other message (other message codes could be added to run different functions for different messages.).
                        String jSON = "{\"user\":\"" + messageArray[0] + "\", \"message\":\"" + messageArray[1] + "\"}"; // used Veeder code to create JSON string
                        System.out.println(jSON);
                        clientRoom_.sendMessage(jSON); // calls the rooms send message function to send the JSON string.
                    }

                }
                catch (Exception e) { //exception breaks from while loop if user leaves.
                    break;
                }
            }
        }

        try {
            if (!isWebSocket && Files.exists(Path.of(fileName))) { //handles html requests
                response.fileResponse(fileName, myPrint, writeOut);
                newSocket_.close();
            } else if (!isWebSocket && !Files.exists(Path.of(fileName))){ //needs addition of error page to display.
                response.errorPageResponse(fileName, myPrint, writeOut);
                newSocket_.close();
            }
        }
        catch (IOException e) { // exits program if no file can be found.
            System.exit(1);
        }
    }
}
