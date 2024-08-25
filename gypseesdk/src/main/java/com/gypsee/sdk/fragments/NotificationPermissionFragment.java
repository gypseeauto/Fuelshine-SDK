package com.gypsee.sdk.fragments;

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

import java.util.ArrayList;
import java.util.List;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.FragmentLocationPermissionBinding;
import com.gypsee.sdk.databinding.FragmentNotificationPermissionBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationPermissionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class NotificationPermissionFragment extends Fragment {

    private static final int REQUEST_PERMISSIONS = 100;


    public NotificationPermissionFragment() {
        // Required empty public constructor
    }

    public static NotificationPermissionFragment newInstance(String param1, String param2) {
        NotificationPermissionFragment fragment = new NotificationPermissionFragment();

        return fragment;
    }

    private FragmentNotificationPermissionBinding notificationPermissionBinding;



    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
// ActivityResultLauncher, as an instance variable.
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    onPermissionsGranted();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        notificationPermissionBinding = FragmentNotificationPermissionBinding.inflate(inflater, container, false);

        notificationPermissionBinding.notificationPermissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions();
            }
        });

        return notificationPermissionBinding.getRoot();
    }

    private void requestPermissions() {
        List<String> permissions = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(
                    android.Manifest.permission.POST_NOTIFICATIONS);
        } else {
            onPermissionsGranted();

        }
    }


    private void onPermissionsGranted() {
        // Handle case where all permissions are granted
        // You can call a method in the parent activity to switch fragments
        if (getActivity() instanceof PermissionActivity) {
            ((PermissionActivity) getActivity()).onPermissionsClick();
        }
    }

}