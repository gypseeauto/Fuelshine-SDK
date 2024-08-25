package com.gypsee.sdk.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.BackoffPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import com.gypsee.sdk.workmanager.RestartForegroundService;

//To restart foreground service when device reboots
public class RebootBroadcastReceiver extends BroadcastReceiver {
    WorkManager mWorkManager;
    PeriodicWorkRequest rebootRequest;
    String TAG = getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "intent.getAction(): "+intent.getAction());

        //Reboot worker
        mWorkManager = WorkManager.getInstance(context);
        rebootRequest = new PeriodicWorkRequest.Builder(RestartForegroundService.class,
                30, TimeUnit.MINUTES)
                .addTag("Restart")
                .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                .build();

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            //do your work and enqueue it
            mWorkManager.enqueue(rebootRequest);
        }
    }
}
