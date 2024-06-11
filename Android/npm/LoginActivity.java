package com.tom.npm;


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

public class LoginActivity extends AppCompatActivity {

    EditText name, password;
    Button login;
    TextView ToSignup;
    SQLHelper sqlHelper;
    SQLiteDatabase db;
    SharedPreferences sp1,sp2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        if(getSupportActionBar() !=null) {
            getSupportActionBar().hide();
        }
        name = findViewById(R.id.name);
        password = findViewById(R.id.pwd);
        login = findViewById(R.id.login);
        ToSignup = findViewById(R.id.textToSign);



        ToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                Toast.makeText(LoginActivity.this, "前往注册！", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        sp1 = getSharedPreferences("userinfo", MODE_PRIVATE);
        sp2 = getSharedPreferences("username",MODE_PRIVATE);

        name.setText(sp1.getString("username", null));
        password.setText(sp1.getString("uspwd",null));
        sqlHelper = new SQLHelper(this,"Userinfo",null,1);      //建数据库或者取数据库
        db = sqlHelper.getReadableDatabase();

        login.setOnClickListener(v -> {
            String username = name.getText().toString().trim();
            String password1 = password.getText().toString().trim();

            Cursor cursor = db.query("logins",new String[]{"username","userpwd"}," username=? and userpwd=?",new String[]{username,password1},null,null,null);

            int flag = cursor.getCount();                            //查询出来的记录项的条数，若没有该用户则为0条
            if(flag!=0){ // 若查询出的记录不为0，则进行跳转操作

                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class); // 设置页面跳转

                SharedPreferences.Editor editor = sp1.edit(); // 使用与MainActivity相同的SharedPreferences文件
                cursor.moveToFirst(); // 将光标移动到position为0的位置，默认位置为-1
                String loginname = cursor.getString(0);
                editor.putString("Loginname", loginname);
                editor.putBoolean("isLoggedIn", true); // 保存登录状态
                editor.commit(); // 将用户名存到SharedPreferences中

                startActivity(intent);
                finish(); // 结束当前的 LoginActivity，防止用户回退回登录界面
            } else {
                Toast.makeText(LoginActivity.this, "用户名或密码错误！", Toast.LENGTH_LONG).show(); // 提示用户信息错误或没有账号
            }
        });


    }




}
