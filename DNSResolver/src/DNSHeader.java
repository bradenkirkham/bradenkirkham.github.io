import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DNSHeader {

    short ID;
    byte QR;
    byte OPcode;
    byte AA;
    byte TC;
    byte RD;
    byte RA;
    byte Z;
    byte AD;
    byte CD;
    byte RCODE;
    short QDCOUNT;
    short ANCOUNT;
    short NSCOUNT;
    short ARCOUNT;

    //--read the header from an input stream (we'll use a ByteArrayInputStream but we will only use the basic read methods of input stream to read 1 byte, or to fill in a byte array, so we'll be generic).
    static DNSHeader decodeHeader(InputStream bytes) throws IOException {
        DNSHeader decodedHeader = new DNSHeader(); //to hold member variables.

        byte[] getbytes = bytes.readNBytes(2); // byte array to update when we need more bytes.
        decodedHeader.ID = (short)((getbytes[0] << 8) | (getbytes[1] & 0x00ff)); // to get each field we had to do bit manipulation like this.
        System.out.println("header ID: " + String.format("%x", decodedHeader.ID));

        getbytes = bytes.readNBytes(1); // we did it basically a byte at a time with some exception.
        decodedHeader.QR = (byte)((getbytes[0] >>> 7) & 0x01);
        System.out.println("header QR: " + String.format("%x", decodedHeader.QR));

        decodedHeader.OPcode = (byte)((getbytes[0] >> 3) & 0x0f);
        System.out.println("header OPcode: " + String.format("%x", decodedHeader.OPcode));

        decodedHeader.AA = (byte)((getbytes[0]>>2) & 0x01);
        System.out.println("header AA: " + String.format("%x", decodedHeader.AA));

        decodedHeader.TC = (byte)((getbytes[0]>>1) & 0x01);
        System.out.println("header TC: " + String.format("%x", decodedHeader.TC));

        decodedHeader.RD = (byte)(((getbytes[0]<<7)>>7) & 0x01);
        System.out.println("header RD: " + String.format("%x", decodedHeader.RD));

        getbytes = bytes.readNBytes(1); // once one byte is decoded get the next

        decodedHeader.RA= (byte)((getbytes[0]>>7) & 0x01);
        System.out.println("header RA: " + String.format("%x", decodedHeader.RA));

        decodedHeader.Z = (byte)((getbytes[0]>>6) & 0x01);
        System.out.println("header Z: " + String.format("%x", decodedHeader.Z));

        decodedHeader.AD = (byte)((getbytes[0]>>5) & 0x01);
        System.out.println("header AD: " + String.format("%x", decodedHeader.AD));

        decodedHeader.CD = (byte)((getbytes[0]>>4) & 0x01);
        System.out.println("header CD: " + String.format("%x", decodedHeader.CD));

        decodedHeader.RCODE = (byte)(((getbytes[0]<<4)>>4) & 0x0f);
        System.out.println("header RCODE: " + String.format("%x", decodedHeader.RCODE));

        getbytes = bytes.readNBytes(2); // the next 4 or so things are all 2 bytes long.

        decodedHeader.QDCOUNT = (short)((getbytes[0] << 8) | (getbytes[1] & 0xff));
        System.out.println("header QDCOUNT: " + String.format("%x", decodedHeader.QDCOUNT));

        getbytes = bytes.readNBytes(2);

        decodedHeader.ANCOUNT = (short)((getbytes[0] << 8) | (getbytes[1] & 0x00ff));
        System.out.println("header ANCOUNT: " + String.format("%x", decodedHeader.ANCOUNT));

        getbytes = bytes.readNBytes(2);

        decodedHeader.NSCOUNT = (short)((getbytes[0] << 8) | (getbytes[1] & 0x00ff));
        System.out.println("header NSCOUNT: " + String.format("%x", decodedHeader.NSCOUNT));

        getbytes = bytes.readNBytes(2);

        decodedHeader.ARCOUNT = (short)((getbytes[0] << 8) | (getbytes[1] & 0x00ff));
        System.out.println("header ARCOUNT: " + String.format("%x", decodedHeader.ARCOUNT));

        return decodedHeader;
    }

    //-- This will create the header for the response. It will copy some fields from the request
    static DNSHeader buildResponseHeader(DNSMessage request, DNSMessage response){
        DNSHeader responseHeader = request.messageHeader;

        responseHeader.QR = 1; // will be the same as the message header but the qr and ancount will need to be updated to 1
        responseHeader.ANCOUNT = 1;

        //google response you need to check the answer count
            //if ans > 0
                //update ANCOUNT = 1
            //else
                //ans == 0;

        //update the QR

        return responseHeader;
    }

    // --encode the header to bytes to be sent back to the client.
    // The OutputStream interface has methods to write a single byte or an array of bytes.
    void writeBytes(OutputStream bytes) throws IOException {
        byte tobyte = 0;

        DNSMessage.writeTwoBytes(bytes, ID); // we often write two whole bytes in a row, and this helps in those cases.

        tobyte = (byte)(QR << 7); // formulate the first byte
        tobyte = (byte)(tobyte | (OPcode << 6));
        tobyte = (byte)(tobyte | (AA << 2));
        tobyte = (byte)(tobyte | (TC << 1));
        tobyte = (byte)(tobyte | (RD));

        bytes.write(tobyte); // write it out to stream

        tobyte = 0; // zero out byte.
        tobyte = (byte)(RA << 7); // formulate the second byte
        tobyte = (byte)(tobyte | (Z << 6));
        tobyte = (byte)(tobyte | (AD << 5));
        tobyte = (byte)(tobyte | (CD << 4));
        tobyte = (byte)(tobyte | (RCODE));

        bytes.write(tobyte); // write out to stream

        DNSMessage.writeTwoBytes(bytes, QDCOUNT); // use two bytes to write the following two byte sections.
        DNSMessage.writeTwoBytes(bytes, ANCOUNT);
        DNSMessage.writeTwoBytes(bytes, NSCOUNT);
        DNSMessage.writeTwoBytes(bytes, ARCOUNT);

    }

    @Override // to string helper method.
    public String toString() {
        return "DNSHeader{" +
                "ID=" + ID +
                ", QR=" + QR +
                ", OPcode=" + OPcode +
                ", AA=" + AA +
                ", TC=" + TC +
                ", RD=" + RD +
                ", RA=" + RA +
                ", Z=" + Z +
                ", AD=" + AD +
                ", CD=" + CD +
                ", RCODE=" + RCODE +
                ", QDCOUNT=" + QDCOUNT +
                ", ANCOUNT=" + ANCOUNT +
                ", NSCOUNT=" + NSCOUNT +
                ", ARCOUNT=" + ARCOUNT +
                '}';
    }
}

//                                      1  1  1  1  1  1
//        0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5
//        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//        |                      ID                       |
//        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//        |QR|   Opcode  |AA|TC|RD|RA|   Z    |   RCODE   |
//        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//        |                    QDCOUNT                    |
//        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//        |                    ANCOUNT                    |
//        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//        |                    NSCOUNT                    |
//        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//        |                    ARCOUNT                    |
//        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
