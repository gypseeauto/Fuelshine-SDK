package com.gypsee.sdk.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.GypseeMainActivity;
import com.gypsee.sdk.config.MyPreferenece;

public class PhoneForegroundService extends Service {

    private final static String NOTIFICATION_CHANNEL = "BLUETOOTH_FOREGROUND_SERVICE";
    private final static String NOTIFICATION_ID = "BLUETOOTH_SERVICE_ID";
    private final static int PENDING_INTENT_REQUEST_CODE = 100;
    private final static int NOTIFICATION_REQUEST_CODE = 101;
    private String TAG = PhoneForegroundService.class.getSimpleName();
    NotificationCompat.Builder newNotificationBuilder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showBluetoothForegroundNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(NOTIFICATION_ID, NOTIFICATION_CHANNEL);
        } else {
            Intent intent = new Intent(this, GypseeMainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, PENDING_INTENT_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.bigText("Fuelshine");
            bigTextStyle.setBigContentTitle("Tap to open");

            builder.setStyle(bigTextStyle);
            // builder.setOnlyAlertOnce(true); //to quietly update the notification
            builder.setWhen(System.currentTimeMillis());
            builder.setSmallIcon(R.drawable.gypsee_theme_logo);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.gypsee_theme_logo));
            builder.setPriority(Notification.PRIORITY_HIGH);
            builder.setFullScreenIntent(pendingIntent, true);

            Notification notification = builder.build();
            startForeground(NOTIFICATION_REQUEST_CODE, notification);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelID, String channelName) {
        Intent resultIntent = new Intent(this, GypseeMainActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent pendingIntent;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S) {
            pendingIntent = taskStackBuilder.getPendingIntent(PENDING_INTENT_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        }else{
            pendingIntent = taskStackBuilder.getPendingIntent(PENDING_INTENT_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationChannel notificationChannel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null; //exit if notification manager is null
        notificationManager.createNotificationChannel(notificationChannel);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelID);

        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.gypsee_theme_logo)
                .setContentTitle("FuelShine")
                .setContentText("Tap to open")
                //.setOnlyAlertOnce(true) //to update notification quietly
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent)
                .build();

        newNotificationBuilder = notificationBuilder;

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_REQUEST_CODE, notificationBuilder.build());
        startForeground(NOTIFICATION_REQUEST_CODE, notification);
    }

    MyPreferenece myPreferenece;
    private boolean isServiceBound = false;
    boolean isStarting = true;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //showBluetoothForegroundNotification();
        myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, this);
        //dtcVals = getDict(R.array.dtc_keys, R.array.dtc_values);
        if (!isServiceBound) {
            isStarting = true;
            //dtcVals = getDict(R.array.dtc_keys, R.array.dtc_values);
            showBluetoothForegroundNotification();
            //runBluetoothAutoConnect();
            //checkGPSEnabledOrnot();
            boolean isDriveMode = myPreferenece.getSharedPreferences().getBoolean(MyPreferenece.DRIVING_MODE, true);

            /*if (isDriveMode)
            {
                checkBluetoothAndConnect();
            }else {
                //We need to start phone mode service.
                //then start trip if the speed is greater than 10km/hr

            }
*/
        }
        return START_STICKY;
    }
}
