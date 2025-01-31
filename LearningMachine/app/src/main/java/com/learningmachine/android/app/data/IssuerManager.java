package com.learningmachine.android.app.data;

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
    private final GsonUtil mGsonUtil;

    @Inject
    public IssuerManager(IssuerStore issuerStore, IssuerService issuerService, GsonUtil gsonUtil) {
        mIssuerStore = issuerStore;
        mIssuerService = issuerService;
        mGsonUtil = gsonUtil;
    }

    public Observable<Void> loadSampleIssuer() {
        try {
            Timber.d("Loading Sample Issuer");
            IssuerResponse issuerResponse = mGsonUtil.loadModelObject("sample-issuer", IssuerResponse.class);
            return mIssuerStore.saveResponse(issuerResponse, null);
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
                .compose(x -> saveIssuer(issuer, request.getBitcoinAddress()));
    }

    public Observable<String> saveIssuer(IssuerResponse issuer, String recipientPubKey) {
        return mIssuerStore.saveResponse(issuer, recipientPubKey).map(x -> issuer.getUuid());
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
            String issuerAnalyticsUrlString = "www.fakeissuerurl.com";//issuer.getAnalyticsUrlString();
            if (StringUtils.isEmpty(issuerAnalyticsUrlString)) {
                return Observable.error(new IssuerAnalyticsException());
            }
            IssuerAnalytic issuerAnalytic = new IssuerAnalytic(certUuid, action);
            return Observable.empty();//mIssuerService.postIssuerAnalytics(issuerAnalyticsUrlString, issuerAnalytic);
        });
    }
}
