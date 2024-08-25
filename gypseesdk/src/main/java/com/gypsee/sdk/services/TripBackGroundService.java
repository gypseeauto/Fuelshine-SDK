package com.gypsee.sdk.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
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
import com.gypsee.sdk.activities.GypseeMainActivity;
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

public class TripBackGroundService extends IntentService {
    public static final String PURPOSE = "PURPOSE";
    public TripBackGroundService() {
        super("TripBackGroundService");
    }



    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

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
        // Ensures the service is restarted if the system kills it

        Log.e(TAG, "onStartCommand: " );

        String purpose = intent.getStringExtra(PURPOSE);
        if (purpose == null) {
            stopSelf();
        } else if (purpose.equals("Trip History")) {

            callServer(getResources().getString(R.string.tripHistoryFileUrl), "Trip history", 0, intent);
            //callServer(Server_Config.tripHistoryUrl, "Trip history", 0, intent);
        } else if (purpose.equals("Trip Update")) {
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
        return START_STICKY;
    }

    String TAG = TripBackGroundService.class.getSimpleName();

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

                /*ArrayList<Tripmodelclass> tripmodelclasses = intent.getExtras().getParcelableArrayList(Tripmodelclass.class.getSimpleName());
              Log.e(TAG, "Size of the Parcelable is :" + tripmodelclasses.size());
                JsonArray tripData = new JsonArray();
                for (Tripmodelclass tr : tripmodelclasses) {
                    JsonObject sjsonObject = new JsonObject();
                    sjsonObject.addProperty("currentLatitude", tr.getCurrentLatitude());
                    sjsonObject.addProperty("currentLongitude", tr.getCurrentLongitude());
                    sjsonObject.addProperty("currentAltitude", tr.getCurrentAltitude());
                    sjsonObject.addProperty("barometricPressure", tr.getBarometricPressure());
                    sjsonObject.addProperty("engineCoolantTemp", tr.getEngineCoolantTemp());
                    sjsonObject.addProperty("fuelLevel", tr.getFuelLevel());
                    sjsonObject.addProperty("engineLoad", tr.getEngineLoad());
                    sjsonObject.addProperty("ambientAirTemp", tr.getAmbientAirTemp());
                    sjsonObject.addProperty("engineRpm", tr.getEngineRpm());
                    sjsonObject.addProperty("intakeManifoldPressure", tr.getIntakeManifoldPressure());
                    sjsonObject.addProperty("MAF", tr.getMAF());
                    sjsonObject.addProperty("termFuelTrimBank1", tr.getTermFuelTrimBank1());
                    sjsonObject.addProperty("fuelEconomy", tr.getFuelEconomy());
                    sjsonObject.addProperty("longTermFuelTrimBank2", tr.getLongTermFuelTrimBank2());
                    sjsonObject.addProperty("fuelType", tr.getFuelType());
                    sjsonObject.addProperty("airIntakeTemp", tr.getAirIntakeTemp());
                    sjsonObject.addProperty("fuelPressure", tr.getFuelPressure());
                    sjsonObject.addProperty("currentSpeed", tr.getCurrentSpeed());
                    sjsonObject.addProperty("shortTermFuelBank2", tr.getShortTermFuelBank2());
                    sjsonObject.addProperty("shortTermFuelBank1", tr.getShortTermFuelBank1());
                    sjsonObject.addProperty("engineRunTime", tr.getEngineRunTime());
                    sjsonObject.addProperty("throttlePos", tr.getThrottlePos());
                    sjsonObject.addProperty("dtcNumber", tr.getDtcNumber());
                    sjsonObject.addProperty("troubleCodes", tr.getTroubleCodes());
                    sjsonObject.addProperty("timingAdvance", tr.getTimingAdvance());

                    sjsonObject.addProperty("equivRatio", tr.getEquivRatio());
                    sjsonObject.addProperty("userId", tr.getUserId());
                    sjsonObject.addProperty("vehicleId", tr.getVehicleId());
                    tripData.add(sjsonObject);
                }*/

                String fileName = intent.getStringExtra("FileName");
                String Base64encoded = new LogCSVWriter().encodeFileToBase64Binary("/storage/emulated/0/GypseeVehicleLogs/" + fileName);

                if (fileName == null || Base64encoded == null)
                    return;
                jsonObject.addProperty("fileName", fileName);
                jsonObject.addProperty("fileContent", Base64encoded);

                call = apiService.uploadVehDetails(user.getUserAccessToken(), jsonObject);
                Log.e(TAG, purpose + " input : " + jsonObject);

                break;

            case 1:
            case 2:
                //Battery voltage


                jsonObject = getCurrentLogValuesinJson(intent);
                JSONObject userTripDetails = null;
                try {
                    userTripDetails = new JSONObject(intent.getStringExtra("userTripDetails"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                /*jsonObject.addProperty("tripDuration", );
                jsonObject.addProperty("tripETA", "");
                jsonObject.addProperty("tripName", "");*/
                jsonObject.addProperty("userId", user.getUserId());
                // jsonObject.addProperty("tripDesc", "");


                try {
                    tripSqlId = userTripDetails.getLong("TripSqlId");
                    tripId = userTripDetails.getString("userTripId");
                    jsonObject.addProperty("tripId", tripId);
                    jsonObject.addProperty("startLatitude", userTripDetails.getString("startLatitude"));
                    jsonObject.addProperty("startLongitude", userTripDetails.getString("startLongitude"));
                    jsonObject.addProperty("startingAltitude", userTripDetails.getString("startingAltitude"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Log.e(TAG, purpose + " input : " + jsonObject);
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

                //Don't call update APi if the distance postcc is not coming from the device. It will be not userful

                if (distancePostCC == null || distancePostCC.isEmpty() || distancePostCC.equals("NA") || distancePostCC.equals("0km")) {
                } else {
                    jsonObject.addProperty("distancePostCC", Double.parseDouble(distancePostCC.replace("km", "")));
                }

                jsonObject.addProperty("engineRunTime", commandResult.get("ENGINE_RUNTIME"));
                jsonObject.addProperty("fuelLevel", commandResult.get("FUEL_LEVEL"));
                jsonObject.addProperty("insuranceReminderDate", vehiclemodel.getInsuranceReminderDate());

                if (lastKnowLocation != null) {
                    jsonObject.addProperty("latitude", lastKnowLocation.getLatitude());
                    jsonObject.addProperty("longitude", lastKnowLocation.getLongitude());
                } else {
                    jsonObject.addProperty("latitude", "0.0");
                    jsonObject.addProperty("longitude", "0.0");
                }
                jsonObject.addProperty("maf", commandResult.get("MAF"));

                if (commandResult.get("VIN") != null && !commandResult.get("VIN").isEmpty()) {
                    jsonObject.addProperty("vin", commandResult.get("VIN"));
                    jsonObject.addProperty("vinAvl", true);
                } else {
                    jsonObject.addProperty("vinAvl", false);

                }

               /* jsonObject.addProperty("odoMeterRdg", vehiclemodel.getOdoMeterRdg());
                jsonObject.addProperty("pollutionReminderDate", vehiclemodel.getPollutionReminderDate());
                jsonObject.addProperty("serviceReminderkm", vehiclemodel.getServiceReminderkm());*/

                Log.e(TAG, purpose + " Input is : " + jsonObject);
                call = apiService.uploadFCMToken(user.getUserAccessToken(), jsonObject);

          /*  {
                "distancePostCC":0,
                    "engineRunTime":"string",
                    "fuelLevel":"string",
                    "insuranceReminderDate":"yyyy-MM-dd HH:mm:ss",
                    "latitude":"string",
                    "longitude":"string",
                    "maf":"string",
                    "odoMeterRdg":0,
                    "pollutionReminderDate":"yyyy-MM-dd HH:mm:ss",
                    "serviceReminderkm":0
            }*/

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
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.e(TAG, "Response is succesful");
                        String responseStr = response.body().string();
                        Log.e(TAG, purpose + " Response : " + responseStr);

                        switch (value) {
                            case 0:
                                //Trip upload succesfully.
                                break;

                            case 1:
                                //1 stands for update trip response
                                break;

                            case 2:
                                // 2 stands for end trip response
                                new DatabaseHelper(getApplicationContext()).deleteTripCSvEntry(tripSqlId);
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
                        stopSelf();

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
        jsonObject.addProperty("tripDistance", Integer.parseInt(commandResult.get("TRIP_DISTANCE")));


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
            for (int i = vehciclesArray.length() - 1; i >= 0; i--) {
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
    }




    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Create custom notification for Android O and higher.
            String NOTIFICATION_CHANNEL_ID = getPackageName();
            String channelName = "My Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            // Create the intent that will start your app
            Intent intent = new Intent(this, GypseeMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            // Build the notification
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Fuelshine")
                    .setContentText("Ready to drive safely and fuel efficiently")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Ready to drive safely and fuel efficiently")
                            .setBigContentTitle("Fuelshine")) // Set the big title here
                    .build();

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                startForeground(2, notification);
            } else {
                startForeground(2, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION);
            }
        } else {
            startForeground(1, new Notification());
        }
    }



}
