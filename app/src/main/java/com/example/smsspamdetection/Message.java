package com.example.smsspamdetection;

public class Message {
    private String message, sender, date;


    public Message(String message, String sender, String date) {
        this.message = message;
        this.sender = sender;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
