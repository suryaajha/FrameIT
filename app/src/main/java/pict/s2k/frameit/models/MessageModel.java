package pict.s2k.frameit.models;

/**
 * Created by suryaa on 12/9/17.
 */

public class MessageModel {

    private String message ;
    private String sender;
    private long timeStamp ;
    private String sender_uid ;
    private String receiver_uid ;
    //private String sender_profile_uri ;
    //private String receiver_profile_uri ;

    public MessageModel() {
    }

    public MessageModel(String message, String sender,long timeStamp, String sender_uid, String receiver_uid) {
        this.message = message;
        this.sender=sender;
        this.timeStamp = timeStamp;
        this.sender_uid = sender_uid;
        this.receiver_uid = receiver_uid;
        //this.sender_profile_uri = sender_profile_uri ;
        //this.receiver_profile_uri = receiver_profile_uri ;
    }

    /*public String getSender_profile_uri() {
        return sender_profile_uri;
    }

    public String getReceiver_profile_uri() {
        return receiver_profile_uri;
    }*/

    public String getMessage() {
        return message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getSender_uid() {
        return sender_uid;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver_uid() {
        return receiver_uid;
    }
}
