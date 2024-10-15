package com.gypsee.sdk.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import com.gypsee.sdk.R;
import com.gypsee.sdk.broadcastreceivers.SmsBroadcastReceiver;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.customviews.OtpEditText;
import com.gypsee.sdk.interfaces.OtpReceivedInterface;
import com.gypsee.sdk.jsonParser.AsyncTaskClass;
import com.gypsee.sdk.jsonParser.ResponseFromServer;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.utils.Utils;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class OtpVerificationFragment extends Fragment implements OtpReceivedInterface, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private String requestId, mobileNo,countryCode;

    private Context context;
    static GoogleSignInClient mGoogleSignInClient;

    SmsBroadcastReceiver mSmsBroadcastReceiver;


    private int RESOLVE_HINT = 1003;


    private ArrayList<String> gmailCredentails = new ArrayList<>();


    public static OtpVerificationFragment newInstance(String requestId, String mobileNo, ArrayList<String> gmailCredentails,String countryCode) {

        Bundle args = new Bundle();
        args.putString("requestId", requestId);
        args.putString("mobileNo", mobileNo);
        args.putString("countryCode", countryCode);

        args.putSerializable("gmailCredentails", gmailCredentails);
        OtpVerificationFragment fragment = new OtpVerificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {
            requestId = b.getString("requestId");
            mobileNo = b.getString("mobileNo");
            countryCode = b.getString("countryCode");
            gmailCredentails = (ArrayList<String>) b.getSerializable("gmailCredentails");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_otp_verification, container, false);
        context = getContext();
        initViews(view);
        initOtpVerificationProcess();
        if (requestId != null) {
            intiViewsforOtpVerification();
        } else {
            intiViewsforMobileInput(view);
        }
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void initOtpVerificationProcess() {

        // init broadcast receiver to receive the OTP
        mSmsBroadcastReceiver = new SmsBroadcastReceiver();
        mSmsBroadcastReceiver.setOnOtpListeners(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.registerReceiver(mSmsBroadcastReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
        }
    }


    private TextView  verifyBtn, resendOtpBtn;
//    private TextView descTv;
    private TextView mobileNumber;
    private OtpEditText otpEditText;

    private LinearLayout mobileNumberLayout;
    ProgressBar progress_bar;

    private void initViews(final View view) {
        otpEditText = view.findViewById(R.id.otpEdittext);
        verifyBtn = view.findViewById(R.id.verifyTv);
//        descTv = view.findViewById(R.id.descriptionTv);
        mobileNumber = view.findViewById(R.id.loginMobileNumber);
        mobileNumberLayout = view.findViewById(R.id.mobileLayout);
        progress_bar = view.findViewById(R.id.progress_bar);
        resendOtpBtn = view.findViewById(R.id.resendOtpBtn);
        //setting the spannable string.

       mobileNumber.setText(mobileNo);
        SpannableString ss1 = new SpannableString("Didn't recieved the OTP? RESEND OTP");
        //ss1.setSpan(new RelativeSizeSpan(1.25f), ss1.length() - 11, ss1.length(), 0); // set size
        ss1.setSpan(new ForegroundColorSpan(Color.RED), ss1.length() - 11, ss1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

//        resendOtpBtn.setText(ss1);
        resendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressView(true);

                JSONObject jsonObject = new JSONObject();
//put something inside the map, could be null
                try {
                    jsonObject.put("userPhoneNumber", countryCode+mobileNo);
                    jsonObject.put("fcmToken", new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getStringData(MyPreferenece.FCM_TOKEN));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new AsyncTaskClass(context, TAG, 0, "Please wait",
                        false, responseFromServer)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject.toString(),
                                "regerror", getResources().getString(R.string.loginOtpSendUrl), "Send login Otp");

                Toast.makeText(context, "Otp resent successfully", Toast.LENGTH_SHORT).show();

            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyBtn.getText().toString().equalsIgnoreCase("verify"))
                    sendOtpToServer();
                else {
                    //We need to call send otpEditText .
                }
            }
        });

        otpEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (otpEditText.getText().toString().length() == 6) {
                    Utils.hideKeyboard((AppCompatActivity) context);
                    sendOtpToServer();

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (mobileNo != null && mobileNo.equals("9996680036"))
            otpEditText.setText(requestId);

        mobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mobileNumber.getText().toString().length() == 10) {

                    Utils.hideKeyboard((AppCompatActivity) context);
                    checkMobileNumberTosendOtp();


                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void checkMobileNumberTosendOtp() {
        showProgressView(true);
        mobileNo = mobileNumber.getText().toString();

        JSONObject jsonObject = new JSONObject();
//put something inside the map, could be null
        try {
            jsonObject.put("userPhoneNumber", mobileNo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (mobileNo.length() == 10) {

            new AsyncTaskClass(context, TAG, 0, "Please wait",
                    false, responseFromServer)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject.toString(),
                            "regerror", getResources().getString(R.string.loginOtpSendUrl), "Send login Otp");
        } else {
            showProgressView(false);
            mobileNumber.setError("Please provide 10 digit mobile number");
        }
    }

    private void sendOtpToServer() {
        showProgressView(true);

        String otpStr = otpEditText.getText().toString();
        if (otpStr.length() == 6) {

            JSONObject jsonInput = new JSONObject();
            try {
                jsonInput.put("fcmToken", new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getStringData(MyPreferenece.FCM_TOKEN));
                jsonInput.put("loginUser", true);
                jsonInput.put("otp", otpStr);
                jsonInput.put("requestId", requestId);
                jsonInput.put("type", "user");
                jsonInput.put("userPhoneNumber", mobileNo);
                jsonInput.put("checkUser", true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Call otpEditText verification API
            new AsyncTaskClass(context, TAG, 1, "Please wait",
                    false, responseFromServer)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonInput.toString(),
                            "regerror", getResources().getString(R.string.loginOtpVerificationUrl), "Verify login Otp");

        } else {
            showProgressView(false);

            Toast.makeText(getContext(), "Please enter valid OTP", Toast.LENGTH_LONG).show();
        }
    }


    private void intiViewsforMobileInput(View view) {

        mobileNumberLayout.setVisibility(View.VISIBLE);
        otpEditText.setVisibility(View.GONE);
//        descTv.setText("Please provide mobile number to proceed");
        verifyBtn.setText("next");

    }

    private String TAG = OtpVerificationFragment.class.getSimpleName();

    private void intiViewsforOtpVerification() {

        mobileNumberLayout.setVisibility(View.GONE);
        resendOtpBtn.setVisibility(View.VISIBLE);
        otpEditText.setVisibility(View.VISIBLE);
//        descTv.setText("Enter the otp sent to +91 - " + mobileNo);
        verifyBtn.setText("verify ");

    }

    private ResponseFromServer responseFromServer = new ResponseFromServer() {
        @Override
        public void responseFromServer(String Response, String className, int value) {

            Log.e(TAG, "Response is : " + Response + " - Value " + value);
            showProgressView(false);
            if (Response.equalsIgnoreCase("SSLHandshakeException")) {
                //internet_access string. show toast
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_LONG).show();
            } else if (Response.equalsIgnoreCase("UnknownHostException")) {
                //show toast server down
                Toast.makeText(context, "Please hold back and try again after sometime", Toast.LENGTH_LONG).show();
            } else {

                try {
                    switch (value) {
                        case 0:
                            showProgressView(false);
                            parseOtpSendResponse(Response);
                            break;

                        case 1:
                            showProgressView(false);

                            checkOtpVerificationResponse(Response);
                            break;
                        case 2:
                            parseLoginRegisterResponse(Response);
                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void parseLoginRegisterResponse(String response) throws JSONException {
        JSONObject jsonResponse = new JSONObject(response);

        String status = jsonResponse.getString("status");
        String message = jsonResponse.getString("message");

        boolean isNewUser = false;
        if (status.equals("200")) {
            showProgressView(true);

            String userExists = "";
            if (jsonResponse.has("userExists"))
                userExists = jsonResponse.getString("userExists");
            else if (message.equalsIgnoreCase("User Successfully Registered")) {
                userExists = "true";
                isNewUser = true;
            }

            if (userExists.equals("true")) {
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

                walletAmount = (userWallet != null) ? (userWallet.has("loyaltyPoints") ? userWallet.getInt("loyaltyPoints") : 0) : 0;

                new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).storeUser(new User(userId, userName, userFullName, userEmail, userPhoneNumber, userAccessToken, fcmToken, userImg, userDeviceMac,
                        userTypes, referCode, createdOn, lastUpdatedOn, approved, locked, signUpBonusCredited, referCodeApplied, false, String.valueOf(walletAmount)));
                //updating inTraining mode in HomeFragment on fresh install
                gotoMainActivity(isNewUser);

            } else if (gmailCredentails.size() >= 0) {
                callRegisterAPi(gmailCredentails.get(0), gmailCredentails.get(2), gmailCredentails.get(1));
            } else {
                //Need Regitration now with gmail login.
                googleSignIn();
            }
        } else {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

    private void checkOtpVerificationResponse(String response) throws JSONException {
        JSONObject jsonResponse = new JSONObject(response);

        String status = jsonResponse.getString("status");
        String message = jsonResponse.getString("message");


        if (status.equals("200")) {

            String userExists = jsonResponse.getString("userExists");

            if (userExists.equals("true")) {
                //Go to main Activity
                parseLoginRegisterResponse(response);
            } else {
                //Need Regitration now with gmail login.
                if (gmailCredentails.size() == 0)
                    googleSignIn();
                else
                    callRegisterAPi(gmailCredentails.get(0), gmailCredentails.get(2), gmailCredentails.get(1));

            }


        } else {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

    private void gotoMainActivity(boolean isNewUser) {
        Intent in;
        in = new Intent(context, PermissionActivity.class);
        if (getActivity() == null) {
            Toast.makeText(context, "An unknown error occured. Please contact Gypsee team", Toast.LENGTH_LONG).show();
            return;
        }
        in.putExtra("freshlogin", true);
        in.putExtra("isNewUser", isNewUser);
        startActivity(in);
        ((AppCompatActivity) context).finishAffinity();
    }

    private void googleSignIn() {

        if (mGoogleSignInClient != null) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, LoginFragment.RC_SIGN_IN);
        } else {
            Toast.makeText(context, "Error occured while signing into gmail. Please try again after sometime.", Toast.LENGTH_LONG).show();
            ((AppCompatActivity) context).getSupportFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        super.onActivityResult(requestCode, resultCode, data);

        Log.e(TAG, "Onactivity result is called");
        if (requestCode == LoginFragment.RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        if (requestCode == RESOLVE_HINT) {
            if (resultCode == RESULT_OK) {
              /*  Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                mobileNumber.setText(credential.getId().replace("+91", ""));
                checkMobileNumberTosendOtp();*/

                // credential.getId();  <-- will need to process phone number string
            }
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Log.e(TAG, "display name: " + acct.getDisplayName());
            String personName = acct.getDisplayName();
            String personPhotoUrl = "";
            if (acct.getPhotoUrl() != null)
                personPhotoUrl = acct.getPhotoUrl().toString();
            String email = acct.getEmail();

            Log.e(TAG, "display name: " + acct.getDisplayName() + " - " + personPhotoUrl + " - " + email);

            callRegisterAPi(personName, personPhotoUrl, email);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());

        }
    }

    private void callRegisterAPi(String personName, String personPhotoUrl, String email) {
        JSONObject jsonInput = new JSONObject();
        try {

            String referrer = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getStringData(MyPreferenece.installReferrer);
            HashMap<String, String> utm = new HashMap<>();
            if (referrer != null){
                String[] referrerSplit = referrer.split("&");
                for (String s: referrerSplit){
                    String[] utmParam = s.split("=");
                    if (utmParam.length == 2){
                        utm.put(utmParam[0], utmParam[1]);
                    }
                }
            }

            String utmSource = utm.get("utm_source");
            String utmCampaign = utm.get("utm_campaign");
            String utmContent = utm.get("utm_content");


            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = manager.getConnectionInfo();
            String address = info.getMacAddress();

            jsonInput.put("countryCode", countryCode);

            jsonInput.put("fcmToken", new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getStringData(MyPreferenece.FCM_TOKEN));
            //jsonInput.put("referCode", "");
            jsonInput.put("userDeviceMac", info.getMacAddress());
            jsonInput.put("userEmail", email);
            jsonInput.put("userFullName", personName);
            jsonInput.put("userImg", personPhotoUrl);
            jsonInput.put("userName", email);
            jsonInput.put("userPassword", email);
            jsonInput.put("userPhoneNumber", mobileNo);
            jsonInput.put("utmSource", (utmSource != null) ? utmSource : "");
            jsonInput.put("utmCampaign", (utmCampaign != null) ? utmCampaign : "");
            jsonInput.put("utmContent", (utmContent != null) ? utmContent : "");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new AsyncTaskClass(context, TAG, 2, "Please wait",
                false, responseFromServer)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonInput.toString(),
                        "regerror", getResources().getString(R.string.registerApi), "Register API");

    }

    private void parseOtpSendResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);

            String status = jsonResponse.getString("status");
            String message = jsonResponse.getString("message");
            requestId = jsonResponse.getString("requestId");


            if (status.equals("200")) {
                String requestId = jsonResponse.getString("requestId");
                //Go to otpEditText verification page
                intiViewsforOtpVerification();

            } else {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onOtpReceived(String otp) {

        //Toast.makeText(context, "Otp Received " + otp, Toast.LENGTH_LONG).show();
        otpEditText.setText(otp);
    }

    @Override
    public void onOtpTimeout() {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDestroy() {
        context.unregisterReceiver(mSmsBroadcastReceiver);
        super.onDestroy();


    }


    private void showProgressView(boolean isShow) {

        if (isShow) {

            progress_bar.setVisibility(View.VISIBLE);
//            verifyBtn.setVisibility(View.GONE);
            mobileNumber.setEnabled(false);
        } else {

            progress_bar.setVisibility(View.GONE);
//            verifyBtn.setVisibility(View.VISIBLE);
            mobileNumber.setEnabled(true);
        }
    }


}
