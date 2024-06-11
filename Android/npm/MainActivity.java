package com.tom.npm;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;


public class MainActivity extends AppCompatActivity {

    private Button btnChat,btnChatHistory,btnBlog,btnMy;

    private ImageView scan;

    SharedPreferences sp;
    WebView webView;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getSupportActionBar() !=null) {
            getSupportActionBar().hide();
        }


        sp = this.getSharedPreferences("username", this.MODE_PRIVATE);
        SharedPreferences sharedPreferences = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(R.anim.default_anim_in, R.anim.default_anim_out);
            finish();
            return;
        }

        btnChat = findViewById(R.id.btn_chat);
        btnChatHistory = findViewById(R.id.btn_chat_history);
        btnBlog = findViewById(R.id.btn_blog);
        btnMy = findViewById(R.id.btn_my);
        scan = findViewById(R.id.scan);

        TextView customFontTextView = findViewById(R.id.main_title);

// 如果你在 XML 中指定了字体，通常不需要下面的代码
// 如果没有在 XML 中指定字体，你可以在代码中设置
        Typeface typeface = ResourcesCompat.getFont(this, R.font.hhh); // 替换 "your_font" 为你的字体名称
        customFontTextView.setTypeface(typeface);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                overridePendingTransition(R.anim.default_anim_in, R.anim.default_anim_out);
                startActivity(intent);
            }
        });




        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle chat button click
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                overridePendingTransition(R.anim.default_anim_in, R.anim.default_anim_out);
                startActivity(intent);
            }
        });

        btnChatHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle chat history button click
                Intent intent = new Intent(MainActivity.this, ChatHistoryActivity.class);
                overridePendingTransition(R.anim.default_anim_in, R.anim.default_anim_out);
                startActivity(intent);
            }
        });

        btnBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle blog button click
                Intent intent = new Intent(MainActivity.this, BlogActivity.class);
                overridePendingTransition(R.anim.default_anim_in, R.anim.default_anim_out);
                startActivity(intent);
            }
        });

        btnMy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle my button click
                Intent intent = new Intent(MainActivity.this, MyActivity.class);
                overridePendingTransition(R.anim.default_anim_in, R.anim.default_anim_out);
                startActivity(intent);
            }
        });





    }






}
