package com.learningmachine.android.app.data.store.pda;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;

@AssistedFactory
public interface PDAIssuerStoreFactory {

    PDAIssuerStore create(
            @Assisted("hatName") String hatName,
            @Assisted("authToken") String authToken);
}
