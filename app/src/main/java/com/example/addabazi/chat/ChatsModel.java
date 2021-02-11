package com.example.addabazi.chat;

public class ChatsModel {

    private String message;
    private String message_from;
    private String message_id;
    private long message_time;
    private String message_type;

    public ChatsModel() {
    }

    public ChatsModel(String message, String message_from, String message_id, long message_time, String message_type) {
        this.message = message;
        this.message_from = message_from;
        this.message_id = message_id;
        this.message_time = message_time;
        this.message_type = message_type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage_from() {
        return message_from;
    }

    public void setMessage_from(String message_from) {
        this.message_from = message_from;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public long getMessage_time() {
        return message_time;
    }

    public void setMessage_time(long message_time) {
        this.message_time = message_time;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }
}
