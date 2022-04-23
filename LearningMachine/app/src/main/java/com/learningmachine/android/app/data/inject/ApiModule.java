package com.learningmachine.android.app.data.inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import com.learningmachine.android.app.LMConstants;
import com.learningmachine.android.app.data.store.pda.PDACertificateStoreService;
import com.learningmachine.android.app.data.store.pda.PDAIndexService;
import com.learningmachine.android.app.data.store.pda.PDAIssuerStoreService;
import com.learningmachine.android.app.data.webservice.BlockchainService;
import com.learningmachine.android.app.data.webservice.CertificateInterceptor;
import com.learningmachine.android.app.data.webservice.CertificateService;
import com.learningmachine.android.app.data.webservice.IssuerService;
import com.learningmachine.android.app.data.webservice.VersionService;
import com.learningmachine.android.app.ui.share.DashboardShareService;

import java.nio.charset.StandardCharsets;
import java.util.ServiceLoader;

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
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Module
public class ApiModule {

    @Provides
    @Singleton
    @Named("hatName")
    static String hatName() {
        return System.getenv("PDA_USERNAME");
    }

    @Provides
    @Singleton
    @Named("password")
    static String password() {
        return System.getenv("PDA_PASSWORD");
    }

    @Provides
    @Singleton
    @Named("authToken")
    static String authToken() {
        return System.getenv("PDA_AUTH_TOKEN");
    }

    @Provides
    @Singleton
    public static Interceptor loggingInterceptor() {
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
    static Gson gson() {
        GsonBuilder builder = new GsonBuilder();
        for (TypeAdapterFactory factory : ServiceLoader.load(TypeAdapterFactory.class)) {
            builder.registerTypeAdapterFactory(factory);
        }
        return builder.create();
    }

    @Provides
    @Singleton
    static GsonConverterFactory converterFactory(Gson gson) {
        return GsonConverterFactory.create(gson);
    }

    @Provides
    @Singleton
    static CallAdapter.Factory callAdapterFactory() {
        return RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
    }

    @Provides
    @Singleton
    @Named("base")
    public static OkHttpClient baseClient(Interceptor loggingInterceptor) {
        return new OkHttpClient.Builder().addInterceptor(loggingInterceptor).build();
    }

    @Provides
    @Singleton
    @Named("certificate")
    static OkHttpClient certificateClient(
            @Named("base") OkHttpClient baseClient, CertificateInterceptor certificateInterceptor) {
        return baseClient.newBuilder().addInterceptor(certificateInterceptor).build();
    }

    @Provides
    @Singleton
    @Named("pda")
    static Retrofit pdaRetrofit(
            @Named("base") OkHttpClient client,
            GsonConverterFactory converterFactory,
            CallAdapter.Factory callAdapterFactory) {
        return new Retrofit.Builder()
                .baseUrl(LMConstants.BASE_PDA_URL)
                .client(client)
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(callAdapterFactory)
                .build();
    }

    @Provides
    @Singleton
    @Named("dashboard")
    static Retrofit dashboardRetrofit(
            @Named("base") OkHttpClient client,
            GsonConverterFactory converterFactory,
            CallAdapter.Factory callAdapterFactory) {
        return new Retrofit.Builder()
                .baseUrl("https://abdcde.free.beeceptor.com")
                .client(client)
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(callAdapterFactory)
                .build();
    }

    @Provides
    @Singleton
    @Named("certificate")
    static Retrofit certificateRetrofit(
            @Named("certificate") OkHttpClient client,
            GsonConverterFactory converterFactory,
            CallAdapter.Factory callAdapterFactory) {
        return new Retrofit.Builder()
                .baseUrl(LMConstants.BASE_URL)
                .client(client)
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(callAdapterFactory)
                .build();
    }

    @Provides
    @Singleton
    @Named("blockchain")
    static Retrofit blockchainRetrofit(
            @Named("base") OkHttpClient client,
            GsonConverterFactory converterFactory,
            CallAdapter.Factory callAdapterFactory) {
        return new Retrofit.Builder()
                .baseUrl(LMConstants.BLOCKCHAIN_SERVICE_URL)
                .client(client)
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(callAdapterFactory)
                .build();
    }

    @Provides
    @Singleton
    @Named("version")
    static Retrofit versionRetrofit(
            @Named("base") OkHttpClient client,
            GsonConverterFactory converterFactory,
            CallAdapter.Factory callAdapterFactory) {
        return new Retrofit.Builder()
                .baseUrl(LMConstants.VERSION_BASE_URL)
                .client(client)
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(callAdapterFactory)
                .build();
    }

    @Provides
    @Singleton
    static PDACertificateStoreService certStoreService(@Named("pda") Retrofit retrofit) {
        return retrofit.create(PDACertificateStoreService.class);
    }

    @Provides
    @Singleton
    static PDAIssuerStoreService issuerStoreService(@Named("pda") Retrofit retrofit) {
        return retrofit.create(PDAIssuerStoreService.class);
    }

    @Provides
    @Singleton
    static PDAIndexService provideIndexService(@Named("pda") Retrofit retrofit) {
        return retrofit.create(PDAIndexService.class);
    }

    @Provides
    @Singleton
    static VersionService versionService(@Named("version") Retrofit retrofit) {
        return retrofit.create(VersionService.class);
    }

    @Provides
    @Singleton
    static DashboardShareService dashboardShareService(@Named("dashboard") Retrofit retrofit) {
        return retrofit.create(DashboardShareService.class);
    }

    @Provides
    @Singleton
    static BlockchainService blockchainService(@Named("blockchain") Retrofit retrofit) {
        return retrofit.create(BlockchainService.class);
    }

    @Provides
    @Singleton
    static IssuerService issuerService(@Named("certificate") Retrofit retrofit) {
        return retrofit.create(IssuerService.class);
    }

    @Provides
    @Singleton
    static CertificateService certificateService(@Named("certificate") Retrofit retrofit) {
        return retrofit.create(CertificateService.class);
    }
}
