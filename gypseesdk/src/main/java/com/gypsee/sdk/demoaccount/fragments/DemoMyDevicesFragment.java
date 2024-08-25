package com.gypsee.sdk.demoaccount.fragments;

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
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;

import com.gypsee.sdk.Adapters.MyDevicesAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.FragmentMyDevicesBinding;
import com.gypsee.sdk.models.BluetoothDeviceModel;
import com.gypsee.sdk.utils.RecyclerItemClickListener;

public class DemoMyDevicesFragment extends Fragment {

    public DemoMyDevicesFragment(){}
    FragmentMyDevicesBinding fragmentMyDevicesBinding;

    ArrayList<BluetoothDeviceModel> deviceList = new ArrayList<>();

    private String TAG = DemoMyDevicesFragment.class.getSimpleName();

    Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private MyDevicesAdapter myDevicesAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //super.onCreateView(inflater, container, savedInstanceState);
        fragmentMyDevicesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_devices, container, false);
        myDevicesAdapter = new MyDevicesAdapter(deviceList);

        initToolbar();
        initViews();


        return fragmentMyDevicesBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initViews(){

        deviceList.clear();
        myDevicesAdapter.notifyDataSetChanged();

        BluetoothDeviceModel model1 = new BluetoothDeviceModel(
                "1",
                "Gypsee Smart Charger",
                "Charger",
                "Auto 12",
                "AUDIO_VIDEO",
                "AUDIO_VIDEO",
                "00:11:22:AA:BB:CC",
                "https://firebasestorage.googleapis.com/v0/b/gypsee-6ee24.appspot.com/o/Car%20Charger-01-1.png?alt=media&token=a476d053-e771-439a-bb58-d172462ec199",
                "2021-07-21 15:28:59",
                "2021-07-21 15:26:15",
                "2021-07-21 15:26:15",
                false,
                null
        );

        BluetoothDeviceModel model2 = new BluetoothDeviceModel(
                "1",
                "OBD-II",
                "OBD",
                "OBD-II",
                "AUDIO_VIDEO",
                "AUDIO_VIDEO",
                "00:11:22:AA:BB:CC",
                "https://firebasestorage.googleapis.com/v0/b/gypsee-6ee24.appspot.com/o/Gypsee_OBD-01.png?alt=media&token=8ece19fb-f48d-45c3-84f6-33092e775c81",
                "2021-07-21 15:28:59",
                "2021-07-21 15:26:15",
                "2021-07-21 15:26:15",
                true,
                null
        );

        deviceList.add(model1);
        deviceList.add(model2);

        myDevicesAdapter.notifyDataSetChanged();
        //refreshRegisteredDevices();

        fragmentMyDevicesBinding.deviceRecyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        fragmentMyDevicesBinding.deviceRecyclerView.setAdapter(myDevicesAdapter);
        fragmentMyDevicesBinding.deviceRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, fragmentMyDevicesBinding.deviceRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.e(TAG, "position: " + position);
                if (position == deviceList.size()){
                    Log.e(TAG, "position here: " + position);
                    DemoSelectDeviceFragment selectDeviceFragment = new DemoSelectDeviceFragment();
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.mainFrameLayout, selectDeviceFragment, "DemoSelectDeviceFragment")
                            .addToBackStack(null)
                            .commit();
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void initToolbar(){

        Toolbar toolbar = fragmentMyDevicesBinding.toolBarLayout.toolbar;
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);

        fragmentMyDevicesBinding.toolBarLayout.setTitle("My Devices");
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






}
