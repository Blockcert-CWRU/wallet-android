package com.learningmachine.android.app.data.inject;

import com.learningmachine.android.app.data.store.CertificateStore;
import com.learningmachine.android.app.data.store.ImageStore;
import com.learningmachine.android.app.data.store.SQLiteCertificateStore;
import com.learningmachine.android.app.data.store.SQLiteImageStore;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module
interface DataBindings {

    @Binds
    @Singleton
    CertificateStore bindCertificateStore(SQLiteCertificateStore certStore);
    ImageStore bindImageStore(SQLiteImageStore imageStore);
}
