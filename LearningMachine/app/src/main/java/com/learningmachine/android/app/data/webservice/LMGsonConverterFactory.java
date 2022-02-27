package com.learningmachine.android.app.data.webservice;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;


public class LMGsonConverterFactory extends Converter.Factory {
    private static final Gson gson = new Gson();

    public static LMGsonConverterFactory create() {
        return new LMGsonConverterFactory();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(@NonNull Type type, @NonNull Annotation[] annotations,
                                                            @NonNull Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new LMGsonResponseBodyConverter<>(gson, adapter);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(@NonNull Type type, @NonNull Annotation[] parameterAnnotations,
                                                          @NonNull Annotation[] methodAnnotations, @NonNull Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new LMGsonRequestBodyConverter<>(gson, adapter);
    }


}
