package com.example.npm;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SpeciesService {
    @GET("v2/species")
    Call<JsonObject> listSpecies(@Query("lang") String language, @Query("type") String type, @Query("api-key") String apiKey);
}

