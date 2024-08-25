package com.gypsee.sdk.activities;


import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.work.BackoffPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;


import java.util.concurrent.TimeUnit;

import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.databinding.ActivitySplashBinding;
import com.gypsee.sdk.fragments.PermissionActivity;
import com.gypsee.sdk.helpers.BluetoothHelperClass;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.utils.Constants;
import com.gypsee.sdk.workmanager.LoginNotifService;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding activitySplashBinding;
    private WorkManager mWorkManager;
    private MyPreferenece myPreferenece;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySplashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, this);
        checkuserLoggedInOrNot();
    }



    private void checkuserLoggedInOrNot() {
        final Intent in;

        MyPreferenece myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, this);
        User user = myPreferenece.getUser();

        if (user == null) {
            if (myPreferenece.getNewInstall()){
                in = new Intent(this, IntroActivity.class);
            } else{
                in = new Intent(this, LoginRegisterActivity.class);
            }
            initWorkManager();
        } else {
            //Here checking the permissions. If all permissions are granted, we will go to MainActivity
            //Else, we will go to permissions Activity
            if (BluetoothHelperClass.fetchDeniedPermissions(this).length > 0 || !Settings.canDrawOverlays(getApplicationContext()) ||
//                    !myPreferenece.getIfAccessibilityPermissionGranted() || //uncomment if accessibility permission is required
                    !myPreferenece.getIfQueryAllPackagesPermissionGranted()) {
                in = new Intent(this, PermissionActivity.class);
            } else /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){

                if (checkSelfPermission(ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    in = new Intent(this, PermissionActivity.class);
                } else {
                    in = new Intent(this, GypseeMainActivity.class);
                }

            } else */{
                in = new Intent(this, GypseeMainActivity.class);
            }

        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(in);
                finish();
            }
        }, 2000);

    }

    //WorkManager to show periodic notification if user exits app without logging in
    private void initWorkManager() {
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(LoginNotifService.class, 6, TimeUnit.HOURS)
                .addTag(Constants.logInRequestWorkerTag)
                .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                .build();
        mWorkManager = WorkManager.getInstance(this.getApplicationContext());

        mWorkManager.enqueue(
                periodicWorkRequest
        );
    }
}