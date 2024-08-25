package com.gypsee.sdk.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import com.gypsee.sdk.Adapters.SafetyInfoAdapter;
import com.gypsee.sdk.Adapters.SafetySOSAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.Emergencyactivity;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.databinding.FragmentSafetySinglePageBinding;
import com.gypsee.sdk.helpers.BluetoothHelperClass;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;
import com.gypsee.sdk.services.BackgroundLocationService;
import com.gypsee.sdk.utils.GpsUtils;
import com.gypsee.sdk.utils.RecyclerItemClickListener;
import com.gypsee.sdk.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SafetySinglePageFragment extends Fragment {

    public static String ARG_POSITION = "ARG_POSITION";

    //LocationManager locationManager;

    public static SafetySinglePageFragment newInstance(int position) {

        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);

        SafetySinglePageFragment fragment = new SafetySinglePageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private int position = 0;
    private FragmentSafetySinglePageBinding fragmentSafetySinglePageBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
        }
    }

    Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentSafetySinglePageBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_safety_single_page, container, false);

        //locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);


        fragmentSafetySinglePageBinding.sosRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        if (position == 0) {
            fragmentSafetySinglePageBinding.sosRecyclerView.setAdapter(new SafetySOSAdapter());
            sosButtonListener();
        } else {
            fragmentSafetySinglePageBinding.sosRecyclerView.setAdapter(new SafetyInfoAdapter());
            setupInfoListOnTouchListener();
        }


        return fragmentSafetySinglePageBinding.getRoot();
    }

    private String sosType = "Accident";

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void sosButtonListener() {

        Log.e(TAG, "sos listener");

        fragmentSafetySinglePageBinding.sosRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, fragmentSafetySinglePageBinding.sosRecyclerView
                , new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (position == 2){

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.readyassist.in/")));

                } else {
                    BluetoothHelperClass.showOkCancelDialog(context, getLayoutInflater(), "Confirmation", "Are you sure want to use SOS?", (Response, className, value) -> {
                        if (value == 1) {
                            IntentFilter filter = new IntentFilter();
                            filter.addAction("LocationReceiver");
                            LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);

                            switch (position) {
                                case 0:
                                    sosType = "Accident";
                                    break;
                                case 3:
                                    sosType = "Road Rage";
                                    break;
                                case 1:
                                    sosType = "Others";
                                    break;
                            }

                            startBackGroundLocationService();
                        }
                    }, 1);

                }



                //SA version-----------------------------------
              /*  BluetoothHelperClass.showCustomOptionDialog(context,
                        getLayoutInflater(),
                        "CALL",
                        "Ready Assist",
                        "SOS",
                        "Choose SOS option",
                        (Response, className, value) -> {

                    if(value == 1){

                        Uri u = Uri.parse("tel:0105921773");
                        Intent i = new Intent(Intent.ACTION_DIAL, u);

                        try
                        {
                            startActivity(i);
                        }
                        catch (SecurityException s)
                        {
                            Toast.makeText(context, "An error occurred", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.readyassist.in/")));
                    }

                        }
                        );
*/
                //SA version end---------------------------


                }


            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("LocationReceiver")) {
                Log.e(TAG, "onreceive new");
                Location location = intent.getParcelableExtra("Location");
                if (!(new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getIsTripRunning())) {
                    stopBackgroundLocationService();
                }

                currentLocation = location;
                LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);

                callServer(getString(R.string.sosTrigger), "SOS Trigger", 0);

            }
        }
    };

    private void startBackGroundLocationService() {
        //starting when app is started, and when activity is captured

        GpsUtils gpsUtils = new GpsUtils(context);
        gpsUtils.turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                if (isGPSEnable){
                    //prepare service
                    Log.e(TAG, "Starting BackgroundLocation Service");
                    /*final */
                    Intent intent = new Intent(context, BackgroundLocationService.class);
                    context.startService(intent);
                    //bindService(intent, locationServiceConnection, Context.BIND_AUTO_CREATE);
                }
            }
        });

    }

    private void stopBackgroundLocationService(){
        //stopping at every trip end

        Log.e(TAG, "stopbackgroundLocationService called");
        /*if (gpsService != null){
            //unbindService(locationServiceConnection);
            //gpsService.unbindService(locationServiceConnection);
            Log.e(TAG, "gps location not null");
            gpsService.stopSelf();
        }*/

        Intent intent = new Intent(context, BackgroundLocationService.class);
        context.stopService(intent);

    }


    private void setupInfoListOnTouchListener(){
        fragmentSafetySinglePageBinding.sosRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, fragmentSafetySinglePageBinding.sosRecyclerView
                , new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                switch (position){

                    case 0:
                        Fragment fragmentOne = SosInfoDetailFragment.newInstance("MEMBERS", "");
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.mainFrameLayout, fragmentOne, SosInfoDetailFragment.class.getSimpleName())
                                .addToBackStack(SafetySinglePageFragment.class.getSimpleName())
                                .commit();
                        break;

                    case 1:
                        Fragment fragmentTwo = SosInfoDetailFragment.newInstance("HOW_IT_WORKS", "");
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.mainFrameLayout, fragmentTwo, SosInfoDetailFragment.class.getSimpleName())
                                .addToBackStack(SafetySinglePageFragment.class.getSimpleName())
                                .commit();
                        break;

                    case 2:
                        startActivity(new Intent(context, Emergencyactivity.class));
                        break;

                    case 3:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.readyassist.in/")));
                        break;

                }

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    private Location currentLocation;

    /*@Override
    public void onLocationChanged(@NonNull Location location) {

        if (location != null){
            locationManager.removeUpdates(this::onLocationChanged);
            currentLocation = location;
            Log.e(TAG, "Location: Lat:" + location.getLatitude() + " , Long: " + location.getLongitude());
            callServer(getString(R.string.sosTrigger), "SOS Trigger", 0);
        }
    }*/

    private String TAG = SafetySinglePageFragment.class.getSimpleName();

    private void callServer(String url, final String purpose, final int value) {

        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);
        Call<ResponseBody> call;
        User user = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getUser();
        JsonObject jsonObject = new JsonObject();

        switch (value) {
            case 0:

                if (currentLocation != null) {
                    jsonObject.addProperty("latitude", String.valueOf(currentLocation.getLatitude()));
                    jsonObject.addProperty("longitude", String.valueOf(currentLocation.getLongitude()));
                    jsonObject.addProperty("typeOfSOS", sosType);
                }

                call = apiService.uploadObd(user.getUserAccessToken(), jsonObject);
                break;


            default:

                call = apiService.uploadObd(user.getUserAccessToken(), jsonObject);
                break;
        }

        Log.e(TAG, purpose + " Input :" + jsonObject.toString());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.e(TAG, "Response is success");
                        //If the response is null, we will return immediately.
                        ResponseBody responseBody = response.body();
                        if (responseBody == null) {
                            Toast.makeText(context, "AN unknown error occured. Kindly contact Gypsee Team.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String responseStr = responseBody.string();
                        Log.e(TAG, purpose + " Response :" + responseStr);

                        switch (value) {
                            case 0:
                                Toast.makeText(context, "SOS sent", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    } else {
                        Log.e(TAG, purpose + " Response is not Successful");
                        ResponseBody responseBody = response.errorBody();
                        String errResponse = responseBody.string();
                        Log.e(TAG, purpose + " Error Response is : " + errResponse);
                        int responseCode = response.code();

                        if (responseCode == 401 || responseCode == 403) {
                            //include logout functionality
                            Utils.clearAllData(context);
                        } else {

                            JSONObject jObjError = new JSONObject(errResponse);
                            Toast.makeText(context, jObjError.getString("message"), Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(context, "Please Check your internet connection", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }





}
