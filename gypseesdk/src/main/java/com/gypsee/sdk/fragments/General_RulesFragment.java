package com.gypsee.sdk.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gypsee.sdk.activities.GypseeMainActivity;
import com.gypsee.sdk.databinding.FragmentGeneralRulesBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link General_RulesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class General_RulesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public General_RulesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment General_RulesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static General_RulesFragment newInstance(String param1, String param2) {
        General_RulesFragment fragment = new General_RulesFragment();
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







    private FragmentGeneralRulesBinding ruleBinding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ruleBinding = FragmentGeneralRulesBinding.inflate(inflater, container, false);

        ((GypseeMainActivity) requireActivity()).hideBottomNav();


        ruleBinding.backButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });





        return ruleBinding.getRoot();
    }


}