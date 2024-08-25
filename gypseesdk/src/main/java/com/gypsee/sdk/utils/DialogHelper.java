package com.gypsee.sdk.utils;

import android.app.AlertDialog;
import android.content.Context;


public class DialogHelper {

    public static void showPopuptoUpdateApp(Context context, String message, String dialog_title, AlertDialog.Builder builder) {


        builder.setMessage(message).setTitle(dialog_title);

        //Setting message manually and performing action on button click

        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.show();
    }

}
