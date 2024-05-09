package com.example.npm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class RecognitionService extends Service {


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private static final OkHttpClient client = new OkHttpClient();

    public RecognitionService(String apiKey) {

    }


}