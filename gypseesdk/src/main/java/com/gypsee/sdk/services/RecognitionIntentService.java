package com.gypsee.sdk.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

public class RecognitionIntentService extends BroadcastReceiver {

    private String RECOGNITION_ACTION = "recognition_action";
    private String TAG = RecognitionIntentService.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        if (ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            for (ActivityTransitionEvent event : result.getTransitionEvents()) {

                int activityType = event.getActivityType();

                Log.e(TAG, "RecognitionIntentService onReceive: " + event.getActivityType() + "Confidence " + event.getElapsedRealTimeNanos());
                //comment out for simulated trip
                if (activityType == DetectedActivity.IN_VEHICLE || activityType == DetectedActivity.ON_BICYCLE) {

                    Log.e(TAG, "in Vehicle");
                    //startForeGroundService();
                    Intent intent1 = new Intent(RECOGNITION_ACTION);
                    intent1.putExtra("inVehicleActivity", true);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
                } else if ((activityType == DetectedActivity.WALKING)) {
                    Log.e(TAG, "on foot");
                    Intent intent1 = new Intent(RECOGNITION_ACTION);
                    intent1.putExtra("inVehicleActivity", false);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
                }


            }
        }


    }

}
