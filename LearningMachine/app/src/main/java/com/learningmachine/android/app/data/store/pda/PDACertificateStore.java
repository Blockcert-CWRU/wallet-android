package com.learningmachine.android.app.data.store.pda;

import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.data.store.CertificateStore;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import rx.Observable;
import timber.log.Timber;

@Singleton
public class PDACertificateStore implements CertificateStore {

    //    private static final PDAComponent COMPONENT = DaggerPDAComponent.builder().build();
    private final PDAIndexService mIndexService;
    private final PDACertificateStoreService mStoreService;
    private final String mHatName = null;
    private final String mAuthToken;
    private final String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxLXF6cEJDQ1YrVEhcL29CTHJjU3J2UTUrdFJpaTBGVkFxVmwrY0FYcEU5Y01QdHNXV2pVVHBUNTN3VHl5U09OamNaMG82NlNlaG1sZ2ZCejZ0Mm9LbjZxenlVXC9HUzErZkJFK0xnWmhBYXpCZkRDell2aFgzdFwvODhhQUZFRVhqUzZkaUE9PSIsInJlc291cmNlIjoiaWFtaXNoYWFuZ3VwdGEuaHVib2ZhbGx0aGluZ3MubmV0IiwiYWNjZXNzU2NvcGUiOiJvd25lciIsImlzcyI6ImlhbWlzaGFhbmd1cHRhLmh1Ym9mYWxsdGhpbmdzLm5ldCIsImV4cCI6MTY1MzAwNzA5MSwiaWF0IjoxNjUwNDE1MDkxLCJqdGkiOiJlMWYwZTYxNzNiMzIzM2RmNDhhNzIxMzg3N2RlNzU5ZWY4NmFhM2NkNDJhZWJiMzA2NTliMjJmMTBhNTE3Y2EwYTI1MGU5ODAwMWFmYjQ1NWZiYzUzZDk3NDZmYjU0M2FhMTZjMGMxZmU1NGFhOTdmMDAxNDM0M2JjNjhlYTcyMzcwZmExZmQ3NTI5ZjA3YTRkZmNiOTNmNTkzNDA5YjBkODJhMTBmMTNiYTczYTMzNTViYjVkYjQ5NWYyODM0N2NiMzU5MTdiZmU0OWJiMzVlMzVjZDhkZDhlOTA2ZDYyOTVmOWJiNmE2OTUwMzg4NmU5ZWIwNDQ1NmY0Yzg1ODllIn0.NlSJ8vzZvsSiqinvxKeZOp2PNeNjjGe0kgCFSbTguj7Me86RKXjDbEDMqMcbRH5hffEWTvUiauGm5tjhrC3HOJc6ZfE_lkgRS-dVT76tx3uadnRnLAWwFFlz3KyAAaNJvLTe51nZXxzW_LYGx0qSqRJxXNumCaAiOFCEGprkGLI_8rXTRnFDxWvv6iIBogcO1YYUxfNN4oK_iDXboHx-DrvjrdUbcTNKkDCvKGmmuNb_mNBVUqaB9u6b_DfyBboCmUt5I_-oUGZCoiLUqI0bjyfOcXRLwCPEyg3z7i-H1sJlV02Is8RuL4zLoKawkV9Ht7JqIOVxwNNhm11Uraoy5A";
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
        System.out.println("//////////////////////////");
        Observable<List<CertificateRecord>> certificateRecordObservable = mStoreService.load(contentType, authToken);
//        System.out.println(certificateRecordObservable.toBlocking().first());
        System.out.println("//////////////////////////");
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
        System.out.println("=========================================================");
        Timber.i("PDA CertificateStore: Save method invoked");
        System.out.println("=========================================================");
        return mStoreService.save(contentType, authToken, cert);
    }

    @Override
    public Observable<Boolean> delete(String certId) {
        return mStoreService.delete(certId, mHatName, authToken).map(x -> true);
    }
}
