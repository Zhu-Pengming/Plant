package com.tom.npm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.json.JSONException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class TranslationService extends Service {
    private static final String APP_ID = "20240514002051247"; // Replace with your actual APP ID
    private static final String KEY = "UpI1T3c9vqa_J8rig85V"; // Replace with your actual Key
    private static final String API_URL = "https://fanyi-api.baidu.com/api/trans/vip/translate";

    private OkHttpClient okHttpClient;

    public TranslationService() {
        this.okHttpClient = new OkHttpClient();
    }
    public IBinder onBind(Intent intent) {
        // Return the communication channel to the service.
        return null;
    }
    public void translate(String text, String fromLang, String toLang, TranslationCallback callback) {
        if (text == null || text.isEmpty()) {
            callback.onFailure(new IllegalArgumentException("Text to translate cannot be null or empty"));
            return;
        }

        String salt = String.valueOf(System.currentTimeMillis());
        String sign = generateMD5(APP_ID + text + salt + KEY);
        String url = API_URL + "?appid=" + APP_ID + "&q=" + text + "&from=" + fromLang + "&to=" + toLang + "&salt=" + salt + "&sign=" + sign;

        Request request = new Request.Builder()
                .url(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure(new IOException("Unexpected code " + response));
                } else {
                    String jsonData = response.body().string();
                    System.out.println("Translation response: " + jsonData); // Add this line
                    try {
                        callback.onSuccess(jsonData);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    private static String generateMD5(String string) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(string.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Could not generate MD5", e);
        }
    }

}