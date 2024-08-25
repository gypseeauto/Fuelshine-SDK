package com.gypsee.sdk.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import com.gypsee.sdk.Adapters.EmergencyContactsRecyclerAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.EmergencyMainBinding;
import com.gypsee.sdk.firebase.FirebaseLogEvents;
import com.gypsee.sdk.models.KeyvalueModelClass;


public class Emergencyactivity extends AppCompatActivity {

    EmergencyMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.emergency_main);
        FirebaseLogEvents.firebaseLogEvent("accessed_emergency_contact_card");
        initViews();
    }

    private void initViews() {

        ArrayList<KeyvalueModelClass> keyvalueModelClasses = new ArrayList<>();
        keyvalueModelClasses.add(new KeyvalueModelClass("National Covid 19 helpline", "1075", getResources().getDrawable(R.drawable.mask)));
        keyvalueModelClasses.add(new KeyvalueModelClass("National Emergency Number", "112", getResources().getDrawable(R.drawable.ic_national_helpline)));
        keyvalueModelClasses.add(new KeyvalueModelClass("Ambulance ", "108", getResources().getDrawable(R.drawable.icon_ambulance)));
        keyvalueModelClasses.add(new KeyvalueModelClass("Fire Brigade", "101", getResources().getDrawable(R.drawable.icon_firebrigade)));
        keyvalueModelClasses.add(new KeyvalueModelClass("National Highway Helpline", "1033", getResources().getDrawable(R.drawable.icon_highway)));
        keyvalueModelClasses.add(new KeyvalueModelClass("Women Helpline", "1091", getResources().getDrawable(R.drawable.icon_women)));
        keyvalueModelClasses.add(new KeyvalueModelClass("Disaster Management", "1078", getResources().getDrawable(R.drawable.ic_disaster)));

        keyvalueModelClasses.add(new KeyvalueModelClass("Senior Citizen Helpline", "1091", getResources().getDrawable(R.drawable.ic_senior_citizen_helpline)));
        keyvalueModelClasses.add(new KeyvalueModelClass("Railway Accident Emergency Service", "1072", getResources().getDrawable(R.drawable.icon_railway)));

        keyvalueModelClasses.add(new KeyvalueModelClass("Children Emergency Service", "1098", getResources().getDrawable(R.drawable.ic_child_helpline)));
        keyvalueModelClasses.add(new KeyvalueModelClass("Tourist Helpline", "1800111363", getResources().getDrawable(R.drawable.ic_tourist_helpline)));
        keyvalueModelClasses.add(new KeyvalueModelClass("Railway Enquiry", "139", getResources().getDrawable(R.drawable.icon_railway)));
        keyvalueModelClasses.add(new KeyvalueModelClass("Traffic Police Number", "103", getResources().getDrawable(R.drawable.ic_traffic_police)));

        Log.e("Emergencyactivity", "Size of recycler Adapter : " + keyvalueModelClasses.size());

        EmergencyContactsRecyclerAdapter emergencyContactsRecyclerAdapter = new EmergencyContactsRecyclerAdapter(Emergencyactivity.this, keyvalueModelClasses);
        activityMainBinding.emergencyRecyclerView.setAdapter(emergencyContactsRecyclerAdapter);

       /* activityMainBinding.emergencyRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, activityMainBinding.emergencyRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever
                        //Send brand name back
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + keyvalueModelClasses.get(position).getDescription()));
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );*/


    }
}