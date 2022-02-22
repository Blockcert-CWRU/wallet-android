package com.learningmachine.android.app.data.inject;

import com.learningmachine.android.app.data.store.pda.PdaCertificateStoreFactory;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApiModule.class)
public interface PdaStoreComponent {

    PdaCertificateStoreFactory getCertFactory();
}
