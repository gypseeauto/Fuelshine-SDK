package com.gypsee.sdk.activities;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.ActivityIntroBinding;
import com.gypsee.sdk.demoaccount.fragments.QuickManualFragment;

public class QuickManualActivity extends AppCompatActivity {

    ActivityIntroBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_intro);

        setupPagerIndidcatorDots();
        setUpViewPager();

        activityMainBinding.skipButton.setText("Skip");

        activityMainBinding.skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    onBackPressed();
            }
        });

    }


    // An equivalent ViewPager2 adapter class
    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            return QuickManualFragment.newInstance(position, "");
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    //Set the number of dots at the bottom of the screen.
    private ImageView[] ivArrayDotsPager;

    private void setUpViewPager() {

        // Instantiate a ViewPager2 and a PagerAdapter.
        ScreenSlidePagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(this);
        activityMainBinding.viewpager.setAdapter(pagerAdapter);

        ivArrayDotsPager[0].setImageResource(R.drawable.white_circle_bg);

        activityMainBinding.viewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                for (int i = 0; i < ivArrayDotsPager.length; i++) {
                    ivArrayDotsPager[i].setImageResource(R.drawable.pager_indicator_selected);
                }
                ivArrayDotsPager[position].setImageResource(R.drawable.white_circle_bg);
            }
        });


    }

    private void setupPagerIndidcatorDots() {
        ivArrayDotsPager = new ImageView[3];
        for (int i = 0; i < ivArrayDotsPager.length; i++) {
            ivArrayDotsPager[i] = new ImageView(this);
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
            activityMainBinding.pagerDots.addView(ivArrayDotsPager[i]);
            activityMainBinding.pagerDots.bringToFront();
        }
    }


}
