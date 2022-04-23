package com.learningmachine.android.app.data.store.pda;

import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.model.CertificateRecord;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface PDACertificateStoreService {

    @GET("wallet/certs")
    Observable<List<CertificateRecord>> load(
            @Header("Content-Type") String contentType,
            @Header("x-auth-token") String authToken
    );

    @POST("wallet/certs/")
    Observable<Void> save(
            @Header("Content-Type") String contentType,
            @Header("x-auth-token") String authToken,
            @Body BlockCert cert);

    @DELETE("/certs")
    Observable<Void> delete(
            @Query("records") String certId,
            @Path("hatName") String hatName,
            @Header("x-auth-token") String authToken);
}