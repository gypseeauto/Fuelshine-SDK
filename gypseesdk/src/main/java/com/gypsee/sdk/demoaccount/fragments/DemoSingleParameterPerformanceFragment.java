package com.gypsee.sdk.demoaccount.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.gypsee.sdk.Adapters.NewperformanceListAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.FragmentSingleParameterPerformanceBinding;
import com.gypsee.sdk.models.PerformanceModel;
import com.gypsee.sdk.models.VehicleHealthDataModel;

public class DemoSingleParameterPerformanceFragment extends Fragment {

    private String mParam1;

    String TAG = DemoSingleParameterPerformanceFragment.class.getName();

    public DemoSingleParameterPerformanceFragment() {
        // Required empty public constructor
    }

    private static final String ARG_PARAM1 = "param1";

    public static DemoSingleParameterPerformanceFragment newInstance(String param1) {
        DemoSingleParameterPerformanceFragment fragment = new DemoSingleParameterPerformanceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    FragmentSingleParameterPerformanceBinding fragmentNewVehiclePerformanceBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentNewVehiclePerformanceBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_single_parameter_performance, container, false);
        context = getContext();
        setTitleTv();
        //registerBroadcastReceivers();
        return fragmentNewVehiclePerformanceBinding.getRoot();
    }

    ArrayList<Boolean> circleImage = new ArrayList<>();

    ArrayList<PerformanceModel> performanceModels = new ArrayList<>();

    private void setTitleTv() {

        circleImage.add(0, true);
        circleImage.add(1, true);
        circleImage.add(2, true);
        circleImage.add(3, true);

        switch (mParam1) {
            case "2":
                fragmentNewVehiclePerformanceBinding.setTitle("Critical Faults");
                performanceModels.add(new PerformanceModel("Charging System : OK", "If it gets red then you might be stuck on the road asking for a lift.", getResources().getDrawable(R.drawable.icon_battery_performance), 0));
                performanceModels.add(new PerformanceModel("Engine Temperature : OK", "Overheating can cause extensive engine damage, so if it gets red immediately turn off your car", getResources().getDrawable(R.drawable.icon_coolant), 0));
                performanceModels.add(new PerformanceModel("Oil Pressure : OK", "A low oil level should be addressed immediately to prevent engine damage.", getResources().getDrawable(R.drawable.icon_engineoil), 0));
                break;
            case "0":
                fragmentNewVehiclePerformanceBinding.setTitle("Performance Parameters");
                performanceModels.add(new PerformanceModel("Engine Idling : 0", " 3 minutes of idling consumes fuel of 2Km of the drive.", getResources().getDrawable(R.drawable.icon_engine_idle), 0));
                performanceModels.add(new PerformanceModel("Battery: 14.0V", "Battery is considered fully charged at 12.6 Volts or higher", getResources().getDrawable(R.drawable.icon_battery_performance), 0));
                performanceModels.add(new PerformanceModel("Coolant: 83C", " Engine coolant above 100°C can be harmful to your engine.", getResources().getDrawable(R.drawable.icon_coolant), 0));
                performanceModels.add(new PerformanceModel("Engine Load: 30%", "Engine load above 80% consistently can reduce the life of engine parts.", getResources().getDrawable(R.drawable.icon_engine_load), 0));

                break;
            case "1":
                performanceModels.add(new PerformanceModel("Engine : OK", "Engine coolant above 100°C can be harmful to your engine.", getResources().getDrawable(R.drawable.icon_engine), 0));
                performanceModels.add(new PerformanceModel("Emission : OK", "Excessive air pollutants from cars can lead to smog and adverse health impacts.", getResources().getDrawable(R.drawable.icon_emission), 0));
                performanceModels.add(new PerformanceModel("Transmission : OK", " If you have a bad transmission it's only a matter of time before your vehicle literally won't be able to drive anywhere.", getResources().getDrawable(R.drawable.icon_transmission), 0));
                performanceModels.add(new PerformanceModel("Control system : OK", "90% of car functions are controlled by a Control system.", getResources().getDrawable(R.drawable.icon_controlsystem), 0));

                fragmentNewVehiclePerformanceBinding.setTitle("Fault codes");
                break;
        }
        loadRecyclerView();
    }

    NewperformanceListAdapter performanceListAdapter;

    private void loadRecyclerView() {
        Map<String, VehicleHealthDataModel> newData = new HashMap<>();
        newData.put("demo", null);
        performanceListAdapter = new NewperformanceListAdapter(performanceModels, getContext(), newData);
        fragmentNewVehiclePerformanceBinding.recyclerView.setAdapter(performanceListAdapter);
    }


}
