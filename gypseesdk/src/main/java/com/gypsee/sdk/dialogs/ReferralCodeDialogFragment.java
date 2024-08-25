package com.gypsee.sdk.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;

import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.databinding.ReferralCodeDialogLayoutBinding;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;
import com.gypsee.sdk.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gypsee.sdk.config.MyPreferenece.GYPSEE_PREFERENCES;

public class ReferralCodeDialogFragment extends Dialog implements View.OnClickListener {

    Context context;

    public ReferralCodeDialogFragment(@NonNull Context context) {
        super(context);
        this.context = context;
    }


    ReferralCodeDialogLayoutBinding referralCodeDialogLayoutBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        referralCodeDialogLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.referral_code_dialog_layout, null, false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(referralCodeDialogLayoutBinding.getRoot());
        getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        referralCodeDialogLayoutBinding.positioveBtn.setOnClickListener(this);
        referralCodeDialogLayoutBinding.negativeBtn.setOnClickListener(this);
        setWindowAttributes();
    }

    private void setWindowAttributes() {

        Window callDialogWindow = getWindow();
        // TO set the
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        callDialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.copyFrom(callDialogWindow.getAttributes());
        params.gravity = Gravity.CENTER;
        callDialogWindow.setAttributes(params);
        getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.positioveBtn) {
            String referralCode = referralCodeDialogLayoutBinding.referralCodeEt.getText().toString();
            if (referralCode.length() < 4) {
                Toast.makeText(context, "Please enter a valid code", Toast.LENGTH_LONG).show();
            } else {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("referCode", referralCode);
                Utils.hideKeyboardFrom(context, referralCodeDialogLayoutBinding.getRoot());
                showHideReferCodeViews(View.GONE, View.VISIBLE);
                callServer(jsonObject, 0, "Referral code");
            }
        } else if (id == R.id.negativeBtn) {
            dismiss();
        }
    }

    private void showHideReferCodeViews(int visibility1, int visibility2) {

        referralCodeDialogLayoutBinding.positioveBtn.setVisibility(visibility1);
        referralCodeDialogLayoutBinding.negativeBtn.setVisibility(visibility1);
        referralCodeDialogLayoutBinding.lineView2.setVisibility(visibility1);
        referralCodeDialogLayoutBinding.progressBar.setVisibility(visibility2);
    }

    String TAG = ReferralCodeDialogFragment.class.getSimpleName();

    private void callServer(JsonObject jsonObject, final int value, final String purpose) {
        ApiInterface apiService = ApiClient.getRetrofitInstance(context).create(ApiInterface.class);
        MyPreferenece myPreferenece = new MyPreferenece(GYPSEE_PREFERENCES, context);
        User user = myPreferenece.getUser();
        Call<ResponseBody> call;
        Log.e(TAG, purpose + " input : " + jsonObject.toString());

        switch (value) {

            case 0:
                call = apiService.uploadReferralCode(user.getUserAccessToken(), jsonObject, user.getUserId());

                break;

            default:
                call = apiService.updateFcmToken(user.getUserAccessToken(), jsonObject, user.getUserId());

        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (response.isSuccessful()) {
                        Log.e(TAG, " Response is success");

                        String responseStr = response.body().string();
                        Log.e(TAG, purpose + " Response :" + responseStr);

                        switch (value) {

                            case 0:
                                parseAddReferCodeResponse(responseStr);

                                break;
                        }

                    } else {
                        Log.e(TAG, "Response is not succesful");

                        String errorResponse = response.errorBody().string();

                        Log.e("Error Response", errorResponse);

                        int responseCode = response.code();
                        if (responseCode == 401 || responseCode == 403) {
                            //include logout functionality
                            Utils.clearAllData(context);
                        } else {
                            // we need to show message to the user. response.errorBody()
                            //{"status":400,"message":"Token Expired","totalRecords":0}

                            JSONObject errorjson = new JSONObject(errorResponse);
                            if (errorjson.has("message"))
                                Toast.makeText(context, errorjson.getString("message"), Toast.LENGTH_LONG).show();
                            showHideReferCodeViews(View.VISIBLE, View.GONE);

                        }


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //Logger.ErrorLog(TAG, "error here since request failed");
                if (t instanceof UnknownHostException) {
                    Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "An unknow error occurered. Contact gypsee team", Toast.LENGTH_LONG).show();


                }
            }
        });
    }

    private void parseAddReferCodeResponse(String responseStr) {

        try {
            JSONObject jsonObject = new JSONObject(responseStr);

            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("FetchUserData"));
            dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}