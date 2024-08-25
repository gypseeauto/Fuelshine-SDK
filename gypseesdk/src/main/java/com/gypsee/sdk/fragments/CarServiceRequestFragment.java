package com.gypsee.sdk.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.SecondActivity;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.databinding.FragmentCarServiceRequestBinding;
import com.gypsee.sdk.models.CarServiceTypeModel;
import com.gypsee.sdk.models.ServiceTransactionModel;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;
import com.gypsee.sdk.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CarServiceRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarServiceRequestFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CarServiceRequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CarServiceRequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CarServiceRequestFragment newInstance() {
        CarServiceRequestFragment fragment = new CarServiceRequestFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentCarServiceRequestBinding fragmentCarServiceRequestBinding;
    View rootView;

    Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentCarServiceRequestBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_car_service_request, container, false);
        rootView = fragmentCarServiceRequestBinding.getRoot();
        selectDate();
        fragmentCarServiceRequestBinding.payBtn.setOnClickListener(this);
        callServer(getString(R.string.carsSrervicesUrl), " Fetch car services ", 0, new JsonObject());


       /* String res= "{\"status\":200,\"message\":\"OK\",\"totalRecords\":0,\"bookingId\":\"GYP355985987334\",\"amount\":11.0,\"paymentStatus\":\"S\",\"user\":{\"userId\":\"c165933e-e5ab-4ae8-a3be-87c466b1985f\",\"userName\":\"bhaskarnagir@gmail.com\",\"userFullName\":\"vijaya bhaskar\",\"userEmail\":\"bhaskarnagir@gmail.com\",\"userPhoneNumber\":\"9108394532\",\"userImg\":\"https://lh3.googleusercontent.com/a-/AOh14GhnWdPA6-JTr5q1W_yefeNIvQhv2cL3sRq47SbLAw\",\"approvalExpiry\":\"2021-03-25 00:00:00\",\"referCode\":\"ETN8JB\",\"createdOn\":\"2020-04-18 07:53:23\",\"lastUpdatedOn\":\"2020-04-18 07:53:23\",\"userAccessToken\":\"eyJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..LURmRUB9mR7iaEMwIdmKqQ.Lw4Rd_Q0dWW1JOTfpUHJj07WL1qAOh7GqxkcCh2R9-GdhfF9uHAYN6lFAVHhWGkm2AiiXsXfa_bcJ3wODopdAQwBhHPlh8NQF5_Qk43_XEoHccO89BQsBNQdNixqwswrgBpcrjkFRzS5V9Y0Dqo-0ijfTnosP9uzQHS9AusXLKptdc8SCU-BICxLZtnJmABbtxKhYaNd02spUDsRDUUIKQH0luZLnP2H6TOp7aLJAKPne8xj_9osNzcmRxzPnMdVphkR7CkQzjrFiqhPlA5Ol63AGhpOegduzNY0DOOSKgzymF6l3cnPUvdAgYRW60Ix3vehHv7UQZJ5XiJc0k5zLb-VPYIjJgzQ4uTWNeGqJWmH-vWFboJ72DDMIgtDAip5LGMERNsNCxOumMDs2BOgiGPntzoOdFv_M0sVYsfRU9FP51WOsnIe-N5GccvgG265.ozbDvQ-go0eOs4DrB5TH0w\",\"fcmToken\":\"d-RdAvMbSiO2c7bU0zRWUQ:APA91bFuxRvANRox7DVZxEYnzjfmEQkWT-E8lOKNnaB7Y7Ijgl8t8C5J3t96UbhmS5GcAWpPA7kM99mrkD3NzVv3rNbdg1pk-Klfl-JSjwcOOt9MDQZoaGBGeBdM7H8bGS5kRYFIR55D\",\"userDeviceMac\":\"7E:0B:EE:EF:43:16\",\"appliedReferCode\":\"J6NDJZ\",\"referCodeApplied\":true,\"signUpBonusCredited\":true,\"approved\":true,\"locked\":false},\"userPhoneNumber\":\"9108394532\",\"gst\":0.0,\"totalAmount\":11.0,\"serviceName\":\"Door step vehicle health check\",\"gypseeService\":{\"id\":24235,\"serviceName\":\"Door step vehicle health check\",\"description\":\" Includes onspot checkup of vehicle condition, battery condition, tyre, fuses, spark plug, other wear & tear problems etc. This does not invlove any fixes to issues identified while inspecting.\",\"amount\":500.0,\"gst\":90.0,\"totalAmount\":11.0,\"locations\":\"Bangalore,Delhi,Gurugram,Hyderabad,Mysore,Mumbai,Pune\",\"timeSlots\":\"8AM - 12PM,12PM - 4PM,4PM- 8PM\",\"createdOn\":\"2020-10-22 16:39:54\",\"lastUpdatedOn\":\"2020-10-22 16:39:54\",\"active\":true},\"location\":\"Hyderabad\",\"otherInfo\":\"{\\\"city\\\":\\\"Hyderabad\\\",\\\"serviceDescription\\\":\\\" Includes onspot checkup of vehicle condition, battery condition, tyre, fuses, spark plug, other wear & tear problems etc. This does not invlove any fixes to issues identified while inspecting.\\\"}\",\"upiRefId\":\"GYP355985987334\",\"serviceDate\":\"2020-11-19\",\"serviceTime\":\"12PM - 4PM\",\"receiverUPIId\":\"omegapay10535@yesbank\",\"createdOn\":\"2020-11-17 14:17:12\",\"lastUpdatedOn\":\"2020-11-17 14:17:12\"}";
        try {
            parseOrderStatusResponse(res);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        return rootView;
    }

    private void selectDate() {
        fragmentCarServiceRequestBinding.selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utils.hideKeyboard((AppCompatActivity) context);

                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                monthOfYear = (monthOfYear + 1);
                                String day = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                                String month = monthOfYear < 10 ? "0" + monthOfYear : "" + monthOfYear;
                                String date = year + "-" + month + "-" + day;
                                fragmentCarServiceRequestBinding.selectDate.setText(date);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);


                datePickerDialog.show();

            }
        });
    }

    CarServiceTypeModel selectedCarServiceTypeModel;
    String selectedTimeSlot, selectedCity;

    private void setSelectServiceTypeSpinner() {
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, serviceTypes);
        //set the spinners adapter to the previously created one.

        ArrayAdapter<String> langAdapter = new ArrayAdapter<String>(context, R.layout.spinner_text, serviceTypes);
        langAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        fragmentCarServiceRequestBinding.selectServiceSpinner.setAdapter(langAdapter);

        fragmentCarServiceRequestBinding.selectServiceSpinner.setAdapter(adapter);
        fragmentCarServiceRequestBinding.selectServiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedItemText = (String) adapterView.getItemAtPosition(position);
                // If user change he default selection
                // First item is disable and it is used for hint
                if (position > 0) {
                    // Notify the selected item text
                    selectedCarServiceTypeModel = carServiceTypeModels.get(position - 1);
                    fragmentCarServiceRequestBinding.serviceDescription.setText(selectedCarServiceTypeModel.getDescription());

                    fragmentCarServiceRequestBinding.amountLayout.setAmountLabel("Amount");
                    fragmentCarServiceRequestBinding.amountLayout.setAmount(selectedCarServiceTypeModel.getAmount());

                    fragmentCarServiceRequestBinding.gstLayout.setAmountLabel("GST @18%");
                    fragmentCarServiceRequestBinding.gstLayout.setAmount(selectedCarServiceTypeModel.getGst());

                    fragmentCarServiceRequestBinding.totalAmountLayout.setAmountLabel("Total Amount");
                    fragmentCarServiceRequestBinding.totalAmountLayout.setAmount(selectedCarServiceTypeModel.getTotalAmount());
                    fragmentCarServiceRequestBinding.totalAmountLayout.amountLabelTxt.setTextColor(getResources().getColor(R.color.white));
                    fragmentCarServiceRequestBinding.totalAmountLayout.amountTxv.setTextColor(getResources().getColor(R.color.white));

                    selectCitySpinner(position - 1);
                    fragmentCarServiceRequestBinding.descriptionLayout.setVisibility(View.VISIBLE);
                    fragmentCarServiceRequestBinding.cityLayout.setVisibility(View.VISIBLE);
                    fragmentCarServiceRequestBinding.timeSlotLayout.setVisibility(View.GONE);

                    selectedTimeSlot = selectedCarServiceTypeModel.isTimeSlotInfoRequired() ? "" : selectedCarServiceTypeModel.getTimeSlots();

                    selectedCity = "";
                } else {
                    fragmentCarServiceRequestBinding.descriptionLayout.setVisibility(View.GONE);
                    fragmentCarServiceRequestBinding.cityLayout.setVisibility(View.GONE);
                    fragmentCarServiceRequestBinding.payBtn.setVisibility(View.GONE);
                    fragmentCarServiceRequestBinding.amountDescLayout.setVisibility(View.GONE);
                    fragmentCarServiceRequestBinding.timeSlotLayout.setVisibility(View.GONE);
                    fragmentCarServiceRequestBinding.dateLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //If size ==1, we will make it as default.
        if (carServiceTypeModels.size() == 1) {
            fragmentCarServiceRequestBinding.selectServiceSpinner.setSelection(1);
        }
    }

    private void selectCitySpinner(final int selectedServicePosition) {
        final ArrayList<String> cityList = new ArrayList<>();
        cityList.add("Select City");

        String[] locations = carServiceTypeModels.get(selectedServicePosition).getLocations().split(",");
        cityList.addAll(Arrays.asList(locations));
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, cityList);
        //set the spinners adapter to the previously created one.

        fragmentCarServiceRequestBinding.selectCity.setAdapter(adapter);
        fragmentCarServiceRequestBinding.selectCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedItemText = (String) adapterView.getItemAtPosition(position);
                // If user change he default selection
                // First item is disable and it is used for hint

                if (position > 0) {
                    selectedCity = cityList.get(position);
                    // Notify the selected item text
                    selectTimeSlotSpinner(selectedServicePosition);
                    fragmentCarServiceRequestBinding.timeSlotLayout.setVisibility(selectedCarServiceTypeModel.isTimeSlotInfoRequired() ? View.VISIBLE : View.GONE);
                    fragmentCarServiceRequestBinding.amountDescLayout.setVisibility(View.VISIBLE);
                    fragmentCarServiceRequestBinding.dateLayout.setVisibility(View.VISIBLE);
                    fragmentCarServiceRequestBinding.payBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void selectTimeSlotSpinner(int selectedServicePosition) {
        final ArrayList<String> cityList = new ArrayList<>();
        cityList.add("Select Time Slot");

        String[] locations = carServiceTypeModels.get(selectedServicePosition).getTimeSlots().split(",");
        cityList.addAll(Arrays.asList(locations));
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, cityList);
        //set the spinners adapter to the previously created one.

        fragmentCarServiceRequestBinding.selectTimeRange.setAdapter(adapter);
        fragmentCarServiceRequestBinding.selectTimeRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedItemText = (String) adapterView.getItemAtPosition(position);
                // If user change he default selection
                // First item is disable and it is used for hint
                if (position > 0) {
                    // Notify the selected item text
                    selectedTimeSlot = cityList.get(position);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    String TAG = CarServiceRequestFragment.class.getSimpleName();

    private void callServer(String url, final String purpose, final int value, JsonObject jsonObject) {

        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);
        Call<ResponseBody> call;

        MyPreferenece myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);
        User user = myPreferenece.getUser();
        switch (value) {
            case 0:
                fragmentCarServiceRequestBinding.progressLayout.setVisibility(View.VISIBLE);
                fragmentCarServiceRequestBinding.serviceLayout.setVisibility(View.GONE);
                call = apiService.getVehicleAlerts(user.getUserAccessToken(), new HashMap<String, Object>());
                break;

            //1 is for generating order_ID.
            case 1:
                hidepregressbar(false);
                call = apiService.uploadVehDetails(user.getUserAccessToken(), jsonObject);
                break;
            case 2:

                hidepregressLayout(true);
                call = apiService.uploadVehDetails(user.getUserAccessToken(), jsonObject);
                break;
            default:
                call = apiService.getVehicleAlerts(user.getUserAccessToken(), new HashMap<String, Object>());
                break;
        }

        Log.e(TAG, purpose + " Input :" + jsonObject);

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
                            case 0:
                                hideProgressLayout(true);
                                parseServicesResponse(responseStr);
                                break;

                            case 1:
                                hidepregressbar(true);
                                parseGenerateBookingIDResponse(responseStr);
                                break;
                            case 2:
                                hidepregressLayout(false);
                                parseOrderStatusResponse(responseStr);
                                break;
                        }
                    } else {

                        ResponseBody responseBody = response.errorBody();
                        String responseStr = responseBody.string();
                        Log.e(TAG, purpose + " Response is not succesfull");
                        Log.e("Error message", responseStr);


                        switch (value) {
                            case 0:
                                goBack();
                                break;
                            case 1:
                                hidepregressbar(true);
                                break;
                            case 2:
                                parseOrderStatusResponse(responseStr);
                                break;
                        }

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

                switch (value) {
                    case 0:
                        goBack();
                        break;
                    case 1:
                        hidepregressbar(true);
                        break;
                    case 2:
                        hidepregressLayout(false);
                        break;
                }

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

    private void parseOrderStatusResponse(String responseStr) throws JSONException {
        JSONObject jsonObject = new JSONObject(responseStr);

        String message = jsonObject.getString("message");

        Log.e(TAG, "Came to parse order : " + message);
      /*  {
            "status":200,
                "message":"OK",
                "totalRecords":0,
                "bookingId":"GYP463489772565",
                "amount":0.0,
                "paymentStatus":"S"
        }*/


        if (message.equalsIgnoreCase("Transaction not yet completed")) {

            if (statuchCheckInt >= 3) {
                //Show error response.
                hidepregressLayout(false);
                return;
            } else {
                statuchCheckInt++;
                callServerFortransactionCheckAPI();
            }
        } else if (message.equalsIgnoreCase("OK")) {
            Log.e(TAG, "Came to OK : " + message);
            String paymentStatus = jsonObject.getString("paymentStatus");

            if (paymentStatus.equals("S")) {
                //Go to payment succesfull page.
                String bookingId, amount, gypseeService, location,
                        upiRefId, serviceDate, serviceTime, receiverUPIId, createdOn, lastUpdatedOn;

                bookingId = jsonObject.getString("bookingId");
                amount = jsonObject.getString("amount");
                gypseeService = jsonObject.getString("gypseeService");
                location = jsonObject.getString("location");
                upiRefId = jsonObject.getString("upiRefId");
                serviceDate = jsonObject.getString("serviceDate");
                serviceTime = jsonObject.getString("serviceTime");
                receiverUPIId = jsonObject.getString("receiverUPIId");
                createdOn = jsonObject.getString("createdOn");
                lastUpdatedOn = jsonObject.getString("lastUpdatedOn");

                ServiceTransactionModel serviceTransactionModel = new ServiceTransactionModel(bookingId, amount, paymentStatus, gypseeService, location,
                        upiRefId, serviceDate, serviceTime, receiverUPIId, createdOn, lastUpdatedOn);


                startActivity(new Intent(getActivity(), SecondActivity.class)
                        .putExtra(ServiceTransactionModel.class.getSimpleName(), serviceTransactionModel)
                        .putExtra("TAG", "ServiceConfirmationFragment"));
            }

        }

    }

    private void hidepregressLayout(boolean b) {
        if (b) {
            fragmentCarServiceRequestBinding.bottomFrameLayout.setVisibility(View.GONE);
            fragmentCarServiceRequestBinding.serviceFieldsLayout.setVisibility(View.GONE);
            fragmentCarServiceRequestBinding.progressLayout.setVisibility(View.VISIBLE);
        } else {
            fragmentCarServiceRequestBinding.bottomFrameLayout.setVisibility(View.VISIBLE);
            fragmentCarServiceRequestBinding.serviceFieldsLayout.setVisibility(View.VISIBLE);
            fragmentCarServiceRequestBinding.progressLayout.setVisibility(View.GONE);
        }
    }

    private void parseGenerateBookingIDResponse(String responseStr) throws JSONException {

        JSONObject jsonObject = new JSONObject(responseStr);

        JSONObject dataObject = jsonObject.getJSONObject("data");
        String amount = dataObject.getString("amount");
        String bookingId = dataObject.getString("bookingId");
        String receiverUPIId = dataObject.getString("receiverUPIId");

        payWithUpi(amount, bookingId, receiverUPIId);

    }

    private void hidepregressbar(boolean b) {
        if (b) {
            fragmentCarServiceRequestBinding.progressBar.setVisibility(View.GONE);
            fragmentCarServiceRequestBinding.payBtn.setVisibility(View.VISIBLE);

        } else {
            fragmentCarServiceRequestBinding.progressBar.setVisibility(View.VISIBLE);
            fragmentCarServiceRequestBinding.payBtn.setVisibility(View.GONE);
        }
    }

    private void goBack() {
        if (getActivity() != null)
            getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    private void hideProgressLayout(boolean b) {
        fragmentCarServiceRequestBinding.progressLayout.setVisibility(View.GONE);
        fragmentCarServiceRequestBinding.serviceLayout.setVisibility(View.VISIBLE);
    }

    ArrayList<CarServiceTypeModel> carServiceTypeModels = new ArrayList<>();

    ArrayList<String> serviceTypes = new ArrayList<>();

    private void parseServicesResponse(String responseStr) throws JSONException {

        JSONObject jsonObject = new JSONObject(responseStr);
        carServiceTypeModels.clear();
        serviceTypes.clear();
        serviceTypes.add("Select service");
        //Go to main Activity
        String id, serviceName, description, amount, gst, totalAmount, locations, timeSlots, createdOn,
                lastUpdatedOn;

        boolean active;

        JSONArray jsonArray = jsonObject.getJSONArray("services");

        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject userJsonObject = jsonArray.getJSONObject(i);

            id = userJsonObject.has("id") ? userJsonObject.getString("id") : "";
            serviceName = userJsonObject.has("serviceName") ? userJsonObject.getString("serviceName") : "";
            description = userJsonObject.has("description") ? userJsonObject.getString("description") : "";
            amount = userJsonObject.has("amount") ? userJsonObject.getString("amount") : "";
            gst = userJsonObject.has("gst") ? userJsonObject.getString("gst") : "";
            totalAmount = userJsonObject.has("totalAmount") ? userJsonObject.getString("totalAmount") : "";
            locations = userJsonObject.has("locations") ? userJsonObject.getString("locations") : "";
            timeSlots = userJsonObject.has("timeSlots") ? userJsonObject.getString("timeSlots") : "";
            createdOn = userJsonObject.has("createdOn") ? userJsonObject.getString("createdOn") : "";
            lastUpdatedOn = userJsonObject.has("lastUpdatedOn") ? userJsonObject.getString("lastUpdatedOn") : "";
            active = userJsonObject.has("active") && userJsonObject.getBoolean("active");
            boolean timeSlotInfoRequired = userJsonObject.has("timeSlotInfoRequired") && userJsonObject.getBoolean("timeSlotInfoRequired");
            serviceTypes.add(serviceName);

            CarServiceTypeModel carServiceTypeModel = new CarServiceTypeModel(id, serviceName, description, amount, gst, totalAmount, locations, timeSlots, createdOn,
                    lastUpdatedOn, active, timeSlotInfoRequired);
            carServiceTypeModels.add(carServiceTypeModel);
            setSelectServiceTypeSpinner();

        }

    }

    String bookingID = "";

    public void payWithUpi(String amount, String bookingId, String receiverUPIId) {
        bookingID = bookingId;

        Uri myAction = Uri.parse("upi://pay?pa=" + receiverUPIId + "&pn=" + "GypseeAutomative" + "&tr=" + bookingId + "&tn=DoorStepService&am=" + amount + "&mam=null&cu=INR&url=https://www.omegaon.com");

        Log.e(TAG, "Upi intent data :" + myAction.toString());

        Intent in = new Intent(Intent.ACTION_VIEW, myAction);
        Intent chooser = Intent.createChooser(in, "Pay with...");
        startActivityForResult(chooser, PaymentIntentCode, null);
    }

    private static final int PaymentIntentCode = 111;
    int statuchCheckInt = 0;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "OnActivityResult called");

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 111:
                   /* if (data != null) {
                        HashMap<String, String> paymentData = new HashMap<>();
                        String paymentResponse = data.getStringExtra("response");
                        Log.e(TAG, "Response is :" + paymentResponse);

                        String[] keyValues = paymentResponse.split("&");
                        for (String keyValue : keyValues) {
                            String[] values = keyValue.split("=");
                            if (values.length > 0)
                                paymentData.put(values[0], values[1]);
                        }

                        if (paymentData.get("Status").equalsIgnoreCase("Success")) {
                            statuchCheckInt = 0;
                            callServerFortransactionCheckAPI();
                            return;
                        }

                    }*/
                    break;
            }
        }

        if (requestCode == 111) {
            Log.e(TAG, "Result came");
            statuchCheckInt = 0;
            callServerFortransactionCheckAPI();
        }
    }

    private void callServerFortransactionCheckAPI() {

        hidepregressLayout(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callServer(context.getString(R.string.OmegaonUPIOrderStatusUrl), "Check Order Status", 2, prepareJsonInputForGettingTransactionStatus());
            }
        }, 7000);

    }

    private JsonObject prepareJsonInputForGettingTransactionStatus() {

        MyPreferenece myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);
        User user = myPreferenece.getUser();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("amount", selectedCarServiceTypeModel.getTotalAmount());
        jsonObject.addProperty("bookingId", bookingID);
        jsonObject.addProperty("userId", user.getUserId());
        return jsonObject;
    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.payBtn) {
            checkAllfieldsFilledorNot();
        }

    }

    private void checkAllfieldsFilledorNot() {

        if (fragmentCarServiceRequestBinding.selectDate.getText().toString().isEmpty()) {
            fragmentCarServiceRequestBinding.selectDate.setError("Please Select Service Date");
        } else if (selectedTimeSlot.isEmpty()) {
            Toast.makeText(context, "Please select time slot", Toast.LENGTH_LONG).show();
        } else if (selectedCity.isEmpty()) {
            Toast.makeText(context, "Please select location", Toast.LENGTH_LONG).show();
        } else {
            MyPreferenece myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);
            User user = myPreferenece.getUser();
            JsonObject jsonObject = new JsonObject();
            try {
                jsonObject.addProperty("amount", selectedCarServiceTypeModel.getTotalAmount());

                JSONObject otherinfo = new JSONObject();
                otherinfo.put("city", selectedCity);
                jsonObject.addProperty("location", selectedCity);
                otherinfo.put("serviceDescription", selectedCarServiceTypeModel.getDescription());
                jsonObject.addProperty("otherInfo", otherinfo.toString());

                jsonObject.addProperty("serviceDate", fragmentCarServiceRequestBinding.selectDate.getText().toString());
                jsonObject.addProperty("serviceName", selectedCarServiceTypeModel.getServiceName());
                jsonObject.addProperty("serviceTime", selectedTimeSlot);
                jsonObject.addProperty("upiRefId", "/");
                jsonObject.addProperty("serviceId", selectedCarServiceTypeModel.getId());
                jsonObject.addProperty("userId", user.getUserId());
                jsonObject.addProperty("userPhoneNumber", user.getUserPhoneNumber());
                callServer(getString(R.string.generateServiceOrderId), "Generate service order Id", 1, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}