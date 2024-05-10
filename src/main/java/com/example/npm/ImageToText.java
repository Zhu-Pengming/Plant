package com.example.npm;

import android.os.AsyncTask;
import android.util.Log;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageToText extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        String API_URL = "https://api-inference.huggingface.co/models/Salesforce/blip-image-captioning-large";
        String filename = params[0];
        String response = "";

        try {
            FileInputStream fileInputStream = new FileInputStream(filename);
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer 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");
            connection.setDoOutput(true);

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            outputStream.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read response
                // Here you can read the response from the API
            } else {
                // Handle error
                Log.e("API_ERROR", "Response code: " + responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        // 在这里处理API的响应数据
        if (result != null && !result.isEmpty()) {
            // 解析并处理API返回的数据
            Log.d("API_RESPONSE", result);
        } else {
            // 处理无效的响应
            Log.e("API_ERROR", "Empty or null response");
        }
    }

}


