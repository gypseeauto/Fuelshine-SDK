package com.gypsee.sdk.demoaccount.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.FragmentCarServiceRequestBinding;
import com.gypsee.sdk.models.CarServiceTypeModel;
import com.gypsee.sdk.utils.Utils;

public class DemoCarServiceRequestFragment extends Fragment implements View.OnClickListener {

    public DemoCarServiceRequestFragment() {
        // Required empty public constructor
    }

    public static DemoCarServiceRequestFragment newInstance() {
        DemoCarServiceRequestFragment fragment = new DemoCarServiceRequestFragment();
        /*Bundle args = new Bundle();
        fragment.setArguments(args);*/
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
        //callServer(getString(R.string.carsSrervicesUrl), " Fetch car services ", 0, new JsonObject());
        setSelectServiceTypeSpinner();


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
    ArrayList<String> serviceTypes = new ArrayList<>();
    ArrayList<CarServiceTypeModel> carServiceTypeModels = new ArrayList<>();

    private void setSelectServiceTypeSpinner() {

        String cities = "Bangalore,Delhi,Gurugram,Hyderabad,Mysore,Mumbai,Pune";

        CarServiceTypeModel carServiceTypeModel1 = new CarServiceTypeModel("id",
                "RSA & Towing (In Selected Cities*)",
                "Puncture Repair/Flat Tyre\nExtra Puncture\nBattery Jumpstart\nMinor Repairs on the spot (Like Cables, Spark Plug, Fuses, etc.)\n" +
                        "Emergency Fuel Delivery (5 Ltrs)(Fuel charges extra)\n(Towing charges are separate*)",
                "800", "144", "944", cities, "24/7", "", "", true, true);

        CarServiceTypeModel carServiceTypeModel2 = new CarServiceTypeModel("id",
                "TVS On-demand RSA & Towing Service",
                "Puncture Repair/Flat Tyre\nExtra Puncture\nBattery Jumpstart\nMinor Repairs on the spot (Like Cables, Spark Plug, Fuses, etc.)\n" +
                        "Emergency Fuel Delivery (5 Ltrs)(Fuel charges extra)\n(Towing charges are separate*)",
                "800", "144", "944", cities, "24/7", "", "", true, true);

        CarServiceTypeModel carServiceTypeModel3 = new CarServiceTypeModel("id",
                "Door Step Vehicle Health Check",
                "Puncture Repair/Flat Tyre\nExtra Puncture\nBattery Jumpstart\nMinor Repairs on the spot (Like Cables, Spark Plug, Fuses, etc.)\n" +
                        "Emergency Fuel Delivery (5 Ltrs)(Fuel charges extra)\n(Towing charges are separate*)",
                "800", "144", "944", cities, "24/7", "", "", true, true);

        CarServiceTypeModel carServiceTypeModel4 = new CarServiceTypeModel("id",
                "TVS RSA & Towing Subscription",
                "Puncture Repair/Flat Tyre\nExtra Puncture\nBattery Jumpstart\nMinor Repairs on the spot (Like Cables, Spark Plug, Fuses, etc.)\n" +
                        "Emergency Fuel Delivery (5 Ltrs)(Fuel charges extra)\n(Towing charges are separate*)",
                "800", "144", "944", cities, "24/7", "", "", true, true);


        carServiceTypeModels.add(carServiceTypeModel1);
        carServiceTypeModels.add(carServiceTypeModel2);
        carServiceTypeModels.add(carServiceTypeModel3);
        carServiceTypeModels.add(carServiceTypeModel4);





        serviceTypes.add("Select service");
        serviceTypes.add("RSA & Towing (In Selected Cities*)");
        serviceTypes.add("TVS On-demand RSA & Towing Service");
        serviceTypes.add("Door Step Vehicle Health Check");
        serviceTypes.add("TVS RSA & Towing Subscription");

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
                    selectedCarServiceTypeModel = carServiceTypeModels.get(position-1);
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.payBtn) {
            Toast.makeText(context, "Please register to avail this service", Toast.LENGTH_LONG).show();
        }
    }
}
