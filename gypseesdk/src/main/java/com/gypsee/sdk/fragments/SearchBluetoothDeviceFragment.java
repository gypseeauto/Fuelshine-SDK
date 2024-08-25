package com.gypsee.sdk.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import com.gypsee.sdk.Adapters.BluetoothListAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.databinding.FragmentSearchBluetoothDeviceBinding;
import com.gypsee.sdk.helpers.BluetoothHelperClass;
import com.gypsee.sdk.jsonParser.ResponseFromServer;
import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;
import com.gypsee.sdk.utils.RecyclerItemClickListener;
import com.gypsee.sdk.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchBluetoothDeviceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchBluetoothDeviceFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private boolean mParam2;

    public SearchBluetoothDeviceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchBluetoothDeviceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchBluetoothDeviceFragment newInstance(String param1, boolean param2) {
        SearchBluetoothDeviceFragment fragment = new SearchBluetoothDeviceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putBoolean(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    Context context;
    FragmentSearchBluetoothDeviceBinding fragmentSearchBluetoothDeviceBinding;
    BluetoothAdapter bluetoothAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getBoolean(ARG_PARAM2, true);
        }
    }
    
    private String TAG = SearchBluetoothDeviceFragment.class.getName();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    BluetoothListAdapter pairedListAdapter;
    ArrayList<BluetoothDevice> pairedList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentSearchBluetoothDeviceBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_bluetooth_device, container, false);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //checkBluetoothEnabled();
        bluetoothAdapter.enable();

        registerBluetoothReceiver();
        initToolbar();

        pairedListAdapter = new BluetoothListAdapter(pairedList);
        fragmentSearchBluetoothDeviceBinding.pairedListRecyclerview.setLayoutManager(new LinearLayoutManager(context));
        fragmentSearchBluetoothDeviceBinding.pairedListRecyclerview.setAdapter(pairedListAdapter);




        bluetoothListAdapter = new BluetoothListAdapter(devicesList);
        fragmentSearchBluetoothDeviceBinding.bluetoothListRecyclerview.setLayoutManager(new LinearLayoutManager(context));
        fragmentSearchBluetoothDeviceBinding.bluetoothListRecyclerview.setAdapter(bluetoothListAdapter);
        setOnclickListeners();
        startBluetoothScanning();

        initPairedList();


        return fragmentSearchBluetoothDeviceBinding.getRoot();
    }

    private BluetoothListAdapter bluetoothListAdapter;

    private void checkBluetoothEnabled(){
        if (!bluetoothAdapter.isEnabled()){
            bluetoothAdapter.enable();
        }
    }

    @Override
    public void onDestroy() {
        context.unregisterReceiver(receiver);
        devicesList.clear();
        super.onDestroy();
    }

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
        fragmentSearchBluetoothDeviceBinding.toolBarLayout.refreshIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartBluetoothScanning();
            }
        });

        fragmentSearchBluetoothDeviceBinding.bluetoothListRecyclerview.addOnItemTouchListener(new RecyclerItemClickListener(context, fragmentSearchBluetoothDeviceBinding.bluetoothListRecyclerview
                , new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!devicesList.isEmpty()){
                    connectToDevice = devicesList.get(position);
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
                connectToDevice = pairedList.get(position);
                BluetoothHelperClass.showOkCancelDialog(context, getLayoutInflater(), "Alert!", "Are you Pairing Car based Bluetooth device?", responseFromServer, 2);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

    }

    private BluetoothDevice connectToDevice;
    private String customDeviceName = "";

    private ResponseFromServer responseFromServer = new ResponseFromServer() {
        @Override
        public void responseFromServer(String Response, String className, int value) {
            switch (value){
                case 1:
                    if (connectToDevice != null){
                        if (mParam2){
                            BluetoothHelperClass.showInputDialog(context, getLayoutInflater(), "Enter Name", "Please enter name for custom device", responseFromServer, 3);
                        } else {
                            connectToDevice.createBond();
                        }

                    }
                    break;

                case -1:
                    connectToDevice = null;
                    customDeviceName = "";
                    break;

                case 2:
                    if (connectToDevice != null){
                        if (mParam2){
                            BluetoothHelperClass.showInputDialog(context, getLayoutInflater(), "Enter Name", "Please enter name for custom device", responseFromServer, 4);
                        } else {
                            isNowConnected = false;
                            callServer(getString(R.string.registerNewDevice), "Register Bluetooth Device", 1);
                        }

                    }
                    break;

                case 3:
                    if (connectToDevice != null){
                        customDeviceName = Response;
                        connectToDevice.createBond();
                    }
                    break;

                case 4:
                    if (connectToDevice != null){
                        isNowConnected = false;
                        customDeviceName = Response;
                        callServer(getString(R.string.registerNewDevice), "Register Bluetooth Device", 1);
                    }
                    break;

            }
        }
    };







    private void initPairedList(){
        pairedList.clear();
        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        pairedList.addAll(pairedDevices);
        pairedListAdapter.notifyDataSetChanged();

        if (pairedList.size() > 0){
            showView(fragmentSearchBluetoothDeviceBinding.listLayout);
        }


    }



    private void registerBluetoothReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        context.registerReceiver(receiver, filter);
    }

    ArrayList<BluetoothDevice> devicesList = new ArrayList<>();

    private void startBluetoothScanning(){
        devicesList.clear();
        bluetoothAdapter.startDiscovery();
        //showView(fragmentSearchBluetoothDeviceBinding.searchLayout);

    }

    private void restartBluetoothScanning(){
        //checkBluetoothEnabled();
        bluetoothAdapter.enable();
        showView(fragmentSearchBluetoothDeviceBinding.searchLayout);
        devicesList.clear();
        if (bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
        }
        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                bluetoothAdapter.startDiscovery();
                initPairedList();
            }
        }, 1000);

    }



    private void showView(View view){
        fragmentSearchBluetoothDeviceBinding.searchLayout.setVisibility(View.GONE);
        fragmentSearchBluetoothDeviceBinding.listLayout.setVisibility(View.GONE);
        //fragmentSearchBluetoothDeviceBinding.noDeviceFound.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
    }






    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())){
                Log.e(TAG, "ACTION FOUND");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                 showView(fragmentSearchBluetoothDeviceBinding.listLayout);
                if (!checkIfDeviceAlreadyAdded(device) && device.getName() != null){
                    devicesList.add(device);
                    bluetoothListAdapter.notifyDataSetChanged();
                }
            }
            if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)){
                Log.e(TAG, "discovery started");
            }
            if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
                Log.e(TAG, "Discovery finished");
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                /*if (devicesList.isEmpty()){
                    showView(fragmentSearchBluetoothDeviceBinding.noDeviceFound);
                }*/
            }
            if (intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice bondedDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (bondedDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    isNowConnected = true;
                    callServer(getString(R.string.registerNewDevice), "Register Bluetooth Device", 1);
                }
            }
        }
    };


    private boolean isNowConnected = false;

    private void callServer(String url, final String purpose, final int value) {

        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);
        Call<ResponseBody> call;
        JsonObject jsonObject = new JsonObject();

        switch (value) {

            default:
            case 1:
                //register device
                if (connectToDevice != null && mParam1 != null){

                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date());

                    jsonObject.addProperty("bluetoothName", (mParam2 && !customDeviceName.equals("")) ? customDeviceName : connectToDevice.getName());
                    jsonObject.addProperty("bluetoothProfilesSupported", getBluetoothClassName(connectToDevice)); //need more research
                    jsonObject.addProperty("bluetoothType", getBluetoothClassName(connectToDevice));
                    jsonObject.addProperty("deviceId", mParam1);
                    jsonObject.addProperty("lastConnectedTime", timeStamp);
                    jsonObject.addProperty("macAddress", connectToDevice.getAddress());
                    jsonObject.addProperty("nowConnected", isNowConnected);
                    if (mParam2){
                        jsonObject.addProperty("deviceName", (!customDeviceName.equals("")) ? customDeviceName : connectToDevice.getName());
                    }
                }
                call = apiService.uploadObd(new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getUser().getUserAccessToken(), jsonObject);
                break;

        }

        Log.e(TAG, purpose + " Input :" + jsonObject);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {

                    if (response.isSuccessful()) {
                        Log.e(TAG, "Response is success");

                        //If the response is null, we will return immediately.
                        ResponseBody responseBody = response.body();
                        if (responseBody == null)
                            return;
                        String responseStr = responseBody.string();
                        Log.e(TAG, purpose + " Response :" + responseStr);

                        switch (value) {

                            default:
                            case 1:
                                Toast.makeText(context, "Device added", Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                                break;

                        }
                    } else {
                        Log.e(TAG, purpose + " Response is not succesfull");

                        String errResponse = response.errorBody().string();
                        Log.e("Error code 400", errResponse);
                        Log.e(TAG, purpose + "Response is : " + errResponse);
                        int responseCode = response.code();
                        if (responseCode == 401 || responseCode == 403) {
                            //include logout functionality
                            Utils.clearAllData(context);
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "error here since request failed");
                if (t.getMessage().contains("Unable to resolve host")) {
                    Toast.makeText(context, "Please Check your internet connection", Toast.LENGTH_LONG).show();
                }
            }
        });


    }


    private String getBluetoothClassName(BluetoothDevice device){
        int i = device.getBluetoothClass().getMajorDeviceClass();
        String deviceClass = "";
        switch (i){
            case 1024:
                deviceClass = "AUDIO_VIDEO";
                break;

            case 256:
                deviceClass = "COMPUTER";
                break;

            case 2304:
                deviceClass = "HEALTH";
                break;

            case 1536:
                deviceClass = "IMAGING";
                break;

            case 0:
                deviceClass = "MISC";
                break;

            case 768:
                deviceClass = "NETWORKING";
                break;

            case 1280:
                deviceClass = "PERIPHERAL";
                break;

            case 512:
                deviceClass = "PHONE";
                break;

            case 2048:
                deviceClass = "TOY";
                break;

            case 7936:
                deviceClass = "UNCATEGORIZED";
                break;

            case 1792:
                deviceClass = "WEARABLE";
                break;

        }
        return deviceClass;
    }




    private boolean checkIfDeviceAlreadyAdded(BluetoothDevice device){
        for (BluetoothDevice object: devicesList){
            if (object.getAddress().equals(device.getAddress())){
                return true;
            }
        }
        return false;
    }




}