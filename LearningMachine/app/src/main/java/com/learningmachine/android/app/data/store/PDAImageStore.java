package com.learningmachine.android.app.data.store;

import android.content.Context;
import android.util.Base64;

import com.learningmachine.android.app.util.ImageUtils;
import com.learningmachine.android.app.util.StringUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import timber.log.Timber;

public class PDAImageStore implements ImageStore{

    private final Context mContext;

    @Inject
    public PDAImageStore(Context context) {
        mContext = context;
    }

    @Override
    public void reset() {

    }

    @Override
    public boolean saveImage(String issuerId, String jsonData) {
        if (StringUtils.isEmpty(issuerId) || StringUtils.isEmpty(jsonData)) {
            return false;
        }

        String filename = ImageUtils.getImageFilename(issuerId);
        if (StringUtils.isEmpty(filename)) {
            return false;
        }

        String imageData = ImageUtils.getImageDataFromJson(jsonData);
        if (StringUtils.isEmpty(imageData)) {
            return false;
        }

        boolean success = false;

        try (FileOutputStream fileOutputStream = mContext.openFileOutput(filename, Context.MODE_PRIVATE)) {
            byte[] decodedString = Base64.decode(imageData, Base64.DEFAULT);
            fileOutputStream.write(decodedString);
            fileOutputStream.flush();
            success = true;
        } catch (FileNotFoundException e) {
            Timber.e(e, "Unable to open file");
        } catch (IOException e) {
            Timber.e(e, "Unable to write to file");
        }

        return success;
    }
}
