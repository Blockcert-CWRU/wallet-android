package com.learningmachine.android.app.data.inject;

import com.learningmachine.android.app.LMConstants;
import com.learningmachine.android.app.data.store.pda.PdaCertificateStoreService;
import com.learningmachine.android.app.data.store.pda.PdaIndexService;
import com.learningmachine.android.app.data.store.pda.PdaIssuerStoreService;
import com.learningmachine.android.app.data.webservice.BlockchainService;
import com.learningmachine.android.app.data.webservice.CertificateInterceptor;
import com.learningmachine.android.app.data.webservice.CertificateService;
import com.learningmachine.android.app.data.webservice.IssuerService;
import com.learningmachine.android.app.data.webservice.VersionService;

import java.nio.charset.StandardCharsets;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Module
public class ApiModule {

    @Provides
    @Singleton
    static Interceptor loggingInterceptor() {
        return chain -> {
            Request request = chain.request();

            Timber.d(String.format("Performing Request: %s %s",
                    request.method(), request.url().toString()));

            if (request.body() != null) {
                Buffer buffer = new Buffer();
                request.body().writeTo(buffer);
                String body = buffer.readUtf8();
                Timber.d(String.format("body: %s", body));
            }

            Response response = chain.proceed(request);
            Timber.d(String.format("response: %s", response.toString()));
            if (response.body() != null && !response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE);
                Buffer buffer = source.getBuffer();
                String responseBodyString = buffer.clone().readString(StandardCharsets.UTF_8);
                Timber.d(String.format("response body: %s", responseBodyString));
            }
            return response;
        };
    }

    @Provides
    @Singleton
    @Named("certStore")
    static OkHttpClient certStoreServiceClient(Interceptor loggingInterceptor) {
        return okHttpClient(loggingInterceptor);
    }

    @Provides
    @Singleton
    @Named("certStore")
    static Retrofit certStoreRetrofit(@Named("certStore") OkHttpClient client) {
        return retrofit(LMConstants.BASE_PDA_URL, client);
    }

    @Provides
    @Singleton
    static PdaCertificateStoreService certStoreService(@Named("certStore") Retrofit retrofit) {
        return retrofit.create(PdaCertificateStoreService.class);
    }

    @Provides
    @Singleton
    static PdaIssuerStoreService issuerStoreService(@Named("certStore") Retrofit retrofit) {
        return retrofit.create(PdaIssuerStoreService.class);
    }

    @Provides
    @Singleton
    @Named("index")
    static OkHttpClient indexServiceClient(Interceptor loggingInterceptor) {
        return okHttpClient(loggingInterceptor);
    }

    @Provides
    @Singleton
    @Named("index")
    static Retrofit provideIndexServiceRetrofit(@Named("index") OkHttpClient client) {
        return retrofit(LMConstants.BASE_PDA_URL, client);
    }

    @Provides
    @Singleton
    static PdaIndexService provideIndexService(@Named("index") Retrofit retrofit) {
        return retrofit.create(PdaIndexService.class);
    }

    @Provides
    @Singleton
    @Named("issuer")
    static OkHttpClient issuerClient(Interceptor loggingInterceptor) {
        return okHttpClient(loggingInterceptor);
    }

    @Provides
    @Singleton
    @Named("issuer")
    static Retrofit issuerRetrofit(@Named("issuer") OkHttpClient client) {
        return retrofit(LMConstants.BASE_URL, client);
    }

    @Provides
    @Singleton
    static IssuerService issuerService(@Named("issuer") Retrofit retrofit) {
        return retrofit.create(IssuerService.class);
    }

    @Provides
    @Singleton
    @Named("certificate")
    static OkHttpClient certificateClient(
            Interceptor loggingInterceptor, CertificateInterceptor certificateInterceptor) {
        return okHttpClient(loggingInterceptor, certificateInterceptor);
    }

    @Provides
    @Singleton
    @Named("certificate")
    static Retrofit certificateRetrofit(@Named("certificate") OkHttpClient client) {
        return retrofit(LMConstants.BASE_URL, client);
    }

    @Provides
    @Singleton
    static CertificateService certificateService(@Named("certificate") Retrofit retrofit) {
        return retrofit.create(CertificateService.class);
    }

    @Provides
    @Singleton
    @Named("blockchain")
    static Retrofit blockchainRetrofit(@Named("issuer") OkHttpClient client) {
        return retrofit(LMConstants.BLOCKCHAIN_SERVICE_URL, client);
    }

    @Provides
    @Singleton
    static BlockchainService blockchainService(@Named("blockchain") Retrofit retrofit) {
        return retrofit.create(BlockchainService.class);
    }

    @Provides
    @Singleton
    @Named("version")
    static Retrofit versionRetrofit(@Named("issuer") OkHttpClient client) {
        return retrofit(LMConstants.VERSION_BASE_URL, client);
    }

    @Provides
    @Singleton
    static VersionService versionService(@Named("version") Retrofit retrofit) {
        return retrofit.create(VersionService.class);
    }

    private static Retrofit retrofit(String baseUrl, OkHttpClient client) {
        return new Retrofit.Builder().baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(
                        RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
    }

    private static OkHttpClient okHttpClient(Interceptor... interceptors) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        for (Interceptor interceptor : interceptors) {
            builder = builder.addInterceptor(interceptor);
        }
        return builder.build();
    }
}
