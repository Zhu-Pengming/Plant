package com.example.npm;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


public class MainActivity extends AppCompatActivity {

    private Button btnChat,btnChatHistory,btnBlog,btnMy;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        askPermission();
        sp = this.getSharedPreferences("username", this.MODE_PRIVATE);
        SharedPreferences sharedPreferences = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        btnChat = findViewById(R.id.btn_chat);
        btnChatHistory = findViewById(R.id.btn_chat_history);
        btnBlog = findViewById(R.id.btn_blog);
        btnMy = findViewById(R.id.btn_my);

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle chat button click
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        btnChatHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle chat history button click
                Intent intent = new Intent(MainActivity.this, ChatHistoryActivity.class);
                startActivity(intent);
            }
        });

        btnBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle blog button click
                Intent intent = new Intent(MainActivity.this, BlogActivity.class);
                startActivity(intent);
            }
        });

        btnMy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle my button click
                Intent intent = new Intent(MainActivity.this, MyActivity.class);
                startActivity(intent);
            }
        });





    }



    private void askPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        }, 0);
    }
    public void showBlogContentFragment(BlogPost post) {
        BlogContentFragment fragment = new BlogContentFragment();
        Bundle args = new Bundle();
        args.putString("title", post.getTitle());
        args.putString("content", post.getContent());
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_blog, fragment) // Assuming 'container' is the ID of your Fragment container
                .addToBackStack(null) // Optional, to add the transaction to the back stack
                .commit();
    }

}
