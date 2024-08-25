package com.gypsee.sdk.demoaccount.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.LayoutFragmentIntroBinding;

public class QuickManualFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private int mParam1;
    private String mParam2;

    public QuickManualFragment() {
        // Required empty public constructor
    }

    public static QuickManualFragment newInstance(int param1, String param2) {
        QuickManualFragment fragment = new QuickManualFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    LayoutFragmentIntroBinding fragmentWelcomeBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentWelcomeBinding = DataBindingUtil.inflate(inflater, R.layout.layout_fragment_intro, container, false);
        checkPageNumber();
        return fragmentWelcomeBinding.getRoot();
    }


    private void checkPageNumber() {





        switch (mParam1) {

            case 0:
                fragmentWelcomeBinding.introImage.setImageDrawable(getResources().getDrawable(R.drawable.intro_1));
                fragmentWelcomeBinding.introTitle.setText("Connect");
                fragmentWelcomeBinding.introDescription.setText("Pair the Fuelshine app with your car stereo's Bluetooth to activate your personal driving assistant");

                break;
            case 2:
                fragmentWelcomeBinding.introImage.setImageDrawable(getResources().getDrawable(R.drawable.intro_3));
                fragmentWelcomeBinding.introTitle.setText("Get Rewards");
                fragmentWelcomeBinding.introDescription.setText("Earn rewards for safe, responsible and fuel efficient driving");
                break;

            case 1:
                fragmentWelcomeBinding.introImage.setImageDrawable(getResources().getDrawable(R.drawable.intro_2));
                fragmentWelcomeBinding.introTitle.setText("Drive Safe");
                fragmentWelcomeBinding.introDescription.setText("Receive immediate feedback on fuel efficient or inefficient driving for corrective action");
                break;

//            case 3:
              //  fragmentWelcomeBinding.introImage.setImageDrawable(getResources().getDrawable(R.drawable.intro_icon_four));
               // fragmentWelcomeBinding.introTitle.setText("Drive Safer");
               // fragmentWelcomeBinding.introDescription.setText("Receive alerts every time when you drive rash & drive safe & secure with Gypsee drive. Your every drive is Gypsee Protected now.");
//                break;


            /*case 0:
                fragmentWelcomeBinding.introImage.setImageDrawable(getResources().getDrawable(R.drawable.intro_safe_drive));
                fragmentWelcomeBinding.introTitle.setText("Gypsee safe driving app");
                fragmentWelcomeBinding.introDescription.setText("Improve your bad driving behaviour practice and achieve your safe driving goals and get rewarded for driving safe.");
                break;

            case 1:
                fragmentWelcomeBinding.introImage.setImageDrawable(getResources().getDrawable(R.drawable.intro_google_assistant));
                fragmentWelcomeBinding.introTitle.setText("Gypsee Supports\nGoogle Assistant");
                fragmentWelcomeBinding.introDescription.setText("Simply tap and say Hello Google Take me to HSR Layout and it does the work. That's not it just tap and send voice reply \"I am driving will call you back in sometime\".");
                break;

            case 2:
                fragmentWelcomeBinding.introImage.setImageDrawable(getResources().getDrawable(R.drawable.intro_rto));
                fragmentWelcomeBinding.introTitle.setText("RTO Vehicle Information");
                fragmentWelcomeBinding.introDescription.setText("Know about car owner and registered detail of the car with RTO.");
                break;

            case 3:
                fragmentWelcomeBinding.introImage.setImageDrawable(getResources().getDrawable(R.drawable.intro_service));
                fragmentWelcomeBinding.introTitle.setText("Service Reminder and Updates");
                fragmentWelcomeBinding.introDescription.setText("Our smart service reminder helps you keep track of your car servicing and save big on maintenance.");
                break;

            case 4:
                fragmentWelcomeBinding.introImage.setImageDrawable(getResources().getDrawable(R.drawable.intro_towing));
                fragmentWelcomeBinding.introTitle.setText("On-demand Towing\nand Service Assistance");
                fragmentWelcomeBinding.introDescription.setText("Car breakdown? Looking for a car service nearby? Say Goodbye to such problems with Gypsee on demand services.");
                break;*/
        }
    }





}
