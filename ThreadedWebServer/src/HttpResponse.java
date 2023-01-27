import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;
import java.util.Scanner;

public class HttpResponse {
    public void fileResponse(String fileName, PrintWriter myPrint, OutputStream writeOut) throws IOException { // writes out an http response.

//        System.out.println("in fileResponse " + Thread.currentThread().getId());
        byte[] webFile = Files.readAllBytes(Path.of(fileName)); //reads in all the bytes from the file
        myPrint.write("HTTP/1.1" + " 200  OK\r\n"); //creates the appropriate response header and writes it to the socket
        myPrint.write("Content-Type: text/html\r\n"); // sets the content type, needs more code to get the actual content type.
        File file = new File( fileName ); // sets a new file to the input filename.
//        System.out.println(fileName);
        myPrint.write("Content-length: " + file.length()  + "\r\n"); // writes out the content lenght.
//        System.out.println("len is " + webFile.length);

        myPrint.write("\r\n"); //writes a new line to show the end of the header
        myPrint.flush(); //flushes the p

        writeOut.write(webFile); // writes out the webfile
        writeOut.flush(); // flushes the writeout stream.
        System.out.println("writting http response finished");

    }

    public void errorPageResponse(String fileName, PrintWriter myPrint, OutputStream writeOut) throws IOException {

        System.out.println("entered 404"); //this is for if there is a 404 error, currently i don't have that page up, so this is pretty useless currently.

        byte[] webFile = Files.readAllBytes(Path.of(Server.errorFile));

        myPrint.write("HTTP/1.1" + " " + 404 + " Not found\n");
        myPrint.write("\n");
        myPrint.flush();

        writeOut.write(webFile);
        writeOut.flush();

    }

    public void wsResponse(Map<String, String> clientRequests, PrintWriter myPrint) throws NoSuchAlgorithmException {
        System.out.println("entered wsResponse");

        String requestKey = clientRequests.get("Sec-WebSocket-Key"); // grabs the websocket key from the map
        String responseKey = generateResponseKey(requestKey); // calles the generate response key function.

        String header = "HTTP/1.1 101 Switching Protocols\r\n"; // header response for websockets.

        myPrint.write(header); // writes out the header.
        myPrint.write("Upgrade: websocket\r\n"); // writes out upgrade response.
        myPrint.write("Connection: Upgrade\r\n"); // writes out connection response.

        myPrint.write("Sec-WebSocket-Accept: " + responseKey + "\r\n"); // rights out response key
        myPrint.write("\r\n"); // writes blank line.

        myPrint.flush(); // flushes.

    }
    public String generateResponseKey(String requestKey) throws NoSuchAlgorithmException {

        String provided = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"; // magic string.
        requestKey += provided; // addition of magic string to request key.
        MessageDigest md = MessageDigest.getInstance("SHA-1"); //not sure exactly what this does.

        byte[] hashed = md.digest( requestKey.getBytes() ); // changes it to bytes.
        String result = Base64.getEncoder().encodeToString( hashed ); // can't remember what this does either.

        return result;
    }
}