package com.sd.sddigiclock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import java.util.List;

/**
 * Created by Brian on 7/5/2019.
 */

public class WidgetBackgroundService extends Service {

    private static final String TAG = "WidgetBackground";
    private static final String CHANNEL_ID = "101";
    private static BroadcastReceiver mMinuteTickReceiver;
    private Context mContext;
    private int appWidgetId;
    private PendingIntent service;

    private DigiClockBroadcastReceiver digiClockBroadcastReceiver;

    @Override
    public IBinder onBind(Intent arg0){
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();

        if(digiClockBroadcastReceiver!= null){
            try {
                unregisterReceiver(digiClockBroadcastReceiver);
            }catch (IllegalArgumentException e){
                e.printStackTrace();
            }
            digiClockBroadcastReceiver = null;
        }
        registerDigiClockBroadcastReceiver();
    }

    private void registerDigiClockBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.BOOT_COMPLETED");
        intentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
        digiClockBroadcastReceiver = new DigiClockBroadcastReceiver();
        getApplicationContext().registerReceiver(digiClockBroadcastReceiver, intentFilter);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, buildForegroundNotification());
        // for Android 8 bring the service to foreground
        //if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
          //  startForeground(1, buildForegroundNotification());
        if(intent != null) {
            if (intent.hasExtra("SHUTDOWN")) {
                if (intent.getBooleanExtra("SHUTDOWN", false)) {

                    if(mMinuteTickReceiver!=null) {
                        unregisterReceiver(mMinuteTickReceiver);
                        mMinuteTickReceiver = null;
                    }
                    stopSelf();
                    return START_NOT_STICKY;
                }
            }
        }

        if(mMinuteTickReceiver==null) {
            registerOnTickReceiver();
        }
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.sd.digiclockwidget";
        String channelName = "DigiClock Background Service";
        NotificationChannel chan = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.sd_white)
                    .setContentTitle("App is running in background")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(2, notification);
        }
    }

    private Notification buildForegroundNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.sd_white)
                .setContentTitle("SD DigiClockWidget")
                .setContentText("Running in the background")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        return builder.build();
    }

    @Override
    public void onDestroy(){
        if(mMinuteTickReceiver!=null) {
            unregisterReceiver(mMinuteTickReceiver);
            mMinuteTickReceiver = null;
        }

        if(digiClockBroadcastReceiver!=null){
            try {
                unregisterReceiver(digiClockBroadcastReceiver);
            }catch(IllegalArgumentException e){
                e.printStackTrace();
            }
            digiClockBroadcastReceiver = null;
        }

        super.onDestroy();
    }

    private void registerOnTickReceiver() {
        mMinuteTickReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent){
                Intent timeTick=new Intent(DigiClockProvider.ACTION_TICK);
                // for Android 8 send an explicit broadcast
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    sendImplicitBroadcast(context, timeTick);
                else
                    sendBroadcast(timeTick);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mMinuteTickReceiver, filter);
    }

    private static void sendImplicitBroadcast(Context ctxt, Intent i) {
        PackageManager pm=ctxt.getPackageManager();
        List<ResolveInfo> matches=pm.queryBroadcastReceivers(i, 0);

        for (ResolveInfo resolveInfo : matches) {
            Intent explicit=new Intent(i);
            ComponentName cn=
                    new ComponentName(resolveInfo.activityInfo.applicationInfo.packageName,
                            resolveInfo.activityInfo.name);

            explicit.setComponent(cn);
            ctxt.sendBroadcast(explicit);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        /*
        int oldOrientation = this.getResources().getConfiguration().orientation;

        if(newConfig.orientation != oldOrientation)
        {
            Log.d("WidgetBGService", "Orientation changed");

            final Intent intent = new Intent(this.getApplicationContext(), UpdateWidgetService.class);

            Bundle extras = intent.getExtras();
            if (extras != null) {
                appWidgetId = extras.getInt(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);
            }

            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            //if (service == null)
            //{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                service = PendingIntent.getService(this.getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
            }else{
                service = PendingIntent.getService(this.getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            }
            startService(intent);
        }

         */
    }


}
