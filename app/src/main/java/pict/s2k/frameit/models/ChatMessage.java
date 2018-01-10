package pict.s2k.frameit.models;

/**
 * Created by kshitij on 16/9/17.
 */

public class ChatMessage {
    private String message;
    private String sender_uid;
    private String receiver_uid;
    private String sender_profile_pic;
    private String receiver_profile_pic;
    private String timestamp;

    public ChatMessage() {
    }

    public ChatMessage(String message, String sender_uid, String receiver_uid, String sender_profile_pic, String receiver_profile_pic) {
        this.message = message;
        this.sender_uid = sender_uid;
        this.receiver_uid = receiver_uid;
        this.sender_profile_pic = sender_profile_pic;
        this.receiver_profile_pic = receiver_profile_pic;
        timestamp=String.valueOf(System.currentTimeMillis());
    }

    public String getMessage() {
        return message;
    }

    public String getSender_uid() {
        return sender_uid;
    }

    public String getReceiver_uid() {
        return receiver_uid;
    }

    public String getSender_profile_pic() {
        return sender_profile_pic;
    }

    public String getReceiver_profile_pic() {
        return receiver_profile_pic;
    }

    public String getTimestamp() {
        return timestamp;
    }
}