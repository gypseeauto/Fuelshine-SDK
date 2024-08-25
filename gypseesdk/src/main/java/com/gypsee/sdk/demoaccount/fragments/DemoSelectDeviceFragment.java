package com.gypsee.sdk.demoaccount.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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

import java.util.ArrayList;

import com.gypsee.sdk.Adapters.DeviceCategoryAdapter;
import com.gypsee.sdk.Adapters.DeviceListAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.databinding.FragmentSelectDeviceBinding;
import com.gypsee.sdk.fragments.ScanningHelpFragment;
import com.gypsee.sdk.models.BluetoothDeviceModel;
import com.gypsee.sdk.utils.RecyclerItemClickListener;

public class DemoSelectDeviceFragment extends Fragment {

    public DemoSelectDeviceFragment(){}

    Context context;
    FragmentSelectDeviceBinding fragmentSelectDeviceBinding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //super.onCreateView(inflater, container, savedInstanceState);

        fragmentSelectDeviceBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_device, container, false);

        allDevices.clear();

        BluetoothDeviceModel model1 = new BluetoothDeviceModel(
                "1",
                "Smart Car Charger",
                "Smart Charger",
                "Gypsee CX1",
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
                false,
                null
        );

        BluetoothDeviceModel model3 = new BluetoothDeviceModel(
                "1",
                "Bluetooth Audio Receiver",
                "Audio",
                "Audio Receiver",
                "AUDIO_VIDEO",
                "AUDIO_VIDEO",
                "00:11:22:AA:BB:CC",
                "https://firebasestorage.googleapis.com/v0/b/gypsee-6ee24.appspot.com/o/Stereo-01.png?alt=media&token=8532ce64-81a8-40ff-8e38-4e50769b87de",
                "2021-07-21 15:28:59",
                "2021-07-21 15:26:15",
                "2021-07-21 15:26:15",
                false,
                null
        );

        BluetoothDeviceModel model4 = new BluetoothDeviceModel(
                "1",
                "Connect Your Car's Bluetooth Device",
                "Custom",
                "Connect Your Car's Bluetooth Device",
                "AUDIO_VIDEO",
                "AUDIO_VIDEO",
                "00:11:22:AA:BB:CC",
                "https://firebasestorage.googleapis.com/v0/b/gypsee-6ee24.appspot.com/o/others%20icon-01.png?alt=media&token=e435803c-5872-4244-ae7f-604b9b507d0f",
                "2021-07-21 15:28:59",
                "2021-07-21 15:26:15",
                "2021-07-21 15:26:15",
                true,
                null
        );

        allDevices.add(model1);
        allDevices.add(model2);
        allDevices.add(model3);
        allDevices.add(model4);



        initToolbar();
        initStaticViews();
        sortCategoryList();
        initViews();


        return fragmentSelectDeviceBinding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
        sortItemList(new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getDeviceCategoryIndex());
    }

    private void initToolbar(){

        Toolbar toolbar = fragmentSelectDeviceBinding.toolBarLayout.toolbar;
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);

        fragmentSelectDeviceBinding.toolBarLayout.setTitle("Add Device");
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


    private void initStaticViews(){
        String text = "Select your device type below and add it manually.\nView Scanning help>>";
        SpannableString ss = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                ScanningHelpFragment fragment = new ScanningHelpFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mainFrameLayout, fragment, "ScanningHelpFragment")
                        .addToBackStack(null).commit();
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        ss.setSpan(clickableSpan, 56, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        fragmentSelectDeviceBinding.scanningHelp.setText(ss);
        fragmentSelectDeviceBinding.scanningHelp.setMovementMethod(LinkMovementMethod.getInstance());
        fragmentSelectDeviceBinding.scanningHelp.setHighlightColor(Color.TRANSPARENT);
    }


    DeviceCategoryAdapter deviceCategoryAdapter;
    DeviceListAdapter deviceListAdapter;

    private void initViews(){

        deviceCategoryAdapter = new DeviceCategoryAdapter(context, categoryNames);
        deviceListAdapter = new DeviceListAdapter(itemList);

        sortItemList(0);

        fragmentSelectDeviceBinding.deviceCategoryRecyclerview.setAdapter(deviceCategoryAdapter);
        fragmentSelectDeviceBinding.deviceItemRecyclerview.setAdapter(deviceListAdapter);

        fragmentSelectDeviceBinding.deviceItemRecyclerview.setLayoutManager(new LinearLayoutManager(context));
        fragmentSelectDeviceBinding.deviceCategoryRecyclerview.setLayoutManager(new LinearLayoutManager(context));

        fragmentSelectDeviceBinding.deviceCategoryRecyclerview.addOnItemTouchListener(new RecyclerItemClickListener(context, fragmentSelectDeviceBinding.deviceCategoryRecyclerview, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).setDeviceCategoryIndex(position);
                deviceCategoryAdapter.notifyDataSetChanged();
                sortItemList(position);
                Log.e("TAG", "item list");
            }
            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));

        fragmentSelectDeviceBinding.deviceItemRecyclerview.addOnItemTouchListener(new RecyclerItemClickListener(context, fragmentSelectDeviceBinding.deviceItemRecyclerview, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                DemoAddDeviceInfoFragment fragment = new DemoAddDeviceInfoFragment();

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mainFrameLayout, fragment, "DemoAddDeviceInfoFragment")
                        .addToBackStack(null).commit();

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));



    }

    ArrayList<String> categoryNames = new ArrayList<>();
    ArrayList<BluetoothDeviceModel> allDevices = new ArrayList<>();

    private void sortCategoryList(){
        categoryNames.clear();
        for (BluetoothDeviceModel model: allDevices){
            if (!categoryNames.contains(model.getCategory())){
                categoryNames.add(model.getCategory());
            }
        }
    }

    ArrayList<BluetoothDeviceModel> itemList = new ArrayList<>();

    private void sortItemList(int position){
        try {
            itemList.clear();
            String category = categoryNames.get(position);
            for (BluetoothDeviceModel model: allDevices){
                if (category.equals(model.getCategory())){
                    itemList.add(model);
                }
            }
            deviceListAdapter.notifyDataSetChanged();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).setDeviceCategoryIndex(0);
        super.onDestroy();
    }
}

