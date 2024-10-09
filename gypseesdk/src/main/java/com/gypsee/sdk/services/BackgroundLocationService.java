package com.gypsee.sdk.services;

import static com.gypsee.sdk.utils.Constants.LOCATION_ACCURACY;

import android.Manifest;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.app.Notification;
import android.app.NotificationChannel;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class BackgroundLocationService extends Service {
    private final LocationServiceBinder binder = new LocationServiceBinder();
    private static final String TAG = BackgroundLocationService.class.getSimpleName();

    private LocationCallback locationCallback;
    //MyPreferenece myPreferenece;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTracking();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForeground(12345678, getNotification());
                }
            } else {

            }


    }

    @Override
    public void onDestroy() {
        if (fusedLocationClient != null & locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        super.onDestroy();

    }


    private void requestCurrentLocation() {
        Log.e(TAG, "requestCurrentLocation()");

        // Request permission
        if (ActivityCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location permission granted");

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setInterval(2000); // 10 seconds
            locationRequest.setFastestInterval(1000); // 5 seconds
            locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

//            LocationRequest locationRequest = LocationRequest.create()
//                .setInterval(LOCATION_INTERVAL)
//                .setFastestInterval(LOCATION_FASTEST_INTERVAL)
//                .setSmallestDisplacement(SMALLEST_DISPLACEMENT)
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            //locationRequest.setPriority(myPreferenece.getBackgroundLocationPriority());

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    if (locationResult == null) {
                        Log.e(TAG, "locationResult null");
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        // Update UI with location data
                        // ...
                        if (location != null && location.getAccuracy() <= LOCATION_ACCURACY) {

                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("LocationReceiver").putExtra("Location", location));

                        }

                    }
                }
            };
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
        } else {
            Log.e(TAG, "Request fine location permission.");
        }
    }


    private FusedLocationProviderClient fusedLocationClient;

    public void startTracking() {
        Log.e(TAG, "Start Tracking");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        requestCurrentLocation();
    }

    public void stopTracking() {
        this.onDestroy();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification getNotification() {

        NotificationChannel channel = new NotificationChannel("channel_01", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        Notification.Builder builder = new Notification.Builder(getApplicationContext(), "channel_01").setAutoCancel(true);
        return builder.build();
    }


    public class LocationServiceBinder extends Binder {
        public BackgroundLocationService getService() {
            return BackgroundLocationService.this;
        }
    }
}
