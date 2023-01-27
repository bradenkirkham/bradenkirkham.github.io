import java.io.IOException;
import java.net.*;

public class DNSServer {
    DatagramSocket mainSocket; // socket for user
    byte[] receivedData; // array for data received from user
    DatagramPacket mainPacket; // packet from user
    DNSMessage receivedMessage; // message to store decoded user packet.

    DatagramSocket googleSocket; // above but for google.
    byte[] googleData;
    DatagramPacket googlePacket;
    DNSMessage googleMessage;

    public static void main(String[] args) { // main, just declares a new DNSserver and then calls runserver.
        DNSServer server = new DNSServer();
        try {
            server.runServer();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void runServer() throws IOException {
        //initiate sockets
        mainSocket = new DatagramSocket(8053); // we listen on port 8053 for users
        googleSocket = new DatagramSocket(53); // listen on port 53 for google.
        while (true) { // keep the server on until i manually close it.

            //initiate array for received data and use it to initialize the packet.
            receivedData = new byte[512];
            mainPacket = new DatagramPacket(receivedData, receivedData.length);

            mainSocket.receive(mainPacket); // reads packet in from socket into mainPacket
            byte[] messageBytes = mainPacket.getData();// stores the data from the packet in a new array.

            Inet4Address clientAddress = (Inet4Address) mainPacket.getAddress(); //not sure exactly how this works but it returns the ip address of the client
            int clientPort = mainPacket.getPort(); // returns the port number of the client.

            receivedMessage = DNSMessage.decodeMessage(messageBytes); // decodes the byte array and returns a DNSMessage.

            if (DNSCache.queryRecord(receivedMessage.questionArray.get(0))){ // if we have the answer cached then send it to client
                System.out.println("sending to client");
                DNSMessage returnMessage = DNSMessage.buildResponse(receivedMessage, DNSCache.getAnswer(receivedMessage.questionArray.get(0)));

                byte[] sendBytes = returnMessage.toBytes();

                DatagramPacket responsePacket = new DatagramPacket(sendBytes, sendBytes.length, clientAddress, clientPort); // constructs packet that we send w/ bytes length, and address and port of recipient
                mainSocket.send(responsePacket);
                System.out.println("sent to client");
            }

            else { // if we don't have the answer cached, do the same as above and send it to google.
                googleData = new byte[512]; // i think this is never used...
                int queryLength = receivedData.length; // received data at this point should have the user message in it.

                Inet4Address google = (Inet4Address) Inet4Address.getByName("8.8.8.8"); // this is googles ip address
                googlePacket = new DatagramPacket(receivedData, queryLength, google, 53); // new packet to send off to google

                googleSocket.send(googlePacket); // send to google with client message

                byte[] googlemessageBytes = new byte[512]; // declare array for google
                DatagramPacket receivedPacket = new DatagramPacket(googlemessageBytes, googlemessageBytes.length); // new packet from google
                googleSocket.receive(receivedPacket); // receive from google
                byte[] googleForwardMessage = receivedPacket.getData(); // fill new byte array with data from packet (this was probably supposed to be googledata...
                googleMessage = DNSMessage.decodeMessage(googleForwardMessage); // decode the google packet to store the answer in the cache

                DatagramPacket forwardingPacket = new DatagramPacket(googleForwardMessage, googleForwardMessage.length, clientAddress, clientPort); // packet to forward to client

                if (googleMessage.messageHeader.ANCOUNT > 0 && (!DNSCache.insertRecord(googleMessage.questionArray.get(0), googleMessage.answerArray.get(0)))){ // if there is an answer and it fails to be updated, exit.
                    System.exit(1);
                }

                mainSocket.send(forwardingPacket); // forward the packet from google to client.
            }
        }
    }
}
