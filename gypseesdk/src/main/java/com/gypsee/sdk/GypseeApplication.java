package com.gypsee.sdk;

import android.app.Application;

import com.google.firebase.analytics.FirebaseAnalytics;

import com.gypsee.sdk.utils.Utils;

// hello  bhaskar
public class GypseeApplication extends Application {

  public static  FirebaseAnalytics mFirebaseAnalytics;
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.registerNetworkCallback(getApplicationContext());
        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(getApplicationContext());
        appSignatureHelper.getAppSignatures();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());

    }




}
