package com.example.npm;


import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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

public class LoginActivity extends AppCompatActivity {

    EditText name, password;
    Button login;
    TextView ToSignup;
    SQLHelper sqlHelper;
    SQLiteDatabase db;
    SharedPreferences sp1,sp2;

    Dialog dialog;

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
                showPrivacy("Privacy_Policy.txt");
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
    public void onClickAgree(View v)
    {
        dialog.dismiss();
    }
    public void onClickDisagree(View v)
    {
        finish();
    }
    public void showPrivacy(String privacyFileName)
    {
        String str = initAssets(privacyFileName);
        final View inflate = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_privacy_show, null);
        TextView tv_title = (TextView) inflate.findViewById(R.id.tv_title);
        tv_title.setText("隐私政策授权提示");
        TextView tv_content = (TextView) inflate.findViewById(R.id.tv_content);
        tv_content.setText(str);
        dialog = new AlertDialog
                .Builder(LoginActivity.this)
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
