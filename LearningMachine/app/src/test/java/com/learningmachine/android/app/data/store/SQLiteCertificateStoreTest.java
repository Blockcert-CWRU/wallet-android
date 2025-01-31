package com.learningmachine.android.app.data.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.learningmachine.android.app.BuildConfig;
import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.cert.v12.BlockCertV12;
import com.learningmachine.android.app.data.cert.v20.Badge;
import com.learningmachine.android.app.data.cert.v20.BlockCertV20;
import com.learningmachine.android.app.data.cert.v20.Issuer;
import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.data.store.sql.SQLiteCertificateStore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.net.URI;
import java.net.URISyntaxException;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 27)
public class SQLiteCertificateStoreTest {

    private SQLiteCertificateStore mCertificateStore;

    @Before
    public void setup() {
        Context context = RuntimeEnvironment.application;
        SQLiteDatabase database = new LMDatabaseHelper(context).getWritableDatabase();
        mCertificateStore = new SQLiteCertificateStore(database);
    }

    @Test
    public void testCertificate_save_andLoad() throws URISyntaxException {
        String certUuid = "certUuid";
        String issuerUuid = "issuerUuid";
        String name = "Sample Certificate 1";
        String description = "Welcome to the sample certificate!";
        String issuedDate = "2017-05-11T18:28:27.415+00:00";
	// This file doesn't exists, but if it did, I suspect the source correctly spells the word
	// "sample"
        String urlString = "https://certificates.learningmachine.com/certificate/sampelcertificate";

        BlockCert blockCert = BlockCertV12.createInstance(certUuid, issuerUuid, name, description, issuedDate, urlString);

        mCertificateStore.save(blockCert);

        CertificateRecord actualCertificate = mCertificateStore.load(certUuid).toBlocking().first();

        assertNotNull(actualCertificate);
        assertEquals(certUuid, actualCertificate.getUuid());
        assertEquals(issuerUuid, actualCertificate.getIssuerUuid());
        assertEquals(name, actualCertificate.getName());
        assertEquals(description, actualCertificate.getDescription());
        assertEquals(issuedDate, actualCertificate.getIssuedOn());
        assertEquals(urlString, actualCertificate.getUrlString());
    }

    @Test
    public void testCertV20_id_and_url_distinction() {
        BlockCertV20 blockCert = new BlockCertV20();
        String certId = "https://certificates.learningmachine.com/certificate/43b0224572a7541693999fd441077c68";
        blockCert.setId(certId);
        Badge badge = new Badge();
        String certUuid = "0ffcc3a8-f171-54a0-808e-f19fbbe19c58";
        String badgeUuid = "urn:uuid:" + certUuid;
        badge.setId(URI.create(badgeUuid));
        badge.setName("Cert name");
        Issuer issuer = new Issuer();
        issuer.setId(URI.create("https://certificates.learningmachine.com"));
        badge.setIssuer(issuer);
        badge.setDescription("Cert");
        blockCert.setBadge(badge);
        blockCert.setIssuedOn("2017-05-11T18:28:27.415+00:00");

        mCertificateStore.save(blockCert);
        CertificateRecord certificateRecord = mCertificateStore.load(certUuid).toBlocking().first();

        assertNotNull("Should be able to load the certificate by UUID", certificateRecord);
        assertTrue(certificateRecord.urlStringContainsUrl());
    }
}
