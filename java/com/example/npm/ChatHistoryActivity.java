package com.example.npm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatHistoryActivity extends AppCompatActivity {
    private ChatViewModel viewModel;
    private RecyclerView recyclerView;
    private ChatSessionAdapter adapter;

    private ImageView returnTo;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);

        recyclerView = findViewById(R.id.recyclerViewChatSessions);
        adapter = new ChatSessionAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        returnTo = findViewById(R.id.chathistory_return);
        returnTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatHistoryActivity.this, MainActivity.class);
                overridePendingTransition(R.anim.default_anim_out, R.anim.default_anim_in);
                startActivity(intent);
            }
        });

        // Get a reference to your AppDatabase instance
        AppDatabase db = AppDatabase.getDatabase(this);
        // Get the ChatSessionDao
        ChatSessionDao chatSessionDao = db.chatSessionDao();

        ChatViewModelFactory factory = new ChatViewModelFactory(chatSessionDao);
        viewModel = new ViewModelProvider(this, factory).get(ChatViewModel.class);

        viewModel.getChatSessions().observe(this, chatSessions -> {
            try {
                Log.d("ChatHistoryActivity", "Number of sessions fetched: " + chatSessions.size());
                if (chatSessions.isEmpty()) {
                    Log.d("ChatHistoryActivity", "No chat sessions available.");
                } else {
                    adapter.setChatSessions(chatSessions);
                    adapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                Log.e("ChatHistoryActivity", "Error fetching chat sessions", e);
                // Show an error message to the user
                Toast.makeText(this, "Error fetching chat sessions", Toast.LENGTH_SHORT).show();
            }
        });


        SearchView searchView = findViewById(R.id.chat_search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });


    }


}