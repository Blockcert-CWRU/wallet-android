package com.learningmachine.android.app.data.store;

import com.learningmachine.android.app.data.webservice.response.IssuerResponse;

import org.joda.time.DateTime;

public abstract class AbstractIssuerStore implements IssuerStore {

    private final ImageStore mImageStore;

    protected AbstractIssuerStore(ImageStore imageStore) {
        mImageStore = imageStore;
    }

    public void saveResponse(IssuerResponse response, String recipientPubKey) {
        if (response != null) {
            mImageStore.saveImage(response.getUuid(), response.getImageData());
            response.setIntroducedOn(DateTime.now().toString());
            saveRecord(response, recipientPubKey);
        }
    }
}