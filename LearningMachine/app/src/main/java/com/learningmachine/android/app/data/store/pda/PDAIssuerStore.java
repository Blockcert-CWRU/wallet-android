package com.learningmachine.android.app.data.store.pda;

import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.data.model.KeyRotation;
import com.learningmachine.android.app.data.store.AbstractIssuerStore;
import com.learningmachine.android.app.data.store.ImageStore;
import com.learningmachine.android.app.data.store.IssuerStore;
import com.learningmachine.android.app.data.store.sql.SQLiteIssuerStore;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;

public class PDAIssuerStore extends AbstractIssuerStore {

    private final PDAIssuerStoreService mStoreService;
    private final PDAIndexService mIndexService;
    private final IssuerStore mKeyStore;
    private final String mHatName;
    private final String mAuthToken;

    @Inject
    PDAIssuerStore(
            PDAIssuerStoreService storeService,
            PDAIndexService indexService,
            ImageStore imageStore,
            SQLiteIssuerStore keyStore,
            @Named("hatName") String hatName,
            @Named("authToken") String authToken) {
        super(imageStore);
        mStoreService = storeService;
        mIndexService = indexService;
        mHatName = hatName;
        mAuthToken = authToken;
        mKeyStore = keyStore;
    }

    @Override
    public Observable<Void> saveRecord(IssuerRecord record, String recipientPubKey) {
        return mStoreService.save(record, record.getUuid(), recipientPubKey, mHatName, mAuthToken);
    }

    @Override
    public Observable<List<IssuerRecord>> loadAll() {
        return mStoreService.loadAll(mHatName, mAuthToken);
    }

    @Override
    public Observable<IssuerRecord> load(String issuerId) {
        return mStoreService.load(issuerId, mHatName, mAuthToken);
    }

    @Override
    public Observable<IssuerRecord> loadForCertificate(String certId) {
        return mIndexService.get(mHatName, mAuthToken)
                .map(PDAIndex::records)
                .flatMap(Observable::from)
                .filter(record -> record.certId().equals(certId))
                .map(PDAIndexRecord::issuerId)
                .map(this::load)
                .toBlocking()
                .first();
    }

    @Override
    public Observable<Void> saveKeyRotation(
            KeyRotation keyRotation, String issuerId, String tableName) {
        return mKeyStore.saveKeyRotation(keyRotation, issuerId, tableName);
    }

    @Override
    public Observable<List<KeyRotation>> loadKeyRotations(String issuerId, String tableName) {
        return mKeyStore.loadKeyRotations(issuerId, tableName);
    }
}