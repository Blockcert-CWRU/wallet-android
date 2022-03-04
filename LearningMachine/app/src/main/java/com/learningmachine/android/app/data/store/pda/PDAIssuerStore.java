package com.learningmachine.android.app.data.store.pda;

import com.google.common.collect.ImmutableList;
import com.learningmachine.android.app.data.inject.DaggerPDAComponent;
import com.learningmachine.android.app.data.inject.PDAComponent;
import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.data.model.KeyRotation;
import com.learningmachine.android.app.data.store.AbstractIssuerStore;
import com.learningmachine.android.app.data.store.ImageStore;
import com.learningmachine.android.app.data.store.IssuerStore;
import com.learningmachine.android.app.data.store.sql.SQLiteIssuerStore;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

public class PDAIssuerStore extends AbstractIssuerStore {

    private static final PDAComponent COMPONENT = DaggerPDAComponent.builder().build();
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
        return COMPONENT.issuerStoreFactory().create(hatName, authToken);
    }

    @Override
    public void reset() {
        // no-op
    }

    @Override
    public void saveIssuer(IssuerRecord issuer, String recipientPubKey) {
        mStoreService.save(issuer.getUuid(), recipientPubKey, issuer);
    }

    @Override
    public List<IssuerRecord> loadIssuers() {
        return ImmutableList.copyOf(mStoreService.loadAll());
    }

    @Override
    public IssuerRecord loadIssuer(String issuerId) {
        return mStoreService.load(issuerId);
    }

    @Override
    public IssuerRecord loadIssuerForCertificate(String certId) {
        List<IssuerRecord> records = mIndexService.get(mHatName, mAuthToken)
                .records()
                .stream()
                .filter(record -> record.certId().equals(certId))
                .map(PDAIndexRecord::issuerId)
                .map(this::loadIssuer)
                .collect(Collectors.toList());
        checkRecords(records);
        return records.get(0);
    }

    private static void checkRecords(List<IssuerRecord> records) {
        if (records.isEmpty()) {
            throw new NoSuchElementException();
        } else if (records.size() > 1) {
            throw new IllegalStateException("More than 1 record corresponding to certID");
        }
    }

    @Override
    public void saveKeyRotation(KeyRotation keyRotation, String issuerId, String tableName) {
        mKeyStore.saveKeyRotation(keyRotation, issuerId, tableName);
    }

    @Override
    public List<KeyRotation> loadKeyRotations(String issuerId, String tableName) {
        return ImmutableList.copyOf(mKeyStore.loadKeyRotations(issuerId, tableName));
    }
}