package com.gypsee.sdk.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import com.gypsee.sdk.Adapters.BasicDetailListAdapter;
import com.gypsee.sdk.Adapters.RTOInfoAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;

import com.gypsee.sdk.models.User;
import com.gypsee.sdk.models.Vehiclemodel;
import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;
import com.gypsee.sdk.services.TripBackGroundService;
import com.gypsee.sdk.utils.CustomLinearLayoutManager;
import com.gypsee.sdk.utils.RecyclerItemClickListener;
import com.gypsee.sdk.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCarDetailActivity extends AppCompatActivity {

    RecyclerView vehicleBasicDetail, rtoDetials;
    ArrayList<String> basicDetailValues;
    TextView update, regDateValue, fuelValue, chassisValue, engineValue;
    EditText serviceReminderKms, serviceDueDate, odometer;
    String serviceReminderKmsValue, serviceDueDateValue, odometerValue, regDate, fuel, chassisNo,
            engine, customerName, vehicleRegNumber, vehicleMakeModel, vehicleClass, mvTaxUpto, insurance, pollution, fitness;

    Vehiclemodel vehiclemodel;
    boolean isServiceDone;
    User user;

    private String TAG = MyCarDetailActivity.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_update_car);

        Vehiclemodel vehiclemodel = getIntent().getParcelableExtra(Vehiclemodel.class.getName());
        this.isServiceDone = getIntent().getBooleanExtra("isServiceDone", true);
        this.vehiclemodel = vehiclemodel;

        initToolBar();
        initViews();
        user = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, getApplicationContext()).getUser();

        if (!user.isInTrainingMode()) {
            rtoDetials.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), rtoDetials,
                    new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            switch (position) {

                                case 1:
                                    showDatePickerDialog("insurance");
                                    break;

                                case 2:
                                    showDatePickerDialog("pollution");

                                    break;

                                case 3:
                                    showDatePickerDialog("fitness");

                                    break;

                                default:
                                    break;

                            }
                        }

                        @Override
                        public void onLongItemClick(View view, int position) {
                        }
                    }));


            serviceDueDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Calendar calendar1 = Calendar.getInstance();
                    DatePickerDialog datePickerDialog1 = new DatePickerDialog(MyCarDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            serviceDueDateValue = year + "-" +
                                    String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth);
                            serviceDueDate.setText(serviceDueDateValue);
                        }
                    }, calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog1.getDatePicker().setMinDate(calendar1.getTimeInMillis() - 1000);
                    datePickerDialog1.show();
                }
            });

            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkAllFieldsFilled();
                }
            });
        } else {
            serviceReminderKms.setEnabled(false);
            odometer.setEnabled(false);
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), "Not available in training mode", Toast.LENGTH_SHORT).show();
                }
            });
        }



    }

    private void showDatePickerDialog(String category){
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = year + "-" +
                        String.format(Locale.getDefault(), "%02d", month + 1) + "-" +
                        String.format(Locale.getDefault(), "%02d", dayOfMonth);
                switch(category){
                    case "pollution":
                        pollution = date;
                        break;
                    case "fitness":
                        fitness = date;
                        break;
                    case "insurance":
                        insurance = date;
                        break;
                }
                rtoDetials.setAdapter(new RTOInfoAdapter(mvTaxUpto, vehicleClass, insurance,
                        pollution, fitness));
                rtoDetials.getAdapter().notifyDataSetChanged();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis() - 1000);
        datePickerDialog.show();
    }


    private void initToolBar() {
        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(toolbar != null){
            toolbar.bringToFront();
            toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);
        }*/
        /*View v = getLayoutInflater().inflate(R.layout.toolbar_layout, null);
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.bringToFront();
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);*/



        TextView title = findViewById(R.id.toolBarTitle);
        title.setText("Update vehicle details");
        title.setTextColor(getResources().getColor(R.color.white));


//        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        /*toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });*/
    }

    private void goBack() {
        onBackPressed();
        //((AppCompatActivity) context).getSupportFragmentManager().popBackStackImmediate();
    }


    protected void initViews(){

        vehicleBasicDetail = findViewById(R.id.vehicle_detail_recyclerview);
        rtoDetials = findViewById(R.id.rto_info_recyclerview);
        update = findViewById(R.id.update);
        serviceReminderKms = findViewById(R.id.service_reminder);
        serviceDueDate = findViewById(R.id.service_due_date);
        odometer = findViewById(R.id.odomter);

        regDateValue = findViewById(R.id.reg_date);
        fuelValue = findViewById(R.id.fuel);
        chassisValue = findViewById(R.id.chassis_no);
        engineValue = findViewById(R.id.engine);

        serviceReminderKmsValue = vehiclemodel.getNewServiceReminderRemainingKm();
        serviceDueDateValue = vehiclemodel.getServicereminderduedate();
        odometerValue = vehiclemodel.getOdoMeterRdg();
        fuel = vehiclemodel.getFuelType();
        engine = vehiclemodel.getEngineNo();
        chassisNo = vehiclemodel.getVin();
        regDate = vehiclemodel.getPurchaseDate();
        customerName = vehiclemodel.getcustomerName();
        vehicleClass = vehiclemodel.getVehicleClass();
        mvTaxUpto = vehiclemodel.getMvTaxUpto();
        fitness = vehiclemodel.getFitness();
        vehicleRegNumber = vehiclemodel.getRegNumber();
        vehicleMakeModel = vehiclemodel.getVehicleModel();
        insurance = vehiclemodel.getInsuranceReminderDate();
        pollution = vehiclemodel.getPollutionReminderDate();

        if(regDate == null){
            regDateValue.setText("");
        } else {
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.ENGLISH).parse(regDate));
                //add 1 to month. Calendar.MONTH always return 1 value less
                regDate = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH)+1) + "-" + cal.get(Calendar.DAY_OF_MONTH);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            regDateValue.setText(regDate);
        }

        if(fuel == null){
            fuelValue.setText("");
        } else {
            fuelValue.setText(fuel);
        }

        if(chassisNo == null){
            chassisValue.setText("");
        } else {
            chassisValue.setText(chassisNo);
        }

        if(engine == null){
            engineValue.setText("");
        } else {
            engineValue.setText(engine);
        }


        if(serviceReminderKmsValue == null){
            serviceReminderKms.setText("");
        } else {
            serviceReminderKms.setText(serviceReminderKmsValue);
        }
        if(serviceDueDateValue == null){
            serviceDueDate.setText("");
        } else {
            serviceDueDate.setText(serviceDueDateValue);
        }
        if(odometerValue == null){
            odometer.setText("");
        } else {
            odometer.setText(odometerValue);
        }

        CustomLinearLayoutManager manager = new CustomLinearLayoutManager(getApplicationContext());
        CustomLinearLayoutManager manager1 = new CustomLinearLayoutManager(getApplicationContext());

        basicDetailValues = new ArrayList<>(Arrays.asList(customerName, vehicleRegNumber, vehicleMakeModel));
        BasicDetailListAdapter adapter = new BasicDetailListAdapter(customerName, vehicleRegNumber, vehicleMakeModel);
        vehicleBasicDetail.setHasFixedSize(true);
        vehicleBasicDetail.setLayoutManager(manager);
        vehicleBasicDetail.setAdapter(adapter);
        vehicleBasicDetail.addItemDecoration(new DividerItemDecoration(vehicleBasicDetail.getContext(), DividerItemDecoration.VERTICAL));

        RTOInfoAdapter rtoInfoAdapter = new RTOInfoAdapter(mvTaxUpto, vehicleClass, insurance,
                pollution, fitness);
        rtoDetials.setHasFixedSize(true);
        rtoDetials.setLayoutManager(manager1);
        rtoDetials.setAdapter(rtoInfoAdapter);
        rtoDetials.addItemDecoration(new DividerItemDecoration(rtoDetials.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void checkAllFieldsFilled() {
        boolean error = false;

        //serviceReminderKms, serviceDueDate, odometer;
        //  insuranceStr = insuranceRenewDate.getText().toString();
        //changed fields due to requirements
        if (serviceReminderKms.getText().toString().isEmpty()) {
            serviceReminderKms.setError("field cannot be empty");
            error = true;
        /*} else if (pollution.isEmpty()) {
            pollutionRenewDate.setError("field cannot be empty");
            error = true;*/
        }  else if (odometer.getText().toString().isEmpty()) {
            odometer.setError("field cannot be empty");
            error = true;
        } else if (serviceDueDate.getText().toString().isEmpty() && isServiceDone) {
            serviceDueDate.setError("field cannot be empty");
            error = true;
        }

        if (!error) {
           /* //call Server to store the vehicle details in the server.
            for (int i = 0; i < appversionModelArrayList.size(); i++) {

                DocumentTypes documentTypes = appversionModelArrayList.get(i);
                if (documentTypes.getFile() == null) {
                    Toast.makeText(context, "Please upload " + documentTypes.getName() + " Image", Toast.LENGTH_LONG).show();
                    progressLayout.setVisibility(View.GONE);
                    save.setEnabled(true);
                    return;
                } else {
                    if (documentTypes.getImagelink() == null) {
                        callServer = false;
                        progressLayout.setVisibility(View.VISIBLE);
                        save.setEnabled(false);
                        uploadWithTransferUtility(documentTypes.getFile(), i);
                    }
                }
            }

            for (int j = 0; j < vehicleDocsModelArrayList.size(); j++) {

                VehicleDocsModel vehicleDocsModel = vehicleDocsModelArrayList.get(j);
                if (vehicleDocsModel.getDocumentLink().equals("")) {
                    callServer = false;
                    progressLayout.setVisibility(View.VISIBLE);
                    save.setEnabled(false);
                    uploadWithTransferUtility(vehicleDocsModel.getFile(), j);
                }
            }*/
            callServer(getResources().getString(R.string.UpdateBasicVehDetails_Url).replace("vehicleId", vehiclemodel.getUserVehicleId()), "Update vehicle ", 2);
        }

    }

    private void callServer(String url, String purpose, final int value) {

        //progressLayout.setVisibility(View.VISIBLE);
        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);

        Call<ResponseBody> call;
        JsonObject jsonInput = new JsonObject();
        //String distancePostCCStr = distancePostCC.getText().toString();
        String serviceReminderkmStr = serviceReminderKms.getText().toString();


        String serviceReminderDateStr = serviceDueDate.getText().toString().equals("") ? "" : serviceDueDate.getText().toString();


        pollution = pollution.equals("") ? vehiclemodel.getPollutionReminderDate() : pollution;
        insurance = insurance.equals("") ? vehiclemodel.getInsuranceReminderDate() : insurance;
        serviceReminderkmStr = serviceReminderkmStr.equals("") ? vehiclemodel.getServiceReminderkm() : serviceReminderkmStr;

        jsonInput.addProperty("distancePostCC", vehiclemodel.getDistancePostCC());
        jsonInput.addProperty("engineRunTime", "");
        jsonInput.addProperty("fuelLevel", "");
        jsonInput.addProperty("insuranceReminderDate", insurance);

        jsonInput.addProperty("maf", "");
        Log.e(TAG, "odometer reading: "+odometer.getText().toString());
        jsonInput.addProperty("odoMeterRdg", Integer.parseInt(odometer.getText().toString()));
        jsonInput.addProperty("pollutionReminderDate", pollution);
        if (serviceReminderkmStr.equalsIgnoreCase("")) {
            jsonInput.addProperty("serviceReminderkm", "");

        } else {
            jsonInput.addProperty("newServiceReminderRemainingKm", Integer.parseInt(serviceReminderkmStr));

        }
        jsonInput.addProperty("serviceReminderDate", serviceReminderDateStr);
        jsonInput.addProperty("vin", vehiclemodel.getVin());

        if (!serviceReminderDateStr.equals("") || isServiceDone)
            jsonInput.addProperty("isServiceDone", true);
        else {
            jsonInput.addProperty("isServiceDone", false);
        }
        Log.e(TAG, purpose + " Input is : " + jsonInput.toString());
        call = apiService.uploadFCMToken(user.getUserAccessToken(), jsonInput);


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // progressLayout.setVisibility(View.GONE);
                try {
                    if (response.isSuccessful()) {
                        Log.e(TAG, "Response is success");
                        String responseStr = response.body().string();
                        Log.e(TAG, "Response :" + responseStr);

                        switch (value) {
                            case 0:
                            case 1:
                            case 2:
                                Intent intent = new Intent("ShowProgress");
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                                Toast.makeText(getApplicationContext(), "Updated car succesfully", Toast.LENGTH_LONG).show();
                                update.setVisibility(View.GONE);
                                callBackgroundService(0);
                                goBack();
                                break;
                        }

                    } else {
                        Log.e(TAG, "Response is not succesfull");
                        String errorBody = response.errorBody().string();
                        Log.e(TAG, " Response :" + errorBody);

                        update.setEnabled(true);
                        int responseCode = response.code();
                        if (responseCode == 401 || responseCode == 403) {
                            //include logout functionality
                            Utils.clearAllData(getApplicationContext());
                            return;
                        }
                        if (response.errorBody() == null) {
                            Log.e("Error code 400", "" + response.errorBody().string());
                        } else {
                            JSONObject jsonObject = new JSONObject(errorBody);
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            Log.e("Error code 400", "" + "Response is empty");
                        }

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

                //progressLayout.setVisibility(View.GONE);
                update.setEnabled(true);
                update.setVisibility(View.VISIBLE);

                if (t.getMessage().contains("Unable to resolve host")) {
                    Toast.makeText(getApplicationContext(), "Please Check your internet connection", Toast.LENGTH_LONG).show();
                }

                if (call.isCanceled()) {

                } else {

                }
            }
        });
    }

    private void callBackgroundService(int value) {

        Intent intent = new Intent(getApplicationContext(), TripBackGroundService.class);
        switch (value) {
            // Trip history update. Sending to service.
            case 0:
                intent.putExtra(TripBackGroundService.PURPOSE, "Get UserVehicles");
                break;
        }

        startService(intent);
    }

}
