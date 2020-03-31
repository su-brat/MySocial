package com.example.mysocial.models;

public class Chat {
    String Message, Receiver, Sender, Timestamp;
    boolean Seen;
    public Chat() {

    }

    public Chat(String message, String receiver, String sender, String timestamp, boolean seen) {
        Message = message;
        Receiver = receiver;
        Sender = sender;
        Timestamp = timestamp;
        Seen = seen;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getReceiver() {
        return Receiver;
    }

    public void setReceiver(String receiver) {
        Receiver = receiver;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        Sender = sender;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public boolean isSeen() {
        return Seen;
    }

    public void setSeen(boolean seen) {
        Seen = seen;
    }
}
