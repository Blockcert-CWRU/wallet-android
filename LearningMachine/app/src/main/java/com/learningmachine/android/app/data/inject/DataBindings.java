package com.learningmachine.android.app.data.inject;

import com.learningmachine.android.app.data.store.CertificateStore;
import com.learningmachine.android.app.data.store.ImageStore;
import com.learningmachine.android.app.data.store.IssuerStore;
import com.learningmachine.android.app.data.store.sql.SQLiteCertificateStore;
import com.learningmachine.android.app.data.store.sql.SQLiteImageStore;
import com.learningmachine.android.app.data.store.sql.SQLiteIssuerStore;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module
interface DataBindings {

    @Binds
    @Singleton
    CertificateStore bindCertificateStore(SQLiteCertificateStore certStore);

    @Binds
    @Singleton
    IssuerStore bindIssuerStore(SQLiteIssuerStore issuerStore);

    @Binds
    @Singleton
    ImageStore bindImageStore(SQLiteImageStore imageStore);
}
