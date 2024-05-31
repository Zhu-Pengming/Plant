package com.example.npm;


import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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

public class RegisterActivity extends AppCompatActivity {

    EditText usename,usepwd,usepwd2;
    Button submit;
    SQLHelper sqlHelper;
    SQLiteDatabase db;
    SharedPreferences sp;

    TextView ToLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        if(getSupportActionBar() !=null) {
            getSupportActionBar().hide();
        }

        usename = this.findViewById(R.id.usename);			    //用户名编辑框
        usepwd =  this.findViewById(R.id.usepwd);				//设置初始密码编辑框
        usepwd2 = this.findViewById(R.id.usepwd2);			    //二次输入密码编辑框
        submit =   this.findViewById(R.id.submit);				//注册按钮
        sqlHelper = new SQLHelper(this,"Userinfo",null,1);      //建数据库
        db = sqlHelper.getReadableDatabase();
        sp = this.getSharedPreferences("userinfo",this.MODE_PRIVATE);

        ToLogin = findViewById(R.id.textToLogin);
        ToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            boolean flag = true;
            @Override
            public void onClick(View v) {
                String name = usename.getText().toString().trim();
                String pwd01 = usepwd.getText().toString().trim();
                String pwd02 = usepwd2.getText().toString().trim();

                if(name.equals("")||pwd01 .equals("")||pwd02.equals("")){
                    Toast.makeText(RegisterActivity.this, "用户名或密码不能为空!！", Toast.LENGTH_LONG).show();
                }
                else{
                    Cursor cursor = db.query("logins",new String[]{"username"},null,null,null,null,null);

                    while (cursor.moveToNext()){
                        if(cursor.getString(0).equals(name)){
                            flag = false;
                            break;
                        }
                    }
                    if(flag==true){                                             //判断用户是否已存在
                        if (pwd01.equals(pwd02)) {								//判断两次输入的密码是否一致，若一致则继续，不一致则提醒密码不一致
                            ContentValues cv = new ContentValues();
                            cv.put("username",name);
                            cv.put("userpwd",pwd01);
                            db.insert("logins",null,cv);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("username",name);
                            editor.putString("userpwd",pwd01);
                            editor.commit();
                            Intent intent = new Intent();
                            intent.setClass(RegisterActivity.this,MainActivity.class);      //跳转到登录页面
                            startActivity(intent);
                            Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "密码不一致！", Toast.LENGTH_LONG).show();			//提示密码不一致
                        }
                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "用户已存在！", Toast.LENGTH_LONG).show();			//提示密码不一致
                    }

                }
            }

        });


    }
}