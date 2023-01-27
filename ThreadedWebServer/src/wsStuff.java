import java.io.*;
import java.nio.charset.StandardCharsets;

public class wsStuff {

    public static String decodeMessage(DataInputStream userInput) throws Exception { //interprets the header and decodes the message
        System.out.println("in decodeMessage");
        byte[] firstTwoBytes = userInput.readNBytes(2); //adds the first two bytes of the header to the byte array.
        int opcode = firstTwoBytes[0] & 0x0f; //uses mask on first two bytes to  get the opcode

        if (opcode == 8) { //if the opcode is 8 that means exit program.
            throw new Exception("Exit");
        }

        boolean isMasked = ((firstTwoBytes[1] & 0x80) != 0); //sets boolean of ismasked (should always be true...).
        int lengthGuess = firstTwoBytes[1] & 0x7F; // gets the first length statement.
        int trueLength = 0; // initializes a variable to hold the true length.

        if (lengthGuess <= 125) {
            trueLength = lengthGuess; // if the length number is 125 or less than that is the true length.
        } else if (lengthGuess == 126) {
            trueLength = userInput.readUnsignedShort(); // if it is 126 then we read the next two bytes as the length
        } else if (lengthGuess == 127) {
            trueLength = (int) userInput.readLong(); // if it is 127 then we read the next 8 bytes as the length.
        }

        if (isMasked == true) { // if masked == true which it always should...
            byte[] maskedBytes = userInput.readNBytes(4); // reads in the mask
            byte[] ENCODED = userInput.readNBytes(trueLength); // reads in the rest of the array.
            var DECODED = ""; // sets a variable to store the coded string
            for (var i = 0; i < ENCODED.length; i++) { // for loop to decode the string.
                DECODED += (char) (ENCODED[i] ^ maskedBytes[i % 4]); //fromula to use the mask to decode the rest of the message.
//                ENCODED[i] ^= maskedBytes[i % 4];
            }
//            String resultMessage = new String(ENCODED);
            System.out.println(DECODED);
            System.out.println("leaving decode message");
            return DECODED; // returns the decoded message.
        } else {
            throw new Exception("unmasked");
        }
    }
}