package com.gypsee.sdk.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.AlertAddcarLayoutBinding;

public class DialogHelper {
    public static void showCustomDialog(boolean isShowPositivebutton, Context context, FragmentActivity fragmentActivity, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater inflater = fragmentActivity.getLayoutInflater();

        AlertAddcarLayoutBinding alertAddcarLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.alert_addcar_layout, null, false);

        alertAddcarLayoutBinding.titleTv.setText(title);
        alertAddcarLayoutBinding.descriptionTV.setText(message);

        if (isShowPositivebutton) {

        } else {
            alertAddcarLayoutBinding.positioveBtn.setVisibility(View.GONE);
            alertAddcarLayoutBinding.lineView2.setVisibility(View.GONE);
            alertAddcarLayoutBinding.negativeBtn.setText("OK");
        }

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog l``ayout
        builder.setView(alertAddcarLayoutBinding.getRoot());
        final AlertDialog addvehicleAlertDialog = builder.create();
        addvehicleAlertDialog.setCanceledOnTouchOutside(false);
        addvehicleAlertDialog.setCancelable(false);
        addvehicleAlertDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        addvehicleAlertDialog.show();
        alertAddcarLayoutBinding.negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addvehicleAlertDialog.dismiss();
            }
        });

    }
}
