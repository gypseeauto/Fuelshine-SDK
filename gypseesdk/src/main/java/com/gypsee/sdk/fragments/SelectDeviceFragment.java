package com.gypsee.sdk.fragments;

import static com.gypsee.sdk.utils.Utils.getLine;

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
import android.widget.TextView;

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
import com.gypsee.sdk.database.DatabaseHelper;
import com.gypsee.sdk.databinding.FragmentSelectDeviceBinding;
import com.gypsee.sdk.firebase.FirebaseLogEvents;
import com.gypsee.sdk.models.BluetoothDeviceModel;
import com.gypsee.sdk.utils.RecyclerItemClickListener;

public class SelectDeviceFragment extends Fragment {

    String TAG = getClass().getSimpleName();

    public SelectDeviceFragment(){}

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

        allDevices = new DatabaseHelper(context).fetchCategoryDevices();

        initToolbar();
        initStaticViews();
        sortCategoryList();
        initViews();


        return fragmentSelectDeviceBinding.getRoot();
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
                Log.e(TAG, "item list");
                String deviceCat = ((TextView)view.findViewById(R.id.category_text)).getText().toString();
                Log.e(TAG, deviceCat);
                FirebaseLogEvents.firebaseLogEvent("adding_"+deviceCat.replace(" ","_"),context);
                for (BluetoothDeviceModel model: itemList){
                    Log.e(TAG+getLine(), model.getBluetoothName());
                    Log.e(TAG+getLine(), model.getImageUrl());
                    Log.e(TAG+getLine(), model.getInformationModel().getImageUrl());
                    Log.e(TAG+getLine(), "_________________________________________");
                }
            }
            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));

        fragmentSelectDeviceBinding.deviceItemRecyclerview.addOnItemTouchListener(new RecyclerItemClickListener(context, fragmentSelectDeviceBinding.deviceItemRecyclerview, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String instructionImg = itemList.get(position).getInformationModel().getImageUrl();
                ArrayList<String> instructionsArray = itemList.get(position).getInformationModel().getInstructions();
                Log.e(TAG+getLine(), instructionImg + "\n"+itemList.get(position).getInformationModel().getName());
                if (!instructionImg.equals("") && instructionImg != null && instructionsArray.size() > 0) {
                    AddDeviceInfoFragment fragment = AddDeviceInfoFragment.newInstance(itemList.get(position).getId()
                            , itemList.get(position).getInformationModel().getImageUrl()
                            , itemList.get(position).getInformationModel().getInstructions()
                            , (itemList.get(position).getCategory().equals("Custom")));
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.mainFrameLayout, fragment, "AddDeviceInfoFragment")
                            .addToBackStack(null).commit();
                } else {
                    SearchBluetoothDeviceFragment fragment = SearchBluetoothDeviceFragment.newInstance(itemList.get(position).getId(), (itemList.get(position).getCategory().equals("Custom")));
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.mainFrameLayout, fragment, "SearchBluetoothDeviceFragment")
                            .addToBackStack(null)
                            .commit();
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));



    }

    ArrayList<String> categoryNames = new ArrayList<>();
    ArrayList<BluetoothDeviceModel> allDevices;

    private void sortCategoryList(){
        categoryNames.clear();
        for (BluetoothDeviceModel model: allDevices){
            if (!categoryNames.contains(model.getCategory())){
                categoryNames.add(model.getCategory());
            }
        }
    }

    ArrayList<BluetoothDeviceModel> itemList = new ArrayList<>();

    @Override
    public void onResume() {
        super.onResume();
        sortItemList(new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getDeviceCategoryIndex());
    }

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
