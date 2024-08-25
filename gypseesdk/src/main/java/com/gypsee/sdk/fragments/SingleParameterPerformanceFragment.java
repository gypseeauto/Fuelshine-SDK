package com.gypsee.sdk.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.gypsee.sdk.Adapters.NewperformanceListAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.database.DatabaseHelper;
import com.gypsee.sdk.databinding.FragmentSingleParameterPerformanceBinding;
import com.gypsee.sdk.helpers.BluetoothHelperClass;
import com.gypsee.sdk.jsonParser.ResponseFromServer;
import com.gypsee.sdk.models.PerformanceModel;
import com.gypsee.sdk.models.VehicleHealthDataModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SingleParameterPerformanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleParameterPerformanceFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = SingleParameterPerformanceFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SingleParameterPerformanceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param responseFromServer Parameter 2.
     * @return A new instance of fragment SingleParameterPerformanceFragment.
     */
    static ResponseFromServer responseFromServer1;

    // TODO: Rename and change types and number of parameters
    public static SingleParameterPerformanceFragment newInstance(String param1, ResponseFromServer responseFromServer) {
        SingleParameterPerformanceFragment fragment = new SingleParameterPerformanceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        responseFromServer1 = responseFromServer;
        fragment.setArguments(args);
        return fragment;
    }

    Context context;
    MyPreferenece myPreferenece;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    FragmentSingleParameterPerformanceBinding fragmentNewVehiclePerformanceBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentNewVehiclePerformanceBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_single_parameter_performance, container, false);
        context = getContext();
        myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);
        setTitleTv();
        registerBroadcastReceivers();
        return fragmentNewVehiclePerformanceBinding.getRoot();
    }


    private void registerBroadcastReceivers() {

        //THis is to receive the obd commands from the device . Regarding the vehcile

        IntentFilter filter = new IntentFilter();
        filter.addAction("Performance");

        //this is for receiving the notification count etc
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, filter);
    }

    public void updateList(String key, String name, String value) {

        value = value.equals("NODATA") ? "RESTRICTED" : value;

        //For trouble code we are returning the last trouble code in the list
        if (key.equals("TROUBLE_CODES")) {
            responseFromServer1.responseFromServer(BluetoothHelperClass.getSingleTroubleCode(value), TAG, 0);

        }
        /*switch (mParam1) {
            case "0":
                if (key.equals("CONTROL_MODULE_VOLTAGE")) {
                    performanceModels.get(1).setTitle("Battery : " + value);

                } else if (key.equals("ENGINE_COOLANT_TEMP")) {

                    performanceModels.get(2).setTitle("Coolant : " + value);


                } else if (key.equals("ENGINE_LOAD")) {
                    performanceModels.get(3).setTitle("Engine Load : " + value);


                }
                break;
            case "1":
                break;
        }*/
        performanceListAdapter.notifyDataSetChanged();

    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();

            if (b == null)
                return;
            updateList(b.getString("cmdID"), b.getString("cmdName"), b.getString("cmdResult"));


        }
    };


    ArrayList<PerformanceModel> performanceModels = new ArrayList<>();


    private void setTitleTv() {

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        Map<String, VehicleHealthDataModel> vehicleHealthDataModelMap = new HashMap<>();

        switch (mParam1) {
            case "2":
                ArrayList<VehicleHealthDataModel> criticalFaults = databaseHelper.fetchCriticalFaults();
//                Map<String, VehicleHealthDataModel> criticalFaultsMap = new HashMap<>();
                String chargingSystem = "";
                String engineTemp = "";
                String oilPressure = "";
                for (VehicleHealthDataModel model: criticalFaults){
                    switch (model.getCode()){
                        case "oilPressure":
                            oilPressure = model.getValue();
                            vehicleHealthDataModelMap.put("oilPressure", model);
                            break;
                        case "chargingSystem":
                            chargingSystem = model.getValue();
                            vehicleHealthDataModelMap.put("chargingSystem", model);
                            break;
                        case "engineTemperature":
                            engineTemp = model.getValue();
                            vehicleHealthDataModelMap.put("engineTemperature", model);
                            break;
                    }
                }
                fragmentNewVehiclePerformanceBinding.setTitle("Critical Faults");
                performanceModels.add(new PerformanceModel("Charging System : " + chargingSystem, "If it gets red then you might be stuck on the road asking for a lift.", getResources().getDrawable(R.drawable.icon_battery_performance), 0));
                performanceModels.add(new PerformanceModel("Engine Temperature : " + engineTemp, "Overheating can cause extensive engine damage, so if it gets red immediately turn off your car", getResources().getDrawable(R.drawable.icon_coolant), 0));
                performanceModels.add(new PerformanceModel("Oil Pressure : " + oilPressure, "A low oil level should be addressed immediately to prevent engine damage.", getResources().getDrawable(R.drawable.icon_engineoil), 0));
                //loadRecyclerView(criticalFaultsMap);
                break;

            case "0":
                ArrayList<VehicleHealthDataModel> performanceMeasures = databaseHelper.fetchPerformanceMeasures();
                //Map<String, VehicleHealthDataModel> performanceMeasuresMap = new HashMap<>();
                String idleTime = "";
                String battery = "";
                String coolantTemp = "";
                String engineLoad = "";
                for (VehicleHealthDataModel model: performanceMeasures){
                    switch (model.getCode()){
                        case "engineIdlingTime":
                            idleTime = model.getValue();
                            vehicleHealthDataModelMap.put("engineIdlingTime", model);
                            break;
                        case "batteryVoltage":
                            battery = model.getValue();
                            vehicleHealthDataModelMap.put("batteryVoltage", model);
                            break;
                        case "engineCoolantTemp":
                            coolantTemp = model.getValue();
                            vehicleHealthDataModelMap.put("engineCoolantTemp", model);
                            break;
                        case "engineLoad":
                            engineLoad = model.getValue();
                            vehicleHealthDataModelMap.put("engineLoad", model);
                            break;
                    }
                }
                fragmentNewVehiclePerformanceBinding.setTitle("Performance Parameters");
                performanceModels.add(new PerformanceModel("Engine Idling : " + idleTime, " 3 minutes of idling consumes fuel of 2Km of the drive.", getResources().getDrawable(R.drawable.icon_engine_idle), 0));
                performanceModels.add(new PerformanceModel("Battery: " + battery, "Battery is considered fully charged at 12.6 Volts or higher.", getResources().getDrawable(R.drawable.icon_battery_performance), 0));
                performanceModels.add(new PerformanceModel("Coolant: " + coolantTemp, " Engine coolant above 100°C can be harmful to your engine.", getResources().getDrawable(R.drawable.icon_coolant), 0));
                performanceModels.add(new PerformanceModel("Engine Load: " + engineLoad, "Engine load above 80% consistently can reduce the life of engine parts.", getResources().getDrawable(R.drawable.icon_engine_load), 0));
                //loadRecyclerView(performanceMeasuresMap);
                break;

            case "1":
                ArrayList<VehicleHealthDataModel> faultCodes = databaseHelper.fetchFaultCodes();
                String engine = "";
                String emission = "";
                String transmission = "";
                String controlSystem = "";
                for(VehicleHealthDataModel model: faultCodes){
                    switch (model.getCode()){
                        case "engine":
                            engine = model.getValue();
                            vehicleHealthDataModelMap.put("engine", model);
                            break;
                        case "emission":
                            emission = model.getValue();
                            vehicleHealthDataModelMap.put("emission", model);
                            break;
                        case "Control System":
                            controlSystem = model.getValue();
                            vehicleHealthDataModelMap.put("Control System", model);
                            break;
                        case "transmission":
                            transmission = model.getValue();
                            vehicleHealthDataModelMap.put("transmission", model);
                            break;
                    }
                }
                performanceModels.add(new PerformanceModel("Engine : " + engine, "Engine coolant above 100°C can be harmful to your engine.", getResources().getDrawable(R.drawable.icon_engine), 0));
                performanceModels.add(new PerformanceModel("Emission : " + emission, "Excessive air pollutants from cars can lead to smog and adverse health impacts.", getResources().getDrawable(R.drawable.icon_emission), 0));
                performanceModels.add(new PerformanceModel("Transmission : " + transmission, " If you have a bad transmission it's only a matter of time before your vehicle literally won't be able to drive anywhere.", getResources().getDrawable(R.drawable.icon_transmission), 0));
                performanceModels.add(new PerformanceModel("Control system : " + controlSystem, "90% of car functions are controlled by a Control system.", getResources().getDrawable(R.drawable.icon_controlsystem), 0));

                fragmentNewVehiclePerformanceBinding.setTitle("Fault codes");
                //loadRecyclerView(faultCodes);
                break;
        }
        loadRecyclerView(vehicleHealthDataModelMap);
    }

    NewperformanceListAdapter performanceListAdapter;

    private void loadRecyclerView(Map<String, VehicleHealthDataModel> healthDataModels) {
        performanceListAdapter = new NewperformanceListAdapter(performanceModels, getContext(), healthDataModels);
        fragmentNewVehiclePerformanceBinding.recyclerView.setAdapter(performanceListAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
        responseFromServer1 = null;

    }


}