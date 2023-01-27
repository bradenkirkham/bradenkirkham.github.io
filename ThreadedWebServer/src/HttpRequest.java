import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class HttpRequest {

    public Map<String, String> parseRequest(Scanner myScanner) { // parses the user http request.
//        System.out.println("in parseRequest");

        String command = myScanner.next(); // reads line into variable and moves to the next line.
        String filename = myScanner.next();// reads line into variable and moves to the next line, this is the useful bit, the rest is unused.
        String protocol = myScanner.next();// reads line into variable and moves to the next line.

        myScanner.nextLine(); // moves to the next line.

        Map<String, String> headerFlags = new HashMap<>(); // sets a string string map to store the rest of header.
        headerFlags.put("fileName", filename); // adds the filename to the header.

        while (myScanner.hasNextLine()){ // this while loop reads the header into the map.
//            System.out.println("in while loop");
            String nextLine = myScanner.nextLine(); //reads the next line.

            if (nextLine.isEmpty()){ // breaks from loop if a line is empty.
//                System.out.println(nextLine.isEmpty());
                break;
            }

            String[] temp = nextLine.split(": ", 2); // splits the line in two.
            String key = temp[0]; // stores the first part of the split as the key.
//            System.out.println(key);
            String value = temp[1]; // stores the second part as the value.
//            System.out.println(key + ": " + value);
            headerFlags.put(key, value); // adds key value pair into map
        }

        return headerFlags; // returns the map.
    }

    public String getRealFilename(String fileName){ // a function just to convert the filename incase of it being a / meaning index.

        if (fileName.equals("/")) {
            fileName = Server.indexFilePath; // if it is a / it is set to the index.
        }

        else {
            fileName = "./resource" + fileName; // otherwise the path is set. (this would be useful if there was more than one page, like say an error page...
        }

        System.out.println("file is: " + fileName + " " + Thread.currentThread().getId());

        return fileName; // returns the fileName.
    }
}


