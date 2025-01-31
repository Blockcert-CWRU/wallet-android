package com.learningmachine.android.app.ui.cert;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.CertificateManager;
import com.learningmachine.android.app.data.CertificateVerifier;
import com.learningmachine.android.app.data.IssuerManager;
import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.cert.BlockCertParser;
import com.learningmachine.android.app.data.cert.v20.BlockCertV20;
import com.learningmachine.android.app.data.error.ExceptionWithResourceString;
import com.learningmachine.android.app.data.inject.ApiModule;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.databinding.FragmentCertificateBinding;
import com.learningmachine.android.app.dialog.AlertDialogFragment;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.ui.share.DashboardRequestBody;
import com.learningmachine.android.app.ui.share.DashboardShareService;
import com.learningmachine.android.app.ui.share.ShareSuccessful;
import com.learningmachine.android.app.util.DialogUtils;
import com.learningmachine.android.app.util.FileUtils;

import java.io.File;
import java.util.UUID;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class CertificateFragment extends LMFragment {

    private static final String ARG_CERTIFICATE_UUID = "CertificateFragment.CertificateUuid";
    private static final String FILE_PROVIDER_AUTHORITY = "com.learningmachine.android.app.fileprovider";
    private static final String TEXT_MIME_TYPE = "text/plain";

    @Inject
    protected CertificateManager mCertificateManager;
    @Inject
    protected IssuerManager mIssuerManager;
    @Inject
    protected CertificateVerifier mCertificateVerifier;
    @Inject
    protected DashboardShareService dashboardShareService;

    private final String dashboardEndpointURL = "https://localhost:8800/share/certificate";
    private FragmentCertificateBinding mBinding;
    private String mCertUuid;

    public CertificateFragment() {
    }

    public static CertificateFragment newInstance(String certificateUuid) {
        Bundle args = new Bundle();
        args.putString(ARG_CERTIFICATE_UUID, certificateUuid);

        CertificateFragment fragment = new CertificateFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.obtain(requireContext())
                .inject(this);
        mCertUuid = requireArguments().getString(ARG_CERTIFICATE_UUID);
        mIssuerManager.certificateViewed(mCertUuid)
                .compose(bindToMainThread())
                .subscribe(aVoid -> Timber.d("Issuer analytics: Certificate viewed"),
                        throwable -> Timber.e(throwable, "Issuer has no analytics url."));
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_certificate, container, false);
        View view = inflater.inflate(R.layout.fragment_certificate, container, false);
        setAllItemsChecked();

        /**
         * Enable the PDALoginActivity via Intent to enable PDA Authentication
         * Set the appropriate <data> tag in the CertificateActivity in AndroidManifest.xml
         */
//        Intent intent = new Intent(getActivity(), PDALoginActivity.class);
//        startActivity(intent);
        mBinding.certBottomNavigation.setOnNavigationItemSelectedListener(item -> {
            setAllItemsChecked();
            switch (item.getItemId()) {
                case R.id.fragment_certificate_info_menu_item:
                    Timber.i("More info tapped on the Certificate display");
                    viewCertificateInfo();
                    return true;
                case R.id.fragment_certificate_verify_menu_item:
                    Timber.i("Verify Certificate tapped on the Certificate display");
                    verifyCertificate();
                    return true;
                case R.id.fragment_certificate_share_menu_item:
                    Timber.i("Share Certificate tapped on the Certificate display");
                    shareCertificate();
                    return true;
                case R.id.fragment_certificate_dashboard_share_menu_item:
                    Timber.i("Share Certificate to Dashboard tapped on the Certificate display");
                    shareCertificateToDashboard();
                    shareSuccessfulLoadScreen();
                    return true;
            }
            return false;
        });

        setBottomIconsSize(28);

        return mBinding.getRoot();
    }

    /**
     * Set the Bottom Navigation Icons size
     *
     * @param size The size (width an height) in dp
     */
    private void setBottomIconsSize(int size) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) mBinding.certBottomNavigation.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            View icon = menuView.getChildAt(i).findViewById(R.id.icon);
            TextView textView = menuView.getChildAt(i).findViewById(R.id.largeLabel);
            textView.setTextAppearance(getContext(), R.style.Text_Footer_4);
            ViewGroup.LayoutParams layoutParams = icon.getLayoutParams();
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, displayMetrics);
            layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, displayMetrics);
            icon.setLayoutParams(layoutParams);
        }
    }

    /**
     * This method will make all items in menu have the same size.
     */
    private void setAllItemsChecked() {
        Menu menu = mBinding.certBottomNavigation.getMenu();

        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            item.setChecked(false);
            item.setCheckable(false);
        }
    }

    private String displayHTML(BlockCert certificate) {
        String displayHTML;

        if (certificate instanceof BlockCertV20) {
            BlockCertV20 cert2 = (BlockCertV20) certificate;
            displayHTML = cert2.getDisplayHtml();
            if (displayHTML != null) {
                //Injecting title to img tag to prevent talkback to read base64 characters
                displayHTML = displayHTML.replace("<img", "<img title=\" \" ");
            } else {
                displayHTML = "<center>" + getString(R.string.cert_old_version_error) + "</center>";
            }
        } else {
            displayHTML = "<center>" + getString(R.string.cert_old_version_error) + "</center>";
        }

        String scalingMetaContent = "";
        String bodyDisplay = "";
        if (!displayHTML.trim().startsWith("<div")) {
            scalingMetaContent = ", maximum-scale=1.0, user-scalable=0";
        } else {
            bodyDisplay = "display: inline-block;";
        }

        String normalizeCss = "/*! normalize.css v7.0.0 | MIT License | github.com/necolas/normalize.css */html{line-height:1.15;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100%}body{margin:0}article,aside,footer,header,nav,section{display:block}h1{font-size:2em;margin:.67em 0}figcaption,figure,main{display:block}figure{margin:1em 40px}hr{box-sizing:content-box;height:0;overflow:visible}pre{font-family:monospace,monospace;font-size:1em}a{background-color:transparent;-webkit-text-decoration-skip:objects}abbr[title]{border-bottom:none;text-decoration:underline;text-decoration:underline dotted}b,strong{font-weight:inherit}b,strong{font-weight:bolder}code,kbd,samp{font-family:monospace,monospace;font-size:1em}dfn{font-style:italic}mark{background-color:#ff0;color:#000}small{font-size:80%}sub,sup{font-size:75%;line-height:0;position:relative;vertical-align:baseline}sub{bottom:-.25em}sup{top:-.5em}audio,video{display:inline-block}audio:not([controls]){display:none;height:0}img{border-style:none}svg:not(:root){overflow:hidden}button,input,optgroup,select,textarea{font-family:sans-serif;font-size:100%;line-height:1.15;margin:0}button,input{overflow:visible}button,select{text-transform:none}[type=reset],[type=submit],button,html [type=button]{-webkit-appearance:button}[type=button]::-moz-focus-inner,[type=reset]::-moz-focus-inner,[type=submit]::-moz-focus-inner,button::-moz-focus-inner{border-style:none;padding:0}[type=button]:-moz-focusring,[type=reset]:-moz-focusring,[type=submit]:-moz-focusring,button:-moz-focusring{outline:1px dotted ButtonText}fieldset{padding:.35em .75em .625em}legend{box-sizing:border-box;color:inherit;display:table;max-width:100%;padding:0;white-space:normal}progress{display:inline-block;vertical-align:baseline}textarea{overflow:auto}[type=checkbox],[type=radio]{box-sizing:border-box;padding:0}[type=number]::-webkit-inner-spin-button,[type=number]::-webkit-outer-spin-button{height:auto}[type=search]{-webkit-appearance:textfield;outline-offset:-2px}[type=search]::-webkit-search-cancel-button,[type=search]::-webkit-search-decoration{-webkit-appearance:none}::-webkit-file-upload-button{-webkit-appearance:button;font:inherit}details,menu{display:block}summary{display:list-item}canvas{display:inline-block}template{display:none}[hidden]{display:none}/*# sourceMappingURL=normalize.min.css.map */";
        String customCss = String.format("body { font-size: 12px; line-height: 1.5; margin:20px; %s } body > section { padding: 0; } body section { max-width: 100%%; word-break: break-word; } body img { max-width: 100%%; height: auto; width: inherit; }", bodyDisplay);
        String autolinkerScript = getAutolinkerScript();
        return String.format("<!doctype html><html class=\"no-js\" lang=\"\"><head><meta charset=\"utf-8\"><meta http-equiv=\"x-ua-compatible\" content=\"ie=edge\"><title></title><meta content=\"width=device-width, initial-scale=1.0%s\" name=\"viewport\" /><meta name=”viewport” content=”width=device-width” /><style type=\"text/css\">%s</style><style type=\"text/css\">%s</style></head><body><div id=\"_displayHTML\">%s</div></script><script type=\"text/javascript\">%s</script></body></html>", scalingMetaContent, normalizeCss, customCss, displayHTML, autolinkerScript);
    }

    private String getAutolinkerScript() {
        String script = "";
        try {
            FileUtils.copyAssetFile(requireContext(), "www/Autolinker.min.js", "Autolinker.js");
            script = FileUtils.getStringFromFile(requireContext().getFilesDir() + "/Autolinker.js") + "\n";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return script +
                "var _displayHTML = document.getElementById('_displayHTML');\n" +
                "_displayHTML.innerHTML = Autolinker.link(_displayHTML.innerHTML, {\n" +
                "    stripPrefix: false,\n" +
                "    email: true,\n" +
                "    phone: false\n" +
                "  });";
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        // Note: This entire code has been reworked to more closely match the iOS application.
        mBinding.webView.setWebViewClient(new LMWebViewClient());
        mBinding.progressBar.setVisibility(View.VISIBLE);
        mBinding.webView.setInitialScale(1);
        WebSettings webSettings = mBinding.webView.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true);

        mCertificateVerifier.loadCertificate(mCertUuid)
                .compose(bindToMainThread())
                .subscribe(certificate -> {
                    String html = displayHTML(certificate);
                    String encodedHtml = Base64.encodeToString(html.getBytes(), Base64.NO_PADDING);
                    mBinding.webView.loadData(encodedHtml, "text/html; charset=UTF-8", "base64");
                }, throwable -> {
                    Timber.e(throwable, "Could not setup webview.");

                    ExceptionWithResourceString throwableRS = (ExceptionWithResourceString) throwable;
                    showFailureDialog(throwableRS.getErrorMessageResId());
                });
    }

    public class LMWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Handle local URLs
            Uri uri = Uri.parse(url);
            if (uri.getScheme() != null && uri.getScheme().equalsIgnoreCase("mailto")) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(intent);
                return true;
            } else if (uri.getHost() != null && uri.getHost().length() > 0) {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mBinding.progressBar.setVisibility(View.GONE);
        }
    }


    private void shareCertificate() {
        String certUuid = requireArguments().getString(ARG_CERTIFICATE_UUID);
        mCertificateManager.getCertificate(certUuid)
                .compose(bindToMainThread())
                .subscribe(certificateRecord -> {
                    if (certificateRecord.urlStringContainsUrl()) {
                        showShareTypeDialog();
                    } else {
                        shareCertificateTypeResult(true);
                    }
                }, throwable -> Timber.e(throwable, "Unable to share certificate"));
    }

    // Method for Dashboard Sharing
    private void  shareCertificateToDashboard() {
        String certUuid = requireArguments().getString(ARG_CERTIFICATE_UUID);
        final BlockCert[] blockCert = new BlockCert[1];
        mCertificateManager.getCertificate(certUuid)
                .compose(bindToMainThread())
                .subscribe(certificateRecord -> {
                    String cert = null;
                    try {
                        cert = FileUtils.getCertificateFileJSON(requireContext(), mCertUuid);
                        BlockCertParser blockCertParser = new BlockCertParser();
                        blockCert[0] = blockCertParser.fromJson(cert);
                        Timber.i(cert);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    DashboardRequestBody dashboardRequestBody =
                            new DashboardRequestBody(cert, UUID.randomUUID().toString());
                    postData(dashboardRequestBody);
                    //dashboardShareService.sendCert(blockCert[0]);
                    Timber.i("Certificate POST request made");
                }, throwable -> Timber.e(throwable, "Unable to share certificate"));
    }

    public void shareSuccessfulLoadScreen() {
        Intent intent = new Intent(getActivity(), ShareSuccessful.class);
        startActivity(intent);
    }

    private void postData(DashboardRequestBody dashboardRequestBody) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        BlockCertParser blockCertParser = new BlockCertParser();

        // on below line we are creating a retrofit
        // builder and passing our base url
        Retrofit retrofit = new Retrofit.Builder()
                .client(ApiModule.defaultClient(ApiModule.loggingInterceptor()))
                .baseUrl(dashboardEndpointURL)
                // as we are sending data in json format so
                // we have to add Gson converter factory
                .addConverterFactory(GsonConverterFactory.create(blockCertParser.mGson))
                // at last we are building our retrofit builder.
                .addCallAdapterFactory(
                        RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
        // below line is to create an instance for our retrofit api class.
        DashboardShareService retrofitAPI = retrofit.create(DashboardShareService.class);


        // calling a method to create a post and passing our modal class.
        Call<DashboardRequestBody> call = retrofitAPI.sendCert(dashboardRequestBody);
        // on below line we are executing our method.
        call.enqueue(new Callback<DashboardRequestBody>() {
            @Override
            public void onResponse(@NonNull Call<DashboardRequestBody> call, @NonNull Response<DashboardRequestBody> response) {
                // we are getting response from our body
                // and passing it to our modal class.
                // on below line we are getting our data from modal class and adding it to our string.
                System.out.println("Response" + response);
            }

            @Override
            public void onFailure(Call<DashboardRequestBody> call, Throwable t) {
                Timber.i("Failure");
            }
        });
    }

    private void showShareTypeDialog() {
        Timber.i("Showing share certificate dialog for " + mCertUuid);
        AlertDialogFragment fragment = DialogUtils.showCustomSheet(getContext(), this,
                R.layout.dialog_share_file_or_url,
                0,
                "",
                "",
                "",
                "",
                btnIdx -> {
                    if (btnIdx.equals(0)) {
                        Timber.i("User chose to share certificate via file");
                        shareCertificateTypeResult(true);
                    }
                    if (btnIdx.equals(1)) {
                        Timber.i("User chose to share the certificate via URL");
                        shareCertificateTypeResult(false);
                    }
                },
                (dialogContent) -> {},
                (dialogContent) -> Timber.i("Share dialog cancelled"));
    }

    private void shareCertificateTypeResult(boolean shareFile) {
        mIssuerManager.certificateShared(mCertUuid)
                .compose(bindToMainThread())
                .subscribe(aVoid -> Timber.d("Issuer analytics: Certificate shared"),
                        throwable -> Timber.e(throwable, "Issuer has no analytics url."));
        Observable.combineLatest(mCertificateManager.getCertificate(mCertUuid),
                mIssuerManager.getIssuerForCertificate(mCertUuid),
                Pair::new)
                .compose(bindToMainThread())
                .subscribe(pair -> {
                    CertificateRecord cert = pair.first;

                    Intent intent = new Intent(Intent.ACTION_SEND);

                    IssuerRecord issuer = pair.second;
                    String issuerName = "dummyIssuerName";//issuer.getName();";

                    String sharingText;

                    if (shareFile) {
                        File certFile = FileUtils.getCertificateFile(requireContext(), mCertUuid);
                        Uri uri = FileProvider.getUriForFile(requireContext(), FILE_PROVIDER_AUTHORITY, certFile);
                        String type = requireContext().getContentResolver().getType(uri);
                        intent.setType(type);
                        intent.putExtra(Intent.EXTRA_STREAM, uri);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        sharingText = getString(R.string.fragment_certificate_share_file_format, issuerName);
                    } else {
                        intent.setType(TEXT_MIME_TYPE);
                        String certUrlString = cert.getUrlString();
                        sharingText = getString(R.string.fragment_certificate_share_url_format,
                                issuerName,
                                certUrlString);
                    }

                    intent.putExtra(Intent.EXTRA_TEXT, sharingText);
                    startActivity(intent);
                }, throwable -> Timber.e(throwable, "Unable to share certificate"));
    }

    private void viewCertificateInfo() {
        Intent intent = CertificateInfoActivity.newIntent(requireActivity(), mCertUuid);
        startActivity(intent);
    }

    private void showFailureDialog(int errorId) {
        DialogUtils.showAlertDialog( this,
                R.drawable.ic_dialog_failure,
                getResources().getString(R.string.cert_verification_failure_title),
                getResources().getString(errorId),
                null,
                getResources().getString(R.string.ok_button),
                (btnIdx) -> {});
    }


    private void verifyCertificate() {
        Timber.i("User tapped verify on this certificate");
        Intent certificateActivity = VerifyCertificateActivity.newIntent(requireContext(), mCertUuid);
        startActivity(certificateActivity);
    }
}