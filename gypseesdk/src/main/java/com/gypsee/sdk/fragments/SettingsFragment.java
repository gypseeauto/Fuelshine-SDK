package com.gypsee.sdk.fragments;

import static androidx.core.location.LocationManagerCompat.requestLocationUpdates;

import static com.gypsee.sdk.config.MyPreferenece.GYPSEE_PREFERENCES;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.GypseeMainActivity;
import com.gypsee.sdk.activities.SecondActivity;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.database.DatabaseHelper;
import com.gypsee.sdk.databinding.FragmentSettingsBinding;
import com.gypsee.sdk.dialogs.GiveFeedbackDialog;
import com.gypsee.sdk.firebase.FirebaseLogEvents;
import com.gypsee.sdk.helpers.BluetoothHelperClass;
import com.gypsee.sdk.jsonParser.ResponseFromServer;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.models.Vehiclemodel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
//    public static SettingsFragment newInstance(String param1, String param2) {
//        SettingsFragment fragment = new SettingsFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    private FragmentSettingsBinding settingsBinding;

    private Vehiclemodel selectedvehiclemodel;
    private boolean mute = false;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "appPreferences";
    private static final String KEY_MUTE = "mute";
    private GoogleSignInClient mGoogleSignInClient;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((GypseeMainActivity) requireActivity()).showBottomNav();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    //    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//                if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//        // Initialize GoogleSignInClient
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
//    }

    private String TAG = SettingsFragment.class.getSimpleName();
    private User user;
    private MyPreferenece myPreferenece;
    PackageInfo pInfo = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        settingsBinding = FragmentSettingsBinding.inflate(inflater, container, false);
        user = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, getContext()).getUser();
        sharedPreferences = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        myPreferenece = new MyPreferenece(GYPSEE_PREFERENCES, requireContext());

//        String logs = myPreferenece.getLogs();
//
//        settingsBinding.logs.setText(logs);



        boolean speed = sharedPreferences.getBoolean("switch1", true);
        boolean harshBrk = sharedPreferences.getBoolean("switch2", true);
        boolean harshAcc = sharedPreferences.getBoolean("switch3", true);
        boolean textanddrive = sharedPreferences.getBoolean("switch4", true);

        Log.e("speedSwitch", String.valueOf(speed));
        Log.e("harshBrkSwitch", String.valueOf(harshBrk));
        Log.e("harshaccSwitch", String.valueOf(harshAcc));
        Log.e("textAndDriveSwitch", String.valueOf(textanddrive));

        String languageSelected = myPreferenece.getLang();

        Log.e("LanguageSelected",languageSelected);


        // Read mute value from SharedPreferences
        mute = sharedPreferences.getBoolean(KEY_MUTE, false); // Default to false
        updateMuteLabel();
        updateSwitchesInAlertsFragment(mute); // Update switches based on initial mute state

        ArrayList<Vehiclemodel> vehiclemodelArrayList = new DatabaseHelper(getContext()).fetchAllVehicles();

        if (!vehiclemodelArrayList.isEmpty() ){
            selectedvehiclemodel = vehiclemodelArrayList.get(0);
        }else{
            Log.e("Vehicle","No vehicle Added");
        }


        ((GypseeMainActivity) requireActivity()).showBottomNav();



        try {
            pInfo = requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        String versionName = pInfo.versionName;

        settingsBinding.version.setText(getString(R.string.fuelshine_version)+versionName);
        
        settingsBinding.myvehicleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Create the new fragment instance
                MyCarsListFragment myCarsListFragment = new MyCarsListFragment();

                // Prepare arguments to pass to the new fragment
                Bundle args = new Bundle();
                args.putParcelable(Vehiclemodel.class.getSimpleName(), selectedvehiclemodel);

                ((GypseeMainActivity) requireActivity()).replaceFragment(myCarsListFragment, args);

            }
        });

        settingsBinding.personalInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment profileFragment = new ProfileFragment();
                ((GypseeMainActivity) requireActivity()).replaceNormalFragment(profileFragment);
            }
        });


        settingsBinding.logsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogsFragment logsFragment = new LogsFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.mainFrameLayout, logsFragment);
                fragmentTransaction.addToBackStack(SettingsFragment.class.getSimpleName());
                fragmentTransaction.commit();

            }
        });

        settingsBinding.alertsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertsConfigurationFragment alertsFragment = new AlertsConfigurationFragment();

                // Prepare arguments to pass to the new fragment
//                Bundle args = new Bundle();
//                args.putParcelable(Vehiclemodel.class.getSimpleName(), selectedvehiclemodel);

                ((GypseeMainActivity) requireActivity()).replaceAlertFragment(alertsFragment);

            }
        });

//
//                startActivity(new Intent(getActivity(), GypseeMainActivity.class)
//                                .putExtra("TAG", "MyCarsListFragment")
//                                .putExtra(Vehiclemodel.class.getSimpleName(), selectedvehiclemodel));
//            }
//        });


        settingsBinding.muteLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toggle mute state
                mute = !mute;
                updateMuteLabel();
                // Save mute value to SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(KEY_MUTE, mute);
                editor.apply();
                // Update switches in AlertsConfigurationFragment
                updateSwitchesInAlertsFragment(mute);

            }
        });

        settingsBinding.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                                Log.e(TAG, "Clicked on share icon");

                startActivity(new Intent(getActivity(), SecondActivity.class)
                        .putExtra("TAG", "ReferAndEarnFragment"));
            }
        });

        settingsBinding.logOutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        settingsBinding.helpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseLogEvents.firebaseLogEvent("accessed_technical_support",getContext());
                        BluetoothHelperClass.showTripEndDialog(getContext(), getLayoutInflater(), "Dear Customer!", "You are about to write mail to Fuelshine customer support team. Within 48 hours, we will resolve your query.", responseFromServer, 2);
//
            }
        });

        settingsBinding.logOutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mGoogleSignInClient != null) {
//                    LoginFragment loginFragment = new LoginFragment();
//                    mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            Log.e(TAG, "sign out onComplete");
//                            Log.e(TAG, "signout task: " + task.isSuccessful());
//                            ((GypseeMainActivity) requireActivity()).replaceNormalFragment(loginFragment);
//                        }
//                    });
//                } else {
//                    Log.e(TAG, "GoogleSignInClient is not initialized");
//                }

                Toast.makeText(requireContext(), "Log Out", Toast.LENGTH_SHORT).show();
            }
        });


        settingsBinding.rateUsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGiveFeedbackDialog();
            }
        });

        settingsBinding.aboutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SecondActivity.class)
                        .putExtra("TAG", "RSATermsAndConditionsFragment")
                        .putExtra("IsPrivaypolicy", true));
            }
        });

        settingsBinding.rulesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                General_RulesFragment rulesFragment = new General_RulesFragment();

                // Prepare arguments to pass to the new fragment
//                Bundle args = new Bundle();
//                args.putParcelable(Vehiclemodel.class.getSimpleName(), selectedvehiclemodel);

                ((GypseeMainActivity) requireActivity()).replaceNormalFragment(rulesFragment);
            }
        });



        return settingsBinding.getRoot();


    }

    private void updateSwitchesInAlertsFragment(boolean mute) {
        AlertsConfigurationFragment alertsFragment = (AlertsConfigurationFragment) getChildFragmentManager().findFragmentByTag("alertsFragment");
        if (alertsFragment != null) {
            alertsFragment.updateSwitches(mute);
        }
    }


    private void updateSwitches(boolean mute) {

        AlertsConfigurationFragment alertsFragment = (AlertsConfigurationFragment) getChildFragmentManager().findFragmentByTag("alertsFragment");
        if (alertsFragment != null) {
            alertsFragment.updateSwitches(mute);
        }
    }

    private void updateMuteLabel() {

            if (mute) {
                settingsBinding.muteLabel.setText(R.string.unmute);
            } else {
                settingsBinding.muteLabel.setText(R.string.mute);
            }
    }


    ResponseFromServer responseFromServer = new ResponseFromServer() {
        @Override
        public void responseFromServer(String Response, String className, int value) {



            switch (Response) {
//                case "0":
//                    rotateAnimation(false);
//                    //stopLiveData();
//                    Intent intent1 = new Intent();
//                    intent1.setAction("ScanServiceActiviated");
//                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
//                    break;
//                case "1":
//                    //Asking for battery optimization ignoring.
//
//                    Intent intent = new Intent();
//                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
//                    intent.setData(Uri.parse("package:" + context.getPackageName()));
//                    startActivity(intent);
//
//                    break;
                case "2":
                    sendEmail("Technical Assistance Request", user.getUserFullName() + "\n" + user.getUserPhoneNumber() + "\n" +
                            Build.MANUFACTURER + " " + Build.MODEL + " " + Build.VERSION.RELEASE);
                    break;

//                case "Exit Training Mode":
//                    if (value == 1){
//                        callServer(getResources().getString(R.string.exitTrainingMode).replace("userId", user.getUserId()), "Exit Training Mode", 16);
//
//                    } else {
//                        Log.e(TAG, "exit training mode cancelled");
//                    }
//                    break;
            }
        }
    };


    private void sendEmail(String subject, String userDetails) {
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setData(Uri.parse("mailto:")); // only email apps should handle this
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"hello@getfuelshine.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, userDetails);

        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.

        startActivity(Intent.createChooser(i, "Send mail..."));

    }


    private void showGiveFeedbackDialog() {

        GiveFeedbackDialog giveFeedbackDialog = new GiveFeedbackDialog(getContext(), "app", "null");
        giveFeedbackDialog.setCanceledOnTouchOutside(false);
        giveFeedbackDialog.setCancelable(false);
        giveFeedbackDialog.show();

        giveFeedbackDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
    }


//    public void checkGPSEnabledOrnotInit() {
//
//        //Register&de-register the broadcast receiver
//        // so that for android "S" version the issue is solved& it will ask for bluetoothpermission
//        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(foregroundServiceBroadcast);
//        registerForegroundServiceReceiver();
//        rotateAnimation(true);
//
//
//        Log.e(TAG, "checkgpsenabled called");
//
//        ArrayList<Vehiclemodel> vehiclemodelArrayList = new DatabaseHelper(context).fetchAllVehicles();
//        Log.e(TAG, "vehicles size : " + vehiclemodelArrayList.size());
//        // If the vehicle Size is 1. We will directly connect with OBD and return from program
//        if (vehiclemodelArrayList.size() == 0) {
//            showZoopAddCarDialog();
//        } else {
//
//            GpsUtils gpsUtils = new GpsUtils(context);
//            gpsUtils.turnGPSOn(new GpsUtils.onGpsListener() {
//                @Override
//                public void gpsStatus(boolean isGPSEnable) {
//                    boolean isDriveMode = myPreferenece.getSharedPreferences().getBoolean(MyPreferenece.DRIVING_MODE, true);
//                    if (isGPSEnable) {
//                        //startBackGroundLocationService();
//                        if (isDriveMode) {
//                            Intent intent = new Intent(context, ForegroundService.class);
//                            context.startService(intent);
//                            //LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("Start Location Service"));
//                        } else {
//
//                            ArrayList<Vehiclemodel> vehiclemodelArrayList = new DatabaseHelper(context).fetchAllVehicles();
//                            Log.e(TAG, "vehicles size : " + vehiclemodelArrayList.size());
//
//                            if (vehiclemodelArrayList.size() == 1) {
//                                selectedvehiclemodel = vehiclemodelArrayList.get(0);
//                                Log.e(TAG, selectedvehiclemodel.getVehicleBrand() + " " + selectedvehiclemodel.getVehicleModel());
////                                fragmentHomeBinding.setUserName("Hola, " + StringFormater.capitalizeWord(user.getUserFullName()) + "\n" + StringFormater.capitalizeWord(selectedvehiclemodel.getVehicleBrand().trim() + "\n" + selectedvehiclemodel.getVehicleModel().trim()));
//
//                            }/* else {
//                                showDialogtoSelectCar();
//                            }*/
//
//                        /*checkForTheVehicle("");
//                        disableDriveLayout(true)*/
//                            ;
//
//
//                            //Phone foreground service
//                        }
//                    } else {
//                        Toast.makeText(requireContext(), "Please switch on your GPS", Toast.LENGTH_LONG).show();
//                    }
//                }
//            });
//        }
//
//
//    }









}