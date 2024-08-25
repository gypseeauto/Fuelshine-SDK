package com.gypsee.sdk.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.databinding.LayoutSubscribeDialogBinding;
import com.gypsee.sdk.models.SubscriptionModel;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;
import com.gypsee.sdk.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscribeDialog extends Dialog {

    private Context context;

    public SubscribeDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    LayoutSubscribeDialogBinding layoutSubscribeDialogBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layoutSubscribeDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_subscribe_dialog, null, false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(layoutSubscribeDialogBinding.getRoot());
        getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

        layoutSubscribeDialogBinding.subscribeNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (layoutSubscribeDialogBinding.referralCodeEt.getText().toString().length() == 0){
                    Toast.makeText(context, "Please enter valid code", Toast.LENGTH_SHORT).show();
                } else {
                    callServer(context.getString(R.string.subscriptionAdd), "Subscription Add", 0);
                }

                Utils.hideKeyboardFrom(context, layoutSubscribeDialogBinding.getRoot());

            }
        });

    }

    private String TAG = SubscribeDialog.class.getSimpleName();

    private void callServer(String url, String purpose, final int value) {

        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);

        Call<ResponseBody> call;
        JsonObject jsonInput = new JsonObject();

        MyPreferenece myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);

        User user = myPreferenece.getUser();

        switch (value) {
            case 0:
                hideProgressBar(false);

                if (myPreferenece.getAvailableSubscriptionId().equals("")){
                    hideProgressBar(true);
                    Toast.makeText(context, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                    dismiss();
                    return;
                }

                jsonInput.addProperty("couponCode", layoutSubscribeDialogBinding.referralCodeEt.getText().toString());
                jsonInput.addProperty("subscriptionId", myPreferenece.getAvailableSubscriptionId());

                Log.e(TAG, purpose + " Input is : " + jsonInput.toString());

                call = apiService.uploadVehDetails(user.getUserAccessToken(), jsonInput);
                break;

            case 1:
                call = apiService.getUserSubscription(user.getUserAccessToken(), true, true);
                break;

            default:
                call = apiService.getDocTypes(user.getUserAccessToken(), true);
                break;
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                hideProgressBar(true);
                try {
                    if (response.isSuccessful()) {
                        Log.e(TAG, "Response is success");
                        String responseStr = response.body().string();
                        Log.e(TAG, "Response :" + responseStr);

                        switch (value) {
                            case 0:
                                Toast.makeText(context, "Subscribed!", Toast.LENGTH_SHORT).show();
                                callServer(context.getResources().getString(R.string.Fetch_UserDetils_url).replace("userid", user.getUserId()), "Fetch user data", 1);
                                // parseDeleteCar(responseStr);
                                break;

                            case 1:
                                parseLoginRegisterResponse(responseStr);
                                dismiss();
                                break;
                        }

                    } else {
                        Log.e(TAG, "Response is not succesfull");
                        String errorBody = response.errorBody().string();

                        int responseCode = response.code();
                        Log.e(TAG, " Response :" + responseCode + errorBody);

                        if (responseCode == 401 || responseCode == 403) {
                            //include logout functionality
                            Utils.clearAllData(context);
                            return;
                        }

                        if (response.errorBody() == null) {
                        } else {
                            JSONObject jsonObject = new JSONObject(errorBody);
                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            Log.e("Error code " + response, "" + "Response is not empty");
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //Logger.ErrorLog(TAG, "error here since request failed");

                // progressLayout.setVisibility(View.GONE);


                if (t.getMessage().contains("Unable to resolve host")) {
                    Toast.makeText(context, "Please Check your internet connection", Toast.LENGTH_LONG).show();
                }

                if (call.isCanceled()) {

                } else {

                }

                switch (value) {
                    case 0:
                    case 1:
                        hideProgressBar(true);
                        break;
                }
            }
        });
    }

    private void hideProgressBar(boolean b) {

        if (b) {
            layoutSubscribeDialogBinding.progressBar.setVisibility(View.GONE);
            layoutSubscribeDialogBinding.subscribeNow.setVisibility(View.VISIBLE);
        } else {
            layoutSubscribeDialogBinding.progressBar.setVisibility(View.VISIBLE);
            layoutSubscribeDialogBinding.subscribeNow.setVisibility(View.GONE);
        }
    }

    private void restartMainActivity(){
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("restart MainActivity"));
    }

    MyPreferenece myPreferenece;


    private void parseLoginRegisterResponse(String response) throws JSONException {
        JSONObject jsonResponse = new JSONObject(response);

        //Go to main Activity

        // ArrayList<String> values = new ArrayList<>();
        JSONObject userJsonObject = jsonResponse.getJSONObject("user");

        String userId, userName, userFullName, userEmail, userPhoneNumber, userAccessToken, fcmToken, userImg, userDeviceMac,
                userTypes, referCode, createdOn, lastUpdatedOn, userAddresses;

        JSONObject userWallet = userJsonObject.has("userWallet")? userJsonObject.getJSONObject("userWallet") : null;

        int walletAmount;

        boolean approved, locked, signUpBonusCredited, referCodeApplied;

        userId = userJsonObject.has("userId") ? userJsonObject.getString("userId") : "";
        userName = userJsonObject.has("userName") ? userJsonObject.getString("userName") : "";
        userFullName = userJsonObject.has("userFullName") ? userJsonObject.getString("userFullName") : "";
        userEmail = userJsonObject.has("userEmail") ? userJsonObject.getString("userEmail") : "";
        userPhoneNumber = userJsonObject.has("userPhoneNumber") ? userJsonObject.getString("userPhoneNumber") : "";
        userAccessToken = userJsonObject.has("userAccessToken") ? userJsonObject.getString("userAccessToken") : "";
        fcmToken = userJsonObject.has("fcmToken") ? userJsonObject.getString("fcmToken") : "";
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

        SubscriptionModel subscriptionModel = null;

        JSONObject subJsonObject;
        try {
            subJsonObject = userJsonObject.getJSONObject("userSubscriptions");
        } catch (Exception e){
            e.printStackTrace();
            subJsonObject = null;
        }
        if(subJsonObject != null){

            boolean active = subJsonObject.has("active") && subJsonObject.getBoolean("active");
            String couponCode = subJsonObject.has("couponCode") ? subJsonObject.getString("couponCode") : "";
            String endDate = subJsonObject.has("endDate") ? subJsonObject.getString("endDate") : "";
            String id = subJsonObject.has("id") ? subJsonObject.getString("id") : "";
            String subLastUpdatedOn = subJsonObject.has("lastUpdatedOn") ? subJsonObject.getString("lastUpdatedOn") : "";
            String startDate = subJsonObject.has("startDate") ? subJsonObject.getString("startDate") : "";
            double paidAmount = subJsonObject.has("paidAmount") ? subJsonObject.getDouble("couponCode") : 0;
            double subscriptionAmount = subJsonObject.has("subscriptionAmount") ? subJsonObject.getDouble("subscriptionAmount") : 0;
            double discountAmount = subJsonObject.has("discountAmount") ? subJsonObject.getDouble("discountAmount") : 0;

            if (!id.equals("")) {

                subscriptionModel = new SubscriptionModel(
                        active,
                        couponCode,
                        createdOn,
                        endDate,
                        id,
                        subLastUpdatedOn,
                        startDate,
                        discountAmount,
                        subscriptionAmount,
                        paidAmount
                );
            }

        }


        Log.e(TAG, "wallet: " + userWallet);

        walletAmount = (userWallet != null) ? (userWallet.has("loyaltyPoints") ? userWallet.getInt("loyaltyPoints") : 0) : 0;

        boolean inTrainingMode = userJsonObject.has("inTrainingMode") && userJsonObject.getBoolean("inTrainingMode");

        Log.e(TAG, "inTrainingMode: " + inTrainingMode);

        myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);
        User user = new User(userId, userName, userFullName, userEmail, userPhoneNumber, userAccessToken, fcmToken, userImg, userDeviceMac,
                userTypes, referCode, createdOn, lastUpdatedOn, userAddresses, approved, locked, signUpBonusCredited, referCodeApplied, inTrainingMode, String.valueOf(walletAmount));
        if (subscriptionModel != null){
            user.setUserSubscriptions(subscriptionModel);
        }

        myPreferenece.storeUser(user);

        restartMainActivity();


    }


}
