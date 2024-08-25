package com.gypsee.sdk.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

public class LocationUtils {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean isLowPrecisionLocationEnabled(Context context) {
        int locationMode = 0;

        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        // Location mode constants:
        // 0 = LOCATION_MODE_OFF
        // 1 = LOCATION_MODE_SENSORS_ONLY (Device Only)
        // 2 = LOCATION_MODE_BATTERY_SAVING
        // 3 = LOCATION_MODE_HIGH_ACCURACY

        return locationMode == Settings.Secure.LOCATION_MODE_BATTERY_SAVING
                || locationMode == Settings.Secure.LOCATION_MODE_SENSORS_ONLY;
    }
}

