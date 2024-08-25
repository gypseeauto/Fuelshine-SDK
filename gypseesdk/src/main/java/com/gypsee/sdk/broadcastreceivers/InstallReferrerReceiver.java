package com.gypsee.sdk.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.gypsee.sdk.config.MyPreferenece;

public class InstallReferrerReceiver extends BroadcastReceiver {

    String referrer = "";
    private String TAG = InstallReferrerReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals("com.android.vending.INSTALL_REFERRER")) {

                Bundle extras = intent.getExtras();
                if (extras != null)
                {
                    referrer = extras.getString("referrer");

                    Log.e(TAG, "===>" + referrer);
                    new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).saveStringData(MyPreferenece.installReferrer, referrer);

                }
            }
        }
    }
}