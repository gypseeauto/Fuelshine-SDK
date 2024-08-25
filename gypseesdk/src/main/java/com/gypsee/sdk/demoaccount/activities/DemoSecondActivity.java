package com.gypsee.sdk.demoaccount.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.HashMap;

import com.gypsee.sdk.R;
import com.gypsee.sdk.demoaccount.fragments.DemoCarScanFragment;
import com.gypsee.sdk.demoaccount.fragments.DemoMyCarsListFragment;
import com.gypsee.sdk.demoaccount.fragments.DemoMyDevicesFragment;
import com.gypsee.sdk.demoaccount.fragments.DemoNewVehiclePerformanceFragment;
import com.gypsee.sdk.demoaccount.fragments.DemoSafetyCenterFragment;
import com.gypsee.sdk.fragments.CarScanResultFragment;
import com.gypsee.sdk.fragments.DemoStoreMainFragment;
import com.gypsee.sdk.fragments.RSATermsAndConditionsFragment;
import com.gypsee.sdk.fragments.StoreMainFragment;

public class DemoSecondActivity extends AppCompatActivity {

    String TAG = DemoSecondActivity.class.getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        setFragmentBasedonIntent();


    }


    private void setFragmentBasedonIntent() {

        Intent in = getIntent();

        if (!in.hasExtra("TAG")) {
            finish();
            return;
        }
        switch (in.getStringExtra("TAG")) {
           /* case "ReferAndEarnFragment":
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, new ReferAndEarnFragment(), ReferAndEarnFragment.class.getSimpleName())
                        .commit();

                break;*/

            case "RSATermsAndConditionsFragment":
                Fragment rsaTermsAndConditionsFragment = new RSATermsAndConditionsFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, rsaTermsAndConditionsFragment, RSATermsAndConditionsFragment.class.getSimpleName())
                        .commit();
                break;

            case "MyCarsListFragment":
                DemoMyCarsListFragment myCarsListFragment = DemoMyCarsListFragment.newInstance();
                //My cars Fragment should show.
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, myCarsListFragment, DemoMyCarsListFragment.class.getSimpleName())
                        .commit();
                break;

            /*case "TroubleCodeDescriptionFragment":
                Log.e(TAG, "Troublecode :" + getIntent().getStringExtra("Troublecode"));

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, TroubleCodeDescriptionFragment.newInstance(getIntent().getStringExtra("Troublecode")), TroubleCodeDescriptionFragment.class.getSimpleName())
                        .commit();
                break;*/

            /*case "AddVehicleFragment":

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
                break;*/

            case "PerformanceFragment":
                Fragment performanceFragment = new DemoNewVehiclePerformanceFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, performanceFragment, DemoNewVehiclePerformanceFragment.class.getSimpleName())
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

            /*case "ServiceConfirmationFragment":

                Fragment serviceConfirmationFragment = ServiceConfirmationFragment.newInstance("", "");
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, serviceConfirmationFragment, ServiceConfirmationFragment.class.getSimpleName())
                        .commit();
                break;*/

            case "DemoCarScanFragment":
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.mainFrameLayout, DemoCarScanFragment.newInstance("", ""), DemoCarScanFragment.class.getSimpleName())
                        .commit();
                break;

            case "DemoSafetyCenterFragment":
                Fragment safetyCenterFragment = DemoSafetyCenterFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, safetyCenterFragment, DemoSafetyCenterFragment.class.getSimpleName())
                        .commit();
                break;

            case "MyDevicesFragment":
                Fragment myDevicesFragment = new DemoMyDevicesFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, myDevicesFragment, DemoMyDevicesFragment.class.getSimpleName())
                        .commit();
                break;

            case "DemoStoreMainFragment":
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.mainFrameLayout, new DemoStoreMainFragment(), StoreMainFragment.class.getSimpleName())
                        .commit();
                break;

            default:
                finish();
                break;
        }

    }


}
