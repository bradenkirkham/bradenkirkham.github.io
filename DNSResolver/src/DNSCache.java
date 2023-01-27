import java.util.HashMap;

public class DNSCache {
    static HashMap<DNSQuestion, DNSRecord> messageAnswer = new HashMap<>();

    static boolean queryRecord(DNSQuestion userQuestion){
        if (messageAnswer.containsKey(userQuestion)){ // check that the hashmap has the question
            DNSRecord queryAnswer = messageAnswer.get(userQuestion);
            if (!queryAnswer.timestampValid()) {//if so checks the timestamp
                messageAnswer.remove(userQuestion, queryAnswer); //if invalid timestamp remove question
                return false; // return false
            }
                return true; // if timestamp valid return true.
        }
        return false; // if no question return false.
    }
    static boolean insertRecord(DNSQuestion userQuestion, DNSRecord queryAnswer){ // adds record.
        if (!queryRecord(userQuestion)) { // use queryrecord function to see if we already have the message.
            messageAnswer.put(userQuestion, queryAnswer); // if not add it
            return true; // return true to indicate it was added.
        }
        return false; // means we already have it.
    }
    static DNSRecord getAnswer (DNSQuestion userQuestion){
        return messageAnswer.get(userQuestion);
    } //return the record connected with the question.
}
