package com.learningmachine.android.app.util;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.inject.Inject;

/**
 * Class can be moved to test package when mock data is no longer necessary
 */
public class GsonUtil {

    private final Context mContext;
    private final Gson mGson;

    @Inject
    public GsonUtil(Context context, Gson gson) {
        mContext = context;
        mGson = gson;
    }

    public <T> T loadModelObject(String file, Class<T> clazz) throws IOException {
        String filename = file + ".json";
        InputStream inputStream = mContext.getAssets().open(filename);
        InputStreamReader reader = new InputStreamReader(inputStream);
        return mGson.fromJson(reader, clazz);
    }
}
