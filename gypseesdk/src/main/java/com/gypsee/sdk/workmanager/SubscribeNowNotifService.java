package com.gypsee.sdk.workmanager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.GypseeMainActivity;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.utils.Constants;

public class SubscribeNowNotifService extends Worker {

    private Context context;
    private final String TAG = getClass().getSimpleName();
    private MyPreferenece myPreferenece;

    public SubscribeNowNotifService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            showNotification();
            return Result.success();
        }catch (Exception e){
            e.printStackTrace();
            return Result.failure();
        }
    }

    private void showNotification() {

        Intent intent = new Intent(getApplicationContext(), GypseeMainActivity.class);
        intent.putExtra("action", Constants.showSubscribeDialog);
        intent.setAction(Constants.showSubscribeDialog);
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


        Notification notification;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1234567");
        notification = builder
                .setColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark))
                .setSmallIcon(R.drawable.ic_splash_screen_wheel_no_bg)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_coin_front))
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Subs"))
                .setAutoCancel(true)
                .setContentTitle("Subscribe Now")
                .setContentIntent(resultPendingIntent)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText("Subs")
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //for oreo
        createNotificationChannel("1234567");

        notificationManager.notify(1234567,notification);
    }

    private void createNotificationChannel(String Title) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("1234567", Title, importance);
            channel.setDescription(Title);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
