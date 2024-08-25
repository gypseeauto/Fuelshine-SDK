package com.gypsee.sdk.fragments;

import static android.content.Context.POWER_SERVICE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.databinding.FragmentBatteryOptimisationPermissionBinding;
import com.gypsee.sdk.databinding.FragmentNotificationPermissionBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BatteryOptimisationPermissionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BatteryOptimisationPermissionFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_CODE_BATTERY_OPTIMIZATIONS = 1001;

    private String mParam1;
    private String mParam2;

    public BatteryOptimisationPermissionFragment() {
        // Required empty public constructor
    }

    public static BatteryOptimisationPermissionFragment newInstance(String param1, String param2) {
        BatteryOptimisationPermissionFragment fragment = new BatteryOptimisationPermissionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private FragmentBatteryOptimisationPermissionBinding batteryOptimisationPermissionBinding;
    private MyPreferenece myPreferenece;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        batteryOptimisationPermissionBinding = FragmentBatteryOptimisationPermissionBinding.inflate(inflater, container, false);

        myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, requireContext());


        batteryOptimisationPermissionBinding.batteryOptimisationPermissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoBatteryOptimization();
            }
        });

        return batteryOptimisationPermissionBinding.getRoot();
    }

    private static final int REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = 1;

    private void gotoBatteryOptimization() {
        PowerManager powerManager = (PowerManager) requireContext().getSystemService(POWER_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (powerManager.isIgnoringBatteryOptimizations(requireContext().getPackageName())) {
                Log.e("TAG", "No battery restriction: ");
                gotoMainActivity();
                myPreferenece.setQueryAllPackagesPermission(true);
            } else {
                Log.e("TAG", "Battery restriction: ");

                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + requireContext().getPackageName()));
                startActivityForResult(intent, REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) {
            PowerManager powerManager = (PowerManager) requireContext().getSystemService(POWER_SERVICE);
            if (powerManager.isIgnoringBatteryOptimizations(requireContext().getPackageName())) {
                Log.e("TAG", "Battery optimization ignored: ");
                gotoMainActivity();
                myPreferenece.setQueryAllPackagesPermission(true);
            } else {
                Log.e("TAG", "Battery optimization not ignored: ");
            }
        }
    }

    private void gotoMainActivity() {
        if (getActivity() instanceof PermissionActivity) {
            ((PermissionActivity) getActivity()).gotoMainActivity();
        }
    }


}

