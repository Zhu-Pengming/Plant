package com.example.npm;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AboutActivity extends AppCompatActivity {

    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);

        // Find the nav_bar components
        nav_bar commentsNavBar = findViewById(R.id.about_comments);
        nav_bar functionNavBar = findViewById(R.id.about_function);
        nav_bar complaintNavBar = findViewById(R.id.about_complaint);


        commentsNavBar.setTextSizeInSp(25);
        functionNavBar.setTextSizeInSp(25);
        complaintNavBar.setTextSizeInSp(25);

        back = findViewById(R.id.about_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutActivity.this, MyActivity.class);
                overridePendingTransition(R.anim.default_anim_out, R.anim.default_anim_in);
                startActivity(intent);
            }
        });




        // Set click listeners
        commentsNavBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=<your-package-name>"));
                startActivity(intent);
            }
        });

        functionNavBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the new Activity for function
                Intent intent = new Intent(AboutActivity.this, FunctionActivity.class);
                startActivity(intent);
            }
        });

        complaintNavBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open WeChat public account
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("weixin://dl/officialaccounts"));
                startActivity(intent);
            }
        });
    }
}