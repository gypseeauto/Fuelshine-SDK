package com.gypsee.sdk.demoaccount.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.AddMemberDialogBinding;

public class DemoAddMemberDialogFragment extends DialogFragment {

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
        adapter.add("Other");
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

        if (addMemberDialogBinding.memberName.getText().toString().trim().equals("")
                || addMemberDialogBinding.memberCity.getText().toString().trim().equals("")
                || addMemberDialogBinding.memberEmail.getText().toString().trim().equals("")
                //|| isValidEmail(addMemberDialogBinding.memberEmail.getText().toString().trim())
                || addMemberDialogBinding.memberMobile.getText().toString().length() < 10
                || addMemberDialogBinding.memberRelation.getSelectedItemPosition() == addMemberDialogBinding.memberRelation.getAdapter().getCount()){


            showErrorTv("Please enter valid inputs");

        } else {

            showHideProgressBar(true);

            new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Member Added", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }, 2000);

        }

    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private String TAG = com.gypsee.sdk.fragments.AddMemberDialogFragment.class.getSimpleName();

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









}

