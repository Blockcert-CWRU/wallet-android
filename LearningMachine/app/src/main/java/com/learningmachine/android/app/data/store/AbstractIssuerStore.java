package com.learningmachine.android.app.data.store;

import com.learningmachine.android.app.data.store.ImageStore;
import com.learningmachine.android.app.data.store.IssuerStore;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;

import org.joda.time.DateTime;

public abstract class AbstractIssuerStore implements IssuerStore {
    private final ImageStore imageStore;

    protected AbstractIssuerStore(ImageStore imageStore){
        this.imageStore = imageStore;
    }

    public void saveIssuerResponse(IssuerResponse issuerResponse, String recipientPubKey){
        if (issuerResponse == null) {
            return;
        }

        String uuid = issuerResponse.getUuid();
        String imageData = issuerResponse.getImageData();
        imageStore.saveImage(uuid, imageData);

        String introducedOn = DateTime.now().toString();
        issuerResponse.setIntroducedOn(introducedOn);

        saveIssuer(issuerResponse, recipientPubKey);
    }
}