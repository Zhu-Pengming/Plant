package com.tom.npm;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface PlantIdentificationService {



    @Multipart
    @POST("v2/identify/{project}")
    Call<PlantIdentificationResponse> identifyPlant(
            @Path("project") String project,
            @Query("include-related-images") boolean includeRelatedImages,
            @Query("no-reject") boolean noReject,
            @Query("lang") String lang,
            @Query("type") String type,
            @Part List<MultipartBody.Part> images,
            @Part("organs") List<RequestBody> organs,
            @Header("Authorization") String authHeader
    );

}


