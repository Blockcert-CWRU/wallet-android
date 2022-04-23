package com.learningmachine.android.app.data.store.pda;

import com.learningmachine.android.app.data.model.IssuerRecord;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

public interface PDAIssuerStoreService {

    @GET("/issuerRecords/{issuerId}")
    Observable<IssuerRecord> load(
            @Path("issuerId") String issuerId,
            @Path("hatName") String hatName,
            @Header("x-auth-token") String authToken);

    @GET("/issuerRecords")
    Observable<List<IssuerRecord>> loadAll(
            @Path("hatName") String hatName,
            @Header("x-auth-token") String authToken);

    @PUT("/issuerRecords/{issuerId}")
    Observable<Void> save(
            @Body IssuerRecord record,
            @Path("issuerId") String issuerId,
            String recipientPubKey,
            @Path("hatName") String hatName,
            @Header("x-auth-token") String authToken);
}
