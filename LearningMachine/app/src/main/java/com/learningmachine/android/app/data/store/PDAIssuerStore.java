package com.learningmachine.android.app.data.store;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.learningmachine.android.app.data.IssuerManager;
import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.data.model.KeyRotation;
import com.learningmachine.android.app.data.store.cursor.IssuerCursorWrapper;
import com.learningmachine.android.app.data.store.pda.IndexRecord;
import com.learningmachine.android.app.data.store.pda.PdaCertificateStoreService;
import com.learningmachine.android.app.data.store.pda.PdaIndexService;
import com.learningmachine.android.app.data.store.pda.PdaIssuerStoreService;
import com.learningmachine.android.app.data.webservice.IssuerService;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;
import com.learningmachine.android.app.util.ListUtils;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import rx.Observable;

public class PDAIssuerStore implements IssuerStore {

    private PdaIssuerStoreService missuerStoreService;
    private final PdaIndexService mIndexService;
    private final String mHatName;
    private final String mAuthToken;


    @AssistedInject
    PDAIssuerStore(
            PdaIssuerStoreService missuerStoreService,
            PdaIndexService mIndexService,
            @Assisted("hatName") String hatName,
            @Assisted("authToken") String authToken) {
        this.missuerStoreService = missuerStoreService;
        this.mIndexService = mIndexService;
        this.mHatName = hatName;
        this.mAuthToken = authToken;
    }


    @Override
    public void reset() {

    }

    @Override
    public void saveIssuerResponse(IssuerResponse issuerResponse, String recipientPubKey) {
//        missuerStoreService.save(issuerResponse.getUuid(), recipientPubKey);
    }

    @Override
    public void saveIssuer(IssuerRecord issuer, String recipientPubKey) {
        missuerStoreService.save(issuer.getUuid(), recipientPubKey);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public List<IssuerRecord> loadIssuers() {
        return missuerStoreService.loadAll();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public IssuerRecord loadIssuer(String issuerId) {
        return missuerStoreService.load(issuerId);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public IssuerRecord loadIssuerForCertificate(String certId) {
        return mIndexService.get(mHatName, mAuthToken)
                                                .records()
                                                .stream()
                                                .filter(record -> record.certId().equals(certId))
                                                .map(IndexRecord::issuerId)
                                                .map(this::loadIssuer)
                                                .collect(ListUtils.toImmutableList())
                                                .get(0);
    }

    @Override
    public void saveKeyRotation(KeyRotation keyRotation, String issuerId, String tableName) {

    }

    @Override
    public List<KeyRotation> loadKeyRotations(String issuerId, String tableName) {
        return null;
    }
}
