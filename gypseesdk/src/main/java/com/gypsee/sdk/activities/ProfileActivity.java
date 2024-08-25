package com.gypsee.sdk.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.models.Vehiclemodel;
import com.gypsee.sdk.network.NetworkActivity;
import com.gypsee.sdk.network.RetrofitHelper;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    MyPreferenece preferenece;
    User user;
    TextInputEditText vehicleRegistrationNo, customerName, vehBrandName, mobileNumber, purchaseDateEt, vehName, vehicleModelTv;
    TextView save;
   // NetworkActivity networkActivity;

    LinearLayout linearLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        preferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, this);
        user = preferenece.getUser();
        initViews();

        //networkActivity = new RetrofitHelper().getNetworkActivity();

        save.setOnClickListener(this);
    }

    ArrayList<Vehiclemodel> vehiclemodels = new ArrayList<>();

    private void initViews() {

        vehBrandName = findViewById(R.id.veh_brandname);
        customerName = findViewById(R.id.tv_customer_name);
        vehicleRegistrationNo = findViewById(R.id.tv_vehicle_reg);
        mobileNumber = findViewById(R.id.tv_phone);
        purchaseDateEt = findViewById(R.id.tv_purchase_date);
        vehName = findViewById(R.id.tv_vehicle_name);
        vehicleModelTv = findViewById(R.id.tv_vehicle_model);
        save = findViewById(R.id.btn_save);
        purchaseDateEt.setOnTouchListener(this);
        linearLayout = findViewById(R.id.ll_progress);

        customerName.setText(user.getUserFullName());
        mobileNumber.setText(user.getUserPhoneNumber());
        mobileNumber.setEnabled(false);
        customerName.setEnabled(false);
        Toolbar toolbar =  findViewById (R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_button);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_save) {

            boolean error = false;
            if (customerName.getText().toString().isEmpty()) {
                customerName.setError("Customer name cannot eb empty");
                error = true;
            } else if (mobileNumber.getText().toString().length() != 10) {
                mobileNumber.setError("Invalid customer phone");
                error = true;
            } else if (vehBrandName.getText().toString().isEmpty()) {
                vehBrandName.setError("Vehicle company cannot be empty");
                error = true;
            } else if (vehName.getText().toString().isEmpty()) {
                vehName.setError("Vehicle name cannot be empty");
                error = true;
            } else if (vehicleModelTv.getText().toString().isEmpty()) {
                vehicleModelTv.setError("Vehicle Model cannot be empty");
                error = true;
            } else if (vehicleRegistrationNo.getText().toString().isEmpty()) {
                vehicleRegistrationNo.setError("Registration number cannot be empty");
                error = true;
            } else if (purchaseDateEt.getText().toString().isEmpty()) {
                purchaseDateEt.setError("Purchase date cannot be empty");
                error = true;
            }

            if (!error) {
                //call Server to store the vehicle details in the server.
              //  callServerToAddVehdetails(AddVehDetails_Url Server_Config.AddVehDetails_Url.replace("userid", user.getUserId()), "Add vehicle",0);

            }


        }
    }

    private void setInputState(boolean enable) {
        vehicleModelTv.setEnabled(enable);
        vehicleRegistrationNo.setEnabled(enable);
        purchaseDateEt.setEnabled(enable);
        customerName.setEnabled(enable);
        mobileNumber.setEnabled(enable);
        vehBrandName.setEnabled(enable);
        if (!enable) {
            save.setText("Edit Info");
        } else
            save.setText("Save");


    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        purchaseDateEt.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());

        datePickerDialog.show();

        return false;
    }

    private void saveAndExit() {
        preferenece.setCustomerName(customerName.getText().toString());
        preferenece.setCustomerPhone(mobileNumber.getText().toString());
        preferenece.setPurchaseDate(purchaseDateEt.getText().toString());
        preferenece.setVehicleMake(vehBrandName.getText().toString());
        preferenece.setVehicleModel(vehicleModelTv.getText().toString());
        preferenece.setVehicleRegNo(vehicleRegistrationNo.getText().toString());
        setInputState(false);
    }

    String TAG = ProfileActivity.class.getSimpleName();

    private void callServerToAddVehdetails(String url, String purpose,final int value) {


        ApiInterface apiService = ApiClient.getClient(TAG,purpose,url).create(ApiInterface.class);

        Call<ResponseBody> call = null;
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("fuelPercent", "");
        jsonObject.addProperty("latitude", "");
        jsonObject.addProperty("longitude", "");
        jsonObject.addProperty("purchaseDateEt", purchaseDateEt.getText().toString());
        jsonObject.addProperty("regNumber", vehicleRegistrationNo.getText().toString());
        jsonObject.addProperty("vehicleBrand", vehBrandName.getText().toString());
        jsonObject.addProperty("vehicleModelTv", vehicleModelTv.getText().toString());
        jsonObject.addProperty("vehicleName", vehName.getText().toString());


        switch (value) {

            default:

                // call = apiService.uploadVehDetails(user.getUserAccessToken(), jsonObject);
                break;

        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (response.isSuccessful()) {
                     Log.e(TAG, "Response is success");

                        String responseStr = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseStr);


                        switch (value) {

                            case 0:

                                //showHideViews(View.GONE);
                             Log.e(TAG, "Response :" + responseStr);
                                //parseCheckAppversionResponse(responseStr);
                                //user.setUserVehicles(jsonResponse.getString("userVehicles"));
                                new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, ProfileActivity.this).storeUser(user);
                                break;
                        }

                    } else {
                     Log.e(TAG, "Response is not succesfull");

                     Log.e("Error code 400", response.errorBody().string());


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
                if (call.isCanceled()) {

                } else {

                }
            }
        });
    }

}
