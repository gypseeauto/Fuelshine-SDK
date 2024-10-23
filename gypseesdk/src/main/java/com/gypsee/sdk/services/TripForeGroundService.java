package com.gypsee.sdk.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.database.DatabaseHelper;
import com.gypsee.sdk.io.LogCSVWriter;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.models.Vehiclemodel;
import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;
import com.gypsee.sdk.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripForeGroundService extends Service {
    public static final String PURPOSE = "PURPOSE";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void updateVehicle(Intent intent) {
        Vehiclemodel vehiclemodel = intent.getParcelableExtra(Vehiclemodel.class.getSimpleName());
        if (vehiclemodel != null) {
            callServer(getResources().getString(R.string.UpdateVehDetails_Url).replace("vehicleId", vehiclemodel.getUserVehicleId()), "Update vehicle ", 4, intent);
        }
    }

    private void getUserVehicles(Intent intent) {
        User user = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, getApplicationContext()).getUser();
        if (user != null) {
            callServer(getResources().getString(R.string.vehicles_url).replace("userid", user.getUserId()), "Fetch cars", 3, intent);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "On handle Intent called");

        boolean isDriveMode = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, getApplicationContext()).getSharedPreferences().getBoolean(MyPreferenece.DRIVING_MODE, true);

        //Sometimes intent is giving null,even though we are passing correct data;
        if (intent == null) {
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }
        String purpose = intent.getStringExtra(PURPOSE);
        if (purpose == null) {
            //stopSelf();
        } else if (purpose.equals("Trip History") && isDriveMode) {
            callServer(getResources().getString(R.string.tripHistoryFileUrl), "Trip history", 0, intent);
            //callServer(Server_Config.tripHistoryUrl, "Trip history", 0, intent);
        } else if (purpose.equals("Trip Update") && isDriveMode) {
            callServer(getResources().getString(R.string.tripupdate_url), "Update trip ", 1, intent);
        } else if (purpose.equals("End Trip")) {
            callServer(getResources().getString(R.string.tripEndurl), "end trip", 2, intent);
            updateVehicle(intent);
        } else if (purpose.equals("Update vehicle")) {
            updateVehicle(intent);

        } else if (purpose.equals("Get UserVehicles")) {
            getUserVehicles(intent);
        } else if (purpose.equals("Get VehicleMakes")) {
            callServer(getResources().getString(R.string.fetchVehicleMakesUrl), "Fetch Vehicle makes ", 5, intent);

        }

       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopSelf();
            }
        },15000);*/
        return super.onStartCommand(intent, flags, startId);

    }

    String TAG = TripForeGroundService.class.getSimpleName();

    long tripSqlId;

    private void callServer(String url, final String purpose, final int value, final Intent intent) {

        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);
        User user = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, getApplicationContext()).getUser();

        if (user == null)
            return;
        Call<ResponseBody> call;
        JsonObject jsonObject = new JsonObject();

        String tripId = "";
        switch (value) {
            case 0:
                String fileName = intent.getStringExtra("FileName");
                String Base64encoded = new LogCSVWriter().encodeFileToBase64Binary("/storage/emulated/0/GypseeVehicleLogs/" + fileName);

                if (fileName == null || Base64encoded == null)
                    return;
                jsonObject.addProperty("fileName", fileName);
                jsonObject.addProperty("fileContent", Base64encoded);
                call = apiService.uploadVehDetails(user.getUserAccessToken(), jsonObject);
                Log.e(TAG, purpose + " input : " + jsonObject.toString());
                break;

            case 1:
            case 2:
                //Battery voltage

                jsonObject = getCurrentLogValuesinJson(intent);
                /*jsonObject.addProperty("tripDuration", );
                jsonObject.addProperty("tripETA", "");
                jsonObject.addProperty("tripName", "");*/
                jsonObject.addProperty("userId", user.getUserId());
                // jsonObject.addProperty("tripDesc", "");

                tripId = intent.getStringExtra("userTripDetails");
                jsonObject.addProperty("tripId", tripId);


                // We need to ask dilli that in update trip and end trip we will not send starting locations. Anyway these locations were already sent in start trip API.

                /*try {

                    jsonObject.addProperty("startLatitude", userTripDetails.getString("startLatitude"));
                    jsonObject.addProperty("startLongitude", userTripDetails.getString("startLongitude"));
                    jsonObject.addProperty("startingAltitude", userTripDetails.getString("startingAltitude"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/


                Log.e(TAG, purpose + " input : " + jsonObject.toString());
                call = apiService.uploadVehDetails(user.getUserAccessToken(), jsonObject);
                break;
            case 3:
                call = apiService.getDocVehicleAlerts(user.getUserAccessToken(), true, false);
                break;

            case 4:

                Location lastKnowLocation = intent.getParcelableExtra("Location");
                HashMap<String, String> commandResult = (HashMap<String, String>) intent.getSerializableExtra("commandResult");
                Vehiclemodel vehiclemodel = intent.getParcelableExtra(Vehiclemodel.class.getSimpleName());


                String distancePostCC = commandResult.get("DISTANCE_TRAVELED_AFTER_CODES_CLEARED");

                //Don't call update APi if the distance postcc is not coming from the device. It will be not userful.

                if (distancePostCC == null || distancePostCC.isEmpty() || distancePostCC.equals("NA") || distancePostCC.equals("0km")) {
                } else {
                    jsonObject.addProperty("distancePostCC", Double.parseDouble(distancePostCC.replace("km", "")));
                }

                jsonObject.addProperty("engineRunTime", commandResult.get("ENGINE_RUNTIME"));
                jsonObject.addProperty("fuelLevel", commandResult.get("FUEL_LEVEL"));
                jsonObject.addProperty("fuelType", commandResult.get("FUEL_TYPE"));

                if (lastKnowLocation != null) {
                    jsonObject.addProperty("latitude", lastKnowLocation.getLatitude());
                    jsonObject.addProperty("longitude", lastKnowLocation.getLongitude());
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

                Log.e(TAG, purpose + " Input is : " + jsonObject.toString());
                call = apiService.uploadFCMToken(user.getUserAccessToken(), jsonObject);

                break;

            case 5:
                call = apiService.getDocTypes(user.getUserAccessToken(), false);
                break;

            default:
                return;
        }

        Log.e(TAG, purpose + " url : " + call.request().url());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.e(TAG, "Response is succesful");
                        String responseStr = response.body().string();
                        Log.e(TAG, purpose + " Response : " + responseStr);

                        switch (value) {
                            case 0:
                                //Trip upload succesfully.
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        stopSelf();
                                    }
                                }, 15000);

                                break;

                            case 1:
                                //1 stands for update trip response
                                break;

                            case 2:
                                // 2 stands for end trip response
                                new DatabaseHelper(getApplicationContext()).deleteTripCSvEntry(tripSqlId);

                                //SendBroadcast to receiver in home fragment. So that we will refresh the data.

                                Intent in = new Intent("TripEndedSuccess");
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(in);

                                break;

                            case 3:
                                //getting user vehicles
                                parseFetchVehicles(responseStr);
                                break;

                            case 4:
                                getUserVehicles(intent);
                                break;
                            case 5:
                                parseVehicleMakes(responseStr);
                                break;
                        }

                    } else {
                        Log.e(TAG, purpose + " Response is not Success : " + response.errorBody().string());
                        int responseCode = response.code();
                        if (responseCode == 401 || responseCode == 403) {
                            //include logout functionality
                            Utils.clearAllData(getApplicationContext());
                            return;
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "error here since request failed");
                if (call.isCanceled()) {

                } else {

                }
            }
        });
    }

    private void parseVehicleMakes(String responseStr) {

        ArrayList<String> brandNamesArrayList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(responseStr);

            JSONArray jsonArray = jsonObject.getJSONArray("vehicleMakes");

            for (int i = 0; i < jsonArray.length(); i++) {

                String vehicleMake = jsonArray.getJSONObject(i).getString("vehicleMake");
                brandNamesArrayList.add(vehicleMake);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new DatabaseHelper(getApplicationContext()).insertAllVehicleMakes(brandNamesArrayList);

    }

    private JsonObject getCurrentLogValuesinJson(Intent intent) {

        JsonObject jsonObject = new JsonObject();
        Location lastKnowLocation = intent.getParcelableExtra("Location");
        HashMap<String, String> commandResult = (HashMap<String, String>) intent.getSerializableExtra("commandResult");

        if (lastKnowLocation != null) {
            jsonObject.addProperty("destAltitude", lastKnowLocation.getAltitude());
            jsonObject.addProperty("destLatitude", lastKnowLocation.getLatitude());
            jsonObject.addProperty("destLongitude", lastKnowLocation.getLongitude());
            jsonObject.addProperty("currentAltitude", lastKnowLocation.getAltitude());
            jsonObject.addProperty("currentLatitude", lastKnowLocation.getLatitude());
            jsonObject.addProperty("currentLongitude", lastKnowLocation.getLongitude());
        } else {
            jsonObject.addProperty("destAltitude", "0.0");
            jsonObject.addProperty("destLatitude", "0.0");
            jsonObject.addProperty("destLongitude", "0.0");
            jsonObject.addProperty("currentAltitude", "0.0");
            jsonObject.addProperty("currentLatitude", "0.0");
            jsonObject.addProperty("currentLongitude", "0.0");
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
        jsonObject.addProperty("tripDistance", Float.parseFloat(commandResult.get("TRIP_DISTANCE")));


        return jsonObject;
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

                Log.e(TAG, "Vehicle Model Created :"+vehicleModel.toString());
                vehiclemodelArrayList.add(vehicleModel);

            }

            new DatabaseHelper(getApplicationContext()).InsertAllVehicles(vehiclemodelArrayList);
            //SendBroadcast to receiver.
            Intent intent = new Intent("CarsList");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        } catch (
                JSONException e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopSelf();
            }
        }, 15000);
    }


    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //create custom notification for Android O and higher.

            String NOTIFICATION_CHANNEL_ID = getPackageName();
            String channelName = "My Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.notif_icon)
                    .setContentTitle("App is running in background")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
//            startForeground(2, notification);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                startForeground(2, notification);
            } else {
                startForeground(2, notification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
            }

        } else
            startForeground(1, new Notification());


    }


}
