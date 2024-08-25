package com.gypsee.sdk.demoaccount.fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import java.util.HashMap;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.FragmentCarScanBinding;
import com.gypsee.sdk.demoaccount.activities.DemoSecondActivity;

public class DemoCarScanFragment extends Fragment implements View.OnClickListener {

    public DemoCarScanFragment() {
        // Required empty public constructor
    }

    public static DemoCarScanFragment newInstance(String param1, String param2) {
        DemoCarScanFragment fragment = new DemoCarScanFragment();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    FragmentCarScanBinding fragmentCarScanBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentCarScanBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_car_scan, container, false);
        initViews();
        initToolbar();
        return fragmentCarScanBinding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void initViews() {
        fragmentCarScanBinding.confirmedTxt.setText("A confirmed fault code is defined as the DTC stored when an OBD system has confirmed that a malfunction exists.");
        fragmentCarScanBinding.pendingTxt.setText("A pending fault code is defined as the diagnostic trouble code, stored as a result of initial detection of a malfunction, before the activation of the Malfunction Indicator Lamp(MIL).");
        fragmentCarScanBinding.permanentTxt.setText("The DTCs stored as Permanent can not be cleared with any scan tool. Permanent codes automatically clean themselves after repairs have been made and the related system monitor runs successfully.");

        changeBackgrundColor(fragmentCarScanBinding.confirmedTxt, ContextCompat.getColor(context, R.color.gold_color));
        changeBackgrundColor(fragmentCarScanBinding.pendingTxt, ContextCompat.getColor(context, R.color.colorPrimary));
        changeBackgrundColor(fragmentCarScanBinding.permanentTxt, ContextCompat.getColor(context, R.color.eco_color));


        fragmentCarScanBinding.readTxt.setOnClickListener(this);
        fragmentCarScanBinding.clearTxt.setOnClickListener(this);
        fragmentCarScanBinding.scanButton.setOnClickListener(this);

        String sourceString = "While scanning, it is recommended to keep your car at stationary position. Our scan feature works aptly when car's engine light is ON.";
        fragmentCarScanBinding.carScanTxt.setText(Html.fromHtml(sourceString));
    }

    String TAG = DemoCarScanFragment.class.getName();

    private void changeBackgrundColor(TextView textView, int color) {
        //Changing the background color
        Drawable background = textView.getBackground();
        if (background instanceof ShapeDrawable) {
            // cast to 'ShapeDrawable'
            Log.e(TAG, "Background ShapeDrawable");
            ShapeDrawable shapeDrawable = (ShapeDrawable) background;
            shapeDrawable.getPaint().setColor(color);
        } else if (background instanceof GradientDrawable) {
            // cast to 'GradientDrawable'
            Log.e(TAG, "Background GradientDrawable");

            GradientDrawable gradientDrawable = (GradientDrawable) background;
            gradientDrawable.setColor(color);
        } else if (background instanceof ColorDrawable) {
            // alpha value may need to be set again after this call
            Log.e(TAG, "Background ColorDrawable");

            ColorDrawable colorDrawable = (ColorDrawable) background;
            colorDrawable.setColor(color);
        }
    }

    private void initToolbar(){

        Toolbar toolbar = fragmentCarScanBinding.toolBarLayout.toolbar;
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);

        fragmentCarScanBinding.toolBarLayout.setTitle("SCAN CAR");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
    }

    private void goBack() {
        ((AppCompatActivity) context).finish();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.scanButton) {
            fragmentCarScanBinding.scanCardView.setVisibility(View.GONE);
            fragmentCarScanBinding.carScanMainLayout.setVisibility(View.VISIBLE);

            return;
        } else if (id == R.id.readTxt) {
            enableButtons(false);
            //registerBroadcastReceivers();
            fragmentCarScanBinding.errorTxt.setVisibility(View.GONE);

            ObjectAnimator.ofInt(fragmentCarScanBinding.horizontalProgressBar, "progress", 20)
                    .setDuration(3000)
                    .start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onTapReadText();
                }
            }, 5000);
        } else if (id == R.id.clearTxt) {//Call clear code implementation
            Toast.makeText(context, "Codes cleared", Toast.LENGTH_SHORT).show();
        }
        //Calling enable bluetooth after 3 seconds, because if blutooth is connected in homefragment. it will take 3 sec to disconnect.
        //scannedResult.clear();


       // enableBluetooth();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        enableButtons(true);
        fragmentCarScanBinding.horizontalProgressBar.setProgress(0);
    }

    private void enableButtons(boolean isEnable) {
        if (isEnable) {
            fragmentCarScanBinding.readTxt.setEnabled(true);
            fragmentCarScanBinding.clearTxt.setEnabled(true);
        } else {
            fragmentCarScanBinding.readTxt.setEnabled(false);
            fragmentCarScanBinding.clearTxt.setEnabled(false);
        }
    }

    private void onTapReadText(){

        HashMap<String, String> scannedResult = new HashMap<>();
        scannedResult.put("TROUBLE_CODES", "P0101\nP0303");
        startActivity(new Intent(context, DemoSecondActivity.class)
                .putExtra("TAG", "CarScanResultFragment")
                .putExtra("TroubleCodes", scannedResult));

    }

    @Override
    public void onResume() {
        super.onResume();
        enableButtons(true);
        fragmentCarScanBinding.horizontalProgressBar.setProgress(0);
    }
}
