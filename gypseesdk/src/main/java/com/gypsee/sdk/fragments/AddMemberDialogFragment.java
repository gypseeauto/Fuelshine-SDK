package com.gypsee.sdk.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.database.DatabaseHelper;
import com.gypsee.sdk.databinding.AddMemberDialogBinding;
import com.gypsee.sdk.firebase.FirebaseLogEvents;
import com.gypsee.sdk.models.MemberModel;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;
import com.gypsee.sdk.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMemberDialogFragment extends DialogFragment {

    AddMemberDialogBinding addMemberDialogBinding;
    Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        addMemberDialogBinding = DataBindingUtil.inflate(inflater, R.layout.add_member_dialog, container, false);

        initViews();


        return addMemberDialogBinding.getRoot();
    }


    private void initViews(){

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item){

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(android.R.id.text1)).setText("");
                    ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1; // you dont display last item. It is used as hint.
            }


        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add("Family");
        adapter.add("Friend");
        adapter.add("Preferred Service Assistance");
        adapter.add("Relation");
        addMemberDialogBinding.memberRelation.setAdapter(adapter);
        addMemberDialogBinding.memberRelation.setSelection(adapter.getCount());


        addMemberDialogBinding.addMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitMember();
            }
        });


    }


    private void submitMember(){
        FirebaseLogEvents.firebaseLogEvent("added_emergency_contact",context);

        if (addMemberDialogBinding.memberName.getText().toString().trim().equals("")
        || addMemberDialogBinding.memberCity.getText().toString().trim().equals("")
        || addMemberDialogBinding.memberEmail.getText().toString().trim().equals("")
        //|| isValidEmail(addMemberDialogBinding.memberEmail.getText().toString().trim())
        || addMemberDialogBinding.memberMobile.getText().toString().length() < 10
        || addMemberDialogBinding.memberRelation.getSelectedItemPosition() == addMemberDialogBinding.memberRelation.getAdapter().getCount()){


            showErrorTv("Please enter valid inputs");

        } else {

            showHideProgressBar(true);

            callServer(getString(R.string.emergencyAddContact), "Add emergency contact", 0);

        }

    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private String TAG = AddMemberDialogFragment.class.getSimpleName();

    private void showHideProgressBar(boolean show){
        if (show){
            addMemberDialogBinding.progressBar.setVisibility(View.VISIBLE);
            addMemberDialogBinding.addMemberButton.setVisibility(View.GONE);
            addMemberDialogBinding.errorText.setVisibility(View.GONE);
            addMemberDialogBinding.memberRelation.setVisibility(View.GONE);
            addMemberDialogBinding.memberMobile.setVisibility(View.GONE);
            addMemberDialogBinding.memberEmail.setVisibility(View.GONE);
            addMemberDialogBinding.memberCity.setVisibility(View.GONE);
            addMemberDialogBinding.memberName.setVisibility(View.GONE);
        } else {
            addMemberDialogBinding.progressBar.setVisibility(View.GONE);
            addMemberDialogBinding.addMemberButton.setVisibility(View.VISIBLE);
            addMemberDialogBinding.errorText.setVisibility(View.GONE);
            addMemberDialogBinding.memberRelation.setVisibility(View.VISIBLE);
            addMemberDialogBinding.memberMobile.setVisibility(View.VISIBLE);
            addMemberDialogBinding.memberEmail.setVisibility(View.VISIBLE);
            addMemberDialogBinding.memberCity.setVisibility(View.VISIBLE);
            addMemberDialogBinding.memberName.setVisibility(View.VISIBLE);
        }

    }

    private void showErrorTv(String message){
        addMemberDialogBinding.progressBar.setVisibility(View.GONE);
        addMemberDialogBinding.errorText.setVisibility(View.VISIBLE);
        addMemberDialogBinding.errorText.setText(message);
    }


    private void callServer(String url, final String purpose, final int value) {

        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);
        Call<ResponseBody> call;
        User user = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getUser();
        JsonObject jsonObject = new JsonObject();
        showHideProgressBar(true);

        switch (value) {
            case 0:

                jsonObject.addProperty("city", addMemberDialogBinding.memberCity.getText().toString());
                jsonObject.addProperty("relation", addMemberDialogBinding.memberRelation.getSelectedItem().toString());
                jsonObject.addProperty("userEmail", addMemberDialogBinding.memberEmail.getText().toString());
                jsonObject.addProperty("userName", addMemberDialogBinding.memberName.getText().toString());
                jsonObject.addProperty("userPhoneNumber", addMemberDialogBinding.memberMobile.getText().toString());

                call = apiService.uploadObd(user.getUserAccessToken(), jsonObject);
                break;

            case 1:


                call = apiService.getEmergencyContacts(user.getUserAccessToken(), user.getUserId());
                break;

            default:

                call = apiService.uploadObd(user.getUserAccessToken(), jsonObject);
                break;
        }

        Log.e(TAG, purpose + " Input :" + jsonObject.toString());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    showHideProgressBar(false);
                    if (response.isSuccessful()) {
                        Log.e(TAG, "Response is success");
                        //If the response is null, we will return immediately.
                        ResponseBody responseBody = response.body();
                        if (responseBody == null) {
                            showErrorTv("AN unknown error occured. Kindly contact Gypsee Team.");
                            return;
                        }
                        String responseStr = responseBody.string();
                        Log.e(TAG, purpose + " Response :" + responseStr);

                        switch (value) {
                            case 0:
                                callServer(getString(R.string.fetchEmergencyContacts), "Fetch emergency contacts", 1);
                                break;
                            case 1:

                                parseFetchEmergencyContacts(responseStr);
                                //LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("Reload initviews"));
                                dismiss();
                                break;
                        }
                    } else {
                        Log.e(TAG, purpose + " Response is not Successful");
                        ResponseBody responseBody = response.errorBody();
                        String errResponse = responseBody.string();
                        Log.e(TAG, purpose + " Error Response is : " + errResponse);
                        int responseCode = response.code();

                        if (responseCode == 401 || responseCode == 403) {
                            //include logout functionality
                            Utils.clearAllData(context);
                        } else {

                            JSONObject jObjError = new JSONObject(errResponse);
                            showErrorTv(jObjError.getString("message"));

                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override

            public void onFailure(Call<ResponseBody> call, Throwable t) {

                showHideProgressBar(false);
                Log.e(TAG, "error here since request failed");
                if (t.getMessage().contains("Unable to resolve host")) {
                    showErrorTv("Please Check your internet connection");
                } else {
                    showErrorTv(t.getMessage());
                }
            }
        });
    }


    private void parseFetchEmergencyContacts(String response) throws JSONException {

        JSONObject jsonResponse = new JSONObject(response);
        DatabaseHelper helper = new DatabaseHelper(context);
        helper.deleteTable(DatabaseHelper.EMERGENCY_CONTACT_TABLE);

        JSONArray emergencyContacts = jsonResponse.getJSONArray("emergencyContacts");
        ArrayList<MemberModel> contacts = new ArrayList<>();

        for (int i=0; i<emergencyContacts.length(); i++){

            JSONObject contact = emergencyContacts.getJSONObject(i);

            String city = contact.has("city") ? contact.getString("city") : "";
            String id = contact.has("id") ? contact.getString("id") : "";
            String relation = contact.has("relation") ? contact.getString("relation") : "";
            String userEmail = contact.has("userEmail") ? contact.getString("userEmail") : "";
            String userName = contact.has("userName") ? contact.getString("userName") : "";
            String userPhoneNumber = contact.has("userPhoneNumber") ? contact.getString("userPhoneNumber") : "";

            contacts.add(new MemberModel(id, userName, city, userPhoneNumber, userEmail, relation));

        }
        helper.insertEmergencyContacts(contacts);
    }








}
