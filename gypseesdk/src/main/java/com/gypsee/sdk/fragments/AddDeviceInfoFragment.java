package com.gypsee.sdk.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.gypsee.sdk.Adapters.InstructionAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.FragmentAddDeviceInfoBinding;

public class AddDeviceInfoFragment extends Fragment {

    Context context;
    FragmentAddDeviceInfoBinding fragmentAddDeviceInfoBinding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";

    public static AddDeviceInfoFragment newInstance(String deviceID, String imageURL, ArrayList<String> instructions, boolean isCustom) {
        AddDeviceInfoFragment fragment = new AddDeviceInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, imageURL);
        args.putStringArrayList(ARG_PARAM2, instructions);
        args.putString(ARG_PARAM3, deviceID);
        args.putBoolean(ARG_PARAM4, isCustom);
        fragment.setArguments(args);
        return fragment;
    }

    private String imageURL;
    private ArrayList<String> instructions = new ArrayList<>();
    private String deviceID;
    private boolean isCustom;
    String TAG = getClass().getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageURL = getArguments().getString(ARG_PARAM1);
            instructions = getArguments().getStringArrayList(ARG_PARAM2);
            deviceID = getArguments().getString(ARG_PARAM3);
            isCustom = getArguments().getBoolean(ARG_PARAM4, true);
            Log.e(TAG, deviceID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentAddDeviceInfoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_device_info, container, false);



        initToolbar();
        initViews();


        return fragmentAddDeviceInfoBinding.getRoot();
    }


    private void initViews(){

        /*if (instructions.isEmpty() || imageURL == null || imageURL.equals("")){
            *//*instructions.add("Plug the charger in cigarette lighter port. LED Display will show BT, It means device is ready for bluetooth pairing.");
            instructions.add("Go to the Bluetooth device list and tap \"Gypsee Auto 15\"");*//*
            SearchBluetoothDeviceFragment fragment = SearchBluetoothDeviceFragment.newInstance(deviceID, isCustom);
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFrameLayout, fragment, "SearchBluetoothDeviceFragment")
                    .addToBackStack(null)
                    .commit();
        }*/

        Log.e("TAG", "image url: " + imageURL);
        if (imageURL != null && !imageURL.equals("")){
            Picasso.get().load(imageURL).placeholder(R.drawable.gypsee_theme_logo).into(fragmentAddDeviceInfoBinding.imageView5);
        }

        fragmentAddDeviceInfoBinding.instructionList.setLayoutManager(new LinearLayoutManager(context));
        fragmentAddDeviceInfoBinding.instructionList.setAdapter(new InstructionAdapter(instructions));

        fragmentAddDeviceInfoBinding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchBluetoothDeviceFragment fragment = SearchBluetoothDeviceFragment.newInstance(deviceID, isCustom);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, fragment, "SearchBluetoothDeviceFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });



    }


    private void initToolbar(){

        Toolbar toolbar = fragmentAddDeviceInfoBinding.toolBarLayout.toolbar;
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);

        fragmentAddDeviceInfoBinding.toolBarLayout.setTitle("Add Device");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
    }

    private void goBack() {
        ((AppCompatActivity) context).getSupportFragmentManager().popBackStackImmediate();

    }


}
