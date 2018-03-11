package com.example.sallarkhan.fixuos_admin;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BrowserActivity extends AppCompatActivity {

    private String url ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        Bundle bundle = getIntent().getExtras();


        if(bundle.getString("url")!= null)
        {
            url = bundle.getString("url");
            WebView webView= (WebView)findViewById(R.id.webView);

            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);

            webView.getSettings().setBuiltInZoomControls(true);


//findViewById returns an instance of View ,which is casted to target class
            webView.setWebViewClient(new WebViewClient());

            webView.getSettings().setJavaScriptEnabled(true);
//This statement is used to enable the execution of JavaScript.

            webView.setVerticalScrollBarEnabled(false);


//This statement hides the Vertical scroll bar and does not remove it.

            webView.setHorizontalScrollBarEnabled(false);
//This statement hides the Horizontal scroll bar and does not remove it.

            webView.loadUrl(url);

//**************************8888*****?///////////////////////******************************************88
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedError(WebView view, int errorCode,
                                            String description, String failingUrl) {
                    Log.d("WEB_VIEW_TEST", "error code:" + errorCode + " - " + description);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    // handle different requests for different type of files
                    // this example handles downloads requests for .apk and .mp3 files
                    // everything else the webview can handle normally
                    if (url.endsWith(".apk")) {
                        Uri source = Uri.parse(url);
                        // Make a new request pointing to the .apk url
                        DownloadManager.Request request = new DownloadManager.Request(source);
                        // appears the same in Notification bar while downloading
                        request.setDescription("Description for the DownloadManager Bar");
                        request.setTitle("YourApp.apk");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            request.allowScanningByMediaScanner();
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        }
                        // save the file in the "Downloads" folder of SDCARD
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "SmartPigs.apk");
                        // get download service and enqueue file
                        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        manager.enqueue(request);
                    } else if (url.endsWith(".mp3")) {
                        // if the link points to an .mp3 resource do something else
                    }
                    // if there is a link to anything else than .apk or .mp3 load the URL in the webview
                    else view.loadUrl(url);
                    return true;
                }
            });

        }
    }

}
