package com.learningmachine.android.app.data.store.pda;

import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.data.model.IssuerRecord;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PdaIssuerStoreService {

    @GET("/issuerRecords/{issuerId}")
    IssuerRecord load(
            @Path("issuerId") String issuerId);

    @GET("/issuerRecords")
    List<IssuerRecord> loadAll();

    @PUT("/issuerRecords/{issuerId}")
    void save(@Path("issuerId") String issuerId, String recipientPubKey);

    @DELETE("/certs")
    void delete(@Query("records") String certId);

}
