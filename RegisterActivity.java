package com.example.npm;


import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class RegisterActivity extends AppCompatActivity {

    EditText usename,usepwd,usepwd2;
    Button submit;
    SQLHelper sqlHelper;
    SQLiteDatabase db;
    SharedPreferences sp;

    TextView ToLogin;

    Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        if(getSupportActionBar() !=null) {
            getSupportActionBar().hide();
        }

        CheckBox checkBox = findViewById(R.id.checkBox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPrivacy("Privacy_Policy.txt");
            }
        });
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

    public void showPrivacy(String privacyFileName)
    {
        String str = initAssets(privacyFileName);
        final View inflate = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.dialog_privacy_show, null);
        TextView tv_title = (TextView) inflate.findViewById(R.id.tv_title);
        tv_title.setText("隐私政策授权提示");
        TextView tv_content = (TextView) inflate.findViewById(R.id.tv_content);
        tv_content.setText(str);
        dialog = new AlertDialog
                .Builder(RegisterActivity.this)
                .setView(inflate)
                .show();
        // 通过WindowManager获取
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = dm.widthPixels*4/5;
        params.height = dm.heightPixels*1/2;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void onClickAgree(View v)
    {
        dialog.dismiss();
    }
    public void onClickDisagree(View v)
    {
        finish();
    }

    /**
     * 从assets下的txt文件中读取数据
     */
    public String initAssets(String fileName) {
        String str = null;
        try {
            InputStream inputStream = getAssets().open(fileName);

            str = getString(inputStream);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return str;
    }

    public static String getString(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}