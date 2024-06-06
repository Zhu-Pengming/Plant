package com.example.npm;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class SQLHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Userinfo.db";
    private static final int DATABASE_VERSION = 1;

    public SQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE logins (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, userpwd TEXT)";
        Log.d("SQLDebug", "Creating table with SQL: " + sql);
        db.execSQL(sql);
        Log.d("SQLDebug", "Database table 'logins' created successfully.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS logins");
        onCreate(db);
    }

    public SQLHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }





}

