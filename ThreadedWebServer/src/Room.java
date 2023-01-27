import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Room {

    public static Map<String, Room> roomMap_ = new HashMap<>();
    public ArrayList<Socket> sockets_;
    public String name_;
    private ArrayList<String> sentMessages_ = new ArrayList<>(); // an array of strings for all previously sent messages, can be used to send to new users.

    public Room(String roomName){ //constructor for the room that sets an arraylist of sockets to hold users.
        name_ = roomName;
        sockets_ = new ArrayList<>();
    }

    public synchronized void sendMessage(String jSON) throws Exception { // function to send messages.
        if (sentMessages_ == null || !sentMessages_.contains(jSON)) { // checks if the message array is either empty, or contains the message.
            System.out.println("in if statement in sendMessage");
            sentMessages_.add(jSON); // adds the new message to the array.
            System.out.println(sentMessages_.get(0));

        }
        System.out.println("in sendMessage");

        for (Socket socket : sockets_) { // an array list to cycle through all clients and send the message.
            DataOutputStream writeOut = new DataOutputStream(socket.getOutputStream()); //creates a new data output stream attached to the socket.
            int messageLength = jSON.length(); // an int to hole the length of the message.

            byte opCode = (byte) 0x81; // holds the op code and fin code, saying to send a message and this is the final message in the set.
            writeOut.write(opCode); // writes out to the socket.

            if (messageLength <= 125){ //checks the message length
                writeOut.write(messageLength); //writes out the length if the message length is 125 or less.
            }

            else if (messageLength < Short.MAX_VALUE * 2){ //checks the message length.
                writeOut.write(126); // writes out 126
                writeOut.writeShort(messageLength); // then writes out the length.
            }

            else{
                writeOut.write(127); // in all other cases, writes out 127
                writeOut.writeLong(messageLength); // then writes out the message length.
            }

            writeOut.write(jSON.getBytes()); //writes out the rest of the message as a byte array.
            writeOut.flush(); //flush the writeout stream.
        }
    }

    public synchronized static void getRoom(String roomName, myRunnable userThread) throws Exception {
        if (roomMap_.containsKey(roomName)){ //checks if the array list of rooms contains the room
            Room tempRoom = roomMap_.get(roomName); //if yes sets a temporary pointer to that room
            tempRoom.sockets_.add(userThread.getSocket()); //adds the user thread to that room
            userThread.setRoom(tempRoom); // sets the room to the user thread.
            tempRoom.newUserJoin(userThread.getSocket()); // runs the new user join function so the new user gets all previous messages.

        }
        else {
            Room newRoom = new Room(roomName); // if it does not exist it creates a new room of roomName.
            newRoom.sockets_.add(userThread.getSocket()); // adds the user socket to the new room
            userThread.setRoom(newRoom); // sets the room to the user thread.
            roomMap_.put(roomName, newRoom); // adds the room to the map of rooms.
        }
    }
    public synchronized void newUserJoin(Socket userSocket) throws Exception {
        for (String string : sentMessages_) { //cycles through all stored messages.
            DataOutputStream writeOut = new DataOutputStream(userSocket.getOutputStream()); // creates a new data outputstream.
            int messageLength = string.length(); // explanation for the rest of the code can be found in sent message function, the only difference is that this only happens to one user.

            byte opCode = (byte) 0x81;
            writeOut.write(opCode);

            if (messageLength <= 125){
                writeOut.write(messageLength);
            }

            else if (messageLength < Short.MAX_VALUE * 2){
                writeOut.write(126);
                writeOut.writeShort(messageLength);
            }

            else{
                writeOut.write(127);
                writeOut.writeLong(messageLength);
            }

            writeOut.write(string.getBytes());
            writeOut.flush();
        }
    }
}
