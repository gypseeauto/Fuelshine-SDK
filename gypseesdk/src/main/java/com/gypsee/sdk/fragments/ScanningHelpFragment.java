package com.gypsee.sdk.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.FragmentScanningHelpBinding;

public class ScanningHelpFragment extends Fragment {

    Context context;
    FragmentScanningHelpBinding fragmentScanningHelpBinding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentScanningHelpBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_scanning_help, container, false);

        initToolbar();

        return fragmentScanningHelpBinding.getRoot();
    }

    private void initToolbar(){

        Toolbar toolbar = fragmentScanningHelpBinding.toolBarLayout.toolbar;
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);

        fragmentScanningHelpBinding.toolBarLayout.setTitle("Scanning Help");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
    }

    private void goBack() {
        ((AppCompatActivity) context).getSupportFragmentManager().popBackStackImmediate();

    }




}
