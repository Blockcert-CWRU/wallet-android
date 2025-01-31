package com.learningmachine.android.app.data.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.learningmachine.android.app.BuildConfig;
import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.data.model.KeyRotation;
import com.learningmachine.android.app.data.store.sql.SQLiteImageStore;
import com.learningmachine.android.app.data.store.sql.SQLiteIssuerStore;
import com.learningmachine.android.app.util.ListUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Currently only tests saving and loading since users cannot modify Issuer or KeyRotations
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 27)
public class IssuerStoreTest {

    private IssuerStore mIssuerStore;

    @Before
    public void setup() throws Exception {
        SQLiteImageStore imageStore = mock(SQLiteImageStore.class);
        Context context = RuntimeEnvironment.application;
        SQLiteDatabase database = new LMDatabaseHelper(context).getWritableDatabase();

        mIssuerStore = new SQLiteIssuerStore(database, imageStore);
    }

    @Test
    public void testIssuer_save_andLoad() throws Exception {
        String issuerUrl = "https://www.blockcerts.org/mockissuer/issuer/got-issuer.json";
        String issuerUuid = "http://www.blockcerts.org/mockissuer/issuer/got-issuer.json";
        String certsUrl = "http://www.blockcerts.org/mockissuer/certificates/";
        String introUrl = "http://www.blockcerts.org/mockissuer/intro/";
        String name = "Game of thrones issuer on testnet";
        String email = "org@org.org";
        String introducedOn = "2017-05-11T18:28:27.415+00:00";
        String analytics = "https://www.learningmachine.com/analytics";
        String recipientPubKey = "aaaabbbbcccc";

	//    public IssuerRecord(String name, String email, String issuerURL, String uuid, String certsUrl, String introUrl, String introducedOn, String analyticsUrlString, String recipientPubKey) 
        IssuerRecord issuerOrig = new IssuerRecord(name, email, issuerUrl, issuerUuid, certsUrl, introUrl, introducedOn, analytics, recipientPubKey);
        issuerOrig.setRevocationKeys(new ArrayList<>());
        issuerOrig.setIssuerKeys(new ArrayList<>());
        mIssuerStore.saveRecord(issuerOrig, recipientPubKey);

        Observable<IssuerRecord> issuerLoaded = mIssuerStore.load(issuerUuid);

        assertNotNull(issuerLoaded);
        assertEquals(name, issuerLoaded.toBlocking().first().getName());
        assertEquals(email, issuerLoaded.toBlocking().first().getEmail());
        assertEquals(issuerUuid, issuerLoaded.toBlocking().first().getUuid());
        assertEquals(certsUrl, issuerLoaded.toBlocking().first().getCertsUrl());
        assertEquals(introUrl, issuerLoaded.toBlocking().first().getIntroUrl());
        assertEquals(introducedOn, issuerLoaded.toBlocking().first().getIntroducedOn());
    }

    @Test
    public void testKeyRotation_save_andLoad() {
        String issuerUuid = "issuer.com";
        String createdDate = "2017-04-18";
        String key = "249jm9wmldskjgmawe";
        KeyRotation keyRotation = new KeyRotation(createdDate, key);

        String tableName = LMDatabaseHelper.Table.ISSUER_KEY;
        mIssuerStore.saveKeyRotation(keyRotation, issuerUuid, tableName);

        Observable<List<KeyRotation>> keyRotationList = mIssuerStore.loadKeyRotations(issuerUuid, tableName);

        assertFalse(ListUtils.isEmpty(keyRotationList.toBlocking().first()));

        KeyRotation actualKeyRotation = keyRotationList.toBlocking().first().get(0);

        assertEquals(createdDate, actualKeyRotation.getCreatedDate());
        assertEquals(key, actualKeyRotation.getKey());
    }
}
