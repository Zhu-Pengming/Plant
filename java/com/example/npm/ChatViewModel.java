package com.example.npm;

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
        AppDatabase.databaseWriteExecutor.execute(() -> chatSessionDao.insert(chatSession));
    }
}
