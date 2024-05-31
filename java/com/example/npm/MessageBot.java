package com.example.npm;

public class MessageBot {
    public enum Sender {USER, BOT}

    private String content;
    private Sender sender;

    public MessageBot(String content, Sender sender) {
        this.content = content;
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public Sender getSender() {
        return sender;
    }
}
