package com.gypsee.sdk.firebase;


import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import com.gypsee.sdk.GypseeApplication;

public class FirebaseLogEvents {
    public static void firebaseLogEvent(String eventName) {

        Bundle bundle = new Bundle();
        bundle.putString("action", eventName);
      GypseeApplication.mFirebaseAnalytics.logEvent(eventName, bundle);
    }
}
