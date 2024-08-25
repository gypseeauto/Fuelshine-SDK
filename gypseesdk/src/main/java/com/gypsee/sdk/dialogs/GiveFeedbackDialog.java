package com.gypsee.sdk.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.databinding.DialogGiveFeedbackBinding;
import com.gypsee.sdk.models.User;


public class GiveFeedbackDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = GiveFeedbackDialog.class.getSimpleName();
    private Context context;
    private DialogGiveFeedbackBinding dialogGiveFeedbackBinding;
    String[] defaultTrip, defaultApp;
    Spinner spinner;
    ArrayAdapter arrayAdapter;
    String feedbackType, tripId;
    User user;


    public GiveFeedbackDialog(Context context, String feedbackType, String tripId) {
        super(context);
        this.context = context;
        this.feedbackType = feedbackType;
        this.tripId = tripId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        defaultTrip = new String[]{"Trip feedback", "Fuelshine app feedback"};
        defaultApp = new String[]{"Fuelshine app feedback", "Trip feedback"};
        user = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getUser();

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        dialogGiveFeedbackBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_give_feedback, null, false);

        if(feedbackType.equals("app")) {
            arrayAdapter = new ArrayAdapter(context, R.layout.simple_spinner_dropdown, defaultApp);
        }else{
            arrayAdapter = new ArrayAdapter(context, R.layout.simple_spinner_dropdown, defaultTrip);
        }
        spinner = dialogGiveFeedbackBinding.feedbackType;
        spinner.setAdapter(arrayAdapter);

        setContentView(dialogGiveFeedbackBinding.getRoot());
        setWindowAttributes();
        implementClickListeners();

    }

    private void implementClickListeners() {

        dialogGiveFeedbackBinding.positioveBtn.setOnClickListener(this);
        dialogGiveFeedbackBinding.negativeBtn.setOnClickListener(this);
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
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.positioveBtn) {//We need call server and upload the details.
            if (dialogGiveFeedbackBinding.feedBackMessage.getText().toString().length() <= 5) {
                Toast.makeText(context, "Please enter proper feedback message", Toast.LENGTH_LONG).show();
            } else if (dialogGiveFeedbackBinding.ratingBar.getRating() == 0) {
                Toast.makeText(context, "Please give ratings", Toast.LENGTH_LONG).show();
            } else {
                String userDetails;
                if (tripId.equals("null")) {
                    userDetails = dialogGiveFeedbackBinding.feedBackMessage.getText() + "\n\n\n" +
                            user.getUserFullName() + "\n" + user.getUserPhoneNumber() + "\n" +
                            Build.MANUFACTURER + " " + Build.MODEL + " " + Build.VERSION.RELEASE;
                } else {
                    userDetails = dialogGiveFeedbackBinding.feedBackMessage.getText() + "\n\n\n" +
                            user.getUserFullName() + "\n" + user.getUserPhoneNumber() + "\n" +
                            "Trip Id: " + tripId + "\n" +
                            Build.MANUFACTURER + " " + Build.MODEL + " " + Build.VERSION.RELEASE;
                }
                sendEmail(spinner.getSelectedItem().toString() + " (" + dialogGiveFeedbackBinding.ratingBar.getRating() + "/5)",
                        userDetails);
                dismiss();
//                    callServer(context.getResources().getString(R.string.feedBackurl), "Give feedback", 0);
            }
        } else if (id == R.id.negativeBtn) {
            dismiss();
        }
    }

    private void sendEmail(String subject, String userDetails) {
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setData(Uri.parse("mailto:")); // only email apps should handle this
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"hello@getfuelshine.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, userDetails);

        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        context.startActivity(Intent.createChooser(i, "Send mail..."));

    }

//    private void callServer(String url, String purpose, final int value) {
//
//        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);
//        Call<ResponseBody> call;
//        JsonObject jsonInput = new JsonObject();
//
//        switch (value) {
//            case 0:
//                showProgressView(true);
//
//                User user = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getUser();
//
//                jsonInput.addProperty("rating", dialogGiveFeedbackBinding.ratingBar.getRating());
//                jsonInput.addProperty("feedback", dialogGiveFeedbackBinding.feedBackMessage.getText().toString());
//
//
//                Log.e(TAG, purpose + " Input is : " + jsonInput.toString());
//                call = apiService.uploadVehDetails(user.getUserAccessToken(), jsonInput);
//                break;
//
//            default:
//                return;
//        }
//
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                showProgressView(false);
//                try {
//                    if (response.isSuccessful()) {
//                        Log.e(TAG, "Response is success");
//                        String responseStr = response.body().string();
//                        Log.e(TAG, "Response :" + responseStr);
//                        Toast.makeText(context, "We have received your Feedback. Our team will check and update you", Toast.LENGTH_LONG).show();
//                        dismiss();
//                    } else {
//                        Log.e(TAG, "Response is not succesfull");
//                        String errorBody = response.errorBody().string();
//                        Log.e(TAG, " Response :" + errorBody);
//
//                        int responseCode = response.code();
//                        if (responseCode == 401 || responseCode == 403) {
//                            //include logout functionality
//                            Utils.clearAllData(context);
//                            return;
//                        }
//                        if (response.errorBody() == null) {
//                            Log.e("Error code 400", "" + response.errorBody().string());
//                        } else {
//                            JSONObject jsonObject = new JSONObject(errorBody);
//                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
//                            Log.e("Error code 400", "" + "Response is not empty");
//                        }
//
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                showProgressView(false);
//
//                if (t.getMessage().contains("Unable to resolve host")) {
//                    Toast.makeText(context, "Please Check your internet connection", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(context, "An unknown error occured, Please contact gypsee team.", Toast.LENGTH_LONG).show();
//                }
//
//            }
//        });
//    }


    void showProgressView(boolean isShowprogress) {

        if (isShowprogress) {
            dialogGiveFeedbackBinding.buttonsLayout.setVisibility(View.GONE);
            dialogGiveFeedbackBinding.progressBar.setVisibility(View.VISIBLE);
        } else {
            dialogGiveFeedbackBinding.buttonsLayout.setVisibility(View.VISIBLE);
            dialogGiveFeedbackBinding.progressBar.setVisibility(View.GONE);
        }
    }
}
