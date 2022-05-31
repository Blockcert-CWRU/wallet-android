package com.learningmachine.android.app.data.store.pda;

import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.data.store.CertificateStore;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class PDACertificateStore implements CertificateStore {

    private final PDAIndexService mIndexService;
    private final PDACertificateStoreService mStoreService;
    private final String mHatName = null;
    private final String mAuthToken;
    private final String authToken = "Enter Access Token for PDA";
    private final String contentType = "application/json";

    @Inject
    PDACertificateStore(
            PDAIndexService indexService,
            PDACertificateStoreService storeService
            ) {
        mIndexService = indexService;
        mStoreService = storeService;       
        mAuthToken = authToken;
    }

    @Override
    public Observable<CertificateRecord> load(String certId) {
        Observable<List<CertificateRecord>> certificateRecordObservable = mStoreService.load(contentType, authToken);
        return Observable.just(certificateRecordObservable.toBlocking().first().get(0));//certificateRecordObservable.map(records -> records.stream().filter(r -> r.getName().equals(certId)).findFirst().get());
    }

    @Override
    public Observable<List<CertificateRecord>> loadForIssuer(String issuerId) {
        return mIndexService.get(mHatName, mAuthToken)
                .map(PDAIndex::records)
                .flatMap(Observable::from)
                .filter(record -> record.issuerId().equals(issuerId))
                .map(PDAIndexRecord::certId)
                .map(this::load)
                .collect(ArrayList::new, (records, obs) -> records.add(obs.toBlocking().first()));
    }

    @Override
    public Observable<Void> save(BlockCert cert) {
        return mStoreService.save(contentType, authToken, cert);
    }

    @Override
    public Observable<Boolean> delete(String certId) {
        return mStoreService.delete(certId, mHatName, authToken).map(x -> true);
    }
}