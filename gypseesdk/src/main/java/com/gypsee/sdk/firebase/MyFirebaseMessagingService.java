package com.gypsee.sdk.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.LoginRegisterActivity;
import com.gypsee.sdk.activities.GypseeMainActivity;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.database.DatabaseHelper;
import com.gypsee.sdk.models.OffersModel;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String TAG = MyFirebaseMessagingService.class.getSimpleName();
    MyPreferenece myPreferenece;
    User user;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        Log.e(TAG, "New TOken : " + s);
        new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, this).saveStringData(MyPreferenece.FCM_TOKEN, s);


    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, getApplicationContext());
        user = myPreferenece.getUser();
        Log.e(TAG, "onMessageReceived");
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
        } else {
            try {
                JSONObject json = new JSONObject(remoteMessage.getData());
                Log.e(TAG, "Notification Body: " + remoteMessage.getData());
                parseNotificationdata(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }


    private void parseNotificationdata(JSONObject json) {
        Log.e(TAG, "parseNotificationdata");
        boolean isServiceReminderNotification, isTripEndNotification, isOfferNotification, isVehicleAlertsNotification, vehicleAddNotification,
                isProfileDetailsMissingAlert, isVideoNotification, isPollutionCheckNotification, serviceReminderConfirmationNotification;
        String image, title, message;
        String targetUrl;
        boolean dialogNotification;

        try {
            isServiceReminderNotification = json.getBoolean("isServiceReminderNotification");
            isTripEndNotification = json.getBoolean("isTripEndNotification");
            isOfferNotification = json.getBoolean("isOfferNotification");
            isVehicleAlertsNotification = json.getBoolean("isVehicleAlertsNotification");
            vehicleAddNotification = json.getBoolean("vehicleAddNotification");
            isProfileDetailsMissingAlert = json.getBoolean("isProfileDetailsMissingAlert");
            isVideoNotification = json.getBoolean("isVideoNotification");
            isPollutionCheckNotification = json.getBoolean("isPollutionCheckNotification");
            serviceReminderConfirmationNotification = json.getBoolean("isServiceReminderConfirmationNotification");

            targetUrl = json.has("targetUrl") ? json.getString("targetUrl") : "null";
            if (targetUrl.equals("null")){
                targetUrl = null;
            }

            dialogNotification = json.has("dialogNotification") && json.getBoolean("dialogNotification");

            image = json.has("image") ? json.getString("image") : "";
            title = json.has("title") ? json.getString("title") : "";
            message = json.has("message") ? json.getString("message") : "";

            Log.e(TAG, "Image parse: " + image);

            JSONObject dataObject = new JSONObject();
            dataObject.put("title", title);
            dataObject.put("message", message);

            //We are taking the notification type and showing it in notifications on home page.
            // So that we can navgat the users to the corresponding page after getting the notification or clicking the notification
            String notificationType = "NormalNotification";
            if (isTripEndNotification) {
                String tripId = json.getString("tripId");
                dataObject.put("tripId", tripId);
                notificationType = "TripEnd";
                message = message.equals("") ? "Trip ended Succesfully" : message;

            } else if (isOfferNotification) {
                //Done
                notificationType = "OfferNotification";

            } else if (isServiceReminderNotification) {
                //done
                notificationType = "ServiceReminderNotification";
                String vehicleId = json.getString("vehicleId");
                dataObject.put("vehicleId", vehicleId);


            } else if (serviceReminderConfirmationNotification) {
                //done
                String vehicleId = json.getString("vehicleId");

                notificationType = "serviceReminderConfirmationNotification";
                dataObject.put("vehicleId", vehicleId);


            } else if (isVehicleAlertsNotification) {
                //Done
                notificationType = "VehicleAlertsNotification";

                String vehicleId = json.getString("vehicleId");
                dataObject.put("vehicleId", vehicleId);

            } else if (vehicleAddNotification) {

                //Done
                notificationType = "vehicleAddNotification";
                String vehicleId = json.getString("vehicleId");
                dataObject.put("vehicleId", vehicleId);


            } else if (isProfileDetailsMissingAlert) {
                //Done
                notificationType = "ProfileDetailsMissingNotification";


            } else if (isVideoNotification) {
                //Done
                notificationType = "videoNotification";

                String videoUrl = json.getString("videoUrl");
                dataObject.put("videoUrl", videoUrl);
            } else if (isPollutionCheckNotification) {
                //Done. Need to add the pollution Check
                notificationType = "pollutionCheckNotification";
            } else {

                //Done. This is general notification.
                notificationType = "general";


            }
            DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
            if (databaseHelper.fetchAllNotification().size() >= 20) {
                ArrayList<OffersModel> offersModelArrayList = databaseHelper.fetchAllNotification();
                databaseHelper.deleteNotification(offersModelArrayList.get(0).getId());
            }

            if(!notificationType.equals("general") || user == null) {
                showNotification(title, message, image, notificationType, dataObject.toString(), targetUrl, dialogNotification);
                databaseHelper.InsertNotification(title, image, message, notificationType, dataObject.toString(), getCurrentTime());

                //Send broadcast to home fragment regarding the notification count increase.
                Intent intent = new Intent("NotificationCount");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }else{
                if(message.toLowerCase().contains("sos")){
                    // checking whether emergency contact exist or not
                    callServer(getString(R.string.fetchEmergencyContacts), "Fetch emergency contacts", "emergencyContacts",
                            title, message, image, notificationType, dataObject.toString(), targetUrl, dialogNotification, databaseHelper);
                }else if(message.toLowerCase().contains("bluetooth")){
                    //checking whether device is already present or not
                    callServer(getString(R.string.getUserRegisteredDevices).replace("userId", user.getUserId()), "Fetch Registered Devices", "bluetoothDevices",
                            title, message, image, notificationType, dataObject.toString(), targetUrl, dialogNotification, databaseHelper);
                }else{
                    //checking if car is already present or not
                    callServer(getResources().getString(R.string.vehicles_url).replace("userid", user.getUserId()), "Fetch cars", "vehicles",
                            title, message, image, notificationType, dataObject.toString(), targetUrl, dialogNotification, databaseHelper);
                }

            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void callServer(String url, final String purpose, final String value,
                            String title, String message, String image, String notificationType, String dataObject, String targetUrl, boolean dialogNotification,
                            DatabaseHelper databaseHelper) {
        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);
        Call<ResponseBody> call;
        JsonObject jsonObject = new JsonObject();
        switch (value) {
            case "vehicles":
                call = apiService.getDocVehicleAlerts(user.getUserAccessToken(), true, false);
                break;
            case "bluetoothDevices":
                call = apiService.getObdDevice(user.getUserAccessToken());
                break;
            case "emergencyContacts":
                call = apiService.getEmergencyContacts(user.getUserAccessToken(), user.getUserId());
                break;
            default:
                call = null;
                break;

        }

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    if(response.isSuccessful()){
                        boolean notifSent = false;
                        ResponseBody responseBody = response.body();
                        if (responseBody == null)
                            return;
                        String responseStr = responseBody.string();
                        Log.e(TAG, purpose + " Response :" + responseStr);
                        JSONObject jsonResponse = null;
                        jsonResponse = new JSONObject(responseStr);
                         switch (value){
                             case "vehicles":
                                 //showing add vehicle notification if no vehicle is present
                                 if(jsonResponse.getJSONArray("userVehicles").length()==0){
                                     notifSent = true;
                                     databaseHelper.InsertNotification(title, image, message, notificationType, dataObject.toString(), getCurrentTime());
                                     showNotification(title, message, image, notificationType, dataObject.toString(), targetUrl, dialogNotification);
                                 }
                                 break;
                             case "bluetoothDevices":
                                 //showing add vehicle notification if no bluetooth device is present
                                 if(jsonResponse.getJSONArray("userRegisteredDevices").length()==0){
                                     notifSent = true;
                                     databaseHelper.InsertNotification(title, image, message, notificationType, dataObject.toString(), getCurrentTime());
                                     showNotification(title, message, image, notificationType, dataObject.toString(), targetUrl, dialogNotification);
                                 }
                                 break;
                             case "emergencyContacts":
                                 //showing add vehicle notification if no emergency contact is present
                                 if(jsonResponse.getJSONArray("emergencyContacts").length()==0){
                                     notifSent = true;
                                     databaseHelper.InsertNotification(title, image, message, notificationType, dataObject.toString(), getCurrentTime());
                                     showNotification(title, message, image, notificationType, dataObject.toString(), targetUrl, dialogNotification);
                                 }
                                 break;
                         }
                        //Send broadcast to home fragment regarding the ntoification count increase.
                        if (notifSent) {
                            Intent intent = new Intent("NotificationCount");
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private String getCurrentTime() {
        //Time format
        DateFormat dateFormat2 = new SimpleDateFormat("MMM dd hh:mm aa");
        String dateString2 = dateFormat2.format(new Date()).toString();
        return dateString2;
    }

    private void showNotification(String title, String message, String imageUrl, String notificationType, String dataObject, String targetUrl, boolean dialogNotification) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getResources().getString(R.string.default_notification_channel_id));

        User user = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, this).getUser();

        Log.e(TAG, "In showNotification");
        Intent intent;
        if (user == null) {
            intent = new Intent(getApplicationContext(), LoginRegisterActivity.class);
        } else if (!dialogNotification){
            if (targetUrl == null) {
                //normal notification
                intent = new Intent(getApplicationContext(), GypseeMainActivity.class);
                intent.putExtra("notificationType", notificationType);
                intent.putExtra("dataObject", dataObject);
            } else {
                //user journey notification
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl));

            }
        } else {
            // dialog notification
            Intent broadcastIntent = new Intent("dialogNotification");
            broadcastIntent.putExtra("targetUrl", targetUrl);
            broadcastIntent.putExtra("notificationBody", message);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);

            intent = new Intent(getApplicationContext(), GypseeMainActivity.class);
            intent.putExtra("notificationType", notificationType);
            intent.putExtra("dataObject", dataObject);
            intent.putExtra("targetUrl", targetUrl);
            intent.putExtra("notificationBody", message);

        }

        final PendingIntent resultPendingIntent;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S) {
            resultPendingIntent =
                    PendingIntent.getActivity(
                            getApplicationContext(),
                            0,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_MUTABLE);
        }else{
            resultPendingIntent =
                    PendingIntent.getActivity(
                            getApplicationContext(),
                            0,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        }
        int icon = R.mipmap.ic_launcher;
        if (imageUrl == null || imageUrl.equals("")) {
            showSmallNotification(builder, icon, title, message, resultPendingIntent);
        } else {
            Bitmap bitmap = getBitmapFromURL(imageUrl);
            if (bitmap != null) {
                showBigNotification(bitmap, builder, icon, title, message, resultPendingIntent);
            } else {
                showSmallNotification(builder, icon, title, message, resultPendingIntent);

            }
        }
    }


    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder mBuilder,
                                     int icon, String title, String message, PendingIntent resultPendingIntent) {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);

        Notification notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setStyle(bigPictureStyle)
                .setWhen(new Date().getTime())
                .setSmallIcon(R.drawable.notif_icon)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), icon))
                .setContentText(message)
                .build();

        Log.e(TAG, "in showBigNotification");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel(getString(R.string.default_notification_channel_id));

        notificationManager.notify(((int) (Math.random() * 100000)) % 1000, notification);


    }


    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String
            title, String message, PendingIntent resultPendingIntent) {

        Notification notification;
        notification = mBuilder.setSmallIcon(icon).setTicker(title)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setWhen(new Date().getTime())
                .setSmallIcon(R.drawable.notif_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), icon))
                .setContentText(message)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //for oreo
        createNotificationChannel(getString(R.string.default_notification_channel_id));

        Log.e(TAG, "in showSmallNotification");
        if (notificationManager != null) {
            notificationManager.notify(((int) (Math.random() * 100000)) % 1000, notification);
        }
    }


    private void createNotificationChannel(String Title) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(getString(R.string.default_notification_channel_id), Title, importance);
            channel.setDescription(Title);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
