package com.example.npm;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    EditText name, password;
    Button login;
    TextView register;
    SQLHelper sqlHelper;
    SQLiteDatabase db;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


        name = findViewById(R.id.name);
        password = findViewById(R.id.pwd);
        login = findViewById(R.id.login);
        register = findViewById(R.id.login_reg);
        sp = getSharedPreferences("userinfo", MODE_PRIVATE);

        name.setText(sp.getString("username", ""));  // 只记住用户名
        sqlHelper = new SQLHelper(this, "Userinfo.db", null, 1);
        db = sqlHelper.getReadableDatabase();

        login.setOnClickListener(v -> {
            String username = name.getText().toString();
            String password1 = password.getText().toString();
            // 使用安全的密码验证方法
            try (Cursor cursor = db.query("logins", new String[]{"usname"}, "usname=? and uspwd=?", new String[]{username, password1}, null, null, null)) {
                if (cursor.moveToFirst()) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("name", username);
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, "Incorrect username or password!", Toast.LENGTH_LONG).show();
                }
            }
        });

        register.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            Toast.makeText(LoginActivity.this, "Go register!", Toast.LENGTH_SHORT).show();
        });
    }
}
