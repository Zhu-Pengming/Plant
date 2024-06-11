package com.tom.npm;

import org.json.JSONException;

public interface TranslationCallback {
    void onSuccess(String translatedText) throws JSONException;
    void onFailure(Exception e);
}
