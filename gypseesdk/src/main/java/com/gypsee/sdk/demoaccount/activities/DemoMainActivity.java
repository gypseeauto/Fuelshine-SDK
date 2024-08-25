package com.gypsee.sdk.demoaccount.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.GypseeMainActivity;
import com.gypsee.sdk.databinding.DemoActivityMainBinding;
import com.gypsee.sdk.demoaccount.fragments.DemoHomeFragement;
import com.gypsee.sdk.fragments.NavigateFragment;

public class DemoMainActivity extends AppCompatActivity {

    public DemoHomeFragement demoHomeFragment;
    private static final String TAG = DemoMainActivity.class.getName();
    //public MyPreferenece myPreferenece;

    public static DemoActivityMainBinding activityMainBinding;
    public static final int GPSPERMISSION_REQUESTCODE = 10001;

    @Override
    protected void onResume() {
        super.onResume();
        activityMainBinding.bottomNavigationView.setSelectedItemId(R.id.nav_drive);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.demo_activity_main);
        //myPreferenece = new MyPreferenece(GYPSEE_PREFERENCES, this);
        activityMainBinding.exitDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DemoMainActivity.this, GypseeMainActivity.class));
                finishAffinity();
            }
        });
        replaceHomeFragment(false);
        initBottomnavigationView();
       // registerBroadcastReceivers();

        /*myPreferenece.setIsTripRunning(false);
        myPreferenece.setIsConnecting(false);*/
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    private void replaceHomeFragment(boolean isNotAttachedToContext) {

        // We need to see if the activity is finishing or destroyed. If it is not finished or destroyed,
        // we will replace the fragment by allowing the state loss.
        if (!isFinishing() && !isDestroyed()) {
            // Remove the fragment if already attached to the fragment
            if (demoHomeFragment != null && isNotAttachedToContext) {
                getSupportFragmentManager().beginTransaction().remove(demoHomeFragment).commit();
            }

            demoHomeFragment = DemoHomeFragement.newInstance(isNotAttachedToContext);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainFrameLayout, demoHomeFragment, DemoHomeFragement.class.getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    private void initBottomnavigationView() {

        activityMainBinding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                final int previousItem = activityMainBinding.bottomNavigationView.getSelectedItemId();
                int id = menuItem.getItemId();
                if (previousItem != id) {
                    removePreviousFragments();

                    if (id == R.id.navigate_icon) {
                        NavigateFragment navigationFragment = NavigateFragment.newInstance();
                        //My cars Fragment should show.
                        getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.mainFrameLayout, navigationFragment, NavigateFragment.class.getSimpleName())
                                .addToBackStack(NavigateFragment.class.getSimpleName())
                                .commit();
                    } else if (id == R.id.nav_drive) {//Do nothing beacuse, the Home fragment is the base.
                        //Utils.clearAllData(MainActivity.this);
                    } else if (id == R.id.navigation_scancar) {
                        startActivity(new Intent(getApplicationContext(), DemoSecondActivity.class)
                                .putExtra("TAG", "DemoSafetyCenterFragment"));

                            /*getSupportFragmentManager().beginTransaction()
                                    .add(R.id.mainFrameLayout, ScanningFragment.newInstance(), ScanningFragment.class.getSimpleName())
                                    .addToBackStack(ScanningFragment.class.getSimpleName())
                                    .commit();*/
                    } else if (id == R.id.navigation_assistance) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://gypsee.ai/offers/"));
                        startActivity(browserIntent);
//                            startActivity(new Intent(getApplicationContext(), DemoSecondActivity.class)
//                                    .putExtra("TAG", "DemoStoreMainFragment"));
                    }
                }
                return true;
            }
        });
    }

    private void removePreviousFragments() {
        //THis is useful, when a user disable the permission from settings. THis will remove the crshes.

        // Clean fragments (only if the app is recreated (When user disable permission))
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (!fragmentManager.isDestroyed() && fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }




}
