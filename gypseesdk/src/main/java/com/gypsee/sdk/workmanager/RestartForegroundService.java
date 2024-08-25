package com.gypsee.sdk.workmanager;

import static android.content.Context.ACTIVITY_SERVICE;

import static androidx.core.content.ContextCompat.getSystemService;

import static com.gypsee.sdk.config.MyPreferenece.GYPSEE_PREFERENCES;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;

import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.services.ForegroundService;

public class RestartForegroundService extends Worker {
    private Context context;
    public RestartForegroundService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    private static final String TAG = RestartForegroundService.class.getSimpleName();

    @NonNull
    @Override
    public Result doWork() {

       MyPreferenece myPreferenece = new MyPreferenece(GYPSEE_PREFERENCES, context);

        try {
            if (!isForegroundRunning()&& myPreferenece.getUser()!=null) {
                Log.e(TAG, "Foreground service not running, restarting");
                Intent intent = new Intent(context, ForegroundService.class);
                context.startService(intent);
            }else{
                Log.e(TAG, "Foreground service already running or do not need to start service");
            }
            return Result.success();
        }catch (Exception e){
            e.printStackTrace();
            return Result.failure();
        }
    }

    private boolean isForegroundRunning(){
        boolean isServiceRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> l = am.getRunningServices(200);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : l) {
            if (runningServiceInfo.service.getClassName().equals(ForegroundService.class.getName())) {
                isServiceRunning = true;
            }
        }
        return isServiceRunning;
    }
}
