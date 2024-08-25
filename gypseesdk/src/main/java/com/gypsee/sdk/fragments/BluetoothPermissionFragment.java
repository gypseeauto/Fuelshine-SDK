package com.gypsee.sdk.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.FragmentBluetoothPermissionBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BluetoothPermissionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BluetoothPermissionFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private static final int REQUEST_PERMISSIONS = 103;

    private FragmentBluetoothPermissionBinding bluetoothPermissionBinding;

    public BluetoothPermissionFragment() {
        // Required empty public constructor
    }

    public static BluetoothPermissionFragment newInstance(String param1, String param2) {
        BluetoothPermissionFragment fragment = new BluetoothPermissionFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bluetoothPermissionBinding = FragmentBluetoothPermissionBinding.inflate(inflater, container, false);

     bluetoothPermissionBinding.bluetoothPermissionBtn.setOnClickListener(v -> {
//           requestPermissions();
         if (getActivity() instanceof PermissionActivity) {
             ((PermissionActivity) getActivity()).onAllPermissionsGranted();
         }
     });

        // Inflate the layout for this fragment
        return bluetoothPermissionBinding.getRoot();
    }



}