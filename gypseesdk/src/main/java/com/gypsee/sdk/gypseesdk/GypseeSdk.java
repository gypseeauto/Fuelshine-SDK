package com.gypsee.sdk.gypseesdk;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.GypseeMainActivity;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.fragments.PermissionActivity;
import com.gypsee.sdk.helpers.BluetoothHelperClass;
import com.gypsee.sdk.jsonParser.AsyncTaskClass;
import com.gypsee.sdk.models.User;

public class GypseeSdk {

    public static void start(Context context, String userName, String password,String fcmToken) {
        final Intent in;


        if(userName.isEmpty()){
            Toast.makeText(context,"Please provide valid username",Toast.LENGTH_SHORT).show();
        }else if(password.isEmpty()){
            Toast.makeText(context,"Please provide valid password",Toast.LENGTH_SHORT).show();
        }else if(fcmToken.isEmpty()){
            Toast.makeText(context,"Please provide valid fcmToken",Toast.LENGTH_SHORT).show();
        }else{
           loginWithEmailPassword(context,userName,fcmToken,password);
        }
//        }else{
//            //Here checking the permissions. If all permissions are granted, we will go to MainActivity
//            //Else, we will go to permissions Activity
//            if (BluetoothHelperClass.fetchDeniedPermissions(context).length > 0 || !Settings.canDrawOverlays(context) ||
////                    !myPreferenece.getIfAccessibilityPermissionGranted() || //uncomment if accessibility permission is required
//                    !myPreferenece.getIfQueryAllPackagesPermissionGranted()) {
//                in = new Intent(context, PermissionActivity.class);
//            } else /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
//
//                if (checkSelfPermission(ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED){
//                    in = new Intent(this, PermissionActivity.class);
//                } else {
//                    in = new Intent(this, MainActivity.class);
//                }
//
//            } else */{
//                in = new Intent(context, MainActivity.class);
//            }
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    context.startActivity(in);
//                }
//            }, 2000);
//        }

    }

    private static void loginWithEmailPassword(Context context, String userName, String fcmToken, String password) {
        MyPreferenece myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);
        User user = myPreferenece.getUser();

//        if (user == null) {
        JSONObject jsonObject = new JSONObject();
//put something inside the map, could be null
        try {
            jsonObject.put("userName", userName);
            jsonObject.put("userPassword", password);
            jsonObject.put("fcmToken", fcmToken);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        new AsyncTaskClass(context, "StartSDK", 1, "Please wait",
                false, (Response, className, value) -> {
            try{
                JSONObject jsonResponse = new JSONObject(Response);

                String status = jsonResponse.getString("status");
                String message = jsonResponse.getString("message");
                if (status.equals("200")) {
                    JSONObject userJsonObject = jsonResponse.getJSONObject("user");

                    String userId, userName1, userFullName, userEmail, userPhoneNumber, userAccessToken, fcmToken1, userImg, userDeviceMac,
                            userTypes, referCode, createdOn, lastUpdatedOn, userAddresses;

                    boolean approved, locked, signUpBonusCredited, referCodeApplied;

                    JSONObject userWallet = userJsonObject.has("userWallet")? userJsonObject.getJSONObject("userWallet") : null;

                    int walletAmount;

                    userId = userJsonObject.has("userId") ? userJsonObject.getString("userId") : "";
                    userName1 = userJsonObject.has("userName") ? userJsonObject.getString("userName") : "";
                    userFullName = userJsonObject.has("userFullName") ? userJsonObject.getString("userFullName") : "";
                    userEmail = userJsonObject.has("userEmail") ? userJsonObject.getString("userEmail") : "";
                    userPhoneNumber = userJsonObject.has("userPhoneNumber") ? userJsonObject.getString("userPhoneNumber") : "";
                    userAccessToken = userJsonObject.has("userAccessToken") ? userJsonObject.getString("userAccessToken") : "";
                    fcmToken1 = userJsonObject.has("fcmToken") ? userJsonObject.getString("fcmToken") : "";
                    userImg = userJsonObject.has("userImg") ? userJsonObject.getString("userImg") : "";
                    userDeviceMac = userJsonObject.has("userDeviceMac") ? userJsonObject.getString("userDeviceMac") : "";
                    userTypes = userJsonObject.has("userTypes") ? userJsonObject.getString("userTypes") : "";
                    referCode = userJsonObject.has("referCode") ? userJsonObject.getString("referCode") : "";
                    createdOn = userJsonObject.has("createdOn") ? userJsonObject.getString("createdOn") : "";
                    lastUpdatedOn = userJsonObject.has("lastUpdatedOn") ? userJsonObject.getString("lastUpdatedOn") : "";
                    userAddresses = userJsonObject.has("userAddresses") ? userJsonObject.getString("userAddresses") : "";
                    approved = userJsonObject.has("approved") && userJsonObject.getBoolean("approved");
                    locked = userJsonObject.has("locked") && userJsonObject.getBoolean("locked");
                    signUpBonusCredited = userJsonObject.has("signUpBonusCredited") && userJsonObject.getBoolean("signUpBonusCredited");
                    referCodeApplied = userJsonObject.has("referCodeApplied") && userJsonObject.getBoolean("referCodeApplied");

                    walletAmount = (userWallet != null) ? (userWallet.has("loyaltyPoints") ? userWallet.getInt("loyaltyPoints") : 0) : 0;

                    myPreferenece.storeUser(new User(userId, userName1, userFullName, userEmail, userPhoneNumber, userAccessToken, fcmToken1, userImg, userDeviceMac,
                            userTypes, referCode, createdOn, lastUpdatedOn, userAddresses, approved, locked, signUpBonusCredited, referCodeApplied, false, String.valueOf(walletAmount)));
                    //updating inTraining mode in HomeFragment on fresh install
                    Intent in1;
                    if (BluetoothHelperClass.fetchDeniedPermissions(context).length > 0 || !Settings.canDrawOverlays(context) ||
//                    !myPreferenece.getIfAccessibilityPermissionGranted() || //uncomment if accessibility permission is required
                            !myPreferenece.getIfQueryAllPackagesPermissionGranted()) {
                        in1 = new Intent(context, PermissionActivity.class);
                    }else {
                        in1 = new Intent(context, GypseeMainActivity.class);
                    }

                    in1.putExtra("freshlogin", true);
                    in1.putExtra("isNewUser", false);
                    context.startActivity(in1);
                }else {
                    registerWithEmailPassword(context,userName,fcmToken,password);
                    //Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){

            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject.toString(),
                "regerror", context.getResources().getString(R.string.mobileLoginAPi), "Login with username and possword");
    }




    private static void registerWithEmailPassword(Context context, String userName, String fcmToken, String password) {
        MyPreferenece myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);
        User user = myPreferenece.getUser();

//        if (user == null) {
        JSONObject jsonObject = new JSONObject();
//put something inside the map, could be null
        try {
            jsonObject.put("userName", userName);
            jsonObject.put("userPassword", password);
            jsonObject.put("userEmail", userName);
            jsonObject.put("fcmToken", fcmToken);
            long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;

            jsonObject.put("userPhoneNumber", "+91"+number);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        new AsyncTaskClass(context, "Register SDK", 1, "Please wait",
                false, (Response, className, value) -> {
            try{
                JSONObject jsonResponse = new JSONObject(Response);

                String status = jsonResponse.getString("status");
                String message = jsonResponse.getString("message");
                if (status.equals("200")) {
                    JSONObject userJsonObject = jsonResponse.getJSONObject("user");

                    String userId, userName1, userFullName, userEmail, userPhoneNumber, userAccessToken, fcmToken1, userImg, userDeviceMac,
                            userTypes, referCode, createdOn, lastUpdatedOn, userAddresses;

                    boolean approved, locked, signUpBonusCredited, referCodeApplied;

                    JSONObject userWallet = userJsonObject.has("userWallet")? userJsonObject.getJSONObject("userWallet") : null;

                    int walletAmount;

                    userId = userJsonObject.has("userId") ? userJsonObject.getString("userId") : "";
                    userName1 = userJsonObject.has("userName") ? userJsonObject.getString("userName") : "";
                    userFullName = userJsonObject.has("userFullName") ? userJsonObject.getString("userFullName") : "";
                    userEmail = userJsonObject.has("userEmail") ? userJsonObject.getString("userEmail") : "";
                    userPhoneNumber = userJsonObject.has("userPhoneNumber") ? userJsonObject.getString("userPhoneNumber") : "";
                    userAccessToken = userJsonObject.has("userAccessToken") ? userJsonObject.getString("userAccessToken") : "";
                    fcmToken1 = userJsonObject.has("fcmToken") ? userJsonObject.getString("fcmToken") : "";
                    userImg = userJsonObject.has("userImg") ? userJsonObject.getString("userImg") : "";
                    userDeviceMac = userJsonObject.has("userDeviceMac") ? userJsonObject.getString("userDeviceMac") : "";
                    userTypes = userJsonObject.has("userTypes") ? userJsonObject.getString("userTypes") : "";
                    referCode = userJsonObject.has("referCode") ? userJsonObject.getString("referCode") : "";
                    createdOn = userJsonObject.has("createdOn") ? userJsonObject.getString("createdOn") : "";
                    lastUpdatedOn = userJsonObject.has("lastUpdatedOn") ? userJsonObject.getString("lastUpdatedOn") : "";
                    userAddresses = userJsonObject.has("userAddresses") ? userJsonObject.getString("userAddresses") : "";
                    approved = userJsonObject.has("approved") && userJsonObject.getBoolean("approved");
                    locked = userJsonObject.has("locked") && userJsonObject.getBoolean("locked");
                    signUpBonusCredited = userJsonObject.has("signUpBonusCredited") && userJsonObject.getBoolean("signUpBonusCredited");
                    referCodeApplied = userJsonObject.has("referCodeApplied") && userJsonObject.getBoolean("referCodeApplied");

                    walletAmount = (userWallet != null) ? (userWallet.has("loyaltyPoints") ? userWallet.getInt("loyaltyPoints") : 0) : 0;

                    myPreferenece.storeUser(new User(userId, userName1, userFullName, userEmail, userPhoneNumber, userAccessToken, fcmToken1, userImg, userDeviceMac,
                            userTypes, referCode, createdOn, lastUpdatedOn, userAddresses, approved, locked, signUpBonusCredited, referCodeApplied, false, String.valueOf(walletAmount)));
                    //updating inTraining mode in HomeFragment on fresh install
                    Intent in1;
                    if (BluetoothHelperClass.fetchDeniedPermissions(context).length > 0 || !Settings.canDrawOverlays(context) ||
//                    !myPreferenece.getIfAccessibilityPermissionGranted() || //uncomment if accessibility permission is required
                            !myPreferenece.getIfQueryAllPackagesPermissionGranted()) {
                        in1 = new Intent(context, PermissionActivity.class);
                    }else {
                        in1 = new Intent(context, GypseeMainActivity.class);
                    }

                    in1.putExtra("freshlogin", true);
                    in1.putExtra("isNewUser", false);
                    context.startActivity(in1);
                }else {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){

            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject.toString(),
                "regerror", context.getResources().getString(R.string.registerApi), "Register with username and password");
    }
}