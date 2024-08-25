package com.gypsee.sdk.fragments;


import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.FragmentServiceReminderBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ServiceReminderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceReminderFragment extends Fragment {

    public ServiceReminderFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ServiceReminderFragment newInstance() {
        ServiceReminderFragment fragment = new ServiceReminderFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    private FragmentServiceReminderBinding fragmentServiceReminderBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fragmentServiceReminderBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_service_reminder, container, false);

        View view = fragmentServiceReminderBinding.getRoot();

        fragmentServiceReminderBinding.setName("Bhaskar");
        fragmentServiceReminderBinding.toolBarLayout.toolBarTitle.setText(getResources().getString(R.string.service_remainder_updates));



        return view;
    }

}
