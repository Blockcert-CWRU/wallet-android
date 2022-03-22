package com.learningmachine.android.app.data.store;

import rx.Observable;

public interface DataStore {

    default Observable<Void> reset() {
        return Observable.empty();
    }
}
