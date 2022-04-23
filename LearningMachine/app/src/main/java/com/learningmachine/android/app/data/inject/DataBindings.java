package com.learningmachine.android.app.data.inject;

import com.learningmachine.android.app.data.store.CertificateStore;
import com.learningmachine.android.app.data.store.ImageStore;
import com.learningmachine.android.app.data.store.IssuerStore;
import com.learningmachine.android.app.data.store.pda.PDACertificateStore;
import com.learningmachine.android.app.data.store.sql.SQLiteImageStore;
import com.learningmachine.android.app.data.store.sql.SQLiteIssuerStore;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

// TODO Dagger does not support injecting @AssistedInject type
//   We will need to migrate to providing these at runtime with authentication

@Module
interface DataBindings {

    @Binds
    @Singleton
    CertificateStore certificateStore(PDACertificateStore certificateStore);

    @Binds
    @Singleton
    IssuerStore issuerStore(SQLiteIssuerStore issuerStore);

    @Binds
    @Singleton
    ImageStore imageStore(SQLiteImageStore imageStore);
}
