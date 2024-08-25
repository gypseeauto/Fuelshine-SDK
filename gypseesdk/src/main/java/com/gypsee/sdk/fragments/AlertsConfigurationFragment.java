package com.gypsee.sdk.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.gypsee.sdk.activities.GypseeMainActivity;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.databinding.FragmentAlertsConfigurationBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlertsConfigurationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlertsConfigurationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AlertsConfigurationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlertsConfigurationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlertsConfigurationFragment newInstance(String param1, String param2) {
        AlertsConfigurationFragment fragment = new AlertsConfigurationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStop() {
        super.onStop();
        ((GypseeMainActivity) requireActivity()).showBottomNav();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "appPreferences";
    private static final String KEY_MUTE = "mute";
    private static final String KEY_SWITCH1 = "switch1";
    private static final String KEY_SWITCH2 = "switch2";
    private static final String KEY_SWITCH3 = "switch3";
    private static final String KEY_SWITCH4 = "switch4";

    private MyPreferenece myPreferenece;

    FragmentAlertsConfigurationBinding alertsBinding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        alertsBinding = FragmentAlertsConfigurationBinding.inflate(inflater, container, false);
        sharedPreferences = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);


        myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, requireContext());

//        String SpeedSwitch = String.valueOf(myPreferenece.getSpeedingSwitch());
//        String harshBrakeSwitch = String.valueOf(myPreferenece.getSpeedingSwitch());
//        String harshAccSwitch = String.valueOf(myPreferenece.getSpeedingSwitch());
//        String textAndDriveSwitch = String.valueOf(myPreferenece.getSpeedingSwitch());
//        String mutedValue = String.valueOf(myPreferenece.getSpeedingSwitch());
//
//
//        Log.e("SwitchValues",)


        updateAllSwitches(myPreferenece.getMute());

        boolean mute = sharedPreferences.getBoolean(KEY_MUTE, false); // Default to false
        updateSwitches(mute);



        ((GypseeMainActivity) requireActivity()).hideBottomNav();

        alertsBinding.backButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        alertsBinding.speedSwitch.setChecked(sharedPreferences.getBoolean(KEY_SWITCH1, true));
        alertsBinding.brakeSwitch.setChecked(sharedPreferences.getBoolean(KEY_SWITCH2, true));
        alertsBinding.accelarationSwitch.setChecked(sharedPreferences.getBoolean(KEY_SWITCH3, true));
        alertsBinding.textAndDriveSwitch.setChecked(sharedPreferences.getBoolean(KEY_SWITCH4, true));

//        alertsBinding.speedSwitch.setChecked(myPreferenece.getSpeedingSwitch());
//        alertsBinding.brakeSwitch.setChecked(myPreferenece.getHarshBrakeSwitch());
//        alertsBinding.accelarationSwitch.setChecked(myPreferenece.getHarshAccelarationSwitch());
//        alertsBinding.textAndDriveSwitch.setChecked(myPreferenece.getTextAndDriveSwitch());

        alertsBinding.speedSwitch.setOnCheckedChangeListener(switchChangeListener);
        alertsBinding.brakeSwitch.setOnCheckedChangeListener(switchChangeListener);
        alertsBinding.accelarationSwitch.setOnCheckedChangeListener(switchChangeListener);
        alertsBinding.textAndDriveSwitch.setOnCheckedChangeListener(switchChangeListener);




        return alertsBinding.getRoot();
    }
    private CompoundButton.OnCheckedChangeListener switchChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            saveSwitchStates();

            checkMuteCondition();
        }
    };

    public void updateAllSwitches(boolean mute){

        if (myPreferenece.getMute()){

            alertsBinding.speedSwitch.setChecked(!mute);
            alertsBinding.brakeSwitch.setChecked(!mute);
            alertsBinding.accelarationSwitch.setChecked(!mute);
            alertsBinding.textAndDriveSwitch.setChecked(!mute);

        }
    }


    public void updateSwitches(boolean mute) {
        boolean allSwitchesOff = !alertsBinding.speedSwitch.isChecked() && !alertsBinding.brakeSwitch.isChecked() && !alertsBinding.accelarationSwitch.isChecked() && !alertsBinding.textAndDriveSwitch.isChecked();

//        if (mute){
//            alertsBinding.speedSwitch.setChecked(false);
//            alertsBinding.brakeSwitch.setChecked(false);
//            alertsBinding.accelarationSwitch.setChecked(false);
//            alertsBinding.textAndDriveSwitch.setChecked(false);
//        }

        if (allSwitchesOff) {
            alertsBinding.speedSwitch.setChecked(!mute);
            alertsBinding.brakeSwitch.setChecked(!mute);
            alertsBinding.accelarationSwitch.setChecked(!mute);
            alertsBinding.textAndDriveSwitch.setChecked(!mute);
        }
    }

    public void saveSwitchStates() {
        // Save switch states to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_SWITCH1, alertsBinding.speedSwitch.isChecked());
        editor.putBoolean(KEY_SWITCH2, alertsBinding.brakeSwitch.isChecked());
        editor.putBoolean(KEY_SWITCH3, alertsBinding.accelarationSwitch.isChecked());
        editor.putBoolean(KEY_SWITCH4, alertsBinding.textAndDriveSwitch.isChecked());
        editor.apply();

//        myPreferenece.setSpeedSwitch(alertsBinding.speedSwitch.isChecked());
//        myPreferenece.setHarshBrakeSwitch(alertsBinding.brakeSwitch.isChecked());
//        myPreferenece.setHarshAccelarationSwitch(alertsBinding.accelarationSwitch.isChecked());
//        myPreferenece.setTextAndDriveSwitch(alertsBinding.textAndDriveSwitch.isChecked());


    }

    private void checkMuteCondition() {
        boolean allOff = !alertsBinding.speedSwitch.isChecked() && !alertsBinding.brakeSwitch.isChecked() && !alertsBinding.accelarationSwitch.isChecked() && !alertsBinding.textAndDriveSwitch.isChecked();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_MUTE, allOff);
        editor.apply();
//        myPreferenece.setMute(allOff);
    }
}