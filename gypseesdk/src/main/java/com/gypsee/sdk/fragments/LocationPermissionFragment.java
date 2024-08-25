package com.gypsee.sdk.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.FragmentLocationPermissionBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationPermissionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationPermissionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    public LocationPermissionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LocationPermissionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocationPermissionFragment newInstance(String param1, String param2) {
        LocationPermissionFragment fragment = new LocationPermissionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        com.gypsee.sdk.databinding.FragmentLocationPermissionBinding locationPermissionBinding = FragmentLocationPermissionBinding.inflate(inflater, container, false);


        locationPermissionBinding.locationPermissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Request location permissions
                requestLocationPermissions();
            }
        });

        // Inflate the layout for this fragment
        return locationPermissionBinding.getRoot();
    }

    ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_COARSE_LOCATION, false);
                        if (fineLocationGranted != null && fineLocationGranted) {
                            // Precise location access granted.
                            checkBackGroundLocationPermissionGranted();

                        } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            // Only approximate location access granted.
                            //SHow dialog to ask precise location
                            requestLocationPermissions();

                        } else {
                            // No location access granted.
                            if (getActivity() instanceof PermissionActivity) {
                                ((PermissionActivity) getActivity()). showSettingsDialog();
                            }
                        }
                    }
            );


    private ActivityResultLauncher<String> backgroundLocationPermssionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    onAllPermissionsGranted();
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    if (getActivity() instanceof PermissionActivity) {
                        ((PermissionActivity) getActivity()). showSettingsDialog();
                    }
                }
            });

    private void onAllPermissionsGranted() {
        // Handle case where all permissions are granted
        // You can call a method in the parent activity to switch fragments
        if (getActivity() instanceof PermissionActivity) {
            ((PermissionActivity) getActivity()).onPermissionsClick();
        }
    }

    private void checkBackGroundLocationPermissionGranted() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            showSetLocationDialog();
        } else {
            onAllPermissionsGranted();

        }
    }


    private void requestLocationPermissions() {
        // Before you perform the actual permission request, check whether your app
// already has the permissions, and whether your app needs to show a permission
// rationale dialog. For more details, see Request permissions.
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        } else {
            checkBackGroundLocationPermissionGranted();
        }
    }


    private void showSetLocationDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_set_location_permission, null);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        final androidx.appcompat.app.AlertDialog dialog = builder.create();

        TextView continueButton = dialogView.findViewById(R.id.button_continue);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                backgroundLocationPermssionLauncher.launch(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION);

            }
        });

        dialog.show();
    }


}