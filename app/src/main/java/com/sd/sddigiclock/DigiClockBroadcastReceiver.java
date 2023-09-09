package com.sd.sddigiclock;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.WildcardType;
import java.util.concurrent.TimeUnit;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class DigiClockBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "DigiClockBroadcastReceiver";

    public static final String REFRESH_WIDGET = "REFRESH_WIDGET";
    private boolean isRegistered;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received intent : " + intent.getAction());

        OneTimeWorkRequest.Builder myWorkBuilder =
                new OneTimeWorkRequest.Builder(UpdateWidgetWorker.class);
        OneTimeWorkRequest myWork = myWorkBuilder
                .build();
        WorkManager.getInstance(context)
                .enqueueUniqueWork("UpdateWidgetWork", ExistingWorkPolicy.KEEP, myWork);
        Log.i(TAG, "Broadcast Received");

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
            intentFilter.addAction("android.intent.action.BOOT_COMPLETED");
            intentFilter.addAction("android.intent.action.SCREEN_ON");
            intentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
            intentFilter.addAction(REFRESH_WIDGET);
            context.registerReceiver(this, intentFilter);
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
