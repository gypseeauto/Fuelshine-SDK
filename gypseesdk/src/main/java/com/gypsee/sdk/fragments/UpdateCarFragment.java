package com.gypsee.sdk.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

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
import com.gypsee.sdk.databinding.FragmentUpdateCarBinding;
import com.gypsee.sdk.models.DocumentTypes;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.models.Vehiclemodel;
import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;
import com.gypsee.sdk.utils.CustomLinearLayoutManager;
import com.gypsee.sdk.utils.RecyclerItemClickListener;
import com.gypsee.sdk.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateCarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateCarFragment extends Fragment {

    public UpdateCarFragment() {
        // Required empty public constructor
    }

    RecyclerView vehicleBasicDetail, rtoDetials;
    ArrayList<String> basicDetailValues;
    TextView update, regDateValue, fuelValue, chassisValue, engineValue;
    EditText serviceReminderKms, serviceDueDate, odometer;
    String serviceReminderKmsValue, serviceDueDateValue, odometerValue, regDate, fuel, chassisNo,
            engine, customerName, vehicleRegNumber, vehicleMakeModel, vehicleClass, mvTaxUpto, insurance, pollution, fitness;




    public static UpdateCarFragment newInstance(Vehiclemodel vehiclemodel) {

        Bundle args = new Bundle();
        args.putParcelable(Vehiclemodel.class.getSimpleName(), vehiclemodel);
        UpdateCarFragment fragment = new UpdateCarFragment();
        fragment.setArguments(args);
        return fragment;
    }

    Vehiclemodel vehiclemodel;
    Context context;
    boolean isServiceDone;
    User user;
    //private LinearLayout progressLayout;
    //Button save;
    //String pollutionDateStr;
   // String insuranceStr;
    private String TAG = UpdateCarFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            vehiclemodel = getArguments().getParcelable(Vehiclemodel.class.getSimpleName());
            isServiceDone = getArguments().getBoolean("isServiceDone");
        }
    }

    FragmentUpdateCarBinding fragmentUpdateCarBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        context = getContext();
        fragmentUpdateCarBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_update_car, container, false);
        initToolBar();
        initViews();
        user = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getUser();

        fragmentUpdateCarBinding.backButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        if (!user.isInTrainingMode()) {
            rtoDetials.addOnItemTouchListener(new RecyclerItemClickListener(context, rtoDetials,
                    new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            switch (position) {
                                case 1:
                                    showDatePickerDialog("insurance");
                                    break;

                                case 2:
                                    showDatePickerDialog("pollution");
//                                    final Calendar calendar = Calendar.getInstance();
//                                    DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
//                                        @Override
//                                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                                            pollution = year + "-" +
//                                                    String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth);
//                                            rtoDetials.setAdapter(new RTOInfoAdapter(mvTaxUpto, vehicleClass, insurance,
//                                                    pollution, fitness));
//                                            rtoDetials.getAdapter().notifyDataSetChanged();
//                                        }
//                                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
//                                    datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis() - 1000);
//                                    datePickerDialog.show();
                                    break;

                                case 3:
                                    showDatePickerDialog("fitness");
//                                    final Calendar calendar1 = Calendar.getInstance();
//                                    DatePickerDialog datePickerDialog1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
//                                        @Override
//                                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                                            fitness = year + "-" +
//                                                    String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth);
//                                            rtoDetials.setAdapter(new RTOInfoAdapter(mvTaxUpto, vehicleClass, insurance,
//                                                    pollution, fitness));
//                                            rtoDetials.getAdapter().notifyDataSetChanged();
//                                        }
//                                    }, calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH));
//                                    datePickerDialog1.getDatePicker().setMinDate(calendar1.getTimeInMillis() - 1000);
//                                    datePickerDialog1.show();
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
                    DatePickerDialog datePickerDialog1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
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
            serviceDueDate.setEnabled(false);
            odometer.setEnabled(false);
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Not available in training mode", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return fragmentUpdateCarBinding.getRoot();
    }

    private void showDatePickerDialog(String category){
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
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
        Toolbar toolbar = fragmentUpdateCarBinding.toolBarLayout.toolbar;
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);

        fragmentUpdateCarBinding.toolBarLayout.setTitle("Update vehicle details");

        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
    }

    private void goBack() {
        ((AppCompatActivity) context).getSupportFragmentManager().popBackStack();

    }

    //EditText seriveReminderKm, servieReminderDueDate, odometerReading, pollutionRenewDate;

    /*private void initViews() {

        servieReminderDueDate = fragmentUpdateCarBinding.servieReminderDueDate;
        seriveReminderKm = fragmentUpdateCarBinding.serviceReminderkm;
        odometerReading = fragmentUpdateCarBinding.odoMeterReading;
        pollutionRenewDate = fragmentUpdateCarBinding.pollutionRenewDate;
        save = fragmentUpdateCarBinding.btnSave;

        String carDtls = "Registration Number : " + vehiclemodel.getRegNumber() + "\n" +
                "Registration Date : " + vehiclemodel.getPurchaseDate() + "\n" +
                "Vehicle Make : " + vehiclemodel.getVehicleBrand() + "\n" +
                "Vehicle Model : " + vehiclemodel.getVehicleModel() + "\n" +
                "Fuel : " + vehiclemodel.getFuelType() + "\n" +
                //"Fitness upto : " + vehiclemodel.getRegNumber() + "\n" +
                "Insurance Validity : " + vehiclemodel.getInsuranceReminderDate() + "\n" +
                "Vehicle Identification number : " + vehiclemodel.getVin();
        fragmentUpdateCarBinding.carDetailsTv.setText(carDtls);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAllFieldsFilled();
            }
        });

        fragmentUpdateCarBinding.pollutionRenewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        fragmentUpdateCarBinding.pollutionRenewDate.setText(year + "-"+
                                String.format("%02d", month+1) + "-" + String.format("%02d", dayOfMonth));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis() - 1000);
                datePickerDialog.show();

            }
        });

        fragmentUpdateCarBinding.servieReminderDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        fragmentUpdateCarBinding.servieReminderDueDate.setText(year + "-"+
                                String.format("%02d", month+1) + "-" + String.format("%02d", dayOfMonth));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis() - 1000);
                datePickerDialog.show();
            }
        });

        if (!vehiclemodel.getPollutionReminderDate().equals("")) {
            pollutionRenewDate.setText(vehiclemodel.getPollutionReminderDate());
        }
        if (!vehiclemodel.getServiceReminderkm().equals("")) {
            seriveReminderKm.setText(vehiclemodel.getServiceReminderkm());
        }
        if (!vehiclemodel.getServicereminderduedate().equals("")) {
            servieReminderDueDate.setText(vehiclemodel.getServicereminderduedate());
        }
        if (!vehiclemodel.getOdoMeterRdg().equals("")) {
            odometerReading.setText(vehiclemodel.getOdoMeterRdg());
        }

        insuranceStr = vehiclemodel.getInsuranceReminderDate();


    }*/

    protected void initViews(){

        vehicleBasicDetail = fragmentUpdateCarBinding.vehicleDetailRecyclerview;
        rtoDetials = fragmentUpdateCarBinding.rtoInfoRecyclerview;
        update = fragmentUpdateCarBinding.update;
        serviceReminderKms = fragmentUpdateCarBinding.serviceReminder;
        serviceDueDate = fragmentUpdateCarBinding.serviceDueDate;
        odometer = fragmentUpdateCarBinding.odomter;

        regDateValue = fragmentUpdateCarBinding.gridInfo.regDate;
        fuelValue = fragmentUpdateCarBinding.gridInfo.fuel;
        chassisValue = fragmentUpdateCarBinding.gridInfo.chassisNo;
        engineValue = fragmentUpdateCarBinding.gridInfo.engine;

        serviceReminderKmsValue = vehiclemodel.getNewServiceReminderRemainingKm();
        serviceDueDateValue = vehiclemodel.getServicereminderduedate();
        odometerValue = vehiclemodel.getOdoMeterRdg();
        fuel = vehiclemodel.getFuelType();
        //chassisNo = vehiclemodel.getVin();
        regDate = vehiclemodel.getPurchaseDate();
        engine = vehiclemodel.getEngineNo();
        chassisNo = vehiclemodel.getVin();
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


        if(serviceReminderKmsValue.equalsIgnoreCase("null")){
            serviceReminderKms.setText("0");
        } else {
            serviceReminderKms.setText(serviceReminderKmsValue);
        }
        if(serviceDueDateValue.equalsIgnoreCase("null") ){
            serviceDueDate.setText("");
        } else {
            serviceDueDate.setText(serviceDueDateValue);
        }
        if(odometerValue.equalsIgnoreCase("null")){
            odometer.setText("0");
        } else {
            odometer.setText(odometerValue);
        }

        CustomLinearLayoutManager manager = new CustomLinearLayoutManager(context);
        CustomLinearLayoutManager manager1 = new CustomLinearLayoutManager(context);

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

                if(!insurance.equals("null"))
                {
                    jsonInput.addProperty("insuranceReminderDate", insurance);
                }

                jsonInput.addProperty("maf", "");
                Log.e(TAG, "odometer reading: "+odometer.getText().toString());
                jsonInput.addProperty("odoMeterRdg", Integer.parseInt(odometer.getText().toString()));
                if(!pollution.equalsIgnoreCase("null"))
                {
                    jsonInput.addProperty("pollutionReminderDate", pollution);
                }
                if (serviceReminderkmStr.equalsIgnoreCase("")) {
                    jsonInput.addProperty("serviceReminderkm", "");

                } else {
                    jsonInput.addProperty("serviceReminderkm", Integer.parseInt(serviceReminderkmStr));

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
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                                Toast.makeText(context, "Updated car succesfully", Toast.LENGTH_LONG).show();
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
                            Utils.clearAllData(context);
                            return;
                        }
                        if (response.errorBody() == null) {
                            Log.e("Error code 400", "" + response.errorBody().string());
                        } else {
                            JSONObject jsonObject = new JSONObject(errorBody);
                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(context, "Please Check your internet connection", Toast.LENGTH_LONG).show();
                }

                if (call.isCanceled()) {

                } else {

                }
            }
        });
    }

    private ArrayList<DocumentTypes> appversionModelArrayList = new ArrayList<>();
    







}