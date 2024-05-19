package com.example.npm;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "chat_sessions")
public class ChatSession {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    public ChatSession(String name,  long timestamp) {
        this.name = name;
        this.timestamp = timestamp;
    }

    // Getter and Setter methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFormattedTimestamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return formatter.format(new Date(this.timestamp));
    }
}