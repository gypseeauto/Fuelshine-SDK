package com.gypsee.sdk.demoaccount.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import com.gypsee.sdk.Adapters.BasicDetailListAdapter;
import com.gypsee.sdk.Adapters.RTOInfoAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.MyCarDetailActivity;
import com.gypsee.sdk.models.Vehiclemodel;
import com.gypsee.sdk.utils.CustomLinearLayoutManager;
import com.gypsee.sdk.utils.RecyclerItemClickListener;

public class DemoMyCarDetailActivity extends AppCompatActivity {

    RecyclerView vehicleBasicDetail, rtoDetials;
    ArrayList<String> basicDetailValues;
    TextView update, regDateValue, fuelValue, chassisValue, engineValue;
    EditText serviceReminderKms, serviceDueDate, odometer;
    String serviceReminderKmsValue, serviceDueDateValue, odometerValue, regDate, fuel, chassisNo,
            engine, customerName, vehicleRegNumber, vehicleMakeModel, vehicleClass, mvTaxUpto, insurance, pollution, fitness;

    Vehiclemodel vehiclemodel;

    private String TAG = MyCarDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_update_car);

        vehiclemodel = new Vehiclemodel("id", "NISSAN MICRA ACTIVE XV", "KA51ME7472", "2015-08-12", "NISSAN",
                "MICRA ACTIVE XV", "", "", "", "", "", "25000", "", "2000",
                "2021-05-21", "2021-06-26", "2021-12-01", "1B2MN5666R8976543", true, true, "3000",
                "10000", "", "Petrol", "",  "JF78MN432123", "Nikhil Kumar", "LMV-NT", "LTT", "LTT",10,30,50);

        initToolBar();
        initViews();

        rtoDetials.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), rtoDetials,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        switch (position){

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
                DatePickerDialog datePickerDialog1 = new DatePickerDialog(DemoMyCarDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                Toast.makeText(DemoMyCarDetailActivity.this, "Please register your account to avail this service", Toast.LENGTH_LONG).show();
            }
        });



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

        TextView title = findViewById(R.id.toolBarTitle);
        title.setText("Update vehicle details");
        title.setTextColor(getResources().getColor(R.color.white));

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

        serviceReminderKmsValue = vehiclemodel.getServiceReminderkm();
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
        regDateValue.setText(regDate);
        fuelValue.setText(fuel);
        chassisValue.setText(chassisNo);
        engineValue.setText(engine);
        serviceReminderKms.setText(serviceReminderKmsValue);
        serviceDueDate.setText(serviceDueDateValue);
        odometer.setText(odometerValue);

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



}
