package com.learningmachine.android.app.data;

import android.content.Context;

import com.learningmachine.android.app.data.error.IssuerAnalyticsException;
import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.data.store.IssuerStore;
import com.learningmachine.android.app.data.webservice.IssuerService;
import com.learningmachine.android.app.data.webservice.request.IssuerAnalytic;
import com.learningmachine.android.app.data.webservice.request.IssuerIntroductionRequest;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;
import com.learningmachine.android.app.util.GsonUtil;
import com.learningmachine.android.app.util.StringUtils;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import timber.log.Timber;

@Singleton
public class IssuerManager {

    private final IssuerStore mIssuerStore;
    private final IssuerService mIssuerService;

    @Inject
    public IssuerManager(IssuerStore issuerStore, IssuerService issuerService) {
        mIssuerStore = issuerStore;
        mIssuerService = issuerService;
    }

    public Observable<Void> loadSampleIssuer(Context context) {
        try {
            GsonUtil gsonUtil = new GsonUtil(context);
            IssuerResponse issuerResponse = gsonUtil.loadModelObject("sample-issuer", IssuerResponse.class);
            mIssuerStore.saveResponse(issuerResponse, null);
            return Observable.just(null);
        } catch (IOException e) {
            Timber.e(e, "Unable to load Sample Issuer");
            return Observable.error(e);
        }
    }

    public Observable<IssuerRecord> getIssuer(String issuerUuid) {
        return mIssuerStore.load(issuerUuid);
    }

    public Observable<IssuerRecord> getIssuerForCertificate(String certUuid) {
        return mIssuerStore.loadForCertificate(certUuid);
    }

    public Observable<List<IssuerRecord>> getIssuers() {
        return mIssuerStore.loadAll();
    }

    public Observable<IssuerResponse> fetchIssuer(String url) {
        return mIssuerService.getIssuer(url);
    }

    public Observable<String> addIssuer(IssuerIntroductionRequest request) {
        IssuerResponse issuer = request.getIssuerResponse();
        return mIssuerService.postIntroduction(issuer.getIntroUrl(), request)
                .map(aVoid -> saveIssuer(issuer, request.getBitcoinAddress()));
    }

    public String saveIssuer(IssuerResponse issuer, String recipientPubKey) {
        mIssuerStore.saveResponse(issuer, recipientPubKey);
        return issuer.getUuid();
    }

    public Observable<Void> certificateViewed(String certUuid) {
        return sendAnalyticsAction(certUuid, IssuerAnalytic.Action.VIEWED);
    }

    public Observable<Void> certificateVerified(String certUuid) {
        return sendAnalyticsAction(certUuid, IssuerAnalytic.Action.VERIFIED);
    }

    public Observable<Void> certificateShared(String certUuid) {
        return sendAnalyticsAction(certUuid, IssuerAnalytic.Action.SHARED);
    }

    private Observable<Void> sendAnalyticsAction(String certUuid, IssuerAnalytic.Action action) {
        return getIssuerForCertificate(certUuid).flatMap(issuer -> {
            String issuerAnalyticsUrlString = issuer.getAnalyticsUrlString();
            if (StringUtils.isEmpty(issuerAnalyticsUrlString)) {
                return Observable.error(new IssuerAnalyticsException());
            }
            IssuerAnalytic issuerAnalytic = new IssuerAnalytic(certUuid, action);
            return mIssuerService.postIssuerAnalytics(issuerAnalyticsUrlString, issuerAnalytic);
        });
    }
}
