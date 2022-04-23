package com.learningmachine.android.app.data.store;

import com.learningmachine.android.app.data.webservice.response.IssuerResponse;

import org.joda.time.DateTime;

import rx.Observable;

public abstract class AbstractIssuerStore implements IssuerStore {

    private final ImageStore mImageStore;

    protected AbstractIssuerStore(ImageStore imageStore) {
        mImageStore = imageStore;
    }

    public Observable<Void> saveResponse(IssuerResponse response, String recipientPubKey) {
        if (response != null) {
            return mImageStore.saveImage(response.getUuid(), response.getImageData())
                    .compose(x -> setIntroducedOnAndSave(response, recipientPubKey))
                    .map(x -> null);
        }
        return Observable.empty();
    }

    private Observable<Void> setIntroducedOnAndSave(IssuerResponse response, String recipientKey) {
        response.setIntroducedOn(DateTime.now().toString());
        return saveRecord(response, recipientKey);
    }
}