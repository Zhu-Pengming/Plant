package com.example.npm;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatHistoryActivity extends AppCompatActivity {
    private ChatViewModel viewModel;
    private RecyclerView recyclerView;
    private ChatSessionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);

        recyclerView = findViewById(R.id.recyclerViewChatSessions);
        adapter = new ChatSessionAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get a reference to your AppDatabase instance
        AppDatabase db = AppDatabase.getDatabase(this);
        // Get the ChatSessionDao
        ChatSessionDao chatSessionDao = db.chatSessionDao();

        ChatViewModelFactory factory = new ChatViewModelFactory(chatSessionDao);
        viewModel = new ViewModelProvider(this, factory).get(ChatViewModel.class);

        viewModel.getChatSessions().observe(this, chatSessions -> {
            adapter.setChatSessions(chatSessions);
            adapter.notifyDataSetChanged();
        });
    }
}