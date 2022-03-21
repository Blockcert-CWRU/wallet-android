package com.learningmachine.android.app.data.store.pda;

import com.learningmachine.android.app.data.cert.BlockCert;
//import com.learningmachine.android.app.data.inject.DaggerPDAComponent;
//import com.learningmachine.android.app.data.inject.PDAComponent;
import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.data.store.CertificateStore;
import com.learningmachine.android.app.util.ListUtils;

import java.util.ArrayList;
import java.util.List;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import rx.Observable;

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
    public Observable<CertificateRecord> load(String certId) {
        return mStoreService.load(certId, mHatName, mAuthToken);
    }

    @Override
    public Observable<List<CertificateRecord>> loadForIssuer(String issuerId) {
        List<Observable<CertificateRecord>> list =
                mIndexService.get(mHatName, mAuthToken).toBlocking().first()
                .records()
                .stream()
                .filter(record -> record.issuerId().equals(issuerId))
                .map(PDAIndexRecord::certId)
                .map(this::load)
                .collect(ListUtils.toImmutableList());

        List<CertificateRecord> records = new ArrayList<>();
        for (Observable<CertificateRecord> recordObservable :list) {
            records.add(recordObservable.toBlocking().first());
        }

        return Observable.just(records);
    }

    @Override
    public void save(BlockCert cert) {
        mStoreService.save(cert, cert.getCertUid(), mHatName, mAuthToken);
    }

    @Override
    public Observable<Boolean> delete(String certId) {
        mStoreService.delete(certId, mHatName, mAuthToken);
        return Observable.just(true);
    }

    @Override
    public void reset() {
        // no-op
    }
}
