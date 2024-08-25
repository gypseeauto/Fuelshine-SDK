package com.gypsee.sdk.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.GypseeMainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigateFragment extends Fragment {

    public NavigateFragment() {
        // Required empty public constructor
    }

    public static NavigateFragment newInstance() {

        Bundle args = new Bundle();

        NavigateFragment fragment = new NavigateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_navigate, container, false);
        context = getContext();
        try {
            startActivity(new Intent(Intent.ACTION_VOICE_COMMAND).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "We are unable to launch assistant. Kindly download google Assistant", Toast.LENGTH_LONG).show();
            // ((AppCompatActivity) context).getSupportFragmentManager().popBackStack();

        }
       /* Intent broadcast = new Intent();
        broadcast.setAction(Intent.ACTION_VOICE_COMMAND);
        context.sendBroadcast(broadcast);*/

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    boolean isAtt = false;

    @Override
    public void onResume() {
        super.onResume();
       Log.e("Navigate", "Onpause called");
        if (isAtt) {
            GypseeMainActivity.activityMainBinding.bottomNavigationView.setSelectedItemId(R.id.nav_drive);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isAtt = true;
    }

}
