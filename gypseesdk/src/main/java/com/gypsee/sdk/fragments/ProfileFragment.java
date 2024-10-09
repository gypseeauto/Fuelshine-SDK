package com.gypsee.sdk.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.ProfileActivity;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.models.Vehiclemodel;
import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener,View.OnTouchListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

    MyPreferenece preferenece;
    User user;
    TextInputEditText vehicleRegistrationNo, customerName, vehBrandName, mobileNumber, purchaseDateEt, vehName, vehicleModelTv;
    TextView save;
    // NetworkActivity networkActivity;

    LinearLayout linearLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        preferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, requireContext());
        user = preferenece.getUser();


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    ArrayList<Vehiclemodel> vehiclemodels = new ArrayList<>();

    private void initViews(View view) {

        vehBrandName = view.findViewById(R.id.veh_brandname);
        customerName = view.findViewById(R.id.tv_customer_name);
        vehicleRegistrationNo = view.findViewById(R.id.tv_vehicle_reg);
        mobileNumber = view.findViewById(R.id.tv_phone);
        purchaseDateEt = view.findViewById(R.id.tv_purchase_date);
        vehName = view.findViewById(R.id.tv_vehicle_name);
        vehicleModelTv = view.findViewById(R.id.tv_vehicle_model);
        save = view.findViewById(R.id.btn_save);
        purchaseDateEt.setOnTouchListener(this);
        linearLayout = view.findViewById(R.id.ll_progress);

//        customerName.setText(user.getUserFullName());
//        mobileNumber.setText(user.getUserPhoneNumber());
//        mobileNumber.setEnabled(false);
//        customerName.setEnabled(false);
//        Toolbar toolbar =  view.findViewById (R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(R.drawable.back_button);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        // Assuming user is already initialized
        if (user != null) {
            customerName.setText(user.getUserFullName());
            mobileNumber.setText(user.getUserPhoneNumber());
        }

        mobileNumber.setEnabled(false);
        customerName.setEnabled(false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.back_button);
            toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
        }



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


        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
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
                                new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, requireContext()).storeUser(user);
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