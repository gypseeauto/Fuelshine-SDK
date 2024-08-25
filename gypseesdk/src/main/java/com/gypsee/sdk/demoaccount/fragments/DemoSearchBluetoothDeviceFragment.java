package com.gypsee.sdk.demoaccount.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.FragmentSearchBluetoothDeviceBinding;
import com.gypsee.sdk.demoaccount.adapters.DemoBluetoothListAdapter;
import com.gypsee.sdk.helpers.BluetoothHelperClass;
import com.gypsee.sdk.jsonParser.ResponseFromServer;
import com.gypsee.sdk.models.BluetoothDeviceModel;
import com.gypsee.sdk.utils.RecyclerItemClickListener;

public class DemoSearchBluetoothDeviceFragment extends Fragment {

    public DemoSearchBluetoothDeviceFragment() {
        // Required empty public constructor
    }


    Context context;
    FragmentSearchBluetoothDeviceBinding fragmentSearchBluetoothDeviceBinding;

    private String TAG = DemoSearchBluetoothDeviceFragment.class.getName();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    DemoBluetoothListAdapter pairedListAdapter;
    ArrayList<BluetoothDeviceModel> pairedList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentSearchBluetoothDeviceBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_bluetooth_device, container, false);

        initToolbar();

        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                fragmentSearchBluetoothDeviceBinding.searchLayout.setVisibility(View.GONE);
                fragmentSearchBluetoothDeviceBinding.listLayout.setVisibility(View.VISIBLE);
            }
        }, 1000);

        BluetoothDeviceModel model1 = new BluetoothDeviceModel(
                "1",
                "Gypsee Charger",
                "Smart Charger",
                "Gypsee CX1",
                "AUDIO_VIDEO",
                "AUDIO_VIDEO",
                "00:11:22:AA:BB:CC",
                "https://firebasestorage.googleapis.com/v0/b/gypsee-6ee24.appspot.com/o/smart_charger_tmp.png?alt=media&token=8f1ed0b1-80d7-4dd1-99d2-3635b39e3b37",
                "2021-07-21 15:28:59",
                "2021-07-21 15:26:15",
                "2021-07-21 15:26:15",
                false,
                null
        );

        BluetoothDeviceModel model2 = new BluetoothDeviceModel(
                "1",
                "OBDII",
                "OBD",
                "OBDII",
                "AUDIO_VIDEO",
                "AUDIO_VIDEO",
                "00:11:22:AA:BB:CC",
                "https://firebasestorage.googleapis.com/v0/b/gypsee-6ee24.appspot.com/o/smart_charger_tmp.png?alt=media&token=8f1ed0b1-80d7-4dd1-99d2-3635b39e3b37",
                "2021-07-21 15:28:59",
                "2021-07-21 15:26:15",
                "2021-07-21 15:26:15",
                false,
                null
        );

        BluetoothDeviceModel model3 = new BluetoothDeviceModel(
                "1",
                "Audio Receiver",
                "Audio",
                "Audio Receiver",
                "AUDIO_VIDEO",
                "AUDIO_VIDEO",
                "00:11:22:AA:BB:CC",
                "https://firebasestorage.googleapis.com/v0/b/gypsee-6ee24.appspot.com/o/smart_charger_tmp.png?alt=media&token=8f1ed0b1-80d7-4dd1-99d2-3635b39e3b37",
                "2021-07-21 15:28:59",
                "2021-07-21 15:26:15",
                "2021-07-21 15:26:15",
                false,
                null
        );

        BluetoothDeviceModel model4 = new BluetoothDeviceModel(
                "1",
                "Custom Device",
                "Custom",
                "Custom Device",
                "AUDIO_VIDEO",
                "AUDIO_VIDEO",
                "00:11:22:AA:BB:CC",
                "https://firebasestorage.googleapis.com/v0/b/gypsee-6ee24.appspot.com/o/smart_charger_tmp.png?alt=media&token=8f1ed0b1-80d7-4dd1-99d2-3635b39e3b37",
                "2021-07-21 15:28:59",
                "2021-07-21 15:26:15",
                "2021-07-21 15:26:15",
                true,
                null
        );

        pairedList.add(model1);
        pairedList.add(model2);
        pairedList.add(model3);
        pairedList.add(model4);



        pairedListAdapter = new DemoBluetoothListAdapter(pairedList);
        fragmentSearchBluetoothDeviceBinding.pairedListRecyclerview.setLayoutManager(new LinearLayoutManager(context));
        fragmentSearchBluetoothDeviceBinding.pairedListRecyclerview.setAdapter(pairedListAdapter);


        devicesList.add(model1);
        devicesList.add(model2);
        devicesList.add(model3);
        devicesList.add(model4);


        bluetoothListAdapter = new DemoBluetoothListAdapter(devicesList);
        fragmentSearchBluetoothDeviceBinding.bluetoothListRecyclerview.setLayoutManager(new LinearLayoutManager(context));
        fragmentSearchBluetoothDeviceBinding.bluetoothListRecyclerview.setAdapter(bluetoothListAdapter);
        setOnclickListeners();


        return fragmentSearchBluetoothDeviceBinding.getRoot();
    }

    private DemoBluetoothListAdapter bluetoothListAdapter;



    private void initToolbar(){
        Toolbar toolbar = fragmentSearchBluetoothDeviceBinding.toolBarLayout.toolbar;
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);

        fragmentSearchBluetoothDeviceBinding.toolBarLayout.setTitle("Add Device");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        fragmentSearchBluetoothDeviceBinding.toolBarLayout.refreshIcon.setVisibility(View.VISIBLE);
        fragmentSearchBluetoothDeviceBinding.toolBarLayout.refreshIcon.setImageResource(R.drawable.refresh_icon);

    }

    private void goBack() {
        ((AppCompatActivity) context).getSupportFragmentManager().popBackStackImmediate();
    }


    private void setOnclickListeners(){


        fragmentSearchBluetoothDeviceBinding.bluetoothListRecyclerview.addOnItemTouchListener(new RecyclerItemClickListener(context, fragmentSearchBluetoothDeviceBinding.bluetoothListRecyclerview
                , new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!devicesList.isEmpty()){
                    BluetoothHelperClass.showOkCancelDialog(context, getLayoutInflater(), "Alert!", "Are you Pairing Car based Bluetooth device?", responseFromServer, 1);
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));


        fragmentSearchBluetoothDeviceBinding.pairedListRecyclerview.addOnItemTouchListener(new RecyclerItemClickListener(context, fragmentSearchBluetoothDeviceBinding.pairedListRecyclerview
                , new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                BluetoothHelperClass.showOkCancelDialog(context, getLayoutInflater(), "Alert!", "Are you Pairing Car based Bluetooth device?", responseFromServer, 1);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

    }

    private ResponseFromServer responseFromServer = new ResponseFromServer() {
        @Override
        public void responseFromServer(String Response, String className, int value) {
            switch (value){
                case 1:
                    Toast.makeText(context, "Device Registered", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                    break;

                case -1:
                    break;

            }
        }
    };

    ArrayList<BluetoothDeviceModel> devicesList = new ArrayList<>();

}
