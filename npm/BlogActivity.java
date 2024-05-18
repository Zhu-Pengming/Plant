package com.example.npm;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BlogActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BlogAdapter blogAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        recyclerView = findViewById(R.id.BlogRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        blogAdapter = new BlogAdapter(this, DataRepository.getBlogPosts());
        recyclerView.setAdapter(blogAdapter);
    }
}