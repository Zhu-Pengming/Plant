package com.tom.npm;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class ChatViewModel extends ViewModel {
    private LiveData<List<ChatSession>> chatSessions;
    private ChatSessionDao chatSessionDao;

    public ChatViewModel(ChatSessionDao chatSessionDao) {
        this.chatSessionDao = chatSessionDao;
        chatSessions = chatSessionDao.getAllSessions();
    }

    public LiveData<List<ChatSession>> getChatSessions() {
        return chatSessions;
    }



    public void insertChatSession(ChatSession chatSession) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            long id = chatSessionDao.insert(chatSession);
            Log.d("DatabaseInsert", "Inserted new session with ID: " + id); // Check if id is valid and not -1
        });
    }
}
