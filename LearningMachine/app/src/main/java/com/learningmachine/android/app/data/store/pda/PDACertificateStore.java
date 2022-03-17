package com.learningmachine.android.app.data.store.pda;

import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.data.store.CertificateStore;
import com.learningmachine.android.app.util.ListUtils;

import java.util.List;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

public class PDACertificateStore implements CertificateStore {

//    private static final PDAComponent COMPONENT = DaggerPDAComponent.builder().build();
    private final PDAIndexService mIndexService;
    private final PDACertificateStoreService mStoreService;
    private final String mHatName;
    private final String mAuthToken;

    @AssistedInject
    PDACertificateStore(
            PDAIndexService indexService,
            PDACertificateStoreService storeService,
            @Assisted("hatName") String hatName,
            @Assisted("authToken") String authToken) {
        mIndexService = indexService;
        mStoreService = storeService;
        mHatName = hatName;
        mAuthToken = authToken;
    }

    public static PDACertificateStore create(String hatName, String authToken) {
//        return COMPONENT.certificateStoreFactory().create(hatName, authToken);
        return null;
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
                .map(PDAIndexRecord::certId)
                .map(this::load)
                .collect(ListUtils.toImmutableList());
    }

    @Override
    public void save(BlockCert cert) {
        mStoreService.save(cert, cert.getCertUid(), mHatName, mAuthToken);
    }

    @Override
    public boolean delete(String certId) {
        mStoreService.delete(certId, mHatName, mAuthToken);
        return true;
    }

    @Override
    public void reset() {
        // no-op
    }
}
