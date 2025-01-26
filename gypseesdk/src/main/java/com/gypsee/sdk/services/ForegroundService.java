package com.gypsee.sdk.services;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION;
import static java.lang.Math.round;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.RuntimeCommand;
import com.github.pires.obd.enums.AvailableCommandNames;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.JsonParser;
import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.ConfigActivity;
import com.gypsee.sdk.activities.GypseeMainActivity;
import com.gypsee.sdk.activities.SplashActivity;
import com.gypsee.sdk.broadcastreceivers.GeofenceBroadcastReceiver;
import com.gypsee.sdk.broadcastreceivers.NotificationReceiver;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.config.ObdConfig;
import com.gypsee.sdk.database.DatabaseExecutor;
import com.gypsee.sdk.database.DatabaseHelper;
import com.gypsee.sdk.database.DrivingAlert;
import com.gypsee.sdk.database.TripAlert;
import com.gypsee.sdk.database.TripDatabase;
import com.gypsee.sdk.database.TripLatLong;
import com.gypsee.sdk.enums.EcoSpeedEnums;
import com.gypsee.sdk.fragments.HomeFragment;
import com.gypsee.sdk.helpers.BluetoothHelperClass;
import com.gypsee.sdk.helpers.DistanceCalculator;
import com.gypsee.sdk.interfaces.BlueToothConnectionInterface;
import com.gypsee.sdk.io.AbstractGatewayService;
import com.gypsee.sdk.io.LogCSVWriter;
import com.gypsee.sdk.io.ObdCommandJob;
import com.gypsee.sdk.io.ObdGatewayService;
import com.gypsee.sdk.io.TempTripData;
import com.gypsee.sdk.models.BluetoothDeviceModel;
import com.gypsee.sdk.models.GypseeThresholdValues;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.models.VehicleAlertModel;
import com.gypsee.sdk.models.Vehiclemodel;
import com.gypsee.sdk.network.InternetAccessThread;
import com.gypsee.sdk.network.RetrofitClient;
import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;
import com.gypsee.sdk.serverclasses.GypseeApiService;
import com.gypsee.sdk.threads.ConnectThread;
import com.gypsee.sdk.trips.TripRecord;
import com.gypsee.sdk.utils.Constants;
import com.gypsee.sdk.utils.FleetSocketConnection;
import com.gypsee.sdk.utils.GpsUtils;
import com.gypsee.sdk.utils.NetworkConnectionCallback;
import com.gypsee.sdk.utils.TimeUtils;
import com.gypsee.sdk.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gypsee.sdk.helpers.BluetoothHelperClass.LookUpCommand;
import static com.gypsee.sdk.helpers.BluetoothHelperClass.getBluetoothDevices;
import static com.gypsee.sdk.utils.Constants.ABOVE_ECO_SPEED;
import static com.gypsee.sdk.utils.Constants.ABOVE_ECO_SPEED_HINDI;
import static com.gypsee.sdk.utils.Constants.ABOVE_ECO_SPEED_KANNADA;
import static com.gypsee.sdk.utils.Constants.ABOVE_ECO_SPEED_MARATHI;
import static com.gypsee.sdk.utils.Constants.ABOVE_ECO_SPEED_TAMIL;
import static com.gypsee.sdk.utils.Constants.ABOVE_ECO_SPEED_TELUGU;
import static com.gypsee.sdk.utils.Constants.ECO_SPEED;
import static com.gypsee.sdk.utils.Constants.ECO_SPEED_HINDI;
import static com.gypsee.sdk.utils.Constants.ECO_SPEED_KANNADA;
import static com.gypsee.sdk.utils.Constants.ECO_SPEED_MARATHI;
import static com.gypsee.sdk.utils.Constants.ECO_SPEED_TAMIL;
import static com.gypsee.sdk.utils.Constants.ECO_SPEED_TELUGU;
import static com.gypsee.sdk.utils.Constants.HARSH_ACC;
import static com.gypsee.sdk.utils.Constants.HARSH_ACC_HINDI;
import static com.gypsee.sdk.utils.Constants.HARSH_ACC_KANNADA;
import static com.gypsee.sdk.utils.Constants.HARSH_ACC_MARATHI;
import static com.gypsee.sdk.utils.Constants.HARSH_ACC_TAMIL;
import static com.gypsee.sdk.utils.Constants.HARSH_ACC_TELUGU;
import static com.gypsee.sdk.utils.Constants.HARSH_BRAKING;
import static com.gypsee.sdk.utils.Constants.HARSH_BRAKING_HINDI;
import static com.gypsee.sdk.utils.Constants.HARSH_BRAKING_KANNADA;
import static com.gypsee.sdk.utils.Constants.HARSH_BRAKING_MARATHI;
import static com.gypsee.sdk.utils.Constants.HARSH_BRAKING_TAMIL;
import static com.gypsee.sdk.utils.Constants.HARSH_BRAKING_TELUGU;
import static com.gypsee.sdk.utils.Constants.OVER_SPEED;
import static com.gypsee.sdk.utils.Constants.OVER_SPEED_HINDI;
import static com.gypsee.sdk.utils.Constants.OVER_SPEED_KANNADA;
import static com.gypsee.sdk.utils.Constants.OVER_SPEED_MARATHI;
import static com.gypsee.sdk.utils.Constants.OVER_SPEED_TAMIL;
import static com.gypsee.sdk.utils.Constants.OVER_SPEED_TELUGU;
import static com.gypsee.sdk.utils.Constants.connectToBluetoothDevice;
import static com.gypsee.sdk.utils.Constants.deviceConnected;
import static com.gypsee.sdk.utils.Constants.fuelBurnMode;
import static com.gypsee.sdk.utils.Constants.fuelSaveMode;
import static com.gypsee.sdk.utils.Constants.harshAccelerationDetected;
import static com.gypsee.sdk.utils.Constants.harshBrakingDetected;
import static com.gypsee.sdk.utils.Constants.hindiNudge1;
import static com.gypsee.sdk.utils.Constants.hindiNudge2;
import static com.gypsee.sdk.utils.Constants.hindiNudge3;
import static com.gypsee.sdk.utils.Constants.hindiNudge4;
import static com.gypsee.sdk.utils.Constants.hindiNudge5;
import static com.gypsee.sdk.utils.Constants.initialMsg;
import static com.gypsee.sdk.utils.Constants.notRewarded;
import static com.gypsee.sdk.utils.Constants.overspeeding;
import static com.gypsee.sdk.utils.Constants.rewarded;
import static com.gypsee.sdk.utils.Constants.switchOnBluetooth;
import static com.gypsee.sdk.utils.Constants.tracking;
import static com.gypsee.sdk.utils.Constants.tripEndedMsg;
import static com.gypsee.sdk.utils.Constants.tripEndedMsgHindi;
import static com.gypsee.sdk.utils.Constants.tripStartedMsg;
import static com.gypsee.sdk.utils.Constants.tripStartedMsgHindi;

public class ForegroundService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final static String NOTIFICATION_CHANNEL = "BLUETOOTH_FOREGROUND_SERVICE";
    private final static String NOTIFICATION_ID = "BLUETOOTH_SERVICE_ID";
    private final static int PENDING_INTENT_REQUEST_CODE = 400;
    private final static int NOTIFICATION_REQUEST_CODE = 401;
    private final String TAG = ForegroundService.class.getSimpleName();
    private Map<String, String> dtcVals;
    TextToSpeech textToSpeech;

    private TripDatabase tripDatabase;

    private GeofencingClient geofencingClient;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private GypseeMainActivity activity;

    public void setActivity(GypseeMainActivity activity) {
        this.activity = activity;
    }

    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }


    private NetworkConnectionCallback networkCallback;
    private ConnectivityManager connectivityManager;
    private boolean isConfigCalled = false;

    private BroadcastReceiver startTripReceiver;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this);
//        maxSpeed = Integer.parseInt(defaultSharedPreferences.getString(ConfigActivity.over_speed_preference, "90"));

        Log.e(TAG,"Is Config Called = "+ isConfigCalled);

        if (harshAccelaration != null || harshDecelaration != null){
            isConfigCalled = true;
        }

        if (!isConfigCalled){
            fetchConfigValues();
        }

        // Create a BroadcastReceiver to handle starting the trip
        startTripReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Check if the broadcast is the start trip intent
                if ("com.example.start_trip".equals(intent.getAction())) {
                    startManualTrip(true);
                }
            }
        };

        // Register the receiver to listen for the start_trip broadcast
        IntentFilter filter = new IntentFilter("com.example.start_trip");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(startTripReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        }

        myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, this);
        Log.e(TAG, ": " );
        dtcVals = getDict(R.array.dtc_keys, R.array.dtc_values);

        initApi = myPreferenece.getInitApi();

        if (tripDatabase == null) {
            tripDatabase = TripDatabase.getDatabase(ForegroundService.this);
        }

        geofencingClient = LocationServices.getGeofencingClient(getApplicationContext());
        if (isServiceBound) {
            new Handler().postDelayed(mQueueCommands, 1500);
            sendMyBroadcast(0);
            if (currentTrip != null) {
                sendMyBroadcast(3);
                myPreferenece.setIsTripRunning(true);
            }
        } else {
            initApi = true;
            myPreferenece.setInitApi(initApi);
        }
        performTasksOnStart();
        return START_STICKY;
    }

    private void performTasksOnStart() {
        setupTTS();
        showBluetoothForegroundNotification();
        setupRecognitionClient();
        registerInternetConnectionObserver();

        if (isManualStart){
            isServiceBound = true;
            isObdConnected = false;
        }else {
            checkRegisteredDeviceConnected();
        }
        // For removing bluetooth dependency add these lines
        // isServiceBound = true;
        //        isObdConnected = false;
//        checkRegisteredDeviceConnected(); // remove this method for removing bluetooth dependency
        registerLocationBroadcastReceiver();

    }


//    private FusedLocationProviderClient fusedLocationProviderClient;
//    private LocationRequest locationRequest;
//
//    @SuppressLint("MissingPermission")
//    public void getContinuousLocation() {
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//
//        // Instantiating the LocationRequest and setting the priority and the interval to update the location.
//        locationRequest = LocationRequest.create();
//        locationRequest.setInterval(1000);  // Set interval to 1000 milliseconds (1 second)
//        locationRequest.setFastestInterval(500);  // Set fastest interval to 500 milliseconds (0.5 seconds)
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        // Instantiating the LocationCallback
//        LocationCallback locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                if (locationResult != null) {
//                    // Showing the latitude, longitude, and accuracy
//                    for (Location location : locationResult.getLocations()) {
//                        // Log the location details or handle them as needed
//                        // For example:
//                        // Log.d("LocationUpdate", "Lat: " + location.getLatitude() + ", Long: " + location.getLongitude() + ", Accuracy: " + location.getAccuracy());
//                    }
//                }
//            }
//        };
//
//        // Check for location permissions
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // Consider calling ActivityCompat#requestPermissions
//            // Here, we return if permissions are not granted
//            return;
//        }
//
//        // Request location updates
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//    }
//


    private void registerInternetConnectionObserver() {
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        networkCallback = new NetworkConnectionCallback(connectivityManager, this);

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);

    }


    private final IBinder binder = new ForegroundServiceBinder();

    public void clearLogs() {
        logBuilder = new StringBuilder();
        addLog("");
    }

    public class ForegroundServiceBinder extends Binder {
        public ForegroundService getService() {
            return ForegroundService.this;
        }
    }


    public void insertTripLatLongtoDb(TripLatLong tripLatLong) {
        DatabaseExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Log.e("Inserting db", "Inserting trip to db");

                if (tripDatabase == null) {
                    tripDatabase = TripDatabase.getDatabase(ForegroundService.this);
                }

                if (currentTrip != null) {
                    tripDatabase.tripDao().insertTrip(tripLatLong);
                    Log.e("Inserting trip", "Inserting Trip Lat long" + tripLatLong.getTripId() + " " + tripLatLong.getLatitude() + " " + tripLatLong.getLongitude());
//                    addLog("Inserting Trip Lat long" + " Trip id = " + tripLatLong.getTripId() + " Trip latitude = " + tripLatLong.getLatitude()  + " Trip longitude = " +tripLatLong.getLongitude() + " Time = " + getTime());
                }
            }
        });
    }
    private boolean isManualStart;

    public void endManualTrip() {
        addLog("End Manual trip Clicked");
        Log.e("ManualTripEnd", "End Manual trip called");
        stopLiveData();

        if (context instanceof GypseeMainActivity) {
            GypseeMainActivity activity = (GypseeMainActivity) context;
            HomeFragment fragment = (HomeFragment) activity.getSupportFragmentManager().findFragmentById(R.id.mainFrameLayout);
            if (fragment != null) {
                fragment.showEndTripBox();
            } else {
                Log.e("WrapTxt", "HomeFragment is null");
            }
        } else {
            Log.e("WrapTxt", "context is not MainActivity or is null");
        }


//        if (activity != null) {
//            HomeFragment fragment = (HomeFragment) activity.getSupportFragmentManager().findFragmentById(R.id.mainFrameLayout);
//            if (fragment != null) {
//                fragment.showEndTripBox();
//            }else {
//                Log.e("WrapTxt","Homefragment is null");
//            }
//        }else {
//            Log.e("WrapTxt","activity is null");
//
//        }

    }

    public void startManualTrip(Boolean isManualStart) {

        if (networkCallback.isNetWorkAvailable) {
            isServiceBound = false;
            addLog("Start Manual trip Clicked");
            Log.e("ManualTripStart", "Start manual trip called");
            this.isManualStart = isManualStart;
            onActivityCaptured();

        } else {
            sendMyBroadcast(7);
        }
    }


    private void setupTTS() {
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    Log.e(TAG, "TTS initialized");
                    Locale deviceLocale = getResources().getConfiguration().locale;
                    textToSpeech.setLanguage(deviceLocale);

                    String language = myPreferenece.getLang();
                    textToSpeech.stop();

                    User user = myPreferenece.getUser();
                    if (user != null && user.getUserFullName() != null) {
                        switch (language) {
                            case "hi":
                                textToSpeech.speak("फ्यूल शाइन में आपका स्वागत है।" + user.getUserFullName(), TextToSpeech.QUEUE_FLUSH, null, null);
                                break;
                            default:
                                textToSpeech.speak("Welcome to Fuel Shine, " + user.getUserFullName(), TextToSpeech.QUEUE_FLUSH, null, null);
                                break;
                        }
                    } else {
                        Log.e(TAG, "User or UserFullName is null");
                    }
                } else {
                    Log.e(TAG, "TTS failed");
                }
            }
        });
    }

    private GeofencingRequest getGeofencingRequest(LatLng centerLatLng, float radius) {
        String GEOFENCE_REQUEST_ID = "GEOFENCE_REQUEST_ID";

        // Handle the case where centerLatLng is null
        if (centerLatLng == null) {
            Log.e("GeofencingRequest", "centerLatLng is null, cannot create geofence.");
            return null; // Return null or handle it appropriately
        }

        Geofence.Builder builder = new Geofence.Builder()
                .setRequestId(GEOFENCE_REQUEST_ID)
                .setCircularRegion(centerLatLng.latitude, centerLatLng.longitude, radius)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(Geofence.NEVER_EXPIRE);

        GeofencingRequest.Builder requestBuilder = new GeofencingRequest.Builder();
        requestBuilder.setInitialTrigger(0);
        requestBuilder.addGeofence(builder.build());

        return requestBuilder.build();
    }

    private PendingIntent geofencePendingIntent;
    private int GEOFENCE_REQUEST_CODE = 501;

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(getApplicationContext(), GeofenceBroadcastReceiver.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            geofencePendingIntent = PendingIntent.getBroadcast(getApplicationContext(), GEOFENCE_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        } else {
            geofencePendingIntent = PendingIntent.getBroadcast(getApplicationContext(), GEOFENCE_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return geofencePendingIntent;
    }


    private void addGeofence(LatLng centerLatLng, float radius) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e(TAG, "returning fine location");
            return;
        }
        geofencingClient.addGeofences(getGeofencingRequest(centerLatLng, radius), getGeofencePendingIntent())
                .addOnSuccessListener(unused -> Log.e(TAG, "SUCCESS! Geofence added."))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "FAILED! Geofence not added.");
                        e.printStackTrace();
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }
                });
    }


    private void removeGeofence() {
        if (geofencePendingIntent != null) {
            geofencingClient.removeGeofences(geofencePendingIntent)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.e(TAG, "Geofence successfully removed");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Geofence not removed");
                            e.printStackTrace();
                            FirebaseCrashlytics.getInstance().recordException(e);
                        }
                    });
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startWidgetService() {
        if (Settings.canDrawOverlays(getApplicationContext())) {
            Intent newIntent = new Intent(getApplicationContext(), FloatingWidgetService.class);
            //bindService(newIntent, widgetServiceConn, BIND_AUTO_CREATE);
            startService(newIntent);
        }
    }

    private void stopWidgetService() {
        Log.e(TAG, "stop widget called");

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(getString(R.string.stop_widget)));
    }


    private void startBackGroundLocationService() {
        //starting when app is started, and when activity is captured
        Log.e(TAG, "Start BackgroundLocation Service method called");

        GpsUtils gpsUtils = new GpsUtils(getApplicationContext());
        gpsUtils.turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                if (isGPSEnable) {
                    //prepare service
                    Log.e(TAG, "Starting BackgroundLocation Service");
                    /*final */


                    Intent intent = new Intent(getApplicationContext(), BackgroundLocationService.class);
                    startService(intent);
                }
            }
        });

    }


    private boolean isObdConnected = false;

    private ActivityRecognitionClient recognitionClient;
    PendingIntent recognitionPendingIntent;

    private void setupRecognitionClient() {
        Intent intent = new Intent(this, RecognitionIntentService.class);
        //Intent intent = new Intent("ACTIVITY_RECOGNITION");
        int RECOGNITION_PENDING_INTENT_REQUEST_CODE = 111;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            recognitionPendingIntent = PendingIntent.getBroadcast(this, RECOGNITION_PENDING_INTENT_REQUEST_CODE, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            recognitionPendingIntent = PendingIntent.getBroadcast(this, RECOGNITION_PENDING_INTENT_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        registerRecognitionBroadcastReceiver();
        requestRecognitionUpdates();
    }

    private void requestRecognitionUpdates() {

        List<ActivityTransition> transitions = new ArrayList<>();

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.IN_VEHICLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());


        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.STILL)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.WALKING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.ON_BICYCLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        ActivityTransitionRequest request = new ActivityTransitionRequest(transitions);


        recognitionClient =  ActivityRecognition.getClient(getApplicationContext());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "requestRecognitionUpdates: " );
            return;
        }

        Task<Void> task = recognitionClient.requestActivityTransitionUpdates(request, recognitionPendingIntent);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "request recognition updates success");
                //Toast.makeText(getApplicationContext(), "Recognition started", Toast.LENGTH_LONG).show();
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                requestRecognitionUpdates();

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1235");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Activity Recognition Fail");
                FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("Activity_Recognition_Fail", bundle);

                Log.e(TAG, "request recognition updates failed: " + e.getMessage());
                //Toast.makeText(getApplicationContext(), "Recognition failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });
        //isRecognitionApiRunning = true;

    }

    private void deRegisterActivityRecognitionClient() {
        // myPendingIntent is the instance of PendingIntent where the app receives callbacks.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            addLog("Activity Recognition Permission not granted");
            return;
        }

        if(recognitionPendingIntent==null){
            return;
        }
        Task<Void> task = ActivityRecognition.getClient(this)
                .removeActivityTransitionUpdates(recognitionPendingIntent);

        task.addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        recognitionPendingIntent.cancel();
                        Log.e(TAG, "onSuccess: deRegisterActivityRecognitionClient" );

                    }
                }
        );

        task.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("MYCOMPONENT", e.getMessage());
                    }
                }
        );
    }

    private void checkRegisteredDeviceConnected() {

        if (enableBluetooth()) {

            ArrayList<BluetoothDevice> connectedDevices = getConnectedDevices();

            for (BluetoothDevice device : connectedDevices) {
                ArrayList<BluetoothDeviceModel> models = new DatabaseHelper(getApplicationContext()).fetchRegisteredDevices();
                for (BluetoothDeviceModel model : models) {
                    if (device.getAddress().equals(model.getMacAddress())) {
                        //myPreferenece.setConnectedDeviceMac(model.getMacAddress());
                        if (currentTrip == null && (inVehicleActivity || isManualStart)) {
                            isServiceBound = true;
                            isObdConnected = false;
                            myPreferenece.setConnectedDeviceMac(model.getMacAddress());
                            checkForVehicle("");
                            return;
                        }
                    }
                }
            }

            if (!isServiceBound && !isObdConnected && (inVehicleActivity || isManualStart)) {
                activityCaptured = false;
                isManualStart = false;
                textToSpeech.speak(connectToBluetoothDevice, TextToSpeech.QUEUE_FLUSH, null, null);
                sendMyBroadcast(2);
            }

        }
    }


    public ArrayList<BluetoothDeviceModel> fetchRegisteredDevices() {

        return new DatabaseHelper(getApplicationContext()).fetchRegisteredDevices();

    }


    private static final String CHANNEL_ID = "NotificationServiceChannel";
    private static final int NOTIFICATION_ID_BASE = 1000;
    private static final long DELAY = 600000; // 10 minutes in milliseconds
//    private static final long DELAY = 60000; // 1 minutes in milliseconds

    private Handler handler;
    private boolean notificationSent = false;
    private boolean shouldSendNotifications = true;

    private String[] notificationMessages = {
            "Your eco-drive rewards are waiting! Open the app and start your trip.",
            "Drive better, save smarter! Start Fuelshine for fuel-efficient tips.",
            "Fuel costs high? Drive smart with Fuelshine. Open the app to start Savings!",
            "Save up to 30% on fuel! Open Fuelshine and start saving today.",
            "Smart driving. Big savings. Open Fuelshine to begin your journey!"
    };

    public ArrayList<BluetoothDeviceModel> getConnectedRegisteredDevices(ArrayList<BluetoothDeviceModel> deviceList) {
        ArrayList<BluetoothDevice> connectedDevices = getConnectedDevices();
        HashSet<String> deviceSet = new HashSet<>();

        if (connectedDevices.size() == 0) {
            for (int i = 0; i < deviceList.size(); i++) {
                deviceList.get(i).setNowConnected(false);
            }
            notificationSent = false;
        } else {
            for (int i = 0; i < deviceList.size(); i++) {
                for (BluetoothDevice device : connectedDevices) {
                    if (device.getAddress().equals(deviceList.get(i).getMacAddress())) {
                        deviceList.get(i).setNowConnected(true);
                        deviceSet.add(device.getAddress());
                        Log.e(TAG,"Connected Device Now = "+device.getName());

                        if (!notificationSent && shouldSendNotifications && currentTrip == null) {
                            handler = new Handler(Looper.getMainLooper());
                            sendNotificationsOneByOne();
                            notificationSent = true;  // Set the flag to true after sending notification
                        }

//                        handler.post(notificationRunnable);

                    } else if (!deviceSet.contains(deviceList.get(i).getMacAddress())) {
                        deviceList.get(i).setNowConnected(false);
                    }
                }
            }
        }
        return deviceList;
    }

    private void sendNotificationsOneByOne() {

        if (!shouldSendNotifications && currentTrip == null) {
            return;
        }

        for (int i = 0; i < notificationMessages.length; i++) {
            final int notificationIndex = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (shouldSendNotifications && currentTrip == null) {
                        sendNotification(notificationIndex);
                    }
                }
            }, DELAY * (notificationIndex + 1)); // Delay for each notification (1, 2, 3,... minute delay)
        }
    }


    private void sendNotification(int notificationIndex) {
        if (!shouldSendNotifications) {
            return;  // Stop sending notifications if flag is set to false
        }

        String language = myPreferenece.getLang();
        textToSpeech.stop();

        // Define the Hindi nudge messages
        String[] hindiNudges = {hindiNudge1, hindiNudge2, hindiNudge3, hindiNudge4, hindiNudge5};

        // Check the notification index bounds
        if (notificationIndex < 0 || notificationIndex >= notificationMessages.length) {
            Log.e(TAG, "Invalid notification index: " + notificationIndex);
            return;
        }

        // Determine the message to call the speaker
        String messageToSpeak;
        if ("hi".equals(language)) {
            messageToSpeak = hindiNudges[notificationIndex]; // Get the corresponding Hindi message
        } else {
            messageToSpeak = notificationMessages[notificationIndex]; // Default to English message
        }
        callSpeaker(messageToSpeak);

        // Create and send the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, NotificationReceiver.class); // Broadcast receiver
        intent.putExtra("notification_id", NOTIFICATION_ID_BASE + notificationIndex);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, notificationIndex, intent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Fuelshine")
                .setContentText(notificationMessages[notificationIndex]) // Use the message from the array
                .setSmallIcon(R.drawable.notif_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.new_app_icon))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        // Show the notification
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID_BASE + notificationIndex, notification);
        }
    }

    public void stopSendingNotifications() {
        shouldSendNotifications = false;  // Set flag to stop notifications
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Notification Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    private void updateDevicesDatabase() {
        Log.e(TAG, "coming in update");
        DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
        ArrayList<BluetoothDeviceModel> savedList = helper.fetchRegisteredDevices();
        helper.deleteTable(DatabaseHelper.REGISTERED_DEVICE_TABLE);
        savedList = getConnectedRegisteredDevices(savedList);
        helper.insertRegisteredDevices(savedList);
        refreshRegisteredDevices();
    }

    public ArrayList<BluetoothDevice> getConnectedDevices() {
        Set<BluetoothDevice> pairedList = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        ArrayList<BluetoothDevice> connectedDevices = new ArrayList<>();
        for (BluetoothDevice device : pairedList) {
            if (isConnected(device)) {
                connectedDevices.add(device);
            }
        }
        return connectedDevices;
    }


    private boolean isConnected(BluetoothDevice device) {
        try {
            Method m = device.getClass().getMethod("isConnected", (Class[]) null);
            boolean connected = (boolean) m.invoke(device, (Object[]) null);
            return connected;
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return false;
    }


    private boolean activityCaptured = false;

    private void registerRecognitionBroadcastReceiver() {
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(activityRecognitionBroadcastReceiver, new IntentFilter(Constants.RECOGNITION_ACTION));
    }


    boolean inVehicleActivity = false;
    //this is to prevent phone trips from starting when user is not driving

    int retryOnWalking = 0;
    private final BroadcastReceiver activityRecognitionBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action == null) {
                return;
            }
            if (action.equals(Constants.RECOGNITION_ACTION)) {
                inVehicleActivity = intent.getBooleanExtra("inVehicleActivity", false);
                addLog(TAG + " - " + "activityRecognitionBroadcastReceiver onReceive: " + inVehicleActivity);

                if (isManualStart) {
                    return;
                }
                timerToStopTheTrip.cancel();

                //call initialization methods only once
                if (!activityCaptured && inVehicleActivity) {
                    onActivityCaptured();
                }


                if (!isObdConnected && currentTrip != null && !inVehicleActivity) { //only for gps trips
                    checkTripStartOrEndCalculation();

                }
            } else {
                showNotification("No Activity Recognition:");
            }
        }
    };

    Timer timerToStopTheTrip = new Timer();
    private void checkTripStartOrEndCalculation(){
        timerToStopTheTrip = new Timer();
        timerToStopTheTrip.schedule(new TimerTask() {
            @Override
            public void run() {
                // Code to run after the delay
                isServiceBound = false;
                stopLiveData();
                activityCaptured = false;
            }
        }, 30000);
    }

    private void fetchConfigValues(){

        // Get values from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("ThresholdPrefs", MODE_PRIVATE);
        double thresholdAcceleration = Double.parseDouble(sharedPreferences.getString("harsh_acceleration", "0"));
        double thresholdBraking = Double.parseDouble(sharedPreferences.getString("harsh_braking", "0"));
        double thresholdSpeed = Double.parseDouble(sharedPreferences.getString("overspeed", "0"));

        harshAccelaration = thresholdAcceleration;
        harshDecelaration = thresholdBraking;
        maxSpeed = (int) thresholdSpeed;
//        maxSpeed = 25;

        if(harshAccelaration != null && harshDecelaration != null && maxSpeed != 0){
            isConfigCalled = true;
        }


        // Use these values in your service logic
        Log.d(TAG, "Harsh Acceleration: " + harshAccelaration);
        Log.d(TAG, "Harsh Braking: " + harshDecelaration);
        Log.d(TAG, "Overspeed: " + maxSpeed);

        addLog("Harsh Acceleration: " + harshAccelaration);
        addLog("Harsh Braking: " + harshDecelaration);
        addLog("Overspeed: " + maxSpeed);

    }


    String detectedFastMovement = "We have detected fast movement and started tracking your trip.";

    //this is called by activity recognition
    private void onActivityCaptured() {

        if (currentTrip == null) {

            Log.e(TAG, "onActivityCaptured: " + "checkBluetoothAndConnect");
            if (isManualStart) {
                if (!isConfigCalled){
                    fetchConfigValues();
                }
                resetAllValues(false);
                isManualStart = true;

                showNotification(detectedFastMovement);
                isServiceBound = true;
                isObdConnected = false;
                checkForVehicle("");


            } else {

                resetAllValues(false);
                if (!isConfigCalled){
                    fetchConfigValues();
                }

                showNotification(detectedFastMovement);
                checkBluetoothAndConnect(); // remove this method for removing bluetooth dependency
                checkRegisteredDeviceConnected(); // remove this method for removing bluetooth dependency


            }
            activityCaptured = true;

//            showNotification(detectedFastMovement);
//            checkBluetoothAndConnect(); // remove this method for removing bluetooth dependency
//            checkRegisteredDeviceConnected(); // remove this method for removing bluetooth dependency

            // For removing bluetooth dependency add these lines


            //  sendMyBroadcast(1);
//            isServiceBound = true;
//            isObdConnected = false;
//            checkForVehicle("");


        }

    }


    private void sendMyBroadcast(int value) {
        Intent intent = new Intent();
        switch (value) {
            case 0: //bluetooth status connected broadcast
                intent.setAction(getString(R.string.bluetooth_status_action));
                intent.putExtra(getString(R.string.status_broadcast), getString(R.string.connected_broadcast));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                break;

            case 1: //bluetooth status connecting broadcast
                intent.setAction(getString(R.string.bluetooth_status_action));
                intent.putExtra(getString(R.string.status_broadcast), getString(R.string.connecting_broadcast));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                break;

            case 2: //bluetooth status disconnecting broadcast
                intent.setAction(getString(R.string.bluetooth_status_action));
                intent.putExtra(getString(R.string.status_broadcast), getString(R.string.disconnected_broadcast));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                break;

            case 3: //current trip broadcast
                intent.setAction(getString(R.string.current_trip_action));
                intent.putExtra(getString(R.string.current_trip_broadcast), currentTrip);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                break;

            case 4: //end trip broadcast

                Log.e(TAG, "sendMyBroadcast : end_trip_action ");
                intent.setAction(getString(R.string.end_trip_action));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                break;

            case 5: //end trip broadcast

                Log.e(TAG, "Connect Bluetooth: ");
                intent.setAction(getString(R.string.connect_bluetooth));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                break;


            case 6: //No internet broadcast for stopping trip

                intent.setAction(getString(R.string.no_internet_trip_will_end_after_getting_connection));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                break;
            case 7: //No internet broadcast for stopping trip

                intent.setAction(getString(R.string.no_internet_trip_will_start_after_getting_connection));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                break;


            default:
                break;
        }
    }


    Map<String, String> getDict(int keyId, int valId) {
        String[] keys = getResources().getStringArray(keyId);
        String[] vals = getResources().getStringArray(valId);

        Map<String, String> dict = new HashMap<String, String>();
        for (int i = 0, l = keys.length; i < l; i++) {
            dict.put(keys[i], vals[i]);
        }
        return dict;
    }


    private void showBluetoothForegroundNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(NOTIFICATION_ID, NOTIFICATION_CHANNEL);
            showNotification(initialMsg);
        } else {
            Intent intent = new Intent(this, GypseeMainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, PENDING_INTENT_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);


            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.bigText("Connecting with bluetooth");
            bigTextStyle.setBigContentTitle("Fuelshine");

            builder.setStyle(bigTextStyle);
            builder.setOngoing(true);
            // builder.setOnlyAlertOnce(true); //to quietly update the notification
            builder.setWhen(System.currentTimeMillis());
            builder.setSmallIcon(R.drawable.notif_icon);
            builder.setPriority(Notification.PRIORITY_LOW);
            builder.setFullScreenIntent(pendingIntent, true);

            Notification notification = builder.build();

//            startForeground(NOTIFICATION_REQUEST_CODE, notification);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                startForeground(NOTIFICATION_REQUEST_CODE, notification);
            } else {
                startForeground(NOTIFICATION_REQUEST_CODE, notification,
                        FOREGROUND_SERVICE_TYPE_LOCATION);
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelID, String channelName) {

        NotificationChannel notificationChannel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_LOW);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null; //exit if notification manager is null
        notificationManager.createNotificationChannel(notificationChannel);
    }


    private void checkBluetoothAndConnect() {
        sendMyBroadcast(1);

        if (enableBluetooth()) {
            //Stop rotate animation if any.
            performBluetoothOps();
        } else {
            activityCaptured = false;

        }

    }

    private boolean enableBluetooth() {
        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean(ConfigActivity.ENABLE_BT_KEY, true).apply();

        if (btAdapter.isEnabled()) {
            return true;
        } else {
            textToSpeech.speak(switchOnBluetooth, TextToSpeech.QUEUE_FLUSH, null, null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                sendMyBroadcast(5);
            } else
                btAdapter.enable();

        }
        return false;
    }

    MyPreferenece myPreferenece;


    private boolean performBluetoothOps() {

        Set<BluetoothDevice> pairedDevices = getBluetoothDevices();
        for (BluetoothDevice bluetoothDevice : pairedDevices) {

            if (bluetoothDevice.getName() != null && bluetoothDevice.getName().equals("OBDII")) {
                myPreferenece.setIsConnecting(true);
                Log.e(TAG, "Mac : " + bluetoothDevice.getAddress());
                myPreferenece.saveStringData(ConfigActivity.BLUETOOTH_LIST_KEY, bluetoothDevice.getAddress());
                connectToBluetoothDevice(bluetoothDevice);
                return true;
            }
        }
        return false;
    }

    ConnectThread connectThread;

    private void connectToBluetoothDevice(BluetoothDevice bluetoothDevice) {
        if (connectThread != null && connectThread.isAlive()) {
            return;
        }
        connectThread = new ConnectThread(bluetoothDevice, blueToothConnectionInterface, this);
        connectThread.start();
    }

    BluetoothSocket bluetoothSocket = null;
    BlueToothConnectionInterface blueToothConnectionInterface = new BlueToothConnectionInterface() {
        @Override
        public void onBluetoothConnected(BluetoothSocket socket) {

            bluetoothSocket = socket;
            Log.e(TAG, "Bluetooth is connected");
            showNotification(deviceConnected);

            myPreferenece.setIsConnecting(false);

            myPreferenece.setConnectedDeviceMac(myPreferenece.getStringData(ConfigActivity.BLUETOOTH_LIST_KEY));

            isObdConnected = true;

            //send broadcast to homefragment
            sendMyBroadcast(0);
            startLiveData();
        }


        @Override
        public void obBluetoothDisconnected() {
            Log.e(TAG, "Failure Starting live data");

            myPreferenece.setIsConnecting(false);

            if (myPreferenece.getConnectedDeviceMac().equals("") || myPreferenece.getConnectedDeviceMac().equals(myPreferenece.getStringData(ConfigActivity.BLUETOOTH_LIST_KEY))) {
                sendMyBroadcast(2);
                stopLiveData();
            }


        }
    };

    private void showNotification(String message) {

        //Create intent
        Intent resultIntent = new Intent(this, SplashActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntentWithParentStack(resultIntent);


        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = taskStackBuilder.getPendingIntent(PENDING_INTENT_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = taskStackBuilder.getPendingIntent(PENDING_INTENT_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        //Notification Builder
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_ID);

        Notification notification = notificationBuilder.setOngoing(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSmallIcon(R.drawable.notif_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.new_app_icon))
                .setColor(getResources().getColor(R.color.colorPrimaryDark))
                .setContentTitle("Fuelshine")
                .setContentText(message)
                //.setOnlyAlertOnce(true) //to update notification quietly
                .setPriority(NotificationManager.IMPORTANCE_LOW)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationBuilder.setWhen(System.currentTimeMillis());

        notificationManagerCompat.notify(NOTIFICATION_REQUEST_CODE, notificationBuilder.build());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            startForeground(NOTIFICATION_REQUEST_CODE, notification);
        } else {
            startForeground(NOTIFICATION_REQUEST_CODE, notification,
                    FOREGROUND_SERVICE_TYPE_LOCATION);
        }

    }


    private boolean zeroSpeedCheck = false;

    private void updateRPMAlert(String cmdResult) {

        if (currentTrip == null || endLocation == null) {
            Log.e(TAG, "rpm null");
            return;
        }

        int rpm = Integer.parseInt(cmdResult.replace("RPM", ""));
        Log.e(TAG, "rpm:vv " + rpm);
        //for vhs usage
        if (rpm > 0) {
            Log.e(TAG, "latest capture speed: " + latestCapturedSpeed);
            if (latestCapturedSpeed == 0) {

                if (!zeroSpeedCheck) {
                    startIdleTimeMillis = System.currentTimeMillis();
                    Log.e(TAG, "start time stamp: " + startIdleTimeMillis);
                    zeroSpeedCheck = true;
                } else {
                    endIdleTimeMillis = System.currentTimeMillis();
                    //if (startIdleTimeMillis != 0){ //this means we have not started recording time
                    Log.e(TAG, "end time stamp: " + endIdleTimeMillis);
                    idleTimeInSec = idleTimeInSec + (endIdleTimeMillis - startIdleTimeMillis) / 1000;
                    Log.e(TAG, "idle time: " + idleTimeInSec);
                    startIdleTimeMillis = System.currentTimeMillis();
                    // }
                }
            } else {
                endIdleTimeMillis = System.currentTimeMillis();
                Log.e(TAG, "start time here");
                if (startIdleTimeMillis != 0) { //this means we have not started recording time
                    Log.e(TAG, "else end time stamp: " + endIdleTimeMillis);
                    idleTimeInSec = idleTimeInSec + (endIdleTimeMillis - startIdleTimeMillis) / 1000;
                    endIdleTimeMillis = 0;
                    startIdleTimeMillis = 0;
                    zeroSpeedCheck = false;
                    Log.e(TAG, "else idle time: " + idleTimeInSec);
                }
            }
        }


        if (rpm > maximumRPM)
            maximumRPM = rpm;
        if (rpm < maxEngineRPM && engineAlert != null) {
            addTripAlertToArray(engineAlert);
            engineAlert = null;
        }
        if (rpm > maxEngineRPM) {

//            engineAlert = engineAlert == null ? new TripAlert("High RPM", rpm + " RPM", "", "", new Date().getTime(), endLocation.getLatitude(), endLocation.getLongitude(), "") : engineAlert;
            engineAlert = engineAlert == null ? new com.gypsee.sdk.database.TripAlert("High RPM", rpm + " RPM", "", "", new Date().getTime(), endLocation.getLatitude(), endLocation.getLongitude(), "",currentTrip.getId()) : engineAlert;

            if (rpm > Integer.parseInt(engineAlert.getAlertValue().replace(" RPM", ""))) {
                engineAlert.setAlertValue(rpm + " RPM");
            }
            engineAlert.setTimeInterval(TimeUtils.calcDiffTime(engineAlert.getTimeStamp(), new Date().getTime()));
            //Toast.makeText(context, "RPM Alert", Toast.LENGTH_SHORT).show();
            showNotification("RPM Alert");
        }

        currentRPM = rpm;
    }


    private void startLiveData() {
        Log.e(TAG, "Starting live data..");
        registerObdBroadCastReceiver();
        doBindService();
        isStarting = true;
    }

    private void registerLocationBroadcastReceiver() {
        //prepare service


        //Background location service already running from HomeFragment
        //Just register the broadcast receiver
        IntentFilter newIntent = new IntentFilter();
        newIntent.addAction("LocationReceiver");
        //newIntent.addAction("Start Location Service");
        newIntent.addAction("connectedvehiclemodel");
        newIntent.addAction("BluetoothConnectionChange");
        newIntent.addAction("BluetoothDisconnected");
        newIntent.addAction("ScanServiceActiviated");
        newIntent.addAction("GEOFENCE_ENTER");
        newIntent.addAction("GEOFENCE_EXIT");
        newIntent.addAction("REFRESH_USER");
        LocalBroadcastManager.getInstance(this).registerReceiver(locationBroadcastReceiver, newIntent);

    }


    private void registerObdBroadCastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("ObdCommandUpdates");
        LocalBroadcastManager.getInstance(this).registerReceiver(obdCommandBroadcastReceiver, filter);
    }

    private BroadcastReceiver obdCommandBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("ObdCommandUpdates")) {
                //it is for receiving the OBD command updates from the OBD gateway service.
                Bundle b = intent.getExtras();
                ObdCommandJob obdCommandJob = null;
                if (b != null)
                    obdCommandJob = b.getParcelable("ObdCommandJob");
                if (obdCommandJob == null) {
                    Log.e(TAG, "No data");
                } else {
                    stateUpdate(obdCommandJob);

                }
            }

        }
    };

    private HashMap<String, String> emptyValues = new HashMap<>();
    private HashMap<String, String> commandResult = new HashMap<>();

    boolean isStarting = true;

    //private HashMap<String, String> initCommands = new HashMap<>();
    private boolean initApi;
    private JsonObject initJson = new JsonObject();

    @Override
    public void onDestroy() {

        deRegisterActivityRecognitionClient();

        new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, getApplicationContext()).setInitApi(true);
        connectivityManager.unregisterNetworkCallback(networkCallback);
        stopLocationService();
        stopService(new Intent(getApplicationContext(), FloatingWidgetService.class));
//        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(activityRecognitionBroadcastReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(locationBroadcastReceiver);

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }

        Log.d("MyForegroundService", "Service destroyed");
        super.onDestroy();
    }


    private double highestEngineCoolantTemp = 0.0;
    private double lowestBatteryVoltage = 15.0;
    private double highestEngineLoad = 0.0;
    private long startIdleTimeMillis = 0;
    private long endIdleTimeMillis = 0;
    private long idleTimeInSec = 0;

    private void resetVhsParamsOnTripUpdate() {
        highestEngineCoolantTemp = 0.0;
        lowestBatteryVoltage = 15.0;
        highestEngineLoad = 0.0;
        startIdleTimeMillis = 0;
        endIdleTimeMillis = 0;
        idleTimeInSec = 0;
        zeroSpeedCheck = false;
    }

    private void checkVhsParams(ObdCommandJob job) throws Exception {

        String cmdID = LookUpCommand(job.getCommand().getName());
        String cmdResult = job.getCommand().getFormattedResult();

        switch (cmdID) {

            case "CONTROL_MODULE_VOLTAGE":
                Log.e(TAG, "voltage format: " + cmdResult);

                double currentBatteryVoltage = Double.parseDouble(cmdResult.replace("V", ""));
                if (currentBatteryVoltage < lowestBatteryVoltage) {
                    lowestBatteryVoltage = currentBatteryVoltage;
                }

                break;

            case "ENGINE_COOLANT_TEMP":
                Log.e(TAG, "temp format: " + cmdResult);

                double currentEngineCoolantTemp = Double.parseDouble(cmdResult.replace("C", ""));
                if (currentEngineCoolantTemp > highestEngineCoolantTemp) {
                    highestEngineCoolantTemp = currentEngineCoolantTemp;
                }

                break;

            case "ENGINE_LOAD":
                Log.e(TAG, "load format: " + cmdResult);

                double currentEngineLoad = Double.parseDouble(cmdResult.replace("%", ""));
                if (currentEngineLoad > highestEngineLoad) {
                    highestEngineLoad = currentEngineLoad;
                }

                break;

        }

    }


    private int latestCapturedSpeed = 0;
    private int fuelDensity = 850;
    private double mileage = 0;

    private void stateUpdate(ObdCommandJob job) {

        final String cmdName = job.getCommand().getName();
        String cmdResult = job.getCommand().getFormattedResult();
        final String cmdID = LookUpCommand(cmdName);

        /*if (cmdID.equals("VIN") && initApi){
            initApi = false;
            myPreferenece.setInitApi(initApi);
            //initCommands.put(cmdID, job.getCommand().getFormattedResult());
            initJson.addProperty(cmdID, job.getCommand().getFormattedResult());
            //calling the server
            callServer(getString(R.string.addObd), "Upload Obd", 18);
        }*/

        //this is to get initial response from device
        if (initApi) {
            initJson.addProperty(cmdID, job.getCommand().getFormattedResult());
            if (cmdID.equals("VIN")) {
                initApi = false;
                myPreferenece.setInitApi(initApi);
                callServer(getString(R.string.addObd), "Upload Obd", 18);
            }
        }


        // Set fuel type for mileage
        if (cmdID.equals("FUEL_TYPE")) {
            // Check if fuel type is available from OBD
            String formattedResult = job.getCommand().getFormattedResult();
            if (formattedResult.equalsIgnoreCase("Gasoline") || formattedResult.equalsIgnoreCase("Petrol")) {
                fuelDensity = 748;
            } else if (formattedResult.equalsIgnoreCase("diesel")) {
                fuelDensity = 850;
            } else {

                ArrayList<Vehiclemodel> vehiclemodelArrayList = new DatabaseHelper(getApplicationContext()).fetchAllVehicles();
                if (!vehiclemodelArrayList.isEmpty()) {
                    String fuelType = vehiclemodelArrayList.get(0).getFuelType();

                    if (fuelType.equalsIgnoreCase("Gasoline") || fuelType.equalsIgnoreCase("GASOLINE") || fuelType.equalsIgnoreCase("Petrol") || fuelType.equalsIgnoreCase("PETROL")) {
                        fuelDensity = 748;
                    } else if (fuelType.equalsIgnoreCase("diesel") || fuelType.equalsIgnoreCase("DIESEL")) {
                        fuelDensity = 850;
                    } else {
                        Log.e("FuelTypeErr", "fuel type is neither Gasoline/Petrol nor Diesel");

                        // To Handle the case where fuel type is neither Gasoline/Petrol nor Diesel
                    }
                } else {
                    Log.e("FuelTypeErr", "vehicle data is not available in the database");
                    // To Handle the case where vehicle data is not available in the database
                }
            }
        }


        //calculating mileage from maf
        //0.00g/s
        if (cmdID.equals("MAF")) {
            double maf = 0;
            try {
                maf = Double.parseDouble(job.getCommand().getFormattedResult().replace("g/s", ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (fuelDensity != 0 && maf > 0 & latestCapturedSpeed > 10) {
                double tempMileage = (14.7 * fuelDensity * latestCapturedSpeed) / (maf * 3600);
                Log.e(TAG, "my values: fuel density: " + fuelDensity + ", latest speed: " + latestCapturedSpeed + ", maf: " + maf + ", tempmileage: " + tempMileage);
                mileage = (double) round(((mileage + tempMileage) / 2) * 100) / 100;
                Log.e(TAG, "temp mileage: " + tempMileage + ", Mileage: " + mileage);
            }
        }
        if (cmdID.equals("TROUBLE_CODES")) {
            Log.e(TAG, "stateUpdate: TROUBLE_CODES");
            uploadTroubleCodes(cmdID, cmdResult);
        }


        //If is is starting and value is engine RPM we need to call queue commands
        if (isStarting) {
            new Handler().postDelayed(mQueueCommands, 1500);
            isStarting = false;
            Log.e(TAG, "Coming here : " + cmdName);
        }

        if (job.getState().equals(ObdCommandJob.ObdCommandJobState.BROKEN_PIPE)) {
            Log.e(TAG, "State update Broken pipe");
            doUnbindService();
            showNotification("Device Disconnected");
            //Start connecting to the bluetooth after sometime.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkBluetoothAndConnect();
                }
            }, 4000);

            return;
        } else if (cmdResult == null) {
            Log.e(TAG, cmdID + " : value is null");
            emptyValues.put(cmdID, "");
            return;
        } else if (job.getState().equals(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR)) {
            Log.e(TAG, "Execution error");
            if (cmdResult.equalsIgnoreCase("NODATA")) {
                emptyValues.put(cmdID, cmdResult);
            }

            if (cmdID.equals("ENGINE_RPM")) {

                switch (cmdResult) {
                    case "NODATA":
                    case "...UNABLETOCONNECT":
                        showNotification("Please switch ON the ignition of your car");
                        break;

                    case "INIT...BUSERROR":
                        showNotification("Connect with Fuelshine technical support");
                        break;
                }

            }
            return;

        } else if (job.getState().equals(ObdCommandJob.ObdCommandJobState.NOT_SUPPORTED)) {
            cmdResult = getString(R.string.status_obd_no_support);
            emptyValues.put(cmdID, cmdResult);
        } else {
            cmdResult = job.getCommand().getFormattedResult();


            emptyValues.remove(cmdID);
            if (cmdID.equals("Reset OBD")) {
                checkForVehicle(cmdResult);
            }
            if (cmdID.equals("ENGINE_RPM")) {
                updateRPMAlert(cmdResult);
            } else if (cmdID.equals("SPEED")) {
                updateSpeed(cmdResult);
            }

        }

        try {
            checkVhsParams(job);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e(TAG, "Command ID = " + cmdID + " - Command Name = " + cmdName + " - Command Result = " + cmdResult);
        commandResult.put(cmdID, cmdResult);

        /*if (myCSVWriter != null && endLocation != null)
            myCSVWriter.writeLineCSV(endLocation.getLatitude(), endLocation.getLongitude(), System.currentTimeMillis(), commandResult);*/

        checkPerformanceFragmentVisible(cmdID, cmdName, cmdResult);
        //updateViews(cmdID, cmdResult);
        updateTripStatistic(job, cmdID);

        //Do not write to CSV until u get the permission of adding and reading external storage

        //writeToCSVMethod();
    }

    public String getTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    private void uploadTroubleCodes(String cmdID, String cmdResult) {
        Log.e(TAG, "uploadTroubleCodes: " + cmdID + "-" + cmdResult);
        if (cmdID.equals("TROUBLE_CODES") && !cmdResult.equals("")) {
            //Here removing the C, B, U trouble codes.
            cmdResult = BluetoothHelperClass.parseTroubleCodes(cmdResult);
            String[] troubleCodes = cmdResult.split("\n");

            if (selectedvehiclemodel == null || endLocation == null || currentTrip == null) {
                return;
            }

            for (String code :
                    troubleCodes) {
                if (this.troubleCodes.contains(code)) {
                    // dont call server fpor uploading the trouble code.
                } else {
                    //callserver to upload details.
                    temptroubleCodes.add(code);
                    this.troubleCodes.add(code);
                }
            }

            //Call server to upload the trouble codes at the end of the journey.
            if (temptroubleCodes.size() > 0) {
                callServer(getString(R.string.troubleCodeUpload_url).replace("vehicleID", selectedvehiclemodel.getUserVehicleId()), "Upload trouble code", 2);
                //callserver to upload details.
                temptroubleCodes.clear();

            }
        }
    }

    private void checkPerformanceFragmentVisible(String cmdID, String cmdName, String cmdResult) {

        Intent intent = new Intent("Performance");
        // You can also include some extra data.
        Bundle b = new Bundle();
        b.putString("cmdID", cmdID);
        b.putString("cmdName", cmdName);
        b.putString("cmdResult", cmdResult);
        intent.putExtras(b);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);


    }


    private void updateTripStatistic(final ObdCommandJob job, final String cmdID) {

        if (currentTrip != null) {
            if (cmdID.equals(AvailableCommandNames.SPEED.toString())) {
                SpeedCommand command = (SpeedCommand) job.getCommand();
                currentTrip.setSpeedMax(command.getMetricSpeed());
            } else if (cmdID.equals(AvailableCommandNames.ENGINE_RPM.toString())) {
                RPMCommand command = (RPMCommand) job.getCommand();
                currentTrip.setEngineRpmMax(command.getRPM());
            } else if (cmdID.endsWith(AvailableCommandNames.ENGINE_RUNTIME.toString())) {
                RuntimeCommand command = (RuntimeCommand) job.getCommand();
                currentTrip.setEngineRuntime(command.getFormattedResult());
            }
            sendMyBroadcast(3);
        }
    }

    private boolean isServiceBound = false;
    private AbstractGatewayService service;
    private final Runnable mQueueCommands = new Runnable() {
        public void run() {
            if (service != null && service.isRunning() && service.queueEmpty()) {
                queueCommands();
                Log.e(TAG, " mQueueCommands Runnbale : " + isServiceBound);
            }
            //THis was running in background earlier. thats the condition.
            if (isServiceBound) {
                new Handler().postDelayed(mQueueCommands, 1500);
            }
        }
    };

    Location endLocation;
    private ArrayList<String> troubleCodes = new ArrayList<>();
    private ArrayList<String> temptroubleCodes = new ArrayList<>();

    //    int maxSpeed = 25;
    int maxSpeed;
    String maximumspeed = "01 km/hr";
    int currentRPM = 0;
    int maxEngineRPM = 3000, maximumRPM = 0;

    private int getDistancePostCC() {
        String distancePostCC = commandResult.get("DISTANCE_TRAVELED_AFTER_CODES_CLEARED");
        if (distancePostCC == null || distancePostCC.isEmpty() || distancePostCC.equalsIgnoreCase("NA")) {
            distancePostCC = "0km";
        }
        return Integer.parseInt(distancePostCC.replace("km", ""));
    }


    ArrayList<TripLatLong> tempTripLatLongArrayList = new ArrayList<>();

    private void callServer(String url, final String purpose, final int value) {

        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);
        Call<ResponseBody> call;
        JsonObject jsonObject = new JsonObject();
        User user = myPreferenece.getUser();

        switch (value) {
            case 1:
                //Start trip
                jsonObject.addProperty("airIntakeTemp", commandResult.get("AIR_INTAKE_TEMP"));
                jsonObject.addProperty("ambientAirTemp", commandResult.get("AMBIENT_AIR_TEMP"));
                jsonObject.addProperty("barometricPressure", commandResult.get("BAROMETRIC_PRESSURE"));
                jsonObject.addProperty("currentSpeed", commandResult.get("SPEED"));
                jsonObject.addProperty("engineCoolantTemp", commandResult.get("ENGINE_COOLANT_TEMP"));
                jsonObject.addProperty("engineLoad", commandResult.get("ENGINE_LOAD"));
                jsonObject.addProperty("engineRpm", commandResult.get("ENGINE_RPM"));
                jsonObject.addProperty("engineRunTime", commandResult.get("ENGINE_RUNTIME"));
                jsonObject.addProperty("equivRatio", commandResult.get("EQUIV_RATIO"));
                jsonObject.addProperty("fuelPressure", commandResult.get("FUEL_PRESSURE"));
                jsonObject.addProperty("intakeManifoldPressure", commandResult.get("INTAKE_MANIFOLD_PRESSURE"));
                jsonObject.addProperty("longTermFuelTrimBank2", commandResult.get("Long Term Fuel Trim Bank 2"));
                jsonObject.addProperty("shortTermFuelBank1", commandResult.get("Short Term Fuel Trim Bank 1"));
                jsonObject.addProperty("shortTermFuelBank2", commandResult.get("Short Term Fuel Trim Bank 2"));
                jsonObject.addProperty("termFuelTrimBank1", commandResult.get("Term Fuel Trim Bank 1"));
                jsonObject.addProperty("isObdConnected", isObdConnected);

                myPreferenece.setConnectedToCar(true);

                //If the end location is null, we will fetch the location. Then only start the trip.
                if (endLocation == null) {
                    startBackGroundLocationService();
                    startTripAfterDelay(15000);
                    return;

                } else {
                    jsonObject.addProperty("currentAltitude", String.valueOf(endLocation.getAltitude()));
                    jsonObject.addProperty("currentLatitude", String.valueOf(endLocation.getLatitude()));
                    jsonObject.addProperty("currentLongitude", String.valueOf(endLocation.getLongitude()));
                }
                jsonObject.addProperty("userId", user.getUserId());
                jsonObject.addProperty("vehicleId", selectedvehiclemodel.getUserVehicleId());

                jsonObject.addProperty("distancePostCC", getDistancePostCC());

                Log.e(TAG, purpose + " input json : " + jsonObject.toString());

                if(networkCallback.isNetWorkAvailable)
                    call = apiService.uploadVehDetails(user.getUserAccessToken(), jsonObject);
                else {
                    startTripAfterDelay(35000);
                    return;
                }
                break;

            case 2:

                //Upload trouble code
                JsonArray jsonArrays = new JsonArray();
                for (String troubleCode :
                        temptroubleCodes) {
                    jsonObject = new JsonObject();
                    jsonObject.addProperty("troubleCode", troubleCode);
                    jsonObject.addProperty("errorDesc", dtcVals.get(troubleCode));
                    jsonObject.addProperty("distanceTraveledSinceCodeClear", getDistancePostCC());
                    jsonObject.addProperty("distanceTraveledWithMILOn", commandResult.get("DISTANCE_TRAVELED_MIL_ON"));
                    jsonObject.addProperty("dtcNumber", commandResult.get("DTC_NUMBER"));
                    jsonObject.addProperty("fuelLevelOnAlert", commandResult.get("FUEL_LEVEL"));
                    jsonObject.addProperty("vehicleOtherDataOnAlert", getCurrentLogValuesinJson().toString());
                    jsonObject.addProperty("latitudeOnAlert", String.valueOf(endLocation.getLatitude()));
                    jsonObject.addProperty("longitudeOnAlert", String.valueOf(endLocation.getLongitude()));
                    jsonObject.addProperty("tripId", currentTrip.getId());
                    jsonArrays.add(jsonObject);
                }
                call = apiService.uploadDTCerrorcodes(user.getUserAccessToken(), jsonArrays);

                break;

            case 3:
                jsonObject.addProperty("vehicleId", selectedvehiclemodel.getUserVehicleId());

                JsonArray jsonArray = new JsonArray();
                // Getting an iterator

                for (Map.Entry mapElement : emptyValues.entrySet()) {
                    String key = (String) mapElement.getKey();
                    // Add some bonus marks
                    // to all the students and print it
                    JsonObject innerJson = new JsonObject();
                    String values = (String) mapElement.getValue();
                    innerJson.addProperty("commandName", key);
                    innerJson.addProperty("commandValue", values);
                    jsonArray.add(innerJson);
                }

                jsonObject.add("issues", jsonArray);
                Log.e(TAG, purpose + " : " + jsonObject.toString());
                call = apiService.uploadVehDetails(user.getUserAccessToken(), jsonObject);

                break;

            //This is for clearing the alert codes

            case 4:


                jsonObject.addProperty("latitudeOnAlertClear", String.valueOf(endLocation.getLatitude()));
                jsonObject.addProperty("longitudeOnAlertClear", String.valueOf(endLocation.getLongitude()));
                jsonObject.addProperty("vehicleOtherDataOnAlertClear", getCurrentLogValuesinJson().toString());
                JsonArray alertIdsArray = new JsonArray();

                //Remove the existing trouble codes and make the remaining status as  fixed by calling the API
                for (String troubleCode :
                        troubleCodes) {
                    for (int i = 0; i < vehicleAlertModelArrayList.size(); i++) {
                        if (vehicleAlertModelArrayList.get(i).getTroubleCode().equalsIgnoreCase(troubleCode)) {
                            vehicleAlertModelArrayList.remove(i);
                            i = i - 1;
                        }
                    }
                }
                for (VehicleAlertModel vehicleAlertModel :
                        vehicleAlertModelArrayList) {

                    alertIdsArray.add(vehicleAlertModel.getVehicleAlertId());
                }
                jsonObject.add("vehicleAlertIds", alertIdsArray);


                Log.e(TAG, purpose + " input : " + jsonObject.toString());
                call = apiService.clearAlertCode(user.getUserAccessToken(), jsonObject);
                break;

            case 5:

                HashMap<String, Object> queryParams = new HashMap<>();
                queryParams.put("isActiveAlerts", true);
                queryParams.put("size", 40);
                queryParams.put("page", 0);

                call = apiService.getVehicleAlerts(user.getUserAccessToken(), queryParams);

                break;
            case 6:

                jsonObject.addProperty("tripId", currentTrip.getId());
                jsonObject.add("alerts", tempdrivingAlerts);

                addLog("Driving Alerts Input = "+tempdrivingAlerts);
                Log.e("Driving Alerts Input =","Driving Alerts Input = "+tempdrivingAlerts);

                call = apiService.uploadVehDetails(user.getUserAccessToken(), jsonObject);

                break;

            case 7:

                for (int i = ecoSpeedAlerts.size() - 1; i >= 0; i--) {
                    JsonElement element = ecoSpeedAlerts.get(i);
                    if (element.isJsonPrimitive() && element.getAsString().isEmpty()) {
                        ecoSpeedAlerts.remove(i);
                    }
                }
                jsonObject.addProperty("tripId", currentTrip.getId());
                jsonObject.add("alerts", ecoSpeedAlerts);


//                List<TripAlert>  tripAlerts =  tripDatabase.tripAlertDao().getTripAlertsByTripId(currentTrip.getId());


//                jsonObject.addProperty("alerts");

                addLog("Eco Alerts input :  "+ecoSpeedAlerts.toString());
                Log.e("Eco Alerts input : ","Eco Alerts input : "+ecoSpeedAlerts);

                call = apiService.uploadVehDetails(user.getUserAccessToken(), jsonObject);

                break;


            case 10:

                if (tripDatabase == null) {
                    tripDatabase = TripDatabase.getDatabase(ForegroundService.this);
                }

                jsonObject.addProperty("calculateDistance", true);
                jsonObject.addProperty("calculateSpeed", true);
                jsonObject.addProperty("tripID", currentTrip.getId());
                jsonObject.addProperty("userID", user.getUserId());
                JsonArray dataArray = new JsonArray();


//                tempTripLatLongArrayList.addAll(tripLatLongArrayList);

//                if (networkCallback.isNetWorkAvailable){
//                    tempTripLatLongArrayList.addAll(tripLatLongArrayList);
//                }else {
//                    tempTripLatLongArrayList.addAll(tripDatabase.tripDao().getTripsLatLongByTripId(currentTrip.getId()));
//                }


                tempTripLatLongArrayList.addAll(tripDatabase.tripDao().getTripsLatLongByTripId(currentTrip.getId()));



                // tripDatabase.tripDao().deleteTripsByTripId(currentTrip.getId());

                //List<TripLatLong> tripsLatlong = tripDatabase.tripDao().getTripsLatLongByTripId(currentTrip.getId());

                //Log.e("TripLatLong","tripsLatLong size :" +tripsLatlong.size() );

                addLog("Uploading GPS for trip calculation API called time = " + getTime() + "  Size - " + tempTripLatLongArrayList.size());
                addLog("Trip distance while uploading data to gps = " + tripDistanceFromGPSm + " time = " + getTime());
//                addLog("TripLatLong tripsLatLong size : " + tripsLatlong.size() + " time = " + getTime()) ;

//                for (TripLatLong tripLatLongById : tempTripLatLongArrayList) {
//                    JsonObject dataObj = new JsonObject();
//                    dataObj.addProperty("latitude", tripLatLongById.getLatitude());
//                    dataObj.addProperty("longitude", tripLatLongById.getLongitude());
//                    dataObj.addProperty("createdOn", tripLatLongById.getTime());
////                    addLog("Trip Update lat long = latitude = " + tripLatLongById.getLatitude() + " longitude = " + tripLatLongById.getLongitude() + " Time = " + getTime());
//                    dataArray.add(dataObj);
//                }

                for (TripLatLong tripLatLongById : tripDatabase.tripDao().getTripsLatLongByTripId(currentTrip.getId())){

                    JsonObject dataObj = new JsonObject();
                    dataObj.addProperty("latitude", tripLatLongById.getLatitude());
                    dataObj.addProperty("longitude", tripLatLongById.getLongitude());
                    dataObj.addProperty("createdOn", tripLatLongById.getTime());
//                    addLog("Trip Update lat long = latitude = " + tripLatLongById.getLatitude() + " longitude = " + tripLatLongById.getLongitude() + " Time = " + getTime());
                    dataArray.add(dataObj);

                    addLog("Uploading Stored lat long = " + "latitude = " + tripLatLongById.getLatitude()  + "longitude = " + tripLatLongById.getLongitude() + "createdOn = " + tripLatLongById.getTime());
                    Log.e("Uploading Stored lat long","Uploading Stored lat long = " + "latitude = " + tripLatLongById.getLatitude()  + "longitude = " + tripLatLongById.getLongitude() + "createdOn = " + tripLatLongById.getTime());

                }


                jsonObject.add("data", dataArray);

                addLog("data Array = "+ dataArray);



                //If the array size is 2, then we need to call this.
                // So that he can atleast call the API and calcualte the distance.


//                List<DrivingAlert> storedAlerts = tripDatabase.drivingAlertDao().getDrivingAlertsByTripId(currentTrip.getId());
//
//                List<TripAlert> unsyncedAlerts = tripDatabase.tripAlertDao().getTripAlertsByTripId(currentTrip.getId());
//
//                if (!storedAlerts.isEmpty()){
//                    syncDataWithServer();
//                }
//
//                if (!unsyncedAlerts.isEmpty()){
//                    syncEcoAlertsDataWithServer();
//                }




                if (tempTripLatLongArrayList.size() > 2) {
                    addLog("Uploading GPS for trip calculation API called finally---");
                    call = apiService.uploadVehDetails(user.getUserAccessToken(), jsonObject);
                } else {
                    checkBackGroundLocationServiceRunningOrNot();
                    return;
                }

//                tripDatabase.tripDao().deleteTripsByTripId(currentTrip.getId());

                tripLatLongArrayList.clear();
                break;


            //14 start trip
            //15 end trip

            case 14:
            case 15:


                //Battery voltage
                addLog("Trip Update Called" + " time = " + getTime());
                addLog("Distance = " + tripDistanceFromGPSm + " time = " + getTime());

                jsonObject = getCurrentLogValuesinJson();

                /*jsonObject.addProperty("tripDuration", );
                jsonObject.addProperty("tripETA", "");
                jsonObject.addProperty("tripName", "");*/
                jsonObject.addProperty("userId", user.getUserId());
                // jsonObject.addProperty("tripDesc", "");


                jsonObject.addProperty("tripId", currentTrip.getId());
                jsonObject.addProperty("startLatitude", currentTrip.getStartLat());
                jsonObject.addProperty("startLongitude", currentTrip.getStartLong());
                jsonObject.addProperty("startingAltitude", endLocation.getAltitude());

                jsonObject.addProperty("idleTimeInSec", idleTimeInSec);
                jsonObject.addProperty("lowestBatteryVoltage", lowestBatteryVoltage);
                jsonObject.addProperty("highestEngineCoolantTemp", highestEngineCoolantTemp);
                jsonObject.addProperty("highestEngineLoad", highestEngineLoad);

                Log.e(TAG, "update trip idle time: " + idleTimeInSec);

                addLog("  =  Trip id = " + currentTrip.getId() + " startLatitude = " + currentTrip.getStartLat() + " startLongitude = " + currentTrip.getStartLong() + "startingAltitude" + endLocation.getAltitude() + ".  Time = " + getTime());

                myPreferenece.setConnectedToCar(false);

                if (value == 15) {
                    addLog("End trip Called" + " Time = " + getTime());
                }

                myPreferenece.setLatLng(endLocation.getLatitude() + "/" + endLocation.getLongitude());

                Log.e(TAG, purpose + " input : " + jsonObject.toString());

                resetVhsParamsOnTripUpdate();


                Log.e(TAG, "Mileage end trip: " + mileage);
                if (purpose.equalsIgnoreCase("end trip")) {
                    if (mileage != 0) {
                        jsonObject.addProperty("mileage", mileage);
                    }
                    call = apiService.endTrip(user.getUserAccessToken(), jsonObject, isObdConnected);
                } else {
                    call = apiService.uploadVehDetails(user.getUserAccessToken(), jsonObject);
                }

                break;

            case 16:
                String distancePostCC = commandResult.get("DISTANCE_TRAVELED_AFTER_CODES_CLEARED");
                //Don't call update APi if the distance postcc is not coming from the device. It will be not userful

                if (distancePostCC == null || distancePostCC.isEmpty() || distancePostCC.equals("NA") || distancePostCC.equals("0km")) {
                } else {
                    jsonObject.addProperty("distancePostCC", Double.parseDouble(distancePostCC.replace("km", "")));
                }

                jsonObject.addProperty("engineRunTime", commandResult.get("ENGINE_RUNTIME"));
                jsonObject.addProperty("fuelLevel", commandResult.get("FUEL_LEVEL"));
                jsonObject.addProperty("insuranceReminderDate", selectedvehiclemodel.getInsuranceReminderDate());
                String protocol = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(ConfigActivity.PROTOCOLS_LIST_KEY, "AUTO");

                jsonObject.addProperty("protocol", protocol);

                if (endLocation != null) {
                    jsonObject.addProperty("latitude", endLocation.getLatitude());
                    jsonObject.addProperty("longitude", endLocation.getLongitude());
                } else {
                    jsonObject.addProperty("latitude", "0.0");
                    jsonObject.addProperty("longitude", "0.0");
                }
                jsonObject.addProperty("maf", commandResult.get("MAF"));
                if (commandResult.get("VIN") != null && !commandResult.get("VIN").equals("")) {
                    jsonObject.addProperty("vin", commandResult.get("VIN"));
                    jsonObject.addProperty("vinAvl", true);
                } else {
                    jsonObject.addProperty("vinAvl", false);
                }

               /* jsonObject.addProperty("odoMeterRdg", vehiclemodel.getOdoMeterRdg());
                jsonObject.addProperty("pollutionReminderDate", vehiclemodel.getPollutionReminderDate());
                jsonObject.addProperty("serviceReminderkm", vehiclemodel.getServiceReminderkm());*/

                Log.e(TAG, purpose + " Input is : " + jsonObject.toString());
                call = apiService.uploadFCMToken(user.getUserAccessToken(), jsonObject);
                break;

            case 17:
                call = apiService.getDocVehicleAlerts(user.getUserAccessToken(), true, false);
                break;

            case 18:
                //upload obd init command results
                //jsonObject = initJson;
                String mac = myPreferenece.getStringData(ConfigActivity.BLUETOOTH_LIST_KEY);
                jsonObject.addProperty("deviceStats", initJson.toString());
                jsonObject.addProperty("macAddress", (mac != null) ? mac : "");
                jsonObject.addProperty("userId", myPreferenece.getUser().getUserId());
                Log.e(TAG, "jsonOnj: " + jsonObject.toString());


                call = apiService.uploadObd(user.getUserAccessToken(), jsonObject);
                break;

            case 19:
                call = apiService.getVehicleHealthData(user.getUserAccessToken());
                break;

            case 20:

                JsonArray deviceArray = new JsonArray();
                ArrayList<BluetoothDeviceModel> models = new DatabaseHelper(getApplicationContext()).fetchRegisteredDevices();
                Log.e(TAG, "models size: " + models.size());
                //String selectedMac = myPreferenece.getConnectedDeviceMac();
                for (BluetoothDeviceModel deviceModel : models) {

                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date());

                    JsonObject object = new JsonObject();
                    object.addProperty("bluetoothName", deviceModel.getDeviceName());
                    object.addProperty("bluetoothType", deviceModel.getBluetoothType());
                    object.addProperty("bluetoothProfilesSupported", deviceModel.getBluetoothProfileSupported());
                    object.addProperty("deviceId", deviceModel.getId());
                    object.addProperty("macAddress", deviceModel.getMacAddress());
                    object.addProperty("nowConnected", deviceModel.isNowConnected());
                    object.addProperty("lastConnectedTime", (deviceModel.isNowConnected()) ? timeStamp : deviceModel.getLastConnectedTime());
                    deviceArray.add(object);
                }

                Log.e(TAG, purpose + " Input :" + deviceArray);


                call = apiService.updateRegisteredDevices(user.getUserAccessToken(), deviceArray);
                break;

            case 21:

                if (purpose.equals("Geofence Trigger Enter")) {
                    jsonObject.addProperty("eventType", "Enter");
                } else {
                    jsonObject.addProperty("eventType", "Leave");
                }
                jsonObject.addProperty("vehicleId", selectedvehiclemodel.getUserVehicleId());

                call = apiService.uploadObd(user.getUserAccessToken(), jsonObject);

                break;


            case 22:

                ArrayList<BluetoothDeviceModel> deviceModels = new ArrayList<>();
                deviceModels = getConnectedRegisteredDevices(new DatabaseHelper(getApplicationContext()).fetchRegisteredDevices());
                JsonArray deviceList = new JsonArray();

                for (BluetoothDeviceModel deviceModel : deviceModels) {
                    if (deviceModel.isNowConnected()) {
                        deviceList.add(deviceModel.getId());
                    }
                }

                call = apiService.updateTripConnectedDevice(user.getUserAccessToken(), deviceList);
                break;


            case 23:
                if (localTripID == null || localDataArray == null) {
                    return;
                }
                jsonObject.addProperty("calculateDistance", true);
                jsonObject.addProperty("calculateSpeed", true);
                jsonObject.addProperty("tripID", localTripID);
                jsonObject.addProperty("userID", user.getUserId());

                JsonArray tempDataArray = new JsonArray();

                Log.e(TAG, "Local Arraylist size : " + localDataArray.length());

                for (int i = 0; i < localDataArray.length(); i++) {
                    try {
                        JSONObject tempDataObj = localDataArray.getJSONObject(i);
                        JsonObject dataObj = new JsonObject();
                        dataObj.addProperty("latitude", tempDataObj.getDouble("latitude"));
                        dataObj.addProperty("latitude", tempDataObj.getDouble("longitude"));
                        dataObj.addProperty("createdOn", tempDataObj.getString("createdOn"));
                        tempDataArray.add(dataObj);
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
                jsonObject.add("data", tempDataArray);

                call = apiService.uploadVehDetails(user.getUserAccessToken(), jsonObject);

                break;

            case 24:
                call = apiService.getUser(user.getUserAccessToken(), true);
                break;

            case 25 :
                Log.e(TAG,"Distance Recalculate Api Called");
                call = apiService.getDistanceRecalculation(user.getUserAccessToken(), currentTrip.getId());
                break;

            default:
                //fragmentHomeBinding.progressLayout.setVisibility(View.VISIBLE);
                call = apiService.getDocVehicleAlerts(user.getUserAccessToken(), true, false);
                break;
        }

        Log.e(TAG, purpose + " Input :" + jsonObject);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {

                    if (response.isSuccessful()) {
                        addLog( purpose +" Response is success");

                        //If the response is null, we will return immediately.
                        ResponseBody responseBody = response.body();
                        if (responseBody == null)
                        {
                            addLog(purpose + " Response null:");
                            return;
                        }

                        String responseStr = responseBody.string();
                        Log.e(TAG, purpose + " Response :" + responseStr);

                        switch (value) {
                            case 0:
                                Log.e(TAG, "Hello");
                                //parseFetchVehicles(responseStr);
                                break;
                            case 1:

                                boolean isDriveMode = myPreferenece.getSharedPreferences().getBoolean(MyPreferenece.DRIVING_MODE, true);

                                //If the service is not bound, we need to exit from the program

                                if (!isDriveMode) {
                                    //It is for phone mode.
                                } else if (!isServiceBound) {
                                    Log.e(TAG, "service bound false");
                                    return;
                                }

                                JSONObject jsonObject = new JSONObject(responseStr);
                                String status = jsonObject.getString("status");
                                JSONObject userTripDetails = jsonObject.getJSONArray("userTrips").getJSONObject(0);


                                String tripID = userTripDetails.getString("userTripId");
                                String startLatitude = userTripDetails.getString("startLatitude");
                                String startLongitude = userTripDetails.getString("startLongitude");
                                String startTime = userTripDetails.getString("startTime");

                                currentTrip = new TripRecord(tripID, startTime, null, 0, 0, "0", "0", 0, startLatitude, startLongitude, null, null, "OnGoing Trip", "NA", 0, 0, 0, 0, "NA", "0", "0");
                                sendMyBroadcast(0);
                                sendMyBroadcast(3);

                                Log.e(TAG, "isDriveMode " + isDriveMode);


                                addLog("Trip start api called. Status : " + status + " onclick trip start = " + isManualStart);


                                /*if (isRecognitionApiRunning){
                                    unregisterRecognitionBroadcastReceiver();
                                    removeRecognitionUpdates();
                                }*/

                                //if (isDriveMode) {
                                //This is for drving mode with OBD-2
                                mileage = 0;
                                performTaskonStartTrip();

                                //} else {
                                // callGPSTripCalculation();
                                // fragmentHomeBinding.startDriveButton.setText("Stop Driving");
                                // }
                                //disableDriveLayout(false);
                                /*BluetoothHelperClass.showSinpleWarningDialog(context, getLayoutInflater(), "Hello There!", "Please wait.... we're connecting with your car!\n" +
                                        "For getting Trip details please don't Close or Minimize the App while Driving.");
*/

                                break;
                            case 2:
                                //Upload trouble code response
                                temptroubleCodes.clear();
                                break;

                            case 3:
                                //new DatabaseHelper(context).insertEmptyVehicleObdDetails(selectedvehiclemodel.getUserVehicleId());
                                break;
                            case 4:
                                break;

                            case 5:
                                parseFetchTroublecodesResponse(responseStr);
                                break;
                            case 6:
                                tempdrivingAlerts = new JsonArray();
                                break;
                            case 7:
                                ecoSpeedAlerts = new JsonArray();

                                //    parseLoginRegisterResponse(responseStr);
                                break;

                            case 8:
                                //   parseFetchDrivingALerts(responseStr);
                                break;
                            case 9:

                                //     parseFetchTripsResponse(responseStr);
                                break;

                            case 10:
                                //Clear temporary lat long model array list.
                                tempTripLatLongArrayList.clear();

                                if (tripDatabase == null) {
                                    tripDatabase = TripDatabase.getDatabase(ForegroundService.this);
                                }
                                parseGPSDistance(responseStr);
                                tripDatabase.tripDao().deleteTripsByTripId(currentTrip.getId());
                                tripDatabase.drivingAlertDao().deleteDrivingAlertByTripId(currentTrip.getId());



                                //Parse GPS calculate for a trip.
                                // parseFetchTripsResponse(responseStr);
                                break;
                            case 11:
                                //  parseFetchDrivingAnalytics(responseStr);
                                break;

                            case 12:
                                //  parseWeeklyReportDrivingAnalytics(responseStr);
                                break;
                            case 13:
                                // parseFetchDrivingTools(responseStr);
                                break;
                            case 14:
                                //resetVhsParamsOnTripUpdate();
                                break;
                            case 15:
                                //voice note when trip is ended
                                JSONObject json = new JSONObject(responseStr);
                                if (json.has("userTrips")) {
                                    JSONArray jArray = json.getJSONArray("userTrips");
                                    double safeKm = Double.parseDouble(jArray.getJSONObject(0).getString("safeKm"));
                                    double unsafeKm = 0;
                                    if (jArray.getJSONObject(0).has("unsafeKm")) {
                                        unsafeKm = Double.parseDouble(jArray.getJSONObject(0).getString("unsafeKm"));
                                    }
                                    if (safeKm + unsafeKm > 1) {
                                        textToSpeech.speak(rewardStatus(safeKm, unsafeKm), TextToSpeech.QUEUE_FLUSH, null, null);
                                    }
                                }
                                resetAllValues(true);


                                String language = myPreferenece.getLang();
                                textToSpeech.stop();

                                switch (language){
                                    case "hi":
                                        callSpeaker(tripEndedMsgHindi);
                                        break;
                                    default:
                                        callSpeaker(tripEndedMsg);
                                        break;

                                }

//                                callSpeaker(tripEndedMsg);
                                showNotification(tripEndedMsg);

                                if (context instanceof GypseeMainActivity) {
                                    GypseeMainActivity activity = (GypseeMainActivity) context;
                                    HomeFragment fragment = (HomeFragment) activity.getSupportFragmentManager().findFragmentById(R.id.mainFrameLayout);
                                    if (fragment != null) {
                                        fragment.hideEndTripBox();
                                    } else {
                                        Log.e("WrapTxt", "HomeFragment is null");
                                    }
                                } else {
                                    Log.e("WrapTxt", "context is not MainActivity or is null");
                                }


                                break;
                            case 16:
                                getUserVehicles();
                                break;
                            case 17:
                                parseFetchVehicles(responseStr);
                                break;
                            case 18:
                                Log.e(TAG, "upload obd response: " + responseStr);
                                break;
                            case 19:
                                //parseFetchVehicleHealth(responseStr);
                                break;

                            case 20:
                                //update registered devices
                                break;

                            case 23:
                                //stop timer task
                                localDataArray = null;
                                localTripID = null;
                                if (internetAvailabilityTimer != null) {
                                    internetAvailabilityTimer.cancel();
                                    internetAvailabilityTimer.purge();
                                    internetAvailabilityTimer = null;
                                }
                                deleteGPSStorageFile();

                                break;

                            case 24:
                                parseLoginRegisterResponse(responseStr);
                                break;
                            case 25:
                                Log.e(TAG,"Distance Recalculated Successful");
                                addLog("Distance Recalculated Successful");
                                break;


                        }
                    }

                    else {
                        Log.e(TAG, purpose + " Response is not successful");

                        String errResponse = response.errorBody().string();
                        Log.e("Error code 400", errResponse);
                        addLog(purpose + "Response is : " + errResponse);

                        int responseCode = response.code();
                        if (responseCode == 401 || responseCode == 403) {
                            //include logout functionality
                            Utils.clearAllData(getApplicationContext());
                            return;
                        }

                        switch (value) {
                            case 1:

                                activityCaptured = false;

                                break;
                            case 10:
                                addLog("Uploading GPS for trip calculation API call failed : " + errResponse);

                            case 11:
                                // loadUserImageInTotoalTrips();
                                break;
                            case 15:
                                if (responseCode == 400 || responseCode == 500) {
                                    if (endCallCounter >= 2) {
                                        resetAllValues(true);
                                        return;
                                    }
                                    endCallCounter += 1;
                                    endTrip();

//                                    isServiceBound = false;
                                }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

               /* fragmentHomeBinding.progressLayout.setVisibility(View.GONE);
                fragmentHomeBinding.logoLayout.setEnabled(true);
                fragmentHomeBinding.startDriveButton.setEnabled(true);
*/
                addLog(purpose+" end trip failed "+t.getMessage());


                Log.e(TAG, "error here since request failed");
                if (t.getMessage().contains("Unable to resolve host")) {
                    //  Toast.makeText(context, "Please Check your internet connection", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Please check your internet connection");
                }
                switch (value) {

                    case 15:
                        resetAllValues(true);
                        /*if (t.getMessage().contains("Unable to resolve host")) {
                            startPushDataTimer();
                        }*/
                        break;
                    default:
                        break;
                }

            }
        });
    }

    private void checkBackGroundLocationServiceRunningOrNot() {
        boolean isRunning =new  Utils().isServiceRunning(BackgroundLocationService.class,this);

        if (!isRunning){
            startBackGroundLocationService();
        }
    }

    private void startTripAfterDelay(long delay) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                Log.e(TAG, "startTripAfterDelay: "+ inVehicleActivity );
                // Code to run after the delay
                if(inVehicleActivity||isManualStart)
                {
                    Log.e(TAG, "run: checkTripStarted" );
                    checkTripStarted();
                }
            }
        }, delay);

    }


    public static String rewardStatus(Double safeKm, Double unsafeKm) {
        if (safeKm < unsafeKm) {
            return notRewarded;
        } else {
            return String.format(rewarded, round(safeKm));
//            return "You drove well, "+round(safeKm)+" safe coins credited to your account";
        }
    }

    private int endCallCounter = 0;

    public Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }


    private void parseLoginRegisterResponse(String response) throws JSONException {
        JSONObject jsonResponse = new JSONObject(response);

        //Go to main Activity

        // ArrayList<String> values = new ArrayList<>();
        JSONObject userJsonObject = jsonResponse.getJSONObject("user");

        String userId, userName, userFullName, userEmail, userPhoneNumber, userAccessToken, fcmToken, userImg, userDeviceMac,
                userTypes, referCode, createdOn, lastUpdatedOn, userAddresses;

        JSONObject userWallet = userJsonObject.has("userWallet") ? userJsonObject.getJSONObject("userWallet") : null;

        int walletAmount;

        boolean approved, locked, signUpBonusCredited, referCodeApplied;

        userId = userJsonObject.has("userId") ? userJsonObject.getString("userId") : "";
        userName = userJsonObject.has("userName") ? userJsonObject.getString("userName") : "";
        userFullName = userJsonObject.has("userFullName") ? userJsonObject.getString("userFullName") : "";
        userEmail = userJsonObject.has("userEmail") ? userJsonObject.getString("userEmail") : "";
        userPhoneNumber = userJsonObject.has("userPhoneNumber") ? userJsonObject.getString("userPhoneNumber") : "";
        userAccessToken = userJsonObject.has("userAccessToken") ? userJsonObject.getString("userAccessToken") : "";
        fcmToken = userJsonObject.has("fcmToken") ? userJsonObject.getString("fcmToken") : "";
        userImg = userJsonObject.has("userImg") ? userJsonObject.getString("userImg") : "";
        userDeviceMac = userJsonObject.has("userDeviceMac") ? userJsonObject.getString("userDeviceMac") : "";
        userTypes = userJsonObject.has("userTypes") ? userJsonObject.getString("userTypes") : "";
        referCode = userJsonObject.has("referCode") ? userJsonObject.getString("referCode") : "";
        createdOn = userJsonObject.has("createdOn") ? userJsonObject.getString("createdOn") : "";
        lastUpdatedOn = userJsonObject.has("lastUpdatedOn") ? userJsonObject.getString("lastUpdatedOn") : "";
        userAddresses = userJsonObject.has("userAddresses") ? userJsonObject.getString("userAddresses") : "";
        approved = userJsonObject.has("approved") && userJsonObject.getBoolean("approved");
        locked = userJsonObject.has("locked") && userJsonObject.getBoolean("locked");
        signUpBonusCredited = userJsonObject.has("signUpBonusCredited") && userJsonObject.getBoolean("signUpBonusCredited");
        referCodeApplied = userJsonObject.has("referCodeApplied") && userJsonObject.getBoolean("referCodeApplied");

        Log.e(TAG, "wallet: " + userWallet);

        walletAmount = (userWallet != null) ? (userWallet.has("loyaltyPoints") ? userWallet.getInt("loyaltyPoints") : 0) : 0;

        boolean inTrainingMode = userJsonObject.has("inTrainingMode") && userJsonObject.getBoolean("inTrainingMode");

        Log.e(TAG, "inTrainingMode: " + inTrainingMode);

        myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, getApplicationContext());
        User user = new User(userId, userName, userFullName, userEmail, userPhoneNumber, userAccessToken, fcmToken, userImg, userDeviceMac,
                userTypes, referCode, createdOn, lastUpdatedOn, approved, locked, signUpBonusCredited, referCodeApplied, inTrainingMode, String.valueOf(walletAmount));
        myPreferenece.storeUser(user);

    }


    private final static String VHS_ID = "VHS_ID";
    private final static String VHS_NAME = "VHS_NAME";
    private final static int VHS_SHARE_PENDING_INTENT_REQUEST = 202;

    private void showNotificationForVHS(double vhsScore) {

        String shareMessage = "Hey, my car health score is " + (int) vhsScore + ". Get your car health score at https://play.google.com/store/apps/details?id=in.gypsee.customer";

        Intent shareIntent = new Intent();

        if (vhsScore == 100) {
            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.hundred);
            String sImageUrl = MediaStore.Images.Media.insertImage(getContentResolver(), icon, "title", "description");
            Uri savedImageURI = Uri.parse(sImageUrl);
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, savedImageURI);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        } else {
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        }


        PendingIntent pendingShareIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingShareIntent = PendingIntent.getActivity(this, VHS_SHARE_PENDING_INTENT_REQUEST, shareIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingShareIntent = PendingIntent.getActivity(this, VHS_SHARE_PENDING_INTENT_REQUEST, shareIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }


        String message = "";

        if (vhsScore == 100) {
            message = "Congratulations your last trip's car health score is 100";
        } else if (vhsScore < 100 && vhsScore > 87.5) {
            message = "Your last trip's car health score is " + (int) vhsScore;
        } else if (vhsScore == 87.5) {
            message = "Oops there was an issue in the last trip. Your last trip's car health score is 87";
        } else {
            message = "Oops there were some issues in the last trip. Your last trip's car health score is " + (int) vhsScore;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createVHSNotificationChannel(VHS_ID, VHS_NAME, message, pendingShareIntent, vhsScore);
        } else {
            Intent intent = new Intent(this, GypseeMainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, VHS_PENDING_INTENT_REQUEST, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.bigText(message);
            //bigTextStyle.setBigContentTitle(message);

            builder.setStyle(bigTextStyle);
            // builder.setOnlyAlertOnce(true); //to quietly update the notification
            builder.setWhen(System.currentTimeMillis());
            builder.setOngoing(false);
            builder.setSmallIcon(R.drawable.notif_icon);
            builder.setPriority(Notification.PRIORITY_HIGH);
            builder.addAction(android.R.drawable.ic_search_category_default, "Check", pendingIntent);
            if (vhsScore == 100) {
                builder.addAction(android.R.drawable.ic_menu_share, "Share", pendingShareIntent);
            }
            builder.setFullScreenIntent(pendingIntent, true);
            builder.build();

            //Notification notification = builder.build();
        }

    }

    private static final int VHS_PENDING_INTENT_REQUEST = 200;
    private static final int VHS_NOTIFICATION_REQUEST_CODE = 201;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createVHSNotificationChannel(String channelID, String channelName, String message, PendingIntent shareIntent, double vhsScore) {

        Intent resultIntent = new Intent(this, GypseeMainActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(VHS_PENDING_INTENT_REQUEST, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationChannel notificationChannel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_LOW);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null; //exit if notification manager is null
        notificationManager.createNotificationChannel(notificationChannel);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(message);
        //bigTextStyle.setBigContentTitle(message);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelID);
        notificationBuilder.setOngoing(false)
                .setSmallIcon(R.drawable.notif_icon)
                .setContentTitle("Fuelshine")
                .setContentText(message)
                .setStyle(bigTextStyle)
                //.setOnlyAlertOnce(true) //to update notification quietly
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.ic_search_category_default, "Check", pendingIntent);
        //.addAction(android.R.drawable.ic_menu_share, "Share", shareIntent)
        //.build();
        if (vhsScore == 100) {
            notificationBuilder.addAction(android.R.drawable.ic_menu_share, "Share", shareIntent);
        }

        notificationBuilder.build();


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(VHS_NOTIFICATION_REQUEST_CODE, notificationBuilder.build());


    }

    private void parseFetchTroublecodesResponse(String responseStr) {
        try {
            vehicleAlertModelArrayList.clear();
            JSONObject jsonObject = new JSONObject(responseStr);
            JSONArray vehicleAlerts = jsonObject.getJSONArray("alerts");

            for (int i = 0; i < vehicleAlerts.length(); i++) {
                //getting the first
                String vehicleAlertId, troubleCode, errorDesc = "", dtcNumber, distanceTraveledWithMILOn, fuelLevelOnAlert, createdOn, lastUpdatedOn;
                Boolean alertFixed;

                String distanceTraveledSinceCodeClear;
                JSONObject vehicleAlert = vehicleAlerts.getJSONObject(i);
                vehicleAlertId = vehicleAlert.getString("vehicleAlertId");
                troubleCode = vehicleAlert.getString("troubleCode");
                //errorDesc = vehicleAlert.getString("errorDesc");
                dtcNumber = commandResult.get("DTC_NUMBER");
                //distanceTraveledWithMILOn = vehicleAlert.getString("distanceTraveledWithMILOn");
                //fuelLevelOnAlert = commandResult.get("FUEL_LEVEL");

                createdOn = vehicleAlert.getString("createdOn");
                lastUpdatedOn = vehicleAlert.getString("lastUpdatedOn");
                alertFixed = vehicleAlert.getBoolean("alertFixed");
                distanceTraveledSinceCodeClear = vehicleAlert.getString("distanceTraveledSinceCodeClear");
                vehicleAlertModelArrayList.add(new VehicleAlertModel(vehicleAlertId, troubleCode, errorDesc, dtcNumber, "", "", createdOn, lastUpdatedOn,
                        alertFixed, distanceTraveledSinceCodeClear));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void resetAllValues(boolean sendTripEndBroadcast) {

        Log.e(TAG, "resetAllValues: ");
        speeds.clear();
        speedsWithIntervals.clear();
        emptyValues.clear();
        //isTriponeKmPopUpNotShown = true;
        // if (!myPreferenece.getUser().isInTrainingMode()) {
        selectedvehiclemodel = null;
        //}
        tripDistanceFromGPSm = 0;
        drivingAlerts = new JsonArray();
        maximumspeed = "01 km/hr";
        //fragmentHomeBinding.logoLayout.setEnabled(true);
        maximumRPM = 0;
        currentTrip = null;
        //isAlreadyRPMNotShown = true;
        isObdConnected = false;
        this.troubleCodes.clear();
        fuelDensity = 0;
        latestCapturedSpeed = 0;
        commandResult.clear();
        endCallCounter = 0;
        isServiceBound = false;
        if (sendTripEndBroadcast) {
            sendMyBroadcast(4);
        }
        isManualStart = false;
        resetEcoSpeedAlerts();
    }

    private void getUserVehicles() {
        User user = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, getApplicationContext()).getUser();
        if (user != null) {
            callServer(getResources().getString(R.string.vehicles_url).replace("userid", user.getUserId()), "Fetch cars", 17);
        }
    }

    private void parseFetchVehicles(String responseStr) {

        ArrayList<Vehiclemodel> vehiclemodelArrayList = new ArrayList<>();
        JSONObject jsonResponse = null;
        new DatabaseHelper(getApplicationContext()).deleteTable(DatabaseHelper.VEHICLES_TABLE);
        try {
            jsonResponse = new JSONObject(responseStr);
            //Go to main Activity
            // ArrayList<String> values = new ArrayList<>();
            JSONArray vehciclesArray = jsonResponse.getJSONArray("userVehicles");
            for (int i = 0; i < vehciclesArray.length(); i++) {
                JSONObject jsonObject = vehciclesArray.getJSONObject(i);

                Gson gson = new Gson();
                Vehiclemodel vehicleModel = gson.fromJson(jsonObject.toString(), Vehiclemodel.class);

                Log.e(TAG, "Vehicle Model Created :" + vehicleModel.toString());

                vehiclemodelArrayList.add(vehicleModel);


                if (jsonObject.has("geoFencingCoordinates")) {
                    JSONObject geofenceObject = new JSONObject(jsonObject.getString("geoFencingCoordinates"));
                    Log.e(TAG, "regionType: " + geofenceObject.getString("regionType"));
                    Log.e(TAG, "center lat: " + geofenceObject.getJSONObject("center").getDouble("lat"));
                    Log.e(TAG, "center long: " + geofenceObject.getJSONObject("center").getDouble("lng"));
                    Log.e(TAG, "center radius: " + geofenceObject.getDouble("radius"));
                    double lat = geofenceObject.getJSONObject("center").getDouble("lat");
                    double lng = geofenceObject.getJSONObject("center").getDouble("lng");
                    float radius = (float) geofenceObject.getDouble("radius");
                    addGeofence(new LatLng(lat, lng), radius);

                }

            }

            new DatabaseHelper(getApplicationContext()).InsertAllVehicles(vehiclemodelArrayList);
            //SendBroadcast to receiver.
            Intent intent = new Intent("CarsList");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        } catch (
                JSONException e) {
            e.printStackTrace();
        }


    }

    double tripDistanceFromGPSm;

    private void parseGPSDistance(String responseStr) throws JSONException {

        JSONObject jsonObject = new JSONObject(responseStr);
        tripDistanceFromGPSm = jsonObject.getDouble("tripDistanceInKm");

        commandResult.put("TRIP_DISTANCE", String.valueOf(tripDistanceFromGPSm));
        currentTrip.setDistanceCovered(tripDistanceFromGPSm + "");
        sendMyBroadcast(3);

        /*if (isTriponeKmPopUpNotShown && tripDistanceFromGPSm >= 1) {
            isTriponeKmPopUpNotShown = false;
            BluetoothHelperClass.showTriponeKmDialog(context, getLayoutInflater(), "", "Your Driving log has now started. Have a safe drive with gypsee.");
        }*/
    }

    private JsonObject getCurrentLogValuesinJson() {

        JsonObject jsonObject = new JsonObject();


        if (endLocation != null) {
            jsonObject.addProperty("destAltitude", endLocation.getAltitude());
            jsonObject.addProperty("destLatitude", endLocation.getLatitude());
            jsonObject.addProperty("destLongitude", endLocation.getLongitude());
            jsonObject.addProperty("currentAltitude", endLocation.getAltitude());
            jsonObject.addProperty("currentLatitude", endLocation.getLatitude());
            jsonObject.addProperty("currentLongitude", endLocation.getLongitude());


        } else {
        }

        jsonObject.addProperty("airIntakeTemp", commandResult.get("AIR_INTAKE_TEMP"));
        jsonObject.addProperty("ambientAirTemp", commandResult.get("AMBIENT_AIR_TEMP"));
        jsonObject.addProperty("barometricPressure", commandResult.get("BAROMETRIC_PRESSURE"));

        jsonObject.addProperty("currentSpeed", commandResult.get("SPEED"));

        jsonObject.addProperty("engineCoolantTemp", commandResult.get("ENGINE_COOLANT_TEMP"));
        jsonObject.addProperty("engineLoad", commandResult.get("ENGINE_LOAD"));
        jsonObject.addProperty("engineRpm", commandResult.get("ENGINE_RPM"));
        jsonObject.addProperty("engineRunTime", commandResult.get("ENGINE_RUNTIME"));
        jsonObject.addProperty("equivRatio", commandResult.get("EQUIV_RATIO"));
        jsonObject.addProperty("fuelEconomy", commandResult.get("FUEL_ECONOMY"));
        jsonObject.addProperty("fuelPressure", commandResult.get("FUEL_PRESSURE"));
        jsonObject.addProperty("intakeManifoldPressure", commandResult.get("INTAKE_MANIFOLD_PRESSURE"));
        jsonObject.addProperty("longTermFuelTrimBank2", commandResult.get("Long Term Fuel Trim Bank 2"));
        jsonObject.addProperty("lowerSpeed", "");
        jsonObject.addProperty("maxRmp", commandResult.get("maxEngineRPM"));
        jsonObject.addProperty("shortTermFuelBank1", commandResult.get("Short Term Fuel Trim Bank 1"));
        jsonObject.addProperty("shortTermFuelBank2", commandResult.get("Short Term Fuel Trim Bank 2"));
        jsonObject.addProperty("termFuelTrimBank1", commandResult.get("Term Fuel Trim Bank 1"));
        jsonObject.addProperty("topSpeed", commandResult.get("maximumspeed"));
        jsonObject.addProperty("fuelLevel", commandResult.get("FUEL_LEVEL"));
        jsonObject.addProperty("MAF", commandResult.get("MAF"));

        jsonObject.addProperty("throttlePos", commandResult.get("THROTTLE_POS"));
        jsonObject.addProperty("dtcNumber", commandResult.get("DTC_NUMBER"));
        jsonObject.addProperty("timingAdvance", commandResult.get("TIMING_ADVANCE"));
        jsonObject.addProperty("equivRatio", commandResult.get("EQUIV_RATIO"));
        jsonObject.addProperty("fuelType", commandResult.get("FUEL_TYPE"));
        String distancePostCC = commandResult.get("DISTANCE_TRAVELED_AFTER_CODES_CLEARED");

        if (distancePostCC == null || distancePostCC.isEmpty() || distancePostCC.equals("NA")) {
            distancePostCC = "0km";
        }

        jsonObject.addProperty("distancePostCC", Double.parseDouble(distancePostCC.replace("km", "")));

        if (commandResult.get("TRIP_DISTANCE") == null) {
            commandResult.put("TRIP_DISTANCE", "0");
        }
        jsonObject.addProperty("tripDistance", commandResult.get("TRIP_DISTANCE"));


        return jsonObject;
    }

    ArrayList<TripLatLong> tripLatLongArrayList = new ArrayList<>();

    private LogCSVWriter myCSVWriter;


    String direction = "";

    private final SensorEventListener orientListener = new SensorEventListener() {
        //float[] accelorometerValues = new float[3];

        public void onSensorChanged(SensorEvent event) {


            Float x = event.values[0];
            String dir = "";
            if (x >= 337.5 || x < 22.5) {
                dir = "N";
            } else if (x >= 22.5 && x < 67.5) {
                dir = "NE";
            } else if (x >= 67.5 && x < 112.5) {
                dir = "E";
            } else if (x >= 112.5 && x < 157.5) {
                dir = "SE";
            } else if (x >= 157.5 && x < 202.5) {
                dir = "S";
            } else if (x >= 202.5 && x < 247.5) {
                dir = "SW";
            } else if (x >= 247.5 && x < 292.5) {
                dir = "W";
            } else if (x >= 292.5 && x < 337.5) {
                dir = "NW";
            }

            direction = dir;
            // We are uploading the latest angle changes
            if (angles.size() == 10) {
                angles.remove(0);
            }
            angles.add(x);
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // do nothing
        }
    };

    private SensorManager sensorManager;

    private void setUpSensorData() {

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // get Orientation sensor
        sensorManager.registerListener(orientListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);

        // Accelaration sensor
    /*    sensorManager.registerListener(orientListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);*/
      /*  sensorManager.registerListener(orientListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_NORMAL);*/
        /*sensorManager.registerListener(orientListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);*/
       /* sensorManager.registerListener(orientListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_NORMAL);*/
    }

    Date tripStartTime;

    private final Runnable thritySecComd = new Runnable() {
        public void run() {

            if (currentTrip != null) {
                isThirtySec = true;
                Log.e(TAG, "Thrirty seconds");
                new Handler().postDelayed(thritySecComd, 30000);
                setTripTime();
            }
        }
    };

    private void setTripTime() {
        if ((commandResult.get("ENGINE_RUNTIME") == null || commandResult.get("ENGINE_RUNTIME").equals("")) && tripStartTime != null && currentTrip != null) {
            String tripTime = TimeUtils.getTimeInhms(TimeUtils.calcDiffTimeInSec(tripStartTime, new Date()));
            currentTrip.setEngineRuntime(tripTime);
            sendMyBroadcast(3);

        }
    }

    private final Runnable oneMinuteCommand = new Runnable() {
        public void run() {

            if (currentTrip != null) {
                isOneMinute = true;
                new Handler().postDelayed(oneMinuteCommand, 60000);
            }
        }
    };

    private Timer uploadGpCalcTimer;

    private void callGPSTripCalculation() {

        if (uploadGpCalcTimer == null) {
        } else {
            uploadGpCalcTimer.cancel();
        }

        uploadGpCalcTimer = new Timer();

        uploadGpCalcTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (currentTrip != null) {
                        if (currentTrip != null) {
                            if (networkCallback.isNetWorkAvailable) {
                                callServer(getString(R.string.gpsTripDistance), "Upload GPS for trip calculation", 10);
                            }
                        }

                    }
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
            }
        }, 100, 30000);
    }

    private void performTaskonStartTrip() {
        myPreferenece.setIsTripRunning(true);

        startWidgetService();

        Log.e(TAG, "performtaskonstarttrip called");
        if (tripDatabase == null) {
            tripDatabase = TripDatabase.getDatabase(ForegroundService.this);
        }
        if (currentTrip != null) {
            tripDatabase.tripDao().deleteTripsByTripId(currentTrip.getId());
            tripDatabase.drivingAlertDao().deleteDrivingAlertByTripId(currentTrip.getId());
        }
        tripLatLongArrayList.clear();
        //initMyCsvWriter();
        setUpSensorData();
        tripStartTime = new Date();
        new Handler().post(thritySecComd);
        new Handler().post(oneMinuteCommand);
        callGPSTripCalculation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //Update the trip detils after 60 seconds to server.
                if (currentTrip != null) {
                    //THis will update the trip details
                    //callBackgroundService(1);
                    callServer(getResources().getString(R.string.tripupdate_url), "Update trip ", 14);


//                  For removing bluetooth dependency comment or remove  callServer(getString(R.string.updateTripConnectedDevices).replace("tripId", currentTrip.getId()), "Update Trip Connected Devices", 22);

                    if(!isManualStart){
                        callServer(getString(R.string.updateTripConnectedDevices).replace("tripId", currentTrip.getId()), "Update Trip Connected Devices", 22);
                    }

                    //callBackgroundService(3);
                    if (selectedvehiclemodel != null) {
                        callServer(getResources().getString(R.string.UpdateVehDetails_Url).replace("vehicleId", selectedvehiclemodel.getUserVehicleId()), "Update vehicle ", 16);
                    }
                    uploadTripHistory();
                    callServer(getString(R.string.vehicleAlertUrl).replace("vehicleId", selectedvehiclemodel.getUserVehicleId()), "Fetch selected Vehicle Alerts", 5);

                }
            }
        }, 70000);
        //Upload trip history every 10 min . Also, we need to implement a way to delete the file.This is pending.

        String language = myPreferenece.getLang();
        textToSpeech.stop();

        switch (language){
            case "hi":
                callSpeaker(tripStartedMsgHindi);
                break;

            default:
                callSpeaker(tripStartedMsg);
                break;
        }


//        callSpeaker(tripStartedMsg);


        showNotification(tracking);
    }

    private void callSpeaker(String message) {
        textToSpeech.stop();
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);

    }

    private void uploadTripHistory() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (currentTrip != null) {
                    // callBackgroundService(0);
                    //callBackgroundService(1);
                    callServer(getResources().getString(R.string.tripupdate_url), "Update trip ", 14);
                    uploadTripHistory();
                }
            }
        }, 600000);
    }


    boolean isThirtySec = true, isOneMinute = true;

    private void queueCommands() {

        Log.e(TAG, "queueCommands called : ");
        if (isServiceBound) {
            for (ObdCommand Command : ObdConfig.getCommands(isStarting, false, (/*currentTrip == null ||*/ commandResult.get("VIN") == null), isThirtySec, isOneMinute, getApplicationContext())) {
                service.queueJob(new ObdCommandJob(Command));

                /*if (currentTrip != null) {
                    isOneMinute = !isOneMinute && isOneMinute;
                    isThirtySec = !isThirtySec && isThirtySec;
                }*/
            }
        }
    }

    private void doBindService() {

        Log.e(TAG, "Binding OBD service..");
        Intent serviceIntent = new Intent(getApplicationContext(), ObdGatewayService.class);
        bindService(serviceIntent, serviceConn, Context.BIND_AUTO_CREATE);
    }

    int incrementValueForVINCheck = 0;

    private boolean serviceConnEnabled = false;

    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            Log.e(TAG, className.toString() + " service is bound");
            isServiceBound = true;
            serviceConnEnabled = true;
            incrementValueForVINCheck = 0;
            service = ((AbstractGatewayService.AbstractGatewayServiceBinder) binder).getService();
            service.setContext(getApplicationContext());


            Log.e(TAG, "Starting service live data");
            try {
                commandResult.clear();
                service.startService(bluetoothSocket, false, false);
                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //If the service is bound then only we need to ask to connect to the vehicle.
                        if (isServiceBound) {
                            addDummyValues();
                            fragmentHomeBinding.logoLayout.setEnabled(true);
                        }
                    }
                }, 15000);*/
            } catch (IOException ioe) {
                Log.e(TAG, "Failure Starting live data OBD");
                if (myPreferenece.getConnectedDeviceMac().equals("") || myPreferenece.getConnectedDeviceMac().equals(myPreferenece.getStringData(ConfigActivity.BLUETOOTH_LIST_KEY))) {
                    stopLiveData();
                }
            }
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.e(TAG, className.toString() + " service is unbound");
            isServiceBound = false;
            serviceConnEnabled = false;
        }
    };


    private Vehiclemodel selectedvehiclemodel;

    private void checkForVehicle(String cmdResult) {

        // Checking if isVinCheckedAlready  = true, then return.
        //fragmentHomeBinding.progressLayout.setVisibility(View.GONE);

        if (selectedvehiclemodel == null) {

            Log.e(TAG, "Checking for the vehicle " + commandResult);

            ArrayList<Vehiclemodel> vehiclemodelArrayList = new DatabaseHelper(getApplicationContext()).fetchAllVehicles();
            Log.e(TAG, "vehicles size : " + vehiclemodelArrayList.size());
            // If the vehicle Size is 1. We will directly connect with OBD and return from program
            if (vehiclemodelArrayList.size() == 0) {
                // showAddCarDialog(true);
            } else {
                //Only one vehicle connected.

                Log.e(TAG, "vehicles size : " + "One ");
                selectedvehiclemodel = vehiclemodelArrayList.get(0);
                if (selectedvehiclemodel.getFuelType().equals("PETROL")) {
                    fuelDensity = 748;
                }

                // fragmentHomeBinding.setUserName("Hola, " + StringFormater.capitalizeWord(user.getUserFullName()) + "\n" + StringFormater.capitalizeWord(selectedvehiclemodel.getVehicleBrand() + " " + selectedvehiclemodel.getVehicleModel()));
                checkTripStarted();

            }
            //multiple cars case removed

        } else {
            Log.e(TAG, "Selected Vehicle Model : " + selectedvehiclemodel.getVehicleBrand() + " - " + selectedvehiclemodel.getVehicleModel());
            //checkTripStarted();
        }
    }

    public TripRecord currentTrip;

    BroadcastReceiver locationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("LocationReceiver")) {
                Location location = intent.getParcelableExtra("Location");

                //Return if invalid location
                if (location.getLatitude() == 0.0 || location.getLongitude() == 0.0) {
                    return;
                }
                setLastCachedLocation(location);
                requestLocationUpdates(location);
                endLocation = location;
                if (fleetSocketConnection != null) {
                    fleetSocketConnection.setEndLocation(endLocation);
                }
                //checkLocationCar(location);
            }
            if (intent.getAction().equals("BluetoothConnectionChange")) {
                updateDevicesDatabase();
                checkRegisteredDeviceConnected();
            }
            if (intent.getAction().equals("BluetoothDisconnected")) {
                Log.e(TAG, "BluetoothDisconnected coming");
                updateDevicesDatabase();
            }
            if (intent.getAction().equals("ScanServiceActiviated")) {
                if (isObdConnected) {
                    stopLiveData();
                } else {
                    isServiceBound = false;
                    endTrip();
                    showNotification("Scanning car");
                }
            }
            if (intent.getAction().equals("GEOFENCE_ENTER")) {
                if (selectedvehiclemodel != null) {
                    callServer(getResources().getString(R.string.UpdateVehDetails_Url).replace("vehicleId", selectedvehiclemodel.getUserVehicleId()), "Update vehicle ", 16);
                }
                callServer(getString(R.string.geofenceTrigger), "Geofence Trigger Enter", 21);
            }
            if (intent.getAction().equals("GEOFENCE_EXIT")) {
                if (selectedvehiclemodel != null) {
                    callServer(getResources().getString(R.string.UpdateVehDetails_Url).replace("vehicleId", selectedvehiclemodel.getUserVehicleId()), "Update vehicle ", 16);
                }
                callServer(getString(R.string.geofenceTrigger), "Geofence Trigger Exit", 21);
            }

            if (intent.getAction().equals("REFRESH_USER")) {
                callServer(getResources().getString(R.string.Fetch_UserDetils_url).replace("userid", myPreferenece.getUser().getUserId()), "Fetch user data", 24);
            }


        }
    };


    public void refreshRegisteredDevices() {
//        callServer(getString(R.string.updateRegisteredDevices).replace("userId", myPreferenece.getUser().getUserId()), "Update Registered Devices", 20);
        User user = myPreferenece.getUser();
        if (user != null) {
            String userId = user.getUserId();
            String url = getString(R.string.updateRegisteredDevices).replace("userId", userId);
            callServer(url, "Update Registered Devices", 20);
        } else {
            Log.e(TAG, "User is null. Cannot refresh registered devices.");
        }
    }


    private Location getLastCachedLocation() {
        double lastLat = 0, lastLng = 0;
        try {
            String[] parkedLocationString = myPreferenece.getLastLocationCache().split("/");
            lastLat = Double.parseDouble(parkedLocationString[0]);
            lastLng = Double.parseDouble(parkedLocationString[1]);
            Log.e(TAG, "cache location: " + lastLat + ", last long: " + lastLng);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Location cachedLocation = new Location(LocationManager.NETWORK_PROVIDER);
        cachedLocation.setLatitude(lastLat);
        cachedLocation.setLongitude(lastLng);
        return cachedLocation;
    }

    private void setLastCachedLocation(Location location) {


        myPreferenece.setLatLng(location.getLatitude() + "/" + location.getLongitude());

    }


    float previousSpeed = 0;

    private void performGPSSpeedCalc(Location location) {
        Log.e(TAG, "coming in performGPS speed");
        float speed = location.getSpeed() * 3.6f;

        previousSpeed = filter(previousSpeed, speed, 2);

        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(0);

        //fragmentHomeBinding.speedFAB.setText(numberFormat.format(previousSpeed) + "\nkm/hr");
        if (emptyValues.containsKey("SPEED") || !isObdConnected) {
            Log.e(TAG, "entering update speed");
            updateSpeed(numberFormat.format(previousSpeed) + "km/h");
        }
    }

    private ArrayList<Date> speedsWithIntervals = new ArrayList<>();
    private List<Date> gpsWithIntervals = new ArrayList<>();
    private ArrayList<Integer> speeds = new ArrayList<>();

    com.gypsee.sdk.database.TripAlert temporaryAccAlert, tempDecAlert;

    JsonArray drivingAlerts = new JsonArray();
    JsonArray ecoSpeedAlerts = new JsonArray();
    JsonArray tempdrivingAlerts = new JsonArray();

    JsonArray angles = new JsonArray();
    boolean isReset;
    //private float harshAccelaration = 9.88f, harshDecelaration = -10.94f;
//    private float harshAccelaration = 10.23f, harshDecelaration = -16.58f;
    private Double harshAccelaration, harshDecelaration;


    private void addTripAlertToArray(com.gypsee.sdk.database.TripAlert temporaryAccAlert) {

        if (networkCallback.isNetWorkAvailable) {
            JsonObject jsonObject = new JsonObject();
            if (currentTrip != null && selectedvehiclemodel != null) {
                jsonObject.addProperty("alert_Type", temporaryAccAlert.getAlertType());
                jsonObject.addProperty("alert_value", temporaryAccAlert.getAlertValue());
                jsonObject.addProperty("alert_description", "");

                int chnageInspeed = Integer.parseInt(temporaryAccAlert.getAlertValue().replace(" RPM", "").replace(" km/hr", ""));

                if (temporaryAccAlert.getAlertType().contains("Harsh")) {
                    jsonObject.addProperty("g_force", (chnageInspeed * 0.028) + "m/sec2");

                } else {
                    jsonObject.addProperty("g_force", "");
                }
                jsonObject.addProperty("time_stamp", temporaryAccAlert.getTimeStamp());
                jsonObject.addProperty("time_interval", temporaryAccAlert.getTimeInterval());
                jsonObject.addProperty("lat", endLocation.getLatitude());
                jsonObject.addProperty("long", endLocation.getLongitude());
                jsonObject.add("impact", angles);
                currentTrip.setAlertCount(currentTrip.getAlertCount() + 1);
                sendMyBroadcast(3);

            }
            drivingAlerts.add(jsonObject.toString());
            if (currentTrip != null && drivingAlerts.size() > 0) {

                Date timeD = new Date(temporaryAccAlert.getTimeStamp());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String timeStamp = sdf.format(timeD);
                tempdrivingAlerts.addAll(drivingAlerts);
                drivingAlerts = new JsonArray();

                callServer(getString(R.string.addDrivingAlerturl).replace("vehicleId", selectedvehiclemodel.getUserVehicleId()), "Add driving alerts", 6);

                addLog("Network Available : The alert json is = " + drivingAlerts);
            }

        } else {
            // Only store specific alert types locally when the network is unavailable
            String alertType = temporaryAccAlert.getAlertType();
//            if (!alertType.equals("BelowEcoSpeed") && !alertType.equals("EcoSpeed") )
            if (alertType.equals("AboveEcoSpeed") || alertType.equals("Harsh Accelaration") || alertType.equals("Harsh Braking") || alertType.equals("Overspeed") || alertType.equals("TextAndDrive"))
            {

                int changeInSpeed = Integer.parseInt(temporaryAccAlert.getAlertValue().replace(" RPM", "").replace(" km/hr", ""));

                DrivingAlert tripAlert = new DrivingAlert("", "", "", "", 0, 0, 0, "", "");
                tripAlert.setAlertType(alertType);
                tripAlert.setAlertValue(temporaryAccAlert.getAlertValue());
                tripAlert.setTimeInterval(temporaryAccAlert.getTimeInterval());
                tripAlert.setTimeStamp(temporaryAccAlert.getTimeStamp());
                tripAlert.setLat(endLocation.getLatitude());
                tripAlert.setTripId(currentTrip.getId());
                tripAlert.setImpact(angles.toString());
                tripAlert.setLng(Double.parseDouble(String.valueOf(endLocation.getLongitude())));
                tripAlert.setgForce(alertType.contains("Harsh") ? (changeInSpeed * 0.028) + "m/sec2" : "");

                addLog("Network not Available: Stored driving alert = " + temporaryAccAlert.getAlertType() + " time interval = "+temporaryAccAlert.getTimeInterval());


                // Insert into Room database
                new Thread(() -> {
                    tripDatabase.drivingAlertDao().insertDrivingAlert(tripAlert);
                    addLog("Network Unavailable: Saved driving alert locally = " + tripAlert.getAlertType());
                }).start();
            } else {
//                addLog("Network Unavailable: Skipped saving alert since it doesn't match the driving alert types.");
            }
        }
    }





    private void syncDataWithServer() {
        if (networkCallback.isNetWorkAvailable) {
            new Thread(() -> {
                List<DrivingAlert> storedAlerts = tripDatabase.drivingAlertDao().getDrivingAlertsByTripId(currentTrip.getId());

                Gson gson = new Gson();
                for (DrivingAlert alert : storedAlerts) {

                    String alertType = alert.getAlertType();
//                    if (!alertType.equals("BelowEcoSpeed") && !alertType.equals("EcoSpeed") )
                    if (alertType.equals("AboveEcoSpeed") || alertType.equals("Harsh Accelaration") || alertType.equals("Harsh Braking") || alertType.equals("Overspeed") || alertType.equals("TextAndDrive"))
                    {

                        addLog("storing alert type = "+alert.getAlertType() + " alert value = " + alert.getAlertValue() );

                        String impactString = alert.getImpact();
                        JsonArray impactJsonArray = JsonParser.parseString(impactString).getAsJsonArray();

                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("alert_Type", alert.getAlertType());
                        jsonObject.addProperty("alert_value", alert.getAlertValue());
                        jsonObject.addProperty("time_stamp", alert.getTimeStamp());
                        jsonObject.addProperty("time_interval", alert.getTimeInterval());
                        jsonObject.addProperty("lat", alert.getLat());
                        jsonObject.add("impact", impactJsonArray);
                        jsonObject.addProperty("long", alert.getLng());
                        jsonObject.addProperty("g_force", alert.getgForce());
                        currentTrip.setAlertCount(currentTrip.getAlertCount() + 1);
                        sendMyBroadcast(3);

                        drivingAlerts.add(jsonObject.toString());

//                        tempdrivingAlerts.add(gson.toJson(jsonObject));

                        addLog("Stored Driving alert = " + alert.getAlertType() + " time interval = "+ alert.getTimeInterval());
                    }

                }
                tempdrivingAlerts.addAll(drivingAlerts);
                drivingAlerts = new JsonArray();


                // Call server for each alert
                callServer(getString(R.string.addDrivingAlerturl).replace("vehicleId", selectedvehiclemodel.getUserVehicleId()), "Add driving alerts", 6);
                // After successful sync, delete alerts from the database
                tripDatabase.drivingAlertDao().deleteDrivingAlertByTripId(currentTrip.getId());
                addLog("Network Available: Synced alerts with server and deleted from local storage.");
            }).start();
        } else {
            addLog("No network available for syncing data with server.");
        }
    }






    private void addVehicleEcoSpeed(com.gypsee.sdk.database.TripAlert ecoSpeedAlert) {

        if (networkCallback.isNetWorkAvailable) {
            JsonObject jsonObject = new JsonObject();
            if (currentTrip != null && selectedvehiclemodel != null) {
                jsonObject.addProperty("alert_Type", ecoSpeedAlert.getAlertType());
                jsonObject.addProperty("alert_value", ecoSpeedAlert.getAlertValue());
                jsonObject.addProperty("alert_description", "");
                jsonObject.addProperty("g_force", "");
                jsonObject.addProperty("time_stamp", ecoSpeedAlert.getTimeStamp());
                jsonObject.addProperty("time_interval", ecoSpeedAlert.getTimeInterval());
                jsonObject.addProperty("lat", endLocation.getLatitude());
                jsonObject.addProperty("long", endLocation.getLongitude());
                ecoSpeedAlerts.add(jsonObject);

                addLog("Network Available stored eco speed alert = "+ecoSpeedAlert.getAlertType() + " time interval = " + ecoSpeedAlert.getTimeInterval());

            }

            if (networkCallback.isNetWorkAvailable) {
                if (currentTrip != null) {
                    addLog("Network is Available. The Eco Speed alert json is = " + ecoSpeedAlerts.toString());
                    callServer(getString(R.string.vehicleEcoSpeedApi).replace("vehicleId", selectedvehiclemodel.getUserVehicleId()), "Add EcoSpeed alerts", 7);
                }
            }
        } else {
            // Only store specific alert types when the network is unavailable
            String alertType = ecoSpeedAlert.getAlertType();
            if ( alertType.equals("EcoSpeed") || alertType.equals("BelowEcoSpeed") )
            {

                // If not connected, store the data in the local database
                storeEcoSpeedAlertLocally(ecoSpeedAlert);
            } else {

            }
        }
    }


    private void storeEcoSpeedAlertLocally(TripAlert ecoSpeedAlert) {
        com.gypsee.sdk.database.TripAlert tripAlert = new com.gypsee.sdk.database.TripAlert(ecoSpeedAlert.getAlertType(), ecoSpeedAlert.getAlertValue(), ecoSpeedAlert.getTimeInterval(), ecoSpeedAlert.getgForce(), ecoSpeedAlert.getTimeStamp(), endLocation.getLatitude(), endLocation.getLongitude(), "", currentTrip.getId());

        new Thread(() -> {
            addLog("Network is not  Available. The Eco Speed alert json is = " + tripAlert);
//            db.tripAlertDao().insert(tripAlertEntity);
            tripDatabase.tripAlertDao().insertTripAlert(tripAlert);
        }).start();
    }


    public void syncEcoAlertsDataWithServer() {
        new Thread(() -> {
            List<com.gypsee.sdk.database.TripAlert> unsyncedAlerts = tripDatabase.tripAlertDao().getTripAlertsByTripId(currentTrip.getId());

//            JsonObject jsonObject = new JsonObject();
            Gson gson = new Gson();  // Using Gson to serialize objects
            for (com.gypsee.sdk.database.TripAlert alert : unsyncedAlerts) {

                String alertType = alert.getAlertType();
                if( alertType.equals("EcoSpeed") || alertType.equals("BelowEcoSpeed") ) {

                    // Create JSON object to send to the server
                    JsonObject jsonObject = new JsonObject();

                    jsonObject.addProperty("alert_Type", alert.getAlertType());
                    jsonObject.addProperty("alert_value", alert.getAlertValue());
                    jsonObject.addProperty("alert_description", "");
                    jsonObject.addProperty("g_force", "");
                    jsonObject.addProperty("time_stamp", alert.getTimeStamp());
                    jsonObject.addProperty("time_interval", alert.getTimeInterval());
                    jsonObject.addProperty("lat", alert.getLat());
                    jsonObject.addProperty("long", alert.getLng());


//                ecoSpeedAlerts.add(gson.toJson(jsonObject));
                    ecoSpeedAlerts.add(jsonObject);
                    addLog("Network not Available stored eco speed alert = "+alert.getAlertType() + " time interval = " + alert.getTimeInterval());

//                addLog("Stored ecospeed alert gson = "+gson.toJson(jsonObject));
                    addLog("Stored ecospeed alert json = " + jsonObject);
                }
                // Delete the alert after successful sync
//                db.tripAlertDao().deleteAlertById(alert.id);
            }
            // Send to the server
            if (currentTrip != null) {
                callServer(getString(R.string.vehicleEcoSpeedApi).replace("vehicleId", selectedvehiclemodel.getUserVehicleId()), "Add EcoSpeed alerts", 7);
            }
            tripDatabase.tripAlertDao().deleteByTripId(currentTrip.getId());
        }).start();
    }



    private SharedPreferences sharedPreferences;

    private void updateSpeed(String cmdResult) {

        if (cmdResult.isEmpty() || currentTrip == null) return;
        int speed;
        if (cmdResult.contains("mph")) {
            double speedn = 1.60934 * Double.parseDouble(cmdResult.replace("mph", "").replace("0.00", "0"));
            speed = (int) round(speedn);
        } else {
            speed = round(Integer.parseInt(cmdResult.replace("km/h", "")));
        }

        Log.e(TAG, "speed here: " + speed);

        latestCapturedSpeed = speed; //for vhs usage

        int timeDiff;
        speeds.add(speed);
        speedsWithIntervals.add(new Date());
        if (speedsWithIntervals.size() == 1)
            return;

        timeDiff = TimeUtils.calcDiffTimeInSec(speedsWithIntervals.get(0), speedsWithIntervals.get(1));
        if (timeDiff == 0) {
            // if (timeDiff == 0 || speed == 0) {

            speeds.remove(1);
            speedsWithIntervals.remove(1);
            Log.e(TAG, "Time Difference for speed 0 " + timeDiff);

            return;
        }

        long changeInSpeed = round((speeds.get(1) - speeds.get(0)) / timeDiff);
        Log.e(TAG, "changeInSpeed : " + changeInSpeed);

        if (currentTrip == null /*|| changeInSpeed > 25 || changeInSpeed < -30 || speed > 200*/) {
            Log.e(TAG, "current trip null running");
            speeds.clear();
            speedsWithIntervals.clear();
            return;
        }

        calculateEcoSpeed(speed);
        if (changeInSpeed <= harshDecelaration) {

            showNotification(harshBrakingDetected);

            String language = myPreferenece.getLang();
            textToSpeech.stop();

//            Translate translate = TranslateOptions.getDefaultInstance().getService();
//            Translation translation = translate.translate("Your English text here", Translate.TranslateOption.targetLanguage("hi"));
//            String translatedText = translation.getTranslatedText();


            switch (language) {
                case "hi":
                    textToSpeech.speak(HARSH_BRAKING_HINDI, TextToSpeech.QUEUE_FLUSH, null, null);
                    break;
                case "te":
                    textToSpeech.speak(HARSH_BRAKING_TELUGU, TextToSpeech.QUEUE_FLUSH, null, null);
                    break;
                case "mr":
                    textToSpeech.speak(HARSH_BRAKING_MARATHI, TextToSpeech.QUEUE_FLUSH, null, null);
                    break;
                case "kn":
                    textToSpeech.speak(HARSH_BRAKING_KANNADA, TextToSpeech.QUEUE_FLUSH, null, null);
                    break;

                case "ta":
                    textToSpeech.speak(HARSH_BRAKING_TAMIL, TextToSpeech.QUEUE_FLUSH, null, null);
                    break;

                default:
                    textToSpeech.speak(HARSH_BRAKING, TextToSpeech.QUEUE_FLUSH, null, null);
                    break;
            }


            Log.e(TAG, "Harsh deceleration");
            if (tempDecAlert == null) {
//                tempDecAlert = new TripAlert("Harsh Braking", changeInSpeed + " km/hr", TimeUtils.getTimeIndhms(timeDiff), changeInSpeed * 0.28 + " m/sec2", new Date().getTime(), endLocation.getLatitude(), endLocation.getLongitude(), "");
                tempDecAlert = new com.gypsee.sdk.database.TripAlert("Harsh Braking", changeInSpeed + " km/hr", TimeUtils.getTimeIndhms(timeDiff), changeInSpeed * 0.28 + " m/sec2", new Date().getTime(), endLocation.getLatitude(), endLocation.getLongitude(), "",currentTrip.getId());
                tempDecAlert.setInitialSpeed(speed);

            } else {
                if (TimeUtils.calcDiffTimeInSec(new Date(tempDecAlert.getTimeStamp()), new Date()) == 0)
                    return;
                int lateChangeInSpeed = (speed - tempDecAlert.getInitialSpeed()) / TimeUtils.calcDiffTimeInSec(new Date(tempDecAlert.getTimeStamp()), new Date());
                tempDecAlert.setTimeInterval(TimeUtils.calcDiffTime(tempDecAlert.getTimeStamp(), new Date().getTime()));
                tempDecAlert.setAlertValue(lateChangeInSpeed + " km/hr");
                tempDecAlert.setgForce(lateChangeInSpeed * 0.028 + " G");
            }


        } else if (changeInSpeed >= harshAccelaration) {

            Log.e(TAG, "Harsh Acceleration");
            showNotification(harshAccelerationDetected);

            String language = myPreferenece.getLang();

            textToSpeech.stop();

            switch (language) {
                case "hi":
                    textToSpeech.speak(HARSH_ACC_HINDI, TextToSpeech.QUEUE_FLUSH, null, null);
                    break;
                case "te":
                    textToSpeech.speak(HARSH_ACC_TELUGU, TextToSpeech.QUEUE_FLUSH, null, null);
                    break;
                case "mr":
                    textToSpeech.speak(HARSH_ACC_MARATHI, TextToSpeech.QUEUE_FLUSH, null, null);
                    break;
                case "kn":
                    textToSpeech.speak(HARSH_ACC_KANNADA, TextToSpeech.QUEUE_FLUSH, null, null);
                    break;
                case "ta":
                    textToSpeech.speak(HARSH_ACC_TAMIL, TextToSpeech.QUEUE_FLUSH, null, null);
                    break;
                default:
                    textToSpeech.speak(HARSH_ACC, TextToSpeech.QUEUE_FLUSH, null, null);
                    break;
            }


//            boolean harshAcc = sharedPreferences.getBoolean("switch3", true);
//
//            Log.e("harshAccSwitch", String.valueOf(harshAcc));
//            if (harshAcc){
//                textToSpeech.stop();
//                textToSpeech.speak(HARSH_ACC, TextToSpeech.QUEUE_FLUSH, null, null);
//            }

            if (temporaryAccAlert == null) {
//                temporaryAccAlert = new TripAlert("Harsh Accelaration", changeInSpeed + " km/hr", TimeUtils.getTimeIndhms(timeDiff), changeInSpeed * 0.028 + " m/sec2", new Date().getTime(), endLocation.getLatitude(), endLocation.getLongitude(), "");
                temporaryAccAlert = new com.gypsee.sdk.database.TripAlert("Harsh Accelaration", changeInSpeed + " km/hr", TimeUtils.getTimeIndhms(timeDiff), changeInSpeed * 0.028 + " m/sec2", new Date().getTime(), endLocation.getLatitude(), endLocation.getLongitude(),"",currentTrip.getId());
                temporaryAccAlert.setInitialSpeed(speed);
            } else {
                if (TimeUtils.calcDiffTimeInSec(new Date(temporaryAccAlert.getTimeStamp()), new Date()) == 0)
                    return;
                int lateChangeInSpeed = (speed - temporaryAccAlert.getInitialSpeed()) / TimeUtils.calcDiffTimeInSec(new Date(temporaryAccAlert.getTimeStamp()), new Date());
                temporaryAccAlert.setTimeInterval(TimeUtils.calcDiffTime(temporaryAccAlert.getTimeStamp(), new Date().getTime()));
                temporaryAccAlert.setAlertValue(lateChangeInSpeed + " km/hr");
                temporaryAccAlert.setgForce(lateChangeInSpeed * 0.028 + " G");
            }
        }

        if (changeInSpeed < harshAccelaration && changeInSpeed > harshDecelaration) {
            // It is normal breaking and normal Accelaration

            //Upload harsh Accelaration data
            if (temporaryAccAlert != null) {
                addTripAlertToArray(temporaryAccAlert);
                temporaryAccAlert = null;

                /*if (fragmentHomeBinding.alertsLayout.alerts.getBackground() != null)
                    fetchAlertsAndDisplay();*/
            }
            if (tempDecAlert != null) {
                //Upload harsh braking data
                addTripAlertToArray(tempDecAlert);
                tempDecAlert = null;
                /*if (fragmentHomeBinding.alertsLayout.alerts.getBackground() != null)
                    fetchAlertsAndDisplay();*/
            }
        }
        speeds.remove(0);
        speedsWithIntervals.remove(0);

        Log.e(TAG, "changeInSpeed : " + changeInSpeed + " Acceleralation force : " + (changeInSpeed * 0.028) + "m/sec2");

        if (speed > Integer.parseInt(maximumspeed.replace(" km/hr", ""))) {
            maximumspeed = speed + " km/hr";
            currentTrip.setSpeedMax(speed);
            sendMyBroadcast(3);

        }

        if (speed < maxSpeed && overSpeedAlert != null) {
            sendOverSpeedAlertToserver();
            /*if (fragmentHomeBinding.alertsLayout.alerts.getBackground() != null)
                fetchAlertsAndDisplay();*/
        }
        if (speed > maxSpeed) {

            if (overSpeedAlert == null) {
                showNotification("Over speed");

                String language = myPreferenece.getLang();
                textToSpeech.stop();


                switch (language) {
                    case "hi":
                        textToSpeech.speak(OVER_SPEED_HINDI, TextToSpeech.QUEUE_FLUSH, null, null);
                        break;
                    case "te":
                        textToSpeech.speak(OVER_SPEED_TELUGU, TextToSpeech.QUEUE_FLUSH, null, null);
                        break;
                    case "mr":
                        textToSpeech.speak(OVER_SPEED_MARATHI, TextToSpeech.QUEUE_FLUSH, null, null);
                        break;
                    case "kn":
                        textToSpeech.speak(OVER_SPEED_KANNADA, TextToSpeech.QUEUE_FLUSH, null, null);
                        break;
                    case "ta":
                        textToSpeech.speak(OVER_SPEED_TAMIL, TextToSpeech.QUEUE_FLUSH, null, null);
                        break;
                    default:
                        textToSpeech.speak(OVER_SPEED, TextToSpeech.QUEUE_FLUSH, null, "overSpeed");
                        break;
                }
                showNotification(overspeeding);
            }

//            overSpeedAlert = overSpeedAlert == null ? new TripAlert("Overspeed", speed + " km/hr", "", "", new Date().getTime(), endLocation.getLatitude(), endLocation.getLongitude(), "") : overSpeedAlert;
            overSpeedAlert = overSpeedAlert == null ? new com.gypsee.sdk.database.TripAlert("Overspeed", speed + " km/hr", "", "", new Date().getTime(), endLocation.getLatitude(), endLocation.getLongitude(), "",currentTrip.getId()) : overSpeedAlert;

            if (speed > Integer.parseInt(overSpeedAlert.getAlertValue().replace(" km/hr", ""))) {
                overSpeedAlert.setAlertValue(speed + " km/hr");
            }
            overSpeedAlert.setTimeInterval(TimeUtils.calcDiffTime(overSpeedAlert.getTimeStamp(), new Date().getTime()));

            if(TimeUtils.calcDiffTimeInSec(new Date(overSpeedAlert.getTimeStamp()), new Date())> Constants.overSpeed_alert_max_duration)
            {
                sendOverSpeedAlertToserver();
            }
            Log.e(TAG, "Speed ALert");
        }

        //checkAlertsCount(); display method

    }

    //Here we are sending overspeed alert to server
    private void sendOverSpeedAlertToserver() {
        addTripAlertToArray(overSpeedAlert);
        overSpeedAlert = null;
    }


    EcoSpeedEnums ecoSpeedEnum;
    private ArrayList<Integer> ecoSpeeds = new ArrayList<>();
    Date ecoSpeedStartTime;

    private void calculateEcoSpeed(int speed) {
        ecoSpeeds.add(speed);
        ecoSpeedStartTime = ecoSpeedStartTime == null ? new Date() : ecoSpeedStartTime;
        EcoSpeedEnums runningEnum = getEcoSpeedEnumFromSpeed(speed);

        if (ecoSpeedEnum == null) {
            ecoSpeedEnum = runningEnum;
            callEcoSpeedAssistant();
        }

//        if (ecoSpeedEnum != null && !runningEnum.name().equals(ecoSpeedEnum.name())) {
//            sendTheEcoSpeedAlert();
//            resetEcoSpeedAlerts();
//        }

        // Check the time difference for AboveEcoSpeed alert
        int timeDiff = TimeUtils.calcDiffTimeInSec(ecoSpeedStartTime, new Date());

        //  AboveEcoSpeed: send alert if time exceeds 30 seconds or if state changes
        if (ecoSpeedEnum == EcoSpeedEnums.AboveEcoSpeed) {
            if (!runningEnum.name().equals(ecoSpeedEnum.name()) || timeDiff > 30) {
                sendTheEcoSpeedAlert();
                resetEcoSpeedAlerts();
            }
        }
        // normal alerts: send alert only when the state changes
        else if (ecoSpeedEnum != null && !runningEnum.name().equals(ecoSpeedEnum.name())) {
            sendTheEcoSpeedAlert();
            resetEcoSpeedAlerts();
        }


    }

    private void callEcoSpeedAssistant() {
        if (ecoSpeedEnum == EcoSpeedEnums.EcoSpeed) {

            String language = myPreferenece.getLang();
            textToSpeech.stop();

            switch (language) {
                case "hi":
                    textToSpeech.speak(ECO_SPEED_HINDI, TextToSpeech.QUEUE_FLUSH, null, null);
                    break;
                case "te":
                    textToSpeech.speak(ECO_SPEED_TELUGU, TextToSpeech.QUEUE_FLUSH, null, null);
                    break;
                case "mr":
                    textToSpeech.speak(ECO_SPEED_MARATHI, TextToSpeech.QUEUE_FLUSH, null, null);
                    break;
                case "kn":
                    textToSpeech.speak(ECO_SPEED_KANNADA, TextToSpeech.QUEUE_FLUSH, null, null);
                    break;
                case "ta":
                    textToSpeech.speak(ECO_SPEED_TAMIL, TextToSpeech.QUEUE_FLUSH, null, null);
                    break;
                default:
                    textToSpeech.speak(ECO_SPEED, TextToSpeech.QUEUE_FLUSH, null, null);
                    break;
            }
            showNotification(fuelSaveMode);
        }


        if (ecoSpeedEnum == EcoSpeedEnums.AboveEcoSpeed) {

            String language = myPreferenece.getLang();
            textToSpeech.stop();

            switch (language) {
                case "hi":
                    textToSpeech.speak(ABOVE_ECO_SPEED_HINDI, TextToSpeech.QUEUE_FLUSH, null, null);
                    break;
                case "te":
                    textToSpeech.speak(ABOVE_ECO_SPEED_TELUGU, TextToSpeech.QUEUE_FLUSH, null, null);
                    break;
                case "mr":
                    textToSpeech.speak(ABOVE_ECO_SPEED_MARATHI, TextToSpeech.QUEUE_FLUSH, null, null);
                    break;
                case "kn":
                    textToSpeech.speak(ABOVE_ECO_SPEED_KANNADA, TextToSpeech.QUEUE_FLUSH, null, null);
                    break;
                case "ta":
                    textToSpeech.speak(ABOVE_ECO_SPEED_TAMIL, TextToSpeech.QUEUE_FLUSH, null, null);

                default:
                    textToSpeech.speak(ABOVE_ECO_SPEED, TextToSpeech.QUEUE_FLUSH, null, null);
                    break;
            }
            showNotification(fuelBurnMode);

        }
    }

    private void sendTheEcoSpeedAlert() {

        // Calculate the average using Java Streams
        int average = (int) (ecoSpeeds.stream()
                .mapToDouble(Integer::doubleValue)
                .average()
                .orElse(0.0));
        int timeDiff = TimeUtils.calcDiffTimeInSec(ecoSpeedStartTime, new Date());
//        TripAlert alert = new TripAlert(ecoSpeedEnum.name(), String.valueOf(average), TimeUtils.getTimeIndhms(timeDiff), "", new Date().getTime(), endLocation.getLatitude(), endLocation.getLongitude(), "");
        com.gypsee.sdk.database.TripAlert alert = new com.gypsee.sdk.database.TripAlert(ecoSpeedEnum.name(), String.valueOf(average), TimeUtils.getTimeIndhms(timeDiff), "", new Date().getTime(), endLocation.getLatitude(), endLocation.getLongitude(), "",currentTrip.getId());


        if (ecoSpeedEnum == EcoSpeedEnums.AboveEcoSpeed){
            addTripAlertToArray(alert);
        } else {
            addVehicleEcoSpeed(alert);
        }


    }

    private void resetEcoSpeedAlerts() {
        //CallServer. Then reset the values
        ecoSpeedEnum = null;
        ecoSpeedStartTime = null;
        ecoSpeeds.clear();
    }

    private EcoSpeedEnums getEcoSpeedEnumFromSpeed(int speed) {
        if (Math.min(selectedvehiclemodel.getEcoSpeedEndRange(), Math.max(selectedvehiclemodel.getEcoSpeedStartRange(), speed)) == speed) {
            return EcoSpeedEnums.EcoSpeed;
        } else if (speed > selectedvehiclemodel.getEcoSpeedEndRange()) {
            return EcoSpeedEnums.AboveEcoSpeed;
        }
        return EcoSpeedEnums.BelowEcoSpeed;
    }

    private com.gypsee.sdk.database.TripAlert overSpeedAlert, engineAlert;

    private float filter(final float prev, final float curr, final int ratio) {
        // If first time through, initialise digital filter with current values
        if (Float.isNaN(prev))
            return curr;
        // If current value is invalid, return previous filtered value
        if (Float.isNaN(curr))
            return prev;
        // Calculate new filtered value
        return (float) (curr / ratio + prev * (1.0 - 1.0 / ratio));
    }

    private int calculateGPSTimeInterval() {

        //This methd is used to calculate the time taken to move between two gps location points.
        int timeDiff;
        gpsWithIntervals.add(new Date());
        if (gpsWithIntervals.size() == 1) {
            return 0;
        }
        if (gpsWithIntervals.size() > 2) {
            List<Date> someArray = new ArrayList<>();
            someArray.add(gpsWithIntervals.get(0));
            someArray.add(new Date());
            gpsWithIntervals = someArray;
        }


        timeDiff = TimeUtils.calcDiffTimeInSec(gpsWithIntervals.get(0), gpsWithIntervals.get(1));
        Log.e(TAG, "GPS Time difference is : " + timeDiff);

        //If the time difference is less than 3 sec, we will not add the time interval.
        if (timeDiff < 3) {
            gpsWithIntervals.remove(1);
            return 0;
        } else {
            gpsWithIntervals.remove(0);

        }

        return timeDiff;
    }

    Location previousLocation = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyForegroundService", "onCreate called");
    }


    private StringBuilder logBuilder = new StringBuilder();

    public void addLog(String log) {
        Log.e(TAG, log);
        logBuilder.append(log).append("\n");
        saveLogsToPreferences();
        sendLogBroadcast();
    }

    private void sendLogBroadcast() {
        Intent intent = new Intent("LOG_UPDATED");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void saveLogsToPreferences() {

        //Clear logs
        if (logBuilder.length() > 5000) {
            clearLogs();
        }

        SharedPreferences sharedPreferences = getSharedPreferences(MyPreferenece.serviceLogs, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MyPreferenece.tripLogs, logBuilder.toString());
        editor.apply();
    }

    private void requestLocationUpdates(Location location) {



      /*  if (currentTrip != null){
            TripLatLong tripLatLong = new TripLatLong(currentTrip.getId(), location.getLatitude(), location.getLongitude(), location.getTime() + "");
            insertTripLatLongtoDb(tripLatLong);
        }*/

        if (currentTrip != null && (isServiceBound || inVehicleActivity || (location.getSpeed() > 10))) {

            //IF the spped is empty then we will calculate the seed
            if (commandResult.get("SPEED") == null || commandResult.get("SPEED").equals("NA") || commandResult.get("SPEED").equals("NODATA"))
                performGPSSpeedCalc(location);

            //Need to collect the gps params only for distance Calculation and storing the trip lat, long in the server& distance between then should exceed 100 meteres.

            int timeDifference = calculateGPSTimeInterval();

            if (timeDifference >= 3) {
//                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//                Log.e(TAG, "my time stamp");

//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//                String utcTimeStamp = sdf.format(new Date());
//

                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

//                Calendar calendar = Calendar.getInstance();
//                calendar.setTime(new Date());
//
//                // Add 5 hours and 30 minutes
//                calendar.add(Calendar.HOUR_OF_DAY, 5);
//                calendar.add(Calendar.MINUTE, 30);
//
//                // Format the new time
//                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());

                TripLatLong tripLatLong = new TripLatLong(currentTrip.getId(), location.getLatitude(), location.getLongitude(), timeStamp);
                if (previousLocation == null) {
                    previousLocation = location;
                } else {
                    double distance = DistanceCalculator.distance(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(previousLocation.getLatitude(), previousLocation.getLongitude()));


                    //Distance between 2 lat long should not exceed 500 meters
                    if (distance > 20 && distance <= 450) {
                        addLog("Added lat long distance: " + distance);
                        insertTripLatLongtoDb(tripLatLong);
//                        tripLatLongArrayList.add(tripLatLong);
                    }

                    previousLocation = location;

                }
            }
        }

    }


    private boolean checkSameElementIntriparray(TripLatLong tripLat) {
        for (TripLatLong tripLatLong :
                tripLatLongArrayList) {
            if (tripLat.getLatitude() == tripLatLong.getLatitude() && tripLat.getLongitude() == tripLatLong.getLongitude()) {
                return true;
            }
        }
        return false;
    }

    private void checkTripStarted() {

        if (selectedvehiclemodel != null) {
            Log.e(TAG, "check trip started vehicle" + selectedvehiclemodel.getVehicleModel());
        }


        Log.e(TAG, "Starting the trip");
        if (currentTrip == null /*&& selectedvehiclemodel != null*/) {
            //Setting user name and connected vehicle.
            commandResult.put("VEHICLE_ID", selectedvehiclemodel.getUserVehicleId());

            refreshRegisteredDevices();

            showNotification("We detected fast movement. So, we have started tracking your trip.");

            callServer(getString(R.string.tripstart_url), "Start trip ", 1);

        } else {
            Log.e(TAG, "Current trip not null 1966");

        }
    }

    private void doUnbindService() {
        sendMyBroadcast(2);
        if (isServiceBound) {
            if (service != null) {
                if (service.isRunning()) {
                    service.stopService();
                    //updateBluetoothImageStatus(false);
//                btStatusTextView.setText(getString(R.string.status_bluetooth_ok));
                }
            }
            Log.e(TAG, "Unbinding OBD service..");
            if (serviceConnEnabled) {
                unbindService(serviceConn);
                serviceConnEnabled = false;
            }
            isServiceBound = false;
            //isObdConnected = false;
            //fragmentHomeBinding.setOdbDrawable(getResources().getDrawable(R.drawable.obd_disconnected));

//            obdStatusTextView.setText(getString(R.string.status_obd_disconnected));
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(obdCommandBroadcastReceiver);

        }
    }

    Boolean needToEndTrip = false;

    //End trip if needed
    public void checkNeedToEndTrip() {

        Log.e(TAG, "checkNeedToEndTrip: ");
        if (needToEndTrip) {
            stopLiveData();
        }


    }

    public void distanceRecalculate() {

        if (networkCallback.wasDisconnected && currentTrip != null){
            callServer(getString(R.string.gpsTripDistance), "Upload GPS for trip calculation", 10);
            callServer(getResources().getString(R.string.distanceRecalculate).replace("{","").replace("}","").replace("tripId", currentTrip.getId()), "Distance Recalculate ", 25);
            networkCallback.wasDisconnected = false;
        }

        if (currentTrip != null){

            List<DrivingAlert> storedAlerts = tripDatabase.drivingAlertDao().getDrivingAlertsByTripId(currentTrip.getId());

            List<com.gypsee.sdk.database.TripAlert> unsyncedAlerts = tripDatabase.tripAlertDao().getTripAlertsByTripId(currentTrip.getId());

            if (!storedAlerts.isEmpty()){
                syncDataWithServer();
            }

            if (!unsyncedAlerts.isEmpty()){
                syncEcoAlertsDataWithServer();
            }

        }


    }



    private void stopLiveData() {
        needToEndTrip = true;

        if (networkCallback.isNetWorkAvailable) {
            needToEndTrip = false;
            Log.e(TAG, "Stopping live data..");
            //If vehicle health score is open, it will chnagethe bluetoothStatus
            Intent intent = new Intent("NewPerformance");
            // You can also include some extra data.
            intent.putExtra("BlueToothStatus", false);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            doUnbindService();
            endTrip();
        } else {
            sendMyBroadcast(6);
        }

    }

    private ArrayList<VehicleAlertModel> vehicleAlertModelArrayList = new ArrayList<>();


    private void endTrip() {
        if (!networkCallback.isNetWorkAvailable) {
            return;
        }


        myPreferenece.setIsTripRunning(false);

        //set initCommand flag
        myPreferenece.setInitApi(true);

        refreshRegisteredDevices();

        myPreferenece.setConnectedDeviceMac("");

        initJson = new JsonObject();

        sendMyBroadcast(2);

        if (currentTrip != null) {

            activityCaptured = false;
            inVehicleActivity = false;

            stopWidgetService();
            //stopFleetTracking();

            removeGeofence();


            //Below is call to upload empty OBD command values coming from OBD
            if (emptyValues.size() > 0) {
                callServer(getString(R.string.obdIssueUrl), "Upload Empty command details", 3);
            }


            checkAllDriveAlerts();
            if (currentTrip != null && drivingAlerts.size() > 0) {
                callServer(getString(R.string.addDrivingAlerturl).replace("vehicleId", selectedvehiclemodel.getUserVehicleId()), "Add driving alerts", 6);
            }
            if (vehicleAlertModelArrayList.size() > 0) {
                callServer(getString(R.string.vehicleAlertClearUrl), "Clear alert code", 4);
            }

            callServer(getString(R.string.vehicleAlertClearUrl), "Clear alert code", 4);


            callServer(getString(R.string.gpsTripDistance), "Upload GPS for trip calculation", 10);


            if (ecoSpeedEnum != null) {
                sendTheEcoSpeedAlert();
                resetEcoSpeedAlerts();
            }

            callServer(getResources().getString(R.string.tripEndurl), "end trip", 15);

            //Save the last connected vehicle.
            myPreferenece.saveStringData(MyPreferenece.lastConnectedVehicle, selectedvehiclemodel.getUserVehicleId());
            stopLocationService();
        }

    }

    private void stopLocationService() {
        //Un register & de-register location broadcast receiver.
        stopService(new Intent(getApplicationContext(), BackgroundLocationService.class));

    }


    private void checkAllDriveAlerts() {

        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        int i = 0;
        if (engineAlert != null) {
            addTripAlertToArray(engineAlert);
            engineAlert = null;
            i = i + 1;
        }
        if (overSpeedAlert != null) {
            addTripAlertToArray(overSpeedAlert);
            overSpeedAlert = null;
            i = i + 1;

        }

        if (temporaryAccAlert != null) {
            addTripAlertToArray(temporaryAccAlert);
            temporaryAccAlert = null;
            i = i + 1;

        }
        if (tempDecAlert != null) {
            addTripAlertToArray(tempDecAlert);
            tempDecAlert = null;
            i = i + 1;

        }

        /*if (fragmentHomeBinding.alertsLayout.alerts.getBackground() != null)
            fetchAlertsAndDisplay();*/
        currentTrip.setAlertCount(currentTrip.getAlertCount() + i);
        sendMyBroadcast(3);

    }


    private FleetSocketConnection fleetSocketConnection;

    private void startFleetTracking() {
        try {
            fleetSocketConnection = new FleetSocketConnection(myPreferenece.getUser(), selectedvehiclemodel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopFleetTracking() {
        if (fleetSocketConnection != null) {
            fleetSocketConnection.closeConnection();
            fleetSocketConnection = null;
        }
    }

    private void writeTempGPSToStorage() throws JSONException, IOException {
        if (currentTrip == null || tempTripLatLongArrayList.size() < 2) {
            return;
        }
        TempTripData tempTripData = TempTripData.getInstance(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tripID", currentTrip.getId());
        JSONArray dataArray = new JSONArray();
        for (TripLatLong latLongModel : tempTripLatLongArrayList) {
            JSONObject dataObj = new JSONObject();
            dataObj.put("latitude", latLongModel.getLatitude());
            dataObj.put("longitude", latLongModel.getLongitude());
            dataObj.put("createdOn", latLongModel.getTime());
            dataArray.put(dataObj);
        }
        jsonObject.put("data", dataArray);
        tempTripData.writeFile(jsonObject);
    }

    String localTripID;
    JSONArray localDataArray;

    private void readTempGPSFromStorage() throws IOException, JSONException {
        TempTripData tempTripData = TempTripData.getInstance(getApplicationContext());

        JSONObject jsonObject = tempTripData.readFile();
        if (!jsonObject.has("tripID") || !jsonObject.has("data")) {
            return;
        }

        localTripID = jsonObject.getString("tripID");
        localDataArray = jsonObject.getJSONArray("data");

        callServer(getString(R.string.gpsTripDistance), "Upload Stored GPS", 23);

    }

    private void deleteGPSStorageFile() {
        TempTripData tempTripData = TempTripData.getInstance(getApplicationContext());
        tempTripData.deleteFile();
    }

    private Timer internetAvailabilityTimer;

    private void startPushDataTimer() {

        internetAvailabilityTimer = new Timer();

        internetAvailabilityTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (checkInternet()) {
                    try {
                        readTempGPSFromStorage();
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, 1000, 300000); //5 minutes

    }


    private boolean checkInternet() {
        InternetAccessThread thread = new InternetAccessThread();
        try {
            thread.start();
            thread.join();
            return thread.isInternetAvailable();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }


    // to observe change in over speed limit
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(ConfigActivity.over_speed_preference)) {
//            maxSpeed = Integer.parseInt(sharedPreferences.getString(key, "90"));
        }
    }


}


