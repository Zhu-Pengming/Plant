package com.tom.npm;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ChatViewModelFactory implements ViewModelProvider.Factory {
    private ChatSessionDao chatSessionDao;

    public ChatViewModelFactory(ChatSessionDao chatSessionDao) {
        this.chatSessionDao = chatSessionDao;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ChatViewModel.class)) {
            return (T) new ChatViewModel(chatSessionDao);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
