package com.learningmachine.android.app.data.store.pda;

import com.learningmachine.android.app.data.inject.PdaStoreComponent;
import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.data.model.KeyRotation;
import com.learningmachine.android.app.data.store.AbstractIssuerStore;
import com.learningmachine.android.app.data.store.CertificateStore;
import com.learningmachine.android.app.data.store.ImageStore;
import com.learningmachine.android.app.data.store.sql.SQLiteIssuerStore;
import com.learningmachine.android.app.data.store.pda.AbstractIssuerStore;
import com.learningmachine.android.app.util.ListUtils;

import java.util.List;
import java.util.NoSuchElementException;

import javax.inject.Singleton;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@Singleton
public class PDAIssuerStore extends AbstractIssuerStore {

    private static final PdaStoreComponent COMPONENT = DaggerPdaStoreComponent.create();
    private final PdaIssuerStoreService missuerStoreService;
    private final PdaIndexService mIndexService;
    private final String mHatName;
    private final String mAuthToken;
    private final SQLiteIssuerStore sQLiteIssuerStore;

    @AssistedInject
    PDAIssuerStore(
            PdaIssuerStoreService missuerStoreService,
            PdaIndexService mIndexService,
            @Assisted("hatName") String hatName,
            @Assisted("authToken") String authToken,
            ImageStore imageStore,
            SQLiteIssuerStore sQLiteIssuerStore ) {

        super(imageStore);
        this.missuerStoreService = missuerStoreService;
        this.mIndexService = mIndexService;
        this.mHatName = hatName;
        this.mAuthToken = authToken;
        this.sQLiteIssuerStore = sQLiteIssuerStore;
    }


    @Override
    public void reset() {

    }

    @Override
    public void saveIssuer(IssuerRecord issuer, String recipientPubKey) {
        missuerStoreService.save(issuer.getUuid(), recipientPubKey, issuer);
    }

    @Override
    public List<IssuerRecord> loadIssuers() {
        return missuerStoreService.loadAll();
    }

    @Override
    public IssuerRecord loadIssuer(String issuerId) {
        return missuerStoreService.load(issuerId);
    }

    public static CertificateStore createPDAIssuerStore(){
        return COMPONENT.getPDAIssuerStoreFactory().create(mHatName, mAuthToken);
    }

    @Override
    public IssuerRecord loadIssuerForCertificate(String certId) {
        List<IssuerRecord> issuerRecordList = mIndexService.get(mHatName, mAuthToken)
                                                .records()
                                                .stream()
                                                .filter(record -> record.certId().equals(certId))
                                                .map(IndexRecord::issuerId)
                                                .map(this::loadIssuer)
                                                .collect(ListUtils.toImmutableList());

        if(issuerRecordList.isEmpty()) {
            throw new NoSuchElementException();
        }
        else if (issuerRecordList.size() > 1){
            throw new IllegalStateException("More than 1 record corresponding to certID");
        }
        return issuerRecordList.get(0);
    }

    @Override
    public void saveKeyRotation(KeyRotation keyRotation, String issuerId, String tableName) {
        sQLiteIssuerStore.saveKeyRotation(keyRotation, issuerId, tableName);
    }

    @Override
    public List<KeyRotation> loadKeyRotations(String issuerId, String tableName) {
        return sQLiteIssuerStore.loadKeyRotations(issuerId, tableName);
    }
}