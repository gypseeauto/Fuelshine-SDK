package com.gypsee.sdk.firebase;


import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;


public class FirebaseLogEvents {
    public static void firebaseLogEvent(String eventName, Context context) {

        Bundle bundle = new Bundle();
        bundle.putString("action", eventName);
        FirebaseAnalytics.getInstance(context).logEvent(eventName, bundle);
    }
}
