package com.gypsee.sdk.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.RequiresApi;

public class NotificationAccessibilityService extends AccessibilityService {

    private String TAG = NotificationAccessibilityService.class.getName();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(23456789, getNotification());
        } else {
            startForeground(34, new Notification());
        }
    }

    private String NOTIFICATION_ACCESSIBILITY_ID = "NOTIFICATION_ACCESSIBILITY_ID";
    private String NOTIFICATION_ACCESSIBILITY_CHANNEL = "NOTIFICATION_ACCESSIBILITY_CHANNEL";

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification getNotification() {

        NotificationChannel channel = new NotificationChannel(NOTIFICATION_ACCESSIBILITY_ID, NOTIFICATION_ACCESSIBILITY_CHANNEL, NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        Notification.Builder builder = new Notification.Builder(getApplicationContext(), NOTIFICATION_ACCESSIBILITY_ID).setAutoCancel(true);
        return builder.build();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        info.notificationTimeout = 100;
        setServiceInfo(info);
    }
}
