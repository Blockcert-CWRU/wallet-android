package com.learningmachine.android.app.data.store.pda;

import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.model.CertificateRecord;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface PDACertificateStoreService {

    @GET("/certs")
    Observable<List<CertificateRecord>> load(@Header("x-auth-token") String authToken);

    @POST("/certs")
    @Headers({"Content-Type: application/json"})
    Observable<Void> save(@Body BlockCert cert, @Header("x-auth-token") String authToken);

    @DELETE("/certs")
    Observable<Void> delete(
            @QueryMap Map<String, String> records,
            @Path("hatName") String hatName,
            @Header("x-auth-token") String authToken);
}