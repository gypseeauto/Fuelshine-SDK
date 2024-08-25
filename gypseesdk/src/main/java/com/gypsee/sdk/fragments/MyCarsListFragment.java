package com.gypsee.sdk.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import com.gypsee.sdk.Adapters.CarListRecyclerViewAdapter;
import com.gypsee.sdk.R;

import com.gypsee.sdk.activities.GypseeMainActivity;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.database.DatabaseHelper;

import com.gypsee.sdk.jsonParser.ResponseFromServer;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.models.Vehiclemodel;
import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;
import com.gypsee.sdk.services.TripBackGroundService;
import com.gypsee.sdk.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyCarsListFragment extends Fragment implements View.OnClickListener {

    // TODO: Customize parameters
    private RecyclerView recyclerview;
    private ArrayList<Vehiclemodel> vehiclemodelArrayList;
    private CarListRecyclerViewAdapter carListRecyclerViewAdapter;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MyCarsListFragment() {
    }

    Vehiclemodel connectedVehiclemodel;

    boolean connected;
    public static MyCarsListFragment newInstance(Vehiclemodel vehiclemodel) {

        Bundle args = new Bundle();
        args.putParcelable(Vehiclemodel.class.getSimpleName(), vehiclemodel);
        MyCarsListFragment fragment = new MyCarsListFragment();
        fragment.setArguments(args);
        return fragment;
    }
//    public static MyCarsListFragment newInstance(Vehiclemodel vehicleModel) {
//        MyCarsListFragment fragment = new MyCarsListFragment();
//        Bundle args = new Bundle();
//        args.putParcelable("vehicleModel", vehicleModel);
//        fragment.setArguments(args);
//        return fragment;
//    }


    @Override
    public void onStop() {
        super.onStop();
        Log.e("MycarsOnstop","MyCarsOnstop");
        ((GypseeMainActivity) requireActivity()).showBottomNav();

    }

    User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {
            connectedVehiclemodel = b.getParcelable(Vehiclemodel.class.getSimpleName());
        }

    }

    private Context context;
    private MyPreferenece myPreferenece;
    private boolean isDriveMode;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_mycars, container, false);
        context = view.getContext();

        user = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getUser();
        ((GypseeMainActivity) requireActivity()).hideBottomNav();
        myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);

        isDriveMode = myPreferenece.getConnectedToCar();

        Log.e("ConnectedTocar", String.valueOf(isDriveMode));



        initViews(view);


        registerBroadcastReceivers();
        fetchVehiclesFromDb();
        checkCarListCount();


        return view;
    }

    private void callBackgroundServicetoFetchCars() {
        Intent intent = new Intent(context, TripBackGroundService.class);
        intent.putExtra(TripBackGroundService.PURPOSE, "Get UserVehicles");
        context.startService(intent);
        showProgressView(true);
    }

    private void checkCarListCount(){
        if (!user.isInTrainingMode()) {
            if (vehiclemodelArrayList.size() < 2) {
                addIcon.setVisibility(View.VISIBLE);
            } else {
                addIcon.setVisibility(View.INVISIBLE);
            }
        } else {
            addIcon.setVisibility(View.GONE);
        }
    }


    private void fetchVehiclesFromDb() {
        vehiclemodelArrayList = new DatabaseHelper(context).fetchAllVehicles();
        Collections.reverse(vehiclemodelArrayList);

        // Check if the vehicle is connected to OBD II.
        // If connected, we will not allow the user to edi the car details until the trip is ended.
        if (connectedVehiclemodel != null)
            for (Vehiclemodel vehcleModel : vehiclemodelArrayList) {
                if (vehcleModel.getUserVehicleId().equals(connectedVehiclemodel.getUserVehicleId())) {
                    vehcleModel.setObdConnected(true);
                    vehiclemodelArrayList.set(vehiclemodelArrayList.indexOf(vehcleModel), vehcleModel);
                }
            }
//       boolean bool = vehiclemodelArrayList.get(0).isObdConnected();
//        Log.e("COnnectedCar", String.valueOf(bool));
        carListRecyclerViewAdapter = new CarListRecyclerViewAdapter(vehiclemodelArrayList, context, true, responseFromServer,isDriveMode);
        recyclerview.setAdapter(carListRecyclerViewAdapter);
        Log.e(TAG, "VehicleData size: " + vehiclemodelArrayList.size());
//        Log.e(TAG, "VehicleData ServiceDate: " + vehiclemodelArrayList.get(0).getServicereminderduedate());



        if (vehiclemodelArrayList.size() == 0) {
            emptycarsTv.setVisibility(View.VISIBLE);
            recyclerview.setVisibility(View.GONE);
        } else {
            emptycarsTv.setVisibility(View.GONE);
            recyclerview.setVisibility(View.VISIBLE);
        }
    }


//    private ImageView refreshIcon;
    private LinearLayout addIcon;
    private TextView emptycarsTv;
    private ProgressBar progressBar;
    private ConstraintLayout backBtn;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "MyPrefs";
    private static final String CONNECTED_TO_CAR = "connectedToCar";
    private void initViews(View view) {
//        Toolbar toolbar = view.findViewById(R.id.toolBarLayout);

        backBtn = view.findViewById(R.id.back_button_layout);
//        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);
//        TextView toolbarTitle = (TextView) view.findViewById(R.id.toolBarTitle);
        emptycarsTv = (TextView) view.findViewById(R.id.emptycarsTv);
        progressBar = view.findViewById(R.id.progressView);
        addIcon = view.findViewById(R.id.rightSideIcon);
//        refreshIcon = view.findViewById(R.id.refreshIcon);
        addIcon.setVisibility(View.VISIBLE);
//        refreshIcon.setVisibility(View.VISIBLE);
//        addIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_icon));
        addIcon.setOnClickListener(this);
//        refreshIcon.setOnClickListener(this);

        TextView emptycarsTv = view.findViewById(R.id.emptycarsTv);

        String s = getActivity().getIntent().getStringExtra("Reason") == null ? "Click on + icon to set automated reminders." : "Click on + icon to add your beloved car.";
        SpannableString ss1 = new SpannableString(s);
        ss1.setSpan(new RelativeSizeSpan(2f), 8, 10, 0); // set size
        // ss1.setSpan(new ForegroundColorSpan(context.getColor(R.color.theme_blue)), 0, 5, 0);// set color
        emptycarsTv.setText(ss1);
        emptycarsTv.setOnClickListener(this);

        sharedPreferences = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        boolean connctedToCar = sharedPreferences.getBoolean(CONNECTED_TO_CAR, false); // Default to false

        updateConnctedValue(connctedToCar);

//        toolbarTitle.setText(getResources().getString(R.string.my_cars));
//        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
//                goBack();
            }
        });

        recyclerview = view.findViewById(R.id.carListRecyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(context));
    }

    private void goBack() {
        ((AppCompatActivity) context).finish();

    }

    private String TAG = MyCarsListFragment.class.getSimpleName();


    private void showProgressView(boolean isShowProgress) {
        if (isShowProgress) {
            progressBar.setVisibility(View.VISIBLE);
            emptycarsTv.setVisibility(View.GONE);
            recyclerview.setVisibility(View.GONE);
//            refreshIcon.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            emptycarsTv.setVisibility(View.VISIBLE);
            recyclerview.setVisibility(View.VISIBLE);
//            refreshIcon.setVisibility(View.VISIBLE);

        }
    }

    public void updateConnctedValue(boolean value) {

        connected = value;

//        boolean allSwitchesOff = !alertsBinding.speedSwitch.isChecked() && !alertsBinding.brakeSwitch.isChecked() && !alertsBinding.accelarationSwitch.isChecked() && !alertsBinding.textAndDriveSwitch.isChecked();

//        if (mute){
//            alertsBinding.speedSwitch.setChecked(false);
//            alertsBinding.brakeSwitch.setChecked(false);
//            alertsBinding.accelarationSwitch.setChecked(false);
//            alertsBinding.textAndDriveSwitch.setChecked(false);
//        }

//        if (allSwitchesOff) {
//            alertsBinding.speedSwitch.setChecked(!mute);
//            alertsBinding.brakeSwitch.setChecked(!mute);
//            alertsBinding.accelarationSwitch.setChecked(!mute);
//            alertsBinding.textAndDriveSwitch.setChecked(!mute);
//        }
    }


    private void registerBroadcastReceivers() {

        //THis is to receive the obd commands from the device . Regarding the vehcile

        IntentFilter filter = new IntentFilter();
        filter.addAction("CarsList");
        filter.addAction("ShowProgress");
        filter.addAction("RefreshList");

        //this is for receiving the notification count etc
        LocalBroadcastManager.getInstance(context).registerReceiver(
                notificationCountBroadcastReceiver, filter);
    }

    private BroadcastReceiver notificationCountBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("CarsList")) {
                showProgressView(false);
                fetchVehiclesFromDb();
                checkCarListCount();

            } else if (intent.getAction().equals("ShowProgress")) {
                fetchUserVehicles();
            }
            if (intent.getAction().equals("RefreshList")){
                callBackgroundServicetoFetchCars();
            }
        }
    };

    private void fetchUserVehicles() {
        Intent intent = new Intent(context, TripBackGroundService.class);
        intent.putExtra(TripBackGroundService.PURPOSE, "Get UserVehicles");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(intent);
            Log.e(TAG, "fetchUserVehicles: from trip background service" );
        } else {
            requireContext().startService(intent);
        }
        showProgressView(true);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(notificationCountBroadcastReceiver);

    }

    int deleteCarPosition;
    ResponseFromServer responseFromServer = new ResponseFromServer() {
        @Override
        public void responseFromServer(String Response, String className, int value) {

            switch (value) {
                //Delete car.
                case 0:

                    deleteCarPosition = Integer.parseInt(Response);
                    vehiclemodelArrayList.get(deleteCarPosition).setDeleting(true);
                    carListRecyclerViewAdapter.notifyItemChanged(deleteCarPosition);
                    callServer(getString(R.string.deleteCarUrl).replace("vehicleId", vehiclemodelArrayList.get(deleteCarPosition).getUserVehicleId()), "Delete car ", 0);
                    break;

                case 1:
                    break;
            }
        }
    };

    private void callServer(String url, String purpose, final int value) {

        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);

        Call<ResponseBody> call;
        JsonObject jsonInput = new JsonObject();

        MyPreferenece myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);

        User user = myPreferenece.getUser();

        switch (value) {
            case 0:
                Log.e(TAG, purpose + " Input is : " + jsonInput.toString());
                call = apiService.deleteCar(user.getUserAccessToken());
                break;

            default:
                call = apiService.getDocTypes(user.getUserAccessToken(), true);
                break;
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // progressLayout.setVisibility(View.GONE);
                try {
                    if (response.isSuccessful()) {
                        Log.e(TAG, "Response is success");
                        String responseStr = response.body().string();
                        Log.e(TAG, "Response :" + responseStr);

                        switch (value) {
                            case 0:
                                parseDeleteCar(responseStr);
                                break;
                        }

                    } else {
                        Log.e(TAG, "Response is not succesfull");
                        String errorBody = response.errorBody().string();

                        int responseCode = response.code();
                        Log.e(TAG, " Response :" + responseCode + errorBody);

                        if (responseCode == 401 || responseCode == 403) {
                            //include logout functionality
                            Utils.clearAllData(context);
                            return;
                        }

                        if (response.errorBody() == null) {
                        } else {
                            JSONObject jsonObject = new JSONObject(errorBody);
                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            Log.e("Error code " + response, "" + "Response is not empty");
                        }

                        switch (value) {
                            case 0:
                                vehiclemodelArrayList.get(deleteCarPosition).setDeleting(false);
                                carListRecyclerViewAdapter.notifyItemChanged(deleteCarPosition);
                                break;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

               /* if (response.body() != null) {
                    try {
                       // String responseString = response.body().string();
                     Log.e(TAG,"Response : "+ responseString);

                        progressBar.setVisibility(View.GONE);
                        loginBtn.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {

                    Toast.makeText(LoginActivity.this,"Please try again",Toast.LENGTH_SHORT).show();
                }*/

            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //Logger.ErrorLog(TAG, "error here since request failed");

                // progressLayout.setVisibility(View.GONE);


                if (t.getMessage().contains("Unable to resolve host")) {
                    Toast.makeText(context, "Please Check your internet connection", Toast.LENGTH_LONG).show();
                }

                if (call.isCanceled()) {

                } else {

                }

                switch (value) {
                    case 0:
                        vehiclemodelArrayList.get(deleteCarPosition).setDeleting(false);
                        carListRecyclerViewAdapter.notifyItemChanged(deleteCarPosition);
                        break;
                }
            }
        });
    }

    private void parseDeleteCar(String responseStr) {

        new DatabaseHelper(context).deleteVehicle(vehiclemodelArrayList.get(deleteCarPosition).getUserVehicleId());
        vehiclemodelArrayList.remove(deleteCarPosition);
        carListRecyclerViewAdapter.notifyItemRemoved(deleteCarPosition);
        checkCarListCount();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.refreshIcon) {

            fetchUserVehicles();


        } else {
            ZoopAddCarDialogFragment zoopAddCarDialogFragment = new ZoopAddCarDialogFragment();
            zoopAddCarDialogFragment.show(getChildFragmentManager(), ZoopAddCarDialogFragment.class.getSimpleName());
        }

        // openAddCar();
    }

    private void openAddCar() {
        AddVehicleFragment addVehicleFragment = AddVehicleFragment.newInstance(null);
        ((AppCompatActivity) context).getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.mainFrameLayout, addVehicleFragment, AddVehicleFragment.class.getSimpleName())
                .addToBackStack(AddVehicleFragment.class.getSimpleName())
                .commit();
    }
}
