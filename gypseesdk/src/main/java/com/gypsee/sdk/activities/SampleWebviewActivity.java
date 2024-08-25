package com.gypsee.sdk.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.ActivitySampleWebviewBinding;

public class SampleWebviewActivity extends AppCompatActivity {

    ActivitySampleWebviewBinding activitySampleWebviewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySampleWebviewBinding = DataBindingUtil.setContentView(this, R.layout.activity_sample_webview);
        loadWebview();
        setUpToolBar();
    }

    private void loadWebview() {

        final WebView mWebview = activitySampleWebviewBinding.webView;
        mWebview.getSettings().setJavaScriptEnabled(true); // enable javascript

        mWebview.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(SampleWebviewActivity.this, description, Toast.LENGTH_SHORT).show();
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }

            @Override
            public void onPageFinished(final WebView view, String url) {
                super.onPageFinished(view, url);
                activitySampleWebviewBinding.progressBar.setVisibility(View.GONE);

                view.findAllAsync("Symptoms");
                view.findNext(true);
                view.findNext(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.clearMatches();
                    }
                }, 1000);
            }
        });

        mWebview.loadUrl(getIntent().getStringExtra("Url"));

    }

    private void setUpToolBar() {
        activitySampleWebviewBinding.toolBarLayout.setTitle(getIntent().getStringExtra("Title"));

        activitySampleWebviewBinding.toolBarLayout.toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);
        activitySampleWebviewBinding.toolBarLayout.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}