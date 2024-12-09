
package com.gypsee.sdk.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;

import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;

import com.gypsee.sdk.database.DatabaseHelper;
import com.gypsee.sdk.databinding.GypseeActivityMainBinding;
import com.gypsee.sdk.dialogs.ReferralCodeDialogFragment;
import com.gypsee.sdk.dialogs.SubscribeDialog;
import com.gypsee.sdk.fragments.ConnectedDeviceAdapter;
import com.gypsee.sdk.fragments.HomeFragment;


import com.gypsee.sdk.fragments.NewVehiclePerformanceFragment;
import com.gypsee.sdk.fragments.SettingsFragment;
import com.gypsee.sdk.fragments.WalletFragment;
import com.gypsee.sdk.helpers.BluetoothHelperClass;
import com.gypsee.sdk.jsonParser.ResponseFromServer;
import com.gypsee.sdk.models.AppversionModel;
import com.gypsee.sdk.models.BluetoothDeviceModel;
import com.gypsee.sdk.models.User;

import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;


import com.bumptech.glide.Glide;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.gypsee.sdk.services.ForegroundService;
import com.gypsee.sdk.utils.DialogHelper;
import com.gypsee.sdk.utils.GpsUtils;
import com.gypsee.sdk.utils.LocationUtils;
import com.gypsee.sdk.utils.NetworkUtil;
import com.gypsee.sdk.utils.Utils;
import okhttp3.ResponseBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gypsee.sdk.config.MyPreferenece.GYPSEE_PREFERENCES;
import static com.gypsee.sdk.utils.Constants.goToDashboard;
import static com.gypsee.sdk.utils.Constants.showSubscribeDialog;
import static com.gypsee.sdk.utils.Utils.getLine;

public class GypseeMainActivity extends AppCompatActivity implements GpsUtils.onGpsListener {

    private static final String TAG = GypseeMainActivity.class.getSimpleName();
    public MyPreferenece myPreferenece;

    public static GypseeActivityMainBinding activityMainBinding;
    public static final int GPSPERMISSION_REQUESTCODE = 10001;

    boolean isServiceTripStarted= false;

    private double latitude;
    private double longitude;

//    private float batteryLevel;

    public static int batteryPercent;

    public static boolean isCharging;


    boolean isPowerSaveMode = false;


    private BluetoothAdapter bluetoothAdapter;
    private BluetoothProfile.ServiceListener serviceListener;
    private boolean isConnected = false;
    private boolean isServiceConnected = false;

    private User user;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                //getConnectedDeviceName();
//                initDevice();
            }
        }
    };

//    public String getCurrentLocation() { // Assuming this is within a method
//        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//        // Check for location permissions (implement permission handling based on your app's needs)
//        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // Request permissions here
//            return "Location permission required."; // Or handle differently based on your needs
//        }
//
//        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//
//        if (location != null) {
//            latitude = location.getLatitude();
//            longitude = location.getLongitude();
//            return "Latitude: " + latitude + ", Longitude: " + longitude;
//        } else {
//            // Handle the case where no last known location is available
//            return "No recent location available."; // Or handle differently based on your needs
//        }
//    }

    public String getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager == null) {
            return "LocationManager not available.";
        }

        if (ActivityCompat.checkSelfPermission(GypseeMainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(GypseeMainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions here
            return "Location permission required.";
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            return "Latitude: " + latitude + ", Longitude: " + longitude;
        } else {
            return "No recent location available.";
        }
    }


    private void getBatteryInfo() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, intentFilter);

        // Extract battery level and charging state
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        batteryPercent = (int) ((level / (float) scale) * 100);

        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        isCharging = chargePlug == BatteryManager.BATTERY_PLUGGED_AC || chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
    }

    private void initializeBluetooth() {

        // Initialize BluetoothAdapter using BluetoothManager
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter == null) {
                // Device doesn't support Bluetooth or Bluetooth is disabled
                isConnected = false;
            }
        }

        // Listener to get connection status changes
        serviceListener = new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if (profile == BluetoothProfile.A2DP) {
                    List<BluetoothDevice> devices = proxy.getDevicesMatchingConnectionStates(new int[]{BluetoothProfile.STATE_CONNECTED});
                    isConnected = !devices.isEmpty();
                    isServiceConnected = true;
                    // Update UI or perform actions indicating that Bluetooth service is connected
                }
            }

            @Override
            public void onServiceDisconnected(int profile) {
                if (profile == BluetoothProfile.A2DP) {
                    isConnected = false;
                    isServiceConnected = false;
                    // Update UI or perform actions indicating that Bluetooth service is disconnected
                }
            }
        };

        // Register service listener
        if (bluetoothAdapter != null) {
            bluetoothAdapter.getProfileProxy(GypseeMainActivity.this, serviceListener, BluetoothProfile.A2DP);
        }
    }

    // Method to check Bluetooth connection status
    private boolean isConnectedToBluetooth() {
        return isConnected && isServiceConnected;
    }

    private ConnectedDeviceAdapter pairedAdapter;

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String KEY_HAS_EXECUTED = "hasExecuted";

    private SharedPreferences sharedPreferences;

    private boolean hasExecuted;

   public  ForegroundService foregroundService;

    private boolean isForegroundRunning = false;

    private final ServiceConnection foregroundServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            foregroundService = ((ForegroundService.ForegroundServiceBinder) service).getService();
            foregroundService.clearLogs();
            isForegroundRunning = true;
            foregroundService.setActivity(GypseeMainActivity.this);
            foregroundService.setContext(GypseeMainActivity.this);

            if(!isServiceTripStarted && foregroundService.currentTrip==null){
                unbindService(foregroundServiceConnection);
                foregroundService.stopSelf();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isForegroundRunning = false;

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.gypsee_activity_main);
        myPreferenece = new MyPreferenece(GYPSEE_PREFERENCES, this);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        hasExecuted = sharedPreferences.getBoolean(KEY_HAS_EXECUTED, false);

        user = myPreferenece.getUser();
        discoveredAdapter = new ArrayAdapter<>(GypseeMainActivity.this, R.layout.available_bluetooth_devices_layout, R.id.available_devices);
//        fetchbluetooth();
        registerBluettothBroadcastReceiver();

        initializeBluetooth();
        getCurrentLocation();
        getBatteryInfo();

        if (!hasExecuted) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    callUpdateDeviceServer(getResources().getString(R.string.mobileDeviceUpdateApi)
                                    .replace("{", "").replace("}", "").replace("userId", user.getUserId()),
                            "Upload device details", 1);
                }
            }, 500);

            // Update the flag so this code doesn't run again in the current session
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_HAS_EXECUTED, true);
            editor.apply();
        }

        // Get the current Locale
        Locale currentLocale = Locale.getDefault();

        // Get the language code (e.g., "en" for English, "fr" for French)
        String languageCode = currentLocale.getLanguage();

        // Optionally, get the country code (e.g., "US" for United States, "FR" for France)
        String countryCode = currentLocale.getCountry();

        // Get the display name of the language (e.g., "English", "Français")
        String displayLanguage = currentLocale.getDisplayLanguage();

        Log.e("Languages", " Language Code = " + languageCode + " Country code = " + countryCode + " Display Language = " + displayLanguage);

        myPreferenece.setLang(languageCode);


      checkServiceRunning();


        registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));

        replaceHomeFragment(false);
        initTopAppBar();
        initBottomnavigationView();
        registerBroadcastReceivers();
        myPreferenece.setIsTripRunning(false);
        myPreferenece.setIsConnecting(false);

        activityMainBinding.topBar.bluetoothIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDropdownMenu(view);
            }
        });




        //Here we will call server to update the FCM token
        callServerTogetAppversions(new JsonObject(), 0, "Get App version");
        // Here we wll update the Fcm to the server.

        //fcm token is not received in android versions after P due to battery permission
        //we will force refresh the token if
        if (myPreferenece.getStringData(MyPreferenece.FCM_TOKEN) == null) {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (task.isSuccessful()) {
                        Log.e(TAG, "fcm task success");
                        myPreferenece.saveStringData(MyPreferenece.FCM_TOKEN, task.getResult());
                    } else {
                        Log.e(TAG, "fcm task failed");
                    }

                }
            });
        }

        if (!myPreferenece.getSharedPreferences().getBoolean(MyPreferenece.isFCmTOkenSaved, false) &&
                myPreferenece.getStringData(MyPreferenece.FCM_TOKEN) != null) {
            Log.e(TAG, "my fcm: " + myPreferenece.getStringData(MyPreferenece.FCM_TOKEN));
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("fcmToken", myPreferenece.getStringData(MyPreferenece.FCM_TOKEN));
            callServerTogetAppversions(jsonObject, 1, "Update FCM token");
        }

        Log.e(TAG, "my new fcm: " + myPreferenece.getStringData(MyPreferenece.FCM_TOKEN));

        //Here we will check the gypsee contact number is stored or not?
        //checkGypseeContactStoredInmobile();



        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (getIntent().getBooleanExtra("isNewUser", false)) {
                                //Show pop up to add the referral code.
//                                showAddReferCodeDialog();

                                //Show pop up to subscribe.
                                showSubscribeNowDialog();
                            }
                        }
                    });
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
            }
        }, 7000);


    }

    public Boolean checkServiceRunning() {
        // Check if the service is running
        boolean isRunning =new  Utils().isServiceRunning(ForegroundService.class,this);

        Intent servicForeIntent = new Intent(this, ForegroundService.class);


        if (isRunning) {
            Log.d("MYNEWSERVICERS", "My back Service is running");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                bindService(servicForeIntent, foregroundServiceConnection, BIND_AUTO_CREATE);

            }
        } else {
            isServiceTripStarted=true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(servicForeIntent);
                bindService(servicForeIntent, foregroundServiceConnection, BIND_AUTO_CREATE);

            } else {
                startService(servicForeIntent);
            }
        }

        return true;
    }

    private void initTopAppBar() {
        User user = myPreferenece.getUser();

        // Log Firebase token
        Log.e(TAG, "Firebase token : " + myPreferenece.getStringData(MyPreferenece.FCM_TOKEN));

        user = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, getApplicationContext()).getUser();

        // Check if user is not null and has a valid image URL
        if (user != null && user.getUserImg() != null && user.getUserImg().contains("http")) {
            Glide
                    .with(getApplicationContext())
                    .load(user.getUserImg())
                    .placeholder(R.drawable.ic_profile)
                    .centerInside()
                    .into(activityMainBinding.topBar.profileImage);
        } else {
            Log.e(TAG, "User or user image is null");
            activityMainBinding.topBar.profileImage.setImageResource(R.drawable.ic_profile);
        }
        activityMainBinding.topBar.profileImgHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GypseeMainActivity.this, ProfileActivity.class));
            }
        });
    }





    private void replaceHomeFragment(boolean isNotAttachedToContext) {
        // We need to see if the activity is finishing or destroyed. If it is not finished or destroyed,
        // we will replace the fragment.
        if (!isFinishing() && !isDestroyed()) {
            HomeFragment homeFragment = HomeFragment.newInstance(isNotAttachedToContext);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainFrameLayout, homeFragment, HomeFragment.class.getSimpleName())
                    // Add the transaction to back stack (optional for back navigation)
                    .commit(); // Remove commitAllowingStateLoss
        }
    }


    private void showAddReferCodeDialog() {

        if (!isFinishing()) {
            ReferralCodeDialogFragment referralCodeDialogFragment = new ReferralCodeDialogFragment(GypseeMainActivity.this);
            referralCodeDialogFragment.show();

        }

    }

    private void showSubscribeNowDialog() {
        if (!isFinishing()) {
            SubscribeDialog subscribeDialog = new SubscribeDialog(this);
            subscribeDialog.setCancelable(true);
            subscribeDialog.setCanceledOnTouchOutside(true);
            subscribeDialog.show();

        }
    }


    private void rateApp(Context context) {
        String appPackageName = context.getPackageName();
// Rate App
        try {
            context
                    .startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri
                                    .parse("market://details?id="
                                            + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context
                    .startActivity(
                            new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id="
                                            + appPackageName)));
        }

    }

    private boolean isDeviceRooted() {
        String buildTags = Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private boolean isMotionActivityPermissionGranted() {
        int activityPermission = ActivityCompat.checkSelfPermission(GypseeMainActivity.this, Manifest.permission.ACTIVITY_RECOGNITION);
        return activityPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void callUpdateDeviceServer(String url, final String purpose, final int value) {

        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);
        Call<ResponseBody> call;
        JsonObject jsonObject = new JsonObject();

        switch (value) {

            default:
            case 1:
                boolean connected = isConnectedToBluetooth();
                int gpspermission = ActivityCompat.checkSelfPermission(GypseeMainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                switch (gpspermission) {
                    case PackageManager.PERMISSION_GRANTED:
                        Log.d("Permissions", "GPS permission: Granted");
                        // Check if GPS is enabled or not
                        break;
                    case PackageManager.PERMISSION_DENIED:
                        Log.d("Permissions", "GPS permission: Not Granted");
                        break;
                }

                String gpsPermissionStatus;
                switch (gpspermission) {
                    case PackageManager.PERMISSION_GRANTED:
                        gpsPermissionStatus = "always";
                        break;
                    case PackageManager.PERMISSION_DENIED:
                        gpsPermissionStatus = "denied";
                        break;
                    default:
                        gpsPermissionStatus = "unknown";
                }

                int activitypermission = ActivityCompat.checkSelfPermission(GypseeMainActivity.this, Manifest.permission.ACTIVITY_RECOGNITION);
                switch (activitypermission) {
                    case PackageManager.PERMISSION_GRANTED:
                        Log.d("Permissions", "Motion Activity permission: Granted");
                        // You have the permission, you can now use motion activity APIs
                        break;
                    case PackageManager.PERMISSION_DENIED:
                        Log.d("Permissions", "Motion Activity permission: Not Granted");
                        break;
                }
                String motionActivityPermissionStatus;
                switch (activitypermission) {
                    case PackageManager.PERMISSION_GRANTED:
                        motionActivityPermissionStatus = "granted";
                        break;
                    case PackageManager.PERMISSION_DENIED:
                        motionActivityPermissionStatus = "denied";
                        break;
                    default:
                        motionActivityPermissionStatus = "unknown";
                }


                PackageInfo pInfo = null;

                try {
                    pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    throw new RuntimeException(e);
                }

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                // Check Wi-Fi
                boolean isWifiEnabled = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();


                // Get the Android version (API level)
                String sdkVersion = String.valueOf(Build.VERSION.SDK_INT);


                boolean isMotionActivityPermissionGranted = isMotionActivityPermissionGranted();


                boolean isLowPrecisionLocationEnabled = LocationUtils.isLowPrecisionLocationEnabled(GypseeMainActivity.this);

                if (isLowPrecisionLocationEnabled) {
                    // Low precision location is enabled
                    Log.d("LocationCheck", "Low precision location is enabled");
                } else {
                    // High accuracy location is enabled
                    Log.d("LocationCheck", "High accuracy location is enabled");
                }

                boolean isConnectedToInternet = NetworkUtil.isInternetConnected(GypseeMainActivity.this);


                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date());
                String deviceModel = Build.MODEL;
                String androidVersion = Build.VERSION.RELEASE;
                boolean isRooted = isDeviceRooted();
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                jsonObject.addProperty("appVersion", pInfo.versionName);
                jsonObject.addProperty("batteryCharging", isCharging);
                jsonObject.addProperty("batteryLevel", batteryPercent);
                jsonObject.addProperty("bluetoothStatus", connected);
                jsonObject.addProperty("deviceModel", deviceModel);
                jsonObject.addProperty("deviceRooted", isRooted);
                jsonObject.addProperty("gpsEnabled", isGPSEnabled);
                jsonObject.addProperty("gpsPermissionGrantType", gpsPermissionStatus);
                jsonObject.addProperty("heartBeatDate", timeStamp);
                jsonObject.addProperty("inLowPowerMode", isPowerSaveMode);
                jsonObject.addProperty("jailBreak", false);
                jsonObject.addProperty("latitude", latitude);
                jsonObject.addProperty("longitude", longitude);
                jsonObject.addProperty("lowPreciseLocation", isLowPrecisionLocationEnabled);
                jsonObject.addProperty("motionActivityGranted", isMotionActivityPermissionGranted);
                jsonObject.addProperty("motionActivityPermissionGrantType", motionActivityPermissionStatus);
                jsonObject.addProperty("osVersion", "Android Version " + androidVersion);
                jsonObject.addProperty("sdkVersion", sdkVersion);
                jsonObject.addProperty("wifiEnabled", isWifiEnabled);
                jsonObject.addProperty("internet", isConnectedToInternet);


                call = apiService.uploadDeviceDetails(new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, GypseeMainActivity.this).getUser().getUserAccessToken(), jsonObject);
                break;
        }

        Log.e("TAG", purpose + " Input :" + jsonObject);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {

                    if (response.isSuccessful()) {
                        Log.e(TAG, "Response is success");

                        //If the response is null, we will return immediately.
                        ResponseBody responseBody = response.body();
                        if (responseBody == null)
                            return;
                        String responseStr = responseBody.string();
                        Log.e(TAG, purpose + " Response :" + responseStr);

                        switch (value) {

                            default:
                            case 1:

//                                Log.e(TAG, purpose + " Device details uploaded successfully");
//                                Toast.makeText(context, "Device Details Updated", Toast.LENGTH_SHORT).show();
//                                getActivity().finish();
                                break;

                        }
                    } else {
                        Log.e(TAG, purpose + " Response is not succesfull");

                        String errResponse = response.errorBody().string();
                        Log.e("Error code 400", errResponse);
                        Log.e(TAG, purpose + "Response is : " + errResponse);
                        int responseCode = response.code();
                        if (responseCode == 401 || responseCode == 403) {
                            //include logout functionality
                            Utils.clearAllData(GypseeMainActivity.this);
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "error here since request failed");
                if (t.getMessage().contains("Unable to resolve host")) {
                    Toast.makeText(GypseeMainActivity.this, "Please Check your internet connection", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("Onstop", "Onstop Called");
        // Reset the flag when the app is stopped (backgrounded or closed)

        //THis was causing leaked window, so fixed the crash
        if(popupWindow!=null &&popupWindow.isShowing()){

            popupWindow.dismiss();
        }
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_HAS_EXECUTED, false);
        editor.apply();
    }

    private void callServerTogetAppversions(JsonObject jsonObject, final int value, final String purpose) {
        ApiInterface apiService = ApiClient.getRetrofitInstance(GypseeMainActivity.this).create(ApiInterface.class);
        User user = myPreferenece.getUser();
        Call<ResponseBody> call;
        Log.e(TAG, purpose + " input : " + jsonObject.toString());
        switch (value) {

            case 0:
                Log.e(TAG, "getting app version at line: " + getLine());
                call = apiService.getAppVersion(user.getUserAccessToken());
                break;


            default:
                Log.e(TAG, "updating FCM token at line: " + getLine() + "\n user FCM token: " + user.getFcmToken() + "\n User Access Token: " + user.getUserAccessToken() + "\n User id: " + user.getUserId());
                call = apiService.updateFcmToken(user.getUserAccessToken(), jsonObject, user.getUserId());
                break;

        }

        Log.e(TAG, purpose + " Url " + call.request().url());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (response.isSuccessful()) {
                        Log.e(TAG, " Response is success");

                        String responseStr = response.body().string();
                        Log.e(TAG, purpose + " Response :" + responseStr);

                        switch (value) {

                            case 0:
                                //showHideViews(View.GONE);
                                parseCheckAppversionResponse(responseStr);
                                break;

                            case 1:
                                myPreferenece.saveBoolean(MyPreferenece.isFCmTOkenSaved, true);
                                break;

                        }

                    } else {
                        Log.e(TAG, purpose + " Response is not succesfull");

                        String errorResponse = response.errorBody().toString();
                        Log.e(TAG + " Error code 400 rm", response.message());
                        Log.e(TAG + " Error code 400 er", errorResponse);
                        Log.e(TAG + " Error code 400 json", jsonObject.toString());

                        int responseCode = response.code();
                        if (responseCode == 401 || responseCode == 403) {
                            //include logout functionality
                            Utils.clearAllData(GypseeMainActivity.this);
                            return;
                        } else {
                            // we need to show message to the user. response.errorBody()
                            //{"status":400,"message":"Token Expired","totalRecords":0}

                            JSONObject errorjson = new JSONObject(errorResponse);
                            if (errorjson.has("message"))
                                Toast.makeText(GypseeMainActivity.this, errorjson.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Error message : " + t.getMessage());
                //Logger.ErrorLog(TAG, "error here since request failed");
                if (t instanceof UnknownHostException) {
                    Toast.makeText(GypseeMainActivity.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (t instanceof ConnectException) {
                    Toast.makeText(GypseeMainActivity.this, "Server Down", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(GypseeMainActivity.this, "An unknow error occurered. Contact gypsee team", Toast.LENGTH_LONG).show();


                }
            }
        });
    }


    Boolean forceUpdate = false;

    private void parseCheckAppversionResponse(String responseStr) throws Exception {

        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        long version = pInfo.versionCode;

        Log.e(TAG, "Current app version : " + version);
        ArrayList<AppversionModel> appversionModelArrayList = new ArrayList<>();
        JSONObject mainJson = new JSONObject(responseStr);

        JSONArray jsonArray = mainJson.getJSONArray("appVersions");

        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String[] keys = {"appVersion", "versionId", "deleteData", "logoutUser",
                    "active", "lastUpdatedOn", "forceUpdate", "isActive", "createdOn", "latest", "versionCode"};
            ArrayList<String> values = new ArrayList<>();
            for (String key : keys) {
                String value = jsonObject.has(key) ? jsonObject.getString(key) : "";
                values.add(value);
            }
            AppversionModel appversionModel = new AppversionModel(values.get(0), values.get(1), values.get(2), values.get(3), values.get(4), values.get(5),
                    values.get(6), values.get(7), values.get(8), values.get(9), values.get(10));

            appversionModelArrayList.add(appversionModel);
        }

        AppversionModel appversionModel = appversionModelArrayList.get(0);


        boolean isGetLatestVersion = appversionModel.getLatest().equals("true")
                && version < Integer.parseInt(appversionModel.getVersionCode());


        forceUpdate = appversionModel.getForceUpdate().equals("true") & isGetLatestVersion;

        Log.e(TAG, "isGetLatestVersion :" + isGetLatestVersion + " forceUpdate: " + forceUpdate);


        if (appversionModelArrayList.get(0).getLogoutUser().equals("true")) {
            // condition is true Logout user after everytime app opens
            Toast.makeText(GypseeMainActivity.this, "Dear user, your sesssion has been expired.", Toast.LENGTH_LONG).show();

            new MyPreferenece(GYPSEE_PREFERENCES, this).getSharedPreferences().edit().clear().apply();
            PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();

            gotoLoginActivity();

        } else if (isGetLatestVersion) {
            //Force update user.
            showPopuptoUpdateApp();
        } else if (appversionModelArrayList.get(0).getDeleteData().equals("true")) {

            //Do not give user to access the app
            // showContactCustomerCardDialog();

        } else {
            // gotHomePage();
        }

    }

    private void gotoLoginActivity() {
        Intent in = new Intent(this, LoginRegisterActivity.class);
        startActivity(in);
        finishAffinity();
    }

    private void showPopuptoUpdateApp() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setCancelable(false)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        rateApp(GypseeMainActivity.this);
                    }
                });


        if (forceUpdate) {

        } else {
            builder

                    .setNegativeButton("No, Thanks", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // gotHomePage();
                        }
                    });
        }

        DialogHelper.showPopuptoUpdateApp(this, "New version of app is available on playstore .Please update App.", "Alert!", builder);
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "Back stack count : " + getSupportFragmentManager().getBackStackEntryCount());
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {

            if (backPressedOnce) {
                super.onBackPressed();
            } else {
                backPressedOnce = true;
                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backPressedOnce = false;
                    }
                }, 2000);
            }
        } else if (activityMainBinding.bottomNavigationView.getSelectedItemId() == R.id.nav_drive) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.popBackStack();

        } else {
            activityMainBinding.bottomNavigationView.setSelectedItemId(R.id.nav_drive);
        }

    }

    boolean backPressedOnce = false;



    private void initBottomnavigationView() {


        activityMainBinding.bottomNavigationView.setOnItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {


            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.nav_drive) {
                    removeAddedFragments();
                }
            }
        });

        activityMainBinding.bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                final int previousItem = activityMainBinding.bottomNavigationView.getSelectedItemId();
                int id = menuItem.getItemId();


                if (previousItem != id) {
                    if (id == R.id.navigate_icon) {
                        NewVehiclePerformanceFragment performanceFragment = NewVehiclePerformanceFragment.newInstance();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.mainFrameLayout, performanceFragment, NewVehiclePerformanceFragment.class.getSimpleName())
                                .addToBackStack(NewVehiclePerformanceFragment.class.getSimpleName())
                                .commit();
                    } else if (id == R.id.nav_drive) {
                        removeAddedFragments();
                    } else if (id == R.id.navigation_scancar) {
                        WalletFragment walletFragment = WalletFragment.newInstance();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.mainFrameLayout, walletFragment, WalletFragment.class.getSimpleName())
                                .addToBackStack(WalletFragment.class.getSimpleName())
                                .commit();
                    } else if (id == R.id.navigation_assistance) {
                        SettingsFragment settingsFragment = SettingsFragment.newInstance();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.mainFrameLayout, settingsFragment, SettingsFragment.class.getSimpleName())
                                .addToBackStack(SettingsFragment.class.getSimpleName())
                                .commit();
                    }
                }
                return true;
            }
        });
    }

    private void removeAddedFragments() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "OnActivity result" + requestCode);

        if (requestCode == GypseeMainActivity.GPSPERMISSION_REQUESTCODE) {
            Log.e(TAG, "GPSPERMISSION_REQUESTCODE");
            if (resultCode == RESULT_OK) {
                //homeFragment.checkGPSEnabledOrnotInit();
            } else {

                showAlertDialogforGPS();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "On Restart called");
    }


    private void checkGPSEnable() {
        GpsUtils gpsUtils = new GpsUtils(GypseeMainActivity.this);
        gpsUtils.turnGPSOn(this);
//        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(ConfigActivity.ENABLE_GPS_KEY, true).apply();
    }

    @Override
    public void gpsStatus(boolean isGPSEnable) {
    }


    @Override
    protected void onResume() {
        super.onResume();
        fetchbluetooth();
        activityMainBinding.bottomNavigationView.setSelectedItemId(R.id.nav_drive);
        String extras = getIntent().getStringExtra("action");
//        Log.e("147852action string", getIntent().getAction()==null?"null":getIntent().getAction());
        if (extras != null) {
//            Log.e("147852", extras);
            if (extras.equals(goToDashboard)) {
                Bundle b = new Bundle();
                b.putString("action", goToDashboard);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                HomeFragment homeFragment = new HomeFragment();
                homeFragment.setArguments(b);
                ft.add(R.id.mainFrameLayout, homeFragment).commit();
            } else if (extras.equals(showSubscribeDialog)) {
                Bundle b = new Bundle();
                b.putString("action", showSubscribeDialog);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                HomeFragment homeFragment = new HomeFragment();
                homeFragment.setArguments(b);
                ft.add(R.id.mainFrameLayout, homeFragment).commit();
            }
        }
    }


    private void showAlertDialogforGPS() {
        new androidx.appcompat.app.AlertDialog.Builder(GypseeMainActivity.this)
                .setTitle("Location")
                .setMessage("Dear User, \n We request you to switch on the GPS for the better experience. We will give analytics based on the GPS.")
                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        checkGPSEnable();
                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setCancelable(false)
                .show();
    }

    private void registerBroadcastReceivers() {

        //THis is to receive the obd commands from the device . Regarding the vehcile

        IntentFilter filter = new IntentFilter();
        filter.addAction("replaceHomeFragment");

        //this is for receiving the notification count etc
        LocalBroadcastManager.getInstance(GypseeMainActivity.this).registerReceiver(
                broadcastReceiver, filter);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("replaceHomeFragment")) {
                replaceHomeFragment(true);
            }
        }
    };




    ArrayAdapter<String> discoveredAdapter;
    ListView pairedListView;
    PopupWindow popupWindow;
    private void showDropdownMenu(View anchorView) {


        // Create a PopupWindow with a custom layout
        popupWindow  = new PopupWindow(anchorView.getContext());
        View dropdownView = LayoutInflater.from(anchorView.getContext()).inflate(R.layout.bluetooth_dropdown_menu, null);

        // Initialize paired and discovered devices lists
        pairedListView = dropdownView.findViewById(R.id.paired_devices);
        ListView discoveredListView = dropdownView.findViewById(R.id.discovered_devices);
        fetchbluetooth();

        if (isForegroundRunning) {
            registeredDevices = foregroundService.fetchRegisteredDevices();
            registeredDevices = foregroundService.getConnectedRegisteredDevices(registeredDevices);

        } else {
            Log.e("BluetoothError", "Foreground service is not connected");
        }

        // Adapter for paired devices list
        ConnectedDeviceAdapter pairedAdapter = new ConnectedDeviceAdapter(this, registeredDevices);

        // Fetch Bluetooth devices before setting the adapter

        pairedListView.setAdapter(pairedAdapter);

        discoveredListView.setAdapter(discoveredAdapter);

        // Set item click listener for discovered devices list
        discoveredListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                connectToDevice = devicesList.get(position);
                Log.e(TAG, "onItemClick: " + connectToDevice.getName());

                BluetoothHelperClass.showOkCancelDialog(GypseeMainActivity.this, getLayoutInflater(), "Alert!", "Are you Pairing Car based Bluetooth device?", responseFromServer, 1);
                // Mark the selected device as connected
                markDeviceAsConnected(connectToDevice.getAddress());
                popupWindow.dismiss();
            }
        });

        // Set the PopupWindow dimensions
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        // Set the PopupWindow content and show it at the specified location
        popupWindow.setContentView(dropdownView);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(anchorView);
    }


    // Add this method in your activity or fragment
//    private void markDeviceAsConnected(String macAddress) {
//        for (BluetoothDeviceModel device : currentConnectedDevice) {
//            if (device.getMacAddress().equals(macAddress)) {
//                device.setNowConnected(true);
//            } else {
//                device.setNowConnected(false);
//            }
//        }
//        // Notify the adapter of the data change
//        currentDeviceAdapter.notifyDataSetChanged();
//    }

//    private void initDevice(){
//        // Adapter for paired devices list
//        ConnectedDeviceAdapter pairedAdapter = new ConnectedDeviceAdapter(this, currentConnectedDevice);
//        fetchbluetooth(); // Call fetchBluetooth() after initializing the adapter
//
//        pairedListView.setAdapter(pairedAdapter);
//    }

    private void markDeviceAsConnected(String macAddress) {
        for (BluetoothDeviceModel device : registeredDevices) {
            device.setNowConnected(device.getMacAddress().equals(macAddress));
        }
        // Notify the adapter of the data change
//        currentDeviceAdapter.notifyDataSetChanged();
    }


    ResponseFromServer responseFromServer = new ResponseFromServer() {
        @Override
        public void responseFromServer(String Response, String className, int value) {

            switch (value) {
                case 1:
                    if (connectToDevice != null) {
                        if (custom) {
                            BluetoothHelperClass.showInputDialog(GypseeMainActivity.this, getLayoutInflater(), "Enter Name", "Please enter name for custom device", responseFromServer, 3);
                        } else {
                            connectToDevice.createBond();
                        }

                    }
                    break;

                case -1:
                    connectToDevice = null;
                    customDeviceName = "";
                    break;

                case 2:
                    if (connectToDevice != null) {
                        if (custom) {
                            BluetoothHelperClass.showInputDialog(getApplicationContext(), getLayoutInflater(), "Enter Name", "Please enter name for custom device", responseFromServer, 4);
                        } else {
                            isNowConnected = false;
//                            callServer(getString(R.string.registerNewDevice), "Register Bluetooth Device", 1);
                        }

                    }
                    break;

                case 3:
                    if (connectToDevice != null) {

                        customDeviceName = Response;
                        if (connectToDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                            connectedDevice = connectToDevice;
                            isNowConnected = true;
                            callBluetoothServer(getString(R.string.registerNewDevice), "Register Bluetooth Device", 1);

                        } else {
                            connectToDevice.createBond();

                        }
                    }
                    break;

//                case 4:
//                    if (connectToDevice != null){
//                        isNowConnected = false;
//                        customDeviceName = Response;
//                        callServer(getString(R.string.registerNewDevice), "Register Bluetooth Device", 1);
//                    }
//                    break;

            }

        }
    };



    ArrayList<BluetoothDevice> pairedList = new ArrayList<>();
    ArrayList<BluetoothDeviceModel> registeredDevices = new ArrayList<>();


    ArrayList<BluetoothDevice> devicesList = new ArrayList<>();

    private void fetchbluetooth() {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //checkBluetoothEnabled();
        bluetoothAdapter.enable();


        registerBluetoothReceiver();
        startBluetoothScanning();



//        initToolbar();
//        pairedListAdapter = new MyDevicesAdapter(pairedList);
//        pairedListAdapter = new BluetoothListAdapter(pairedList);

//        currentDeviceAdapter = new ConnectedDeviceAdapter(this,currentConnectedDevice);

//        fragmentSearchBluetoothDeviceBinding.pairedListRecyclerview.setLayoutManager(new LinearLayoutManager(context));
//        fragmentSearchBluetoothDeviceBinding.pairedListRecyclerview.setAdapter(pairedListAdapter);


//        fragmentSearchBluetoothDeviceBinding.bluetoothListRecyclerview.setLayoutManager(new LinearLayoutManager(context));
//        fragmentSearchBluetoothDeviceBinding.bluetoothListRecyclerview.setAdapter(bluetoothListAdapter);
//        setOnclickListeners();

        initPairedList();
//        initConnectedDevicesViews();

    }

    private void initPairedList() {
        pairedList.clear();
        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        pairedList.addAll(pairedDevices);
//        pairedListAdapter.notifyDataSetChanged();

//        if (pairedList.size() > 0){
//            showView(fragmentSearchBluetoothDeviceBinding.listLayout);
//        }


    }

    private void registerBluettothBroadcastReceiver() {
        IntentFilter newIntent = new IntentFilter();
        newIntent.addAction("BluetoothConnectionChange");
        newIntent.addAction("BluetoothDisconnected");
        LocalBroadcastManager.getInstance(this).registerReceiver(bluetoothStatus, newIntent);
    }

    private final BroadcastReceiver bluetoothStatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("BluetoothConnectionChange") || intent.getAction().equals("BluetoothDisconnected")) {
                new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        initDevice();
//                        initViews();
                    }
                }, 1000);
            }
        }
    };


//    private void initConnectedDevicesViews(){
//
//        currentConnectedDevice.clear();
////        myDevicesAdapter.notifyDataSetChanged();
//
//        BluetoothDeviceModel model1 = new BluetoothDeviceModel(
//                "1",
//                "Gypsee Smart Charger",
//                "Charger",
//                "Auto 12",
//                "AUDIO_VIDEO",
//                "AUDIO_VIDEO",
//                "00:11:22:AA:BB:CC",
//                "https://firebasestorage.googleapis.com/v0/b/gypsee-6ee24.appspot.com/o/Car%20Charger-01-1.png?alt=media&token=a476d053-e771-439a-bb58-d172462ec199",
//                "2021-07-21 15:28:59",
//                "2021-07-21 15:26:15",
//                "2021-07-21 15:26:15",
//                false,
//                null
//        );
//
//        BluetoothDeviceModel model2 = new BluetoothDeviceModel(
//                "1",
//                "OBD-II",
//                "OBD",
//                "OBD-II",
//                "AUDIO_VIDEO",
//                "AUDIO_VIDEO",
//                "00:11:22:AA:BB:CC",
//                "https://firebasestorage.googleapis.com/v0/b/gypsee-6ee24.appspot.com/o/Gypsee_OBD-01.png?alt=media&token=8ece19fb-f48d-45c3-84f6-33092e775c81",
//                "2021-07-21 15:28:59",
//                "2021-07-21 15:26:15",
//                "2021-07-21 15:26:15",
//                true,
//                null
//        );
//
////        currentConnectedDevice.add(model1);
////        currentConnectedDevice.add(model2);
//
//        currentConnectedDevice.addAll(new DatabaseHelper(this).fetchRegisteredDevices());
//
//
//
//    }


    private boolean isNowConnected = false;

    private String deviceId = "";
    private final Boolean custom = true;

    private BluetoothDevice connectToDevice;
    private String customDeviceName = "";


    private void callBluetoothServer(String url, final String purpose, final int value) {

        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);
        Call<ResponseBody> call;
        JsonObject jsonObject = new JsonObject();

        switch (value) {

            default:
            case 1:
                //register device
                if (connectToDevice != null && deviceId != null) {

                    deviceId = new DatabaseHelper(GypseeMainActivity.this).fetchCategoryDevices().get(0).getId();

                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date());

                    jsonObject.addProperty("bluetoothName", (custom && !customDeviceName.isEmpty()) ? customDeviceName : connectToDevice.getName());
                    jsonObject.addProperty("bluetoothProfilesSupported", getBluetoothClassName(connectToDevice)); //need more research
                    jsonObject.addProperty("bluetoothType", getBluetoothClassName(connectToDevice));
                    jsonObject.addProperty("deviceId", deviceId);
                    jsonObject.addProperty("lastConnectedTime", timeStamp);
                    jsonObject.addProperty("macAddress", connectToDevice.getAddress());
                    jsonObject.addProperty("nowConnected", isNowConnected);
                    if (custom) {
                        jsonObject.addProperty("deviceName", (!customDeviceName.isEmpty()) ? customDeviceName : connectToDevice.getName());
                    }
                }
                call = apiService.uploadObd(new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, this).getUser().getUserAccessToken(), jsonObject);
                break;

            case 2:
                call = apiService.getObdDevice(user.getUserAccessToken());
                break;

        }

        Log.e(TAG, purpose + " Input :" + jsonObject);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {

                    if (response.isSuccessful()) {
                        Log.e(TAG, "Response is success");

                        //If the response is null, we will return immediately.
                        ResponseBody responseBody = response.body();
                        if (responseBody == null)
                            return;
                        String responseStr = responseBody.string();
                        Log.e(TAG, purpose + " Response :" + responseStr);

                        switch (value) {

                            case 1:
                                Toast.makeText(getApplicationContext(), "Device added", Toast.LENGTH_SHORT).show();
                                callBluetoothServer(getString(R.string.getUserRegisteredDevices).replace("userId", user.getUserId()), "Fetch Registered Devices", 2);
                                break;
                            case 2:
                                parseFetchRegisteredDevices(responseStr);
                                break;

                        }
                    } else {
                        Log.e(TAG, purpose + " Response is not succesfull");

                        String errResponse = response.errorBody().string();
                        Log.e("Error code 400", errResponse);
                        Log.e(TAG, purpose + "Response is : " + errResponse);
                        int responseCode = response.code();
                        if (responseCode == 401 || responseCode == 403) {
                            //include logout functionality
                            Utils.clearAllData(getApplicationContext());
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "error here since request failed");
                if (t.getMessage().contains("Unable to resolve host")) {
                    Toast.makeText(getApplicationContext(), "Please Check your internet connection", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private void parseFetchRegisteredDevices(String response) throws JSONException {
        JSONObject jsonResponse = new JSONObject(response);

        JSONArray userRegisteredDevices = jsonResponse.getJSONArray("userRegisteredDevices");
        ArrayList<BluetoothDeviceModel> deviceModelArrayList = new ArrayList<>();

        new DatabaseHelper(this).deleteTable(DatabaseHelper.REGISTERED_DEVICE_TABLE);

        for (int i=0; i<userRegisteredDevices.length(); i++){

            JSONObject registeredDevice = userRegisteredDevices.getJSONObject(i);
            Gson gson = new Gson();
            BluetoothDeviceModel bluetoothDeviceModel = gson.fromJson(registeredDevice.toString(), BluetoothDeviceModel.class);

            deviceModelArrayList.add(bluetoothDeviceModel);
        }

        new DatabaseHelper(this).insertRegisteredDevices(deviceModelArrayList);
        //Checking the devices

        if (isForegroundRunning) {
            registeredDevices = foregroundService.fetchRegisteredDevices();
            registeredDevices = foregroundService.getConnectedRegisteredDevices(registeredDevices);

        } else {
            Log.e("BluetoothError", "Foreground service is not connected");
        }


//        registeredDevices = foregroundService.fetchRegisteredDevices();
//        registeredDevices = foregroundService.getConnectedRegisteredDevices(registeredDevices);

    }



    private String getBluetoothClassName(BluetoothDevice device) {
        int i = device.getBluetoothClass().getMajorDeviceClass();
        String deviceClass = "";
        switch (i) {
            case 1024:
                deviceClass = "AUDIO_VIDEO";
                break;

            case 256:
                deviceClass = "COMPUTER";
                break;

            case 2304:
                deviceClass = "HEALTH";
                break;

            case 1536:
                deviceClass = "IMAGING";
                break;

            case 0:
                deviceClass = "MISC";
                break;

            case 768:
                deviceClass = "NETWORKING";
                break;

            case 1280:
                deviceClass = "PERIPHERAL";
                break;

            case 512:
                deviceClass = "PHONE";
                break;

            case 2048:
                deviceClass = "TOY";
                break;

            case 7936:
                deviceClass = "UNCATEGORIZED";
                break;

            case 1792:
                deviceClass = "WEARABLE";
                break;

        }
        return deviceClass;
    }


//    private void startBluetoothScanning(){
//        devicesList.clear();
//        bluetoothAdapter.startDiscovery();
//        //showView(fragmentSearchBluetoothDeviceBinding.searchLayout);
//
//    }

    private void startBluetoothScanning() {
        // Clear the list of discovered devices
        //devicesList.clear();

        // Check if Bluetooth discovery is already in progress
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        // Start Bluetooth discovery
        bluetoothAdapter.startDiscovery();
    }


    private boolean isDiscovering = false;
    private BluetoothDevice connectedDevice = null;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!checkIfDeviceAlreadyAdded(device) && device.getName() != null) {
                    // Log the name and address of the discovered device
                    Log.d(TAG, "Discovered Device: Name - " + device.getName() + ", Address - " + device.getAddress());

                    discoveredAdapter.add(device.getName()); // Add the device to the adapter
                    discoveredAdapter.notifyDataSetChanged(); // Notify adapter of the data change
                    devicesList.add(device);

                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())) {
                Log.e(TAG, "Discovery started");
                isDiscovering = true;
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
                if (isDiscovering) {
                    Log.e(TAG, "Discovery finished");
                    isDiscovering = false;
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

                    ArrayList<BluetoothDevice> currentBoundDevices = foregroundService.getConnectedDevices();

                    if(currentBoundDevices.size()>0)
                    {
                        boolean isNotInFoundList = true;
                         for (BluetoothDevice ble: devicesList)
                        {
                            if(currentBoundDevices.get(0).getAddress().equals(ble.getAddress()))
                            {
                                isNotInFoundList =false;
                            }
                        }

                        if(isNotInFoundList)
                        {
                            discoveredAdapter.add(currentBoundDevices.get(0).getName()); // Add the device to the adapter
                            discoveredAdapter.notifyDataSetChanged(); // Notify adapter of the data change
                            devicesList.add(currentBoundDevices.get(0));

                        }

                    }

                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())) {
                BluetoothDevice bondedDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (bondedDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    // Update the connected device when bonding is complete
                    connectedDevice = bondedDevice;
                    isNowConnected = true;
                    Log.e("bondedDevices", bondedDevice.getName());
                    callBluetoothServer(getString(R.string.registerNewDevice), "Register Bluetooth Device", 1);
                    // Log the currently connected device
                    Log.d(TAG, "Connected Device: Name - " + connectedDevice.getName() + ", Address - " + connectedDevice.getAddress());
                }
            }

        }
    };


    private void registerBluetoothReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        getApplicationContext().registerReceiver(receiver, filter);

    }

    private boolean checkIfDeviceAlreadyAdded(BluetoothDevice device) {
        for (BluetoothDevice object : devicesList) {
            if (object.getAddress().equals(device.getAddress())) {
                return true;
            }
        }
        return false;
    }


    public void hideBottomNav() {
        activityMainBinding.bottomNavigationView.setVisibility(View.GONE);
    }

    public void showBottomNav() {
        activityMainBinding.bottomNavigationView.setVisibility(View.VISIBLE);
    }


    public void replaceNormalFragment(Fragment fragment) {
//        fragment.setArguments(args); // Set the arguments before replacing the fragment
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.mainFrameLayout, fragment, SettingsFragment.class.getSimpleName())
                .addToBackStack(SettingsFragment.class.getSimpleName())
                .commit();
    }

    public void replaceAlertFragment(Fragment fragment) {
//        fragment.setArguments(args); // Set the arguments before replacing the fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void replaceFragment(Fragment fragment, Bundle args) {

        fragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.mainFrameLayout, fragment, SettingsFragment.class.getSimpleName())
                .addToBackStack(SettingsFragment.class.getSimpleName())
                .commit();
    }


    @Override
    protected void onDestroy() {
        unbindService(foregroundServiceConnection);
        super.onDestroy();
    }
}