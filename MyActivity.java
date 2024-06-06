package com.example.npm;

import android.annotation.SuppressLint;
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

public class MyActivity extends AppCompatActivity {

    private ImageView back;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my);

        back = findViewById(R.id.h_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyActivity.this,MainActivity.class);
                overridePendingTransition(R.anim.default_anim_out, R.anim.default_anim_in);
                startActivity(intent);
            }
        });

        nav_bar helpNavBar = findViewById(R.id.help);
        nav_bar cardsNavBar = findViewById(R.id.cards);

        nav_bar aboutNavBar = findViewById(R.id.about);
        helpNavBar.setTextSizeInSp(25);
        cardsNavBar.setTextSizeInSp(25);
        aboutNavBar.setTextSizeInSp(25);


        // Set click listeners
        helpNavBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the new Activity for help
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("weixin://dl/officialaccounts"));
                startActivity(intent);
            }
        });

        cardsNavBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the new Activity for cards
                Intent intent = new Intent(MyActivity.this, CardsActivity.class);
                startActivity(intent);
            }
        });



        aboutNavBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the new Activity for about
                Intent intent = new Intent(MyActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

    }
}