package com.gypsee.sdk.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gypsee.sdk.Adapters.TripAlertAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.database.DatabaseHelper;
import com.gypsee.sdk.trips.TripAlert;
import com.gypsee.sdk.trips.TripLog;

import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlertsFragment extends Fragment {


    public AlertsFragment() {
        // Required empty public constructor
    }

    private View view;
    private Context context;
    private RecyclerView tripList;
    TextView empty;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_alerts, container, false);

        ImageView backBtn = view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppCompatActivity) context).getSupportFragmentManager().popBackStack();
            }
        });
        tripList = view.findViewById(R.id.rv_trip_list);
        empty = view.findViewById(R.id.tv_empty);

        initView();
        return view;
    }

    private void initView() {
        TripLog triplog = TripLog.getInstance(this.getContext());
        List<TripAlert> records = new DatabaseHelper(context).fetchAllTripAlerts();
        Collections.reverse(records);
        if (records.size() == 0) {
            empty.setVisibility(View.VISIBLE);
            tripList.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.GONE);
            tripList.setVisibility(View.VISIBLE);
        }

        tripList.setAdapter(new TripAlertAdapter(records, context));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
