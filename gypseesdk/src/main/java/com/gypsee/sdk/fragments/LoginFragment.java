package com.gypsee.sdk.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.fragments.countrycodepicker.CountryCodeFragment;
import com.gypsee.sdk.GypseeSdk;
import com.gypsee.sdk.jsonParser.AsyncTaskClass;
import com.gypsee.sdk.jsonParser.ResponseFromServer;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.utils.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener, CountryCodeFragment.CountryCodeListener {

    private GoogleSignInClient mGoogleSignInClient;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {

        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private View view;
    private Context context;
    private ProgressDialog dialog;
//    private TextView termsText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);
        context = getContext();
        initGmailOptions();

        dialog = new ProgressDialog(context);
        initViews(view);

        signUpBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });

        updateFCMTokenIfNotLoggedIn();
        return view;
    }

    private void updateFCMTokenIfNotLoggedIn() {

        User user = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getUser();
        if (user == null) {

            if(new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getStringData(MyPreferenece.FCM_TOKEN) == null){
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()){
                            new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).saveStringData(MyPreferenece.FCM_TOKEN, task.getResult());
                            Log.e(TAG, "login fcm: " + task.getResult());
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("fcmToken", new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getStringData(MyPreferenece.FCM_TOKEN));

                            new AsyncTaskClass(context, TAG, 2, "Please wait",
                                    false, responseFromServer)
                                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject.toString(),
                                            "regerror", getResources().getString(R.string.onboardFcmTokenUrl), "Uplaod Fcm Token");
                        } else {
                            Log.e(TAG, "fcm task failed");
                        }
                    }
                });
            } else {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("fcmToken", new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getStringData(MyPreferenece.FCM_TOKEN));

                Log.e(TAG, "login fcm 2: " + jsonObject.get("fcmToken"));

                new AsyncTaskClass(context, TAG, 2, "Please wait",
                        false, responseFromServer)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject.toString(),
                                "regerror", getResources().getString(R.string.onboardFcmTokenUrl), "Uplaod Fcm Token");
            }
        }
    }


    private void initGmailOptions() {
        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(GypseeSdk.googleClientId)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

        logoutFromGmail();
    }


    private String email, image_url, username;


    EditText loginMobileNumber;

    Button doneImage;
    ProgressBar progress_bar;
    RelativeLayout countryCode;

    LinearLayout signUpBox;
    TextView countryCodeTxt;

    private void initViews(View view) {
        TextView  facebookLogin;
        ImageView gmailSignInBtn;
        gmailSignInBtn = view.findViewById(R.id.gmailLogin);
        facebookLogin = view.findViewById(R.id.facebookLogin);
        loginMobileNumber = view.findViewById(R.id.loginMobileNumber);
        doneImage = view.findViewById(R.id.rightImage);
        countryCode = view.findViewById(R.id.countryCode);
        countryCodeTxt = view.findViewById(R.id.countryCodeTxt);
        signUpBox = view.findViewById(R.id.signupBox);


        progress_bar = view.findViewById(R.id.progress_bar);

//
//        termsText = view.findViewById(R.id.terms_text);
//
//        termsText.setMovementMethod(LinkMovementMethod.getInstance());
//        termsText.setClickable(true);

        String text = "By continuing, you agree to the <a href='http://appadmin.gypsee.in/html/gypsee_terms_v2.html'>Terms of Services</a>.\n" +
                "Note: The Fuelshine <a href='http://appadmin.gypsee.in/html/gypsee_privacy_v2.html'>privacy policy</a> describes\n" +
                "how data is handled in this service. App sends\n" +
                "diagnostic data to Fuelshine to help improve the\napp.";

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            termsText.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
//        } else {
//            termsText.setText(Html.fromHtml(text));
//        }

//        facebookLogin.setOnClickListener(this);

        doneImage.setOnClickListener(this);
        countryCode.setOnClickListener(this);

    }

    private void checkMobileNumberTosendOtp() {
        String mobileNo = loginMobileNumber.getText().toString();

        JSONObject jsonObject = new JSONObject();

        String countryCode ;
        if(countryCodeTxt.getText().toString().contains("+91"))
        {
            countryCode = "+91";
        }else{
            countryCode = countryCodeTxt.getText().toString();
        }
            //put something inside the map, could be null
        try {

            jsonObject.put("countryCode",countryCode);
            jsonObject.put("fcmToken", new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getStringData(MyPreferenece.FCM_TOKEN));
            jsonObject.put("userPhoneNumber", mobileNo);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Utils.hideKeyboard((AppCompatActivity) context);
        new AsyncTaskClass(context, TAG, 1, "Please wait",
                false, responseFromServer)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject.toString(),
                        "regerror", getResources().getString(R.string.loginOtpSendUrl), "Login with Mobile");

       /* if (mobileNo.length() == 10) {

           } else {
            showProgressView(false);
            loginMobileNumber.setError("Please provide 10 digit mobile number");
        }*/
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        //updateUI(account);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();//            case R.id.signupBox:
//                googleSignIn();
//                break;
        if (id == R.id.countryCode) {
            Log.e(TAG, "onClick: ");
            navigateToCountryCodePickerFragment();
        } else if (id == R.id.rightImage) {
            requestId = null;
            mobileNo = null;
            showProgressView(true);
            checkMobileNumberTosendOtp();
        }
    }

    private void showDialogMessage(String message, boolean isShowDialog) {

        if (isShowDialog) {

            if (dialog.isShowing())
                dialog.dismiss();
            dialog.setMessage(message);
            dialog.show();
        } else {
            dialog.dismiss();
        }

    }

    private void showProgressView(boolean isShow) {

        if (isShow) {

            progress_bar.setVisibility(View.VISIBLE);
//            doneImage.setVisibility(View.GONE);
        } else {

            progress_bar.setVisibility(View.GONE);
//            doneImage.setVisibility(View.VISIBLE);
        }
    }

    public static final int RC_SIGN_IN = 1001;

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        super.onActivityResult(requestCode, resultCode, data);

        Log.e(TAG, "Onactivity result is called");
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    String TAG = LoginFragment.class.getSimpleName();

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);

            String token = acct.getIdToken();
            // Signed in successfully, show authenticated UI.
            Log.e(TAG, "display name: " + acct.getDisplayName());
            username = acct.getDisplayName();
            if (acct.getPhotoUrl() != null)
                image_url = acct.getPhotoUrl().toString();
            else
                image_url = "";
            email = acct.getEmail();
            loginWithGmail(email, token, "");
            Log.e(TAG, "display name: " + acct.getDisplayName() + " - " + image_url + " - " + email + " - " + token);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            e.printStackTrace();
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());
            Log.e(TAG, "signInResult:failed code=" + e.getMessage());
            Toast.makeText(context, "Login failed. Error code: " + e.getStatusCode() + ", Message: " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    private void loginWithGmail(String email, String gmailToken, String fbToken) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("macId", Utils.getMACAddress());
        jsonObject.addProperty("userName", email);
        jsonObject.addProperty("userPassword", email);
        jsonObject.addProperty("fbToken", fbToken);
        jsonObject.addProperty("gmailToken", gmailToken);

        new AsyncTaskClass(context, TAG, 0, "Please wait",
                false, responseFromServer)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject.toString(),
                        "regerror", getResources().getString(R.string.mobileLoginv2APi), "Login with gmail");
    }

    private void gotoOtpVerificationwithGmailDetails(String personName, String email, String personPhotoUrl, String mobileNo, String requestId) {

        String countryCode = countryCodeTxt.getText().toString();

        Log.e(TAG, "gotoOtpVerificationwithGmailDetails: "+mobileNo );
        ArrayList<String> gmailCredentails = new ArrayList<>();
        gmailCredentails.add(personName);
        gmailCredentails.add(email);
        gmailCredentails.add(personPhotoUrl);
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.baseframelayout, OtpVerificationFragment.newInstance(requestId, mobileNo, gmailCredentails,countryCode), OtpVerificationFragment.class.getSimpleName()) // give your fragment container id in first parameter
                .addToBackStack(OtpVerificationFragment.class.getSimpleName())  // if written, this transaction will be added to backstack
                .commitAllowingStateLoss(); //to remove IllegalStateException crash. no saved state is used in this.
    }

    private ResponseFromServer responseFromServer = new ResponseFromServer() {
        @Override
        public void responseFromServer(String Response, String className, int value) {

            showProgressView(false);
            if (Response.equalsIgnoreCase("SSLHandshakeException")) {
                //internet_access string. show toast
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_LONG).show();
            } else if (Response.equalsIgnoreCase("UnknownHostException")) {
                //show toast server down
                Toast.makeText(context, "Please hold back and try again after sometime", Toast.LENGTH_LONG).show();
            } else {
                switch (value) {
                    case 0:
                        parseGmailVerification(Response);
                        break;
                    case 1:
                        showProgressView(false);
                        parseOtpSendResponse(Response);
                        break;
                }
            }
        }
    };

    private void parseGmailVerification(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);

            String status = jsonResponse.getString("status");
            String message = jsonResponse.getString("message");
            String mobileNo = loginMobileNumber.getText().toString();


            if (status.equals("200")) {
                if (message.equals("No user found.")) {
                    gotoOtpVerificationwithGmailDetails(username, email, image_url, mobileNo, requestId);
                } else {

                    JSONObject userJsonObject = jsonResponse.getJSONObject("user");
                    String userId, userName, userFullName, userEmail, userPhoneNumber, userAccessToken, fcmToken, userImg, userDeviceMac,
                            userTypes, referCode, createdOn, lastUpdatedOn;

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
                    approved = userJsonObject.has("approved") && userJsonObject.getBoolean("approved");
                    locked = userJsonObject.has("locked") && userJsonObject.getBoolean("locked");
                    signUpBonusCredited = userJsonObject.has("signUpBonusCredited") && userJsonObject.getBoolean("signUpBonusCredited");
                    referCodeApplied = userJsonObject.has("referCodeApplied") && userJsonObject.getBoolean("referCodeApplied");

                    walletAmount = (userWallet != null) ? (userWallet.has("loyaltyPoints") ? userWallet.getInt("loyaltyPoints") : 0) : 0;


                    new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).storeUser(new User(userId, userName, userFullName, userEmail, userPhoneNumber, userAccessToken, fcmToken, userImg, userDeviceMac,
                            userTypes, referCode, createdOn, lastUpdatedOn, approved, locked, signUpBonusCredited, referCodeApplied, false, String.valueOf(walletAmount)));
                    gotoMainActivity(false);
                }

            } else {
                if (message.equals("No user found.")) {
                    gotoOtpVerificationwithGmailDetails(username, email, image_url, mobileNo, requestId);
                    return;
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void logoutFromGmail() {
        //mGoogleSignInClient.signOut();
       // Auth.GoogleSignInApi.signOut(mGoogleSignInClient.asGoogleApiClient());
        //mGoogleSignInClient.revokeAccess();

        //mGoogleSignInClient.getApiOptions().getLogSessionId()


        /*mGoogleSignInClient.revokeAccess().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.e(TAG, "revoke access");
                Log.e(TAG, "revoke task status: "+String.valueOf(task.isSuccessful()));
            }
        });*/

        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.e(TAG, "sign out onComplete");
                Log.e(TAG, "signout task: "+String.valueOf(task.isSuccessful()));
            }
        });


    }

    String requestId = null, mobileNo = null;

    private void parseOtpSendResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);

            String status = jsonResponse.getString("status");
            String message = jsonResponse.getString("message");
            mobileNo = loginMobileNumber.getText().toString().trim();

            if (status.equals("200")) {
                if (mobileNo.equals("9996680036")){
                    requestId = jsonResponse.getString("otp");
                } else {
                    requestId = jsonResponse.getString("requestId");
                }
                Boolean userExists = jsonResponse.getBoolean("userExists");
                if (userExists) {
                    //Go to otp verification page
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .add(R.id.baseframelayout, OtpVerificationFragment.newInstance(requestId, mobileNo, new ArrayList<String>(),countryCodeTxt.getText().toString()), OtpVerificationFragment.class.getSimpleName()) // give your fragment container id in first parameter
                            .addToBackStack(OtpVerificationFragment.class.getSimpleName())  // if written, this transaction will be added to backstack
                            .commitAllowingStateLoss();
                } else {
                    googleSignIn();
                }


            } else {

                Toast.makeText(context, "Please login with Gmail or Facebook", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void gotoMainActivity(boolean isNewUser) {
        Intent in;
        in = new Intent(context, PermissionActivity.class);
        in.putExtra("isNewUser", isNewUser);
        in.putExtra("freshlogin", true);
        startActivity(in);
        ((AppCompatActivity) context).finishAffinity();
    }


    @Override
    public void onCountryCodeSelected(String countryCode) {
        countryCodeTxt.setText(countryCode);
    }

    private void navigateToCountryCodePickerFragment() {
        Log.e(TAG, "navigateToCountryCodePickerFragment: " );
        CountryCodeFragment countryCodePickerFragment =  CountryCodeFragment.newInstance();
        countryCodePickerFragment.setCountryCodeListener(this);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.add(R.id.baseframelayout, countryCodePickerFragment);
        transaction.addToBackStack("CountryCodeFragment");
        transaction.commit();
    }
}
