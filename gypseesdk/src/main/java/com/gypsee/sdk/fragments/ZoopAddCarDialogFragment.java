package com.gypsee.sdk.fragments;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.GypseeMainActivity;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.database.DatabaseHelper;
import com.gypsee.sdk.databinding.FragmentZoopAddCarDialogBinding;
import com.gypsee.sdk.models.Vehiclemodel;
import com.gypsee.sdk.models.ZoopSearchVehicleModel;
import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;
import com.gypsee.sdk.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ZoopAddCarDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ZoopAddCarDialogFragment extends DialogFragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ZoopAddCarDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ZoopAddCarDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ZoopAddCarDialogFragment newInstance(String param1, String param2) {
        ZoopAddCarDialogFragment fragment = new ZoopAddCarDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    FragmentZoopAddCarDialogBinding fragmentZoopAddCarDialogBinding;

    Context context;
    int carListSize = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // make dialog itself transparent
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        context = getContext();
        fragmentZoopAddCarDialogBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_zoop_add_car_dialog, container, false);
        initViews();
        carListSize = new DatabaseHelper(context).fetchAllVehicles().size();
        return fragmentZoopAddCarDialogBinding.getRoot();
    }

    private void initViews() {
        fragmentZoopAddCarDialogBinding.positioveBtn.setOnClickListener(this);
        fragmentZoopAddCarDialogBinding.negativeBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        //Hiding the soft key board.
        Utils.hideKeyboardFrom(context, fragmentZoopAddCarDialogBinding.getRoot());

        if (v.getId() == R.id.positioveBtn) {

            //Submit means proceed to add the car. else search for the car.
            if (fragmentZoopAddCarDialogBinding.positioveBtn.getText().toString().equalsIgnoreCase("DONE")) {
                dismiss();
            } else if (fragmentZoopAddCarDialogBinding.positioveBtn.getText().toString().equalsIgnoreCase("Add Now!")) {
                callServer(getString(R.string.zoopAddVehicleAPI), "Add car with Zoop", 1);
            } else {
                if (fragmentZoopAddCarDialogBinding.registrationNoEt.getText().toString().length() >= 8) {
                    callServer(getString(R.string.zoopVehicleSearchAPI).replace("regNo", fragmentZoopAddCarDialogBinding.registrationNoEt.getText().toString().trim()), "Get vehicle with Zoop", 0);

                } else {
                    fragmentZoopAddCarDialogBinding.registrationNoEt.setError("Please enter valid registration number. Ex: KA01K2488");
                }
            }

        } else if (v.getId() == R.id.negativeBtn) {
            /*if(carListSize == 0){
                showAddCarNotification();
            } else {
                dismiss();
            }*/

            showAddCarNotification();
            dismiss();

        }

    }

    String TAG = ZoopAddCarDialogFragment.class.getSimpleName();

    private void callServer(String url, final String purpose, final int value) {

        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);
        Call<ResponseBody> call;
        JsonObject jsonObject = new JsonObject();
        MyPreferenece myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);
        showHideProgressBar(true);

        switch (value) {
            case 0:
                call = apiService.getDocTypes(myPreferenece.getUser().getUserAccessToken(), false);
                break;
            case 1:

                jsonObject.addProperty("resultId", zoopSearchVehicleModel.getResultId());
                jsonObject.addProperty("userId", zoopSearchVehicleModel.getUserId());
                call = apiService.uploadVehDetails(myPreferenece.getUser().getUserAccessToken(), jsonObject);
                break;

            default:

                call = apiService.getDocVehicleAlerts(myPreferenece.getUser().getUserAccessToken(), true, false);
                break;
        }

        Log.e(TAG, purpose + " Input :" + jsonObject.toString());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    showHideProgressBar(false);
                    if (response.isSuccessful()) {
                        Log.e(TAG, "Response is success");
                        //If the response is null, we will return immediately.
                        ResponseBody responseBody = response.body();
                        if (responseBody == null) {
                            showErrorTv("AN unknown error occured. Kindly contact Gypsee Team.");
                            return;
                        }
                        String responseStr = responseBody.string();
                        Log.e(TAG, purpose + " Response :" + responseStr);

                        switch (value) {
                            case 0:
                                parseSearchZoopVehicle(responseStr);
                                break;
                            case 1:
                                //sending intent to show loading bar in MycarsListFragment.

                                Intent intent = new Intent("ShowProgress");
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                                parseFetchVehicles(responseStr);

                                /*//THis is for refreshing the vehicle details.
                                intent = new Intent(context, TripBackGroundService.class);
                                intent.putExtra(TripBackGroundService.PURPOSE, "Get UserVehicles");
                                context.startService(intent);*/
                                dismiss();
                                break;
                        }
                    } else {
                        Log.e(TAG, purpose + " Response is not Successful");
                        ResponseBody responseBody = response.errorBody();
                        String errResponse = responseBody.string();
                        Log.e(TAG, purpose + " Error Response is : " + errResponse);
                        int responseCode = response.code();

                        if (responseCode == 401 || responseCode == 403) {
                            //include logout functionality
                            Utils.clearAllData(context);
                        } else {

                            JSONObject jObjError = new JSONObject(errResponse);
                            showErrorTv(jObjError.getString("message"));
                            if (value == 1) {
                                fragmentZoopAddCarDialogBinding.carDetailsTv.setVisibility(View.GONE);
                                fragmentZoopAddCarDialogBinding.descriptionTV.setVisibility(View.GONE);
                                fragmentZoopAddCarDialogBinding.positioveBtn.setText("DONE");
                            }
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override

            public void onFailure(Call<ResponseBody> call, Throwable t) {

                showHideProgressBar(false);
                Log.e(TAG, "error here since request failed");
                if (t.getMessage().contains("Unable to resolve host")) {
                    showErrorTv("Please Check your internet connection");
                } else {
                    showErrorTv(t.getMessage());
                }
            }
        });
    }

    private void parseFetchVehicles(String responseStr) {
        Log.e(TAG, "fetchvehicle response:" + responseStr);

        ArrayList<Vehiclemodel> vehiclemodelArrayList = new ArrayList<>();
        JSONObject jsonResponse = null;
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

            new DatabaseHelper(context).InsertAllVehicles(vehiclemodelArrayList);
            //SendBroadcast to receiver.
            Intent intent = new Intent("CarsList");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        } catch (
                JSONException e) {
            e.printStackTrace();
        }
    }


    private void parseSearchZoopVehicle(String responseStr) throws JSONException {

        Log.e(TAG, "parseZoop: " + responseStr);

        JSONObject jsonObject = new JSONObject(responseStr);
        boolean closeDialog = jsonObject.getBoolean("closeDialog");
        if(closeDialog){
            Intent intent = new Intent("RefreshList");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            dismiss();
        } else {
            JSONObject searchResult = jsonObject.getJSONObject("searchResult");

            String makeAndModelName = searchResult.getString("makeAndModelName");
            String regNumber = searchResult.getString("regNumber");
            String userId = searchResult.getString("userId");
            String resultId = searchResult.getString("resultId");
            String fuelType = searchResult.getString("fuelType");
            String registrationDate = searchResult.getString("registrationDate");
            String vehicleExists = searchResult.getString("vehicleExists");
            MyPreferenece myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);

            zoopSearchVehicleModel = new ZoopSearchVehicleModel(makeAndModelName, regNumber, myPreferenece.getUser().getUserId(), resultId, fuelType, registrationDate, vehicleExists);
            setDetails();
        }



    }

    private void setDetails() {
        fragmentZoopAddCarDialogBinding.descriptionTV.setText("Below are the car details");
        fragmentZoopAddCarDialogBinding.registrationNoEt.setVisibility(View.GONE);
        fragmentZoopAddCarDialogBinding.carDetailsTv.setVisibility(View.VISIBLE);
        fragmentZoopAddCarDialogBinding.negativeBtn.setVisibility(View.GONE);
        fragmentZoopAddCarDialogBinding.lineView2.setVisibility(View.GONE);

        fragmentZoopAddCarDialogBinding.positioveBtn.setText("Add Now!");
        String carDetails = "Make & Model : " + zoopSearchVehicleModel.getMakeAndModelName() + "\n\n" +
                "Registration No : " + zoopSearchVehicleModel.getRegNumber() + "\n" +
                "Fuel : " + zoopSearchVehicleModel.getFuelType() + "\n" +
                "Registered Date : " + zoopSearchVehicleModel.getRegistrationDate() + "\n";
        fragmentZoopAddCarDialogBinding.carDetailsTv.setText(carDetails);
    }

    ZoopSearchVehicleModel zoopSearchVehicleModel;

    private void showErrorTv(String error) {
        fragmentZoopAddCarDialogBinding.errorTxt.setVisibility(View.VISIBLE);
        fragmentZoopAddCarDialogBinding.errorTxt.setText(error);
    }

    private void showHideProgressBar(boolean isShowProgressBar) {

        if (isShowProgressBar) {
            fragmentZoopAddCarDialogBinding.progressBar.setVisibility(View.VISIBLE);
            fragmentZoopAddCarDialogBinding.postiveNegativeBtnsLayout.setVisibility(View.GONE);
            fragmentZoopAddCarDialogBinding.errorTxt.setVisibility(View.GONE);
        } else {
            fragmentZoopAddCarDialogBinding.progressBar.setVisibility(View.GONE);
            fragmentZoopAddCarDialogBinding.postiveNegativeBtnsLayout.setVisibility(View.VISIBLE);
        }

    }


    private final static String NOTIFICATION_CHANNEL = "BLUETOOTH_FOREGROUND_SERVICE";
    private final static String NOTIFICATION_ID = "BLUETOOTH_SERVICE_ID";
    private final static int PENDING_INTENT_REQUEST_CODE = 300;
    private final static int NOTIFICATION_REQUEST_CODE = 301;

    private void showAddCarNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(NOTIFICATION_ID, NOTIFICATION_CHANNEL);
        } else {
            Intent intent = new Intent(context, GypseeMainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, PENDING_INTENT_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.bigText("Fuelshine");
            bigTextStyle.setBigContentTitle("Enter your car registration number to know more about your car registered detail with your nearest RTO");

            builder.setStyle(bigTextStyle);
            // builder.setOnlyAlertOnce(true); //to quietly update the notification
            builder.setWhen(System.currentTimeMillis());
            builder.setSmallIcon(R.drawable.notif_icon);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.new_app_icon));
            builder.setPriority(Notification.PRIORITY_HIGH);
            builder.setFullScreenIntent(pendingIntent, true);
            builder.build();
            //builder.notify();
            synchronized (builder){
                builder.notify();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelID, String channelName) {
        Intent resultIntent = new Intent(context, GypseeMainActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent pendingIntent;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S) {
            pendingIntent = taskStackBuilder.getPendingIntent(PENDING_INTENT_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        }else{
            pendingIntent = taskStackBuilder.getPendingIntent(PENDING_INTENT_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationChannel notificationChannel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null; //exit if notification manager is null
        notificationManager.createNotificationChannel(notificationChannel);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelID);

        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.notif_icon)
                .setContentTitle("Fuelshine")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Enter your car registration number to know more about your car registered detail with your nearest RTO"))
                .setContentText("Enter your car registration number to know more about your car registered detail with your nearest RTO")
                //.setOnlyAlertOnce(true) //to update notification quietly
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(NOTIFICATION_REQUEST_CODE, notification);
    }




}