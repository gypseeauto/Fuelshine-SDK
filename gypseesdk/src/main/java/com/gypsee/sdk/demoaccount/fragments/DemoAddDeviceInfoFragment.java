package com.gypsee.sdk.demoaccount.fragments;

import android.content.Context;
import android.os.Bundle;
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

public class DemoAddDeviceInfoFragment extends Fragment {

    public DemoAddDeviceInfoFragment(){}

    Context context;
    FragmentAddDeviceInfoBinding fragmentAddDeviceInfoBinding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
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

        String imageURL = "https://firebasestorage.googleapis.com/v0/b/gypsee-6ee24.appspot.com/o/smart_charger_tmp.png?alt=media&token=8f1ed0b1-80d7-4dd1-99d2-3635b39e3b37";
        Picasso.get().load(imageURL).placeholder(R.drawable.gypsee_theme_logo).into(fragmentAddDeviceInfoBinding.imageView5);

        ArrayList<String> instructions = new ArrayList<>();
        instructions.add("Plug the charger in cigarette ligher port. LED Display will show BT, It means device is ready for bluettoth pairing.");
        instructions.add("Go to the Blueetooth device list and tap \"Gypsee Auto 15\" to start pairing.");

        fragmentAddDeviceInfoBinding.instructionList.setLayoutManager(new LinearLayoutManager(context));
        fragmentAddDeviceInfoBinding.instructionList.setAdapter(new InstructionAdapter(instructions));

        fragmentAddDeviceInfoBinding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DemoSearchBluetoothDeviceFragment fragment = new DemoSearchBluetoothDeviceFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, fragment, "DemoSearchBluetoothDeviceFragment")
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
