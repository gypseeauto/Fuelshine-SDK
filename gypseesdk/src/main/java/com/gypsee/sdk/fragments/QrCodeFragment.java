package com.gypsee.sdk.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.FragmentEcoRewardsFragmnetBinding;
import com.gypsee.sdk.databinding.FragmentQrCodeBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QrCodeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QrCodeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public QrCodeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QrCodeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QrCodeFragment newInstance(String param1, String param2) {
        QrCodeFragment fragment = new QrCodeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    FragmentQrCodeBinding qrCodeBinding;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 600000;
    private boolean showCode = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        qrCodeBinding = FragmentQrCodeBinding.inflate(inflater,container,false);

        qrCodeBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        startTimer();

        qrCodeBinding.showOrHideCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (showCode){
                    qrCodeBinding.showOrHideCode.setImageResource(R.drawable.show_img);

                    qrCodeBinding.ellipse1.setVisibility(View.GONE);
                    qrCodeBinding.ellipse2.setVisibility(View.GONE);
                    qrCodeBinding.ellipse3.setVisibility(View.GONE);
                    qrCodeBinding.ellipse4.setVisibility(View.GONE);
                    qrCodeBinding.otp.setVisibility(View.VISIBLE);
                    qrCodeBinding.otp.setText("2314");

                    showCode = false;
                }else {

                    qrCodeBinding.showOrHideCode.setImageResource(R.drawable.hide_img);

                    qrCodeBinding.ellipse1.setVisibility(View.VISIBLE);
                    qrCodeBinding.ellipse2.setVisibility(View.VISIBLE);
                    qrCodeBinding.ellipse3.setVisibility(View.VISIBLE);
                    qrCodeBinding.ellipse4.setVisibility(View.VISIBLE);
                    qrCodeBinding.otp.setVisibility(View.GONE);

                    showCode = true;
                }






            }
        });


        return qrCodeBinding.getRoot();

    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {

                qrCodeBinding.timer.setText("00 min : 00 sec");
            }
        }.start();
    }

    private void updateTimer() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;


        String timeLeftFormatted = String.format("%02d min : %02d sec", minutes, seconds);
        qrCodeBinding.timer.setText(timeLeftFormatted);
    }

}