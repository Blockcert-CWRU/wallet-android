package com.learningmachine.android.app.data.inject;

import com.learningmachine.android.app.data.store.pda.PDACertificateStoreFactory;
import com.learningmachine.android.app.data.store.pda.PDAIssuerStoreFactory;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApiModule.class)
public interface PdaStoreComponent {

    PDACertificateStoreFactory certificateStoreFactory();

    PDAIssuerStoreFactory issuerStoreFactory();
}
