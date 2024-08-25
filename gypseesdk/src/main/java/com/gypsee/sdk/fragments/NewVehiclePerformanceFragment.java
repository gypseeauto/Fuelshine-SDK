package com.gypsee.sdk.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.gypsee.sdk.Adapters.AlertsArrayAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.database.DatabaseHelper;
import com.gypsee.sdk.databinding.FragmentNewVehiclePerformanceBinding;
import com.gypsee.sdk.firebase.FirebaseLogEvents;
import com.gypsee.sdk.jsonParser.ResponseFromServer;
import com.gypsee.sdk.models.AlertsArray;
import com.gypsee.sdk.models.GameLevelModel;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.models.VehicleHealthDataModel;
import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;
import com.gypsee.sdk.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.text.SimpleDateFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewVehiclePerformanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewVehiclePerformanceFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private ImageView[] ivArrayDotsPager;

    public NewVehiclePerformanceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewVehiclePerformanceFragment.
     */
    // TODO: Rename and change types and number of parameters
//    public static NewVehiclePerformanceFragment newInstance(String param1, String param2) {
//        NewVehiclePerformanceFragment fragment = new NewVehiclePerformanceFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }



    FragmentNewVehiclePerformanceBinding fragmentNewVehiclePerformanceBinding;
    MyPreferenece myPreferenece;
    private User user;

    public static NewVehiclePerformanceFragment newInstance() {

        return new NewVehiclePerformanceFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        FirebaseLogEvents.firebaseLogEvent("checked_car_health",context);
        fragmentNewVehiclePerformanceBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_vehicle_performance, container, false);
        context = getContext();

        myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);

        user = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, requireContext()).getUser();




        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Define the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Format the current date
        String formattedDate = currentDate.format(formatter);

        // Print the formatted date
        System.out.println("Current date: " + formattedDate);

        Log.e("userdate",user.getCreatedOn());


        String originalDateTimeString = user.getCreatedOn();

        // Define formatter for parsing the original datetime string
        DateTimeFormatter originalFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Parse the original datetime string into LocalDateTime object
        LocalDateTime originalDateTime = LocalDateTime.parse(originalDateTimeString, originalFormatter);

        // Define formatter for the desired format
        DateTimeFormatter desiredFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Format the LocalDateTime object into the desired format
        String formattedPeriodFromDate = originalDateTime.format(desiredFormatter);




        callPerformanceServer(getResources().getString(R.string.performance).replace("{","").replace("}","").replace("userId",user.getUserId()),"Get Performance",formattedDate,formattedDate,2);

        callGameServer(getResources().getString(R.string.gameLevel).replace("{","").replace("}","").replace("userId",user.getUserId()),"Get Game Level",formattedDate,formattedDate);


//        initVehicleHealthOps();

        fragmentNewVehiclePerformanceBinding.dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDropdownMenu(view);
            }
        });

        //Backward button functionality.
//        fragmentNewVehiclePerformanceBinding.backBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((AppCompatActivity) context).finish();
//            }
//        });

        /*if(myPreferenece.getLastTripVhsScore() == 100){
            fragmentNewVehiclePerformanceBinding.shareVHS.setVisibility(View.VISIBLE);
        } else {
            fragmentNewVehiclePerformanceBinding.shareVHS.setVisibility(View.GONE);
        }*/

//        fragmentNewVehiclePerformanceBinding.shareVHS.setVisibility(View.GONE);

////          mileage visibility
//        if (myPreferenece.getLastTripMileage() == 0) {
//            fragmentNewVehiclePerformanceBinding.mileage.setVisibility(View.GONE);
//        } else {
//            fragmentNewVehiclePerformanceBinding.mileage.setVisibility(View.VISIBLE);
//            fragmentNewVehiclePerformanceBinding.mileage.setText("Mileage: " + myPreferenece.getLastTripMileage() + "kmpl");
//        }

        //shareButtonOnclick();

//        fragmentNewVehiclePerformanceBinding.shareVHS.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                String shareMessage = "Hey, my car health score is " + myPreferenece.getLastTripVhsScore() + ". Get your car health score at https://play.google.com/store/apps/details?id=in.gypsee.customer";
//                double vhsScore = myPreferenece.getLastTripVhsScore();
//
//                if (vhsScore == 100) {
//                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
//                            R.drawable.hundred);
//                    String sImageUrl = MediaStore.Images.Media.insertImage(context.getContentResolver(), icon, "title", "description");
//                    Uri savedImageURI = Uri.parse(sImageUrl);
//
//                    Intent shareIntent = new Intent();
//                    shareIntent.setAction(Intent.ACTION_SEND);
//                    shareIntent.setType("image/*");
//                    shareIntent.putExtra(Intent.EXTRA_STREAM, savedImageURI);
//                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
//                    context.startActivity(shareIntent);
//                } else {
//                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
//                    whatsappIntent.setType("text/plain");
//                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
//                    context.startActivity(whatsappIntent);
//                }
//            }
//        });


//        fragmentNewVehiclePerformanceBinding.vhsDisplay.setText(myPreferenece.getLastTripVhsScore() + "/100");

//        setupPagerIndidcatorDots();
//        setUpViewPager();
        // registerBroadcastReceivers();
        return fragmentNewVehiclePerformanceBinding.getRoot();
    }

//    private void shareButtonOnclick() {
////        fragmentNewVehiclePerformanceBinding.shareVHS.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                String shareMessage = "Hey, my car health score is " + myPreferenece.getLastTripVhsScore() + ". Get your car health score at https://play.google.com/store/apps/details?id=in.gypsee.customer";
//                double vhsScore = myPreferenece.getLastTripVhsScore();
//
//                if (vhsScore == 100) {
//                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
//                            R.drawable.hundred);
//                    String sImageUrl = MediaStore.Images.Media.insertImage(context.getContentResolver(), icon, "title", "description");
//                    Uri savedImageURI = Uri.parse(sImageUrl);
//
//                    Intent shareIntent = new Intent();
//                    shareIntent.setAction(Intent.ACTION_SEND);
//                    shareIntent.setType("image/*");
//                    shareIntent.putExtra(Intent.EXTRA_STREAM, savedImageURI);
//                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
//                    context.startActivity(shareIntent);
//                } else {
//                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
//                    whatsappIntent.setType("text/plain");
//                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
//                    context.startActivity(whatsappIntent);
//                }
//            }
//        });
//    }




    private void refreshViews() {
        /*if(myPreferenece.getLastTripVhsScore() == 100){
            fragmentNewVehiclePerformanceBinding.shareVHS.setVisibility(View.VISIBLE);
        } else {
            fragmentNewVehiclePerformanceBinding.shareVHS.setVisibility(View.GONE);
        }*/

        //shareButtonOnclick();

//        if (myPreferenece.getLastTripMileage() == 0) {
//            fragmentNewVehiclePerformanceBinding.mileage.setVisibility(View.GONE);
//        } else {
//            fragmentNewVehiclePerformanceBinding.mileage.setVisibility(View.VISIBLE);
//            fragmentNewVehiclePerformanceBinding.mileage.setText("Mileage: " + myPreferenece.getLastTripMileage() + "kmpl");
//        }

//        fragmentNewVehiclePerformanceBinding.vhsDisplay.setText(myPreferenece.getLastTripVhsScore() + "/100");

        //fragmentNewVehiclePerformanceBinding.pagerDots.removeAllViews();
        //setupPagerIndidcatorDots();
        setUpViewPager();
    }


    /*private void registerBroadcastReceivers() {

        //THis is to receive the obd commands from the device . Regarding the vehcile

        IntentFilter filter = new IntentFilter();
        filter.addAction("NewPerformance");

        //this is for receiving the notification count etc
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, filter);
    }*/


    /*private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            boolean connectedStatus = intent.getBooleanExtra("BlueToothStatus", false);
            if (connectedStatus) {
                fragmentNewVehiclePerformanceBinding.obdConnectedTv.setText("Connected");
                //   fragmentNewVehiclePerformanceBinding.obdImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.obd_green, null));
            } else {
                fragmentNewVehiclePerformanceBinding.obdConnectedTv.setText("Not Connected");
                // fragmentNewVehiclePerformanceBinding.obdImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.obd_disconnected, null));

            }


        }
    };*/

    private String TAG = NewVehiclePerformanceFragment.class.getSimpleName();

    private void initVehicleHealthOps() {
        callServer(getString(R.string.fetchVehicleHealthData).replace("userId", new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getUser().getUserId()), "Fetch Vehicle Health Data", 15);
    }


    private void callServer(String url, final String purpose, final int value) {
        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);
        Call<ResponseBody> call;
        JsonObject jsonObject = new JsonObject();

        call = apiService.getVehicleHealthData(new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getUser().getUserAccessToken());

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

                        parseFetchVehicleHealth(responseStr);

                    } else {
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

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    private void parseFetchVehicleHealth(String responseStr) throws JSONException {

        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        JSONObject jsonObject = new JSONObject(responseStr);
        boolean dataLoading = jsonObject.getBoolean("dataLoading");

        if (dataLoading) {
            //call server after some time
            new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    callServer(context.getString(R.string.fetchVehicleHealthData).replace("userId", new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getUser().getUserId()), "Fetch Vehicle Health Data", 15);
                }
            }, 5000);
        } else {

            if (jsonObject.has("vehicleHealthData")) {

                JSONObject vehicleHealthData = jsonObject.getJSONObject("vehicleHealthData");

                double vhsScore = vehicleHealthData.has("vhsScore") ? vehicleHealthData.getDouble("vhsScore") : 0;
                double mileage = vehicleHealthData.has("mileage") ? vehicleHealthData.getDouble("mileage") : 0;
                myPreferenece.setLastTripVhsScore(vhsScore);
                myPreferenece.setLastTripMileage(mileage);

                databaseHelper.deleteTable(DatabaseHelper.PERFORMANCE_MEASURES_TABLE);
                databaseHelper.deleteTable(DatabaseHelper.FAULT_CODES_TABLE);
                databaseHelper.deleteTable(DatabaseHelper.CRITICAL_FAULTS_TABLE);

                ArrayList<VehicleHealthDataModel> performanceMeasuresArrayList = new ArrayList<>();
                ArrayList<VehicleHealthDataModel> faultCodesArrayList = new ArrayList<>();
                ArrayList<VehicleHealthDataModel> criticalFaultsArrayList = new ArrayList<>();

                JSONArray performanceMeasures = vehicleHealthData.getJSONArray("performanceMeasures");
                Gson gson = new Gson();
                VehicleHealthDataModel[] performanceMeasuresArray = gson.fromJson(performanceMeasures.toString(), VehicleHealthDataModel[].class);
                Log.e(TAG, "Array Size is " + performanceMeasuresArray.length);
                performanceMeasuresArrayList.addAll(Arrays.asList(performanceMeasuresArray));
                /*for (int i = 0; i < performanceMeasures.length(); i++) {
                    JSONObject object = performanceMeasures.getJSONObject(i);
                    String code = object.getString("code");
                    String title = object.getString("title");
                    String value = object.getString("value");
                    String thresholdValue = object.getString("thresholdValue");
                    boolean dataNotSupported = object.getBoolean("dataNotSupported");
                    boolean error = object.getBoolean("error");

                    GsonParse gsonparse = gson.fromJson(response, GsonParse.class);

                    performanceMeasuresArrayList.add(new VehicleHealthDataModel(code, title, value, thresholdValue, dataNotSupported, error));
                }*/

                JSONArray faultCodes = vehicleHealthData.getJSONArray("faultCodes");
                performanceMeasuresArray = gson.fromJson(faultCodes.toString(), VehicleHealthDataModel[].class);
                faultCodesArrayList.addAll(Arrays.asList(performanceMeasuresArray));

                JSONArray criticalFaults = vehicleHealthData.getJSONArray("criticalFaults");
                performanceMeasuresArray = gson.fromJson(criticalFaults.toString(), VehicleHealthDataModel[].class);
                criticalFaultsArrayList.addAll(Arrays.asList(performanceMeasuresArray));

                if (!performanceMeasuresArrayList.isEmpty()) {
                    new DatabaseHelper(context).insertPerformanceMeasures(performanceMeasuresArrayList);
                }
                if (!faultCodesArrayList.isEmpty()) {
                    new DatabaseHelper(context).insertFaultCodes(faultCodesArrayList);
                }
                if (!criticalFaultsArrayList.isEmpty()) {
                    new DatabaseHelper(context).insertCriticalFaults(criticalFaultsArrayList);
                }
            }
            /*FragmentTransaction tr = getActivity().getSupportFragmentManager().beginTransaction();
            tr.replace(R.id.mainFrameLayout, new NewVehiclePerformanceFragment(), PerformanceFragment.class.getSimpleName());
            tr.commit();*/
            refreshViews();
        }
    }


    private void callPerformanceServer(String url, final String purpose, String periodFrom, String periodTo, int value){

        fragmentNewVehiclePerformanceBinding.progressBar.setVisibility(View.VISIBLE);
        fragmentNewVehiclePerformanceBinding.performamceRecView.setVisibility(View.GONE);

        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);
        Call<ResponseBody> call;
        JsonObject jsonObject = new JsonObject();
        User user = myPreferenece.getUser();

        call = apiService.getPerformance(user.getUserAccessToken() ,periodFrom, periodTo);



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

                       parsePerformance(responseStr);



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

    private ArrayList<AlertsArray> alertsArrayList = new ArrayList<>();

    private void parsePerformance(String response) {
        try {
            alertsArrayList.clear();

            fragmentNewVehiclePerformanceBinding.progressBar.setVisibility(View.GONE);
            fragmentNewVehiclePerformanceBinding.performamceRecView.setVisibility(View.VISIBLE);

            JSONObject jsonObject = new JSONObject(response);

            if (!jsonObject.has("alertsData")) {
                Toast.makeText(requireContext(), "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONArray alertsArray = jsonObject.getJSONArray("alertsData");

            if (alertsArray == null || alertsArray.length() == 0) {
                Log.d(TAG, "alerts Array is empty");
            } else {
                for (int i = 0; i < alertsArray.length(); i++) {
                    JSONObject alertsObj = alertsArray.getJSONObject(i);
                    String alertName = alertsObj.optString("alertName");
                    String minutesOfSpeeding = alertsObj.optString("minutesOfSpeeding");
                    String hoursOfTravelling = alertsObj.optString("hoursOfTravelling");
                    String kmsTravelled = alertsObj.optString("kmsTravelled");
                    String eventsCount = alertsObj.optString("eventsCount");

                    alertsArrayList.add(new AlertsArray(alertName, minutesOfSpeeding, hoursOfTravelling, kmsTravelled, eventsCount));
                }

                Log.e("AlertsList", alertsArrayList.toString());

                if (fragmentNewVehiclePerformanceBinding.performamceRecView != null) {
                    final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                    fragmentNewVehiclePerformanceBinding.performamceRecView.setLayoutManager(layoutManager);
                    fragmentNewVehiclePerformanceBinding.performamceRecView.setAdapter(new AlertsArrayAdapter(alertsArrayList, context));
                } else {
                    Log.e(TAG, "performamceRecView is null");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
            // Handle JSON parsing error
        }
    }


    private void callGameServer(String url, final String purpose, String periodFrom, String periodTo){

        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);
        Call<ResponseBody> call;
        JsonObject jsonObject = new JsonObject();
        User user = myPreferenece.getUser();



        call = apiService.getGameLevel(user.getUserAccessToken(),periodFrom,periodTo);




        Log.e(TAG, purpose + " Input : " + jsonObject);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (response.isSuccessful()){
                        Log.e(TAG, "Response is success");

                        ResponseBody responseBody = response.body();
                        if (responseBody == null){
                            return;
                        }


                        String responseStr = responseBody.string();
                        Log.e(TAG, purpose + " Resonse : " + responseStr);





                        parseGameLevel(responseStr);


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

    private ArrayList<GameLevelModel> gamelevelArray;
    private void parseGameLevel(String response) throws JSONException {

        JSONObject jsonObject = new JSONObject(response);

        if (!jsonObject.has("gameLevel")){
            Toast.makeText(context, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject gameLevelObj = jsonObject.getJSONObject("gameLevel");


        String level = gameLevelObj.getString("gameLevel");
        String totalSafePercent = gameLevelObj.getString("totalSafeKmPercent");
        String totalSafeKm = gameLevelObj.getString("totalSafeKms");

        fragmentNewVehiclePerformanceBinding.level1.setText(level);
        fragmentNewVehiclePerformanceBinding.kmDrivenTv.setText(totalSafeKm);
        fragmentNewVehiclePerformanceBinding.level3.setText(totalSafePercent + "%");

//        fragmentHomeBinding.level1.setText(level);
//        fragmentHomeBinding.text3.setText(level);
//        fragmentHomeBinding.kmDrivenTv.setText(totalSafeKm);
//        fragmentHomeBinding.safeKmPercent.setText(totalSafePercent);

    }




    Context context;

    private void setUpViewPager() {
        // Instantiate a ViewPager2 and a PagerAdapter.
        ScreenSlidePagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(((AppCompatActivity) context));
//        fragmentNewVehiclePerformanceBinding.viewPager.setAdapter(pagerAdapter);

        ivArrayDotsPager[0].setImageResource(R.drawable.pager_indicator_selected);
//        fragmentNewVehiclePerformanceBinding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//                for (int i = 0; i < ivArrayDotsPager.length; i++) {
//                    ivArrayDotsPager[i].setImageResource(R.drawable.pager_indicator_unselected);
//                }
//                ivArrayDotsPager[position].setImageResource(R.drawable.pager_indicator_selected);
//            }
//        });

    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(AppCompatActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            if (position <= 2)
                return SingleParameterPerformanceFragment.newInstance(String.valueOf(position), responseFromServer);
            else {
                return PerformanceFragment.newInstance();
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }

    //Set the number of dots at the bottom of the screen.

    private void setupPagerIndidcatorDots() {
        ivArrayDotsPager = new ImageView[4];
        for (int i = 0; i < ivArrayDotsPager.length; i++) {
            ivArrayDotsPager[i] = new ImageView(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 0, 5, 0);
            ivArrayDotsPager[i].setLayoutParams(params);
            ivArrayDotsPager[i].setImageResource(R.drawable.pager_indicator_unselected);
            //ivArrayDotsPager[i].setAlpha(0.4f);
            ivArrayDotsPager[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setAlpha(1);
                }
            });
//            fragmentNewVehiclePerformanceBinding.pagerDots.addView(ivArrayDotsPager[i]);
//            fragmentNewVehiclePerformanceBinding.pagerDots.bringToFront();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
    }

    ResponseFromServer responseFromServer = new ResponseFromServer() {
        @Override
        public void responseFromServer(String Response, String className, int value) {

            parseTroubelCode(Response);
        }
    };

    private void parseTroubelCode(String response) {
        if (response.equals("")) {
            //fragmentNewVehiclePerformanceBinding.troubleCodeTv.setText("Hey There! You are all good.");
        } else {
            //fragmentNewVehiclePerformanceBinding.troubleCodeTv.setText(response);
            //Map<String, String> dtcVals = getDict(R.array.dtc_keys, R.array.dtc_values);
            //fragmentNewVehiclePerformanceBinding.troubleCodeDescriptionTv.setText(dtcVals.get(response));


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



    private void showDropdownMenu(View anchorView) {
        // Create a PopupWindow with a custom layout
        PopupWindow popupWindow = new PopupWindow(anchorView.getContext());
        View dropdownView = LayoutInflater.from(anchorView.getContext()).inflate(R.layout.date_drop_down_menu, null);

        // Initialize the ListView for the dropdown menu
        ListView dateListView = dropdownView.findViewById(R.id.dates_list_view);

        // Create an ArrayList with the dropdown options
        ArrayList<String> dateOptions = new ArrayList<>();
        dateOptions.add("Today");
        dateOptions.add("14 days");
        dateOptions.add("28 days");
        dateOptions.add("3 months");
        dateOptions.add("6 months");
        dateOptions.add("1 Year");

        // Create the custom adapter
        CustomArrayAdapter dateAdapter = new CustomArrayAdapter(anchorView.getContext(), R.layout.date_dropdown_item, dateOptions);

        // Set the adapter to the ListView
        dateListView.setAdapter(dateAdapter);

        // Set item click listener for the dropdown menu items
        dateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle item click based on position
                String selectedItem = dateOptions.get(position);
                fragmentNewVehiclePerformanceBinding.performanceDate.setText(selectedItem);

                // Get the current date
                LocalDate currentDate1 = LocalDate.now();

                // Define the desired format
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                // Format the current date
                String formattedDate = currentDate1.format(formatter);

                // Get the current date
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String currentDate = dateFormat.format(calendar.getTime());

                // Calculate the date based on the selected option
                Calendar selectedDateCalendar = Calendar.getInstance();
                selectedDateCalendar.setTime(calendar.getTime());
                switch (selectedItem) {
                    case "Today":
                        // No need to do anything, currentDate already holds today's date
                        break;
                    case "14 days":
                        selectedDateCalendar.add(Calendar.DAY_OF_MONTH, -14);
                        break;
                    case "28 days":
                        selectedDateCalendar.add(Calendar.DAY_OF_MONTH, -28);
                        break;
                    case "3 months":
                        selectedDateCalendar.add(Calendar.MONTH, -3);
                        break;
                    case "6 months":
                        selectedDateCalendar.add(Calendar.MONTH, -6);
                        break;
                    case "1 Year":
                        selectedDateCalendar.add(Calendar.YEAR, -1);
                        break;
                }
                String selectedDate = dateFormat.format(selectedDateCalendar.getTime());

                Toast.makeText(getContext(), "Selected Date: " + selectedDate, Toast.LENGTH_SHORT).show();
                Log.e("selected dates","PeriodFrom = "+selectedDate+" periodTo = "+formattedDate);
                callPerformanceServer(getResources().getString(R.string.performance).replace("{","").replace("}","").replace("userId",user.getUserId()),"Get Performance",selectedDate,formattedDate,2);
                callGameServer(getResources().getString(R.string.gameLevel).replace("{","").replace("}","").replace("userId",user.getUserId()),"Get Game ",selectedDate,formattedDate);


                // Dismiss the PopupWindow after item selection
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


}

