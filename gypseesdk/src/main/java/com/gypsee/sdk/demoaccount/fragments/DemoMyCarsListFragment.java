package com.gypsee.sdk.demoaccount.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.demoaccount.adapters.DemoCarListRecyclerViewAdapter;
import com.gypsee.sdk.models.Vehiclemodel;

public class DemoMyCarsListFragment extends Fragment implements View.OnClickListener{

    public DemoMyCarsListFragment() {
    }

    Vehiclemodel connectedVehiclemodel;

    public static DemoMyCarsListFragment newInstance() {

        //Bundle args = new Bundle();
        //args.putParcelable(Vehiclemodel.class.getSimpleName(), vehiclemodel);
        DemoMyCarsListFragment fragment = new DemoMyCarsListFragment();
        //fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            connectedVehiclemodel = new Vehiclemodel("id", "NISSAN MICRA ACTIVE XV", "KA51ME7472", "2015-08-12", "NISSAN",
                    "MICRA ACTIVE XV", "", "", "", "", "", "25000", "", "2000",
                    "2021-05-21", "2021-06-26", "2021-12-01", "1B2MN5666R8976543", true, true, "3000",
                    "10000", "", "Petrol", "",  "JF78MN432123", "Nikhil Kumar", "LMV-NT", "LTT", "LTT",10,30,50);



    }

    private Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_mycars, container, false);
        context = view.getContext();
        initViews(view);


        /*registerBroadcastReceivers();
        fetchVehiclesFromDb();
        checkCarListCount();
*/

        return view;
    }

    private ImageView addIcon, refreshIcon;
    //private TextView emptycarsTv;
    //private ProgressBar progressBar;
    private RelativeLayout backbtn;
    private RecyclerView recyclerview;
    private ArrayList<Vehiclemodel> vehiclemodelArrayList = new ArrayList<>();
    private DemoCarListRecyclerViewAdapter demoCarListRecyclerViewAdapter;

    private void initViews(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolBarLayout);

        backbtn = view.findViewById(R.id.back_button_layout);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);
        TextView toolbarTitle = (TextView) view.findViewById(R.id.toolBarTitle);
       // emptycarsTv = (TextView) view.findViewById(R.id.emptycarsTv);
       // progressBar = view.findViewById(R.id.progressView);
        addIcon = view.findViewById(R.id.rightSideIcon);
        refreshIcon = view.findViewById(R.id.refreshIcon);
        addIcon.setVisibility(View.VISIBLE);
        refreshIcon.setVisibility(View.VISIBLE);
        addIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_icon));
        addIcon.setOnClickListener(this);
        refreshIcon.setOnClickListener(this);

        vehiclemodelArrayList.add(connectedVehiclemodel);

        /*TextView emptycarsTv = view.findViewById(R.id.emptycarsTv);

        String s = getActivity().getIntent().getStringExtra("Reason") == null ? "Click on + icon to set automated reminders." : "Click on + icon to add your beloved car.";
        SpannableString ss1 = new SpannableString(s);
        ss1.setSpan(new RelativeSizeSpan(2f), 8, 10, 0); // set size
        // ss1.setSpan(new ForegroundColorSpan(context.getColor(R.color.theme_blue)), 0, 5, 0);// set color
        emptycarsTv.setText(ss1);
        emptycarsTv.setOnClickListener(this);*/


        toolbarTitle.setText("My Vehicles");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        demoCarListRecyclerViewAdapter = new DemoCarListRecyclerViewAdapter(vehiclemodelArrayList, context);



        recyclerview = view.findViewById(R.id.carListRecyclerview);
        recyclerview.setAdapter(demoCarListRecyclerViewAdapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(context));
    }

    private void goBack() {
        ((AppCompatActivity) context).finish();
    }




    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.refreshIcon || id == R.id.rightSideIcon) {
            Toast.makeText(context, "Please register your account to avail this service", Toast.LENGTH_LONG).show();
        }

    }
}
