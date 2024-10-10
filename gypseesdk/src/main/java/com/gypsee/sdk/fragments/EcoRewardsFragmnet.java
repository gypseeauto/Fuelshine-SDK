package com.gypsee.sdk.fragments;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.GypseeMainActivity;
import com.gypsee.sdk.databinding.FragmentEcoRewardsFragmnetBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EcoRewardsFragmnet#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EcoRewardsFragmnet extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EcoRewardsFragmnet() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EcoRewardsFragmnet.
     */
    // TODO: Rename and change types and number of parameters
    public static EcoRewardsFragmnet newInstance(String param1, String param2) {
        EcoRewardsFragmnet fragment = new EcoRewardsFragmnet();
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

    private FragmentEcoRewardsFragmnetBinding rewardBinding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rewardBinding = FragmentEcoRewardsFragmnetBinding.inflate(inflater, container, false);

        ((GypseeMainActivity) requireActivity()).hideBottomNav();

//        // Set status bar color programmatically
//        Window window = requireActivity().getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.green)); // Use your color resource

        rewardBinding.backButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        rewardBinding.generateQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                QrCodeFragment qrCodeFragment = new QrCodeFragment();
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, qrCodeFragment, QrCodeFragment.class.getSimpleName())
                        .addToBackStack(QrCodeFragment.class.getSimpleName())
                        .commit();

//                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
//                transaction.replace(R.id.fragment_container, new QrCodeFragment());
//                transaction.addToBackStack(null);  // Optional: Adds the transaction to the back stack
//                transaction.commit();

            }
        });

        rewardBinding.transactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TransactionsFragment transactionsFragment = new TransactionsFragment();
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, transactionsFragment, TransactionsFragment.class.getSimpleName())
                        .addToBackStack(TransactionsFragment.class.getSimpleName())
                        .commit();

//                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
//                transaction.replace(R.id.fragment_container, new TransactionsFragment());
//                transaction.addToBackStack(null);  // Optional: Adds the transaction to the back stack
//                transaction.commit();
            }
        });


        // Inflate the layout for this fragment
        return rewardBinding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        rewardBinding = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        ((GypseeMainActivity) requireActivity()).showBottomNav();
    }

}