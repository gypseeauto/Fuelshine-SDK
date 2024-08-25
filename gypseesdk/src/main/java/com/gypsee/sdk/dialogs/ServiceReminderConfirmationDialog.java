package com.gypsee.sdk.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.MyCarDetailActivity;
import com.gypsee.sdk.databinding.FragmentServiceReminderConfirmationDialogBinding;
import com.gypsee.sdk.models.Vehiclemodel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServiceReminderConfirmationDialog extends Dialog {


    private FragmentServiceReminderConfirmationDialogBinding fragmentServiceReminderConfirmationDialogBinding;

    private Context context;
    private Vehiclemodel vehiclemodel;
    private String dataObject;

    public ServiceReminderConfirmationDialog(@NonNull Context context, Vehiclemodel vehiclemodel, String dataObject) {
        super(context);
        this.context = context;
        this.vehiclemodel = vehiclemodel;
        this.dataObject = dataObject;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentServiceReminderConfirmationDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.fragment_service_reminder_confirmation_dialog, null, false);
        setContentView(fragmentServiceReminderConfirmationDialogBinding.getRoot());
        getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

        initViews();

    }

    private void initViews() {

        try {
            JSONObject jsonObject = new JSONObject(dataObject);

            fragmentServiceReminderConfirmationDialogBinding.titleTv.setText(jsonObject.getString("title"));
            fragmentServiceReminderConfirmationDialogBinding.descriptionTV.setText(jsonObject.getString("message"));

            fragmentServiceReminderConfirmationDialogBinding.positioveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Go to AddVehicleFragment.

                    ((AppCompatActivity) context).startActivity(new Intent(context, MyCarDetailActivity.class)
                            //.putExtra("TAG", "AddVehicleFragment")
                            .putExtra("isServiceDone", true)
                            .putExtra(Vehiclemodel.class.getSimpleName(), vehiclemodel)
                    );
                    dismiss();

                }
            });

            fragmentServiceReminderConfirmationDialogBinding.negativeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
