package com.learningmachine.android.app.data.inject;

import com.learningmachine.android.app.data.store.CertificateStore;
import com.learningmachine.android.app.data.store.ImageStore;
import com.learningmachine.android.app.data.store.IssuerStore;
import com.learningmachine.android.app.data.store.pda.PDACertificateStore;
import com.learningmachine.android.app.data.store.pda.PDAIssuerStore;
import com.learningmachine.android.app.data.store.sql.SQLiteImageStore;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module
interface DataBindings {

    @Binds
    @Singleton
    CertificateStore certificateStore(PDACertificateStore certificateStore);

    @Binds
    @Singleton
    IssuerStore issuerStore(PDAIssuerStore issuerStore);

    @Binds
    @Singleton
    ImageStore imageStore(SQLiteImageStore imageStore);
}
