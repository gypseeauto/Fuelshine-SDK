package com.gypsee.sdk.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.gypsee.sdk.Adapters.MyDevicesAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.database.DatabaseHelper;
import com.gypsee.sdk.databinding.FragmentMyDevicesBinding;
import com.gypsee.sdk.models.BluetoothDeviceModel;
import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;
import com.gypsee.sdk.utils.RecyclerItemClickListener;
import com.gypsee.sdk.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyDevicesFragment extends Fragment {

    public MyDevicesFragment(){}
    FragmentMyDevicesBinding fragmentMyDevicesBinding;

    ArrayList<BluetoothDeviceModel> deviceList = new ArrayList<>();

    private String TAG = MyDevicesFragment.class.getSimpleName();

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

        //deviceList = new DatabaseHelper(context).fetchRegisteredDevices();

        myDevicesAdapter = new MyDevicesAdapter(deviceList);

        initToolbar();
        registerBroadcastReceiver();
        initViews();


        return fragmentMyDevicesBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        callServer(getString(R.string.getUserRegisteredDevices).replace("userId", new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getUser().getUserId()), "Fetch Registered Devices", 1);
    }

    private void initViews(){

        deviceList.clear();
        deviceList.addAll(new DatabaseHelper(context).fetchRegisteredDevices());
        //deviceList = getConnectedRegisteredDevices(deviceList);
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
                    SelectDeviceFragment selectDeviceFragment = new SelectDeviceFragment();
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.mainFrameLayout, selectDeviceFragment, "SelectDeviceFragment")
                            .addToBackStack(null)
                            .commit();
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));

    }

    /*private void updateDevicesDatabase(){
        DatabaseHelper helper = new DatabaseHelper(context);
        helper.deleteTable(DatabaseHelper.REGISTERED_DEVICE_TABLE);
        helper.insertRegisteredDevices(savedList);
        refreshRegisteredDevices();
    }*/


    private void registerBroadcastReceiver(){
        IntentFilter newIntent = new IntentFilter();
        newIntent.addAction("BluetoothConnectionChange");
        newIntent.addAction("BluetoothDisconnected");
        LocalBroadcastManager.getInstance(context).registerReceiver(bluetoothStatus, newIntent);
    }


    private BroadcastReceiver bluetoothStatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("BluetoothConnectionChange") || intent.getAction().equals("BluetoothDisconnected")){
                new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initViews();
                    }
                }, 1000);
            }
        }
    };

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(bluetoothStatus);
        super.onDestroy();
    }

    private ArrayList<BluetoothDeviceModel> getConnectedRegisteredDevices(ArrayList<BluetoothDeviceModel> deviceList){
        ArrayList<BluetoothDevice> connectedDevices = getConnectedDevices();
        HashSet<String> deviceSet = new HashSet<>();

        for (int i=0; i<deviceList.size(); i++){
            for (BluetoothDevice device : connectedDevices){
                if (device.getAddress().equals(deviceList.get(i).getMacAddress())){
                    deviceList.get(i).setNowConnected(true);
                    deviceSet.add(device.getAddress());
                } else if (!deviceSet.contains(deviceList.get(i).getMacAddress())){
                    deviceList.get(i).setNowConnected(false);
                }
            }
        }
        return deviceList;
    }



    private ArrayList<BluetoothDevice> getConnectedDevices(){
        Set<BluetoothDevice> pairedList = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        ArrayList<BluetoothDevice> connectedDevices = new ArrayList<>();
        for (BluetoothDevice device: pairedList){
            if (isConnected(device)){
                connectedDevices.add(device);
            }
        }
        return connectedDevices;
    }


    private boolean isConnected(BluetoothDevice device) {
        try {
            Method m = device.getClass().getMethod("isConnected", (Class[]) null);
            boolean connected = (boolean) m.invoke(device, (Object[]) null);
            return connected;
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return false;
    }





    private void refreshRegisteredDevices(){
        callServer(getString(R.string.updateRegisteredDevices).replace("userId", new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getUser().getUserId()), "Update Registered Devices", 2);
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

    private void parseFetchRegisteredDevices(String response) throws JSONException {
        JSONObject jsonResponse = new JSONObject(response);

        JSONArray userRegisteredDevices = jsonResponse.getJSONArray("userRegisteredDevices");
        ArrayList<BluetoothDeviceModel> deviceModelArrayList = new ArrayList<>();

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        MyPreferenece myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);

        databaseHelper.deleteTable(DatabaseHelper.REGISTERED_DEVICE_TABLE);
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date());

        for (int i=0; i<userRegisteredDevices.length(); i++){
            JSONObject registeredDevice = userRegisteredDevices.getJSONObject(i);
            String deviceMac = registeredDevice.has("macAddress") ? registeredDevice.getString("macAddress") : "";
            deviceModelArrayList.add(new BluetoothDeviceModel(
                    registeredDevice.has("id") ? registeredDevice.getString("id") : "",
                    registeredDevice.has("deviceName") ? registeredDevice.getString("deviceName") : "",
                    "",
                    registeredDevice.has("bluetoothName") ? registeredDevice.getString("bluetoothName") : "",
                    registeredDevice.has("bluetoothType") ? registeredDevice.getString("bluetoothType") : "",
                    registeredDevice.has("bluetoothProfilesSupported") ? registeredDevice.getString("bluetoothProfilesSupported") : "",
                    registeredDevice.has("macAddress") ? registeredDevice.getString("macAddress") : "",
                    registeredDevice.has("imageUrl") ? registeredDevice.getString("imageUrl") : "",
                    (myPreferenece.getConnectedDeviceMac().equals(deviceMac)) ? timeStamp : registeredDevice.has("lastConnectedTime") ? registeredDevice.getString("lastConnectedTime") : "",
                    registeredDevice.has("createdOn") ? registeredDevice.getString("createdOn") : "",
                    registeredDevice.has("lastUpdatedOn") ? registeredDevice.getString("lastUpdatedOn") : "",
                    false,
                    null
            ));
        }
        deviceModelArrayList = getConnectedRegisteredDevices(deviceModelArrayList);
        databaseHelper.insertRegisteredDevices(deviceModelArrayList);
        initViews();
    }


    private void callServer(String url, final String purpose, final int value) {

        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);
        Call<ResponseBody> call;
        JsonObject jsonObject = new JsonObject();

        switch (value) {

            default:
            case 1:
                //fetchRegisteredDevices
                call = apiService.getObdDevice(new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getUser().getUserAccessToken());
                break;

            case 2:
                JsonArray deviceArray = new JsonArray();
                ArrayList<BluetoothDeviceModel> models = new DatabaseHelper(context).fetchRegisteredDevices();
                Log.e(TAG, "models size: " + models.size());
                //String selectedMac = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getConnectedDeviceMac();
                for (BluetoothDeviceModel deviceModel: models){

                    JsonObject object = new JsonObject();
                    object.addProperty("bluetoothName", deviceModel.getDeviceName());
                    object.addProperty("bluetoothType", deviceModel.getBluetoothType());
                    object.addProperty("bluetoothProfilesSupported", deviceModel.getBluetoothProfileSupported());
                    object.addProperty("deviceId", deviceModel.getId());
                    object.addProperty("macAddress", deviceModel.getMacAddress());
                    object.addProperty("nowConnected", deviceModel.isNowConnected());
                    object.addProperty("lastConnectedTime", deviceModel.getLastConnectedTime());

                    /*if (selectedMac.equals(deviceModel.getMacAddress())){
                        object.addProperty("nowConnected", true);
                        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date());
                        object.addProperty("lastConnectedTime", timeStamp);
                    } else {
                        object.addProperty("nowConnected", false);
                        object.addProperty("lastConnectedTime", deviceModel.getLastConnectedTime());
                    }*/
                    deviceArray.add(object);
                }

                Log.e(TAG, purpose + " Input :" + deviceArray);

                call = apiService.updateRegisteredDevices(new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getUser().getUserAccessToken(), deviceArray);
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

                            case 1:
                                parseFetchRegisteredDevices(responseStr);
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







}
