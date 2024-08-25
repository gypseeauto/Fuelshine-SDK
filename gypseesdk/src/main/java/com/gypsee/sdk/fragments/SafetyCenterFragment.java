package com.gypsee.sdk.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.AddMemberDialogBinding;
import com.gypsee.sdk.databinding.FragmentSafetyCenterBinding;
import com.gypsee.sdk.firebase.FirebaseLogEvents;

public class SafetyCenterFragment extends Fragment {

    Context context;
    private FragmentSafetyCenterBinding fragmentSafetyCenterBinding;

    public SafetyCenterFragment(){
        //Empty constructor
    }

    public static SafetyCenterFragment newInstance() {
        Bundle args = new Bundle();
        SafetyCenterFragment fragment = new SafetyCenterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentSafetyCenterBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_safety_center, container, false);

        fragmentSafetyCenterBinding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goback();
            }
        });

        fragmentSafetyCenterBinding.addMemberTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMemberDialogFragment addMemberDialogFragment = new AddMemberDialogFragment();
                addMemberDialogFragment.setCancelable(true);
                addMemberDialogFragment.show(getChildFragmentManager(), "TAG");
            }
        });

        setupPagerIndidcatorDots();
        setUpViewPager();
        FirebaseLogEvents.firebaseLogEvent("safety_center");



        return fragmentSafetyCenterBinding.getRoot();
    }

    private void goback() {
        /*if (getActivity() != null)
            getActivity().getSupportFragmentManager().popBackStackImmediate();*/
        ((AppCompatActivity)context).finish();
    }


    private ImageView[] ivArrayDotsPager;

    private void setupPagerIndidcatorDots() {
        ivArrayDotsPager = new ImageView[2];
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
            fragmentSafetyCenterBinding.pagerDots.addView(ivArrayDotsPager[i]);
            fragmentSafetyCenterBinding.pagerDots.bringToFront();
        }
    }


    private void setUpViewPager() {
        // Instantiate a ViewPager2 and a PagerAdapter.
        ScreenSlidePagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(((AppCompatActivity) context));
        fragmentSafetyCenterBinding.safetyViewPager.setAdapter(pagerAdapter);

        ivArrayDotsPager[0].setImageResource(R.drawable.pager_indicator_selected);
        fragmentSafetyCenterBinding.safetyViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                for (int i = 0; i < ivArrayDotsPager.length; i++) {
                    ivArrayDotsPager[i].setImageResource(R.drawable.pager_indicator_unselected);
                }
                ivArrayDotsPager[position].setImageResource(R.drawable.pager_indicator_selected);
            }
        });

    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(AppCompatActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            return SafetySinglePageFragment.newInstance(position);
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }











}
