import javax.naming.Name;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class DNSRecord {
    Calendar creationDate = Calendar.getInstance();
    ArrayList<String> Name = new ArrayList<>();
    short Type;
    short Class;
    int TTL;
    short RDLength;
    byte[] RData;

    static DNSRecord decodeRecord(InputStream readin, DNSMessage userMessage) throws IOException {
        DNSRecord newRecord = new DNSRecord();

        readin.mark(2); // place a flag in the stream to potentially return to.
        byte[] getbytes = readin.readNBytes(1); // get the first byte

        int significantBits = getbytes[0] & 0xc0; // grab just the significant bit.
        if (significantBits == 0xc0){ // if it is set, then this is compression.
            readin.reset(); // reset the stream
            getbytes = readin.readNBytes(2); // grab the next two bytes.
            short name = (short)(((getbytes[0] << 8) | (getbytes[1] & 0x00ff)) & 0x3fff); // convert them to a short
            int firstBytes = name; // convert to an int
            newRecord.Name = userMessage.readDomainName(firstBytes); //call readDomain name for compression
        }
        else{ // no compression
            readin.reset();
            newRecord.Name = userMessage.readDomainName(readin); // read the full domain name.
        }

        getbytes = readin.readNBytes(2); // for the next two bytes we use bitwise operators to decode them
        newRecord.Type = (short)((getbytes[0] << 8) | (getbytes[1] & 0x00ff));
        System.out.println("Type: " + String.format("%x", newRecord.Type));

        getbytes = readin.readNBytes(2);
        newRecord.Class = (short)((getbytes[0] << 8) | (getbytes[1] & 0x00ff));
        System.out.println("Class: " + String.format("%x", newRecord.Class));

        getbytes = readin.readNBytes(4); // the next 4 bytes require a loop to get out the time to live field is 4 bytes long
        int shift = 24;
        for (int i = 0; i < getbytes.length; i++){ // repeat the following process to decode.
            int temp = getbytes[i]; // get the first set of bytes
            temp = temp << shift; // shift it over shift number of times.
            int mask = 0xff << shift; // generate mask for it
            temp = temp & mask; // mask it
            newRecord.TTL = newRecord.TTL | temp; // or TTL field with it
            shift = shift - 8; // update shift.
        }
        System.out.println(newRecord.TTL);
//        System.out.println("TTL: " + String.format("%x", newRecord.TTL));

        getbytes = readin.readNBytes(2);
        newRecord.RDLength = (short)((getbytes[0] << 8) | (getbytes[1] & 0x00ff));
        System.out.println("RDLength: " + String.format("%x", newRecord.RDLength));

        if (newRecord.RDLength != 0) { // if rdlength is not zero than there is information needed in rdata
            newRecord.RData = readin.readNBytes(newRecord.RDLength); //rdata is a byte array so just read in the length amount of data
        }
//        newRecord.RData = (short)((getbytes[0] << 8) | (getbytes[1] & 0xff));
//        System.out.println("RData: " + String.format("%x", newRecord.RData));

        System.out.println("DNSrecord domain name:" + newRecord.Name);
        return newRecord;
    }

    void writeBytes(ByteArrayOutputStream writeOut, HashMap<String, Integer> domainNameLocatoins) throws IOException { // this function works almost the same as in other classes.
        DNSMessage.writeDomainName(writeOut, domainNameLocatoins, Name);

        DNSMessage.writeTwoBytes(writeOut, Type);

        DNSMessage.writeTwoBytes(writeOut, Class);

        byte newByte = 0;
        newByte = (byte)(TTL >> 24);
        writeOut.write(newByte);

        newByte = (byte)(TTL >> 16);
        writeOut.write(newByte);

        newByte = (byte)(TTL >> 8);
        writeOut.write(newByte);

        newByte = (byte)(TTL);
        writeOut.write(newByte);

        DNSMessage.writeTwoBytes(writeOut, RDLength);

        if (RDLength > 0){
            writeOut.writeBytes(RData);
        }

    }

    @Override
    public String toString() {
        return "DNSRecord{" +
                "creationDate=" + creationDate +
                ", Name=" + Name +
                ", Type=" + Type +
                ", Class=" + Class +
                ", TTL=" + TTL +
                ", RDLength=" + RDLength +
                ", RData=" + RData +
                '}';
    }

//
    // -- return whether the creation date + the time to live is after the current time. The Date and Calendar classes will be useful for this.
    boolean timestampValid(){
        creationDate.add(Calendar.SECOND, TTL);

        return creationDate.after(Calendar.getInstance());
    }
}
