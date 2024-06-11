package com.tom.npm;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlantIdentificationUtil {

    private String TAG = "PlantIdentificationUtil";
    private PlantIdentificationService service;
    private String apiKey;

    public PlantIdentificationUtil(PlantIdentificationService service, String apiKey) {
        this.service = service;
        this.apiKey = apiKey;
    }

    public void identifyPlant(Bitmap image, final PlantIdentificationCallback callback) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        // Prepare the image part
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), byteArray);
        MultipartBody.Part body = MultipartBody.Part.createFormData("images", "image.jpg", requestFile);

        // Prepare organs part
        List<RequestBody> organs = new ArrayList<>();
        organs.add(RequestBody.create(MediaType.parse("text/plain"), "auto"));

        // Prepare authorization header
        String authHeader = "Bearer " + this.apiKey;

        // Call Retrofit to execute the request
        Call<PlantIdentificationResponse> call = service.identifyPlant(
                "all",
                false,
                false,
                "en",
                "kt",
                Collections.singletonList(body),
                organs,
                authHeader
        );





        call.enqueue(new Callback<PlantIdentificationResponse>() {
            @Override
            public void onResponse(Call<PlantIdentificationResponse> call, Response<PlantIdentificationResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Identification successful.");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "API Error: " + response.code() + " - " + response.message();
                    switch (response.code()) {
                        case 400:
                            errorMessage = "错误请求 - 可能是格式错误的请求。";
                            break;
                        case 401:
                            errorMessage = "未授权 - 请检查 API 密钥或令牌。";
                            break;
                        case 404:
                            errorMessage = "未找到物种。";
                            break;
                        case 413:
                            errorMessage = "负载过大。";
                            break;
                        case 414:
                            errorMessage = "URI 太长。";
                            break;
                        case 415:
                            errorMessage = "不支持的媒体类型。";
                            break;
                        case 429:
                            errorMessage = "请求过多 - 超过了速率限制。";
                            break;
                        case 500:
                            errorMessage = "内部服务器错误。";
                            break;
                    }
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }
            @Override
            public void onFailure(Call<PlantIdentificationResponse> call, Throwable t) {
                Log.e(TAG, "Network failure or error in Retrofit setup.", t);
                callback.onError(t.getMessage());
            }
        });
    }



    public interface PlantIdentificationCallback {
        void onSuccess(PlantIdentificationResponse response);
        void onError(String error);
    }
}

