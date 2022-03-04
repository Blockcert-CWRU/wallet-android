package com.learningmachine.android.app.data.store.pda;

import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.inject.PdaStoreComponent;
import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.data.store.CertificateStore;
import com.learningmachine.android.app.util.ListUtils;

import java.util.List;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

public class PdaCertificateStore implements CertificateStore {

    private static final PdaStoreComponent COMPONENT = DaggerPdaStoreComponent.create();
    private final PdaIndexService mIndexService;
    private final PdaCertificateStoreService mStoreService;
    private final String mHatName;
    private final String mAuthToken;

    @AssistedInject
    PdaCertificateStore(
            PdaIndexService indexService,
            PdaCertificateStoreService storeService,
            @Assisted("hatName") String hatName,
            @Assisted("authToken") String authToken) {
        mIndexService = indexService;
        mStoreService = storeService;
        mHatName = hatName;
        mAuthToken = authToken;
    }

    public static PdaCertificateStore create(String hatName, String authToken) {
        return COMPONENT.getCertFactory().create(hatName, authToken);
    }

    @Override
    public CertificateRecord load(String certId) {
        return mStoreService.load(certId, mHatName, mAuthToken);
    }

    @Override
    public List<CertificateRecord> loadForIssuer(String issuerId) {
        return mIndexService.get(mHatName, mAuthToken)
                .records()
                .stream()
                .filter(record -> record.issuerId().equals(issuerId))
                .map(IndexRecord::certId)
                .map(this::load)
                .collect(ListUtils.toImmutableList());
    }

    @Override
    public void save(BlockCert cert) {
        mStoreService.save(cert.getCertUid(), cert);
    }

    @Override
    public boolean delete(String certId) {
        mStoreService.delete(certId);
        return true;
    }

    @Override
    public void reset() {
        // no-op
    }
}
