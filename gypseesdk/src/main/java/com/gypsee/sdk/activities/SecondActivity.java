package com.gypsee.sdk.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;

import com.gypsee.sdk.R;
import com.gypsee.sdk.fragments.AddVehicleFragment;
import com.gypsee.sdk.fragments.CarScanFragment;
import com.gypsee.sdk.fragments.CarScanResultFragment;
import com.gypsee.sdk.fragments.MyCarsListFragment;
import com.gypsee.sdk.fragments.MyDevicesFragment;
import com.gypsee.sdk.fragments.MyOrdersFragment;
import com.gypsee.sdk.fragments.NewVehiclePerformanceFragment;
import com.gypsee.sdk.fragments.PerformanceFragment;
import com.gypsee.sdk.fragments.RSATermsAndConditionsFragment;
import com.gypsee.sdk.fragments.ReferAndEarnFragment;
import com.gypsee.sdk.fragments.SafetyCenterFragment;
import com.gypsee.sdk.fragments.ServiceConfirmationFragment;
import com.gypsee.sdk.fragments.StoreMainFragment;
import com.gypsee.sdk.fragments.TroubleCodeDescriptionFragment;
import com.gypsee.sdk.fragments.UpdateCarFragment;
import com.gypsee.sdk.fragments.WebViewFragment;
import com.gypsee.sdk.models.Vehiclemodel;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Log.e("Hello", "Came to second Activity " + getIntent().getStringExtra("TAG"));
        setFragmentBasedonIntent();
    }


    /*
    * *Customer journey and automated Notifications:*
    *
    * *case 3: Pair bluetooth device* -> app://gypsee-second/MyDevicesFragment
    * *case 4: Add car* -> app://gypsee-second/MyCarsListFragment
    * *case 5: Add member in safety center* -> app://gypsee-second/SafetyCenterFragment
    * *case 6: drive mode* -> welcome message already implemented
    * *case 7: gypsee coins* -> app://gypsee-second/StoreMainFragment
    *
    *
    *
    * *In-app promotion and notification
    *
    * welcome text to speech is already implemented
    *
    * */



    private void setFragmentBasedonIntent() {

        Intent in = getIntent();

        Uri  data = in.getData();
        String key1 = null;
        if(data != null) {
            key1 = data.getQueryParameter("key");
        }



        String fragmentName;

        if (!in.hasExtra("TAG") && key1 == null) {
            finish();
            return;
        }

        if (key1 != null){
            fragmentName = key1;
        } else {
            fragmentName = in.getStringExtra("TAG");
        }

        switch (fragmentName) {
            case "ReferAndEarnFragment":
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, new ReferAndEarnFragment(), ReferAndEarnFragment.class.getSimpleName())
                        .commit();

                break;

            case "RSATermsAndConditionsFragment":
                Fragment rsaTermsAndConditionsFragment = new RSATermsAndConditionsFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, rsaTermsAndConditionsFragment, RSATermsAndConditionsFragment.class.getSimpleName())
                        .commit();
                break;

            case "MyCarsListFragment":
                MyCarsListFragment myCarsListFragment = MyCarsListFragment.newInstance((Vehiclemodel) getIntent().getParcelableExtra(Vehiclemodel.class.getSimpleName()));
                //My cars Fragment should show.
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, myCarsListFragment, MyCarsListFragment.class.getSimpleName())
                        .commit();
                break;

            case "TroubleCodeDescriptionFragment":
                Log.e(TAG, "Troublecode :" + getIntent().getStringExtra("Troublecode"));

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, TroubleCodeDescriptionFragment.newInstance(getIntent().getStringExtra("Troublecode")), TroubleCodeDescriptionFragment.class.getSimpleName())
                        .commit();
                break;

            case "AddVehicleFragment":

                Vehiclemodel vehiclemodel = (Vehiclemodel) getIntent().getParcelableExtra(Vehiclemodel.class.getSimpleName());
                if (vehiclemodel == null) {
                    AddVehicleFragment addVehicleFragment = AddVehicleFragment.newInstance(vehiclemodel,
                            getIntent().getBooleanExtra("isServiceDone", false)
                    );
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.mainFrameLayout, addVehicleFragment, AddVehicleFragment.class.getSimpleName())
                            .commit();
                } else {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.mainFrameLayout, new UpdateCarFragment(), AddVehicleFragment.class.getSimpleName())
                            .commit();
                }
                break;

            case "PerformanceFragment":
                Fragment performanceFragment = new NewVehiclePerformanceFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, performanceFragment, PerformanceFragment.class.getSimpleName())
                        .commit();
                break;
            case "CarScanResultFragment":
                HashMap<String, String> scanResult = (HashMap<String, String>) getIntent().getSerializableExtra("TroubleCodes");
                Fragment carScanResultFragment = CarScanResultFragment.newInstance(scanResult, "");
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, carScanResultFragment, CarScanResultFragment.class.getSimpleName())
                        .commit();
                break;
            case "ServiceConfirmationFragment":

                Fragment serviceConfirmationFragment = ServiceConfirmationFragment.newInstance("", "");
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, serviceConfirmationFragment, ServiceConfirmationFragment.class.getSimpleName())
                        .commit();
                break;

            case "MyDevicesFragment":

                Fragment myDevicesFragment = new MyDevicesFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, myDevicesFragment, MyDevicesFragment.class.getSimpleName())
                        .commit();
                break;

            case "SafetyCenterFragment":

                Fragment safetyCenterFragment = SafetyCenterFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, safetyCenterFragment, SafetyCenterFragment.class.getSimpleName())
                        .commit();
                break;

            case "CarScanFragment":
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.mainFrameLayout, CarScanFragment.newInstance("", ""), CarScanFragment.class.getSimpleName())
                        .commit();
                break;

            case "StoreMainFragment":
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.mainFrameLayout, new StoreMainFragment(), StoreMainFragment.class.getSimpleName())
                        .commit();
                break;

            case "WebViewFragment":

                WebViewFragment webViewFragment = new WebViewFragment("Driving Report");

                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.mainFrameLayout, webViewFragment, WebViewFragment.class.getSimpleName())
                        .commit();


                break;


            case "MyOrdersFragment":

                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.mainFrameLayout, new MyOrdersFragment(), MyOrdersFragment.class.getSimpleName())
                        .commit();
                break;


            default:
                finish();
                break;
        }

    }

    String TAG = SecondActivity.class.getSimpleName();
}


