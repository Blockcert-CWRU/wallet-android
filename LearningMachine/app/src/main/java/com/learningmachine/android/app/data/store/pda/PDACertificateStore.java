package com.learningmachine.android.app.data.store.pda;

import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.data.store.CertificateStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import rx.Observable;
import timber.log.Timber;

@Singleton
public class PDACertificateStore implements CertificateStore {

    private final PDAIndexService mIndexService;
    private final PDACertificateStoreService mStoreService;
    private final String mHatName;
    private final String mAuthToken;

    @Inject
    PDACertificateStore(
            PDAIndexService indexService,
            PDACertificateStoreService storeService,
            @Named("hatName") String hatName,
            @Named("authToken") String authToken) {
        mIndexService = indexService;
        mStoreService = storeService;
        mHatName = hatName;
        mAuthToken = authToken;
    }

    @Override
    public Observable<CertificateRecord> load(String certId) {
        // TODO This should use the PDA index -- the first cert isn't necessary the right one
        return Observable.just(mStoreService.load(mAuthToken).toBlocking().first().get(0));
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
        Timber.i("PDA CertificateStore: Save method invoked");
        return mStoreService.save(cert, mAuthToken);
    }

    @Override
    public Observable<Boolean> delete(String certId) {
        Map<String, String> delete = PdaUtil.deleteQuery(certId);
        return mStoreService.delete(delete, mHatName, mAuthToken).map(x -> true);
    }
}
