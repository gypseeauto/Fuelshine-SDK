package com.gypsee.sdk.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.LayoutFragmentIntroBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IntroFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IntroFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int mParam1;
    private String mParam2;

    public IntroFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WelcomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IntroFragment newInstance(int param1, String param2) {
        IntroFragment fragment = new IntroFragment();
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
            /*case 0:
                fragmentWelcomeBinding.introImage.setImageDrawable(getResources().getDrawable(R.drawable.intro_icon_one));
                fragmentWelcomeBinding.introTitle.setText("Connect");
                fragmentWelcomeBinding.introDescription.setText("Simply plug your Gypsee device into the OBD port of the car & it's done. Yeah!so simple, like putting a Pendrive to your Laptop");

                break;
            case 1:
                fragmentWelcomeBinding.introImage.setImageDrawable(getResources().getDrawable(R.drawable.intro_icon_two));
                fragmentWelcomeBinding.introTitle.setText("Communicate");
                fragmentWelcomeBinding.introDescription.setText("Now, let's pair it with Gypsee app - Go to your phone settings > turn on your bluetooth > You'll find a device named OBDII, click on it to pair. ");
                break;

            case 2:
                fragmentWelcomeBinding.introImage.setImageDrawable(getResources().getDrawable(R.drawable.intro_icon_three));
                fragmentWelcomeBinding.introTitle.setText("Add Your Car");
                fragmentWelcomeBinding.introDescription.setText("Time to add your beloved car details to the app.- Go to 'My Cars' in the app, click on + icon to add your car information to start using it.");
                break;

            case 3:
                fragmentWelcomeBinding.introImage.setImageDrawable(getResources().getDrawable(R.drawable.intro_icon_four));
                fragmentWelcomeBinding.introTitle.setText("Drive Safer");
                fragmentWelcomeBinding.introDescription.setText("Receive alerts every time when you drive rash & drive safe & secure with Gypsee drive. Your every drive is Gypsee Protected now.");
                break;*/


           /* case 0:
                fragmentWelcomeBinding.introImage.setImageDrawable(getResources().getDrawable(R.drawable.intro_safe_drive));
                fragmentWelcomeBinding.introTitle.setText("Drive safe app");
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

            case 0:
                fragmentWelcomeBinding.introImage.setImageDrawable(getResources().getDrawable(R.drawable.intro_1));
                 fragmentWelcomeBinding.introTitle.setText(R.string.connect);
                fragmentWelcomeBinding.introDescription.setText(R.string.pair_the_fuelshine_app);

                break;
            case 2:
                fragmentWelcomeBinding.introImage.setImageDrawable(getResources().getDrawable(R.drawable.intro_3));
                fragmentWelcomeBinding.introTitle.setText(R.string.get_rewards);
                fragmentWelcomeBinding.introDescription.setText(R.string.earn_rewards);
                break;

            case 1:
                fragmentWelcomeBinding.introImage.setImageDrawable(getResources().getDrawable(R.drawable.intro_2));
                fragmentWelcomeBinding.introTitle.setText(R.string.drive_safe);
                fragmentWelcomeBinding.introDescription.setText(R.string.receive_imm);
                break;


        }
    }
}