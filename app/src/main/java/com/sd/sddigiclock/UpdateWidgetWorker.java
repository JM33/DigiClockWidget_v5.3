package com.sd.sddigiclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.AlarmManagerCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OutOfQuotaPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
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

    private static WorkerParameters workerParams;

    public UpdateWidgetWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);


        mContext = context;
        workerParams = params;
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
        try{
            for(int appWidgetId: appWidgetIds){
                if(appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID){
                    UpdateWidgetView.updateView(mContext, appWidgetId);
                    Log.i(TAG, "Worker updated widget ID: " + appWidgetId);
                    //Toast.makeText(mContext, "Worker updated widget ID: " + appWidgetId, Toast.LENGTH_SHORT);
                }
            }

        //Intent intent = new Intent(mContext.getApplicationContext(), DigiClockProvider.class);
        //intent.setAction(DigiClockProvider.ACTION_TICK);
        //mContext.sendBroadcast(intent);

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

        scheduleUpdate(mContext);

        // Indicate whether the work finished successfully with the Result
            return Result.success();
        } catch (Exception exception){
            Log.d(TAG, "doWork Exception: "+ exception);
            return Result.failure();
        }

    }

    @Override
    public void onStopped() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //Toast.makeText(mContext, "STOPPED WORK: " + StopReason(this.getStopReason()), Toast.LENGTH_LONG);
            Log.d(TAG, "Stop Reason: "+ StopReason(this.getStopReason()));
        }

        doWork();
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

        Log.d(TAG, "WidgetWorker scheduledUpdate - next update in " + millisToNextMin + " milliseconds");


        Intent refreshIntent = new Intent(context, DigiClockBroadcastReceiver.class);
        refreshIntent.setPackage(context.getPackageName());
        refreshIntent.setAction(DigiClockBroadcastReceiver.REFRESH_WIDGET);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent pendingIntentR = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            AlarmManagerCompat.setExact(alarmManager, AlarmManager.RTC_WAKEUP, currentTimeMillis + millisToNextMin, pendingIntentR);
        }else{
            PendingIntent pendingIntentR = PendingIntent.getService(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            AlarmManagerCompat.setExact(alarmManager, AlarmManager.RTC_WAKEUP, currentTimeMillis + millisToNextMin, pendingIntentR);
        }


        OneTimeWorkRequest.Builder myWorkBuilder =
                new OneTimeWorkRequest.Builder(WidgetWorker.class);

        OneTimeWorkRequest myWork = myWorkBuilder
                .setInitialDelay(millisToNextMin, TimeUnit.MILLISECONDS)
                //.setInitialDelay(10, TimeUnit.SECONDS)
                .build();
        WorkManager.getInstance(context)
                .enqueueUniqueWork("ScheduledUpdateWidgetWork", ExistingWorkPolicy.KEEP, myWork);
    }


    public static String StopReason(int stopReason) {
        String reason = "STOP_REASON_UNKNOWN";
        switch (stopReason) {
            case WorkInfo.STOP_REASON_NOT_STOPPED :
                reason ="STOP_REASON_NOT_STOPPED";
            case WorkInfo.STOP_REASON_CANCELLED_BY_APP:
                reason ="STOP_REASON_CANCELLED_BY_APP";
            case WorkInfo.STOP_REASON_PREEMPT :
                reason ="STOP_REASON_PREEMPT";
            case WorkInfo.STOP_REASON_TIMEOUT :
                reason ="STOP_REASON_TIMEOUT";
            case WorkInfo.STOP_REASON_DEVICE_STATE:
                reason ="STOP_REASON_DEVICE_STATE";
            case WorkInfo.STOP_REASON_CONSTRAINT_BATTERY_NOT_LOW :
                reason = "STOP_REASON_CONSTRAINT_BATTERY_NOT_LOW";
            case WorkInfo.STOP_REASON_CONSTRAINT_CHARGING:
                reason ="STOP_REASON_CONSTRAINT_CHARGING";
            case WorkInfo.STOP_REASON_CONSTRAINT_CONNECTIVITY:
                reason ="STOP_REASON_CONSTRAINT_CONNECTIVITY";
            case WorkInfo.STOP_REASON_CONSTRAINT_DEVICE_IDLE:
                reason ="STOP_REASON_CONSTRAINT_DEVICE_IDLE";
            case WorkInfo.STOP_REASON_CONSTRAINT_STORAGE_NOT_LOW:
                reason ="STOP_REASON_CONSTRAINT_STORAGE_NOT_LOW";
            case WorkInfo.STOP_REASON_QUOTA :
                reason ="STOP_REASON_QUOTA";
            case WorkInfo.STOP_REASON_BACKGROUND_RESTRICTION :
                reason ="STOP_REASON_BACKGROUND_RESTRICTION";
            case WorkInfo.STOP_REASON_APP_STANDBY :
                reason ="STOP_REASON_APP_STANDBY";
            case WorkInfo.STOP_REASON_USER :
                reason ="STOP_REASON_USER";
            case WorkInfo.STOP_REASON_SYSTEM_PROCESSING :
                reason ="STOP_REASON_SYSTEM_PROCESSING";
            case WorkInfo.STOP_REASON_ESTIMATED_APP_LAUNCH_TIME_CHANGED :
                reason ="STOP_REASON_ESTIMATED_APP_LAUNCH_TIME_CHANGED";
        }
        return reason;
    }

}
