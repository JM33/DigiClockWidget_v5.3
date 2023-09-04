package com.sd.sddigiclock;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UpdateWidgetWorker extends Worker {

    private final static String TAG = "UpdateWidgetWorker";

    private Context mContext;
    int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private final int[] appWidgetIds;

    private static DigiClockBroadcastReceiver digiClockBroadcastReceiver;

    public UpdateWidgetWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);


        mContext = context;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        ComponentName thisAppWidget = new ComponentName(mContext.getPackageName(), DigiClockProvider.class.getName());
        appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

        try {
            if (digiClockBroadcastReceiver != null) {
                digiClockBroadcastReceiver.unregister(context);
                digiClockBroadcastReceiver = null;
            }
            digiClockBroadcastReceiver = new DigiClockBroadcastReceiver();
            digiClockBroadcastReceiver.register(context);
        }catch (IllegalArgumentException e) {
            digiClockBroadcastReceiver = null;
        }

    }

    @Override
    public Result doWork() {

        // Do the work here

        for(int appWidgetId: appWidgetIds){
            if(appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID){
                UpdateWidgetView.updateView(mContext, appWidgetId);
                Log.i(TAG, "Worker updated widget ID: " + appWidgetId);
            }
        }

        //Intent intent = new Intent(mContext.getApplicationContext(), DigiClockProvider.class);
        //intent.setAction(DigiClockProvider.ACTION_TICK);
        //mContext.sendBroadcast(intent);

        scheduleUpdate(getApplicationContext());

        // Indicate whether the work finished successfully with the Result
        return Result.success();
    }

    public static void scheduleUpdate(Context context) {

        Calendar calendar = Calendar.getInstance();
        long currentTimeMillis = calendar.getTimeInMillis();
        int sec = calendar.get(Calendar.SECOND);
        int millis = calendar.get(Calendar.MILLISECOND);
        int secsToNextMin = 59-sec;
        int millisToNextSec = 999-millis;
        int millisToNextMin = secsToNextMin*1000 + millisToNextSec;

        /*
        Log.i(TAG, "Current timeMillis = " + currentMillis);
        Log.i(TAG, "Current sec = " + sec);
        Log.i(TAG, "Current millis = " + millis);
        Log.i(TAG, "Sec to Next Min = " + secsToNextMin);
        Log.i(TAG, "Millis To Next Sec = " + millisToNextSec);
        Log.i(TAG, "Millis To Next Min = " + millisToNextMin);
        */

        Log.d(TAG, "WidgetWorker scheduledUpdate");

        OneTimeWorkRequest.Builder myWorkBuilder =
                new OneTimeWorkRequest.Builder(WidgetWorker.class);

        OneTimeWorkRequest myWork = myWorkBuilder
                .setInitialDelay(millisToNextMin, TimeUnit.MILLISECONDS)
                //.setInitialDelay(10, TimeUnit.SECONDS)
                .build();
        WorkManager.getInstance(context)
                .enqueueUniqueWork("ScheduledUpdateWidgetWork", ExistingWorkPolicy.REPLACE, myWork);
    }


}
