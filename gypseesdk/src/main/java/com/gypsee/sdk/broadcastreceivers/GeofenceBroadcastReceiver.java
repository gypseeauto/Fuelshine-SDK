package com.gypsee.sdk.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private String TAG = GeofenceBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()){
            String error = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, error);
            return;
        }


        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        switch (geofenceTransition){

            case Geofence.GEOFENCE_TRANSITION_ENTER:
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("GEOFENCE_ENTER"));
                break;

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("GEOFENCE_EXIT"));
                break;

        }




    }
}
