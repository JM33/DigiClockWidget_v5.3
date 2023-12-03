package com.sd.sddigiclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.AlarmManagerCompat;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class ScheduleUpdateWorker extends Worker {

    private final static String TAG = "ScheduleUpdateWorker";
    private Context mContext;
    int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private final int[] appWidgetIds;

    public ScheduleUpdateWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);


        mContext = context;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        ComponentName thisAppWidget = new ComponentName(mContext.getPackageName(), DigiClockProvider.class.getName());
        appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
    }

    @Override
    public Result doWork() {

        /*
        Calendar calendar = Calendar.getInstance();
        long currentTimeMillis = calendar.getTimeInMillis();
        int sec = calendar.get(Calendar.SECOND);
        int millis = calendar.get(Calendar.MILLISECOND);
        int secsToNextMin = 59-sec;
        int millisToNextSec = 999-millis;
        int millisToNextMin = secsToNextMin*1000 + millisToNextSec;


        Intent refreshIntent = new Intent(mContext, DigiClockBroadcastReceiver.class);
        refreshIntent.setPackage(mContext.getPackageName());
        refreshIntent.setAction(DigiClockBroadcastReceiver.REFRESH_WIDGET);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent pendingIntentR = PendingIntent.getBroadcast(mContext, 0, refreshIntent, PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            AlarmManagerCompat.setExact(alarmManager, AlarmManager.RTC_WAKEUP, currentTimeMillis + millisToNextMin, pendingIntentR);
        }else{
            PendingIntent pendingIntentR = PendingIntent.getService(mContext, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            AlarmManagerCompat.setExact(alarmManager, AlarmManager.RTC_WAKEUP, currentTimeMillis + millisToNextMin, pendingIntentR);
        }

         */


        OneTimeWorkRequest.Builder myWorkBuilder =
                new OneTimeWorkRequest.Builder(UpdateWidgetWorker.class);
        OneTimeWorkRequest myWork = myWorkBuilder
                .build();
        //WorkManager.getInstance(getApplicationContext()).cancelAllWorkByTag("UpdateWidgetWork");
        WorkManager.getInstance(getApplicationContext())
                .enqueueUniqueWork("UpdateWidgetWork", ExistingWorkPolicy.KEEP, myWork);
        Log.d(TAG, "ScheduleUpdateWorker Work Complete");

        // Indicate whether the work finished successfully with the Result
        return Result.success();
    }

}
