import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class DNSQuestion {
    ArrayList<String> question;
    short QType;
    short QClass;

   // -- read a question from the input stream. Due to compression, you may have to ask the DNSMessage containing this question to read some of the fields.
    static DNSQuestion decodeQuestion(InputStream readin, DNSMessage clientMessage) throws IOException {
        DNSQuestion clientQuestion = new DNSQuestion();


        clientQuestion.question = clientMessage.readDomainName(readin); // the question starts with the domain name.

        System.out.println("Domain Name:" + clientQuestion.question);

        byte[] getbytes = readin.readNBytes(2); // grab the next two bytes.

        clientQuestion.QType = (short)((getbytes[0] << 8) | (getbytes[1] & 0x00ff)); // decode Qtype with bitwise operators
        System.out.println("Qtype: " + String.format("%x", clientQuestion.QType));

        getbytes = readin.readNBytes(2); // same as above for qclass
        clientQuestion.QClass = (short)((getbytes[0] << 8) | (getbytes[1] & 0x00ff));
        System.out.println("QClass: " + String.format("%x", clientQuestion.QClass));

        return clientQuestion;
    }

    //. Write the question bytes which will be sent to the client.
    // The hash map is used for us to compress the message, see the DNSMessage class below.
    void writeBytes(ByteArrayOutputStream writeOut, HashMap<String,Integer> domainNameLocations) throws IOException {

        DNSMessage.writeDomainName(writeOut, domainNameLocations, question); // use helper function to write out the domain name

        DNSMessage.writeTwoBytes(writeOut, QType); // use helper function to write out next two parts of question.

        DNSMessage.writeTwoBytes(writeOut, QClass);
    }
//
//    // -- Let your IDE generate these. They're needed to use a question as a HashMap key, and to get a human readable string.
//

    @Override
    public String toString() {
        return "DNSQuestion{" +
                "question=" + question +
                ", QType=" + QType +
                ", QClass=" + QClass +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DNSQuestion)) return false;
        DNSQuestion that = (DNSQuestion) o;
        return QType == that.QType && QClass == that.QClass && question.equals(that.question);
    }

    @Override
    public int hashCode() {
        return Objects.hash(question, QType, QClass);
    }
}
