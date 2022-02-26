package com.learningmachine.android.app.util;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Class can be moved to test package when mock data is no longer necessary
 */
public class GsonUtil {

    private static final Gson GSON = new Gson();
    private final Context mContext;

    public GsonUtil(Context context) {
        mContext = context;
    }

    public <T> T loadModelObject(String file, Class<T> clazz) throws IOException {
        String filename = file + ".json";
        AssetManager assetManager = mContext.getAssets();
        InputStream inputStream = assetManager.open(filename);
        InputStreamReader reader = new InputStreamReader(inputStream);
        return (T) GSON.fromJson(reader, clazz);
    }
}
