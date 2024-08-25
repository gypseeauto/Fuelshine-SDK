package com.gypsee.sdk.demoaccount.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.FragmentNewVehiclePerformanceBinding;

public class DemoNewVehiclePerformanceFragment extends Fragment {

    private ImageView[] ivArrayDotsPager;

    public DemoNewVehiclePerformanceFragment() {
        // Required empty public constructor
    }


    public static DemoNewVehiclePerformanceFragment newInstance(String param1, String param2) {
        DemoNewVehiclePerformanceFragment fragment = new DemoNewVehiclePerformanceFragment();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentNewVehiclePerformanceBinding fragmentNewVehiclePerformanceBinding;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fragmentNewVehiclePerformanceBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_vehicle_performance, container, false);
        context = getContext();

        //Backward button functionality.
//        fragmentNewVehiclePerformanceBinding.backBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((AppCompatActivity) context).finish();
//            }
//        });

//        fragmentNewVehiclePerformanceBinding.vhsDisplay.setText("100/100");

        setupPagerIndidcatorDots();
        setUpViewPager();
        // registerBroadcastReceivers();
        return fragmentNewVehiclePerformanceBinding.getRoot();
    }

    private void setupPagerIndidcatorDots() {
        ivArrayDotsPager = new ImageView[4];
        for (int i = 0; i < ivArrayDotsPager.length; i++) {
            ivArrayDotsPager[i] = new ImageView(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 0, 5, 0);
            ivArrayDotsPager[i].setLayoutParams(params);
            ivArrayDotsPager[i].setImageResource(R.drawable.pager_indicator_unselected);
            //ivArrayDotsPager[i].setAlpha(0.4f);
            ivArrayDotsPager[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setAlpha(1);
                }
            });
//            fragmentNewVehiclePerformanceBinding.pagerDots.addView(ivArrayDotsPager[i]);
//            fragmentNewVehiclePerformanceBinding.pagerDots.bringToFront();
        }
    }

    private void setUpViewPager() {
        // Instantiate a ViewPager2 and a PagerAdapter.
        ScreenSlidePagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(((AppCompatActivity) context));
//        fragmentNewVehiclePerformanceBinding.viewPager.setAdapter(pagerAdapter);
        //fragmentNewVehiclePerformanceBinding.troubleCodeTv.setText("Hey There! You are all good.");

        ivArrayDotsPager[0].setImageResource(R.drawable.pager_indicator_selected);
//        fragmentNewVehiclePerformanceBinding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//                for (int i = 0; i < ivArrayDotsPager.length; i++) {
//                    ivArrayDotsPager[i].setImageResource(R.drawable.pager_indicator_unselected);
//                }
//                ivArrayDotsPager[position].setImageResource(R.drawable.pager_indicator_selected);
//            }
//        });

    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(AppCompatActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            if (position <= 2)
                return DemoSingleParameterPerformanceFragment.newInstance(String.valueOf(position));
            else {
                return DemoPerformanceFragment.newInstance();
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }













}
