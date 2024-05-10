package com.example.npm;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecognitionService {
    private static final String BASE_URL = "https://my-api.plantnet.org/"; // Use the actual base URL
    private SpeciesService speciesService;

    private String APIKey;

    public RecognitionService(String apiKey) {
        APIKey = apiKey;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        speciesService = retrofit.create(SpeciesService.class);
    }

    public Call<JsonObject> getSpecies(String language, String type) {
        return speciesService.listSpecies(language, type, APIKey);
    }
}