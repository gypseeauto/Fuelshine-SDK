package com.gypsee.sdk.demoaccount.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.gypsee.sdk.Adapters.PerformanceListAdapter;
import com.gypsee.sdk.R;

public class DemoPerformanceFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<String> ids = new ArrayList<>();
    TextView troubleCode;

    PerformanceListAdapter adapter;

    public static DemoPerformanceFragment newInstance() {

        Bundle args = new Bundle();

        DemoPerformanceFragment fragment = new DemoPerformanceFragment();
        //fragment.setArguments(args);
        return fragment;
    }

    public DemoPerformanceFragment() {
    }

    private View view;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_performance, container, false);
        context = getContext();
        //registerBroadcastReceivers();


        recyclerView = view.findViewById(R.id.rv_all);
        adapter = new PerformanceListAdapter();
//        troubleCode = view.findViewById(R.id.tv_trouble_code);
        updateList();
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void updateList() {

        //static values

        adapter.data.add("Vehicle Speed:30km/h");
        adapter.data.add("Fuel Type:Petrol");
        adapter.data.add("Distance since codes cleared:50km");
        adapter.data.add("Engine RPM:2000RPM");
        adapter.data.add("Engine Load:30%");
        adapter.data.add("Engine Coolant Temperature:83C");
        adapter.data.add("Throttle Position:8.6%");
        adapter.data.add("Distance traveled with MIL on:0km");
        adapter.data.add("Control Module Power Supply:14.0V");
        adapter.data.add("Diagnostic Trouble Codes:MIL is OFF0 codes");

        adapter.notifyDataSetChanged();
    }


}
