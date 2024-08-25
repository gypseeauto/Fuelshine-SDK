package com.gypsee.sdk.fragments;

import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.ConfigActivity;
import com.gypsee.sdk.activities.SecondActivity;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.database.DatabaseHelper;
import com.gypsee.sdk.databinding.FragmentCarScanBinding;
import com.gypsee.sdk.firebase.FirebaseLogEvents;
import com.gypsee.sdk.helpers.BluetoothHelperClass;
import com.gypsee.sdk.interfaces.BlueToothConnectionInterface;
import com.gypsee.sdk.io.AbstractGatewayService;
import com.gypsee.sdk.io.ObdCommandJob;
import com.gypsee.sdk.io.ObdGatewayService;
import com.gypsee.sdk.jsonParser.ResponseFromServer;
import com.gypsee.sdk.models.Vehiclemodel;
import com.gypsee.sdk.threads.ConnectThread;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CarScanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarScanFragment extends Fragment implements View.OnClickListener {


    MyPreferenece myPreferenece;

    public CarScanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CarScanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CarScanFragment newInstance(String param1, String param2) {
        CarScanFragment fragment = new CarScanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    Context context;
    Vehiclemodel selectedVehicleModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    FragmentCarScanBinding fragmentCarScanBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentCarScanBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_car_scan, container, false);
        initViews();

        initToolbar();
        myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);
        fetchVehicleModel();


        return fragmentCarScanBinding.getRoot();
    }

    private void initToolbar() {

        Toolbar toolbar = fragmentCarScanBinding.toolBarLayout.toolbar;
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);

        fragmentCarScanBinding.toolBarLayout.setTitle("SCAN CAR");
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


    private void fetchVehicleModel() {
        ArrayList<Vehiclemodel> vehiclemodels = new DatabaseHelper(context).fetchAllVehicles();
        if (vehiclemodels.size() != 0) {
            selectedVehicleModel = vehiclemodels.get(0);
        }
    }


    private void initViews() {
        fragmentCarScanBinding.confirmedTxt.setText("A confirmed fault code is defined as the DTC stored when an OBD system has confirmed that a malfunction exists.");
        fragmentCarScanBinding.pendingTxt.setText("A pending fault code is defined as the diagnostic trouble code, stored as a result of initial detection of a malfunction, before the activation of the Malfunction Indicator Lamp(MIL).");
        fragmentCarScanBinding.permanentTxt.setText("The DTCs stored as Permanent can not be cleared with any scan tool. Permanent codes automatically clean themselves after repairs have been made and the related system monitor runs successfully.");

        changeBackgrundColor(fragmentCarScanBinding.confirmedTxt, ContextCompat.getColor(context, R.color.gold_color));
        changeBackgrundColor(fragmentCarScanBinding.pendingTxt, ContextCompat.getColor(context, R.color.colorPrimary));
        changeBackgrundColor(fragmentCarScanBinding.permanentTxt, ContextCompat.getColor(context, R.color.eco_color));


        fragmentCarScanBinding.readTxt.setOnClickListener(this);
        fragmentCarScanBinding.clearTxt.setOnClickListener(this);
        fragmentCarScanBinding.scanButton.setOnClickListener(this);

        String sourceString = "While scanning, it is recommended to keep your car at stationary position. Our scan feature works aptly when car's engine light is ON.";
        fragmentCarScanBinding.carScanTxt.setText(Html.fromHtml(sourceString));
    }

    private void changeBackgrundColor(TextView textView, int color) {
        //Changing the background color
        Drawable background = textView.getBackground();
        if (background instanceof ShapeDrawable) {
            // cast to 'ShapeDrawable'
            Log.e(TAG, "Background ShapeDrawable");
            ShapeDrawable shapeDrawable = (ShapeDrawable) background;
            shapeDrawable.getPaint().setColor(color);
        } else if (background instanceof GradientDrawable) {
            // cast to 'GradientDrawable'
            Log.e(TAG, "Background GradientDrawable");

            GradientDrawable gradientDrawable = (GradientDrawable) background;
            gradientDrawable.setColor(color);
        } else if (background instanceof ColorDrawable) {
            // alpha value may need to be set again after this call
            Log.e(TAG, "Background ColorDrawable");

            ColorDrawable colorDrawable = (ColorDrawable) background;
            colorDrawable.setColor(color);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.scanButton) {
            if (myPreferenece.getIsConnecting()) {
                Toast.makeText(context, "Device is connecting for trip. Please try after some time.", Toast.LENGTH_LONG).show();
            } else if (myPreferenece.getIsTripRunning()) {
                //Dialog
                BluetoothHelperClass.showTripEndDialog(context, getLayoutInflater(), "", "Trip is running in background. If you scan car, trip will end automatically. Do you wish to end the trip?", responseFromServer, 0);

            } else {
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent().setAction("ScanServiceActiviated"));
                fragmentCarScanBinding.scanCardView.setVisibility(View.GONE);
                fragmentCarScanBinding.carScanMainLayout.setVisibility(View.VISIBLE);
            }
            return;
        } else if (id == R.id.readTxt) {
            isScanCar = true;
            isClearCode = false;
            Log.e(TAG, "Clicked on Read");
            FirebaseLogEvents.firebaseLogEvent("scanning_car",context);
        } else if (id == R.id.clearTxt) {//Call clear code implementation
            isScanCar = false;
            isClearCode = true;
        }
        //Calling enable bluetooth after 3 seconds, because if blutooth is connected in homefragment. it will take 3 sec to disconnect.
        scannedResult.clear();
        enableButtons(false);
        registerBroadcastReceivers();
        fragmentCarScanBinding.errorTxt.setVisibility(View.GONE);

        ObjectAnimator.ofInt(fragmentCarScanBinding.horizontalProgressBar, "progress", 20)
                .setDuration(3000)
                .start();
        enableBluetooth();
    }

    private void enableButtons(boolean isEnable) {
        if (isEnable) {
            fragmentCarScanBinding.readTxt.setEnabled(true);
            fragmentCarScanBinding.clearTxt.setEnabled(true);
        } else {
            fragmentCarScanBinding.readTxt.setEnabled(false);
            fragmentCarScanBinding.clearTxt.setEnabled(false);
        }
    }

    private boolean enableBluetooth() {
        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(ConfigActivity.ENABLE_BT_KEY, true).apply();

        if (btAdapter.isEnabled()) {
            performBluetoothOps();
            return true;
        } else {
            btAdapter.enable();

            ObjectAnimator.ofInt(fragmentCarScanBinding.horizontalProgressBar, "progress", 0,50)
                    .setDuration(15000)
                    .start();
            fragmentCarScanBinding.errorTxt.setText("Please wait ... Connecting to bluetooth");
            fragmentCarScanBinding.errorTxt.setVisibility(View.VISIBLE);
            fragmentCarScanBinding.errorTxt.setBackgroundColor(getResources().getColor(R.color.green));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fragmentCarScanBinding.errorTxt.setVisibility(View.GONE);
                    performBluetoothOps();
                }
            }, 15000);
            return false;
        }

    }

    private void performBluetoothOps() {
        /*Intent intent = new Intent("CarScanBluetoothConnecting");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);*/
        Set<BluetoothDevice> pairedDevices = BluetoothHelperClass.getBluetoothDevices();
        for (BluetoothDevice bluetoothDevice : pairedDevices) {

            if (bluetoothDevice.getName().equals("OBDII")) {
                //Connect to bluetooth is here.
                connectToBluetoothDevice(bluetoothDevice);
                return;
            }
        }

        //Stop loading the loading bar if any.
        enableButtons(true);

        BluetoothHelperClass.showDialogToPairObd2Device(context);
    }

    // Here we are connecting the bluetooth on a different thread, because it frezes the entire screen.

    ConnectThread connectThread;

    private void connectToBluetoothDevice(BluetoothDevice bluetoothDevice) {

        if (connectThread != null && connectThread.isAlive()) {
            return;
        }
        // Here will show the animation of long bar.

        ObjectAnimator.ofInt(fragmentCarScanBinding.horizontalProgressBar, "progress", 40)
                .setDuration(5000)
                .start();
        connectThread = new ConnectThread(bluetoothDevice, blueToothConnectionInterface, context);
        connectThread.start();
    }

    BluetoothSocket bluetoothSocket;
    BlueToothConnectionInterface blueToothConnectionInterface = new BlueToothConnectionInterface() {
        @Override
        public void onBluetoothConnected(BluetoothSocket socket) {

            bluetoothSocket = socket;
            doBindService();
            fragmentCarScanBinding.errorTxt.setVisibility(View.GONE);

            //LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("CarScanBluetoothConnected"));

            Log.e(TAG, "Bluetooth is connected");

        }

        @Override
        public void obBluetoothDisconnected() {
            Log.e(TAG, "Failure Starting live data");
            //Stop animation if any
            fragmentCarScanBinding.horizontalProgressBar.setProgress(0);
            showBluetoothDisconnected("Failed to connect to OBDII device. Try again");
            //LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("CarScanBluetoothDisconnected"));
            enableButtons(true);

        }
    };

    private void showBluetoothDisconnected(String message) {
        fragmentCarScanBinding.errorTxt.setText(message);
        fragmentCarScanBinding.errorTxt.setVisibility(View.VISIBLE);
        fragmentCarScanBinding.errorTxt.setBackgroundColor(context.getResources().getColor(R.color.red));
    }

    private void doBindService() {
       /* if (!isServiceBound) {

            // start command execution
            //new Handler().postDelayed(mQueueCommands, 1000);

            //Below code is for when the bluetooth is not connected even after 7 seconds
        }*/
        Log.e(TAG, "Binding OBD service..");
        Intent serviceIntent = new Intent(context, ObdGatewayService.class);
        context.bindService(serviceIntent, serviceConn, Context.BIND_AUTO_CREATE);
    }

    AbstractGatewayService service;
    boolean isScanCar = false, isClearCode = false;
    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            Log.e(TAG, className.toString() + " service is bound");

            ObjectAnimator.ofInt(fragmentCarScanBinding.horizontalProgressBar, "progress", 70)
                    .setDuration(3000)
                    .start();

            service = ((AbstractGatewayService.AbstractGatewayServiceBinder) binder).getService();
            service.setContext(context);

            Log.e(TAG, "isScanCar here: " + isScanCar);

            Log.e(TAG, "Starting service live data");
            try {
                service.startService(bluetoothSocket, isScanCar, isClearCode);
            } catch (IOException ioe) {
                Log.e(TAG, "Failure Starting live data");
                //disconnectBluetooth();
            }
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.e(TAG, className.toString() + " service is unbound");
        }
    };


    String TAG = CarScanFragment.class.getSimpleName();

    private void registerBroadcastReceivers() {

        //THis is to receive the obd commands from the device . Regarding the vehcile

        IntentFilter filter = new IntentFilter();

        filter.addAction("ObdCommandUpdates");


        //this is for receiving the notification count etc
        LocalBroadcastManager.getInstance(context).registerReceiver(notificationCountBroadcastReceiver, filter);
    }

    private BroadcastReceiver notificationCountBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("ObdCommandUpdates")) {
                //it is for receiving the OBD command updates from the OBD gateway service.

                Bundle b = intent.getExtras();
                ObdCommandJob obdCommandJob = null;
                if (b != null)
                    obdCommandJob = b.getParcelable("ObdCommandJob");
                if (obdCommandJob == null) {
                    Log.e(TAG, "No data");
                } else {

                    ObjectAnimator.ofInt(fragmentCarScanBinding.horizontalProgressBar, "progress", 100)
                            .setDuration(3000)
                            .start();
                    stateUpdate(obdCommandJob);
                }
            }

        }
    };

    HashMap<String, String> scannedResult = new HashMap<>();

    private void stateUpdate(ObdCommandJob job) {
        final String cmdName = job.getCommand().getName();
        String cmdResult = "";
        final String cmdID = BluetoothHelperClass.LookUpCommand(cmdName);
        Log.e(TAG, "State update called");

        if (job.getState().equals(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR)) {
            cmdResult = job.getCommand().getResult();
            if (cmdResult != null) {

                Log.e(TAG, "Execution error");
                /*if (cmdResult.equalsIgnoreCase("NODATA")) {
                    emptyValues.put(cmdID, cmdResult);
                }
                if (cmdID.equals("ENGINE_RPM")) {
                    commandResult.put(cmdID, cmdResult);
                    checkPerformanceFragmentVisible(cmdID, cmdName, cmdResult);
                    changeProtocol();
                }*/


                // return;
            }
        } else if (job.getState().equals(ObdCommandJob.ObdCommandJobState.BROKEN_PIPE)) {
            Log.e(TAG, "State update Broken pipe");
            showBluetoothDisconnected("Failed to connect to OBDII device. Try again");
            //LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("CarScanBluetoothDisconnected"));
            disconnectBluetooth();

            return;
        } else if (job.getState().equals(ObdCommandJob.ObdCommandJobState.NOT_SUPPORTED)) {
            cmdResult = getString(R.string.status_obd_no_support);
        } else {
            cmdResult = job.getCommand().getFormattedResult();
        }
        Log.e(TAG, "Command ID = " + cmdID + " - Command Name = " + cmdName + " - Command Result = " + cmdResult);

        //Here, we are checking for empty values
        if (cmdResult == null || cmdResult.equals("NODATA") || cmdResult.contains("UNABLETOCONNECT") || cmdResult.equalsIgnoreCase("NA")) {
            cmdResult = "";
        }

        scannedResult.put(cmdID, cmdResult.trim());

        if (cmdID.equals("PERMANENT_TROUBLE_CODES")) {
            disconnectBluetooth();
            gotoCarScanResultPage();
        }
        if (cmdID.equals("44") && cmdResult.equals("44")) {
            disconnectBluetooth();
            showBluetoothDisconnected("All fault codes are cleared");
            fragmentCarScanBinding.errorTxt.setBackgroundColor(getResources().getColor(R.color.green));

        }
    }

    private void gotoCarScanResultPage() {
        if (scannedResult.size() == 0) {
            showBluetoothDisconnected("Failed to scan your car. Please contact Fuelshine team");

        } else {
            Log.e(TAG, "scannedResult: " + scannedResult.toString());
            startActivity(new Intent(getActivity(), SecondActivity.class)
                    .putExtra("TAG", "CarScanResultFragment")
                    .putExtra("TroubleCodes", scannedResult));
        }
    }

    private void doUnbindService() {

        if (service != null && service.isRunning()) {
            service.stopService();
            context.unbindService(serviceConn);

        }
        Log.e(TAG, "Unbinding OBD service..");

    }


    @Override
    public void onDestroyView() {
        showMyToast();
        disconnectBluetooth();
        //LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("restart checkGPSEnabledOrnotInit"));
        super.onDestroyView();
    }

    private void disconnectBluetooth() {
        enableButtons(true);
        LocalBroadcastManager.getInstance(context).unregisterReceiver(notificationCountBroadcastReceiver);
        doUnbindService();
    }

    private void showMyToast() {
        if (selectedVehicleModel != null) {
            String brand = selectedVehicleModel.getVehicleBrand();
            if (brand.contains("MARUTI") || brand.contains("SUZUKI") || brand.contains("TATA") || brand.contains("MAHINDRA")) {
                Toast.makeText(context, "After using the Scan car please Re-plug the device again in OBD port.", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        FragmentManager fm = getChildFragmentManager();
        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getChildFragmentManager().getBackStackEntryCount() == 0) getActivity().finish();
            }
        });
    }


    ResponseFromServer responseFromServer = new ResponseFromServer() {
        @Override
        public void responseFromServer(String Response, String className, int value) {
            switch (Response) {
                case "0":
                    //THis is to end the trip. in foreground service
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent().setAction("ScanServiceActiviated"));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fragmentCarScanBinding.scanCardView.setVisibility(View.GONE);
                            fragmentCarScanBinding.carScanMainLayout.setVisibility(View.VISIBLE);
                        }
                    }, 1000);
                    break;

            }
        }
    };

}