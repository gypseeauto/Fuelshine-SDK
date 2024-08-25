package com.gypsee.sdk.jsonParser;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AsyncTaskClass extends AsyncTask<String, String, String> {

    private Context context;
    private String tag;
    private int value;
    private ResponseFromServer responseFromServer;
    private String alertMessage;
    private boolean showAlertDialog;
    private boolean isIntentServerice;
    public static boolean isSessionExpired = false;


    public AsyncTaskClass(Context context, String tag, int value, String alertMessage, boolean showAlertDialog,
                          ResponseFromServer responseFromServer, boolean isIntentServerice) {
        this.context = context;
        this.tag = tag;
        this.value = value;
        this.responseFromServer = responseFromServer;
        this.alertMessage = alertMessage;
        this.showAlertDialog = showAlertDialog;
        this.isIntentServerice = isIntentServerice;
    }

    private String logTagName = "";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
       /* if (showAlertDialog)
            CustomProgressDialogFragment.showCustomProgress(context, alertMessage);*/
    }


    @Override
    protected String doInBackground(String... inputParameters) {
        logTagName = inputParameters[3];
        return OkHttp_Class.callServer(inputParameters[0], inputParameters[1], inputParameters[2], inputParameters[3]);
    }

    @Override
    protected void onPostExecute(String responseString) {
        super.onPostExecute(responseString);
        if (showAlertDialog)
        //CustomProgressDialogFragment.dismissCustomProgress(context);
        {
            Log.e(tag, "Dismiss Alert Dialog" + showAlertDialog);
        }
        if ((responseString.equalsIgnoreCase("SSLHandshakeException")
        ) && !isIntentServerice) {

            //internet_access string. show toast
            responseFromServer.responseFromServer(responseString, tag, value);

            Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_LONG).show();


        } else if (responseString.equalsIgnoreCase("UnknownHostException") && !isIntentServerice) {
            //show toast server down
            responseFromServer.responseFromServer(responseString, tag, value);

            Toast.makeText(context, "Our server is slow", Toast.LENGTH_LONG).show();
        } else if (responseString.equalsIgnoreCase("")) {

        } else {
            responseFromServer.responseFromServer(responseString, tag, value);
        }
    }

    public AsyncTaskClass(Context context, String tag, int value, String alertMessage, boolean showAlertDialog) {
        this.context = context;
        this.tag = tag;
        this.value = value;
//        this.responseFromServer = (ResponseFromServer) context;
        this.alertMessage = alertMessage;
        this.showAlertDialog = showAlertDialog;
    }

    public AsyncTaskClass(Context context, String tag, int value, String alertMessage, boolean showAlertDialog,
                          ResponseFromServer responseFromServer) {
        this.context = context;
        this.tag = tag;
        this.value = value;
        this.responseFromServer = responseFromServer;
        this.alertMessage = alertMessage;
        this.showAlertDialog = showAlertDialog;
    }

}




