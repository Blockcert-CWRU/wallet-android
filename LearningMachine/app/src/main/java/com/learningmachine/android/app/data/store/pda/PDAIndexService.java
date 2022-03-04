package com.learningmachine.android.app.data.store.pda;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PDAIndexService {

    @GET("/index")
    PDAIndex get(@Path("hatName") String hatName, @Header("x-auth-token") String authToken);

    @POST("/index")
    void create(
            @Body PDAIndex index,
            @Path("hatName") String hatName,
            @Header("x-auth-token") String authToken);

    @PUT("/index")
    void update(
            @Body PDAIndex index,
            @Path("hatName") String hatName,
            @Header("x-auth-token") String authToken);

    @DELETE("/index")
    void delete(@Path("hatName") String hatName, @Header("x-auth-token") String authToken);
}
