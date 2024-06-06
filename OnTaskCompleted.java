package com.example.npm;

import org.json.JSONException;

public interface OnTaskCompleted {
    void onTaskCompleted(String result) throws JSONException;
    void onFailure(Exception e);
}
