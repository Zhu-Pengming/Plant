package com.tom.npm;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chat_sessions")
public class ChatSession {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "chat_name")
    private String chatName;

    @ColumnInfo(name = "sequence_number")
    private int sequenceNumber;

    @ColumnInfo(name = "sender")
    private String sender;  // "USER" or "BOT"

    @ColumnInfo(name = "message")
    private String message;

    @ColumnInfo(name = "time")
    private String time;

    // Constructor
    public ChatSession(String chatName, int sequenceNumber, String sender, String message,String time) {
        this.chatName = chatName;
        this.sequenceNumber = sequenceNumber;
        this.sender = sender;
        this.message = message;
        this.time = time;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}