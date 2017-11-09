package com.nus.iss.android.tayologin;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by Ritesh on 11/8/2017.
 */

public class WebviewActivity extends Activity {

    private static final String LOGTAG = "WebviewActivity";

    private WebView webView;
    private String loginurl = "https://tayo.casa";
    private boolean logTime;
    private long startTime;
    private long pageLoadTime;
    private boolean timeoutFlag;

    private Handler handler;
    public static final int MSG_TIMEOUT = 0xC001;
    public static final int MSG_NAVIGATE = 0xC002;

    public static final String MSG_NAV_LOGTIME = "logtime";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_login);

        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new TayoWebviewClient());
        webView.setWebChromeClient(new TayoChromeWebviewClient());
        WebSettings settings = webView.getSettings();
        settings.setSaveFormData(true);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDomStorageEnabled(true);
        settings.setSupportMultipleWindows(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);

        webView.loadUrl(loginurl);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_TIMEOUT:
                        handleTimeout();
                        return;
                    case MSG_NAVIGATE:
                        navigate(msg.getData().getString(loginurl), msg.arg1);
                        logTime = msg.getData().getBoolean(MSG_NAV_LOGTIME);
                        return;
                }
            }
        };
    }



    private void navigate(String url, int timeout) {
        if(url == null) {
            finish();
        }
        webView.stopLoading();
        if(logTime) {
            webView.clearCache(true);
        }
        startTime = System.currentTimeMillis();
        webView.loadUrl(url);

        if(timeout != 0) {
            handler.sendMessageDelayed(handler.obtainMessage(MSG_TIMEOUT),
                    timeout);
        }
    }

    private void handleTimeout() {
        int progress = webView.getProgress();
        Log.v(LOGTAG, "Page timeout triggered, progress = " + progress);
        webView.stopLoading();
        timeoutFlag = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.clearCache(true);
        webView.destroy();
    }

    public long getPageLoadTime() {
        return pageLoadTime;
    }
}


