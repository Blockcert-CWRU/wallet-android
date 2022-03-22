package com.learningmachine.android.app.data.store;

import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.data.model.KeyRotation;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;

import java.util.List;

import rx.Observable;

public interface IssuerStore extends DataStore {

    Observable<Void> saveResponse(IssuerResponse response, String recipientPubKey);

    Observable<Void> saveRecord(IssuerRecord record, String recipientPubKey);

    Observable<List<IssuerRecord>> loadAll();

    Observable<IssuerRecord> load(String issuerId);

    Observable<IssuerRecord> loadForCertificate(String certId);

    Observable<Void> saveKeyRotation(KeyRotation keyRotation, String issuerId, String tableName);

    Observable<List<KeyRotation>> loadKeyRotations(String issuerId, String tableName);
}
