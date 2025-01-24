package com.gypsee.sdk.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gypsee.sdk.activities.GypseeMainActivity;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra("notification_id", -1);

        if (notificationId != -1) {
            // Trigger MainActivity and pass the flag to show the dialog
            Intent showDialogIntent = new Intent(context, GypseeMainActivity.class);
            showDialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            showDialogIntent.putExtra("show_dialog", true);  // Add extra to indicate dialog should be shown
            context.startActivity(showDialogIntent);
        }


    }


}
