package com.gypsee.sdk.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.github.pires.obd.commands.ObdCommand;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.ConfigActivity;
import com.gypsee.sdk.activities.GypseeMainActivity;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.config.ObdConfig;
import com.gypsee.sdk.helpers.BluetoothHelperClass;
import com.gypsee.sdk.interfaces.BlueToothConnectionInterface;
import com.gypsee.sdk.io.AbstractGatewayService;
import com.gypsee.sdk.io.ObdCommandJob;
import com.gypsee.sdk.io.ObdGatewayService;
import com.gypsee.sdk.models.Vehiclemodel;
import com.gypsee.sdk.threads.ConnectThread;

import static com.gypsee.sdk.helpers.BluetoothHelperClass.LookUpCommand;

public class BluetoothConnectionService extends Service {

    private final static String NOTIFICATION_CHANNEL = "BLUETOOTH_FOREGROUND_SERVICE";
    private final static String NOTIFICATION_ID = "BLUETOOTH_SERVICE_ID";
    private final static int PENDING_INTENT_REQUEST_CODE = 100;
    private final static int NOTIFICATION_REQUEST_CODE = 101;
    private String TAG = BluetoothConnectionService.class.getSimpleName();

    MyPreferenece myPreferenece;
    ConnectThread connectThread;
    BluetoothSocket bluetoothSocket;
    NotificationCompat.Builder newNotificationBuilder;
    private HashMap<String, String> emptyValues = new HashMap<>();
    private HashMap<String, String> commandResult = new HashMap<>();
    boolean isStarting = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, this);

        /*connectThread =null;
        bluetoothSocket =null;
        emptyValues = new HashMap<>();
        commandResult = new HashMap<>();
        isStarting = true;*/
        startBluetoothForegroundService();
        checkBluetoothAndConnect();
        return START_STICKY;
    }

    private void checkBluetoothAndConnect() {

        if (enableBluetooth()) {
            //Stop rotate animation if any.
            performBluetoothOps();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkBluetoothAndConnect();
                }
            }, 12000);
            // return;
        }

    }

    private void startBluetoothForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(NOTIFICATION_ID, NOTIFICATION_CHANNEL);
        } else {
            Intent intent = new Intent(this, GypseeMainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, PENDING_INTENT_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.bigText("Fuelshine");
            bigTextStyle.setBigContentTitle("Tap to open");

            builder.setStyle(bigTextStyle);
            builder.setWhen(System.currentTimeMillis());
            builder.setSmallIcon(R.drawable.gypsee_theme_logo);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.gypsee_theme_logo));
            builder.setPriority(Notification.PRIORITY_HIGH);
            builder.setFullScreenIntent(pendingIntent, true);

            Notification notification = builder.build();
            startForeground(NOTIFICATION_REQUEST_CODE, notification);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelID, String channelName) {
        Intent resultIntent = new Intent(this, GypseeMainActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent pendingIntent;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S) {
            pendingIntent = taskStackBuilder.getPendingIntent(PENDING_INTENT_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        }else{
            pendingIntent = taskStackBuilder.getPendingIntent(PENDING_INTENT_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationChannel notificationChannel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null; //exit if notification manager is null
        notificationManager.createNotificationChannel(notificationChannel);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelID);

        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.gypsee_theme_logo)
                .setContentTitle("Fuelshine")
                .setContentText("Tap to open")
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent)
                .build();

        newNotificationBuilder = notificationBuilder;

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_REQUEST_CODE, notificationBuilder.build());
        startForeground(NOTIFICATION_REQUEST_CODE, notification);
    }

    private boolean performBluetoothOps() {
        Set<BluetoothDevice> pairedDevices = getBluetoothDevices();
        for (BluetoothDevice bluetoothDevice : pairedDevices) {
            if (bluetoothDevice.getName() != null && bluetoothDevice.getName().equals("OBDII")) {
                myPreferenece.saveStringData(ConfigActivity.BLUETOOTH_LIST_KEY, bluetoothDevice.getAddress());
                Log.e(TAG, "MAC: " + bluetoothDevice.getAddress());
                connectToBluetoothDevice(bluetoothDevice);
                return true;
            }
        }
        return false;
    }

    private boolean enableBluetooth() {
        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean(ConfigActivity.ENABLE_BT_KEY, true).apply();

        if (btAdapter.isEnabled()) {
            return true;
        } else {
            btAdapter.enable();
            return false;
        }
    }

    private Set<BluetoothDevice> getBluetoothDevices() {
        // get Bluetooth device
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter.isEnabled()) {
            btAdapter.startDiscovery();
        } else {
            btAdapter.enable();
        }
        //Make some Bluetooth operations
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        return pairedDevices;
    }

    private void connectToBluetoothDevice(BluetoothDevice bluetoothDevice) {
        if (connectThread != null && connectThread.isAlive()) {
            return;
        }
        connectThread = new ConnectThread(bluetoothDevice, blueToothConnectionInterface, this);
        connectThread.start();
    }

    BlueToothConnectionInterface blueToothConnectionInterface = new BlueToothConnectionInterface() {
        @Override
        public void onBluetoothConnected(BluetoothSocket socket) {

            bluetoothSocket = socket;
            Log.e(TAG, "Bluetooth is connected");
            showNotification("Device Connected");
            startLiveData();
        }


        @Override
        public void obBluetoothDisconnected() {
            Log.e(TAG, "Failure Starting live data");
            showNotification("Failure Starting live data");
        }
    };


    private void startLiveData() {
        Log.e(TAG, "Starting live data..");
        registerObdBroadCastReceiver();
        doBindService();
        //Directly starting the GPS to get the locations for every 5meters distance and 1 sec.
    }


    private void registerObdBroadCastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("ObdCommandUpdates");
        LocalBroadcastManager.getInstance(this).registerReceiver(obdCommandBroadcastReceiver, filter);
    }

    private BroadcastReceiver obdCommandBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /*if (getApplicationContext() == null || !isAdded()) {
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("replaceHomeFragment"));
                return;
            }*/
            if (intent.getAction().equals("ObdCommandUpdates")) {
                //it is for receiving the OBD command updates from the OBD gateway service.
                Bundle b = intent.getExtras();
                ObdCommandJob obdCommandJob = null;
                if (b != null)
                    obdCommandJob = b.getParcelable("ObdCommandJob");
                if (obdCommandJob == null) {
                    Log.e(TAG, "No data");
                } else {
                    stateUpdate(obdCommandJob);
                    Intent in = new Intent("NewPerformance");
                    in.putExtra("BlueToothStatus", true);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(in);
                }
            }

        }
    };

    private void stateUpdate(ObdCommandJob job) {

        final String cmdName = job.getCommand().getName();
        String cmdResult = "";
        final String cmdID = LookUpCommand(cmdName);
        Log.e(TAG, "State update called");

        if (job.getState().equals(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR)) {
            cmdResult = job.getCommand().getResult();
            if (cmdResult != null) {

                Log.e(TAG, "Execution error");
                if (cmdResult.equalsIgnoreCase("NODATA")) {
                    emptyValues.put(cmdID, cmdResult);
                }

                if (cmdID.equals("ENGINE_RPM")) {

                    switch (cmdResult) {
                        case "NODATA":
                        case "...UNABLETOCONNECT":
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                newNotificationBuilder.setContentText("Please switch ON ignition of your car");
                                NotificationManagerCompat.from(BluetoothConnectionService.this).notify(NOTIFICATION_REQUEST_CODE, newNotificationBuilder.build());
                            } else {
                                NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                                bigTextStyle.setBigContentTitle("Fuelshine");
                                bigTextStyle.bigText("Please switch ON the ignition of your car");
                                newNotificationBuilder.build();
                                newNotificationBuilder.notify();
                                //performBluetoothOps();
                            }

                            break;
                        case "INIT...BUSERROR":
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                newNotificationBuilder.setContentText("Connect with Fuelshine technical support");
                                NotificationManagerCompat.from(BluetoothConnectionService.this).notify(NOTIFICATION_REQUEST_CODE, newNotificationBuilder.build());
                            } else {
                                NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                                bigTextStyle.setBigContentTitle("Fuelshine");
                                bigTextStyle.bigText("Connect with Fuelshine technical support");
                                newNotificationBuilder.build();
                                newNotificationBuilder.notify();
                                //performBluetoothOps();
                            }
                            break;
                    }
                }

                /*if (cmdID.equals("ENGINE_RPM")) {
                    commandResult.put(cmdID, cmdResult);
                    checkPerformanceFragmentVisible(cmdID, cmdName, cmdResult);
                    changeProtocol();
                    new Handler().postDelayed(mQueueCommands, 1000);

                }*/
                return;
            }
        } else if (job.getState().equals(ObdCommandJob.ObdCommandJobState.BROKEN_PIPE)) {
            Log.e(TAG, "State update Broken pipe");
            showNotification("Device Disconnected");
            doUnbindService();
            //performTaskOnBrokenPipe();
            return;
        } else if (job.getState().equals(ObdCommandJob.ObdCommandJobState.NOT_SUPPORTED)) {
            cmdResult = getString(R.string.status_obd_no_support);
        } else {
            cmdResult = job.getCommand().getFormattedResult();
            if (cmdID.equalsIgnoreCase("ENGINE_RPM")) {
                if (isStarting) {
                    new Handler().postDelayed(mQueueCommands, 1000);
                }
                isStarting = false;

                Log.e(TAG, "Coming here : " + cmdName);
            }

            if (cmdID.equals("TROUBLE_CODES")) {
                //Here removing the C, B, U trouble codes.

                cmdResult = BluetoothHelperClass.parseTroubleCodes(cmdResult);
            }
        }
        Log.e(TAG, "Command ID = " + cmdID + " - Command Name = " + cmdName + " - Command Result = " + cmdResult);

        //isStarting = false;

        if (cmdResult == null) {
            cmdResult = "null";
        }
        if (cmdResult.equals("NA") || cmdResult.equalsIgnoreCase("null")) {
            emptyValues.put(cmdID, cmdResult);
            //Check CMD ID is not VIN. Because, we are checking the car VIN. If the VIN number comes, then we can compare the cars
            if (!cmdID.equals("VIN"))
                return;
        } else {
            emptyValues.remove(cmdID);
        }

        //If the vin is empty, we will not add VIN to the Command result map. SO that wrong things don't happen.

        if (cmdID.equals("VIN") && (cmdResult.contains("@") || cmdResult.equals("null") || cmdResult.equals("NA"))) {
            return;
        }

        commandResult.put(cmdID, cmdResult);
        /*if (myCSVWriter != null && endLocation != null)
            myCSVWriter.writeLineCSV(endLocation.getLatitude(), endLocation.getLongitude(), System.currentTimeMillis(), commandResult);

        checkPerformanceFragmentVisible(cmdID, cmdName, cmdResult);
        updateTripStatistic(job, cmdID);
*/
        //Do not write to CSV until u get the permission of adding and reading external storage

        //      writeToCSVMethod();
    }
   /* private void checkPerformanceFragmentVisible(String cmdID, String cmdName, String cmdResult) {
        Intent intent = new Intent("Performance");
        // You can also include some extra data.
        Bundle b = new Bundle();
        b.putString("cmdID", cmdID);
        b.putString("cmdName", cmdName);
        b.putString("cmdResult", cmdResult);
        intent.putExtras(b);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    int selectedProtocolPosition = 0;

    private void changeProtocol() {
        //Changing the protocol to the correct Protocol until we get the correct protocol
        if (isStarting)
            unableToConnect++;
        if (unableToConnect > 1) {
            if (selectedProtocolPosition == protocolStrings.size() - 1) {
                selectedProtocolPosition = 0;
            } else {
                selectedProtocolPosition = selectedProtocolPosition + 1;
            }

            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(ConfigActivity.PROTOCOLS_LIST_KEY, protocolStrings.get(selectedProtocolPosition)).apply();
            service.queueJob(new ObdCommandJob(new SelectProtocolCommand(ObdProtocols.valueOf(protocolStrings.get(selectedProtocolPosition)))));
            service.queueJob(new ObdCommandJob(new RPMCommand()));
            unableToConnect = 0;
            Log.e(TAG, "Protocol : " + protocolStrings.get(selectedProtocolPosition));
        } else
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isStarting) {

                        service.queueJob(new ObdCommandJob(new SpeedCommand()));
                        service.queueJob(new ObdCommandJob(new RPMCommand()));
                    }
                }
            }, 2000);

    }*/

    private final Runnable mQueueCommands = new Runnable() {
        public void run() {
            if (service != null && service.isRunning() && service.queueEmpty()) {
                queueCommands();
                Log.e(TAG, " mQueueCommands Runnbale : " + isServiceBound);
            }
            Log.e(TAG, "Is Service bound : " + isServiceBound);
            //THis was running in background earlier. thats the condition.
            if (isServiceBound) {
                new Handler().postDelayed(mQueueCommands, 500);
            }
        }
    };


    private void showNotification(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            newNotificationBuilder.setContentText(text);
            NotificationManagerCompat.from(BluetoothConnectionService.this).notify(NOTIFICATION_REQUEST_CODE, newNotificationBuilder.build());
        } else {
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle("Fuelshine");
            bigTextStyle.bigText(text);
            newNotificationBuilder.build();
            newNotificationBuilder.notify();
        }
    }


    private void performTaskOnBrokenPipe() {

        //On broken pipe, we will check first wether bluetooth is enabled or not. If enabled, we will unbind and then will try to connect with device.
        // If the device is not paired we will end trip.

        if (enableBluetooth()) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    performBluetoothOps();
                }
            }, 3000);

        }
        //If bluetooth is off, we will end the trip.
        else {
            Log.e(TAG, "stop self");
            //stopForeground(true);
            // stopSelf();
            //stopLiveData();
        }


    }


    boolean isThirtySec = true, isOneMinute = true;

    private void queueCommands() {

        Log.e(TAG, "queueCommands called : ");
        if (isServiceBound) {
            for (ObdCommand Command : ObdConfig.getCommands(isStarting, false, (/*currentTrip == null ||*/ commandResult.get("VIN") == null), isThirtySec, isOneMinute, getApplicationContext())) {
                service.queueJob(new ObdCommandJob(Command));

               /* if (currentTrip != null) {
                    isOneMinute = !isOneMinute && isOneMinute;
                    isThirtySec = !isThirtySec && isThirtySec;
                }*/
            }
        }
    }

    private void doUnbindService() {

        if (isServiceBound) {
            if (service.isRunning()) {
                service.stopService();
            }
            Log.e(TAG, "Unbinding OBD service..");
            unbindService(serviceConn);
            isServiceBound = false;
            service = null;
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(obdCommandBroadcastReceiver);

        }
    }


    private void doBindService() {

        Log.e(TAG, "Binding OBD service..");
        Intent serviceIntent = new Intent(getApplicationContext(), ObdGatewayService.class);
        //startService(serviceIntent);
        bindService(serviceIntent, serviceConn, Context.BIND_AUTO_CREATE);
    }

    private boolean isServiceBound = false;
    int incrementValueForVINCheck = 0;
    private AbstractGatewayService service;

    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            Log.e(TAG, className.toString() + " service is bound");
            isServiceBound = true;
            incrementValueForVINCheck = 0;
            service = ((AbstractGatewayService.AbstractGatewayServiceBinder) binder).getService();
            service.setContext(getApplicationContext());
            // rotateAnimation(false);
            //fragmentHomeBinding.progressLayout.setVisibility(View.VISIBLE);
            Log.e(TAG, "Starting service live data");
            try {
                commandResult.clear();
                service.startService(bluetoothSocket, false, false);
                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //If the service is bound then only we need to ask to connect to the vehicle.
                        if (isServiceBound) {
                           // addDummyValues();
                            //fragmentHomeBinding.logoLayout.setEnabled(true);
                        }
                    }
                }, 15000);*/
            } catch (IOException ioe) {
                Log.e(TAG, "Failure Starting live data");
                //stopLiveData();
            }
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.e(TAG, className.toString() + " service is unbound");
            isServiceBound = false;
        }
    };

    private void addDummyValues() {

        if (!commandResult.containsKey("VIN")) {

            // checkForTheVehicle("");
        }

    }

    private boolean isVinCheckedAlready = false;
    private Vehiclemodel selectedvehiclemodel;

/*
    private void checkForTheVehicle(String cmdResult) {

        // Checking if isVinCheckedAlready  = true, then return.
       // fragmentHomeBinding.progressLayout.setVisibility(View.GONE);

        if (isVinCheckedAlready) {
            return;
        } else {
            isVinCheckedAlready = true;
        }

        if (selectedvehiclemodel == null) {

            Log.e(TAG, "Checking for the vehicle " + commandResult);

            ArrayList<Vehiclemodel> vehiclemodelArrayList = new DatabaseHelper(getApplicationContext()).fetchAllVehicles();
            Log.e(TAG, "vehicles size : " + vehiclemodelArrayList.size());
            // If the vehicle Size is 1. We will directly connect with OBD and return from program

            if (vehiclemodelArrayList.size() == 0) {
               // showAddCarDialog(true);
                return;
            } else if (vehiclemodelArrayList.size() == 1) {
                //Only one vehicle connected.
                Log.e(TAG, "vehicles size : " + "One ");
                selectedvehiclemodel = vehiclemodelArrayList.get(0);
               // fragmentHomeBinding.setUserName("Hola, " + StringFormater.capitalizeWord(user.getUserFullName()) + "\n" + StringFormater.capitalizeWord(selectedvehiclemodel.getVehicleBrand() + " " + selectedvehiclemodel.getVehicleModel()));
                //checkTripStarted();
                return;
            }

            if (cmdResult.isEmpty()) {
                Log.e(TAG, "vehicles size : " + "Empty ");
               // showDialogtoSelectCar();
                return;
            }

            // We will loop through the vehicleList and check VIn matching. If matches, we will return
            for (Vehiclemodel vehiclemodel : vehiclemodelArrayList) {
                String vin = vehiclemodel.getVin();

                Log.e(TAG, "Vehicle : " + vehiclemodel.getVehicleBrand() + vehiclemodel.getVin());
                if (vin.equals(cmdResult)) {
                    // If the dialog is already showing, no need to show the popup again.
                  */
/*  if (selectCarDialog != null && selectCarDialog.isShowing()) {
                        selectCarDialog.dismiss();
                    }*//*


                    Log.e(TAG, "Selected vehicle: " + vehiclemodel.getVin());

                    selectedvehiclemodel = vehiclemodel;
                  //  fragmentHomeBinding.setUserName("Hola, " + StringFormater.capitalizeWord(user.getUserFullName()) + "\n" + StringFormater.capitalizeWord(selectedvehiclemodel.getVehicleBrand() + " " + selectedvehiclemodel.getVehicleModel()));
                    //checkTripStarted();
                    return;
                }
            }

           // showDialogtoSelectCar();
        }
    }
*/

    /*private void checkTripStarted() {


        //String RPM = commandResult.get("ENGINE_RPM");
        // boolean isRpmEmpty = (RPM == null || RPM.contains("UNABLE") || RPM.equalsIgnoreCase("NODATA") || RPM.isEmpty());

        Log.e(TAG, "Starting the trip");
        if (currentTrip == null *//*&& selectedvehiclemodel != null*//*) {
            //Setting user name and connected vehicle.
            commandResult.put("VEHICLE_ID", selectedvehiclemodel.getUserVehicleId());
            Log.e(TAG, "calling start trip 1962");
            callServer(getString(R.string.tripstart_url), "Start trip ", 1);

        } else {
            Log.e(TAG, "Current trip not null 1966");

        }
    }*/


}
