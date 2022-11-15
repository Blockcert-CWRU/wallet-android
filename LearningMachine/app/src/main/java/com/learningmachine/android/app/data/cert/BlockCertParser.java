package com.learningmachine.android.app.data.cert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;

public class BlockCertParser {
    public final Gson mGson;

    public BlockCertParser() {
        mGson = new GsonBuilder()
                .registerTypeAdapter(BlockCert.class, new BlockCertAdapter())
                .excludeFieldsWithoutExposeAnnotation()
                .create();
    }

    public BlockCert fromJson(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        return mGson.fromJson(inputStreamReader, BlockCert.class);
    }

    public BlockCert fromJson(String string) {
        return mGson.fromJson(string, BlockCert.class);
    }

    public String toJson(BlockCert blockCert) {
        return mGson.toJson(blockCert);
    }
}
