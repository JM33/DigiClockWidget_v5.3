package com.sd.sddigiclock;


import static java.util.concurrent.TimeUnit.SECONDS;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.WildcardType;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import androidx.core.app.AlarmManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OutOfQuotaPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class DigiClockBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "DigiClockBroadcastReceiver";

    public static final String REFRESH_WIDGET = "REFRESH_WIDGET";
    private boolean isRegistered;
    private int[] appWidgetIds;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received intent : " + intent.getAction());
        //WorkManager.getInstance(context).pruneWork();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), DigiClockProvider.class.getName());
        appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

        try{
            for(int appWidgetId: appWidgetIds){
                if(appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID){
                    UpdateWidgetView.updateView(context, appWidgetId);
                    Log.i(TAG, "BroadcastReceiver updated widget ID: " + appWidgetId);
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

        } catch (Exception exception){
            Log.d(TAG, "OnReceive Exception: "+ exception);
        }

        /*
        OneTimeWorkRequest.Builder myWorkBuilder =
                new OneTimeWorkRequest.Builder(UpdateWidgetWorker.class);
        OneTimeWorkRequest myWork = myWorkBuilder
                .build();
        WorkManager.getInstance(context)
                .enqueueUniqueWork("UpdateWidgetWork", ExistingWorkPolicy.KEEP, myWork);


        Log.i(TAG, "Broadcast Received");
        */

        /*
        SharedPreferences prefs = context.getSharedPreferences("prefs", 0);
        boolean batterySave = prefs.getBoolean("IgnoreBatterySave", false);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), DigiClockProvider.class.getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        if(appWidgetIds.length > 0) {
            new DigiClockProvider().onUpdate(context, appWidgetManager, appWidgetIds);
            Log.i(TAG, "Updating all widgets via Provider");
        }

        //Intent serviceBG = new Intent(context, WidgetBackgroundService.class);
        if(batterySave) {
            //serviceBG.setAction("android.settings.IGNORE_BATTERY_OPTIMIZATION_SETTINGS");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                //context.startForegroundService(serviceBG);
                //Log.d("DigiClockProvider", "Start service android 31+");
                OneTimeWorkRequest.Builder myWorkBuilder =
                        new OneTimeWorkRequest.Builder(UpdateWidgetWorker.class);
                OneTimeWorkRequest myWork = myWorkBuilder
                        .build();
                WorkManager.getInstance(context)
                        .enqueueUniqueWork("UpdateWidgetWork", ExistingWorkPolicy.REPLACE, myWork);
                Log.d(TAG, "Start OneTimeWorkRequest");

            } catch (android.app.ForegroundServiceStartNotAllowedException e) {
                Log.d(TAG, e.getMessage());
            }

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // for Android 8 start the service in foreground
            //context.startForegroundService(serviceBG);
            OneTimeWorkRequest.Builder myWorkBuilder =
                    new OneTimeWorkRequest.Builder(UpdateWidgetWorker.class);
            OneTimeWorkRequest myWork = myWorkBuilder
                    .build();
            WorkManager.getInstance(context)
                    .enqueueUniqueWork("UpdateWidgetWork", ExistingWorkPolicy.REPLACE, myWork);
            Log.d("DigiClockProvider", "Start service android 26-31");
        } else {
            //context.startService(serviceBG);
            Log.d("DigiClockProvider", "Start service android -26");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DigiClockProvider.scheduleJob(context);
        } else {
            AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(context);
            appWidgetAlarm.startAlarm();
        }
        */

    }

    public void register(final Context context) {
        if (!isRegistered){
            //Log.d(this.toString(), " going to register this broadcast receiver");
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(REFRESH_WIDGET);
            intentFilter.addAction("android.intent.action.BOOT_COMPLETED");
            intentFilter.addAction("android.intent.action.SCREEN_ON");
            intentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
            intentFilter.addAction("android.intent.action.TIME_SET");
            intentFilter.addAction("android.intent.action.TIME_TICK");
            intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
            intentFilter.addAction("android.intent.action.DATE_CHANGED");
            intentFilter.addAction("android.intent.action.DEVICE_IDLE_MODE_CHANGED");
            intentFilter.addAction("android.intent.action.ACTION_DREAMING_STOPPED");
            intentFilter.addAction("android.appwidget.action.APPWIDGET_UPDATE");

            //context.registerReceiver(this, intentFilter);


            boolean listenToBroadcastsFromOtherApps = true;
            int receiverFlags;
            if (listenToBroadcastsFromOtherApps) {
                receiverFlags = ContextCompat.RECEIVER_EXPORTED;
            } else {
                receiverFlags = ContextCompat.RECEIVER_NOT_EXPORTED;
            }
            ContextCompat.registerReceiver(context, this, intentFilter, receiverFlags);

            isRegistered = true;

        }
    }
    public void unregister(final Context context) {
        if (isRegistered) {
            //Log.d(this.toString(), " going to unregister this broadcast receiver");
            context.unregisterReceiver(this);
            isRegistered = false;
        }
    }
}
