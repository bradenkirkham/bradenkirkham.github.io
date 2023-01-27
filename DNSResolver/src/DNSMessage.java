import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DNSMessage { // holds most of the information for each message.
    DNSHeader messageHeader;
    ArrayList<DNSQuestion> questionArray = new ArrayList<>();
    ArrayList<DNSRecord> answerArray = new ArrayList<>();
    ArrayList<DNSRecord> authorityRecords = new ArrayList<>();
    ArrayList<DNSRecord> additionalRecords = new ArrayList<>();
    byte[] message;


    static DNSMessage decodeMessage(byte[] bytes) throws IOException {
        DNSMessage newMessage = new DNSMessage();
        newMessage.message = bytes; // hold the byte array in the message itself.

        InputStream readIn = new ByteArrayInputStream(bytes); // input stream of bytes byte[]
        newMessage.messageHeader = DNSHeader.decodeHeader(readIn); // decode the header.

        for (int i = 0; i < newMessage.messageHeader.QDCOUNT; i++) { // use corresponding helper functions to decode each piece cycling through if there are multiple of each piece.
            DNSQuestion newQuestion = DNSQuestion.decodeQuestion(readIn, newMessage);
            newMessage.questionArray.add(newQuestion);
        }

        for (int i = 0; i < newMessage.messageHeader.ANCOUNT; i++) {
            DNSRecord newRecord = DNSRecord.decodeRecord(readIn, newMessage);
            newMessage.answerArray.add(newRecord);
        }

        for (int i = 0; i < newMessage.messageHeader.NSCOUNT; i++) {
            DNSRecord newRecord = DNSRecord.decodeRecord(readIn, newMessage);
            newMessage.authorityRecords.add(newRecord);
        }

        for (int i = 0; i < newMessage.messageHeader.ARCOUNT; i++) {
            DNSRecord newRecord = DNSRecord.decodeRecord(readIn, newMessage);
            newMessage.additionalRecords.add(newRecord);
        }

        return newMessage;
    }

    //read the pieces of a domain name starting from the current position of the input stream
    ArrayList<String> readDomainName(InputStream readIn) throws IOException { //helps in reading the domain name section of packet.
        ArrayList<String> domainNameArray = new ArrayList<>(); // array list of strings to hold the domain name.

        byte[] getBytes = readIn.readNBytes(1); // the length of the domain name?
        int numOctet = getBytes[0];
        while (numOctet != 0) { // 0 is the indicator of the end of the domain name.
            byte[] domainFrag = readIn.readNBytes(numOctet); // read in the domain name or fragment?
            String translatedFrag = new String(domainFrag); // convert it to a string.
            domainNameArray.add(translatedFrag); // add the fragment to the array
            getBytes = readIn.readNBytes(1); // update
            numOctet = getBytes[0]; // update
        }


        return domainNameArray; 
    }
//
//    //--same, but used when there's compression and we need to find the domain from earlier in the message.
//    This method should make a ByteArrayInputStream that starts at the specified byte and call the other version of this method
    ArrayList<String> readDomainName(int firstByte) throws IOException {

        InputStream readin = new ByteArrayInputStream(message);
        byte[] getbytes = readin.readNBytes(firstByte);

        ArrayList<String> DomainName = readDomainName(readin);

        return DomainName;
    }

    //--build a response based on the request and the answers you intend to send back.
    static DNSMessage buildResponse(DNSMessage request, DNSRecord answer){ // builds a response message with an updated header and answer, but all other partsof message equal to request
        DNSMessage response = new DNSMessage();

        response.messageHeader = DNSHeader.buildResponseHeader(request, response);

        response.questionArray = request.questionArray;

        response.answerArray.add(answer);

        response.authorityRecords = request.authorityRecords;

        response.additionalRecords = request.additionalRecords;

        return response;
    }
//-- get the bytes to put in a packet and send back
    byte[] toBytes() throws IOException { // convert return message into bytes.
        ByteArrayOutputStream messageByteArray = new ByteArrayOutputStream();

        HashMap<String, Integer> domainNameLocations = new HashMap<>(); // empty hash map needed for other write bytes functions.
        this.messageHeader.writeBytes(messageByteArray); // start with header, others need for loop incase there is more than one of each.

        for (DNSQuestion q: this.questionArray) { // use helper functions in each class to change to bytes and add it to new byte array.
            q.writeBytes(messageByteArray, domainNameLocations);
        }

        for (DNSRecord a: this.answerArray) {
            a.writeBytes(messageByteArray, domainNameLocations);
        }

        for (DNSRecord a: this.authorityRecords) {
            a.writeBytes(messageByteArray, domainNameLocations);
        }

        for (DNSRecord a: this.additionalRecords) {
            a.writeBytes(messageByteArray, domainNameLocations);
        }

        return messageByteArray.toByteArray();
    }

    // -- If this is the first time we've seen this domain name in the packet, write it using the DNS encoding
    // (each segment of the domain prefixed with its length, 0 at the end), and add it to the hash map.
    // Otherwise, write a back pointer to where the domain has been seen previously.

    static void writeDomainName(ByteArrayOutputStream writeOut, HashMap<String,Integer> domainLocations, ArrayList<String > domainPieces){

        String domainName = String.join(".", domainPieces); // stitches together the array of domain name pieces into a string

        if (!domainLocations.containsKey(domainName)){ // if this is the first time the domain name has been writen
            domainLocations.put(domainName, writeOut.size());
            for (String s: domainPieces){
                writeOut.write(s.length());
                for (int i = 0; i < s.length(); i++){ // write it out char by char full name
                    char c = s.charAt(i);
                    writeOut.write(c);
                }
            }
            writeOut.write(0);
        }
        else{ // write out the position of the domain name.
            int position = domainLocations.get(domainName);
            writeOut.write(((position >> 8) | 0xc0));
            writeOut.write(position);
        }

    }


    @Override
    public String toString() {
        return "DNSMessage{" +
                "messageHeader=" + messageHeader +
                ", questionArray=" + questionArray +
                ", answerArray=" + answerArray +
                ", authorityRecords=" + authorityRecords +
                ", additionalRecords=" + additionalRecords +
                ", message=" + Arrays.toString(message) +
                '}';
    }

    static void writeTwoBytes (OutputStream writeOut, short outputShort) throws IOException {
        byte first = (byte)(outputShort >> 8); // get the first byte
        writeOut.write(first); // write it out
        byte second = (byte)(outputShort); // the first 8 bits of the short will be printed nothing else so no shifting or bit manipulation needed.
        writeOut.write(second); // print it out
    }
//    static void writeZero (OutputStream writeOut) throws IOException {
//        writeOut.write(0);
//        writeOut.write(0);
//    }
}
