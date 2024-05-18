package com.example.npm;


import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import retrofit2.http.Query;


public interface SpeciesService {
    @Multipart
    @GET("v2/species")
    Call<JsonObject> listSpecies(
            @Part("image") RequestBody image,
            @Query("lang") String language,
            @Query("type") String type,
            @Query("api-key") String apiKey
    );
}

