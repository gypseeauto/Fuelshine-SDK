package com.gypsee.sdk.fragments;


import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.databinding.FragmentTripdetailsBinding;
import com.gypsee.sdk.dialogs.GiveFeedbackDialog;
import com.gypsee.sdk.helpers.BluetoothHelperClass;
import com.gypsee.sdk.helpers.DirectionsJSONParser;
import com.gypsee.sdk.models.GameLevelModel;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;
import com.gypsee.sdk.trips.TripRecord;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;

    TripRecord tripRecord;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    FragmentTripdetailsBinding fragmentTripdetailsBinding;
    private LatLng mOrigin;
    private LatLng mDestination;
    private MarkerOptions mMarkerOptions;
    private Polyline mPolyline;
    private boolean isFuelPriceVisible = false;
    private boolean isPotentialFuelSave = false;
    private boolean isKmVisible = false;
    private boolean isTextKmVisible = false;
    private boolean isEcoTrack = false;
    private ArrayList<GameLevelModel> gamelevelArray;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentTripdetailsBinding = DataBindingUtil.setContentView(this, R.layout.fragment_tripdetails);
        tripRecord = getIntent().getParcelableExtra(TripRecord.class.getSimpleName());
        gamelevelArray = getIntent().getParcelableExtra("level");
        user = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, this).getUser();

        if (tripRecord == null) {
            Toast.makeText(this, "Unknown error occured. Contact gypsee team.", Toast.LENGTH_LONG).show();
            finish();
        }

        setDate();
        setUpToolBar();
        callServer(getString(R.string.fetchTripLatLong).replace("tripId", tripRecord.getId()), "Fetch single trip lat,lng", 0);

        callServer(getResources().getString(R.string.fetchDrivingAlertUrl).replace("userid", user.getUserId()), "Fetch Driving Alerts", 4);


        callServer(getResources().getString(R.string.fuelPrice).replace("{","").replace("}","").replace("tripId", tripRecord.getId()), "Fetch Fuel Price", 3);

        String Mileage = String.valueOf(tripRecord.getMileage());

        Log.e("TripMileage",Mileage);
        Log.e("singleTripRecord",tripRecord.toString());

        fragmentTripdetailsBinding.recenterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!routeCoordinates.isEmpty()) {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (LatLng latLng : routeCoordinates) {
                        builder.include(latLng);
                    }
                    LatLngBounds bounds = builder.build();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                } else {
                    Toast.makeText(MapsActivity.this, "Route not available", Toast.LENGTH_SHORT).show();
                }
            }
        });


//        fragmentTripdetailsBinding.kmValue2.setText(tripRecord.getDistanceCovered());
//        fragmentTripdetailsBinding.kmValue2.setText(tripRecord.getDistanceCovered());
//        fragmentTripdetailsBinding.safeKmValue.setText(tripRecord.getSafeKm());
//        fragmentTripdetailsBinding.safePerValue.setText(tripRecord.getSafeKm());
        fragmentTripdetailsBinding.property1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFuelPriceVisible) {
                    // If fuel price is visible, hide it and show down arrow
                    fragmentTripdetailsBinding.fuelPriceText1.setVisibility(View.GONE);
                    fragmentTripdetailsBinding.downArrow.setVisibility(View.VISIBLE);
                    fragmentTripdetailsBinding.upArrow.setVisibility(View.GONE);
                    isFuelPriceVisible = false;
                } else {
                    // If fuel price is not visible, show it and hide down arrow
                    fragmentTripdetailsBinding.fuelPriceText1.setVisibility(View.VISIBLE);
                    fragmentTripdetailsBinding.downArrow.setVisibility(View.GONE);
                    fragmentTripdetailsBinding.upArrow.setVisibility(View.VISIBLE);
//                    // Set top margin to 0
//                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fragmentTripdetailsBinding.fuelPriceText1.getLayoutParams();
//                    params.topMargin = 0;
//                    fragmentTripdetailsBinding.fuelPrice.setLayoutParams(params);
                    isFuelPriceVisible = true;
                }
            }
        });


        fragmentTripdetailsBinding.property2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isKmVisible) {
                    // If fuel price is visible, hide it and show down arrow
                    fragmentTripdetailsBinding.kmText1.setVisibility(View.GONE);
                    fragmentTripdetailsBinding.downArrow1.setVisibility(View.VISIBLE);
                    fragmentTripdetailsBinding.upArrow1.setVisibility(View.GONE);
                    isKmVisible = false;
                } else {
                    // If fuel price is not visible, show it and hide down arrow
                    fragmentTripdetailsBinding.kmText1.setVisibility(View.VISIBLE);
                    fragmentTripdetailsBinding.downArrow1.setVisibility(View.GONE);
                    fragmentTripdetailsBinding.upArrow1.setVisibility(View.VISIBLE);
//                    // Set top margin to 0
//                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fragmentTripdetailsBinding.fuelPriceText1.getLayoutParams();
//                    params.topMargin = 0;
//                    fragmentTripdetailsBinding.fuelPrice.setLayoutParams(params);
                    isKmVisible = true;
                }
            }
        });




//        float amount = Float.parseFloat(tripRecord.getTripSavingsCommission());
//
//        if (amount <= 0){
//            fragmentTripdetailsBinding.data.setBackground(getResources().getDrawable(R.drawable.data_red));
//            fragmentTripdetailsBinding.fuelValue.setTextColor(getColor(R.color.white));
//            fragmentTripdetailsBinding.fuelValue.setText("- ₹" + tripRecord.getTripSavingsCommission());
//        }
//        else {
//            fragmentTripdetailsBinding.fuelValue.setText("+ ₹" + tripRecord.getTripSavingsCommission());
//        }

        fragmentTripdetailsBinding.property3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTextKmVisible) {
                    // If fuel price is visible, hide it and show down arrow
                    fragmentTripdetailsBinding.tripKmText.setVisibility(View.GONE);
                    fragmentTripdetailsBinding.downArrow2.setVisibility(View.VISIBLE);
                    fragmentTripdetailsBinding.upArrow2.setVisibility(View.GONE);
                    isTextKmVisible = false;
                } else {
                    // If fuel price is not visible, show it and hide down arrow
                    fragmentTripdetailsBinding.tripKmText.setVisibility(View.VISIBLE);
                    fragmentTripdetailsBinding.downArrow2.setVisibility(View.GONE);
                    fragmentTripdetailsBinding.upArrow2.setVisibility(View.VISIBLE);
//                    // Set top margin to 0
//                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fragmentTripdetailsBinding.fuelPriceText1.getLayoutParams();
//                    params.topMargin = 0;
//                    fragmentTripdetailsBinding.fuelPrice.setLayoutParams(params);
                    isTextKmVisible = true;
                }
            }
        });

        fragmentTripdetailsBinding.property5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isFuelPriceVisible) {
                    // If fuel price is visible, hide it and show down arrow
                    fragmentTripdetailsBinding.tripKmText2.setVisibility(View.GONE);
                    fragmentTripdetailsBinding.downArrow4.setVisibility(View.VISIBLE);
                    fragmentTripdetailsBinding.upArrow4.setVisibility(View.GONE);
                    isFuelPriceVisible = false;
                } else {
                    // If fuel price is not visible, show it and hide down arrow
                    fragmentTripdetailsBinding.tripKmText2.setVisibility(View.VISIBLE);
                    fragmentTripdetailsBinding.downArrow4.setVisibility(View.GONE);
                    fragmentTripdetailsBinding.upArrow4.setVisibility(View.VISIBLE);
//                    // Set top margin to 0
//                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fragmentTripdetailsBinding.fuelPriceText1.getLayoutParams();
//                    params.topMargin = 0;
//                    fragmentTripdetailsBinding.fuelPrice.setLayoutParams(params);
                    isFuelPriceVisible = true;
                }


            }
        });

        fragmentTripdetailsBinding.property4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEcoTrack) {
                    // If fuel price is visible, hide it and show down arrow
                    fragmentTripdetailsBinding.tripKmText1.setVisibility(View.GONE);
                    fragmentTripdetailsBinding.downArrow3.setVisibility(View.VISIBLE);
                    fragmentTripdetailsBinding.upArrow3.setVisibility(View.GONE);
                    isEcoTrack = false;
                } else {
                    // If fuel price is not visible, show it and hide down arrow
                    fragmentTripdetailsBinding.tripKmText1.setVisibility(View.VISIBLE);
                    fragmentTripdetailsBinding.downArrow3.setVisibility(View.GONE);
                    fragmentTripdetailsBinding.upArrow3.setVisibility(View.VISIBLE);
//                    // Set top margin to 0
//                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fragmentTripdetailsBinding.fuelPriceText1.getLayoutParams();
//                    params.topMargin = 0;
//                    fragmentTripdetailsBinding.fuelPrice.setLayoutParams(params);
                    isEcoTrack = true;
                }
            }
        });

        fragmentTripdetailsBinding.backButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        String safeKm = String.valueOf(tripRecord.getSafeKm());

//        String safeKm = tripRecord.getTripSavedAmount();
//
//        if (tripRecord.getSafeKm() < 0){
//            fragmentTripdetailsBinding.fuelValue.setText("+ ₹" + safeKm+".00");
//        } else if (tripRecord.getSafeKm() == 0) {
//            fragmentTripdetailsBinding.fuelValue.setText("+ ₹" + safeKm+".00");
//        } else {
//            fragmentTripdetailsBinding.fuelValue.setText("+ ₹" + safeKm+".00");
//        }


        fragmentTripdetailsBinding.contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFeedbackDialog();
            }
        });


    }

    private void showFeedbackDialog() {
        try {
            if (tripRecord == null || tripRecord.getId() == null) {
                Log.e("MapsActivity", "tripRecord or tripRecord.getId() is null");
                return;
            }

            GiveFeedbackDialog giveFeedbackDialog = new GiveFeedbackDialog(this, "trip", tripRecord.getId());
            giveFeedbackDialog.setCanceledOnTouchOutside(false);
            giveFeedbackDialog.setCancelable(false);
            giveFeedbackDialog.show();

            // Check if the window is not null before setting the background
            if (giveFeedbackDialog.getWindow() != null) {
                giveFeedbackDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
            } else {
                Log.e("MapsActivity", "Dialog window is null");
            }
        } catch (Exception e) {
            Log.e("MapsActivity", "Exception in showFeedbackDialog", e);
        }
    }


//    private void showFeedbackDialog() {
//        GiveFeedbackDialog giveFeedbackDialog = new GiveFeedbackDialog(this, "trip", tripRecord.getId());
//        giveFeedbackDialog.setCanceledOnTouchOutside(false);
//        giveFeedbackDialog.setCancelable(false);
//        giveFeedbackDialog.show();
//
//        giveFeedbackDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
//    }

    private void initGoogleMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Add the route and marker sources to the map
     */

    String TAG = MapsActivity.class.getSimpleName();

    private void setDate() {

//        try {
//
//            SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy");
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//            Date date1 = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(tripRecord.getStartDate());
////            fragmentTripdetailsBinding.tripdetailsCardOne.tvDate.setText(format.format(Objects.requireNonNull(date1)));
//            fragmentTripdetailsBinding.dateTime.setText(format.format(Objects.requireNonNull(date1)));

//            fragmentTripdetailsBinding.tripdetailsCardOne.tripDurationTv.setText(/*TimeUtils.calcDiffTime(simpleDateFormat.parse(tripRecord.getStartDate()).getTime(), simpleDateFormat.parse(tripRecord.getEndDate()).getTime())*/ TimeUtils.getTimeIndhms(tripRecord.getTripDuration()*60));
//            fragmentTripdetailsBinding.tripDurationTv.setText(/*TimeUtils.calcDiffTime(simpleDateFormat.parse(tripRecord.getStartDate()).getTime(), simpleDateFormat.parse(tripRecord.getEndDate()).getTime())*/ TimeUtils.getTimeIndhms(tripRecord.getTripDuration()*60));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }


        String dateString = tripRecord.getStartDate();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM dd - h:mm a");
        try {
            Date date = inputFormat.parse(dateString);
            String convertedDate = outputFormat.format(date);
            fragmentTripdetailsBinding.dateTime.setText(convertedDate + " / ");
        } catch (Exception e) {
            Log.e("Error occurred: ", e.getMessage());
        }


        String timing = String.valueOf(tripRecord.getTripDuration());

        Log.e("Timing",timing);

//        fragmentTripdetailsBinding.tripDurationTv.setText(TimeUtils.getTimeIndhms(tripRecord.getTripDuration()*60));
        fragmentTripdetailsBinding.tripDurationTv.setText(timing + " Min");

        String walletAmount = String.valueOf(tripRecord.getTripSavingsCommission());

        if (walletAmount.equals(null) || walletAmount.equals("null")){
            fragmentTripdetailsBinding.walletAmount.setText("0.0");
        }
        else {

            fragmentTripdetailsBinding.walletAmount.setText(walletAmount);
        }


//        int totalKm = Integer.parseInt(tripRecord.getDistanceCovered());
//        int safeKm = tripRecord.getSafeKm();
//
//        int safePercent = (safeKm/totalKm) * 100;
//
//        fragmentTripdetailsBinding.safePerValue.setText(safePercent+"%");



        double totalKm = Double.parseDouble(tripRecord.getDistanceCovered());
        int safeKm = tripRecord.getSafeKm();

        double safePercent = (safeKm / totalKm) * 100;

        fragmentTripdetailsBinding.safePerValue.setText(String.format("%.2f%%", safePercent));



//        fragmentTripdetailsBinding.tripdetailsCardOne.sourceDestinationTv.setText(tripRecord.getStartLocationName() + " ➥ \n" +
//                tripRecord.getDestinationName());

        fragmentTripdetailsBinding.source.setText(tripRecord.getStartLocationName());

        fragmentTripdetailsBinding.destination.setText(tripRecord.getDestinationName());

        fragmentTripdetailsBinding.safeKmValue.setText(String.valueOf(tripRecord.getSafeKm()));

//        fragmentTripdetailsBinding.tripdetailsCardOne.tripDistanceTv.setText(tripRecord.getDistanceCovered() + " KM");

        fragmentTripdetailsBinding.distance.setText(tripRecord.getDistanceCovered() + " KM");

//        fragmentTripdetailsBinding.tripdetailsCardOne.alertCountTv.setText(tripRecord.getAlertCount() + "");

    }

    private void setUpToolBar() {
//        fragmentTripdetailsBinding.toolbarlayout.setTitle("Trip detail");
//        fragmentTripdetailsBinding.toolbarlayout.toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);
//        fragmentTripdetailsBinding.toolbarlayout.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    private void callServer(String url, String purpose, final int value) {

        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);

        Call<ResponseBody> call;
        JsonObject jsonInput = new JsonObject();

        JsonArray jsonArray = new JsonArray();


        switch (value) {
            case 0:
                call = apiService.uploadVehDetails(user.getUserAccessToken(), jsonInput);
                break;

            case 1:
                HashMap<String, Object> queryMap = new HashMap<>();
                queryMap.put("tripId", tripRecord.getId());
                queryMap.put("page", 0);
                queryMap.put("size", 100);
                call = apiService.getVehicleAlerts(user.getUserAccessToken(), queryMap);
                break;

            case 2:
                call = apiService.getVehicleAlertsForTrip(user.getUserAccessToken());
                break;
            case 3:
                call = apiService.getFuelPrice(user.getUserAccessToken());
                break;

            case 4:
                call = apiService.getTripDrivingAlerts(user.getUserAccessToken(), 10, 0, user.getUserId(),tripRecord.getId());
                break;

            default:

                call = apiService.getDocTypes(user.getUserAccessToken(), true);
                break;
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.e(TAG, purpose + " Response is success");
                        String responseStr = response.body().string();
                        Log.e(TAG, purpose + " Response :" + responseStr);

                        switch (value) {
                            case 0:
                                parselatLngOfTrip(responseStr);
                                break;
                            case 1:
                                parseFetchDrivingALerts(responseStr);
                                break;
                            case 2:
                                parseFetchVehicleAlerts(responseStr);
                                break;
                            case 3:
                                parseFuelPrice(responseStr);

                        }

                    } else {
                        Log.e(TAG, purpose + " Response is not succesfull");
                        String errorBody = response.errorBody().string();
                        Log.e(TAG, purpose + " Response :" + errorBody);

                     /*   save.setEnabled(true);
                        int responseCode = response.code();
                        if (responseCode == 401 || responseCode == 403) {
                            //include logout functionality
                            Utils.clearAllData(context);
                            return;
                        }
                        if (response.errorBody() == null) {
                            Log.e("Error code 400", "" + response.errorBody().string());
                        } else {
                            JSONObject jsonObject = new JSONObject(errorBody);
                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            Log.e("Error code 400", "" + "Response is empty");
                        }*/

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

               /* if (response.body() != null) {
                    try {
                       // String responseString = response.body().string();
                     Log.e(TAG,"Response : "+ responseString);

                        progressBar.setVisibility(View.GONE);
                        loginBtn.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {

                    Toast.makeText(LoginActivity.this,"Please try again",Toast.LENGTH_SHORT).show();
                }*/

            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //Logger.ErrorLog(TAG, "error here since request failed");

               /* progressLayout.setVisibility(View.GONE);
                save.setEnabled(true);
                save.setVisibility(View.VISIBLE);*/

                if (t.getMessage().contains("Unable to resolve host")) {
                    Toast.makeText(MapsActivity.this, "Please Check your internet connection", Toast.LENGTH_LONG).show();
                }

                if (call.isCanceled()) {

                } else {

                }
            }
        });
    }

    private void parseFuelPrice(String response) throws JSONException {

        JSONObject jsonObject = new JSONObject(response);

        if (!jsonObject.has("data")){
            Toast.makeText(this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject fuelPriceObj = jsonObject.getJSONObject("data");


        String totalDeficiencyWeightage = fuelPriceObj.getString("totalDeficiencyWeightage");
        String expectedFuelCost = fuelPriceObj.getString("expectedFuelCost");
        String actualFuelCost = fuelPriceObj.getString("actualFuelCost");
        String efficiency_deficiencyPercentage = fuelPriceObj.getString("efficiency_deficiencyPercentage");
        String fuelPrice = fuelPriceObj.getString("fuelPrice");
        String weightagesData = fuelPriceObj.getString("weightagesData");
        String tripDistanceKm = fuelPriceObj.getString("tripDistanceKm");
        String epaArAiMileage = fuelPriceObj.getString("epaArAiMileage");
        String ecoSpeedStartRange = fuelPriceObj.getString("ecoSpeedStartRange");
        String ecoSpeedEndRange = fuelPriceObj.getString("ecoSpeedEndRange");
        String overSpeedBaseWeightage = fuelPriceObj.getString("overSpeedBaseWeightage");
        String overSpeedStepWeightage = fuelPriceObj.getString("overSpeedStepWeightage");
        String efficiencyWeightage = fuelPriceObj.getString("efficiencyWeightage");
        String mileageObtained = fuelPriceObj.getString("mileageObtained");
        String netWeightage = fuelPriceObj.getString("netWeightage");
        String fuelSavingAmount = fuelPriceObj.getString("fuelSavingAmount");
        String fuelConsumedInLiters = fuelPriceObj.getString("fuelConsumedInLiters");
        String safeKmPercentage = fuelPriceObj.getString("safeKmPercentage");
        String fuelSaving = fuelPriceObj.getString("fuelSaving");
        String co2Emission = fuelPriceObj.getString("co2Emission");



        fragmentTripdetailsBinding.fuelAmount.setText(fuelSavingAmount);
        fragmentTripdetailsBinding.fuelPriceValue.setText("₹" +fuelPrice);
//        fragmentTripdetailsBinding.safePerValue.setText(safeKmPercentage);
        fragmentTripdetailsBinding.kmValue2.setText(tripDistanceKm);

        float emission = Float.parseFloat(co2Emission);

        if (emission >= 0){
            fragmentTripdetailsBinding.tripKmValue1.setText("+ " + co2Emission + "KG");
            fragmentTripdetailsBinding.tripKmValue1.setTextColor(getResources().getColor(R.color.red));
            fragmentTripdetailsBinding.tripKmText1.setText(R.string.save_our_planet_driving_fuel_efficiently);


        }else{
            fragmentTripdetailsBinding.tripKmValue1.setText(co2Emission + "KG");
            fragmentTripdetailsBinding.tripKmValue1.setTextColor(getResources().getColor(R.color.light_green));
            fragmentTripdetailsBinding.tripKmText1.setText(R.string.you_are_making_our_planet_greener);
        }




        float amount = Float.parseFloat(fuelSavingAmount);

        if (amount < 0){
            fragmentTripdetailsBinding.data.setBackground(getResources().getDrawable(R.drawable.data_red));
            fragmentTripdetailsBinding.fuelValue.setTextColor(getColor(R.color.white));
            fragmentTripdetailsBinding.fuelValue.setText(" ₹" + fuelSavingAmount );
        }else if (amount == 0){
            fragmentTripdetailsBinding.data.setBackground(getResources().getDrawable(R.drawable.data));
            fragmentTripdetailsBinding.fuelValue.setText("+ ₹" +  fuelSavingAmount );
        }
        else {
            fragmentTripdetailsBinding.data.setBackground(getResources().getDrawable(R.drawable.data));
            fragmentTripdetailsBinding.fuelValue.setText( "+ ₹" +  fuelSavingAmount );
        }



        fragmentTripdetailsBinding.tripKmValue.setText(mileageObtained);
        fragmentTripdetailsBinding.kmValue.setText(epaArAiMileage);






        if (emission > 0){
            fragmentTripdetailsBinding.tripKmValue2.setText(co2Emission);
            fragmentTripdetailsBinding.tripKmValue2.setTextColor(getResources().getColor(R.color.red));
            fragmentTripdetailsBinding.tripKmText2.setText("You could have saved Rupees "+ emission + "on this trip driving fuel efficiently.");
        }else if (emission == 0){
            fragmentTripdetailsBinding.tripKmValue2.setText(co2Emission);
            fragmentTripdetailsBinding.tripKmValue2.setTextColor(getResources().getColor(R.color.light_green));
            fragmentTripdetailsBinding.tripKmText2.setText(R.string.good_job_fuel_use);
        }
        else{
            fragmentTripdetailsBinding.tripKmValue2.setText(co2Emission);
            fragmentTripdetailsBinding.tripKmValue2.setTextColor(getResources().getColor(R.color.light_green));
            fragmentTripdetailsBinding.tripKmText2.setText(R.string.poor_driving_habbits);
        }




    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;


        // Enable built-in zoom controls
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);



        //We will zoom more if the distance covered is less than 5 KM.

        mMap.setMinZoomPreference(Double.parseDouble(tripRecord.getDistanceCovered()) < 5 ? 14.0f : 11.0f);
        mMap.setMaxZoomPreference(Double.parseDouble(tripRecord.getDistanceCovered()) < 5 ? 16.0f : 14.0f);


        drawMapUsingLatLngOfTrip();

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(Double.parseDouble(tripRecord.getStartLat()), Double.parseDouble(tripRecord.getStartLong()));
        mMap.addMarker(new MarkerOptions()
                .position(sydney)
                .icon(BitmapDescriptorFactory.fromBitmap(BluetoothHelperClass.convertToBitmap(ContextCompat.getDrawable(MapsActivity.this, R.drawable.ic_map_origin), 50, 50)))
                .title("start"));


        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        sydney = new LatLng(Double.parseDouble(tripRecord.getEndLat()), Double.parseDouble(tripRecord.getEndLong()));
        mMap.addMarker(new MarkerOptions()
                .position(sydney)
                .icon(BitmapDescriptorFactory.fromBitmap(BluetoothHelperClass.convertToBitmap(ContextCompat.getDrawable(MapsActivity.this, R.drawable.ic_map_destination), 50, 50)))
                .title("Stop"));


        if (tripRecord.getAlertCount() > 0) {
            User user = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, this).getUser();

            callServer(getResources().getString(R.string.fetchDrivingAlertUrl).replace("userid", user.getUserId()), "Fetch Driving Alerts", 1);
        }

        callServer(getResources().getString(R.string.fetchVehicleAlertsByTripId).replace("tripId", tripRecord.getId()), "Fetch Vehicle Alerts", 2);

        //drawRoute();
        // getMyLocation();
    }


    private void getMyLocation() {

        // Getting LocationManager object from System Service LOCATION_SERVICE
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mOrigin = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mOrigin, 12));
                if (mOrigin != null && mDestination != null)
                    drawRoute();
            }
        };

        int currentApiVersion = Build.VERSION.SDK_INT;
        if (currentApiVersion >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED) {
                mMap.setMyLocationEnabled(true);
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0f, mLocationListener);

                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        mDestination = latLng;
                        mMap.clear();
                        mMarkerOptions = new MarkerOptions().position(mDestination).title("Destination");
                        mMap.addMarker(mMarkerOptions);
                        if (mOrigin != null && mDestination != null)
                            drawRoute();
                    }
                });

            } else {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, 100);
            }
        }
    }

    private void drawRoute() {

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(mOrigin, mDestination);

        Log.e(TAG, "Direction API : " + url);
        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + tripRecord.getStartLat() + "," + tripRecord.getStartLong();

        // Destination of route
        String str_dest = "destination=" + tripRecord.getEndLat() + "," + tripRecord.getEndLong();

        // Key
        String key = "key=" + getString(R.string.google_maps_key);


        //While adding the waypoints, we will see the ditance between 2 lat should be grater than 100meters for now.
        String waypoints = "waypoints=";


        for (int i = 0; i < actualroutePoints.size(); i++) {
            LatLng latLng = actualroutePoints.get(i);
            waypoints = waypoints + "via:" + latLng.latitude + "%2C" + latLng.longitude + "%7C";


        }
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + waypoints + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception on download", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * A class to download data from Google Directions URL
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("DownloadTask", "DownloadTask : " + data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Directions in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(getResources().getColor(R.color.theme_blue));
            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                if (mPolyline != null) {
                    mPolyline.remove();
                }
                mPolyline = mMap.addPolyline(lineOptions);

            } else
                Toast.makeText(getApplicationContext(), "No route is found", Toast.LENGTH_LONG).show();
        }
    }


    private void drawMapUsingLatLngOfTrip() {

        PolylineOptions lineOptions = new PolylineOptions();

        LatLng sydney = new LatLng(Double.parseDouble(tripRecord.getStartLat()), Double.parseDouble(tripRecord.getStartLong()));
        routeCoordinates.add(0, sydney);
        sydney = new LatLng(Double.parseDouble(tripRecord.getEndLat()), Double.parseDouble(tripRecord.getEndLong()));
        routeCoordinates.add(sydney);

        // Adding all the points in the route to LineOptions
        lineOptions.addAll(routeCoordinates);
        lineOptions.width(6);
        lineOptions.color(getResources().getColor(R.color.theme_blue));

        // Drawing polyline in the Google Map for the i-th route
        if (lineOptions != null) {
            if (mPolyline != null) {
                mPolyline.remove();
            }
            mPolyline = mMap.addPolyline(lineOptions);
        } else {
            Toast.makeText(getApplicationContext(), "No route is found", Toast.LENGTH_LONG).show();
        }
    }


    private List<LatLng> routeCoordinates;
    private List<LatLng> actualroutePoints = new ArrayList<>();

    private void parselatLngOfTrip(String responseStr) throws JSONException {

        routeCoordinates = new ArrayList<>();

        JSONObject jsonResponse = new JSONObject(responseStr);

        //Go to main Activity

        // ArrayList<String> values = new ArrayList<>();
        JSONArray drivingAlertsArray = jsonResponse.getJSONArray("data");

        for (int i = 0; i <= drivingAlertsArray.length() - 1; i++) {

            JSONObject jsonObject = drivingAlertsArray.getJSONObject(i);

            double lat, lng;

            //{"alert_Type":"Harsh Accelaration","alert_value ":"51 km\/hr","alert_description":"","g_force":"1.428m\/sec2","time_stamp":1598954550242,"time_interval":"1s","lat":12.9229433,"long":77.6471967,"impact":[32.484375,32.484375,32.546875,32.546875,32.59375,32.59375,32.703125,32.703125,32.625,32.625]}

            lat = jsonObject.getDouble("latitude");
            lng = jsonObject.getDouble("longitude");
            String createdOn = jsonObject.getString("createdOn");
            routeCoordinates.add(new LatLng(lat, lng));
        }

        initGoogleMap();

        /*Point origin = Point.fromLngLat(Double.parseDouble(tripRecord.getStartLong()), Double.parseDouble(tripRecord.getStartLat()));
        Point destination = Point.fromLngLat(Double.parseDouble(tripRecord.getEndLong()), Double.parseDouble(tripRecord.getEndLat()));
        getRoute(mapboxMap, origin, destination, routeCoordinates);

        List<Point> routeSubCoordinates = new ArrayList<>();

        for (int i = 1; i < routeCoordinates.size() - 1; i++) {

            //If origin eaulas to null, it means the items are greater than 24. We will make previous destination as origin of the new map.
            if (origin == null) {
                origin = destination;
                routeSubCoordinates.add(routeCoordinates.get(i));

            } else if (routeSubCoordinates.size() < 23) {

                routeSubCoordinates.add(routeCoordinates.get(i));

            } else {

                destination = Point.fromLngLat(routeCoordinates.get(i).longitude(), routeCoordinates.get(i).latitude());
                getRoute(mapboxMap, origin, destination, routeSubCoordinates);
                routeSubCoordinates = new ArrayList<>();
                origin = null;

                Log.e(TAG, "Drawing the map1 - " + routeSubCoordinates.size());

                return;
            }
        }

        int sizeofSubCordinates = routeSubCoordinates.size();

        if (origin == null) {
            //It means there are no other thing to plot
        } else {
            //Here if size =0, means we need to reverse destination & origin.Because, destination here is last route destination.
            if (sizeofSubCordinates == 0) {
                getRoute(mapboxMap, destination, origin, routeSubCoordinates);

            } else {
                //If the size is greater than 1, we will get last one from sub array.
                destination = Point.fromLngLat(Double.parseDouble(tripRecord.getEndLong()), Double.parseDouble(tripRecord.getEndLat()));
                getRoute(mapboxMap, origin, destination, routeSubCoordinates);
            }
            Log.e(TAG, "Drawing the map2 - " + routeSubCoordinates.size());


        }*/

    }

    private void addCoordinatesAfterSpan(int spanLength) {

        actualroutePoints.clear();
        actualroutePoints.add(routeCoordinates.get(spanLength / 2));

        for (int i = 0; i < routeCoordinates.size(); i++) {
            actualroutePoints.add(routeCoordinates.get(i));
            i = i + spanLength;
        }
    }

//    private void parseFetchDrivingALerts(String response) throws JSONException {
//
//        JSONObject jsonResponse = new JSONObject(response);
//
//        //Go to main Activity
//
//        // ArrayList<String> values = new ArrayList<>();
//        JSONArray drivingAlertsArray = jsonResponse.getJSONArray("drivingAlerts");
//
//        for (int i = drivingAlertsArray.length() - 1; i >= 0; i--) {
//
//            JSONObject jsonObject = drivingAlertsArray.getJSONObject(i);
//            JSONObject alertData = new JSONObject(jsonObject.getString("data"));
//            String alertType, alertValue, timeInterval, gForce, impact;
//            long timeStamp;
//            double lat, lng;
//            Log.e(TAG, "Alerts is : " + alertData);
//
//            //{"alert_Type":"Harsh Accelaration","alert_value ":"51 km\/hr","alert_description":"","g_force":"1.428m\/sec2","time_stamp":1598954550242,"time_interval":"1s","lat":12.9229433,"long":77.6471967,"impact":[32.484375,32.484375,32.546875,32.546875,32.59375,32.59375,32.703125,32.703125,32.625,32.625]}
//
//            alertType = alertData.getString("alert_Type");
//            alertValue = alertData.has("alert_value ") ? alertData.getString("alert_value ") : alertData.getString("alert_value");
//            timeInterval = alertData.getString("time_interval");
//            gForce = alertData.getString("g_force");
//            impact = alertData.getString("impact");
//            timeStamp = alertData.getLong("time_stamp");
//            lat = alertData.getDouble("lat");
//            lng = alertData.getDouble("long");
//            Log.e(TAG, "Alert Value from server : " + alertValue);
//
//            LatLng latLng = new LatLng(lat, lng);
//            mMap.addMarker(new MarkerOptions()
//                    .position(latLng)
//                    .icon(BitmapDescriptorFactory.fromBitmap(BluetoothHelperClass.convertToBitmap(ContextCompat.getDrawable(MapsActivity.this, checkAlertType(alertType)), 50, 50)))
//                    .title(alertType));
//
//        }
//
//    }
//
    private void parseFetchVehicleAlerts(String responseStr){

        try {

            JSONObject jsonObject = new JSONObject(responseStr);
            JSONArray alertsArray = jsonObject.getJSONArray("alerts");

            for (int i=0; i<alertsArray.length(); i++){
                JSONObject alertObject = alertsArray.getJSONObject(i);
                String troubleCode, alertLat, alertLng;

                troubleCode = alertObject.getString("troubleCode");
                alertLat = alertObject.getString("latitudeOnAlert");
                alertLng = alertObject.getString("longitudeOnAlert");

                LatLng latLng = new LatLng(Double.parseDouble(alertLat), Double.parseDouble(alertLng));
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(BluetoothHelperClass.convertToBitmap(ContextCompat.getDrawable(MapsActivity.this, R.drawable.ic_vehicle_alert), 50, 50)))
                        .title("Trouble Code Found")
                        .snippet(troubleCode));

            }


        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void parseFetchDrivingALerts(String response) throws JSONException {
        int harshAccelerationCount = 0;
        int harshBrakingCount = 0;
        int overspeedingCount = 0;
        int textDriveCount = 0;

        JSONObject jsonResponse = new JSONObject(response);
        JSONArray drivingAlertsArray = jsonResponse.getJSONArray("drivingAlerts");

        for (int i = 0; i < drivingAlertsArray.length(); i++) {
            JSONObject jsonObject = drivingAlertsArray.getJSONObject(i);
            JSONObject alertData = new JSONObject(jsonObject.getString("data"));

            String alertType = alertData.getString("alert_Type");

            switch (alertType) {
                case "Harsh Accelaration":
                    harshAccelerationCount++;
                    break;
                case "Harsh Braking":
                    harshBrakingCount++;
                    break;
                case "Overspeed":
                    overspeedingCount++;
                    break;
                case "text & Driving":
                    textDriveCount++;
                    break;
                default:

                    break;
            }

            // Get other alert details
            String alertValue = alertData.has("alert_value ") ? alertData.getString("alert_value ") : alertData.getString("alert_value");
            String timeInterval = alertData.getString("time_interval");
            String gForce = alertData.getString("g_force");
            String impact = alertData.getString("impact");
            long timeStamp = alertData.getLong("time_stamp");
            double lat = alertData.getDouble("lat");
            double lng = alertData.getDouble("long");

            Log.e(TAG, "Alert Type: " + alertType);
            Log.e(TAG, "Alert Value from server: " + alertValue);

            // Add marker to map
            LatLng latLng = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(BluetoothHelperClass.convertToBitmap(ContextCompat.getDrawable(MapsActivity.this, checkAlertType(alertType)), 50, 50)))
                    .title(alertType));
        }

        String harshAcc = String.valueOf(harshAccelerationCount);
        String harshBrk = String.valueOf(harshAccelerationCount);
        String speeding = String.valueOf(overspeedingCount);
        String textAndDrv = String.valueOf(textDriveCount);

        fragmentTripdetailsBinding.harshAccValue.setText(harshAcc);
        fragmentTripdetailsBinding.speedValue.setText(speeding);
        fragmentTripdetailsBinding.harshBrkValue.setText(harshBrk);
        fragmentTripdetailsBinding.drvTextValue.setText(textAndDrv);

        // Print or use the counts as needed after processing all alerts
        Log.e("Harsh Acceleration Alerts", "Number of Harsh Acceleration Alerts: " + harshAccelerationCount);
        Log.e("Harsh Braking Alerts", "Number of Harsh Braking Alerts: " + harshBrakingCount);
        Log.e("Overspeeding Alerts", "Number of Overspeeding Alerts: " + overspeedingCount);
//        Log.e("text & Driving Alerts", "Number of text and driving Alerts: " + textDriveCount);
    }


    private int checkAlertType(String alerType) {

        int drawable = R.drawable.ic_map_highrpm;
        switch (alerType) {
            case "High RPM":
                break;

            case "Harsh Braking":
                drawable = R.drawable.ic_map_harshbraking;
                break;
            case "Harsh Accelaration":
                drawable = R.drawable.ic_map_harshaccelaration;
                break;
            case "Overspeed":
                drawable = R.drawable.ic_map_overspeed;
                break;
        }

        return drawable;
    }


}
