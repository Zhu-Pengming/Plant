package com.tom.npm;

public interface BotResponseCallback {
    void onSuccess(String response);
    void onFailure(Exception e);
}