package com.gypsee.sdk.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gypsee.sdk.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RaiseTicketFragment extends Fragment {

    public RaiseTicketFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_raise_ticket, container, false);
    }
}
