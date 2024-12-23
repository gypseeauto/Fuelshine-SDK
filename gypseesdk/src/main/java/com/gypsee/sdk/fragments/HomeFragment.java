package com.gypsee.sdk.fragments;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS;
import static android.Manifest.permission.ACTIVITY_RECOGNITION;
import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.BLUETOOTH_CONNECT;
import static android.Manifest.permission.BLUETOOTH_SCAN;
import static android.Manifest.permission.MODIFY_AUDIO_SETTINGS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.BIND_AUTO_CREATE;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.POWER_SERVICE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

//import static com.facebook.FacebookSdk.getApplicationContext;


import static com.gypsee.sdk.utils.Constants.goToDashboard;
import static com.gypsee.sdk.utils.Constants.showSubscribeDialog;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.work.BackoffPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.RuntimeCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.enums.AvailableCommandNames;
import com.github.pires.obd.enums.ObdProtocols;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gypsee.sdk.Adapters.DriveDetailsRecyclerAdapter;
import com.gypsee.sdk.Adapters.SingleDayTripAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.ConfigActivity;
import com.gypsee.sdk.activities.LoginRegisterActivity;
import com.gypsee.sdk.activities.GypseeMainActivity;
import com.gypsee.sdk.activities.SecondActivity;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.config.ObdConfig;
import com.gypsee.sdk.database.DatabaseHelper;
import com.gypsee.sdk.databinding.AlertAddcarLayoutBinding;
import com.gypsee.sdk.databinding.FragmentHomeBinding;
import com.gypsee.sdk.dialogs.ChannelpromocodeDialog;
import com.gypsee.sdk.dialogs.GiveFeedbackDialog;
import com.gypsee.sdk.dialogs.ReferralCodeDialogFragment;
import com.gypsee.sdk.dialogs.SelectCarDialog;
import com.gypsee.sdk.dialogs.ServiceReminderConfirmationDialog;
import com.gypsee.sdk.dialogs.SubscribeDialog;
import com.gypsee.sdk.helpers.BluetoothHelperClass;
import com.gypsee.sdk.helpers.DistanceCalculator;
import com.gypsee.sdk.interfaces.BlueToothConnectionInterface;
import com.gypsee.sdk.io.AbstractGatewayService;
import com.gypsee.sdk.io.LogCSVWriter;
import com.gypsee.sdk.io.ObdCommandJob;
import com.gypsee.sdk.io.ObdGatewayService;
import com.gypsee.sdk.models.BluetoothDeviceModel;
import com.gypsee.sdk.models.DailyTripAlertCountModel;
import com.gypsee.sdk.models.DeviceInformationModel;
import com.gypsee.sdk.models.DriveDetailsModelClass;
import com.gypsee.sdk.models.GameLevelModel;
import com.gypsee.sdk.models.LastSafeTripModel;
import com.gypsee.sdk.models.LatLongModel;
import com.gypsee.sdk.models.MemberModel;
import com.gypsee.sdk.models.ObdProtocolsModel;
import com.gypsee.sdk.models.SubscriptionModel;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.models.UserObdDeviceModel;
import com.gypsee.sdk.models.VehicleAlertModel;
import com.gypsee.sdk.models.Vehiclemodel;
import com.gypsee.sdk.net.ObdReading;
import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;
import com.gypsee.sdk.services.ForegroundService;
import com.gypsee.sdk.services.TripBackGroundService;
import com.gypsee.sdk.services.TripForeGroundService;
import com.gypsee.sdk.threads.ConnectThread;
import com.gypsee.sdk.trips.TripAlert;
import com.gypsee.sdk.trips.TripRecord;
import com.gypsee.sdk.utils.Constants;
import com.gypsee.sdk.utils.GpsUtils;
import com.gypsee.sdk.utils.MixpanelUtils;
import com.gypsee.sdk.utils.StringFormater;
import com.gypsee.sdk.utils.TimeUtils;
import com.gypsee.sdk.utils.Utils;
import com.gypsee.sdk.workmanager.SubscribeNowNotifService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private Context context;
    private MyPreferenece myPreferenece;
    private Map<String, String> dtcVals;
    private FragmentHomeBinding fragmentHomeBinding;
    DatabaseHelper databaseHelper;
    private SelectCarDialog selectCarDialog;

    private BluetoothAdapter bluetoothAdapter;


    public HomeFragment() {
    }

    public static HomeFragment newInstance(boolean isNotAttachedToContext) {
        Bundle args = new Bundle();
        args.putBoolean("isNotAttachedToContext", isNotAttachedToContext);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onResume() {
        super.onResume();
//        registerListener();
        //call to bluetooth connection service

        // We are calling it after some period . because directly setting the values giving nullpointer exception.
        //If a new User, Do not go with the device connection
        // We will connect to bluetooth only if the home fragment is top of the fragments list.
        //Fetching the car list size so that we can directly connect if there is car list.

        //This is to initialize foreground services


        callServer(getResources().getString(R.string.Fetch_UserDetils_url).replace("userid", user.getUserId()), "Fetch user data", 7);

    }

    private void callfunctionONResume() {
        callServer(getString(R.string.subscriptionFetch), "Fetch Available Subscriptions", 21);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "Pausing..");

    }

    ZoopAddCarDialogFragment zoopAddCarDialogFragment;

    private void showZoopAddCarDialog() {

        if (zoopAddCarDialogFragment != null && zoopAddCarDialogFragment.isAdded() && isAdded()) {

        } else {
            zoopAddCarDialogFragment = new ZoopAddCarDialogFragment();
            zoopAddCarDialogFragment.setCancelable(false);
            zoopAddCarDialogFragment.show(getChildFragmentManager(), ZoopAddCarDialogFragment.class.getSimpleName());
        }
    }


    private String[] troubleCodes;

    private boolean isServiceRunning(String serviceClassName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)) {
                Log.e(TAG, "Running");
                return true;
            }
        }
        Log.e(TAG, "Not running");
        return false;
    }

    private WorkManager mWorkManager;

    public void showEndTripBox(){
        fragmentHomeBinding.wrapTripBox.setVisibility(View.VISIBLE);
        Animation rotateAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate);
        fragmentHomeBinding.renewIcon.startAnimation(rotateAnimation);
        fragmentHomeBinding.startTripBtn.setEnabled(false);

//        fragmentHomeBinding.startTripBtn.setBackgroundColor();

        Log.e("WrapTxt","Show WrapUp Text");
    }

    public void hideEndTripBox(){
        fragmentHomeBinding.wrapTripBox.setVisibility(View.GONE);
        fragmentHomeBinding.renewIcon.clearAnimation();
        fragmentHomeBinding.startTripBtn.setEnabled(true);

        Log.e("WrapTxt","Hide WrapUp Text");

    }




    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {


        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        rootView = fragmentHomeBinding.getRoot();

        ((GypseeMainActivity) requireActivity()).showBottomNav();

        dtcVals = getDict(R.array.dtc_keys, R.array.dtc_values);
        databaseHelper = new DatabaseHelper(context);
        myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);

        user = myPreferenece.getUser();
        Log.e(TAG, "Firebasetoken : " + myPreferenece.getStringData(MyPreferenece.FCM_TOKEN));
        latLongModelArrayList.clear();
        // calling new triplist
        //enableBluetooth();
        fragmentHomeBinding.startTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "Manual Trip start clicked");
                if (foregroundService == null) {
                    // Start service and bind service
                    ((GypseeMainActivity) requireActivity()).checkServiceRunning();
                    return;
                }

                if (foregroundService.currentTrip == null) {
                    Log.e(TAG,"foregroundService currentTrip = " +foregroundService.currentTrip );
                    showHideProgressLayout(true,false);
                    foregroundService.startManualTrip(true);
                } else {
                    showEndTripConfirmationDialog();
                }
            }
        });

        user = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getUser();



        Log.e("userdate",user.getCreatedOn());



        initSqlDatabaseOps();
        initViews();
        implementclickListener();
        initNavigationViews();
        setUpHeaderView();

        initMixpanel();
        sendMixpanelUserData();
        // setDrivingMode();
        getSavedObd();
        getDevices();
        //initVehicleHealthOps();
        getUserVehicles();
        getEmergencyContacts();

        Log.e(TAG, "inTrainingMode here: " + user.isInTrainingMode());

        registerForegroundServiceReceiver();
        Intent intent = ((AppCompatActivity) context).getIntent();

        isServiceRunning("BluetoothConnectionService");
        if (intent.getStringExtra("notificationType") != null) {
            checkNotificationIntentValues();
        }

        if (intent.getStringExtra("targetUrl") != null && intent.getStringExtra("notificationBody") != null) {
            notificationDialog(intent.getStringExtra("notificationBody"), intent.getStringExtra("targetUrl"));
        }

        // For fresh login we will fetch the driving alerts.
        if (getActivity().getIntent().getBooleanExtra("freshlogin", false)) {

            callServer(getResources().getString(R.string.Fetch_UserDetils_url).replace("userid", user.getUserId()), "Fetch user data", 7);
        }

//        fragmentHomeBinding.alertsLayout.getRoot().setVisibility(/*BuildConfig.DEBUG ? View.VISIBLE : */View.GONE);
        addProtocol();

        //checkGpsTimer();
        gotoBatteryOptiomization();
        callfunctionONResume();
//        showHideProgressLayout(true,false);

        Bundle bundle = getArguments();

        String action = bundle.getString("action");
        if (action != null) {
            if (action.equals(goToDashboard)) {
//                changeBackgroundforSelectedDriveMode(fragmentHomeBinding.status);
            } else if (action.equals(showSubscribeDialog)) {
                showSubscribeDialog();
            }
        }

        checkForeServiceInitializedOrNot();
        return rootView;
    }

    private void checkForeServiceInitializedOrNot() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAdded() && ((GypseeMainActivity) requireActivity()).checkServiceRunning()) {
                    connectWithForegroundService();
                } else if (isAdded()) {
                    // Re-run the check if the fragment is still attached
                    checkForeServiceInitializedOrNot();
                }
            }
        }, 3000L);
    }


//    private void checkForeServiceInitializedOrNot() {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (((MainActivity) requireActivity()).checkServiceRunning())
//                {
//                    connectWithForegroundService();
//                }else {
//                    checkForeServiceInitializedOrNot();
//                }
//            }
//        },3000L);
//    }


    private void showEndTripConfirmationDialog() {
        new AlertDialog.Builder(context)
                .setTitle("Stop Trip")
                .setMessage("Are you sure you want to stop tracking your trip?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showHideProgressLayout(true,true);
                        foregroundService.endManualTrip();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showHideProgressLayout(boolean showProgressBar, boolean isEndTrip) {

        String text= context.getString(R.string.waiting_for_gps);
        if(isEndTrip)
        {
            text= context.getString(R.string.ending_trip);
        }
        fragmentHomeBinding.progressViewText.setText(text);
        if(showProgressBar){
            fragmentHomeBinding.startTripBtn.setVisibility(View.GONE);
            fragmentHomeBinding.startTripProgressLayout.setVisibility(View.VISIBLE);
        }else{
            fragmentHomeBinding.startTripBtn.setVisibility(View.VISIBLE);
            fragmentHomeBinding.startTripProgressLayout.setVisibility(View.GONE);
        }
    }






















    //To start periodic notification to subscribe
    private void initWorkManager() {
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(SubscribeNowNotifService.class, 6, TimeUnit.HOURS)
                .addTag(Constants.subsNowWorkerTag)
                .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                .build();
        mWorkManager = WorkManager.getInstance(context.getApplicationContext());

        mWorkManager.enqueue(
                periodicWorkRequest
        );
    }

    private void notificationDialog(String message, String url) {
        BluetoothHelperClass.notificationDialog(context, getLayoutInflater(), message, url);
    }


    private void getSavedObd() {

        Log.e(TAG, "tokenn: " + user.getUserAccessToken());
        Log.e(TAG, "userID: " + user.getUserId());
        callServer(getString(R.string.getObdDevices).replace("userId", user.getUserId()), "Fetch OBD List", 14);

    }

    private void getDevices() {
        callServer(getString(R.string.getUserRegisteredDevices).replace("userId", user.getUserId()), "Fetch Registered Devices", 18);
        callServer(getString(R.string.getDeviceCategories), "Fetch Device Categories", 19);
    }


    private void registerForegroundServiceReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(context.getString(R.string.bluetooth_status_action));
        intentFilter.addAction(context.getString(R.string.connect_bluetooth));
        intentFilter.addAction(context.getString(R.string.current_trip_action));
        intentFilter.addAction(context.getString(R.string.end_trip_action));
        intentFilter.addAction(context.getString(R.string.no_internet_trip_will_end_after_getting_connection));
        intentFilter.addAction(context.getString(R.string.no_internet_trip_will_start_after_getting_connection));
        intentFilter.addAction("restart checkGPSEnabledOrnotInit");
        intentFilter.addAction("restart MainActivity");
        intentFilter.addAction("dialogNotification");

        LocalBroadcastManager.getInstance(context).registerReceiver(foregroundServiceBroadcast, intentFilter);
    }

    private void checkGPSEnable() {
        GpsUtils gpsUtils = new GpsUtils(context);
        gpsUtils.turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                //if (isGPSEnable)
            }
        });
//        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(ConfigActivity.ENABLE_GPS_KEY, true).apply();
    }

    private void callWallet(String url, final String purpose,boolean includeTransactions, String periodFrom, String periodTo){

        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);
        Call<ResponseBody> call;
        JsonObject jsonObject = new JsonObject();
        User user = myPreferenece.getUser();

        call = apiService.getWallet(user.getUserAccessToken(),includeTransactions ,periodFrom, periodTo);


        Log.e(TAG, purpose + " Input : " + jsonObject);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

//                    fragmentStoreMainBinding.progressLayout.setVisibility(View.GONE);

                    if (response.isSuccessful()){
                        Log.e(TAG, "Response is success");

                        ResponseBody responseBody = response.body();
                        if (responseBody == null){
                            return;
                        }


                        String responseStr = responseBody.string();
                        Log.e(TAG, purpose + " Resonse : " + responseStr);

                        parseWallet(responseStr);




                    } else {

                        Log.e(TAG, purpose + " Response is not successful");

                        String errResponse = response.errorBody().string();
                        Log.e("Error code 400", errResponse);
                        Log.e(TAG, purpose + "Response is : " + errResponse);
                        int responseCode = response.code();
                        if (responseCode == 401 || responseCode == 403) {
                            //include logout functionality
                            Utils.clearAllData(requireContext());
                            return;
                        }

                    }

                } catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Log.e(TAG, "error here since request failed");
                Log.e(TAG, t.getMessage());
                if (t.getMessage().contains("Unable to resolve host")) {
                    Toast.makeText(requireContext(), "Please Check your internet connection", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Please check your internet connection");
                } else {}

            }
        });



    }

    private void parseWallet(String response) throws JSONException {

        JSONObject jsonObject = new JSONObject(response);

        if (!jsonObject.has("data")){
            Toast.makeText(requireContext(), "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject walletObj = jsonObject.getJSONObject("data");


        String totalEarnedAmount = walletObj.getString("totalEarnedAmount");
        String totalSpentAmount = walletObj.getString("totalSpentAmount");
        String balanceAmount = walletObj.getString("balanceAmount");


//        JSONArray transactionsArray = walletObj.getJSONArray("transactions");
//
//        if (transactionsArray == null || transactionsArray.length() == 0) {
//            Log.d(TAG, "Transaction array is empty");
//        } else {
//            for (int i = 0; i < transactionsArray.length(); i++) {
//                JSONObject transactionObj = transactionsArray.getJSONObject(i);
////                transactionObj.getString("");
//            }
//        }


        fragmentHomeBinding.earnedWalletAmount.setText("₹"+totalEarnedAmount);
        fragmentHomeBinding.spentWalletAmount.setText("₹"+totalSpentAmount);
        fragmentHomeBinding.balanceWalletAmount.setText("₹"+balanceAmount);


    }


    private void callGameServer(String url, final String purpose, String periodFrom, String periodTo, int value){

        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);
        Call<ResponseBody> call;
        JsonObject jsonObject = new JsonObject();
        User user = myPreferenece.getUser();

//        fragmentStoreMainBinding.loadBar.setVisibility(View.VISIBLE);

        switch (value){

            case 2:

                call = apiService.getFuelSavings(user.getUserAccessToken(), periodFrom, periodTo);
                break;

            //fetch init items
            case 0:
                //fetch product list
//                loadNext = false;
//                Log.e(TAG, "offset: " + offset + ", limit: " + limit);
//                jsonObject.addProperty("periodFrom", periodFrom);
//                jsonObject.addProperty("periodTo", periodTo);
                call = apiService.getGameLevel(user.getUserAccessToken(), periodFrom, periodTo);

                break;

            case 1:
            default:
//                jsonObject.addProperty("periodFrom", periodFrom);
//                jsonObject.addProperty("periodTo", periodTo);
                call = apiService.getGameLevel(user.getUserAccessToken(),periodFrom,periodTo);
                break;


        }



        Log.e(TAG, purpose + " Input : " + jsonObject);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

//                    fragmentStoreMainBinding.progressLayout.setVisibility(View.GONE);

                    if (response.isSuccessful()){
                        Log.e(TAG, "Response is success");

                        ResponseBody responseBody = response.body();
                        if (responseBody == null){
                            return;
                        }


                        String responseStr = responseBody.string();
                        Log.e(TAG, purpose + " Resonse : " + responseStr);

                        switch (value){

                            case 1:
                                parseGameLevel(responseStr);
//                                parseFetchCategories(responseStr);
                                break;

                            case 2:
                                parseFuelSaving(responseStr);
                                break;
                            case 3:
//                                parseFuelPrice(responseStr);
                                break;


                        }



                    } else {

                        Log.e(TAG, purpose + " Response is not successful");

                        String errResponse = response.errorBody().string();
                        Log.e("Error code 400", errResponse);
                        Log.e(TAG, purpose + "Response is : " + errResponse);
                        int responseCode = response.code();
                        if (responseCode == 401 || responseCode == 403) {
                            //include logout functionality
                            Utils.clearAllData(context);
                            return;
                        }

                    }

                } catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Log.e(TAG, "error here since request failed");
                Log.e(TAG, t.getMessage());
                if (t.getMessage().contains("Unable to resolve host")) {
                    Toast.makeText(context, "Please Check your internet connection", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Please check your internet connection");
                } else {}

            }
        });



    }


    private void parseFuelSaving(String response) throws JSONException {

        JSONObject jsonObject = new JSONObject(response);

        if (!jsonObject.has("data")){
            Toast.makeText(context, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject fuelSavingObj = jsonObject.getJSONObject("data");


        String fuelSavings = fuelSavingObj.getString("fuelSavings");
        String earnedAmount = fuelSavingObj.getString("earnedAmount");

        double fuelSavingsValue = Double.parseDouble(fuelSavings);
        String formattedFuelSavings = String.format("%.1f", fuelSavingsValue);
        fragmentHomeBinding.safeTripCountTv.setText(formattedFuelSavings);

        fragmentHomeBinding.earnedAmount.setText("₹" + earnedAmount);


//        if (gameLevelObj.has("gameLevel")){
//            String level = gameLevelObj.getString("gameLevel");
//            String totalSafePercent = gameLevelObj.getString("totalSafeKmPercent");
//            String totalSafeKm = gameLevelObj.getString("totalSafeKms");
//
//            gamelevelArray.add(new GameLevelModel(level,totalSafePercent,totalSafeKm));
//
//            fragmentHomeBinding.level1.setText(level);
//            fragmentHomeBinding.text3.setText(level);
//            fragmentHomeBinding.kmDrivenTv.setText(totalSafeKm);
//            fragmentHomeBinding.safeKmPercent.setText(totalSafePercent);
//        }else{
//            String totalSafePercent = gameLevelObj.getString("totalSafeKmPercent");
//            String totalSafeKm = gameLevelObj.getString("totalSafeKms");
//
//            gamelevelArray.add(new GameLevelModel("LEVEL1",totalSafePercent,totalSafeKm));
//
//            fragmentHomeBinding.level1.setText("LEVEL1");
//            fragmentHomeBinding.text3.setText("LEVEL1");
//            fragmentHomeBinding.kmDrivenTv.setText(totalSafeKm + "km");
//            fragmentHomeBinding.safeKmPercent.setText(totalSafePercent + "%");
//        }

    }


    private ArrayList<GameLevelModel> gamelevelArray;
    private void parseGameLevel(String response) throws JSONException {

        JSONObject jsonObject = new JSONObject(response);

        if (!jsonObject.has("gameLevel")){
            Toast.makeText(context, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject gameLevelObj = jsonObject.getJSONObject("gameLevel");

        String level = gameLevelObj.has("gameLevel") ?gameLevelObj.getString("gameLevel"):"0";
        String totalSafePercent = gameLevelObj.getString("totalSafeKmPercent");
        String totalSafeKm = gameLevelObj.getString("totalSafeKms");

//        if (gameLevelObj.has("gameLevel")){
//            String level = gameLevelObj.getString("gameLevel");
//            String totalSafePercent = gameLevelObj.getString("totalSafeKmPercent");
//            String totalSafeKm = gameLevelObj.getString("totalSafeKms");
//
//            gamelevelArray.add(new GameLevelModel(level,totalSafePercent,totalSafeKm));
//
//            fragmentHomeBinding.level1.setText(level);
//            fragmentHomeBinding.text3.setText(level);
//            fragmentHomeBinding.kmDrivenTv.setText(totalSafeKm);
//            fragmentHomeBinding.safeKmPercent.setText(totalSafePercent);
//        }else{
//            String totalSafePercent = gameLevelObj.getString("totalSafeKmPercent");
//            String totalSafeKm = gameLevelObj.getString("totalSafeKms");
//
//            gamelevelArray.add(new GameLevelModel("LEVEL1",totalSafePercent,totalSafeKm));
//
//            fragmentHomeBinding.level1.setText("LEVEL1");
//            fragmentHomeBinding.text3.setText("LEVEL1");
//            fragmentHomeBinding.kmDrivenTv.setText(totalSafeKm + "km");
//            fragmentHomeBinding.safeKmPercent.setText(totalSafePercent + "%");
//        }

        fragmentHomeBinding.level1.setText(level);
        fragmentHomeBinding.text3.setText(level);
        fragmentHomeBinding.kmDrivenTv.setText(totalSafeKm);
        fragmentHomeBinding.safeKmPercent.setText(totalSafePercent + "%");

    }



    private void gotoBatteryOptiomization() {

        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (powerManager.isIgnoringBatteryOptimizations(context.getPackageName())) {
                Log.e(TAG, "No battery restriction: " );
            } else {
                Log.e(TAG, "battery restriction: " );

                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                startActivity(intent);

                //BluetoothHelperClass.showOkCancelDialog(context, getLayoutInflater(), "", "Gypsee app collects location data to enable \"Plotting trip Route on google map\", \"Distance travelled\", & \"Driving alerts\" even when the app is closed or not in use.", responseFromServer, 1);


            }
        }


    }

    MixpanelAPI mixpanelAPI;

    private void initMixpanel() {
        mixpanelAPI = MixpanelAPI.getInstance(context, MixpanelUtils.TOKEN);
        mixpanelAPI.identify(user.getUserId());
        mixpanelAPI.getPeople().identify(user.getUserId());
    }


    private void sendMixpanelUserData() {
        try {
            MixpanelAPI.People people = mixpanelAPI.getPeople();
            PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);

            JSONObject initProperties = new JSONObject();
            initProperties.put(MixpanelUtils.USER_NAME, user.getUserFullName());
            initProperties.put(MixpanelUtils.USER_EMAIL, user.getUserEmail());
            initProperties.put(MixpanelUtils.USER_PHONE, user.getUserPhoneNumber());

            JSONObject updateProperties = new JSONObject();
            updateProperties.put(MixpanelUtils.USER_TYPE, user.getUserTypes());
            updateProperties.put(MixpanelUtils.USER_TRAINING_MODE, user.isInTrainingMode());

            JSONArray deniedPermissionJSONArray = new JSONArray();
            for (String item : fetchDeniedPermissions()) {
                deniedPermissionJSONArray.put(item);
            }

            updateProperties.put(MixpanelUtils.USER_DENIED_PHONE_PERMISSIONS, deniedPermissionJSONArray);
            updateProperties.put(MixpanelUtils.USER_BATTERY_OPTIMIZATION_DISABLED, powerManager.isIgnoringBatteryOptimizations(context.getPackageName()));

            Log.d("Mixpanel", "initProperties: " + initProperties.toString());
            Log.d("Mixpanel", "updateProperties: " + updateProperties.toString());

            people.setOnce(initProperties);
            people.set(updateProperties);
            mixpanelAPI.flush();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedOperationException e) {
            Log.e("Mixpanel", "UnsupportedOperationException: " + e.getMessage());
        }
    }


    private List<String> fetchDeniedPermissions() {
        String[] requiredPermissions = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION, WRITE_EXTERNAL_STORAGE, BLUETOOTH, BLUETOOTH_ADMIN, ACCESS_LOCATION_EXTRA_COMMANDS, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE,MODIFY_AUDIO_SETTINGS};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // only for gingerbread and newer versions
            requiredPermissions = new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION, WRITE_EXTERNAL_STORAGE, BLUETOOTH, BLUETOOTH_ADMIN, ACCESS_LOCATION_EXTRA_COMMANDS, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, ACTIVITY_RECOGNITION,MODIFY_AUDIO_SETTINGS};
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //Bluetooth_scan and bluetooth_connect permissions required only for android s and newer version
            requiredPermissions = new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION, WRITE_EXTERNAL_STORAGE, BLUETOOTH, BLUETOOTH_ADMIN, BLUETOOTH_CONNECT, BLUETOOTH_SCAN, ACCESS_LOCATION_EXTRA_COMMANDS, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, ACTIVITY_RECOGNITION,MODIFY_AUDIO_SETTINGS};
        }

        List<String> deniedPermissionList = new ArrayList<>();
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PERMISSION_GRANTED) {
                deniedPermissionList.add(permission);
            }
        }
        return deniedPermissionList;
    }





    ArrayList<String> protocolStrings = new ArrayList<>();
    int selectedProtocolPosition = 0;

    private void addProtocol() {
        for (ObdProtocolsModel protocol : ObdProtocolsModel.values()) {
            protocolStrings.add(protocol.name());
        }
    }

    private void implementclickListener() {

//        fragmentHomeBinding.status.setOnClickListener(this);
//        fragmentHomeBinding.logoLayout.setOnClickListener(this);
//        fragmentHomeBinding.tvLocateMe.setOnClickListener(this);
//        fragmentHomeBinding.menuBtn.setOnClickListener(this);
//        fragmentHomeBinding.notificationIcon.setOnClickListener(this);
//        fragmentHomeBinding.shareIcon.setOnClickListener(this);
//        //fragmentHomeBinding.driveModeLayout.setOnClickListener(this);
//
//        fragmentHomeBinding.driveModeLayout.setOnClickListener(this);
//
//        fragmentHomeBinding.startDriveButton.setOnClickListener(this);
//
//        fragmentHomeBinding.exitTrainingMode.setOnClickListener(this);
//        fragmentHomeBinding.topBar.bluetoothIcon.setOnClickListener(this);
        fragmentHomeBinding.historical.setOnClickListener(this);
        fragmentHomeBinding.today.setOnClickListener(this);
        fragmentHomeBinding.genRules.setOnClickListener(this);
//        fragmentHomeBinding.fuelSavingText.setOnClickListener(this);

    }

    // This is to check the notification intent values

    private void checkNotificationIntentValues() {
        Intent intent = ((AppCompatActivity) context).getIntent();

        String notificationType = intent.getStringExtra("notificationType");
        Log.e(TAG, "notificationType : " + notificationType);
        switch (notificationType) {
            case "serviceReminderConfirmationNotification":
                String dataObject = intent.getStringExtra("dataObject");

                try {
                    JSONObject jsonObject = new JSONObject(dataObject);
                    String vehicleId = jsonObject.getString("vehicleId");
                    ArrayList<Vehiclemodel> vehiclemodelArrayList = new DatabaseHelper(context).fetchAllVehicles();
                    for (Vehiclemodel vehiclemodel : vehiclemodelArrayList) {
                        Log.e(TAG, "VehicleID" + vehicleId);
                        Log.e(TAG, "Vehicle ID : " + vehiclemodel.getUserVehicleId());

                        if (vehiclemodel.getUserVehicleId().equals(vehicleId)) {

                            //Show dialog to update the ServiceReminderConfirmationNotification status to true.
                            ServiceReminderConfirmationDialog serviceReminderConfirmationDialog = new ServiceReminderConfirmationDialog(context, vehiclemodel, dataObject);
                            serviceReminderConfirmationDialog.setCanceledOnTouchOutside(false);
                            serviceReminderConfirmationDialog.setCancelable(false);
                            serviceReminderConfirmationDialog.show();

                            Window callDialogWindow = serviceReminderConfirmationDialog.getWindow();
                            // TO set the
                            callDialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
        }
    }


    AlertDialog addvehicleAlertDialog;

    private void showAddCarDialog(final boolean isShowingCarAlert) {

        if (addvehicleAlertDialog != null && addvehicleAlertDialog.isShowing() && !isServiceBound) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertAddcarLayoutBinding alertAddcarLayoutBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.alert_addcar_layout, null, false);

        if (isShowingCarAlert) {

        } else {
            alertAddcarLayoutBinding.titleTv.setText("Permission Request!");
            alertAddcarLayoutBinding.descriptionTV.setText("We request you to provide all the permissions to provide you the best features of the app");
            alertAddcarLayoutBinding.negativeBtn.setVisibility(View.GONE);
            alertAddcarLayoutBinding.lineView2.setVisibility(View.GONE);
        }

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog l``ayout
        builder.setView(alertAddcarLayoutBinding.getRoot());
        addvehicleAlertDialog = builder.create();
        addvehicleAlertDialog.setCanceledOnTouchOutside(false);
        addvehicleAlertDialog.setCancelable(false);
        addvehicleAlertDialog.show();
        addvehicleAlertDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

        alertAddcarLayoutBinding.positioveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to AddVehicleFragment.
                addvehicleAlertDialog.dismiss();
                if (isShowingCarAlert) {
                    startActivity(new Intent(getActivity(), SecondActivity.class)
                            .putExtra("TAG", "AddVehicleFragment"));
                } else {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, PERMISSION_REQUEST_CODE);
                }


            }
        });

        alertAddcarLayoutBinding.negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addvehicleAlertDialog.dismiss();
            }
        });
    }


    //Created Today


    private void setUpSensorData() {

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

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

    private void setUpHeaderView() {
//        TextView profileusername = headerView.findViewById(R.id.profileUserName);
//        TextView profileEmail = headerView.findViewById(R.id.profileEmail);
//        CircleImageView profileImage = headerView.findViewById(R.id.profileImage);
        user = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getUser();
        Log.e(TAG, "Authorization " + user.getUserAccessToken());
        Log.e(TAG, "User ID " + user.getUserId());

//        profileusername.setText(StringFormater.capitalizeWord(user.getUserFullName()));
        String email = user.getUserEmail();
//        profileEmail.setText(email);
//        if (user.getUserImg().contains("http"))
//            Glide
//                    .with(context)
//                    .load(user.getUserImg())
//                    .placeholder(R.drawable.ic_profile)
//                    .centerInside()
//                    .into(profileImage);
    }


//    private NavigationView navigationView;
//    private DrawerLayout drawer;
//    private View headerView;

    private void initNavigationViews() {

//        drawer = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);
//        navigationView = (NavigationView) rootView.findViewById(R.id.nav_view);
//        headerView = navigationView.inflateHeaderView(R.layout.user_profile_header_layout);

//        TextView termsAndCondTv = navigationView.findViewById(R.id.termsConditionsTv);
//        TextView appVersionTv = navigationView.findViewById(R.id.appVersion);
//        TextView privacyPolicy = navigationView.findViewById(R.id.privacyPolicy);

//        PackageInfo pInfo = null;
//        try {
//            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
//            appVersionTv.setText("V" + pInfo.versionName + " (" + pInfo.versionCode + ")");
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }

//        termsAndCondTv.setOnClickListener(this);
//        privacyPolicy.setOnClickListener(this);
//        navigationView.invalidate();
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//
//            // This method will trigger on item Click of navigation menu
//            @Override
//            public boolean onNavigationItemSelected(MenuItem menuItem) {
//                drawer.closeDrawers();
//
//                //Check to see which item was being clicked and perform appropriate action
//                switch (menuItem.getItemId()) {
//                    //Replacing the main content with ContentFragment Which is our Inbox View;
//
//                    case R.id.nav_intro:
//                        startActivity(new Intent(context, QuickManualActivity.class));
//
//                        break;
//                    case R.id.nav_raiseTicket:
//
//                       /* String userDetails = Utils.getDeviceName()
//                                + "\n" + user.getUserFullName()
//                                + "\n" + user.getUserPhoneNumber();
//                        sendEmail("Issue with Gypsee App", userDetails);*/
//                        showGiveFeedbackDialog();
//
//                        break;
//                    case R.id.nav_emContact:
//                        startActivity(new Intent(context, Emergencyactivity.class));
//                        break;
//                    case R.id.referral_code:
//                        checkReferralCodeApplied();
//                        break;
//
//                    case R.id.partner_login:
//                        FirebaseLogEvents.firebaseLogEvent("partner_login");
//                        Intent partnerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://gypsee.in/gypsee-services/#/account/login"));
//                        startActivity(partnerIntent);
//                        break;
//
//                    case R.id.nav_subscribe:
//                        showSubscribeDialog();
//                        break;
//
//
//                    case R.id.nav_statting:
//                        if (currentTrip == null) {
//                            startActivity(new Intent(context, ConfigActivity.class));
//                        } else {
//                            Toast.makeText(context, "Change settings after end of your trip.", Toast.LENGTH_LONG).show();
//                        }
//                        break;
//
//                    case R.id.nav_vehicles:
//
//                        startActivity(new Intent(getActivity(), SecondActivity.class)
//                                .putExtra("TAG", "MyCarsListFragment")
//                                .putExtra(Vehiclemodel.class.getSimpleName(), selectedvehiclemodel));
//                        break;
////
//                    case R.id.nav_buy_iot:
//                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://gypsee.ai/shop/gypsee-your-smart-safe-driving-kit/"));
//                        startActivity(browserIntent);
//                        break;
//                    case R.id.nav_offers:
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://gypsee.ai/offers/"));
//                        startActivity(intent);
//                        break;
//                    case R.id.channel_referral_code:
//                        showChannelPromocodeDialog();
//                        break;
//
//                    case R.id.technicalSupport:
//                        FirebaseLogEvents.firebaseLogEvent("accessed_technical_support");
//                        BluetoothHelperClass.showTripEndDialog(context, getLayoutInflater(), "Dear Customer!", "You are about to write mail to Gypsee customer support team. Within 48 hours, we will resolve your query.", responseFromServer, 2);
//                        break;
//
//
//                }
//
//                //Checking if the item is   in checked state or not, if not make it in checked state
//                if (menuItem.isChecked()) {
//                    menuItem.setChecked(false);
//                } else {
//                    menuItem.setChecked(true);
//                }
//
//                return true;
//            }
//        });

    }

    private void showChannelPromocodeDialog() {

        //Show dialog to update the ServiceReminderConfirmationNotification status to true.
        ChannelpromocodeDialog channelpromocodeDialog = new ChannelpromocodeDialog(context);
        channelpromocodeDialog.setCanceledOnTouchOutside(true);
        channelpromocodeDialog.setCancelable(true);
        channelpromocodeDialog.show();

        Window callDialogWindow = channelpromocodeDialog.getWindow();

        callDialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


    }


    private void showSubscribeDialog() {

        SubscribeDialog subscribeDialog = new SubscribeDialog(context);
        subscribeDialog.setCancelable(true);
        subscribeDialog.setCanceledOnTouchOutside(true);
        subscribeDialog.show();

        Window dialogWindow = subscribeDialog.getWindow();
        dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    private void showGiveFeedbackDialog() {

        GiveFeedbackDialog giveFeedbackDialog = new GiveFeedbackDialog(context, "app", "null");
        giveFeedbackDialog.setCanceledOnTouchOutside(false);
        giveFeedbackDialog.setCancelable(false);
        giveFeedbackDialog.show();

        giveFeedbackDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
    }

    private void checkReferralCodeApplied() {

        if (user.isReferCodeApplied()) {
            Toast.makeText(context, "Referral code Already applied", Toast.LENGTH_LONG).show();
        } else {
            showReferralCodeDialog();
        }

    }

    private void showReferralCodeDialog() {
        ReferralCodeDialogFragment referralCodeDialogFragment = new ReferralCodeDialogFragment(context);
        referralCodeDialogFragment.show();
    }


    private ArrayList<DriveDetailsModelClass> driveDetailsModelClasses = new ArrayList<>();


    private void initSqlDatabaseOps() {

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        // databaseHelper.InsertNotification("Bhaskar","https://images.unsplash.com/photo-1503023345310-bd7c1de61c7d?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=8","Reddy","May 12 01:21 AM");
        int notificationCount = databaseHelper.fetchAllNotification().size();
        Log.e(TAG, "Notification size  :" + notificationCount);
        fragmentHomeBinding.setNotificationCount("" + notificationCount);
//        if (notificationCount == 0)
//            fragmentHomeBinding.notificationCountTV.setVisibility(View.GONE);
//        else {
//            fragmentHomeBinding.notificationCountTV.setVisibility(View.VISIBLE);
//
//        }
    }


    private User user;
    DriveDetailsRecyclerAdapter driveDetailsRecyclerAdapter;

    private void loadRecyclerView() {
//        if (fragmentHomeBinding.status.getBackground() == null) {
        return;
//        }
//        driveDetailsRecyclerAdapter = new DriveDetailsRecyclerAdapter(context, driveDetailsModelClasses);
//        fragmentHomeBinding.driveDetailsRecyclerview.setAdapter(driveDetailsRecyclerAdapter);
    }


    private void initViews() {
//        fragmentHomeBinding.speedFAB.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);
        fragmentHomeBinding.setBlueToothDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.bluetooth_red, null));
//        fragmentHomeBinding.alertsLayout.setTitle("Alerts");
//        float walletAmount = Float.parseFloat(new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getUser().getWalletAmount());
//        fragmentHomeBinding.safeTripCountTv.setText(String.valueOf(walletAmount));

//        fragmentHomeBinding.safeTripCountTv.setText(new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getUser().getWalletAmount());
//        fragmentHomeBinding.kmDrivenTv.setText(new Double(myPreferenece.getStringData("totalDistanceTravelledInKm")).intValue() + " KM");
//        fragmentHomeBinding.alertsLayout.setAlertsCount(String.valueOf(0));
//        fragmentHomeBinding.alertsLayout.countTv.setVisibility(View.GONE);
//        fragmentHomeBinding.performanceLayout.setTitle("Car Health");
//        fragmentHomeBinding.performanceLayout.setAlertsCount(String.valueOf(0));
//        fragmentHomeBinding.performanceLayout.countTv.setVisibility(View.GONE);
        fragmentHomeBinding.setOdbDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.obd_disconnected, null));

        ArrayList<Vehiclemodel> vehiclemodelArrayList = new DatabaseHelper(context).fetchAllVehicles();
//        if (vehiclemodelArrayList.size() == 0) {
//            fragmentHomeBinding.setUserName("Hola, " + StringFormater.capitalizeWord(user.getUserFullName()));
//        } else {
//            Vehiclemodel tempVehiclemodel = vehiclemodelArrayList.get(0);
//            fragmentHomeBinding.setUserName("Hola, " + StringFormater.capitalizeWord(user.getUserFullName()) + "\n" + StringFormater.capitalizeWord(tempVehiclemodel.getVehicleBrand().trim() + " " + tempVehiclemodel.getVehicleModel().trim()));
//        }

        if (vehiclemodelArrayList.size() == 0) {
            String userFullName = user.getUserFullName();
            String displayName = (userFullName != null) ? StringFormater.capitalizeWord(userFullName) : "User";
            fragmentHomeBinding.setUserName("Hola, " + displayName);
        } else {
            Vehiclemodel tempVehiclemodel = vehiclemodelArrayList.get(0);

            String userFullName = user.getUserFullName();
            String vehicleBrand = tempVehiclemodel.getVehicleBrand();
            String vehicleModel = tempVehiclemodel.getVehicleModel();

            String displayName = (userFullName != null) ? StringFormater.capitalizeWord(userFullName) : "User";
            String vehicleBrandName = (vehicleBrand != null) ? StringFormater.capitalizeWord(vehicleBrand.trim()) : "Unknown Brand";
            String vehicleModelName = (vehicleModel != null) ? StringFormater.capitalizeWord(vehicleModel.trim()) : "Unknown Model";

            fragmentHomeBinding.setUserName("Hola, " + displayName + "\n" + vehicleBrandName + " " + vehicleModelName);
        }


        fetchTodayDateAndFetchTrips();

    }

    private void fetchTodayDateAndFetchTrips() {




        LocalDate currentDate = LocalDate.now();

        // Define the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Format the current date
        String formattedDate = currentDate.format(formatter);
        String dateString = formattedDate;

        if(isHistoricalTripData){

            String originalDateTimeString = user.getCreatedOn();

            // Define formatter for parsing the original datetime string
            DateTimeFormatter originalFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Parse the original datetime string into LocalDateTime object
            LocalDateTime originalDateTime = LocalDateTime.parse(originalDateTimeString, originalFormatter);

            // Define formatter for the desired format
            DateTimeFormatter desiredFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // Format the LocalDateTime object into the desired format
            String formattedPeriodFromDate = originalDateTime.format(desiredFormatter);



            // Calculate the date 7 days ago
            LocalDate sevenDaysAgo = currentDate.minusDays(7);

            // Format the date string
            dateString = sevenDaysAgo.format(formatter);

            callGameServer(getResources().getString(R.string.gameLevel).replace("{","").replace("}","").replace("userId",user.getUserId()),"Get Game Level",dateString,formattedDate,1);


            callGameServer(getResources().getString(R.string.fuelSavings).replace("{","").replace("}","").replace("userId",user.getUserId()),"Get Fuel Savings",formattedPeriodFromDate,formattedDate,2);


            callWallet(getResources().getString(R.string.wallet).replace("{","").replace("}","").replace("userId",user.getUserId()),"Get wallet Transactions",false,formattedPeriodFromDate,formattedDate);

        }else{

            callGameServer(getResources().getString(R.string.gameLevel).replace("{","").replace("}","").replace("userId",user.getUserId()),"Get Game Level",formattedDate,formattedDate,1);


            callGameServer(getResources().getString(R.string.fuelSavings).replace("{","").replace("}","").replace("userId",user.getUserId()),"Get Fuel Savings",formattedDate,formattedDate,2);


            callWallet(getResources().getString(R.string.wallet).replace("{","").replace("}","").replace("userId",user.getUserId()),"Get wallet Transactions",false,formattedDate,formattedDate);

        }


        callTodayTrips(getResources().getString(R.string.tripListUrl).replace("{","").replace("}","").replace("userId",user.getUserId()),"Get User Today Trips",dateString,formattedDate);



    }


    private void updateBluetoothImageStatus(boolean isActive) {

        fragmentHomeBinding.setBlueToothDrawable(isActive ? context.getResources().getDrawable(R.drawable.bluetooth_green) : context.getResources().getDrawable(R.drawable.bluetooth_red));
    }


    private void updateObdImage(boolean isActive) {
        fragmentHomeBinding.setOdbDrawable(isActive ? ResourcesCompat.getDrawable(context.getResources(), R.drawable.obd_green, null) : ResourcesCompat.getDrawable(context.getResources(), R.drawable.obd_disconnected, null));
    }

    private String TAG = HomeFragment.class.getSimpleName();

    private ArrayList<VehicleAlertModel> vehicleAlertModelArrayList = new ArrayList<>();

    private void updateViews(String cmdID, String cmdResult) {
        Log.e(TAG, "updateViews: " + cmdID + " - " + cmdResult);
        if (cmdID.equals("Reset OBD")) {
           /* doUnbindService();
            performTaskOnBrokenPipe();*/
            isReset = false;
        } else if (cmdID.equals("CONTROL_MODULE_VOLTAGE")) {

        } else if (cmdID.equals("TROUBLE_CODES")) {
            Log.e(TAG, "Trouble codes : " + cmdResult);
            cmdResult = cmdResult.equals("") ? "No trouble code found. Good job" : cmdResult;

            if (cmdResult.contains("No trouble code")) {
                //troubleCodes = new String[0];
//                fragmentHomeBinding.performanceLayout.countTv.setVisibility(View.GONE);
            } else {
                // send performance alert to the server
                cmdResult = cmdResult.replace("C0300", "").replace("U3000", "").replace("C0303", "").trim();

                if (cmdResult.equals("")) {
//                    fragmentHomeBinding.performanceLayout.countTv.setVisibility(View.GONE);
                } else {
                    troubleCodes = cmdResult.split("\n");
                    String selectedTroubleCode = troubleCodes[troubleCodes.length - 1];
//                    fragmentHomeBinding.performanceLayout.countTv.setVisibility(View.VISIBLE);
//                    fragmentHomeBinding.performanceLayout.setAlertsCount("" + troubleCodes.length);
                }
            }
        } /*else if (cmdID.equals("DISTANCE_TRAVELED_AFTER_CODES_CLEARED")) {
            if (startingDistancc == null && !commandResult.equals("0") && !commandResult.isEmpty()) {
                startingDistancc = cmdResult;
            }
            calculateDistanceForNextService();
        }*/ else if (cmdID.equals("VIN")) {
            Log.e(TAG, "Calling the VIN " + cmdResult);
            Log.e(TAG, "Calling the VIN type : " + incrementValueForVINCheck);
            //Wait for 2 turns to get the VIN value then check for the vehicle.
            /*if (incrementValueForVINCheck < 1) {
                ++incrementValueForVINCheck;
                return;
            }*/

            Log.e(TAG, "Vin in " + cmdResult);
            checkForTheVehicle(cmdResult);

//            fragmentHomeBinding.logoLayout.setEnabled(true);
        } else if (cmdID.equals("ENGINE_RUNTIME")) {

            if ((cmdResult.equals("") || cmdResult.equals("00:00:00")) && tripStartTime != null) {
                setTripTime();
            }
        } else if (cmdID.equals("SPEED")) {
            updateSpeed(cmdResult);
        } else if (cmdID.equals("ENGINE_RPM") && cmdResult != null) {
            updateRPMAlert(cmdResult);
        }
    }

    // Funcation to calculate vehicle service reminder parameters (Present distance travelled , next service reminder odomoter reading/next service reminder date .

    int previousDistnacePostCC = 0;

    private boolean isVinCheckedAlready = false;

    private void checkForTheVehicle(String cmdResult) {

        // Checking if isVinCheckedAlready  = true, then return.
//        fragmentHomeBinding.progressLayout.setVisibility(View.GONE);

        if (isVinCheckedAlready) {
            return;
        } else {
            isVinCheckedAlready = true;
        }

        if (selectedvehiclemodel == null) {

            Log.e(TAG, "Checking for the vehicle " + commandResult);

            ArrayList<Vehiclemodel> vehiclemodelArrayList = new DatabaseHelper(context).fetchAllVehicles();
            Log.e(TAG, "vehicles size : " + vehiclemodelArrayList.size());
            // If the vehicle Size is 1. We will directly connect with OBD and return from program

            if (vehiclemodelArrayList.size() == 0) {
                showAddCarDialog(true);
                return;
            } else if (vehiclemodelArrayList.size() == 1) {
                //Only one vehicle connected.
                Log.e(TAG, "vehicles size : " + "One ");
                selectedvehiclemodel = vehiclemodelArrayList.get(0);
                Log.e(TAG, selectedvehiclemodel.getVehicleBrand() + " " + selectedvehiclemodel.getVehicleModel());


                fragmentHomeBinding.setUserName("Hola, " + StringFormater.capitalizeWord(user.getUserFullName()) + "\n" + StringFormater.capitalizeWord(selectedvehiclemodel.getVehicleBrand().trim() + " " + selectedvehiclemodel.getVehicleModel().trim()));
                checkTripStarted();
                return;
            }

            if (cmdResult.isEmpty()) {
                Log.e(TAG, "vehicles size : " + "Empty ");
                showDialogtoSelectCar();
                return;
            }

            // We will loop through the vehicleList and check VIn matching. If matches, we will return
            for (Vehiclemodel vehiclemodel : vehiclemodelArrayList) {
                String vin = vehiclemodel.getVin();

                Log.e(TAG, "Vehicle : " + vehiclemodel.getVehicleBrand() + vehiclemodel.getVin());
                if (vin.equals(cmdResult)) {
                    // If the dialog is already showing, no need to show the popup again.
                    if (selectCarDialog != null && selectCarDialog.isShowing()) {
                        selectCarDialog.dismiss();
                    }

                    Log.e(TAG, "Selected vehicle: " + vehiclemodel.getVin());

                    selectedvehiclemodel = vehiclemodel;
                    fragmentHomeBinding.setUserName("Hola, " + StringFormater.capitalizeWord(user.getUserFullName()) + "\n" + StringFormater.capitalizeWord(selectedvehiclemodel.getVehicleBrand() + " " + selectedvehiclemodel.getVehicleModel()));
                    checkTripStarted();
                    return;
                }
            }

            showDialogtoSelectCar();
        }
    }

    private void showDialogtoSelectCar() {

        Log.e(TAG, "Showing dialog to select car");
        // If the dialog is already showing, no need to show the popup again.
        if (selectCarDialog != null && selectCarDialog.isShowing()) {
            return;
        }

        //show Dialog to select the vehicle from list of vehicles
        selectCarDialog = new SelectCarDialog(context);
        FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(SelectCarDialog.class.getSimpleName());
        selectCarDialog.show();
    }



    private void checkStartTripButton() {

        if (foregroundService == null){
            Log.e("Foreground","Foreground is null");
        }else
        {
            Log.e("Foreground","Foreground is not null");

        }

        showHideProgressLayout(false,false);

        if(foregroundService==null ||foregroundService.currentTrip == null) {
            fragmentHomeBinding.startTripBtn.setText("Start tracking your trip");
            fragmentHomeBinding.startTripBtn.setBackgroundColor(context.getColor(R.color.colorPrimary));
            fragmentHomeBinding.startTripBtn.setTextColor(context.getColor(R.color.colorPrimaryDark));
        }else{
            fragmentHomeBinding.startTripBtn.setText("Stop tracking your trip");
            fragmentHomeBinding.startTripBtn.setBackgroundColor(context.getColor(R.color.red));
            fragmentHomeBinding.startTripBtn.setTextColor(context.getColor(R.color.white));
        }

    }

    boolean isHistoricalTripData = false;
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.bluetooth_icon) {
//            Toast.makeText(context, "Clicked Bluetooth Icon", Toast.LENGTH_SHORT).show();


            return;
        }

        if (v.getId() == R.id.historical) {
//            fetchTripsAndDisplay();
//            fetchTrips();
            records.clear();

            String sinceDateSTring = user.getCreatedOn();

            try {
                // Parse the input date string
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                Date date = inputFormat.parse(sinceDateSTring);

                // Format the date in the desired format
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
                String sinceDate = outputFormat.format(date);

                fragmentHomeBinding.sinceInfo.setText("Since "+sinceDate);


            } catch (ParseException e) {
                e.printStackTrace();
            }




            isHistoricalTripData=true;
            fetchTodayDateAndFetchTrips();



            fragmentHomeBinding.fuelSavingText.setTextColor(context.getColor(R.color.white));
            fragmentHomeBinding.historical.setBackground(getResources().getDrawable(R.drawable.historical));
            fragmentHomeBinding.savingBox.setBackground(getResources().getDrawable(R.drawable.money2));
            fragmentHomeBinding.emptyView.setBackgroundColor(getResources().getColor(R.color.historicalBack));

            fragmentHomeBinding.historical.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

            fragmentHomeBinding.levelInfo.setVisibility(View.VISIBLE);
            fragmentHomeBinding.sinceInfo.setVisibility(View.VISIBLE);
            fragmentHomeBinding.data2.setVisibility(View.GONE);
            fragmentHomeBinding.data3.setVisibility(View.VISIBLE);

            // Set background drawable
            fragmentHomeBinding.today.setBackground(null);

            fragmentHomeBinding.today.setTextColor(getResources().getColor(R.color.white));

            return;
        }

        if (v.getId() == R.id.today) {

            isHistoricalTripData =false;
            records.clear();
//            Toast.makeText(context, "Clicked historical", Toast.LENGTH_SHORT).show();
            // Set background color

// Set background drawable
//            fetchTrips();
//            fetchTripsAndDisplay();

            fetchTodayDateAndFetchTrips();


            fragmentHomeBinding.today.setBackground(getResources().getDrawable(R.drawable.hoy));
            fragmentHomeBinding.savingBox.setBackground(getResources().getDrawable(R.drawable.money));
            fragmentHomeBinding.emptyView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            fragmentHomeBinding.today.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            fragmentHomeBinding.levelInfo.setVisibility(View.GONE);
            fragmentHomeBinding.sinceInfo.setVisibility(View.GONE);
            fragmentHomeBinding.data2.setVisibility(View.VISIBLE);
            fragmentHomeBinding.data3.setVisibility(View.GONE);
            fragmentHomeBinding.historical.setBackground(null);
            fragmentHomeBinding.historical.setTextColor(getResources().getColor(R.color.white));

            return;
        }

        if (v.getId() == R.id.gen_rules){

            General_RulesFragment rulesFragment = new General_RulesFragment();

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.mainFrameLayout, rulesFragment)
                    .addToBackStack("General_RulesFragment")  // Optional: Add transaction to the back stack
                    .commit();

        }



    }






    private boolean enableBluetooth() {


        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(requireContext(), "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                // Bluetooth is not enabled
                bluetoothAdapter.enable();
                askPermissionForAndroidS();

            } else {
                // Bluetooth is enabled
                // Toast.makeText(requireContext(), "Bluetooth is already enabled", Toast.LENGTH_SHORT).show();
            }
        }


        return true;

    }

    private void fetchAlertsAndDisplay() {
        List<TripAlert> records = databaseHelper.fetchAllTripAlerts();
        Collections.reverse(records);

    }

    //    List<TripRecord> records ;
    List<TripRecord> records = new ArrayList<>();
    private void fetchTripsAndDisplay() {
        records = new DatabaseHelper(context).fetchAllTripRecords();
//If the current trip is not null,we will add the trip to the list.
        if (currentTrip != null) {
            records.add(currentTrip);
        }
        Collections.reverse(records);


        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        fragmentHomeBinding.tripListRecyclerview.setLayoutManager(layoutManager);
        fragmentHomeBinding.tripListRecyclerview.setAdapter(new SingleDayTripAdapter(records, context,gamelevelArray));



    }

    private void changeBackgroundforSelectedDriveMode(final TextView textView) {

        textView.setBackground(context.getResources().getDrawable(R.drawable.buttoncolor));

        boolean isDriveMode = myPreferenece.getSharedPreferences().getBoolean(MyPreferenece.DRIVING_MODE, true);


    }


    private boolean performBluetoothOps() {

        Set<BluetoothDevice> pairedDevices = getBluetoothDevices();
        for (BluetoothDevice bluetoothDevice : pairedDevices) {

            if (bluetoothDevice.getName() != null && bluetoothDevice.getName().equals("OBDII")) {
                myPreferenece.saveStringData(ConfigActivity.BLUETOOTH_LIST_KEY, bluetoothDevice.getAddress());
                connectToBluetoothDevice(bluetoothDevice);
                return true;
            }
        }

        fragmentHomeBinding.setBlueToothDrawable(getResources().getDrawable(R.drawable.bluetooth_red));
        BluetoothHelperClass.showDialogToPairObd2Device(context);

        return false;

    }



    boolean bluetoothPermissionRequested =false;
    BroadcastReceiver foregroundServiceBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            connectWithForegroundService();


            //User tapped on start trip but there is not internet connection
            if (intent.getAction().equals(context.getString(R.string.no_internet_trip_will_start_after_getting_connection))) {
                checkStartTripButton();
                Toast.makeText(context, context.getString(R.string.no_internet_trip_will_start_after_getting_connection), Toast.LENGTH_LONG).show();
            }

            //User tapped on stop trip but there is not internet connection
            if (intent.getAction().equals(context.getString(R.string.no_internet_trip_will_end_after_getting_connection))) {
                checkStartTripButton();
                Toast.makeText(context, context.getString(R.string.no_internet_trip_will_end_after_getting_connection), Toast.LENGTH_LONG).show();
            }
            if (intent.getAction().equals(context.getString(R.string.connect_bluetooth))) {
                if(!bluetoothPermissionRequested) {
                    bluetoothPermissionRequested =true;
                    askPermissionForAndroidS();
                }
            }
            if (intent.getAction().equals(context.getString(R.string.bluetooth_status_action))) {

                //BluetoothHelperClass.showTriponeKmDialog(context, getLayoutInflater(), "", "Your Driving log has now started. Have a safe drive with gypsee.");
                String status = intent.getStringExtra(context.getString(R.string.status_broadcast));
                if (status.equals(context.getString(R.string.connected_broadcast))) {
                    fragmentHomeBinding.setBlueToothDrawable(context.getDrawable(R.drawable.bluetooth_green));

                    ArrayList<Vehiclemodel> vehiclemodelArrayList = new DatabaseHelper(context).fetchAllVehicles();
                    if (vehiclemodelArrayList.size() != 0){
                        selectedvehiclemodel = vehiclemodelArrayList.get(0);
                    }
                } else if (status.equals(context.getString(R.string.connecting_broadcast))) {
                    fragmentHomeBinding.setBlueToothDrawable(context.getDrawable(R.drawable.bluetooth_red));
                } else {
                    fragmentHomeBinding.setBlueToothDrawable(context.getDrawable(R.drawable.bluetooth_red));
                    selectedvehiclemodel = null;
                    checkStartTripButton();
                }
            }


            if (intent.getAction().equals(context.getString(R.string.current_trip_action))) {
                TripRecord receivedTrip = intent.getParcelableExtra(context.getString(R.string.current_trip_broadcast));
                Log.e(TAG, "Received trip distance: " + receivedTrip.getDistanceCovered());
                currentTrip = receivedTrip;
                try {
                    tripDistanceFromGPSm = Double.parseDouble(receivedTrip.getDistanceCovered());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (isTriponeKmPopUpNotShown && Float.parseFloat(receivedTrip.getDistanceCovered()) >= 1) {
                    isTriponeKmPopUpNotShown = false;
                }

                checkStartTripButton();

            }
            if (intent.getAction().equals(context.getString(R.string.end_trip_action))) {
                Log.e(TAG, "onReceive:+trip ended " );
                resetAllValues();
                checkStartTripButton();
            }


            if (intent.getAction().equals("dialogNotification")){
                if (intent.getStringExtra("targetUrl") != null && intent.getStringExtra("notificationBody") != null){
                    notificationDialog(intent.getStringExtra("notificationBody"), intent.getStringExtra("targetUrl"));
                }
            }
        }
    };

    private void connectWithForegroundService() {
        //bind foreground service
        if (foregroundService==null)
        {
            Intent servicForeIntent = new Intent(context, ForegroundService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.bindService(servicForeIntent, foregroundServiceConnection, BIND_AUTO_CREATE);
            }
        }

    }

    private void askPermissionForAndroidS() {
        checkStartTripButton();
        if(isAdded()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }





    // Here we are connecting the bluetooth on a different thread, because it frezes the entire screen.

    ConnectThread connectThread;

    private void connectToBluetoothDevice(BluetoothDevice bluetoothDevice) {

        if (connectThread != null && connectThread.isAlive()) {
            return;
        }
        connectThread = new ConnectThread(bluetoothDevice, blueToothConnectionInterface, context);
        connectThread.start();
    }


    private Set<BluetoothDevice> getBluetoothDevices() {
        // get Bluetooth device
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter.isEnabled()) {
            btAdapter.startDiscovery();
        } else {
            btAdapter.enable();
        }

        //Make some Bluetooth operations
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        return pairedDevices;
    }



    private void parseFetchEmergencyContacts(String response) throws JSONException{

        JSONObject jsonResponse = new JSONObject(response);
        DatabaseHelper helper = new DatabaseHelper(context);
        helper.deleteTable(DatabaseHelper.EMERGENCY_CONTACT_TABLE);

        JSONArray emergencyContacts = jsonResponse.getJSONArray("emergencyContacts");
        ArrayList<MemberModel> contacts = new ArrayList<>();

        for (int i=0; i<emergencyContacts.length(); i++){

            JSONObject contact = emergencyContacts.getJSONObject(i);

            String city = contact.has("city") ? contact.getString("city") : "";
            String id = contact.has("id") ? contact.getString("id") : "";
            String relation = contact.has("relation") ? contact.getString("relation") : "";
            String userEmail = contact.has("userEmail") ? contact.getString("userEmail") : "";
            String userName = contact.has("userName") ? contact.getString("userName") : "";
            String userPhoneNumber = contact.has("userPhoneNumber") ? contact.getString("userPhoneNumber") : "";

            contacts.add(new MemberModel(id, userName, city, userPhoneNumber, userEmail, relation));

        }
        helper.insertEmergencyContacts(contacts);
    }





    private void parseFetchRegisteredDevices(String response) throws JSONException{
        JSONObject jsonResponse = new JSONObject(response);

        JSONArray userRegisteredDevices = jsonResponse.getJSONArray("userRegisteredDevices");
        ArrayList<BluetoothDeviceModel> deviceModelArrayList = new ArrayList<>();

        new DatabaseHelper(context).deleteTable(DatabaseHelper.REGISTERED_DEVICE_TABLE);

        for (int i=0; i<userRegisteredDevices.length(); i++){

            JSONObject registeredDevice = userRegisteredDevices.getJSONObject(i);
            deviceModelArrayList.add(new BluetoothDeviceModel(
                    registeredDevice.has("id") ? registeredDevice.getString("id") : "",
                    registeredDevice.has("deviceName") ? registeredDevice.getString("deviceName") : "",
                    "",
                    registeredDevice.has("bluetoothName") ? registeredDevice.getString("bluetoothName") : "",
                    registeredDevice.has("bluetoothType") ? registeredDevice.getString("bluetoothType") : "",
                    registeredDevice.has("bluetoothProfilesSupported") ? registeredDevice.getString("bluetoothProfilesSupported") : "",
                    registeredDevice.has("macAddress") ? registeredDevice.getString("macAddress") : "",
                    registeredDevice.has("imageUrl") ? registeredDevice.getString("imageUrl") : "",
                    registeredDevice.has("lastConnectedTime") ? registeredDevice.getString("lastConnectedTime") : "",
                    registeredDevice.has("createdOn") ? registeredDevice.getString("createdOn") : "",
                    registeredDevice.has("lastUpdatedOn") ? registeredDevice.getString("lastUpdatedOn") : "",
                    false,
                    null
            ));
        }
        new DatabaseHelper(context).insertRegisteredDevices(deviceModelArrayList);
    }



    private void parseFetchDeviceCategories(String response) throws JSONException{
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray devicesCategories = jsonResponse.getJSONArray("devicesCategories");
        ArrayList<BluetoothDeviceModel> deviceCategoryList = new ArrayList<>();
        new DatabaseHelper(context).deleteTable(DatabaseHelper.ALL_DEVICES_TABLE);

        for (int i=0; i<devicesCategories.length(); i++){

            JSONObject object = devicesCategories.getJSONObject(i);
            String category = object.getString("name");
            JSONArray devices = object.getJSONArray("devices");

            for (int j=0; j<devices.length(); j++){
                ArrayList<String> instructions = new ArrayList<>();
                JSONObject device = devices.getJSONObject(j);
                DeviceInformationModel informationModel;

                String id = device.has("id") ? device.getString("id") : "";
                String deviceName = device.has("name") ? device.getString("name") : "";
                String deviceImage = device.has("imageUrl") ? device.getString("imageUrl") : "";

                JSONArray instructionsArray = device.getJSONArray("instructions");
                //using only first element
                if (instructionsArray.length()>0){
                    JSONArray instructionsData = instructionsArray.getJSONObject(0).getJSONArray("instructions");
                    for (int a=0; a<instructionsData.length(); a++){
                        instructions.add(instructionsData.getString(a));
                    }
                    informationModel = new DeviceInformationModel("",
                            instructionsArray.getJSONObject(0).getString("imageUrl"),
                            deviceName,
                            instructions
                    );
                } else {
                    informationModel = new DeviceInformationModel("", "", "", instructions);
                }

                deviceCategoryList.add(new BluetoothDeviceModel(
                        id,
                        deviceName,
                        category,
                        deviceName,
                        "",
                        "",
                        "",
                        deviceImage,
                        "",
                        "",
                        "",
                        false,
                        informationModel
                ));
            }
        }
        new DatabaseHelper(context).insertCategoryDevices(deviceCategoryList);

    }











    private TripRecord currentTrip;
    int maxSpeed = 90;
    String maximumspeed = "01 km/hr";
    int currentRPM = 0;
    int maxEngineRPM = 3000, maximumRPM = 0;
    private TripAlert speedAlert, engineAlert;


    private LogCSVWriter myCSVWriter;

    private void startLiveData() {
        Log.e(TAG, "Starting live data..");


        doBindService();
        //Directly starting the GPS to get the locations for every 5meters distance and 1 sec.
//        fragmentHomeBinding.logoLayout.setEnabled(false);
    }

    String csVfileName = "";

    private void initMyCsvWriter() {
        if (myCSVWriter == null && isServiceBound)
            try {
                // Create the CSV Logger
                long mils = System.currentTimeMillis();

                SimpleDateFormat sdf = new SimpleDateFormat("_dd_MM_yyyy_HH_mm_ss");
                String prefix = "prod_";
                if (getResources().getString(R.string.base_url).contains("qa")) {
                    prefix = "qa_";
                }
                csVfileName = prefix + currentTrip.getId() + "_" + sdf.format(new Date(mils)) + ".csv";
                myCSVWriter = new LogCSVWriter(csVfileName,
                        "GypseeVehicleLogs", context
                );
            } catch (Exception e) {
                Log.e(TAG, "Can't enable logging to file.", e);
            }
    }


    private boolean isServiceBound = false;

    private Vehiclemodel selectedvehiclemodel;

    private void doBindService() {
       /* if (!isServiceBound) {

            // start command execution
            //new Handler().postDelayed(mQueueCommands, 1000);

            //Below code is for when the bluetooth is not connected even after 7 seconds
        }*/
        Log.e(TAG, "Binding OBD service..");

        Intent serviceIntent = new Intent(context, ObdGatewayService.class);
        context.bindService(serviceIntent, serviceConn, BIND_AUTO_CREATE);
    }



    private AbstractGatewayService service;
    //If the value becomes 3
    int incrementValueForVINCheck = 0;
    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            Log.e(TAG, className.toString() + " service is bound");
            isServiceBound = true;

            incrementValueForVINCheck = 0;
            service = ((AbstractGatewayService.AbstractGatewayServiceBinder) binder).getService();
            service.setContext(context);
//            fragmentHomeBinding.progressLayout.setVisibility(View.VISIBLE);
            Log.e(TAG, "Starting service live data");
            try {
                commandResult.clear();
                service.startService(bluetoothSocket, false, false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //If the service is bound then only we need to ask to connect to the vehicle.
                        if (isServiceBound) {
                            addDummyValues();
//                            fragmentHomeBinding.logoLayout.setEnabled(true);
                        }
                    }
                }, 15000);
            } catch (IOException ioe) {
                Log.e(TAG, "Failure Starting live data");
                stopLiveData();
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
        }
    };



    private void callBackgroundService(int value) {
        //Intent intent = new Intent(context, TripBackGroundService.class);
        Intent intent = new Intent(context, TripForeGroundService.class);

        switch (value) {
            // Trip history update. Sending to service.
            case 0:


                if (currentTrip == null) {
                    return;
                }

                intent.putExtra(TripBackGroundService.PURPOSE, "Trip History");
                intent.putExtra("userTripId", currentTrip.getId());
                //intent.putExtra("FileName", csVfileName);
                intent.putExtra("FileName", csVfileName);

                if (myCSVWriter != null) {
                    myCSVWriter.closeLogCSVWriter();
                    myCSVWriter = null;
                    initMyCsvWriter();

                }
                break;

            //trip update
            case 1:
            case 2:
                if (currentTrip != null && currentTrip != null) {
                    intent.putExtra("Location", endLocation);
                    commandResult.put("maxEngineRPM", String.valueOf(maximumRPM));
                    commandResult.put("maximumspeed", maximumspeed);

                    if (commandResult.get("ENGINE_RUNTIME") == null || commandResult.get("ENGINE_RUNTIME").equals("")) {
                        commandResult.put("ENGINE_RUNTIME", currentTrip.getEngineRuntime());
                    }
                    intent.putExtra("commandResult", commandResult);
                    intent.putExtra("userTripDetails", currentTrip.getId());
                    intent.putExtra(Vehiclemodel.class.getSimpleName(), selectedvehiclemodel);
                    intent.putExtra(TripBackGroundService.PURPOSE, value == 1 ? "Trip Update" : "End Trip");
                }
                break;

            case 3:
                if (getDistancePostCC() > 1 && currentTrip != null) {
                    intent.putExtra("Location", endLocation);
                    intent.putExtra("commandResult", commandResult);
                    intent.putExtra("userTripDetails", currentTrip.getId());
                    intent.putExtra(Vehiclemodel.class.getSimpleName(), selectedvehiclemodel);
                    intent.putExtra(TripBackGroundService.PURPOSE, "Update vehicle");

                    //Calling update vehicle every 20 min
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (currentTrip != null)
                                callBackgroundService(3);
                        }
                    }, 600000);
                }
                break;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            context.bindService(intent, foregroundServiceConnection, BIND_AUTO_CREATE);
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }

        //If engine runtime is not coming, we will pass trip time as engine run time. that's why we are removing here.
        commandResult.remove("ENGINE_RUNTIME");
    }


    ForegroundService foregroundService;
    private final ServiceConnection foregroundServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            foregroundService = ((ForegroundService.ForegroundServiceBinder) service).getService();
            checkStartTripButton();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            foregroundService = null;
            connectWithForegroundService();
        }
    };

    private final Runnable mQueueCommands = new Runnable() {
        public void run() {
            if (service != null && service.isRunning() && service.queueEmpty()) {
                queueCommands();
                Log.e(TAG, " mQueueCommands Runnbale : " + isServiceBound);
            }
            //THis was running in background earlier. thats the condition.
            if (isServiceBound) {
                new Handler().postDelayed(mQueueCommands, 500);
            }
        }
    };

    private void doUnbindService() {

        if (isServiceBound) {
            if (service.isRunning()) {
                service.stopService();
                updateBluetoothImageStatus(false);
//                btStatusTextView.setText(getString(R.string.status_bluetooth_ok));
            }
            Log.e(TAG, "Unbinding OBD service..");
            context.unbindService(serviceConn);
            isServiceBound = false;
            fragmentHomeBinding.setOdbDrawable(getResources().getDrawable(R.drawable.obd_disconnected));

//            obdStatusTextView.setText(getString(R.string.status_obd_disconnected));
        }
    }

    boolean isStarting = true;

    private void queueCommands() {

        Log.e(TAG, "queueCommands called : ");
        if (isServiceBound) {
            for (ObdCommand Command : ObdConfig.getCommands(isStarting, false, (currentTrip == null || commandResult.get("VIN") == null), isThirtySec, isOneMinute, context)) {
                service.queueJob(new ObdCommandJob(Command));

                if (currentTrip != null) {
                    isOneMinute = !isOneMinute && isOneMinute;
                    isThirtySec = !isThirtySec && isThirtySec;
                }
            }
        }
    }

    boolean isThirtySec = true, isOneMinute = true;

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
            uploadGpCalcTimer = new Timer();
        }

        uploadGpCalcTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (currentTrip != null) {
                                callServer(getString(R.string.gpsTripDistance), "Upload GPS for trip calculation", 10);
                            }

                        }
                    });
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
            }
        }, 100, 30000);
    }


    private HashMap<String, String> commandResult = new HashMap<>();


    private static final int PERMISSION_REQUEST_CODE = 10003;
    private static final int REQUEST_ENABLE_BT = 10004;

    private void stopLiveData() {

        //If vehicle health score is open, it will chnagethe bluetoothStatus
        Intent intent = new Intent("NewPerformance");
        // You can also include some extra data.
        intent.putExtra("BlueToothStatus", false);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

//        fragmentHomeBinding.logoLayout.setEnabled(false);
        doUnbindService();
        initViews();
        if (sensorManager != null)
            sensorManager.unregisterListener(orientListener);


    }

    ArrayList<LatLongModel> latLongModelArrayList = new ArrayList<>();
    ArrayList<LatLongModel> templatLongModelArrayList = new ArrayList<>();

    private void resetAllValues() {
        speeds.clear();
        speedsWithIntervals.clear();
        gpsWithIntervals.clear();
        emptyValues.clear();
        isVinCheckedAlready = false;
        isTriponeKmPopUpNotShown = true;
        selectedvehiclemodel = null;
        tripDistanceFromGPSm = 0;
        drivingAlerts = new JsonArray();
        maximumspeed = "01 km/hr";
//        fragmentHomeBinding.logoLayout.setEnabled(true);
        maximumRPM = 0;
        currentTrip = null;
        isAlreadyRPMNotShown = true;

//        stopForeGroundService();
    }

    private void checkAllDriveAlerts() {

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        int i = 0;
        if (engineAlert != null) {
            databaseHelper.insertTripAlert(engineAlert);
            addTripAlertToArray(engineAlert);
            engineAlert = null;
            i = i + 1;
        }
        if (speedAlert != null) {
            databaseHelper.insertTripAlert(speedAlert);
            addTripAlertToArray(speedAlert);
            speedAlert = null;
            i = i + 1;

        }

        if (temporaryAccAlert != null) {
            databaseHelper.insertTripAlert(temporaryAccAlert);
            addTripAlertToArray(temporaryAccAlert);
            temporaryAccAlert = null;
            i = i + 1;

        }
        if (tempDecAlert != null) {
            databaseHelper.insertTripAlert(tempDecAlert);
            addTripAlertToArray(tempDecAlert);

            tempDecAlert = null;
            i = i + 1;

        }

//        if (fragmentHomeBinding.alertsLayout.alerts.getBackground() != null)
//            fetchAlertsAndDisplay();
        currentTrip.setAlertCount(currentTrip.getAlertCount() + i);
    }

    @Override
    public void onDestroy() {
        performExitCleanup();
        super.onDestroy();
        Log.e(TAG, "On Destroy called");
    }



    private String LookUpCommand(String txt) {
        for (AvailableCommandNames item : AvailableCommandNames.values()) {
            if (item.getValue().equals(txt)) return item.name();
        }
        return txt;
    }

    private HashMap<String, String> emptyValues = new HashMap<>();

    int unableToConnect = 0;

    private void stateUpdate(ObdCommandJob job) {

        final String cmdName = job.getCommand().getName();
        String cmdResult = "";
        final String cmdID = LookUpCommand(cmdName);
        Log.e(TAG, "State update called");

        if (job.getState().equals(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR)) {
            cmdResult = job.getCommand().getResult();
            if (cmdResult != null) {

                Log.e(TAG, "Execution error");
                if (cmdResult.equalsIgnoreCase("NODATA")) {
                    emptyValues.put(cmdID, cmdResult);
                }

                if (cmdID.equals("ENGINE_RPM")) {

                    if (rpmDialog != null && rpmDialog.isShowing()) {
                        return;
                    }

                    switch (cmdResult) {
                        case "NODATA":
                        case "...UNABLETOCONNECT":
                            rpmDialog = BluetoothHelperClass.showSinpleWarningDialog(context, getLayoutInflater(), "Dear Customer!", "Please switch ON the ignition of your car.");

                            break;
                        case "INIT...BUSERROR":
                            rpmDialog = BluetoothHelperClass.showSinpleWarningDialog(context, getLayoutInflater(), "Dear Customer!", "Connect with Gypsee technical support.");
                            break;
                    }

                }

                if (cmdID.equals("ENGINE_RPM")) {
                    commandResult.put(cmdID, cmdResult);
                    checkPerformanceFragmentVisible(cmdID, cmdName, cmdResult);
                    changeProtocol();
                    new Handler().postDelayed(mQueueCommands, 1000);

                }
                return;
            }
        } else if (job.getState().equals(ObdCommandJob.ObdCommandJobState.BROKEN_PIPE)) {
            Log.e(TAG, "State update Broken pipe");
            doUnbindService();
            performTaskOnBrokenPipe();
            return;
        } else if (job.getState().equals(ObdCommandJob.ObdCommandJobState.NOT_SUPPORTED)) {
            cmdResult = getString(R.string.status_obd_no_support);
        } else {
            cmdResult = job.getCommand().getFormattedResult();
            if (cmdID.equalsIgnoreCase("ENGINE_RPM")) {
                if (isStarting)
                    new Handler().postDelayed(mQueueCommands, 1000);
                isStarting = false;

                Log.e(TAG, "Coming here : " + cmdName);
            }

            if (cmdID.equals("TROUBLE_CODES")) {
                //Here removing the C, B, U trouble codes.

                cmdResult = BluetoothHelperClass.parseTroubleCodes(cmdResult);
            }
        }
        Log.e(TAG, "Command ID = " + cmdID + " - Command Name = " + cmdName + " - Command Result = " + cmdResult);

        //isStarting = false;

        if (cmdResult == null) {
            cmdResult = "null";
        }
        if (cmdResult.equals("NA") || cmdResult.equalsIgnoreCase("null")) {
            emptyValues.put(cmdID, cmdResult);
            //Check CMD ID is not VIN. Because, we are checking the car VIN. If the VIN number comes, then we can compare the cars
            if (!cmdID.equals("VIN"))
                return;
        } else {
            emptyValues.remove(cmdID);
        }

        //If the vin is empty, we will not add VIN to the Command result map. SO that wrong things don't happen.

        if (cmdID.equals("VIN") && (cmdResult.contains("@") || cmdResult.equals("null") || cmdResult.equals("NA"))) {
            return;
        }

        commandResult.put(cmdID, cmdResult);
        if (myCSVWriter != null && endLocation != null)
            myCSVWriter.writeLineCSV(endLocation.getLatitude(), endLocation.getLongitude(), System.currentTimeMillis(), commandResult);

        checkPerformanceFragmentVisible(cmdID, cmdName, cmdResult);
        updateViews(cmdID, cmdResult);
        updateTripStatistic(job, cmdID);

        //Do not write to CSV until u get the permission of adding and reading external storage

        writeToCSVMethod();
    }


    private void changeProtocol() {
        //Changing the protocol to the correct Protocol until we get the correct protocol
        if (isStarting)
            unableToConnect++;
        if (unableToConnect > 1) {
            if (selectedProtocolPosition == protocolStrings.size() - 1) {
                selectedProtocolPosition = 0;
            } else {
                selectedProtocolPosition = selectedProtocolPosition + 1;
            }

            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(ConfigActivity.PROTOCOLS_LIST_KEY, protocolStrings.get(selectedProtocolPosition)).apply();
            service.queueJob(new ObdCommandJob(new SelectProtocolCommand(ObdProtocols.valueOf(protocolStrings.get(selectedProtocolPosition)))));
            service.queueJob(new ObdCommandJob(new RPMCommand()));
            unableToConnect = 0;
            Log.e(TAG, "Protocol : " + protocolStrings.get(selectedProtocolPosition));
        } else
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isStarting) {

                        service.queueJob(new ObdCommandJob(new SpeedCommand()));
                        service.queueJob(new ObdCommandJob(new RPMCommand()));
                    }
                }
            }, 2000);

    }

    private void performTaskOnBrokenPipe() {

        //On broken pipe, we will check first wether bluetooth is enabled or not. If enabled, we will unbind and then will try to connect with device.
        // If the device is not paired we will end trip.

        if (enableBluetooth()) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (performBluetoothOps()) {
                        //Do nothing here
                    } else {
                        stopLiveData();
                    }
                }
            }, 3000);

        }
        //If bluetooth is off, we will end the trip.
        else {
            stopLiveData();
        }


    }

//    private void initializeConnectedToCar() {
//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean("connectedToCar", false);
//        editor.apply(); // Use apply() to save changes asynchronously
//    }
//
//    private void updateConnectedToCar(boolean connectedToCar) {
//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean("connectedToCar", connectedToCar);
//        editor.apply(); // Use apply() to save changes asynchronously
//    }

    private boolean getConnectedToCar() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("connectedToCar", false); // false is the default value if not found
    }

    private void updateConnected(boolean value) {
        MyCarsListFragment carsListFragment = (MyCarsListFragment) getChildFragmentManager().findFragmentByTag("MyCarsListFragment");
        if (carsListFragment != null) {
//            alertsFragment.updateSwitches(mute);
            carsListFragment.updateConnctedValue(value);

        }
    }


    //Below method will be called after selecting a car.
    private void checkTripStarted() {


        //String RPM = commandResult.get("ENGINE_RPM");
        // boolean isRpmEmpty = (RPM == null || RPM.contains("UNABLE") || RPM.equalsIgnoreCase("NODATA") || RPM.isEmpty());

        Log.e(TAG, "Starting the trip");

//        carConnect = true;
//        updateConnectedToCar(carConnect);
//        updateConnected(carConnect);



        if (currentTrip == null /*&& selectedvehiclemodel != null*/) {
            //Setting user name and connected vehicle.
            commandResult.put("VEHICLE_ID", selectedvehiclemodel.getUserVehicleId());
            Log.e(TAG, "calling start trip 1962");

//            myPreferenece.setConnectedToCar(isServiceBound);
//            updateConnectedToCar(isServiceBound);
            updateConnected(isServiceBound);
            callServer(getString(R.string.tripstart_url), "Start trip ", 1);
            fragmentHomeBinding.fuelSavingText.setText("0.0");

        } else {
            Log.e(TAG, "Current trip not null 1966");

        }
    }

    private void writeToCSVMethod() {

        if (service != null && service.isRunning() && myCSVWriter != null) {
            double lat = 0;
            double lon = 0;
            double alt = 0;
            if (endLocation != null) {
                lat = endLocation.getLatitude();
                lon = endLocation.getLongitude();
                alt = endLocation.getAltitude();
                myPreferenece.setLatLng(endLocation.getLatitude() + "/" + endLocation.getLongitude());
            }

            // Write the current reading to CSV
            Map<String, String> temp = new HashMap<String, String>(commandResult);
            ObdReading reading = new ObdReading(lat, lon, alt, System.currentTimeMillis(), temp);

        /*    if (tripmodelclass != null)
                tripmodelclasses.add(tripmodelclass);*/
        }

    }

    private void checkAlertsCount() {

        List<TripAlert> alerts = databaseHelper.fetchAllTripAlerts();

//        if (alerts.size() > 0) {
//            fragmentHomeBinding.alertsLayout.setAlertsCount(String.valueOf(alerts.size()));
//            fragmentHomeBinding.alertsLayout.countTv.setVisibility(View.VISIBLE);
//        } else {
//            fragmentHomeBinding.alertsLayout.countTv.setVisibility(View.GONE);
//
//        }
    }


    private void checkPerformanceFragmentVisible(String cmdID, String cmdName, String cmdResult) {
        Intent intent = new Intent("Performance");
        // You can also include some extra data.
        Bundle b = new Bundle();
        b.putString("cmdID", cmdID);
        b.putString("cmdName", cmdName);
        b.putString("cmdResult", cmdResult);
        intent.putExtras(b);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private boolean checkPerformanceFragmentVisible(String TAG) {

        Fragment performanceFragment = ((AppCompatActivity) context).getSupportFragmentManager().findFragmentByTag(TAG);
        if (performanceFragment != null && performanceFragment.isVisible()) {
            return true;
            //DO STUFF
        } else {
            //Whatever
            return false;
        }


    }


    private ArrayList<Date> speedsWithIntervals = new ArrayList<>();
    private ArrayList<Date> gpsWithIntervals = new ArrayList<>();
    private ArrayList<Integer> speeds = new ArrayList<>();

    TripAlert temporaryAccAlert, tempDecAlert;

    JsonArray drivingAlerts = new JsonArray();
    JsonArray tempdrivingAlerts = new JsonArray();

    JsonArray angles = new JsonArray();

    private void updateSpeed(String cmdResult) {

        if (cmdResult.isEmpty() || cmdResult.equals("NA") || currentTrip == null) return;
        int speed;
        if (cmdResult.contains("mph")) {
            double speedn = 1.60934 * Double.parseDouble(cmdResult.replace("mph", "").replace("0.00", "0"));
            speed = (int) Math.round(speedn);
        } else {
            speed = Integer.parseInt(cmdResult.replace("km/h", ""));
        }
        int timeDiff;
        speeds.add(speed);
        speedsWithIntervals.add(new Date());
        if (speedsWithIntervals.size() == 1)
            return;

        Log.e(TAG, "Speed currentTimeInSec : " + speedsWithIntervals.get(1) + "previousTimeInSec : " + speedsWithIntervals.get(0));
        timeDiff = TimeUtils.calcDiffTimeInSec(speedsWithIntervals.get(0), speedsWithIntervals.get(1));
        Log.e(TAG, "Speed Time difference is : " + timeDiff);
        if (timeDiff == 0) {
            // if (timeDiff == 0 || speed == 0) {


            speeds.remove(1);
            speedsWithIntervals.remove(1);
            Log.e(TAG, "Time Difference for speed 0 " + timeDiff);
            if (timeDiff > 20) {
                isReset = true;
                speedsWithIntervals.clear();
            }
            return;
        }

        long changeInSpeed = Math.round((speeds.get(1) - speeds.get(0)) / timeDiff);
        Log.e(TAG, "changeInSpeed : " + changeInSpeed);

        if (currentTrip == null /*|| changeInSpeed > 25 || changeInSpeed < -30 || speed > 200*/) {
            speeds.clear();
            speedsWithIntervals.clear();
            return;
        }

        if (changeInSpeed >= harshAccelaration) {

            Log.e(TAG, "Harsh Accelration");

            if (temporaryAccAlert == null) {
                temporaryAccAlert = new TripAlert("Harsh Accelaration", changeInSpeed + " km/hr", TimeUtils.getTimeIndhms(timeDiff), changeInSpeed * 0.028 + " m/sec2", new Date().getTime(), endLocation.getLatitude(), endLocation.getLongitude(), "");
                temporaryAccAlert.setInitialSpeed(speed);
            } else {
                int lateChangeInSpeed = (speed - temporaryAccAlert.getInitialSpeed()) / TimeUtils.calcDiffTimeInSec(new Date(temporaryAccAlert.getTimeStamp()), new Date());
                temporaryAccAlert.setTimeInterval(TimeUtils.calcDiffTime(temporaryAccAlert.getTimeStamp(), new Date().getTime()));
                temporaryAccAlert.setAlertValue(lateChangeInSpeed + " km/hr");
                temporaryAccAlert.setgForce(lateChangeInSpeed * 0.028 + " m/sec2");
            }
        }
        if (changeInSpeed <= harshDecelaration) {

            Log.e(TAG, "Harsh decelaration");
            if (tempDecAlert == null) {
                tempDecAlert = new TripAlert("Harsh Braking", changeInSpeed + " km/hr", TimeUtils.getTimeIndhms(timeDiff), changeInSpeed * 0.028 + " m/sec2", new Date().getTime(), endLocation.getLatitude(), endLocation.getLongitude(), "");
                tempDecAlert.setInitialSpeed(speed);

            } else {
                int lateChangeInSpeed = (speed - tempDecAlert.getInitialSpeed()) / TimeUtils.calcDiffTimeInSec(new Date(tempDecAlert.getTimeStamp()), new Date());
                tempDecAlert.setTimeInterval(TimeUtils.calcDiffTime(tempDecAlert.getTimeStamp(), new Date().getTime()));
                tempDecAlert.setAlertValue(lateChangeInSpeed + " km/hr");
                tempDecAlert.setgForce(lateChangeInSpeed * 0.028 + " m/sec2");
            }


        }
        if (changeInSpeed < harshAccelaration && changeInSpeed > harshDecelaration) {
            // It is normal breaking and normal Accelaration

            //Upload harsh Accelaration data
            if (temporaryAccAlert != null) {
                new DatabaseHelper(context).insertTripAlert(temporaryAccAlert);
                addTripAlertToArray(temporaryAccAlert);
                temporaryAccAlert = null;

//                if (fragmentHomeBinding.alertsLayout.alerts.getBackground() != null)
//                    fetchAlertsAndDisplay();


            }
            if (tempDecAlert != null) {
                //Upload harsh braking data
                new DatabaseHelper(context).insertTripAlert(tempDecAlert);
                addTripAlertToArray(tempDecAlert);
                tempDecAlert = null;
//                if (fragmentHomeBinding.alertsLayout.alerts.getBackground() != null)
//                    fetchAlertsAndDisplay();
            }


        }
        speeds.remove(0);
        speedsWithIntervals.remove(0);

        Log.e(TAG, "chnageInSpeed : " + changeInSpeed + " Acceleralation force : " + (changeInSpeed * 0.028) + "m/sec2");

        if (speed > Integer.parseInt(maximumspeed.replace(" km/hr", ""))) {
            maximumspeed = speed + " km/hr";
            currentTrip.setSpeedMax(speed);
        }

        if (speed < maxSpeed && speedAlert != null) {
            new DatabaseHelper(context).insertTripAlert(speedAlert);
            addTripAlertToArray(speedAlert);

            speedAlert = null;
//            if (fragmentHomeBinding.alertsLayout.alerts.getBackground() != null)
//                fetchAlertsAndDisplay();
        }
        if (speed > maxSpeed) {
            speedAlert = speedAlert == null ? new TripAlert("Overspeed", speed + " km/hr", "", "", new Date().getTime(), endLocation.getLatitude(), endLocation.getLongitude(), "") : speedAlert;

            if (speed > Integer.parseInt(speedAlert.getAlertValue().replace(" km/hr", ""))) {
                speedAlert.setAlertValue(speed + " km/hr");
            }


            speedAlert.setTimeInterval(TimeUtils.calcDiffTime(speedAlert.getTimeStamp(), new Date().getTime()));
            Toast.makeText(context, "Speed Alert", Toast.LENGTH_SHORT).show();
        }

        checkAlertsCount();

    }

    private void addTripAlertToArray(TripAlert temporaryAccAlert) {

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
        }
        drivingAlerts.add(jsonObject.toString());
        if (currentTrip != null && drivingAlerts.size() > 0) {
            tempdrivingAlerts.addAll(drivingAlerts);
            drivingAlerts = new JsonArray();
            callServer(getString(R.string.addDrivingAlerturl).replace("vehicleId", selectedvehiclemodel.getUserVehicleId()), "Add driving alerts", 6);
        }
    }

    private void callTodayTrips(String url, final String purpose, String periodFrom, String periodTo){

        fragmentHomeBinding.progressBar.setVisibility(View.VISIBLE);

        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);
        Call<ResponseBody> call;
        JsonObject jsonObject = new JsonObject();
        User user = myPreferenece.getUser();

//        call = apiService.getTripListBasic(user.getUserAccessToken());
        call = apiService.getTodayTripListBasic(user.getUserAccessToken(), 10, 0, true,periodFrom,periodTo);

        Log.e(TAG, purpose + " Input : " + jsonObject);


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

//                fragmentHomeBinding.progressBar.setVisibility(View.GONE);

                try {

//                    fragmentWalletBinding.progressBar.setVisibility(View.GONE);
//                    fragmentStoreMainBinding.progressLayout.setVisibility(View.GONE);

                    if (response.isSuccessful()){
                        Log.e(TAG, "Response is success");

                        ResponseBody responseBody = response.body();
                        if (responseBody == null){
                            return;
                        }


                        String responseStr = responseBody.string();
                        Log.e(TAG, purpose + " Resonse : " + responseStr);


                        parseFetchTripsResponse(responseStr);




                    } else {

                        Log.e(TAG, purpose + " Response is not successful");

                        String errResponse = response.errorBody().string();
                        Log.e("Error code 400", errResponse);
                        Log.e(TAG, purpose + "Response is : " + errResponse);
                        int responseCode = response.code();
                        if (responseCode == 401 || responseCode == 403) {
                            //include logout functionality
                            Utils.clearAllData(requireContext());
                            return;
                        }

                    }

                } catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Log.e(TAG, "error here since request failed");
                Log.e(TAG, t.getMessage());
                if (t.getMessage().contains("Unable to resolve host")) {
                    Toast.makeText(requireContext(), "Please Check your internet connection", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Please check your internet connection");
                } else {}

            }
        });



    }



    boolean isReset;
    int previousRPM = 0;
    ArrayList<Date> rpmTimeInterval = new ArrayList<>();
    ArrayList<Date> speedIntervals = new ArrayList<>();

    Dialog rpmDialog;
    boolean isAlreadyRPMNotShown = true;

    private void updateRPMAlert(String cmdResult) {
        if (isAlreadyRPMNotShown) {
            rpmDialog = BluetoothHelperClass.showSinpleWarningDialog(context, getLayoutInflater(), "Congratulations!", "Your car is compatible with Gypsee.");
            isAlreadyRPMNotShown = false;
        }
        if (cmdResult.isEmpty() || cmdResult.contains("NODATA") || currentTrip == null || endLocation == null) {
            return;
        }

        int rpm = Integer.parseInt(cmdResult.replace("RPM", ""));

        checkRpmChangingOrNot(rpm);

        if (rpm > maximumRPM)
            maximumRPM = rpm;
        if (rpm < maxEngineRPM && engineAlert != null) {
            new DatabaseHelper(context).insertTripAlert(engineAlert);
            addTripAlertToArray(engineAlert);
            engineAlert = null;
            //If Alerts is clcked, we will show the current alerts.
//            if (fragmentHomeBinding.alertsLayout.alerts.getBackground() != null)
//                fetchAlertsAndDisplay();
            checkAlertsCount();

        }
        if (rpm > maxEngineRPM) {

            engineAlert = engineAlert == null ? new TripAlert("High RPM", rpm + " RPM", "", "", new Date().getTime(), endLocation.getLatitude(), endLocation.getLongitude(), "") : engineAlert;

            if (rpm > Integer.parseInt(engineAlert.getAlertValue().replace(" RPM", ""))) {
                engineAlert.setAlertValue(rpm + " RPM");
            }
            engineAlert.setTimeInterval(TimeUtils.calcDiffTime(engineAlert.getTimeStamp(), new Date().getTime()));

            Toast.makeText(context, "RPM Alert", Toast.LENGTH_SHORT).show();
        }

        currentRPM = rpm;
    }

    private boolean checkRPMDialog(boolean isDismissDialog) {
        if (rpmDialog != null && rpmDialog.isShowing()) {
            if (isDismissDialog) {
                rpmDialog.dismiss();
            }
            return true;
        }
        return false;
    }

    private void checkRpmChangingOrNot(int rpm) {

        rpmTimeInterval.add(new Date());
        Log.e(TAG, "rpmTimeInterval size: " + rpmTimeInterval.size());

        if (rpm > 20 && previousRPM == rpm) {
            switch (rpmTimeInterval.size()) {
                case 1:
                    break;
                case 2:
                    int timeDiff = TimeUtils.calcDiffTimeInSec(rpmTimeInterval.get(0), rpmTimeInterval.get(1));
                    Log.e(TAG, "rpmTimeInterval : " + timeDiff);
                    rpmTimeInterval.remove(1);

                    if (timeDiff >= 60) {
                        isReset = true;
                        rpmTimeInterval.clear();
                    }

                    break;
            }
        } else {
            rpmTimeInterval.clear();
        }
        previousRPM = rpm;
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
        }
    }

    //private ArrayList<Tripmodelclass> tripmodelclasses = new ArrayList<>();

    private SensorManager sensorManager;
    String direction = "";

    private final SensorEventListener orientListener = new SensorEventListener() {
        //float[] accelorometerValues = new float[3];

        public void onSensorChanged(SensorEvent event) {

           /* if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelorometerValues = event.values;
                commandResult.put("Accelarometer_x", String.valueOf(accelorometerValues[0]));
                commandResult.put("Accelarometer_y", String.valueOf(accelorometerValues[1]));
                commandResult.put("Accelarometer_z", String.valueOf(accelorometerValues[2]));
               Log.e(TAG, "Accelorometer values :" + Arrays.toString(accelorometerValues));

            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                accelorometerValues = event.values;
                commandResult.put("MagnetometerValues", Arrays.toString(accelorometerValues));
                commandResult.put("Magnetometer_x", String.valueOf(accelorometerValues[0]));
                commandResult.put("Magnetometer_y", String.valueOf(accelorometerValues[1]));
                commandResult.put("Magnetometer_z", String.valueOf(accelorometerValues[2]));

               Log.e(TAG, "Magnetometer values :" + Arrays.toString(accelorometerValues));


            }*/
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

    Map<String, String> getDict(int keyId, int valId) {
        String[] keys = getResources().getStringArray(keyId);
        String[] vals = getResources().getStringArray(valId);

        Map<String, String> dict = new HashMap<String, String>();
        for (int i = 0, l = keys.length; i < l; i++) {
            dict.put(keys[i], vals[i]);
        }
        return dict;
    }


    Date tripStartTime;
    Timer startTripTimer;

    private void callServer(String url, final String purpose, final int value) {

        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);
        Call<ResponseBody> call;
        JsonObject jsonObject = new JsonObject();

        switch (value) {


            case 2:

                //Upload trouble code
                JsonArray jsonArrays = new JsonArray();
                for (String troubleCode :
                        troubleCodes) {
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

                if (troubleCodes == null) {
                    troubleCodes = new String[0];
                }

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
                call = apiService.uploadVehDetails(user.getUserAccessToken(), jsonObject);

                break;

            case 7:
//                fragmentHomeBinding.progressLayout.setVisibility(View.VISIBLE);
                call = apiService.getUserSubscription(user.getUserAccessToken(), true, true);
                break;

            case 8:
//                fragmentHomeBinding.progressLayout.setVisibility(View.VISIBLE);
                call = apiService.getDrivingAlerts(user.getUserAccessToken(), 10, 0, user.getUserId());
                break;
            case 9:
//                fragmentHomeBinding.progressLayout.setVisibility(View.VISIBLE);
                call = apiService.getTripListBasic(user.getUserAccessToken(), 10, 0, true);
                break;

            //10 --> is used to send GPs coordinates to the server.
            case 10:

               /* "data": [
            {
                "createdOn":"yyyy-MM-dd HH:mm:ss",
                    "latitude":0,
                    "longitude":0
            }
  ]*/

                jsonObject.addProperty("calculateDistance", true);
                jsonObject.addProperty("calculateSpeed", true);
                jsonObject.addProperty("tripID", currentTrip.getId());
                jsonObject.addProperty("userID", user.getUserId());
                JsonArray dataArray = new JsonArray();

                templatLongModelArrayList.addAll(latLongModelArrayList);

                Log.e(TAG, "Update trip distance: "+latLongModelArrayList.size());
                for (LatLongModel latLongModel : latLongModelArrayList) {
                    JsonObject dataObj = new JsonObject();
                    dataObj.addProperty("latitude", latLongModel.getLat());
                    dataObj.addProperty("longitude", latLongModel.getLongitude());
                    dataObj.addProperty("createdOn", latLongModel.getTime());
                    dataArray.add(dataObj);
                }
                jsonObject.add("data", dataArray);
                //If the array size is 2, then we need to call this.
                // So that he can atleast call the API and calcualte the distance.

                if (dataArray.size() > 2) {
                    call = apiService.uploadVehDetails(user.getUserAccessToken(), jsonObject);
                } else {
                    return;
                }
                latLongModelArrayList.clear();

                break;

            case 11:
                call = apiService.getVehicleAlerts(user.getUserAccessToken(), new HashMap<String, Object>());
                break;

            case 12:
                call = apiService.getVehicleAlerts(user.getUserAccessToken(), new HashMap<String, Object>());
                break;
            case 13:
                call = apiService.getVehicleAlerts(user.getUserAccessToken(), new HashMap<String, Object>());
                break;

            case 14:
                call = apiService.getObdDevice(user.getUserAccessToken());
                break;

            case 15:
                call = apiService.getVehicleHealthData(user.getUserAccessToken());
                break;

            case 16:
                call = apiService.exitTrainingMode(user.getUserAccessToken());
                break;

            case 17:
                call = apiService.getDocVehicleAlerts(user.getUserAccessToken(), true, false);
                break;

            case 18:
                //fetchRegisteredDevices
                call = apiService.getObdDevice(user.getUserAccessToken());
                break;

            case 19:
                //fetchDeviceCategories
            case 21: //fetch available subscriptions
                call = apiService.getObdDevice(user.getUserAccessToken());
                break;

            case 20:
                call = apiService.getEmergencyContacts(user.getUserAccessToken(), user.getUserId());
                break;

            default:
//                fragmentHomeBinding.progressLayout.setVisibility(View.VISIBLE);
                call = apiService.getDocVehicleAlerts(user.getUserAccessToken(), true, false);
                break;
        }

        Log.e(TAG, purpose + " Input :" + jsonObject);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {

//                    fragmentHomeBinding.progressLayout.setVisibility(View.GONE);
//                    //fragmentHomeBinding.progressTv.setText("Please wait ... we are connecting with your car");
//                    fragmentHomeBinding.progressTv.setText("Please wait ...");

                    if (response.isSuccessful()) {
                        Log.e(TAG, "Response is success");

                        //If the response is null, we will return immediately.
                        ResponseBody responseBody = response.body();
                        if (responseBody == null)
                            return;
                        String responseStr = responseBody.string();
                        Log.e(TAG, purpose + " Response :" + responseStr);

                        switch (value) {
                            case 0:
                                Log.e(TAG, "Hello");
                                parseFetchVehicles(responseStr);
                                break;

                            case 2:
                                //Upload trouble code response
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
                                parseLoginRegisterResponse(responseStr);
                                break;

                            case 8:
                                parseFetchDrivingALerts(responseStr);
                                break;
                            case 9:

                                parseFetchTripsResponse(responseStr);
                                break;

                            case 10:
                                //Clear temporary lat long model array list.
                                templatLongModelArrayList.clear();
                                parseGPSDistance(responseStr);
                                //Parse GPS calculate for a trip.
                                // parseFetchTripsResponse(responseStr);
                                break;
                            case 11:
                                parseFetchDrivingAnalytics(responseStr);
                                break;

                            case 12:
                                parseWeeklyReportDrivingAnalytics(responseStr);
                                break;
                            case 13:
                                //parseFetchDrivingTools(responseStr);
                                break;

                            case 14:
                                parseFetchObdDevice(responseStr);
                                break;

                            case 15:
                                //parseFetchVehicleHealth(responseStr);
                                break;

                            case 16:
                                performWipeData();
                                break;

                            case 17:
                                parseFetchVehicles(responseStr);
                                Log.e(TAG, url + "\n" + responseStr.toString());
                                break;

                            case 18:
                                parseFetchRegisteredDevices(responseStr);
                                break;

                            case 19:
                                parseFetchDeviceCategories(responseStr);
                                break;

                            case 20:
                                parseFetchEmergencyContacts(responseStr);
                                break;

                            case 21:
                                parseFetchAvailableSubscriptions(responseStr);
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
                            Utils.clearAllData(context);
                            return;
                        }

                        switch (value) {

                            case 11:

                                break;

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
                    Toast.makeText(context, "Please Check your internet connection", Toast.LENGTH_LONG).show();
                }

                /*if (call.isCanceled()) {

                } else {

                }*/
            }
        });
    }

    private void getUserVehicles() {
        if (user != null) {
            callServer(getResources().getString(R.string.vehicles_url).replace("userid", user.getUserId()), "Fetch cars", 17);
        }
    }


    private void getEmergencyContacts(){
        callServer(getString(R.string.fetchEmergencyContacts), "Fetch emergency contacts", 20);
    }


    private void parseFetchAvailableSubscriptions(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);

        String subId;

        if (jsonObject.has("subscriptions")) {
            JSONArray subs = jsonObject.getJSONArray("subscriptions");
            if (subs.length() == 0){
                subId = "";
            } else {
                JSONObject arrayItem = subs.getJSONObject(0);
                subId = arrayItem.has("id") ? arrayItem.getString("id") : "";
            }
        } else {
            subId = "";
        }

        myPreferenece.setAvailableSubscriptionId(subId);

    }



    private void parseFetchVehicles(String responseStr) {

        ArrayList<Vehiclemodel> vehiclemodelArrayList = new ArrayList<>();
        JSONObject jsonResponse = null;
        new DatabaseHelper(context).deleteTable(DatabaseHelper.VEHICLES_TABLE);
        try {
            jsonResponse = new JSONObject(responseStr);
            //Go to main Activity
            // ArrayList<String> values = new ArrayList<>();
            JSONArray vehciclesArray = jsonResponse.getJSONArray("userVehicles");
            for (int i = 0; i < vehciclesArray.length(); i++) {
                JSONObject jsonObject = vehciclesArray.getJSONObject(i);
                Log.e(TAG, jsonObject.toString());

                Gson gson = new Gson();
                Vehiclemodel vehicleModel = gson.fromJson(jsonObject.toString(), Vehiclemodel.class);

                Log.e(TAG, "Vehicle Model Created :"+vehicleModel.toString());

                vehiclemodelArrayList.add(vehicleModel);

            }

            new DatabaseHelper(context).InsertAllVehicles(vehiclemodelArrayList);
            //SendBroadcast to receiver.
            Intent intent = new Intent("CarsList");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        } catch (
                JSONException e) {
            e.printStackTrace();
        }

    }

    private void parseFetchObdDevice(String responseStr) throws JSONException{

        JSONObject jsonObject = new JSONObject(responseStr);
        JSONArray jsonArray = jsonObject.getJSONArray("userOBDDevices");

        //using userObdDeviceModel class for future usage
        ArrayList<UserObdDeviceModel> obdList = new ArrayList<>();
        String id, macAddress, deviceStats, lastConnectedTime, createdOn, lastUpdatedOn;
        boolean active;

        for (int i=0; i<jsonArray.length(); i++){

            id = jsonArray.getJSONObject(i).getString("id");
            macAddress = jsonArray.getJSONObject(i).getString("macAddress");
            deviceStats = jsonArray.getJSONObject(i).getString("deviceStats");
            lastConnectedTime = jsonArray.getJSONObject(i).getString("lastConnectedTime");
            createdOn = jsonArray.getJSONObject(i).getString("createdOn");
            lastUpdatedOn = jsonArray.getJSONObject(i).getString("lastUpdatedOn");
            active = jsonArray.getJSONObject(i).getBoolean("active");

            obdList.add(new UserObdDeviceModel(id, macAddress, deviceStats, lastConnectedTime, createdOn, lastUpdatedOn, active));

        }

        //for now nwe are using the phone based trip for curret obd users
        //when obd fails to connect or user is not using obd
        if (obdList.isEmpty()){
            myPreferenece.saveBoolean(MyPreferenece.obdExistForUser, false);
            Log.e(TAG, "obd not exist!");
        } else {
            myPreferenece.saveBoolean(MyPreferenece.obdExistForUser, true);
            Log.e(TAG, "obd exist!");
        }


    }



    ArrayList<DailyTripAlertCountModel> dailyTripAlertCountModelArrayList = new ArrayList<>();


    private void parseWeeklyReportDrivingAnalytics(String responseStr) throws JSONException {
        dailyTripAlertCountModelArrayList.clear();
        JSONObject jsonObject = new JSONObject(responseStr);
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject innerJson = jsonArray.getJSONObject(i);
            int totalTrips, totalHarshBreakingAlerts, totalOverSpeedAlerts, totalHarshAccelerationAlerts, totalHighRPMAlerts,
                    totalDistanceTravelledInKm;
            String date, dayName;
            boolean dataLoading, enabled;
            totalTrips = innerJson.getInt("totalTrips");
            totalHarshBreakingAlerts = innerJson.getInt("totalHarshBreakingAlerts");
            totalOverSpeedAlerts = innerJson.getInt("totalOverSpeedAlerts");
            totalHarshAccelerationAlerts = innerJson.getInt("totalHarshAccelerationAlerts");
            totalHighRPMAlerts = innerJson.getInt("totalHighRPMAlerts");
            totalDistanceTravelledInKm = innerJson.getInt("totalDistanceTravelledInKm");
            date = innerJson.getString("date");
            dayName = innerJson.getString("dayName");
            dataLoading = innerJson.getBoolean("dataLoading");
            enabled = innerJson.getBoolean("enabled");

            DailyTripAlertCountModel dailyTripAlertCountModel;

            dailyTripAlertCountModel = new DailyTripAlertCountModel(totalTrips, totalHarshBreakingAlerts, totalOverSpeedAlerts, totalHarshAccelerationAlerts, totalHighRPMAlerts,
                    totalDistanceTravelledInKm, date, dayName, dataLoading, enabled);
            dailyTripAlertCountModelArrayList.add(dailyTripAlertCountModel);
        }


    }


    private void parseGPSDistance(String responseStr) throws JSONException {

        JSONObject jsonObject = new JSONObject(responseStr);
        tripDistanceFromGPSm = Double.parseDouble(jsonObject.getString("tripDistanceInKm"));
        Log.e(TAG, "parseGPSDistance: "+String.valueOf(tripDistanceFromGPSm) );
        commandResult.put("TRIP_DISTANCE", String.valueOf(tripDistanceFromGPSm));
        currentTrip.setDistanceCovered(tripDistanceFromGPSm + "");

        if (isTriponeKmPopUpNotShown && tripDistanceFromGPSm >= 1) {
            isTriponeKmPopUpNotShown = false;
            BluetoothHelperClass.showTriponeKmDialog(context, getLayoutInflater(), "", "Your Driving log has now started. Have a safe drive with gypsee.");
        }
    }

    boolean isTriponeKmPopUpNotShown = true;

    ArrayList<LastSafeTripModel> lastSafeTripModels = new ArrayList<>();

    private void parseFetchDrivingAnalytics(String responseStr) throws JSONException {

        lastSafeTripModels.clear();

        JSONObject jsonObject = new JSONObject(responseStr);


        if (!jsonObject.has("userDrivingAnalytics")) {
            return;
        }
        JSONObject userDrivingAnalytics = jsonObject.getJSONObject("userDrivingAnalytics");

        String totalDistanceTravelledInKm, totalTrips, totalDrivingTimeInMin, totalSafeTrips, totalSafeTripsDistance;
        totalDistanceTravelledInKm = userDrivingAnalytics.getString("totalDistanceTravelledInKm");
        totalTrips = userDrivingAnalytics.getString("totalTrips");
        totalDrivingTimeInMin = userDrivingAnalytics.getString("totalDrivingTimeInMin");
        totalSafeTrips = userDrivingAnalytics.getString("totalSafeTrips");
        totalSafeTripsDistance = userDrivingAnalytics.has("totalSafeTripsDistance") ? userDrivingAnalytics.getString("totalSafeTripsDistance") : "0";

        myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);

        myPreferenece.saveStringData("totalDistanceTravelledInKm", totalDistanceTravelledInKm);
        myPreferenece.saveStringData("totalTrips", totalTrips);
        myPreferenece.saveStringData("totalDrivingTimeInMin", totalDrivingTimeInMin);
        myPreferenece.saveStringData("totalSafeTrips", totalSafeTrips);
        myPreferenece.saveStringData("totalSafeTripsDistance", totalSafeTripsDistance);
        Log.e(TAG, "Total trips count" + totalTrips);

        //Last Trip Details
        String tripId, startLat, startLong, endLat, endLong, tripDistanceInKm, tripTimeInMin, triAlerts = "0", createdOn, startLocationName, destinationName, tripSavedAmount,tripSavingsCommission;

        if (userDrivingAnalytics.has("lastTrip")) {
            JSONObject lastTripJsonObject = userDrivingAnalytics.getJSONObject("lastTrip");

            tripId = lastTripJsonObject.getString("tripId");
            startLat = lastTripJsonObject.getString("startLat");
            startLong = lastTripJsonObject.getString("startLong");
            endLat = lastTripJsonObject.has("endLat") ? lastTripJsonObject.getString("endLat") : "0.0";
            endLong = lastTripJsonObject.getString("endLong");
            tripDistanceInKm = lastTripJsonObject.getString("tripDistanceInKm");
            tripTimeInMin = lastTripJsonObject.getString("tripTimeInMin");
            createdOn = lastTripJsonObject.getString("createdOn");
            triAlerts = lastTripJsonObject.has("drivingAlerts") ? lastTripJsonObject.getString("drivingAlerts") : "0";
            startLocationName = jsonObject.has("startLocationName") ? jsonObject.getString("startLocationName") : "NA";
            destinationName = jsonObject.has("destinationName") ? jsonObject.getString("destinationName") : "NA";
            tripSavedAmount = jsonObject.has("tripSavedAmount") ? jsonObject.getString("tripSavedAmount") : "NA";
            tripSavingsCommission = jsonObject.has("tripSavingsCommission") ? jsonObject.getString("tripSavingsCommission") : "NA";
            lastSafeTripModels.add(new LastSafeTripModel(tripId, startLat, startLong, endLat, endLong, tripDistanceInKm, tripTimeInMin, triAlerts, createdOn, startLocationName, destinationName,tripSavedAmount,tripSavingsCommission));
        }

        if (userDrivingAnalytics.has("lastSafeTrip"))
        //Safe Trip Details
        {
            JSONObject safeTripDetails = userDrivingAnalytics.getJSONObject("lastSafeTrip");

            tripId = safeTripDetails.getString("tripId");

            startLat = safeTripDetails.getString("startLat");
            startLong = safeTripDetails.getString("startLong");
            endLat = safeTripDetails.has("endLat") ? safeTripDetails.getString("endLat") : "0.0";
            endLong = safeTripDetails.getString("endLong");
            tripDistanceInKm = safeTripDetails.getString("tripDistanceInKm");
            tripTimeInMin = safeTripDetails.getString("tripTimeInMin");
            createdOn = safeTripDetails.getString("createdOn");
            triAlerts = safeTripDetails.has("drivingAlerts") ? safeTripDetails.getString("drivingAlerts") : "0";
            startLocationName = jsonObject.has("startLocationName") ? jsonObject.getString("startLocationName") : "NA";
            destinationName = jsonObject.has("destinationName") ? jsonObject.getString("destinationName") : "NA";
            tripSavedAmount = jsonObject.has("tripSavedAmount") ? jsonObject.getString("tripSavedAmount") : "NA";
            tripSavingsCommission = jsonObject.has("tripSavingsCommission") ? jsonObject.getString("tripSavingsCommission") : "NA";

            //If both the last trip and safe trip are same, we will not add second element.
            if (!lastSafeTripModels.get(0).getTripId().equals(tripId)) {
                lastSafeTripModels.add(new LastSafeTripModel(tripId, startLat, startLong, endLat, endLong, tripDistanceInKm, tripTimeInMin, triAlerts, createdOn, startLocationName, destinationName,tripSavedAmount,tripSavingsCommission));
            }
        }



    }

    private void performTaskonStartTrip() {
        latLongModelArrayList.clear();
        initMyCsvWriter();
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
                    callBackgroundService(1);
                    callBackgroundService(3);
                    callServer(getString(R.string.vehicleAlertUrl).replace("vehicleId", selectedvehiclemodel.getUserVehicleId()), "Fetch selected Vehicle Alerts", 5);
                }
            }
        }, 70000);
        //Upload trip history every 10 min . Also, we need to implement a way to delete the file.This is pending.

    }

    TripRecord tripRecord;
    private void parseFetchTripsResponse(String responseStr) throws JSONException {

        fragmentHomeBinding.progressBar.setVisibility(View.GONE);

        new DatabaseHelper(context).deleteTable(DatabaseHelper.TRIPRECORD_TABLE);
        JSONObject jsonResponse = new JSONObject(responseStr);

        // ArrayList<String> values = new ArrayList<>();
        JSONArray basicTripsData = jsonResponse.getJSONArray("basicTripsData");
        for (int i = basicTripsData.length() - 1; i >= 0; i--) {

            JSONObject jsonObject = basicTripsData.getJSONObject(i);
            /// the date the trip started
            String startDate, endDate;

            Integer engineRpmMax = 0, speed = 0;
            double vhsScore = 0, mileage = 0;
            long tripDuration = 0;

            int safeKm;
            String tripId, engineRuntime, distanceCovered = "RESTRICTED", startLat, startLong, endLat, endLong, startLocationName, destinationName, lastUpdatedOn,tripSavedAmount,tripSavingsCommission;
            int alertsCount;

            tripId = jsonObject.getString("tripId");
            startDate = jsonObject.getString("startDate");
            endDate = jsonObject.has("endDate") ? jsonObject.getString("endDate") : null;
            engineRpmMax = jsonObject.getInt("engineRpmMax");
            speed = jsonObject.getInt("speed");
            engineRuntime = jsonObject.has("engineRuntime") ? jsonObject.getString("engineRuntime") : "RESTRICTED";
            distanceCovered = jsonObject.getString("distanceCovered");
            startLat = jsonObject.getString("startLat");
            startLong = jsonObject.getString("startLong");
            endLat = jsonObject.has("endLat") ? jsonObject.getString("endLat") : "0.0";
            endLong = jsonObject.getString("endLong");
            alertsCount = jsonObject.getInt("alertsCount");

            startLocationName = jsonObject.has("startLocationName") ? jsonObject.getString("startLocationName") : "NA";
            destinationName = jsonObject.has("destinationName") ? jsonObject.getString("destinationName") : "NA";
            tripSavedAmount = jsonObject.has("tripSavedAmount") ? jsonObject.getString("tripSavedAmount") : "NA";
            tripSavingsCommission = jsonObject.has("tripSavedAmount") ? jsonObject.getString("tripSavingsCommission") : "NA";

            vhsScore = jsonObject.has("vhsScore") ? jsonObject.getDouble("vhsScore") : 0;
            mileage = jsonObject.has("mileage") ? jsonObject.getDouble("mileage") : 0;
            tripDuration = jsonObject.has("tripDuration") ? jsonObject.getLong("tripDuration") : 0;
            safeKm = jsonObject.has("safeKm") ? jsonObject.getInt("safeKm") : 0;
            lastUpdatedOn = jsonObject.getJSONArray("tripDevices").length()>0?
                    jsonObject.getJSONArray("tripDevices").getJSONObject(0).getString("lastUpdatedOn") : "NA";
//            Log.e("147852", String.valueOf(jsonObject));


//            TripRecord tripRecord = new TripRecord(tripId, startDate, endDate, engineRpmMax, speed, engineRuntime, distanceCovered, alertsCount, startLat, startLong, endLat, endLong, startLocationName, destinationName, vhsScore, mileage, tripDuration, safeKm, lastUpdatedOn);
            tripRecord = new TripRecord(tripId, startDate, endDate, engineRpmMax, speed, engineRuntime, distanceCovered, alertsCount, startLat, startLong, endLat, endLong, startLocationName, destinationName, vhsScore, mileage, tripDuration, safeKm, lastUpdatedOn,tripSavedAmount,tripSavingsCommission);

            new DatabaseHelper(context).insertTripRecord(tripRecord);


        }


        fetchTripsAndDisplay();
    }


    private void parseFetchDrivingALerts(String response) throws JSONException {

        JSONObject jsonResponse = new JSONObject(response);

        //Go to main Activity

        // ArrayList<String> values = new ArrayList<>();
        JSONArray drivingAlertsArray = jsonResponse.getJSONArray("drivingAlerts");

        for (int i = drivingAlertsArray.length() - 1; i >= 0; i--) {

            JSONObject jsonObject = drivingAlertsArray.getJSONObject(i);
            JSONObject alertData = new JSONObject(jsonObject.getString("data"));
            String alertType, alertValue, timeInterval, gForce, impact;
            long timeStamp;
            double lat, lng;
            Log.e(TAG, "Alerts is : " + alertData);

            //{"alert_Type":"Harsh Accelaration","alert_value ":"51 km\/hr","alert_description":"","g_force":"1.428m\/sec2","time_stamp":1598954550242,"time_interval":"1s","lat":12.9229433,"long":77.6471967,"impact":[32.484375,32.484375,32.546875,32.546875,32.59375,32.59375,32.703125,32.703125,32.625,32.625]}

            alertType = alertData.getString("alert_Type");
            alertValue = alertData.has("alert_value ") ? alertData.getString("alert_value ") : alertData.getString("alert_value");
            timeInterval = alertData.getString("time_interval");
            gForce = alertData.getString("g_force");
            impact = alertData.getString("impact");
            timeStamp = alertData.getLong("time_stamp");
            lat = alertData.getDouble("lat");
            lng = alertData.getDouble("long");
            Log.e(TAG, "Alert Value from server : " + alertValue);
            databaseHelper.insertTripAlert(new TripAlert(alertType, alertValue, timeInterval, gForce, timeStamp, lat, lng, impact));

        }
//        if (fragmentHomeBinding.alertsLayout.alerts.getBackground() != null)
//            fetchAlertsAndDisplay();
    }

    private int getDistancePostCC() {
        String distancePostCC = commandResult.get("DISTANCE_TRAVELED_AFTER_CODES_CLEARED");
        if (distancePostCC == null || distancePostCC.isEmpty()) {
            distancePostCC = "0km";
        }
        return Integer.parseInt(distancePostCC.replace("km", ""));
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

    private JsonObject getCurrentLogValuesinJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("airIntakeTemp", commandResult.get("AIR_INTAKE_TEMP"));
        jsonObject.addProperty("ambientAirTemp", commandResult.get("AMBIENT_AIR_TEMP"));
        jsonObject.addProperty("barometricPressure", commandResult.get("BAROMETRIC_PRESSURE"));
        jsonObject.addProperty("currentAltitude", endLocation.getAltitude());
        jsonObject.addProperty("currentLatitude", endLocation.getLatitude());
        jsonObject.addProperty("currentLongitude", endLocation.getLongitude());
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
        jsonObject.addProperty("maxRmp", maximumRPM);
        jsonObject.addProperty("shortTermFuelBank1", commandResult.get("Short Term Fuel Trim Bank 1"));
        jsonObject.addProperty("shortTermFuelBank2", commandResult.get("Short Term Fuel Trim Bank 2"));
        jsonObject.addProperty("termFuelTrimBank1", commandResult.get("Term Fuel Trim Bank 1"));
        jsonObject.addProperty("topSpeed", maximumspeed);
        jsonObject.addProperty("fuelLevel", commandResult.get("FUEL_LEVEL"));
        jsonObject.addProperty("MAF", commandResult.get("MAF"));

        jsonObject.addProperty("throttlePos", commandResult.get("THROTTLE_POS"));
        jsonObject.addProperty("dtcNumber", commandResult.get("DTC_NUMBER"));
        jsonObject.addProperty("timingAdvance", commandResult.get("TIMING_ADVANCE"));
        jsonObject.addProperty("equivRatio", commandResult.get("EQUIV_RATIO"));
        jsonObject.addProperty("fuelType", commandResult.get("FUEL_TYPE"));
        String distancePostCC = commandResult.get("DISTANCE_TRAVELED_AFTER_CODES_CLEARED");

        jsonObject.addProperty("distancePostCC", getDistancePostCC());
        return jsonObject;
    }

    private float harshAccelaration = 9.88f, harshDecelaration = -10.94f;





    Location endLocation;
    double tripDistanceFromGPSm;


    private void requestLocationUpdates(Location location) {


        boolean isDriveMode = myPreferenece.getSharedPreferences().getBoolean(MyPreferenece.DRIVING_MODE, true);

        if (currentTrip != null && (isServiceBound || !isDriveMode)) {

            Log.e(TAG, "Fetched location : " + location.getLatitude() + "-" + location.getLongitude());
            //IF the spped is empty then we will calculate the seed
            if (commandResult.get("SPEED") == null || commandResult.get("SPEED").equals("NA") || commandResult.get("SPEED").equals("NODATA"))
                performGPSSpeedCalc(location);

            //Need to collect the gps params only for distance Calculation and storing the trip lat, long in the server& distance between then should exceed 100 meteres.

            if (calculateGPSTimeInterval() >= 3) {
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                LatLongModel latLongModel = new LatLongModel(location.getLatitude(), location.getLongitude(), timeStamp);
                if (previousLocation == null) {
                    previousLocation = location;
                } else if (DistanceCalculator.distance(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(previousLocation.getLatitude(), previousLocation.getLongitude())) > 25 && !checkSameElementInarray(latLongModel)) {
                    latLongModelArrayList.add(latLongModel);
                    previousLocation = location;
                }
            }
        }
    }

    private boolean checkSameElementInarray(LatLongModel latLongModel) {
        for (LatLongModel latLn :
                latLongModelArrayList) {
            if (latLongModel.getLat() == latLn.getLat() && latLongModel.getLongitude() == latLn.getLongitude()) {
                return true;
            }
        }
        return false;
    }


    float previousSpeed = 0;

    private void performGPSSpeedCalc(Location location) {
        float speed = location.getSpeed() * 3.6f;

        previousSpeed = filter(previousSpeed, speed, 2);

        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(0);
//        fragmentHomeBinding.speedFAB.setVisibility(View.VISIBLE);
//        fragmentHomeBinding.speedFAB.setText(numberFormat.format(previousSpeed) + "\nkm/hr");
        updateSpeed(numberFormat.format(previousSpeed) + "km/h");
    }


    /**
     * Simple recursive filter
     *
     * @param prev Previous value of filter
     * @param curr New input value into filter
     * @return New filtered value
     */
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


    Location previousLocation = null;

    private int calculateGPSTimeInterval() {

        //This methd is used to calculate the time taken to move between two gps location points.
        int timeDiff;
        gpsWithIntervals.add(new Date());
        if (gpsWithIntervals.size() == 1) {
            return 0;
        }

        Log.e(TAG, "GPS currentTimeInSec : " + gpsWithIntervals.get(1) + "previousTimeInSec : " + gpsWithIntervals.get(0));
        timeDiff = TimeUtils.calcDiffTimeInSec(gpsWithIntervals.get(0), gpsWithIntervals.get(1));
        Log.e(TAG, "GPS Time difference is : " + timeDiff);

        //If the time difference is less than 5 sec, we will not add the time interval.
        if (timeDiff < 3) {
            gpsWithIntervals.remove(1);
            return 0;
        } else {
            gpsWithIntervals.remove(0);

        }

        return timeDiff;
    }

    private void parseLoginRegisterResponse(String response) throws JSONException {
        JSONObject jsonResponse = new JSONObject(response);

        //Go to main Activity

        // ArrayList<String> values = new ArrayList<>();
        JSONObject userJsonObject = jsonResponse.getJSONObject("user");

        String userId, userName, userFullName, userEmail, userPhoneNumber, userAccessToken, fcmToken, userImg, userDeviceMac,
                userTypes, referCode, createdOn, lastUpdatedOn, userAddresses;

        JSONObject userWallet = userJsonObject.has("userWallet")? userJsonObject.getJSONObject("userWallet") : null;

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

        SubscriptionModel subscriptionModel = null;

        JSONObject subJsonObject=null;
        if(userJsonObject.has("userSubscriptions"))
        {
            subJsonObject = userJsonObject.getJSONObject("userSubscriptions");
        }
        if(subJsonObject != null){

            boolean active = subJsonObject.has("active") && subJsonObject.getBoolean("active");
            String couponCode = subJsonObject.has("couponCode") ? subJsonObject.getString("couponCode") : "";
            String endDate = subJsonObject.has("endDate") ? subJsonObject.getString("endDate") : "";
            String id = subJsonObject.has("id") ? subJsonObject.getString("id") : "";
            String subLastUpdatedOn = subJsonObject.has("lastUpdatedOn") ? subJsonObject.getString("lastUpdatedOn") : "";
            String startDate = subJsonObject.has("startDate") ? subJsonObject.getString("startDate") : "";
            double paidAmount = subJsonObject.has("paidAmount") ? subJsonObject.getDouble("paidAmount") : 0;
            double subscriptionAmount = subJsonObject.has("subscriptionAmount") ? subJsonObject.getDouble("subscriptionAmount") : 0;
            double discountAmount = subJsonObject.has("discountAmount") ? subJsonObject.getDouble("discountAmount") : 0;

            if (!id.equals("")) {

                subscriptionModel = new SubscriptionModel(
                        active,
                        couponCode,
                        createdOn,
                        endDate,
                        id,
                        subLastUpdatedOn,
                        startDate,
                        discountAmount,
                        subscriptionAmount,
                        paidAmount
                );
            }

        }


        Log.e(TAG, "wallet: " + userWallet);

        walletAmount = (userWallet != null) ? (userWallet.has("loyaltyPoints") ? userWallet.getInt("loyaltyPoints") : 0) : 0;

        boolean inTrainingMode = userJsonObject.has("inTrainingMode") && userJsonObject.getBoolean("inTrainingMode");

        Log.e(TAG, "inTrainingMode: " + inTrainingMode);

        myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);
        user = new User(userId, userName, userFullName, userEmail, userPhoneNumber, userAccessToken, fcmToken, userImg, userDeviceMac,
                userTypes, referCode, createdOn, lastUpdatedOn, approved, locked, signUpBonusCredited, referCodeApplied, inTrainingMode, String.valueOf(walletAmount));

        user.setUserSubscriptions(subscriptionModel);


        myPreferenece.storeUser(user);



    }

    private void addDummyValues() {


        if (!commandResult.containsKey("VIN")) {

            checkForTheVehicle("");
        }

    }


    BluetoothSocket bluetoothSocket;
    BlueToothConnectionInterface blueToothConnectionInterface = new BlueToothConnectionInterface() {
        @Override
        public void onBluetoothConnected(BluetoothSocket socket) {

            bluetoothSocket = socket;
            Log.e(TAG, "Bluetooth is connected");
            fragmentHomeBinding.setBlueToothDrawable(ContextCompat.getDrawable(context, R.drawable.bluetooth_green));
            updateObdImage(true);
            isStarting = true;
            startLiveData();
        }

        @Override
        public void obBluetoothDisconnected() {
            Log.e(TAG, "Failure Starting live data");
            isStarting = true;
//            fragmentHomeBinding.progressLayout.setVisibility(View.GONE);
            Toast.makeText(context, "Please check connection of Gypsee Drive with your OBDII port", Toast.LENGTH_LONG).show();
            stopLiveData();
//            fragmentHomeBinding.logoLayout.setEnabled(true);
        }
    };



    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }


    private void performExitCleanup() {

        if (uploadGpCalcTimer != null) {
            uploadGpCalcTimer.cancel();
            uploadGpCalcTimer.purge();
        }
    }


    private void performWipeData(){

        new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).clearAllSharesPreferences();
        new DatabaseHelper(context).deleteTotalDatabase(context);
        new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).setNewInstall(false);
        Intent mStartActivity = new Intent(context, LoginRegisterActivity.class);
        startActivity(mStartActivity);
        getActivity().finish();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK&&requestCode==REQUEST_ENABLE_BT) {
            registerForegroundServiceReceiver();
        }
        bluetoothPermissionRequested =false;

    }














}


