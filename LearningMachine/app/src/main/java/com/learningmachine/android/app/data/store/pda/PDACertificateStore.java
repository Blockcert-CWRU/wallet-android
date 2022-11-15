package com.learningmachine.android.app.data.store.pda;

import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.data.store.CertificateStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import retrofit2.HttpException;
import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

@Singleton
public class PDACertificateStore implements CertificateStore {

    //    private static final PDAComponent COMPONENT = DaggerPDAComponent.builder().build();
    private final PDAIndexService mIndexService;
    private final PDACertificateStoreService mStoreService;
    private final String mHatName = null;
    private final String mAuthToken;
    private final String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxLThTUHBheVpUMkkreEg1cmNKNkVwdE1RT1U1aTd5YmxnbUVQbUh5NFwvbHh6aWJNR0FFcHlZK3BwcG1YbWZpNjl5cWdQQkR1MEVEQk01WFMrNFB3ZmFBTnVVa216dDlvaFBtZlRMNEdBQTFRcE54NU9LR3d1ZEROdWtaZTdiUDBwbmR3PT0iLCJyZXNvdXJjZSI6ImlhbWlzaGFhbmd1cHRhLmh1Ym9mYWxsdGhpbmdzLm5ldCIsImFjY2Vzc1Njb3BlIjoib3duZXIiLCJpc3MiOiJpYW1pc2hhYW5ndXB0YS5odWJvZmFsbHRoaW5ncy5uZXQiLCJleHAiOjE2NTM1MTI0MzgsImlhdCI6MTY1MDkyMDQzOCwianRpIjoiM2M4NzM2N2VjNTIxMTUwMzRkNzUwNzkzYTAyYTdmNTRiMDBhZThhOWI0NDg0NjZmZTQyNjRlNjlmMTBiMGJiMWQwMzBiZTM0Y2JmMDliNjA0YjE2M2U4Nzk5Y2IzMDE0ZGU0OTEwNzQ2MjMwYWZmYTc3NDlkMDg0ZjdhZDM2ZmM0ZTkwYWU3MmQxMWUwYTEwNGUyMTJkYjJjNzljOWI0M2I4Yzc1MmZjOGU2NTQ5YzUyZDA0ODRlYzdkZGVmNTU3NGFjOGJlOGRjYmMzODA5ZmRjMDJlNDIxZTZlMjMxODlhNzBhNDJmYjcwMjZiMjk2YTZkOGI3MGIwOTdmZDZjNyJ9.baKs4lFA4cR8l3aXvhn6fxxW968y55WVPoa0ZcfR8rfyXub0NSNEGSE9_6u-Cdr9yjTn-GeNL5ZVE6_dQCPyrd6mPNTSAeG1TB6Ltj-LW_bnO98sViB16qSne3XF9yDOx5-r3gqwepjkrgP3Sq5QINtaXBnezGwrwqIMy1TVgq2zABFU01RFBtnxhPsb8gZ6aumJMrkF2CrrFl8PlXZ1Mi5m6LK-RllG0FQwUc4t5K2ULQ0LgyjDFRl08iYcCZZSgvLY5vCnkVSOptq0tAmYuvMs2Mh5Dm67Cll71e5_pny6gmSMf_dty8iykrenO_eOj9HAjU8RUAvIz9726vYwig";
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
        Observable<Void> r = mStoreService.save(contentType, authToken, cert);
        r.subscribe(new Subscriber<Void>() {
            @Override
            public void onCompleted() {
                Timber.i("completed");
            }

            @Override
            public void onError(Throwable e) {
                String err = null;
                try {
                    err = ((HttpException)e).response().errorBody().string();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                Timber.i("err: " + err);
            }

            @Override
            public void onNext(Void myResponseObject) {
                Timber.i("next");
            }
        });
        return r;
    }

    @Override
    public Observable<Boolean> delete(String certId) {
        return mStoreService.delete(certId, mHatName, authToken).map(x -> true);
    }
}
