package com.learningmachine.android.app.data.inject;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;
import timber.log.Timber.DebugTree;

@Module(includes = DataBindings.class)
public class DevDataModule {

    @Provides
    @Singleton
    Timber.Tree provideLoggingTree() {
        return new DebugTree();
    }

    @Provides
    @Singleton
    NetworkParameters providesBitcoinNetworkParameters() {
        return MainNetParams.get();
    }

    @Provides
    HttpLoggingInterceptor.Level providesLogLevel() {
        return HttpLoggingInterceptor.Level.BODY;
    }
}
