package com.gypsee.sdk.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.Settings;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.GypseeMainActivity;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.database.DatabaseHelper;
import com.gypsee.sdk.databinding.AlertAddcarLayoutBinding;
import com.gypsee.sdk.databinding.FragmentPermissionBinding;
import com.gypsee.sdk.jsonParser.ResponseFromServer;
import com.gypsee.sdk.services.TripBackGroundService;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACTIVITY_RECOGNITION;
import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.BLUETOOTH_CONNECT;
import static android.Manifest.permission.BLUETOOTH_SCAN;
import static android.Manifest.permission.MODIFY_AUDIO_SETTINGS;
import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class PermissionActivity extends AppCompatActivity {


    private static final int PERMISSION_REQUEST_CODE = 101;
    private MyPreferenece myPreferenece;

    public PermissionActivity() {
        // Required empty public constructor
    }

    FragmentPermissionBinding fragmentPermissionBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, this);
        fragmentPermissionBinding = DataBindingUtil.setContentView(this, R.layout.fragment_permission);

        //Check vehicles size ==0, then callBackGround Service



//        fragmentPermissionBinding.confirmBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                checkPermissions();
//                //showLocationPermissionPrompt();
//                //BluetoothHelperClass.showOkCancelDialog(PermissionActivity.this, getLayoutInflater(), "", "Gypsee app collects location data to enable \"Plotting trip Route on google map\", \"Distance travelled\", & \"Driving alerts\" even when the app is closed or not in use.", responseFromServer);
//            }
//        });
//        initViews();

        if (savedInstanceState == null){
            addFragment(new NewPermissionsFragment());
        }


    }

    private void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.perm_frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.perm_frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void onPermissionsClick(){

        //Check permission and show the fragment accordingly.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P && ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACTIVITY_RECOGNITION) !=
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            replaceFragment(new PhysicalActivityPermissionFragment());
        }else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)) {
            replaceFragment(new LocationPermissionFragment());
            }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            replaceFragment(new NotificationPermissionFragment());

        }

        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                        )) {
            replaceFragment(new BluetoothPermissionFragment());
        }

        else{
            replaceFragment(new BatteryOptimisationPermissionFragment());

        }
    }
    void showSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Need Permissions")
                .setMessage("This app needs permissions to work properly. You can grant them in app settings.")
                .setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        openSettings();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }


    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
    public void onAllPermissionsGranted(){

        checkPermissions();

    }
    private void callBackgroundService(int value) {

        Intent intent = new Intent(PermissionActivity.this, TripBackGroundService.class);
        intent.putExtra(TripBackGroundService.PURPOSE, "Get UserVehicles");
        startService(intent);
    }


    private void initViews() {

//        //Location layout
//        fragmentPermissionBinding.locationLayout.alertImage.setImageDrawable(getDrawable(R.drawable.permission_location_pin));
//        fragmentPermissionBinding.locationLayout.title.setText("Location");
//        fragmentPermissionBinding.locationLayout.description.setText("Trip records, Bluetooth pairing, Always in use");
//
//        //storageLayout
//        fragmentPermissionBinding.storageLayout.alertImage.setImageDrawable(getDrawable(R.drawable.storage_icon));
//        fragmentPermissionBinding.storageLayout.title.setText("Storage");
//        fragmentPermissionBinding.storageLayout.description.setText("Driving record storage, Log file storage");
//
//        //cameraLayout
//        fragmentPermissionBinding.cameraLayout.alertImage.setImageDrawable(getDrawable(R.drawable.camera_icon));
//        fragmentPermissionBinding.cameraLayout.title.setText("Camera");
//        fragmentPermissionBinding.cameraLayout.description.setText("Photos to confirm your car registration");
//
//        //contactLayout
//        fragmentPermissionBinding.contactPermissionLayout.alertImage.setImageDrawable(getDrawable(R.drawable.contacts));
//        fragmentPermissionBinding.contactPermissionLayout.title.setText("Contact");
//        fragmentPermissionBinding.contactPermissionLayout.description.setText("To deliver emergency assitance services");
//
//        //recognitionLayout
//        fragmentPermissionBinding.recognitionLayout.alertImage.setImageDrawable(getDrawable(R.drawable.fs_permission_car_icon));
//        fragmentPermissionBinding.recognitionLayout.title.setText("Activity Recognition");
//        fragmentPermissionBinding.recognitionLayout.description.setText("Activity recognition for detecting the motion of your Car");
//
//        //accessibilityLayout
////        fragmentPermissionBinding.accessibilityLayout.alertImage.setImageDrawable(getDrawable(R.drawable.accessibility_premission_icon));
////        fragmentPermissionBinding.accessibilityLayout.title.setText("Android Accessibility");
////        fragmentPermissionBinding.accessibilityLayout.description.setText("We are using this API to detect motion in the vehicle. So that we can check vehicle health and calculate analytics & keep the app running in the background");
//
//        //queryAllPackagesLayout
//        fragmentPermissionBinding.queryAllPackagesLayout.alertImage.setImageDrawable(getDrawable(R.drawable.query_icon));
//        fragmentPermissionBinding.queryAllPackagesLayout.title.setText("Query All Packages");
//        fragmentPermissionBinding.queryAllPackagesLayout.description.setText("We are using this permission to keep improving our app through your feedbacks");

    }

    String TAG = PermissionActivity.class.getSimpleName();

    // Register the permissions callback, which handles the user's response to the
// system permissions dialog. Save the return value, an instance of
// ActivityResultLauncher, as an instance variable.
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    Log.e(TAG, "Permission not granted: " );
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });
    private final int OVERLAY_PERMISSION_CODE = 602;

//    private void checkPermissions() {
//        Log.e(TAG, "Resuming..");
//
//        // Check if both ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION permissions are granted
//        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
//                && checkSelfPermission(ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {
//            // Both permissions are granted, start the TripBackGroundService
//            Intent intent = new Intent(PermissionActivity.this, TripBackGroundService.class);
//            intent.putExtra(TripBackGroundService.PURPOSE, "Get UserVehicles");
//            startService(intent);
//        } else {
//            Log.e("MyService", "No permissions ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION!");
//        }
//
//        // Existing permission check logic
//        String[] allPermissionNotGranted = fetchDeniedPermissions();
//        if (allPermissionNotGranted.length > 0) {
//            Log.e("Permissions requested", Arrays.toString(allPermissionNotGranted));
//            requestPermissions(allPermissionNotGranted, PERMISSION_REQUEST_CODE);
//        } else {
//            checkIfOverlayDrawGranted();
//        }
//    }

    private void checkPermissions() {
        Log.e(TAG, "Resuming..");

        String[] allPermissionNotGranted = fetchDeniedPermissions();
        if (allPermissionNotGranted.length > 0) {
            Log.e("Permissions requested", Arrays.toString(allPermissionNotGranted));

            requestPermissions(allPermissionNotGranted, PERMISSION_REQUEST_CODE);

            //   requestPermissionLauncher.launch(ACCESS_FINE_LOCATION);
        } else
        {
//            replaceFragment(new BatteryOptimisationPermissionFragment());

            checkIfOverlayDrawGranted();
        }

    }

    public void gotoMainActivity() {
        startActivity(new Intent(this, GypseeMainActivity.class)
                .putExtra("isNewUser", getIntent().getBooleanExtra("isNewUser", false))
                .putExtra("freshlogin", getIntent().getBooleanExtra("freshlogin", false)));
        finish();
    }

    private void checkIfOverlayDrawGranted(){

        if (new DatabaseHelper(PermissionActivity.this).fetchAllVehicles().size() == 0)
            callBackgroundService(0);


        if (Settings.canDrawOverlays(getApplicationContext())){
            replaceFragment(new BatteryOptimisationPermissionFragment());
//            checkIfQueryAllPackagesGranted();
        } else {
            showOverlayPermissionPrompt();
        }
    }



//      if (grantResults.length > 0 && allPermissionsGranted(grantResults)) {
//        // All permissions are granted
//        checkIfOverlayDrawGranted();
//    } else {
//        // At least one permission is denied
//        showAddCarDialog();
//    }

//
////    private void checkIfAccessibilityPermissionGranted(){
////        if (myPreferenece.getIfAccessibilityPermissionGranted()){
////            checkIfQueryAllPackagesGranted();
////        } else {
////            showAccessibilityPermissionDialog();
////        }
////    }
//
    private void checkIfQueryAllPackagesGranted(){
        if (myPreferenece.getIfQueryAllPackagesPermissionGranted()){
            gotoMainActivity();
        } else {
            showQueryAllPackagesPermissionDialog();
        }
    }

    private String[] fetchDeniedPermissions() {

        ArrayList<String> permissions = new ArrayList<>();

        permissions.add(ACCESS_COARSE_LOCATION);
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissions.add(BLUETOOTH);
        permissions.add(MODIFY_AUDIO_SETTINGS);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            // activity_recognition permission is required only for gingerbread and newer versions
            permissions.add(ACTIVITY_RECOGNITION);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            //Bluetooth_scan and bluetooth_connect permissions required only for android S and newer version

            permissions.add(BLUETOOTH_CONNECT);
            permissions.add(BLUETOOTH_SCAN);
            permissions.remove(BLUETOOTH);
            permissions.remove(BLUETOOTH_ADMIN);

        }
        if(Build.VERSION.SDK_INT >=33)
        {
            permissions.remove(WRITE_EXTERNAL_STORAGE);
            permissions.remove(READ_EXTERNAL_STORAGE);
            permissions.add(POST_NOTIFICATIONS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {

            permissions.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION);

        }


        List<String> missingPermissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PERMISSION_GRANTED) {
                missingPermissionList.add(permission);
            }
        }
        return missingPermissionList.toArray(new String[0]);
    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        Log.e("OnrequestPermission", "Reached");
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (BluetoothHelperClass.fetchDeniedPermissions(this).length > 0) {
//                showAddCarDialog();
//            } else {
//                checkIfOverlayDrawGranted();
//            }
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("OnrequestPermission", "Reached");

        if (requestCode == PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && allPermissionsGranted(grantResults)) {
                // All permissions are granted
                checkIfOverlayDrawGranted();
            } else {
                // At least one permission is denied
                showSettingsDialog();
            }


        }
    }


    private boolean allPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    private int BACKGROUND_LOCATION_REQUEST_CODE = 601;




    private void showOverlayPermissionPrompt(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Overlay Permission");
        builder.setMessage("Gypsee need to draw on top. Please allow draw on top.");
        builder.setPositiveButton("AGREE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_CODE);

            }
        });
        builder.setNegativeButton("DISAGREE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                replaceFragment(new BatteryOptimisationPermissionFragment());
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void showAccessibilityPermissionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Android Accessibility");
        builder.setMessage("We are using this Permission to detect motion in the vehicle. So that we can check vehicle health and calculate analytics & keep the app running in the background.");
        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                myPreferenece.setAccessibilityPermission(true);
                checkIfQueryAllPackagesGranted();

            }
        });
        builder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                myPreferenece.setAccessibilityPermission(false);
            }
        });
        builder.show();
    }

    private void showQueryAllPackagesPermissionDialog(){

        replaceFragment(new BatteryOptimisationPermissionFragment());

//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Query All Packages");
//        builder.setMessage("We are using this permission to keep improving our app through your feedbacks");
//        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//                myPreferenece.setQueryAllPackagesPermission(true);
//                gotoMainActivity();
//
//            }
//        });
//        builder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//                myPreferenece.setQueryAllPackagesPermission(false);
//            }
//        });
//        builder.show();
    }

    private void showAddCarDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertAddcarLayoutBinding alertAddcarLayoutBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.alert_addcar_layout, null, false);

        alertAddcarLayoutBinding.titleTv.setText("Permission Request!");
        alertAddcarLayoutBinding.descriptionTV.setText("We request you to provide all the permissions to provide you the best features of the app");
        alertAddcarLayoutBinding.negativeBtn.setVisibility(View.GONE);
        alertAddcarLayoutBinding.lineView2.setVisibility(View.GONE);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog l``ayout
        builder.setView(alertAddcarLayoutBinding.getRoot());
        final AlertDialog addvehicleAlertDialog = builder.create();
        addvehicleAlertDialog.setCanceledOnTouchOutside(false);
        addvehicleAlertDialog.setCancelable(false);
        addvehicleAlertDialog.show();
        addvehicleAlertDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

        alertAddcarLayoutBinding.positioveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to AddVehicleFragment.
                addvehicleAlertDialog.dismiss();
                //Go to permissions page in settings.
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, PERMISSION_REQUEST_CODE);

            }
        });

        alertAddcarLayoutBinding.negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addvehicleAlertDialog.dismiss();
            }
        });
    }

    ResponseFromServer responseFromServer = new ResponseFromServer() {
        @Override
        public void responseFromServer(String Response, String className, int value) {
            switch (value) {
                case 0:
                    checkPermissions();
                    break;
                case 1:
                    break;
            }
        }
    };


}
