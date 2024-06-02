package com.example.npm;

import android.graphics.Bitmap;

import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecognitionService {
    private static final String BASE_URL = "https://my-api.plantnet.org/"; // Use the actual base URL
    private SpeciesService speciesService;

    private String APIKey = "2b10MglNpUK4UBkH5myWDPWe";

    public RecognitionService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        speciesService = retrofit.create(SpeciesService.class);
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    public Call<JsonObject> getSpeciesWithImage(Bitmap bitmap, String language, String type) {
        byte[] imageData = convertBitmapToByteArray(bitmap);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), imageData);
        return speciesService.listSpecies(requestBody, language, type, APIKey);
    }
}