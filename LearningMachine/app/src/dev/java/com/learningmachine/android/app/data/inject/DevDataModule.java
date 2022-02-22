package com.learningmachine.android.app.data.inject;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;
import timber.log.Timber.DebugTree;

@Module(includes = DataModule.class)
public class DevDataModule {

    @Provides
    @Singleton
    static Timber.Tree loggingTree() {
        return new DebugTree();
    }

    @Provides
    @Singleton
    static NetworkParameters bitcoinNetworkParameters() {
        return MainNetParams.get();
    }

    @Provides
    static HttpLoggingInterceptor.Level logLevel() {
        return HttpLoggingInterceptor.Level.BODY;
    }
}
