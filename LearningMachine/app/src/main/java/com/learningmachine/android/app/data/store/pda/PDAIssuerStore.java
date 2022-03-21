package com.learningmachine.android.app.data.store.pda;

import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.data.model.KeyRotation;
import com.learningmachine.android.app.data.store.AbstractIssuerStore;
import com.learningmachine.android.app.data.store.ImageStore;
import com.learningmachine.android.app.data.store.IssuerStore;
import com.learningmachine.android.app.data.store.sql.SQLiteIssuerStore;

import java.util.List;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import rx.Observable;

public class PDAIssuerStore extends AbstractIssuerStore {

    //    private static final PDAComponent COMPONENT = DaggerPDAComponent.builder().build();
    private final PDAIssuerStoreService mStoreService;
    private final PDAIndexService mIndexService;
    private final IssuerStore mKeyStore;
    private final String mHatName;
    private final String mAuthToken;

    @AssistedInject
    PDAIssuerStore(
            PDAIssuerStoreService storeService,
            PDAIndexService indexService,
            ImageStore imageStore,
            SQLiteIssuerStore keyStore,
            @Assisted("hatName") String hatName,
            @Assisted("authToken") String authToken) {
        super(imageStore);
        mStoreService = storeService;
        mIndexService = indexService;
        mHatName = hatName;
        mAuthToken = authToken;
        mKeyStore = keyStore;
    }

    public static PDAIssuerStore create(String hatName, String authToken) {
//        return COMPONENT.issuerStoreFactory().create(hatName, authToken);
        return null;
    }

    @Override
    public void reset() {
        // no-op
    }

    @Override
    public void saveRecord(IssuerRecord record, String recipientPubKey) {
        mStoreService.save(record, record.getUuid(), recipientPubKey, mHatName, mAuthToken);
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
    public void saveKeyRotation(KeyRotation keyRotation, String issuerId, String tableName) {
        mKeyStore.saveKeyRotation(keyRotation, issuerId, tableName);
    }

    @Override
    public Observable<List<KeyRotation>> loadKeyRotations(String issuerId, String tableName) {
        return mKeyStore.loadKeyRotations(issuerId, tableName);
    }
}