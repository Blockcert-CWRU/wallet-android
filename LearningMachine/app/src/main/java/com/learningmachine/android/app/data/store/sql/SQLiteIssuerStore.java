package com.learningmachine.android.app.data.store.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.data.model.KeyRotation;
import com.learningmachine.android.app.data.store.AbstractIssuerStore;
import com.learningmachine.android.app.data.store.ImageStore;
import com.learningmachine.android.app.data.store.LMDatabaseHelper;
import com.learningmachine.android.app.data.store.cursor.IssuerCursorWrapper;
import com.learningmachine.android.app.util.ListUtils;
import com.learningmachine.android.app.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class SQLiteIssuerStore extends AbstractIssuerStore {

    private final SQLiteDatabase mDatabase;
    private final ImageStore mImageStore;

    @Inject
    public SQLiteIssuerStore(SQLiteDatabase database, ImageStore imageStore) {
        super(imageStore);
        mDatabase = database;
        mImageStore = imageStore;
    }

    @Override
    public Observable<Void> saveRecord(IssuerRecord record, String recipientPubKey) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(LMDatabaseHelper.Column.Issuer.NAME, record.getName());
        contentValues.put(LMDatabaseHelper.Column.Issuer.EMAIL, record.getEmail());
        contentValues.put(LMDatabaseHelper.Column.Issuer.ISSUERURL, record.getIssuerURL());
        contentValues.put(LMDatabaseHelper.Column.Issuer.INTRODUCED_ON, record.getIntroducedOn());
        contentValues.put(LMDatabaseHelper.Column.Issuer.RECIPIENT_PUB_KEY, recipientPubKey);

        // Issuers in certificates are incomplete, do not overwrite data if it was there before
        String certsUrl = record.getCertsUrl();
        if (!StringUtils.isEmpty(certsUrl)) {
            contentValues.put(LMDatabaseHelper.Column.Issuer.CERTS_URL, certsUrl);
        }
        String introUrl = record.getIntroUrl();
        if (!StringUtils.isEmpty(introUrl)) {
            contentValues.put(LMDatabaseHelper.Column.Issuer.INTRO_URL, introUrl);
        }
        String analyticsUrlString = record.getAnalyticsUrlString();
        if (!StringUtils.isEmpty(analyticsUrlString)) {
            contentValues.put(LMDatabaseHelper.Column.Issuer.ANALYTICS, analyticsUrlString);
        }
        String issuerId = record.getUuid();
        if (!ListUtils.isEmpty(record.getIssuerKeys())) {
            saveIssuerKeys(record.getIssuerKeys(), issuerId);
        }
        if (!ListUtils.isEmpty(record.getRevocationKeys())) {
            saveRevocationKeys(record.getRevocationKeys(), issuerId);
        }

        if (load(issuerId) == null) {
            contentValues.put(LMDatabaseHelper.Column.Issuer.UUID, issuerId);
            mDatabase.insert(LMDatabaseHelper.Table.ISSUER,
                    null,
                    contentValues);
        } else {
            mDatabase.update(LMDatabaseHelper.Table.ISSUER,
                    contentValues, LMDatabaseHelper.Column.Issuer.UUID + " = ?",
                    new String[]{issuerId});
        }
        return Observable.empty();
    }

    @Override
    public Observable<List<IssuerRecord>> loadAll() {

        List<IssuerRecord> issuerList = new ArrayList<>();

        Cursor cursor = mDatabase.query(
                LMDatabaseHelper.Table.ISSUER,
                null,
                null,
                null,
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            IssuerCursorWrapper cursorWrapper = new IssuerCursorWrapper(cursor);
            while (!cursorWrapper.isAfterLast()) {
                IssuerRecord issuer = cursorWrapper.getIssuer();

                Observable<List<KeyRotation>> issuerKeys = loadIssuerKeys(issuer.getUuid());
                issuer.setIssuerKeys(issuerKeys.toBlocking().first());
                Observable<List<KeyRotation>> revocationKeys = loadRevocationKeys(issuer.getUuid());
                issuer.setRevocationKeys(revocationKeys.toBlocking().first());

                issuerList.add(issuer);
                cursorWrapper.moveToNext();
            }
        }

        cursor.close();

        return Observable.just(issuerList);
    }

    @Override
    public Observable<IssuerRecord> load(String issuerId) {
        IssuerRecord issuer = null;
        Cursor cursor = mDatabase.query(
                LMDatabaseHelper.Table.ISSUER,
                null,
                LMDatabaseHelper.Column.Issuer.UUID + " = ?",
                new String[]{issuerId},
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            IssuerCursorWrapper cursorWrapper = new IssuerCursorWrapper(cursor);
            issuer = cursorWrapper.getIssuer();
            Observable<List<KeyRotation>> issuerKeys = loadIssuerKeys(issuer.getUuid());
            issuer.setIssuerKeys(issuerKeys.toBlocking().first());
            Observable<List<KeyRotation>> revocationKeys = loadRevocationKeys(issuer.getUuid());
            issuer.setRevocationKeys(revocationKeys.toBlocking().first());
        }

        cursor.close();

        return Observable.just(issuer);
    }

    @Override
    public Observable<IssuerRecord> loadForCertificate(String certId) {
        IssuerRecord issuer = null;

        String selectQuery = "SELECT "
                + LMDatabaseHelper.Table.ISSUER + "." + LMDatabaseHelper.Column.Issuer.ID + ", "
                + LMDatabaseHelper.Table.ISSUER + "." + LMDatabaseHelper.Column.Issuer.NAME + ", "
                + LMDatabaseHelper.Table.ISSUER + "." + LMDatabaseHelper.Column.Issuer.EMAIL + ", "
                + LMDatabaseHelper.Table.ISSUER + "." + LMDatabaseHelper.Column.Issuer.ISSUERURL + ", "
                + LMDatabaseHelper.Table.ISSUER + "." + LMDatabaseHelper.Column.Issuer.UUID + ", "
                + LMDatabaseHelper.Table.ISSUER + "." + LMDatabaseHelper.Column.Issuer.CERTS_URL + ", "
                + LMDatabaseHelper.Table.ISSUER + "." + LMDatabaseHelper.Column.Issuer.INTRO_URL + ", "
                + LMDatabaseHelper.Table.ISSUER + "." + LMDatabaseHelper.Column.Issuer.INTRODUCED_ON + ", "
                + LMDatabaseHelper.Table.ISSUER + "." + LMDatabaseHelper.Column.Issuer.ANALYTICS + ", "
                + LMDatabaseHelper.Table.ISSUER + "." + LMDatabaseHelper.Column.Issuer.RECIPIENT_PUB_KEY
                + " FROM "
                + LMDatabaseHelper.Table.ISSUER
                + " INNER JOIN " + LMDatabaseHelper.Table.CERTIFICATE
                + " ON " + LMDatabaseHelper.Table.ISSUER + "." + LMDatabaseHelper.Column.Issuer.UUID
                + " = " + LMDatabaseHelper.Table.CERTIFICATE + "." + LMDatabaseHelper.Column.Certificate.ISSUER_UUID
                + " WHERE " + LMDatabaseHelper.Table.CERTIFICATE + "." + LMDatabaseHelper.Column.Certificate.UUID
                + " = ?";

        Cursor cursor = mDatabase.rawQuery(selectQuery, new String[]{certId});

        if (cursor.moveToFirst()) {
            IssuerCursorWrapper cursorWrapper = new IssuerCursorWrapper(cursor);
            issuer = cursorWrapper.getIssuer();
            Observable<List<KeyRotation>> issuerKeys = loadIssuerKeys(issuer.getUuid());
            issuer.setIssuerKeys(issuerKeys.toBlocking().first());
            Observable<List<KeyRotation>> revocationKeys = loadRevocationKeys(issuer.getUuid());
            issuer.setRevocationKeys(revocationKeys.toBlocking().first());
        }

        cursor.close();

        return Observable.just(issuer);
    }

    @Override
    public Observable<Void> saveKeyRotation(KeyRotation keyRotation, String issuerId, String tableName) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(LMDatabaseHelper.Column.KeyRotation.KEY, keyRotation.getKey());
        contentValues.put(LMDatabaseHelper.Column.KeyRotation.CREATED_DATE, keyRotation.getCreatedDate());
        contentValues.put(LMDatabaseHelper.Column.KeyRotation.ISSUER_UUID, issuerId);

        if (ListUtils.isEmpty(loadKeyRotations(issuerId, tableName).toBlocking().first())) {
            mDatabase.insert(tableName,
                    null,
                    contentValues);
        } else {
            mDatabase.update(tableName,
                    contentValues,
                    LMDatabaseHelper.Column.KeyRotation.KEY + " = ? "
                            + " AND " + LMDatabaseHelper.Column.KeyRotation.ISSUER_UUID + " = ?",
                    new String[]{keyRotation.getKey(), issuerId});
        }
        return Observable.empty();
    }

    @Override
    public Observable<List<KeyRotation>> loadKeyRotations(String issuerId, String tableName) {
        return Observable.just(Collections.emptyList());
    }

    @Override
    public Observable<Void> reset() {
        mDatabase.delete(LMDatabaseHelper.Table.ISSUER, null, null);
        mDatabase.delete(LMDatabaseHelper.Table.ISSUER_KEY, null, null);
        mDatabase.delete(LMDatabaseHelper.Table.REVOCATION_KEY, null, null);
        return mImageStore.reset();
    }

    private Observable<List<KeyRotation>> loadIssuerKeys(String issuerId) {
        return loadKeyRotations(issuerId, LMDatabaseHelper.Table.ISSUER_KEY);
    }

    private Observable<List<KeyRotation>> loadRevocationKeys(String issuerId) {
        return loadKeyRotations(issuerId, LMDatabaseHelper.Table.REVOCATION_KEY);
    }

    private void saveIssuerKeys(Iterable<KeyRotation> keyRotations, String issuerId) {
        saveKeyRotations(keyRotations, issuerId, LMDatabaseHelper.Table.ISSUER_KEY);
    }

    private void saveRevocationKeys(Iterable<KeyRotation> keyRotations, String issuerId) {
        saveKeyRotations(keyRotations, issuerId, LMDatabaseHelper.Table.REVOCATION_KEY);
    }

    private void saveKeyRotations(Iterable<KeyRotation> keyRotations, String issuerId, String tableName) {
        for (KeyRotation keyRotation : keyRotations) {
            saveKeyRotation(keyRotation, issuerId, tableName);
        }
    }
}
