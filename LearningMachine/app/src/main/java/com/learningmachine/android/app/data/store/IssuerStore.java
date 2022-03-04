package com.learningmachine.android.app.data.store;

import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.data.model.KeyRotation;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;

import java.util.List;

public interface IssuerStore extends DataStore {

    void saveResponse(IssuerResponse response, String recipientPubKey);


    void saveRecord(IssuerRecord record, String recipientPubKey);

    List<IssuerRecord> loadAll();

    IssuerRecord load(String issuerId);

    IssuerRecord loadForCertificate(String certId);


    void saveKeyRotation(KeyRotation keyRotation, String issuerId, String tableName);

    List<KeyRotation> loadKeyRotations(String issuerId, String tableName);
}
