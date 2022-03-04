package com.learningmachine.android.app.data.store;

import com.learningmachine.android.app.data.webservice.response.IssuerResponse;

import org.joda.time.DateTime;

public abstract class AbstractIssuerStore implements IssuerStore {

    private final ImageStore mImageStore;

    protected AbstractIssuerStore(ImageStore imageStore) {
        mImageStore = imageStore;
    }

    public void saveIssuerResponse(IssuerResponse issuerResponse, String recipientPubKey) {
        if (issuerResponse != null) {
            mImageStore.saveImage(issuerResponse.getUuid(), issuerResponse.getImageData());
            issuerResponse.setIntroducedOn(DateTime.now().toString());
            saveIssuer(issuerResponse, recipientPubKey);
        }
    }
}