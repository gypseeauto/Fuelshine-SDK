package com.gypsee.sdk.fragments;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.FragmentTroubleCodeDescriptionBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class TroubleCodeDescriptionFragment extends Fragment implements View.OnClickListener {

    private FragmentTroubleCodeDescriptionBinding fragmentTroubleCodeDescriptionBinding;

    public TroubleCodeDescriptionFragment() {
        // Required empty public constructor
    }

    public static TroubleCodeDescriptionFragment newInstance(String troubleCode) {

        Bundle args = new Bundle();
        args.putString("TroubleCode", troubleCode);
        TroubleCodeDescriptionFragment fragment = new TroubleCodeDescriptionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private String troubleCode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        troubleCode = getArguments().getString("TroubleCode");
    }

    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getContext();
        fragmentTroubleCodeDescriptionBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_trouble_code_description, container, false);
        initViews();
        loadWebview();
        setUpToolBar();
        return fragmentTroubleCodeDescriptionBinding.getRoot();

    }

    private void initViews() {

        fragmentTroubleCodeDescriptionBinding.callGypsee.setOnClickListener(this);
        fragmentTroubleCodeDescriptionBinding.messageGypsee.setOnClickListener(this);
    }

    private void loadWebview() {

        final WebView mWebview = fragmentTroubleCodeDescriptionBinding.webView;
        mWebview.getSettings().setJavaScriptEnabled(true); // enable javascript

        mWebview.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(context, description, Toast.LENGTH_SHORT).show();
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
                fragmentTroubleCodeDescriptionBinding.progressBar.setVisibility(View.GONE);

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

        mWebview.loadUrl("https://www.obd-codes.com/" + troubleCode);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.callGypsee) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:06362948112"));
            context.startActivity(intent);
        } else if (id == R.id.messageGypsee) {
            sendSMS("06362948112", "Hi Gypsee, I found some issues with my vehicle. Please contact me for delivering assistance.");
        }
    }

    private void sendSMS(String phoneNo, String msg) {

        Uri uri = Uri.parse("smsto:" + phoneNo);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", msg);
        startActivity(it);


    }

    private void setUpToolBar() {
        fragmentTroubleCodeDescriptionBinding.toolBarLayout.setTitle("Trouble code description");


        fragmentTroubleCodeDescriptionBinding.toolBarLayout.toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);
        fragmentTroubleCodeDescriptionBinding.toolBarLayout.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppCompatActivity) context).finish();
            }
        });
    }


}
