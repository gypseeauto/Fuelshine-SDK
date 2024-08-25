package com.gypsee.sdk.services;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ServiceInfo;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.text.DecimalFormat;

import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.DriveModeActivity;
import com.gypsee.sdk.trips.TripRecord;

public class FloatingWidgetService extends Service {

    private WindowManager windowManager;
    private View floatingView;
    private WindowManager.LayoutParams layoutParams;
    private TripRecord currentTrip;
    private final IBinder binder = new FloatingWidgetServiceBinder();

    TextView tripDistance;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiver);
        if (floatingView != null){
            windowManager.removeView(floatingView);
        }
        super.onDestroy();
    }

    public void stopWidgetService(){
        stopForeground(true);
        stopSelf();
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    public void onCreate() {
        super.onCreate();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                startForeground(12345678, getNotification());
            } else {
                startForeground(12345678, getNotification(),
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
            }
        } else {
            startForeground(3, new Notification());
        }

//        if (Build.VERSION.SDK_INT >= 34) {
//            startForeground(
//                    12345678,
//                    getNotification(),
//                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
//        }else {
//            startForeground(
//                    3,
//                    new Notification());
//        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForeground(12345678, getNotification());
//        } else {
//            startForeground(3, new Notification());
//        }

        floatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
        tripDistance = floatingView.findViewById(R.id.trip_distance);
        //used to stop simulated trip
//        floatingView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e("RecognitionIntent", "clicked");
//                Constants.isFakeDriving = false;
//            }
//        });


        registerBroadcastReceiver();

        int LAYOUT_FLAG;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        layoutParams.x = (size.x)/3;

        windowManager.addView(floatingView, layoutParams);



        floatingView.setOnTouchListener(new View.OnTouchListener() {

            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;


            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){

                    case MotionEvent.ACTION_DOWN:
                        initialX = layoutParams.x;
                        initialY = layoutParams.y;

                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:

                        int xDiff = (int) (event.getRawX() - initialTouchX);
                        int yDiff = (int) (event.getRawY() - initialTouchY);

                        if (xDiff < 10 && yDiff < 10){
                            v.performClick();
                            Intent intent = new Intent(getApplicationContext(), DriveModeActivity.class);
                            intent.setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                        //use gesture detector here

                        break;

                    case MotionEvent.ACTION_MOVE:

                        layoutParams.x = initialX + (int)(event.getRawX() - initialTouchX);
                        layoutParams.y = initialY + (int)(event.getRawY() - initialTouchY);

                        windowManager.updateViewLayout(floatingView, layoutParams);
                        return true;


                }

                return false;
            }
        });


    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(getString(R.string.widget_visible))){
                floatingView.setVisibility(View.VISIBLE);
            }
            if (intent.getAction().equals(getString(R.string.widget_gone))){
                floatingView.setVisibility(View.GONE);
            }
            if (intent.getAction().equals(getString(R.string.current_trip_action))){
                currentTrip = intent.getParcelableExtra(getString(R.string.current_trip_broadcast));
                if (currentTrip != null){
                    try{
                        double distance = Double.parseDouble(currentTrip.getDistanceCovered());
                        DecimalFormat decimalFormat = new DecimalFormat("#.#");
                        tripDistance.setText(decimalFormat.format(distance));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            if (intent.getAction().equals(getString(R.string.stop_widget))){
                stopWidgetService();
            }

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification getNotification() {

        NotificationChannel channel = new NotificationChannel("channel_01", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        Notification.Builder builder = new Notification.Builder(getApplicationContext(), "channel_01").setAutoCancel(true);
        return builder.build();
    }



    private void registerBroadcastReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.widget_gone));
        filter.addAction(getString(R.string.widget_visible));
        filter.addAction(getString(R.string.current_trip_action));
        filter.addAction(getString(R.string.stop_widget));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver, filter);
    }


    public class FloatingWidgetServiceBinder extends Binder{
        public FloatingWidgetService getService(){
            return FloatingWidgetService.this;
        }
    }




}
