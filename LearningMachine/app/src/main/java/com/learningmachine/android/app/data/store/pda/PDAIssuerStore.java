package com.learningmachine.android.app.data.store.pda;

import com.google.common.collect.ImmutableList;
//import com.learningmachine.android.app.data.inject.DaggerPDAComponent;
//import com.learningmachine.android.app.data.inject.PDAComponent;
import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.data.model.KeyRotation;
import com.learningmachine.android.app.data.store.AbstractIssuerStore;
import com.learningmachine.android.app.data.store.ImageStore;
import com.learningmachine.android.app.data.store.IssuerStore;
import com.learningmachine.android.app.data.store.sql.SQLiteIssuerStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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

    private static void checkRecords(Collection<Observable<IssuerRecord>> records) {
        if (records.isEmpty()) {
            throw new NoSuchElementException();
        } else if (records.size() > 1) {
            throw new IllegalStateException("More than 1 record corresponding to certID");
        }
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
        List<Observable<IssuerRecord>> list = mIndexService.get(mHatName, mAuthToken).toBlocking().first()
                .records()
                .stream()
                .filter(record -> record.certId().equals(certId))
                .map(PDAIndexRecord::issuerId)
                .map(this::load)
                .collect(Collectors.toList());

        checkRecords(list);
        return list.get(0);
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