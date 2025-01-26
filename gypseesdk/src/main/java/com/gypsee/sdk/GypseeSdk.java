package com.gypsee.sdk;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import com.gypsee.sdk.activities.GypseeMainActivity;
import com.gypsee.sdk.activities.SplashActivity;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.fragments.PermissionActivity;
import com.gypsee.sdk.helpers.BluetoothHelperClass;
import com.gypsee.sdk.jsonParser.AsyncTaskClass;
import com.gypsee.sdk.models.User;

import java.util.Objects;

public class GypseeSdk {

    public static void start(Context context, String userName, String password,String fcmToken) {
        final Intent in;

        //If all the values are emmpty, it will start splash screen. It is Directly our fuel shine app
        if(userName.isEmpty() &&password.isEmpty()&&fcmToken.isEmpty()){
            in = new Intent(context, SplashActivity.class);
         context.startActivity(in);
        }
        else if(userName.isEmpty()){
            Toast.makeText(context,"Please provide valid username",Toast.LENGTH_SHORT).show();
        }else if(password.isEmpty()){
            Toast.makeText(context,"Please provide valid password",Toast.LENGTH_SHORT).show();
        }else if(fcmToken.isEmpty()){
            Toast.makeText(context,"Please provide valid fcmToken",Toast.LENGTH_SHORT).show();
        }else{
           loginWithEmailPassword(context,userName,fcmToken,password);
        }

    }

    /**
     * Login with user email and password & go to main page
     * @param context content
     * @param userName userName
     * @param fcmToken  fcmToken
     * @param password password
     */

    private static void loginWithEmailPassword(Context context, String userName, String fcmToken, String password) {
        MyPreferenece myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);
        User user = myPreferenece.getUser();

        if (user != null && Objects.equals(user.getUserEmail(), userName)) {
            checkPermissionsAndNavigate(context,myPreferenece);
            return;
        }
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

                    parseUserJson(jsonResponse,context,myPreferenece);

                    checkPermissionsAndNavigate(context,myPreferenece);
                }else {
                    registerWithEmailPassword(context,userName,fcmToken,password);
                    //Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject.toString(),
                "regerror", context.getResources().getString(R.string.mobileLoginAPi), "Login with username and possword");
    }

    private static void checkPermissionsAndNavigate(Context context, MyPreferenece myPreferenece) {

        //updating inTraining mode in HomeFragment on fresh install
        Intent in1;
        if (BluetoothHelperClass.fetchDeniedPermissions(context).length > 0 || !Settings.canDrawOverlays(context) ||
//                    !myPreferenece.getIfAccessibilityPermissionGranted() || //uncomment if accessibility permission is required
                !myPreferenece.getIfQueryAllPackagesPermissionGranted()) {
            in1 = new Intent(context, PermissionActivity.class);
        }else {
            in1 = new Intent(context, GypseeMainActivity.class);
        }
        Log.e("TAG", "checkPermissionsAndNavigate: ");
        in1.putExtra("freshlogin", true);
        in1.putExtra("isNewUser", false);
        context.startActivity(in1);
    }

    /**
     * Register with username and password & go to permission page or main activity
     * @param context
     * @param userName
     * @param fcmToken
     * @param password
     */
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

            jsonObject.put("userPhoneNumber", userName);


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

                    parseUserJson(jsonResponse,context,myPreferenece);
                    //updating inTraining mode in HomeFragment on fresh install
                    checkPermissionsAndNavigate(context,myPreferenece);

                }else {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){

            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject.toString(),
                "regerror", context.getResources().getString(R.string.registerApi), "Register with username and password");
    }

    private static void parseUserJson(JSONObject jsonResponse, Context context, MyPreferenece myPreferenece) throws JSONException {

        JSONObject userJsonObject = jsonResponse.getJSONObject("user");

        String userId, userName1, userFullName, userEmail, userPhoneNumber, userAccessToken, fcmToken1, userImg, userDeviceMac,
                userTypes, referCode, createdOn, lastUpdatedOn;

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
        approved = userJsonObject.has("approved") && userJsonObject.getBoolean("approved");
        locked = userJsonObject.has("locked") && userJsonObject.getBoolean("locked");
        signUpBonusCredited = userJsonObject.has("signUpBonusCredited") && userJsonObject.getBoolean("signUpBonusCredited");
        referCodeApplied = userJsonObject.has("referCodeApplied") && userJsonObject.getBoolean("referCodeApplied");

        walletAmount = (userWallet != null) ? (userWallet.has("loyaltyPoints") ? userWallet.getInt("loyaltyPoints") : 0) : 0;

        myPreferenece.storeUser(new User(userId, userName1, userFullName, userEmail, userPhoneNumber, userAccessToken, fcmToken1, userImg, userDeviceMac,
                userTypes, referCode, createdOn, lastUpdatedOn, approved, locked, signUpBonusCredited, referCodeApplied, false, String.valueOf(walletAmount)));
    }

    public static String googleClientId;

     public static void setClientId(String clientId) {
      googleClientId = clientId;
    }
}