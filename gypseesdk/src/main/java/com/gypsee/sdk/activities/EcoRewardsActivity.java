package com.gypsee.sdk.activities;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gypsee.sdk.fragments.EcoRewardsFragmnet;
import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.ActivityEcoRewardsBinding;

public class EcoRewardsActivity extends AppCompatActivity {

    private ActivityEcoRewardsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEcoRewardsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        // Set status bar color programmatically
//        Window window = getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(ContextCompat.getColor(this, R.color.green)); // Use your color resource


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new EcoRewardsFragmnet())
                    .commit();
        }

//        setSupportActionBar(binding.toolbar);
//        binding.toolbar.setNavigationIcon(R.drawable.back_button);
//        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

//        binding.backButtonLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });


    }
}