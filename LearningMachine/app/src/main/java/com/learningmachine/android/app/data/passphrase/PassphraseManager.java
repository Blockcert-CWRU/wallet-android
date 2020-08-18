package com.learningmachine.android.app.data.passphrase;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;

import com.learningmachine.android.app.util.AESCrypt;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.Scanner;

import timber.log.Timber;

public class PassphraseManager {
    private final Context mContext;
    private String mPassphrase;
    private PassphraseCallback mCallback;

    @FunctionalInterface
    public interface PassphraseCallback {
        void apply (String a);
    }

    public PassphraseManager(Context context) {
        mContext = context;
    }

    private Uri getLegacyPassphraseFileUri() {
        return Uri.parse(Environment.getExternalStorageDirectory() + "/learningmachine.dat");
    }

    public boolean doesLegacyPassphraseFileExist() {
        String passphraseFilePath = getLegacyPassphraseFileUri().getPath();
        return passphraseFilePath != null && new File(passphraseFilePath).exists();
    }

    public void deleteLegacyPassphrase() {
        String passphraseFile = getLegacyPassphraseFileUri().getPath();
        if (passphraseFile != null) {
            File legacyFile = new File(passphraseFile);
            legacyFile.delete();
        }
    }

    public void migrateSavedPassphrase(PassphraseCallback passphraseCallback) {
        getLegacyPassphraseFromDevice((passphrase) -> {
            savePassphraseInLegacyStorage(passphrase, passphraseCallback);
            deleteLegacyPassphrase();
        });
    }

    public void reset() {
        deleteLegacyPassphrase();
    }
    private String getDeviceId() {
        @SuppressLint("HardwareIds")
        final String deviceId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        if(deviceId == null || deviceId.length() == 0) {
            return "NOT_IDEAL_KEY";
        }
        return deviceId;
    }

    public void initPassphraseBackup(String passphrase, PassphraseCallback callback) {
        mPassphrase = passphrase;
        mCallback = callback;
    }

    public void storePassphraseBackup(Uri location) {
        ContentResolver resolver = mContext.getContentResolver();
        try (OutputStream stream = resolver.openOutputStream(location)) {
            PrintWriter out = new PrintWriter(stream);
            String encryptionKey= getDeviceId();
            String mneumonicString = "mneumonic:"+mPassphrase;
            try {
                String encryptedMsg = AESCrypt.encrypt(encryptionKey, mneumonicString);
                out.println(encryptedMsg);
                mCallback.apply(mPassphrase);
            }catch (GeneralSecurityException e){
                Timber.e(e, "Could not encrypt passphrase.");
                mCallback.apply(null);
            }
        } catch (Exception e) {
            Timber.e(e, "Could not write to passphrase file");
            mCallback.apply(null);
        }
    }

    public void cleanupPassphraseBackup() {
        mPassphrase = null;
        mCallback = null;
    }

    public void savePassphraseInLegacyStorage(String passphrase, PassphraseCallback passphraseCallback) {
        if (passphrase == null) {
            passphraseCallback.apply(null);
            return;
        }

        ContentResolver resolver = mContext.getContentResolver();
        Uri passphraseFile = getLegacyPassphraseFileUri();
        try (OutputStream stream = resolver.openOutputStream(passphraseFile)) {
            try(PrintWriter out = new PrintWriter(stream)) {
                String encryptionKey= getDeviceId();
                String mneumonicString = "mneumonic:"+passphrase;
                try {
                    String encryptedMsg = AESCrypt.encrypt(encryptionKey, mneumonicString);
                    out.println(encryptedMsg);
                    passphraseCallback.apply(passphrase);
                }catch (GeneralSecurityException e){
                    Timber.e(e, "Could not encrypt passphrase.");
                    passphraseCallback.apply(null);
                }
            } catch(Exception e) {
                Timber.e(e, "Could not write to passphrase file");
                passphraseCallback.apply(null);
            }
        } catch (IOException e) {
            Timber.e(e, "Could not write to passphrase file");
            passphraseCallback.apply(null);
        }
    }

    public void getLegacyPassphraseFromDevice(PassphraseCallback passphraseCallback) {
        Uri passphraseFile = getLegacyPassphraseFileUri();
        getPassphraseFromDevice(passphraseFile, passphraseCallback);
    }

    private void getPassphraseFromDevice(Uri passphraseFile, PassphraseCallback passphraseCallback) {
        try {
            ContentResolver resolver = mContext.getContentResolver();
            String encryptedMsg = new Scanner(resolver.openInputStream(passphraseFile)).useDelimiter("\\Z").next();
            String encryptionKey = getDeviceId();
            try {
                String content = AESCrypt.decrypt(encryptionKey, encryptedMsg);
                if (content.startsWith("mneumonic:")) {
                    passphraseCallback.apply(content.substring(10).trim());
                    return;
                }
            }catch (GeneralSecurityException e){
                Timber.e(e, "Could not decrypt passphrase.");
            }
        } catch(Exception e) {
            // note: this is a non-critical feature, so if this fails nbd
            Timber.e(e, "Could not read passphrase file.");
        }

        passphraseCallback.apply(null);
    }
}
