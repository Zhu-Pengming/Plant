package com.example.npm;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


public class MainActivity extends AppCompatActivity {

    private Fragment ChatFragment, BlogFragment, MyFragment;
    private View ChatLayout, BlogLayout, MyLayout;
    private ImageView ChatImg, BlogImg, MyImg;
    private TextView ChatText, BlogText, MyText;

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

        initViews();
        setTabSelection(0); // Show chat by default
    }

    private void initViews() {
        ChatLayout = findViewById(R.id.chat_layout);
        BlogLayout = findViewById(R.id.blog_layout);
        MyLayout = findViewById(R.id.my_layout);

        ChatImg = (ImageView) findViewById(R.id.chat_img);
        BlogImg = (ImageView) findViewById(R.id.blog_img);
        MyImg = (ImageView) findViewById(R.id.my_img);

        ChatImg.setImageResource(R.drawable.ic_chat);
        BlogImg.setImageResource(R.drawable.ic_blog);
        MyImg.setImageResource(R.drawable.ic_my);

        ChatText = (TextView) findViewById(R.id.chat_text);
        BlogText = (TextView) findViewById(R.id.blog_text);
        MyText = (TextView) findViewById(R.id.my_text);

        ChatLayout.setOnClickListener(v -> setTabSelection(0));
        BlogLayout.setOnClickListener(v -> setTabSelection(1));
        MyLayout.setOnClickListener(v -> setTabSelection(2));
    }

    private void setTabSelection(int index) {
        clearSelection();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideFragments(transaction);

        switch (index) {
            case 0:
                ChatText.setTextColor(Color.parseColor("#B3EE3A"));
                if (ChatFragment == null) {
                    ChatFragment = new ChatFragment();
                    transaction.add(R.id.container_chat, ChatFragment);
                } else {
                    transaction.show(ChatFragment);
                }
                break;
            case 1:
                BlogText.setTextColor(Color.parseColor("#B3EE3A"));
                if (BlogFragment == null) {
                    BlogFragment = new BlogFragment();
                    transaction.add(R.id.container_blog, BlogFragment);
                } else {
                    transaction.show(BlogFragment);
                }
                break;
            case 2:
                MyText.setTextColor(Color.parseColor("#B3EE3A"));
                if (MyFragment == null) {
                    MyFragment = new MyFragment();
                    transaction.add(R.id.container_my, MyFragment);
                } else {
                    transaction.show(MyFragment);
                }
                break;
        }

        transaction.commit();
    }

    private void clearSelection() {
        ChatText.setTextColor(Color.parseColor("#82858b"));
        BlogText.setTextColor(Color.parseColor("#82858b"));
        MyText.setTextColor(Color.parseColor("#82858b"));
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (ChatFragment != null) transaction.hide(ChatFragment);
        if (BlogFragment != null) transaction.hide(BlogFragment);
        if (MyFragment != null) transaction.hide(MyFragment);
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        }, 0);
    }

}
