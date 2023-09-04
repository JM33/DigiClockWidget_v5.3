package com.sd.sddigiclock;

import android.app.PendingIntent;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static android.content.Context.WINDOW_SERVICE;

public class UpdateWidgetView {
    private static final String TAG = "UpdateWidgetView";
    private static final String ClockOnClick = "clockOnClickTag";
    private static RemoteViews view;
    private static WindowManager mWindowManager;
    private static PackageManager packageManager;

    private static Intent alarmClockIntent;
    private static Intent prefsIntent;


    private static String clockButtonApp;

    private static List<ApplicationInfo> packages;

    private static PendingIntent pendingIntentClock;

    public static void updateView(Context context, int appWidgetId){
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            return;
        }

        mWindowManager =  (WindowManager) context.getSystemService(WINDOW_SERVICE);
        packageManager = context.getPackageManager();

        alarmClockIntent = new Intent();
        prefsIntent = new Intent();

        getPrefs(context, appWidgetId);

        view = new RemoteViews(context.getPackageName(), R.layout.widget_layout);


        view.setViewVisibility(R.id.linearLayoutAdvanced, View.VISIBLE);
        view.setViewVisibility(R.id.BackGround, View.VISIBLE);
        view.setViewVisibility(R.id.linearLayoutClassic, View.GONE);


        Bitmap updateBitmap = WidgetImage.buildClockImage(context, appWidgetId);

        int maxsize = (int)(getScreenWidth() * getScreenHeight() * 4 * 1.5f);
        //Log.d("UpdateWidgetService", "SW - " + getScreenWidth() + " x SH - " + getScreenHeight() + " x 4 x 1.5 = " +maxsize);
        //Log.d("UpdateWidgetService", "Bitmap = " + updateBitmap.getByteCount());
        boolean isOversize = false;
        if(updateBitmap!=null && updateBitmap.getByteCount() > maxsize){
            Toast.makeText(context, context.getResources().getString(R.string.oversize), Toast.LENGTH_LONG).show();
            isOversize = true;
        }

        if(updateBitmap !=null) {
            view.setImageViewBitmap(R.id.BackGround, updateBitmap);
        }




        ComponentName cnpref = new ComponentName("com.sd.sddigiclock", "com.sd.sddigiclock.DigiClockPrefs");
        prefsIntent.setComponent(cnpref);
        prefsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        prefsIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        prefsIntent.setData(Uri.parse(prefsIntent.toUri(Intent.URI_INTENT_SCHEME)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent pendingIntent = PendingIntent.getActivity(context,
                    0, prefsIntent, PendingIntent.FLAG_MUTABLE);
            view.setOnClickPendingIntent(R.id.SettingsButton, pendingIntent);
            //view.setOnClickPendingIntent(R.id.SettingsButton, getPendingSelfIntent(context, ClockOnClick));
        }else{
            PendingIntent pendingIntent = PendingIntent.getActivity(context,
                    0, prefsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.SettingsButton, pendingIntent);
            //view.setOnClickPendingIntent(R.id.SettingsButton, getPendingSelfIntent(context, ClockOnClick));
        }


        Intent appChooserIntent=new Intent(context, AppSelector.class);

        Bundle bundle = new Bundle();
        bundle.putInt("AppWidgetId", appWidgetId);
        appChooserIntent.putExtras(bundle);


        /*
        ComponentName cnClock = new ComponentName("com.sd.sddigiclock", "com.sd.sddigiclock.DigiClockPrefs");
        alarmClockIntent.setComponent(cnClock);
        alarmClockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        alarmClockIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        alarmClockIntent.setData(Uri.parse(alarmClockIntent.toUri(Intent.URI_INTENT_SCHEME)));
        if(clockButtonApp.equals("NONE")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntentClock = PendingIntent.getActivity(context, 0, alarmClockIntent, PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);
                view.setOnClickPendingIntent(R.id.ClockButton, pendingIntentClock);
            }else{
                pendingIntentClock = PendingIntent.getActivity(context, 0, alarmClockIntent, PendingIntent.FLAG_IMMUTABLE);
                view.setOnClickPendingIntent(R.id.ClockButton, pendingIntentClock);
            }
        }else{
            setClockButtonApp(clockButtonApp, appWidgetId, context);
        }
        */
        //Clock Button stopped working so now sending the pending intent info to DigiClockProvider
        view.setOnClickPendingIntent(R.id.ClockButton, DigiClockProvider.getPendingSelfIntent(context,DigiClockProvider.ClockOnClick, appWidgetId));


        Intent refreshIntent = new Intent(context, DigiClockPrefs.class);
        //refreshIntent.setComponent(cnpref);
        refreshIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        refreshIntent.putExtra("Refresh", "Yes");
        //refreshIntent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        refreshIntent.setData(Uri.withAppendedPath(Uri.parse("myapp://widget/id/#togetituniqie" + appWidgetId), String.valueOf(appWidgetId)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent pendingIntentR = PendingIntent.getActivity(context, 0, refreshIntent, PendingIntent.FLAG_IMMUTABLE);
            view.setOnClickPendingIntent(R.id.refreshButton, pendingIntentR);
        }else{
            PendingIntent pendingIntentR = PendingIntent.getService(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.refreshButton, pendingIntentR);
        }




        AppWidgetManager manager = AppWidgetManager.getInstance(context);

        AppWidgetHost appWidgetHost = new AppWidgetHost(context, 1); // for removing phantoms
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(
                "prefs", 0);
        boolean hasWidget = false;

        /*
        int[] appWidgetIDs = manager.getAppWidgetIds(new ComponentName(context, DigiClockProvider.class));
        for (int i = 0; i < appWidgetIDs.length; i++) {
            int id = appWidgetIDs[i];
            String key = String.format("appwidget%d_configured", id);
            if (prefs.getBoolean(key, false)) {
                hasWidget = true;
            } else {
                // delete the phantom appwidget
                appWidgetHost.deleteAppWidgetId(id);
            }
        }

        if (hasWidget) {

        } else {
            // turn off alarms
        }
    */

        manager.updateAppWidget(appWidgetId, view);
        Log.i(TAG, "Update widget : " +appWidgetId);
    }

    private static void getPrefs(Context context, int appWidgetId){
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(
                "prefs", 0);


        clockButtonApp = prefs.getString("ClockButtonApp"+appWidgetId, "NONE");
    }


    public static int getScreenWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }
    public static int getScreenHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static void setClockButtonApp(final String packagename, int appWidgetId, Context context){
        Log.d("SDDC", "Set Clock Button Application " +  " --> " + packagename );
        Log.d("SDDC", "Set Clock Button for widget ID == " +  " --> " + appWidgetId );
        packageManager = context.getPackageManager();
        //packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        view = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        if(packagename.equals("com.sd.sddigiclock")){

            view.setOnClickPendingIntent(R.id.ClockButton, DigiClockProvider.getPendingSelfIntent(context,DigiClockProvider.ClockOnClick, appWidgetId));
            SharedPreferences prefs = context.getSharedPreferences(
                    "prefs", 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString("ClockButtonApp"+appWidgetId, "NONE");
            edit.apply();
            clockButtonApp = packagename;
            ClockSettingsFragment.setCurrentClockAppText();
            return;
        }

        try{
            PackageInfo packageInfo = packageManager.getPackageInfo(packagename, 0);

            if(packagename == null){
                return;
            }
            Log.d(TAG, "Found " +  " --> " + packagename );
            //Log.d("SDDC", "LaunchActivity = " + launchActivity );
            //try {
            //ComponentName cn = new ComponentName(packageInfo.packageName, launchActivity);
            //packageManager.getActivityInfo(cn, PackageManager.GET_META_DATA);
            alarmClockIntent = packageManager.getLaunchIntentForPackage(packageInfo.packageName);
            if(alarmClockIntent == null){
                return;
            }
            //alarmClockIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            alarmClockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            alarmClockIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            alarmClockIntent.setData(Uri.parse(alarmClockIntent.toUri(Intent.URI_INTENT_SCHEME)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntentClock = PendingIntent.getActivity(context, 0, alarmClockIntent, PendingIntent.FLAG_IMMUTABLE);
                if(view !=null) {
                    view.setOnClickPendingIntent(R.id.ClockButton, pendingIntentClock);
                    //view.setOnClickPendingIntent(R.id.ClockButton, DigiClockProvider.getPendingSelfIntent(context, DigiClockProvider.ClockOnClick, appWidgetId));
                    //view.setOnClickPendingIntent(R.id.ClockButton, getPendingSelfIntent(context, ClockOnClick));
                }
            }else{
                pendingIntentClock = PendingIntent.getActivity(context, 0, alarmClockIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                if(view !=null) {
                    view.setOnClickPendingIntent(R.id.ClockButton, pendingIntentClock);
                    //view.setOnClickPendingIntent(R.id.ClockButton, getPendingSelfIntent(context, ClockOnClick));
                }
            }
            SharedPreferences prefs = context.getSharedPreferences(
                    "prefs", 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString("ClockButtonApp"+appWidgetId, packagename);

            //add to history
            String allPreviousClickApps = prefs.getString("allPreviousClickApps", "");
            String[] previousApps = allPreviousClickApps.split(";");
            ArrayList<String> prevAppsList = new ArrayList<>(Arrays.asList(previousApps));

            boolean appExists = false;
            for(String str:previousApps){
                if(str.equals(packagename)){
                    appExists = true;
                }
            }
            if(!appExists){
                prevAppsList.add(packagename);
                StringBuilder newPreviousClickApps = new StringBuilder();
                for(String app:prevAppsList){
                    newPreviousClickApps.append(app).append(";");
                }
                edit.putString("allPreviousClickApps", newPreviousClickApps.toString());
            }


            edit.apply();
            clockButtonApp = packagename;
            //Log.d("TAG", "Found " +  " --> " + packagename + "/" + launchActivity);
            //Log.d("TAG", "Prefs clock app = " +  prefs.getString("ClockButtonApp", "NONE"));


            ClockSettingsFragment.setCurrentClockAppText();

            return;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

            Log.d(TAG, "App does not exist:"+packagename);
            return;
        }

        //Log.d("SDDC", "LOOKING FOR PACKAGE :" + packagename);

    }


}
