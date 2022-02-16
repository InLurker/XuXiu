package com.projekt.mirage.models;

public class ChatsModel {

    private static int counter = 0;

    private int id;
    private String message;
    private String sender;

    public ChatsModel(String message, String sender) {
        this.message = message;
        this.sender = sender;
        id = counter++;
    }

    public ChatsModel(int id, String message, String sender) {
        this.id = id;
        this.message = message;
        this.sender = sender;
        ++counter;
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

    public int getID() {
        return id;
    }

    public int recordAmount() {
        return counter;
    }

    public void resetCounter() {
        counter = 0;
    }

}
