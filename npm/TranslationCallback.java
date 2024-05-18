package com.example.npm;

public interface TranslationCallback {
    void onSuccess(String translatedText);
    void onFailure(Exception e);
}
