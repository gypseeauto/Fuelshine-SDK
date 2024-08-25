package com.gypsee.sdk.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

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
public class RSATermsAndConditionsFragment extends Fragment {

    public RSATermsAndConditionsFragment() {
        // Required empty public constructor
    }


    private Context context;

    private FragmentTroubleCodeDescriptionBinding fragmentTroubleCodeDescriptionBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getContext();
        fragmentTroubleCodeDescriptionBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_trouble_code_description, container, false);

        initViews();
        loadWebview();
        return fragmentTroubleCodeDescriptionBinding.getRoot();
    }

    private void initViews() {

        fragmentTroubleCodeDescriptionBinding.callGypsee.setVisibility(View.GONE);
        fragmentTroubleCodeDescriptionBinding.messageGypsee.setVisibility(View.GONE);
        fragmentTroubleCodeDescriptionBinding.toolBarLayout.setTitle(getActivity().getIntent().getBooleanExtra("IsPrivaypolicy",false)? "Privay policy":"User Agreement");
        fragmentTroubleCodeDescriptionBinding.toolBarLayout.toolbar.setNavigationIcon(R.drawable.back_button);
        fragmentTroubleCodeDescriptionBinding.toolBarLayout.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppCompatActivity) context).finish();
            }
        });
    }

    private void loadWebview() {

        final WebView mWebview = fragmentTroubleCodeDescriptionBinding.webView;
        mWebview.getSettings().setJavaScriptEnabled(true); // enable javascript

        mWebview.setWebViewClient(new WebViewClient() {

            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String
                    failingUrl) {
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
            }
        });

        mWebview.loadUrl(getActivity().getIntent().getBooleanExtra("IsPrivaypolicy",false) ?getString(R.string.privacyPolicyUrl):"http://appadmin.gypsee.in/html/gypsee_terms_v2.html");

    }

}
