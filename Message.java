package com.example.npm;

public class Message {

    // String to store our message and sender
    private String message;
    private String sender;
    private String imageUri; // URI to store the path of the image associated with the message

    // Constructor for messages with text and image
    public Message(String message, String imageUri, String sender) {
        this.message = message;
        this.imageUri = imageUri;
        this.sender = sender;
    }

    // Constructor for messages with text only
    public Message(String message, String sender) {
        this.message = message;
        this.sender = sender;
        this.imageUri = null; // No image associated
    }

    // Getter and setter methods
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

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
