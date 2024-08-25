package com.gypsee.sdk.fragments;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.FragmentPhysicalActivityPermissionBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class PhysicalActivityPermissionFragment extends Fragment {

    private static final int REQUEST_PHYSICAL_ACTIVITY_PERMISSION = 1;
    private FragmentPhysicalActivityPermissionBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPhysicalActivityPermissionBinding.inflate(inflater, container, false);

        binding.activityPermissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPhysicalActivityPermission();
            }
        });

        return binding.getRoot();
    }

    // Register the permissions callback, which handles the user's response to the
// system permissions dialog. Save the return value, an instance of
// ActivityResultLauncher, as an instance variable.
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    onPermissionGranted();
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

    private void requestPhysicalActivityPermission() {


        if (ContextCompat.checkSelfPermission(
                getContext(),android.Manifest.permission.ACTIVITY_RECOGNITION) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            onPermissionGranted();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                getActivity(),android.Manifest.permission.ACTIVITY_RECOGNITION)) {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected, and what
            // features are disabled if it's declined. In this UI, include a
            // "cancel" or "no thanks" button that lets the user continue
            // using your app without granting the permission.
            requestPermissionLauncher.launch(
                    android.Manifest.permission.ACTIVITY_RECOGNITION);
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(
                    android.Manifest.permission.ACTIVITY_RECOGNITION);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PHYSICAL_ACTIVITY_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted();
            } else {
                Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onPermissionGranted() {

        if (getActivity() instanceof PermissionActivity) {
            ((PermissionActivity) getActivity()).onPermissionsClick();
        }
    }
}