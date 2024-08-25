package com.gypsee.sdk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;

import com.gypsee.sdk.R;
import com.gypsee.sdk.fragments.LoginFragment;

public class LoginRegisterActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        //CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseLogEvent();

        replaceLoginFragment();
        startSMSListener();
    }


    private void firebaseLogEvent() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1234");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "LoginEvent");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    private void replaceLoginFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        // this will clear the back stack and displays no animation on the screen
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.baseframelayout, LoginFragment.newInstance(), LoginFragment.class.getSimpleName()); // give your fragment container id in first parameter
        //transaction.replace(R.id.baseframelayout, new OtpVerificationFragment("1234","9108394532",null), OtpVerificationFragment.class.getSimpleName()); // give your fragment container id in first parameter
        transaction.commitAllowingStateLoss();

    }

    String TAG = LoginRegisterActivity.class.getSimpleName();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "MainActivity onactivity result");
        Log.e(TAG, "Request code is : " + requestCode);
    }

    public void startSMSListener() {
        SmsRetrieverClient mClient = SmsRetriever.getClient(LoginRegisterActivity.this);
        Task<Void> mTask = mClient.startSmsRetriever();
        Log.e(TAG, "SMS Listener called :");

        mTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // layoutInput.setVisibility(View.GONE);
                // layoutVerify.setVisibility(View.VISIBLE);
                //Toast.makeText(context, "SMS Retriever starts", Toast.LENGTH_LONG).show();
                Log.e(TAG, "SMS Listener :");

            }
        });
        mTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //  Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
            }
        });

    }

}
