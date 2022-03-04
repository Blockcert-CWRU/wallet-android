package com.learningmachine.android.app.data.store.pda;

import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.model.CertificateRecord;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PDACertificateStoreService {

    @GET("/certs/{certId}")
    CertificateRecord load(
            @Path("certId") String certId,
            @Path("hatName") String hatName,
            @Header("x-auth-token") String authToken);

    @PUT("/certs/{certId}")
    void save(
            @Body BlockCert cert,
            @Path("certId") String certId,
            @Path("hatName") String hatName,
            @Header("x-auth-token") String authToken);

    @DELETE("/certs")
    void delete(
            @Query("records") String certId,
            @Path("hatName") String hatName,
            @Header("x-auth-token") String authToken);
}
