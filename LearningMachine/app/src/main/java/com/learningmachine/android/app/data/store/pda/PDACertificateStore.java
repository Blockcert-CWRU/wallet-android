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
    private final String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxLVwvdzNrUnBaUDRDN2FhSFQxZ01pZzFpUzU1T1VmVldYdDI2bDVFMEdBSUR3K1FIN25PRDBuVWlyazhwWGlFNnpWZmJ2eFdVWlwvV1gzbjc5emRQV0dGQzNrdXdxTkI0Tm5Ld2p2Yzc3SFpzMFhRUGlud2ZwU25DaitxcXljM2Vnb0IwZz09IiwicmVzb3VyY2UiOiJpYW1pc2hhYW5ndXB0YS5odWJvZmFsbHRoaW5ncy5uZXQiLCJhY2Nlc3NTY29wZSI6Im93bmVyIiwiaXNzIjoiaWFtaXNoYWFuZ3VwdGEuaHVib2ZhbGx0aGluZ3MubmV0IiwiZXhwIjoxNjYyNTc3NDczLCJpYXQiOjE2NTk5ODU0NzMsImp0aSI6IjAwYWU5YTA3MzUyMjFkMGI1OTE0NjRjZDkzMWM3MjkwOGJlYjNhMDJkMmEzZjZiY2E1NTNlNGQyYzdhNzM5M2Q1MTE4YzVhYWMzYjhlMjdlOWE1ZTdhZTZmZWJjNWExZTBiNWZiMjJiZjdmYjJkYjY3ZWVhZjExNzgzNDhiYThmMTJkMGE1ZTI1MTM4MDQ2MjdlMTYyMGFkMTM3MzgwZDdhNTdkYTM2YjA0NjM3NGI1NDc3NGM0NmRlYjU0ZGRkZDA1OTNkMTYwZGIyNjhkMjM1MWY3ZWRkMmQxMTk3Y2U3NGM0MTdlMTE0NjVhZjQwZmI5YjkxYjRlNzk5YjdjZmMifQ.gbMtr9SlL6M6flT4qjbsIP4ChVSv4uRmBMjMBHFvsfZGO3yxg5vNRkUg_8G1vUIL1oGCV705C5BfR7evgBqiDKMr3yY2wU9hKah4U072ZVl-uIFYh_c_NbqYLMoVnoEZ7Zapbe90l9KEshyI5Nl1Gvw3ShMep9vPofmR4S_SFMUpZ-Uisd_HQuhRCLBUHCrxwkssxmQhXGXMqDJ1Pp1zMT2JDLdGqokfKVGmnP0cpYeyaIT13NaUSD_BD8DL3cGz1ab97BbzcHGIHtPWaD_armTjU27iHYdSsVNgXkgkDudcb-FivIpMXfZqs_hk7jP55cL4YobaQyKNwOxW5JiMUA";
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