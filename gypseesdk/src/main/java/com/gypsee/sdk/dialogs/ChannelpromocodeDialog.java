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

import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.databinding.ChannelPromoCodeBinding;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;
import com.gypsee.sdk.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChannelpromocodeDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = ChannelpromocodeDialog.class.getSimpleName();
    private Context context;


    public ChannelpromocodeDialog(Context context) {
        super(context);
        this.context = context;
    }


    ChannelPromoCodeBinding channelPromoCodeBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        channelPromoCodeBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.channel_promo_code, null, false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(channelPromoCodeBinding.getRoot());
        getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

        channelPromoCodeBinding.positioveBtn.setOnClickListener(this);
        channelPromoCodeBinding.negativeBtn.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.positioveBtn) {
            if (channelPromoCodeBinding.referralCodeEt.getText().toString().length() < 2) {
                Toast.makeText(context, "Please enter valid promocode ", Toast.LENGTH_LONG).show();
            } else {

                callServer(context.getString(R.string.verifyCouponUrl), "Verify Coupon", 0);
            }
            Utils.hideKeyboardFrom(context, channelPromoCodeBinding.getRoot());
        } else if (id == R.id.negativeBtn) {
            dismiss();
        }
    }

    private void callServer(String url, String purpose, final int value) {

        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);

        Call<ResponseBody> call;
        JsonObject jsonInput = new JsonObject();

        MyPreferenece myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);

        User user = myPreferenece.getUser();

        switch (value) {
            case 0:
                hideProgressBar(false);

                jsonInput.addProperty("couponCode", channelPromoCodeBinding.referralCodeEt.getText().toString());
                jsonInput.addProperty("mobileNumber", user.getUserPhoneNumber());
                Log.e(TAG, purpose + " Input is : " + jsonInput.toString());
                call = apiService.uploadVehDetails(user.getUserAccessToken(), jsonInput);
                break;

            case 1:
                call = apiService.getUser(user.getUserAccessToken(), true);
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
                                Toast.makeText(context, "Channel code applies succesflly", Toast.LENGTH_LONG).show();
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
            channelPromoCodeBinding.progressBar.setVisibility(View.GONE);
            channelPromoCodeBinding.bottomLinearLayout.setVisibility(View.VISIBLE);
        } else {
            channelPromoCodeBinding.progressBar.setVisibility(View.VISIBLE);
            channelPromoCodeBinding.bottomLinearLayout.setVisibility(View.GONE);
        }
    }



    private void parseLoginRegisterResponse(String response) throws JSONException {
        JSONObject jsonResponse = new JSONObject(response);

        //Go to main Activity

        // ArrayList<String> values = new ArrayList<>();
        JSONObject userJsonObject = jsonResponse.getJSONObject("user");

        String userId, userName, userFullName, userEmail, userPhoneNumber, userAccessToken, fcmToken, userImg, userDeviceMac,
                userTypes, referCode, createdOn, lastUpdatedOn, userAddresses;

        boolean approved, locked, signUpBonusCredited, referCodeApplied;

        JSONObject userWallet = userJsonObject.has("userWallet")? userJsonObject.getJSONObject("userWallet") : null;

        int walletAmount;

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

        boolean inTrainingMode = userJsonObject.has("inTrainingMode") && userJsonObject.getBoolean("inTrainingMode");

        walletAmount = (userWallet != null) ? (userWallet.has("loyaltyPoints") ? userWallet.getInt("loyaltyPoints") : 0) : 0;

        Log.e(TAG, "inTrainingMode: " + inTrainingMode);

        MyPreferenece myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);
        User user = new User(userId, userName, userFullName, userEmail, userPhoneNumber, userAccessToken, fcmToken, userImg, userDeviceMac,
                userTypes, referCode, createdOn, lastUpdatedOn, approved, locked, signUpBonusCredited, referCodeApplied, inTrainingMode, String.valueOf(walletAmount));
        myPreferenece.storeUser(user);

        restartMainActivity();

    }

    private void restartMainActivity(){
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("restart MainActivity"));
    }



}
