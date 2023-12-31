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
import androidx.core.content.ContextCompat;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class UpdateWidgetWorker extends Worker {

    private final static String TAG = "UpdateWidgetWorker";
    private Context mContext;
    int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private final int[] appWidgetIds;



    public UpdateWidgetWorker(
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

        // Do the work here
        try {
            if (DigiClockProvider.digiClockBroadcastReceiver != null) {
                DigiClockProvider.digiClockBroadcastReceiver.unregister(getApplicationContext());
                DigiClockProvider.digiClockBroadcastReceiver = null;
            }
            DigiClockProvider.digiClockBroadcastReceiver = new DigiClockBroadcastReceiver();
            DigiClockProvider.digiClockBroadcastReceiver.register(getApplicationContext());
        }catch (IllegalArgumentException e) {
            DigiClockProvider.digiClockBroadcastReceiver = null;
        }

        try {
            for (int appWidgetId : appWidgetIds) {
                UpdateWidgetView.updateView(mContext, appWidgetId);
                Log.i(TAG, "Worker updated widget ID: " + appWidgetId);
            }

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

            //note: adding an equal alarm will replace existing alarm, two alarms will not be set
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent pendingIntentR = PendingIntent.getBroadcast(mContext, 0, refreshIntent, PendingIntent.FLAG_IMMUTABLE);
                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                AlarmManagerCompat.setExact(alarmManager, AlarmManager.RTC_WAKEUP, currentTimeMillis + millisToNextMin, pendingIntentR);
            }else{
                PendingIntent pendingIntentR = PendingIntent.getService(mContext, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                AlarmManagerCompat.setExact(alarmManager, AlarmManager.RTC_WAKEUP, currentTimeMillis + millisToNextMin, pendingIntentR);
            }

        }
        catch(Exception e){
            Log.e(TAG, e.getMessage());
        }


        if(!WidgetBackgroundService.isMyServiceRunning(mContext, WidgetBackgroundService.class)){

            Intent serviceBG = new Intent(getApplicationContext(), WidgetBackgroundService.class);
            //if(batterySave) {
            serviceBG.setAction("android.settings.IGNORE_BATTERY_OPTIMIZATION_SETTINGS");
            //}
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                try {
                    if(!WidgetBackgroundService.isMyServiceRunning(mContext, WidgetBackgroundService.class)){
                        ContextCompat.startForegroundService(mContext, serviceBG);
                    }else{
                        Log.d(TAG, " BG Service Already Running");
                    }

                }catch(android.app.ForegroundServiceStartNotAllowedException e){
                    Log.d(TAG, e.getMessage());
                }
            } else {
                if(!WidgetBackgroundService.isMyServiceRunning(mContext, WidgetBackgroundService.class)){
                    ContextCompat.startForegroundService(mContext, serviceBG);
                }else{
                    Log.d(TAG, " BG Service Already Running");
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                DigiClockProvider.scheduleJob(getApplicationContext());
            } else {
                AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(getApplicationContext());
                appWidgetAlarm.startAlarm();
            }
            Log.d(TAG, "UpdateWidgetWorker Started BG Service");
        }else{
            Log.d(TAG, "BG Service already running");
        }


        scheduleUpdate(mContext);
        // Indicate whether the work finished successfully with the Result
        return Result.success();
    }

    private void scheduleUpdate(Context mContext) {
        Calendar calendar = Calendar.getInstance();
        long currentTimeMillis = calendar.getTimeInMillis();
        int sec = calendar.get(Calendar.SECOND);
        int millis = calendar.get(Calendar.MILLISECOND);
        int secsToNextMin = 59-sec;
        int millisToNextSec = 999-millis;
        int millisToNextMin = secsToNextMin*1000 + millisToNextSec;


        OneTimeWorkRequest.Builder myWorkBuilder =
                new OneTimeWorkRequest.Builder(ScheduleUpdateWorker.class);
        OneTimeWorkRequest myWork = myWorkBuilder
                .setInitialDelay(millisToNextMin, TimeUnit.MILLISECONDS)
                .build();
        //WorkManager.getInstance(getApplicationContext()).cancelAllWorkByTag("UpdateWidgetWork");
        WorkManager.getInstance(getApplicationContext())
                .enqueueUniqueWork("ScheduleUpdateWork", ExistingWorkPolicy.KEEP, myWork);
        Log.d(TAG, "Schedule Update Complete");

    }
}
