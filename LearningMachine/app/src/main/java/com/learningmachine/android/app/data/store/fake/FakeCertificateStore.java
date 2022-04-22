//package com.learningmachine.android.app.data.store.fake;
//
//import android.content.Context;
//import android.content.res.AssetManager;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Build;
//
//import com.learningmachine.android.app.data.cert.BlockCert;
//import com.learningmachine.android.app.data.model.CertificateRecord;
//import com.learningmachine.android.app.data.store.CertificateStore;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.inject.Inject;
//import javax.inject.Singleton;
//
//import rx.Observable;
//
//@Singleton
//public class FakeCertificateStore implements CertificateStore {
//
//    private final Context mContext;
//
//    @Inject
//    public FakeCertificateStore(Context context) {
//        mContext = context;
//    }
//
//    @Override
//    public Observable<CertificateRecord> load(String certId)  {
//        String content = null;
//            try {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    content = new String(Files.readAllBytes(Paths.get("cert.txt")));
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        JSONObject jsonObject = null;
//        try {
//            assert content != null;
//            jsonObject = new JSONObject(content);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        if(jsonObject != null) {
//            String certUuid = null;
//            String issuerUuid = null;
//            String name = null;
//            String description = null;
//            String issueDate = null;
//            String expirationDate = null;
//            String urlString = null;
//            String metadata = null;
//
//            try {
//                certUuid = jsonObject.getString("UUID");
//                issuerUuid = jsonObject.getString("ISSUER_UUID");
//                name = jsonObject.getString("NAME");
//                description = jsonObject.getString("DESCRIPTION");
//                issueDate = jsonObject.getString("ISSUE_DATE");
//                expirationDate = jsonObject.getString("EXPIRATION_DATE");
//                urlString = jsonObject.getString("URL");
//                metadata = jsonObject.getString("METADATA");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//            CertificateRecord certificateRecord = new CertificateRecord(certUuid, issuerUuid, name,
//                    description, issueDate, urlString, metadata, expirationDate);
//
//            return Observable.just(certificateRecord);
//        }
//        return Observable.just(null);
//    }
//
//    @Override
//    public Observable<List<CertificateRecord>> loadForIssuer(String issuerId) {
//        return Observable.just(new ArrayList<>());
//    }
//
//    @Override
//    public Observable<Void> save(BlockCert cert) {
//        String certUid = cert.getCertUid();
//        String urlString = cert.getUrl();
//        String issuerId = cert.getIssuerId();
//        String certName = cert.getCertName();
//        String certDescription = cert.getCertDescription();
//        String issueDate = cert.getIssueDate();
//        String metadata = cert.getMetadata();
//        String expirationDate = cert.getExpirationDate();
//
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("UUID", certUid);
//            jsonObject.put("NAME", certName);
//            jsonObject.put("DESCRIPTION", certDescription);
//            jsonObject.put("ISSUER_UUID", issuerId);
//            jsonObject.put("ISSUE_DATE", issueDate);
//            jsonObject.put("URL", urlString);
//            jsonObject.put("EXPIRATION_DATE", expirationDate);
//            jsonObject.put("METADATA", metadata);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//
//
//        File fl = new File("src/main/assets/cert.txt");
//        try {
//            FileInputStream fin = new FileInputStream(fl);
//            FileWriter writer = new FileWriter(fl, true);
//            cos
//            writer.write(jsonObject.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return Observable.empty();
//    }
//
//    @Override
//    public Observable<Boolean> delete(String certId) {
//        return Observable.just(false);
//    }
//}
