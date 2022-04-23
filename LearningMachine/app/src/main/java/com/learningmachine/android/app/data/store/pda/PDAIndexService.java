package com.learningmachine.android.app.data.store.pda;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

public interface PDAIndexService {

    @GET("/index")
    Observable<PDAIndex> get(
            @Path("hatName") String hatName, @Header("x-auth-token") String authToken);

    @POST("/index")
    @Headers({"Content-Type: application/json"})
    Observable<Void> create(
            @Body PDAIndex index,
            @Path("hatName") String hatName,
            @Header("x-auth-token") String authToken);

    @PUT("/index")
    @Headers({"Content-Type: application/json"})
    Observable<Void> update(
            @Body PDAIndex index,
            @Path("hatName") String hatName,
            @Header("x-auth-token") String authToken);

    @DELETE("/index")
    Observable<Void> delete(
            @Path("hatName") String hatName, @Header("x-auth-token") String authToken);
}
