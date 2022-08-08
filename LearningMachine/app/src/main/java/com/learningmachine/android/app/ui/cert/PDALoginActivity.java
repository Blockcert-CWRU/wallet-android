package com.learningmachine.android.app.ui.cert;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.learningmachine.android.app.R;

import timber.log.Timber;

public class PDALoginActivity extends AppCompatActivity {

    private String  pdaLoginURL = "https://hatters.dataswift.io/services/login?application_id=ad-s-walletandroid&redirect_uri=blockcerts://success";
    private String registrationURL = "https://hatters.dataswift.io/services/signup?application_id=ad-s-walletandroid&redirect_uri=blockcerts://success";
    @Override
    //val mWebView = findViewById(R.id.webView)
    //mWebView.loadUrl(url)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdalogin);
        WebView myWebView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        Timber.i("Started PDA Login Activity");
        myWebView.loadUrl(registrationURL);
    }
}