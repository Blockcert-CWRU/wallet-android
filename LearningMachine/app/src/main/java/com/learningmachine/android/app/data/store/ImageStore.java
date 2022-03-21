package com.learningmachine.android.app.data.store;

import rx.Observable;

public interface ImageStore extends DataStore {

    Observable<Boolean> saveImage(String issuerId, String jsonData);
}