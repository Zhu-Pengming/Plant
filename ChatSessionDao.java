package com.example.npm;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ChatSessionDao {
    @Insert
    long insert(ChatSession chatSession);

    @Update
    void update(ChatSession chatSession);

    @Query("SELECT * FROM chat_sessions ORDER BY time DESC")
    LiveData<List<ChatSession>> getAllSessions();

    @Query("SELECT * FROM chat_sessions WHERE chat_name = :name LIMIT 1")
    LiveData<ChatSession> getSessionByName(String name);

}
