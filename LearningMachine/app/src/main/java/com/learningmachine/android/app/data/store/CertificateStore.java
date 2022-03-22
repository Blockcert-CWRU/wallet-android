package com.learningmachine.android.app.data.store;

import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.model.CertificateRecord;

import java.util.List;

import rx.Observable;

public interface CertificateStore extends DataStore {

    Observable<CertificateRecord> load(String certId);

    Observable<List<CertificateRecord>> loadForIssuer(String issuerId);

    Observable<Void> save(BlockCert cert);

    Observable<Boolean> delete(String certId);
}
