package com.learningmachine.android.app.data.store.sql;

import android.content.Context;
import android.util.Base64;

import com.learningmachine.android.app.data.store.ImageStore;
import com.learningmachine.android.app.util.ImageUtils;
import com.learningmachine.android.app.util.StringUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import rx.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

@Singleton
public class SQLiteImageStore implements ImageStore {

    private final Context mContext;

    @Inject
    public SQLiteImageStore(Context context) {
        mContext = context;
    }

    /**
     * @param issuerId Issuer url
     * @param jsonData Image data
     * @return true if the image was written to file successfully
     */
    @Override
    public Observable<Boolean> saveImage(String issuerId, String jsonData) {
        if (StringUtils.isEmpty(issuerId) || StringUtils.isEmpty(jsonData)) {
            return Observable.just(false);
        }

        String filename = ImageUtils.getImageFilename(issuerId);
        if (StringUtils.isEmpty(filename)) {
            return Observable.just(false);
        }

        String imageData = ImageUtils.getImageDataFromJson(jsonData);
        if (StringUtils.isEmpty(imageData)) {
            return Observable.just(false);
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

        return Observable.just(success);
    }

    @Override
    public void reset() {
        String[] fileList = mContext.fileList();
        for (String file : fileList) {
            if (file.contains(".png")) {
                mContext.deleteFile(file);
            }
        }
    }
}
