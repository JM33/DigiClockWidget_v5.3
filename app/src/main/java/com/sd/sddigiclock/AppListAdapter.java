package com.sd.sddigiclock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.annotation.NonNull;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 1/15/2016. Modified by Brian Kimmel 2/9/2019
 * source: https://www.reddit.com/r/androiddev/comments/413hkm/list_view_with_all_the_installed_apps_and_their/
 */
public class AppListAdapter implements ListAdapter {

    private final static String TAG = "AppListAdapter";
    private final Context mContext;
    private final PackageManager mPackageManager;
    private final LayoutInflater mLayoutInflater;
    private final List<PackageInfo> mPackageInfos;
    private final HashMap<String, ExtraPackageInfo> mExtraData = new HashMap<>();
    private final HashSet<View> mViewsPendingImages = new HashSet<>();
    private final Object mLock = new Object();
    private final HandlerThread mHandlerThread;
    private final Handler mHandler;
    private final HashMap<String, View> mPendingViews = new HashMap<>();
    private final HashMap<View, String> mPendingViewPackageNames = new HashMap<>();
    private String[][] clockImpls = new String[][]{};

    public AppListAdapter(Context context) {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPackageManager = context.getPackageManager();
        //mPackageInfos = mPackageManager.getInstalledPackages(0);
        mHandlerThread = new HandlerThread("AppListAdapterHandler");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());


        ArrayList<String[]> allClockApps = new ArrayList<>();
        ArrayList<String>packagesList = new ArrayList<>();

        clockImpls = new String[][] {
                {mContext.getString(R.string.p_Title), "com.sd.sddigiclock", "com.sd.sddigiclock.DigiClockPrefs"},

                {"HTC Alarm Clock", "com.htc.android.worldclock", "com.htc.android.worldclock.WorldClockTabControl" },
                {"Google Clock", "com.google.android.deskclock", "com.google.android.deskclock.DeskClock"},
                {"Moto Blur Alarm Clock", "com.motorola.blur.alarmclock",  "com.motorola.blur.alarmclock.AlarmClock"},
                {"Samsung Galaxy Clock","com.sec.android.app.clockpackage","com.sec.android.app.clockpackage.ClockPackage"},
                {"Sony Ericsson Clock", "com.sonyericsson.alarm","com.sonyericsson.alarm.ALARM_ALERT"},
                {"ZTE Clock", "zte.com.cn.alarmclock","zte.com.cn.alarmclock.ALARM_ALERT"},
                {"LG Clock", "com.lge.alarm.alarmclocknew", "com.lge.alarm.alarmclocknew.AlarmClockActivity"},
                {"LG Alarm Clock", "com.lge.clock", "com.lge.clock.AlarmClockActivity"},
                {"Sony Ericsson Xperia Z", "com.sonyericsson.organizer", "com.sonyericsson.organizer.Organizer_WorldClock" },
                {"OnePlus", "com.oneplus.deskclock", "com.oneplus.deskclcok.DeskClock"},

                {"SimpleClock", "com.simplemobiletools.clock", "com.simplemobiletools.clock.Clock"},
                {"Alarm Clock Extreme" , "com.alarmclock.xtreme.free", "com.alarmclock.xtreme.free.AlarmClock"},
                {"The Clock: Alarm Clock & Timer" , "hdesign.theclock", "hdesign.theclock.Clock"},
                {"Zen Flip Clock" , "com.mad.zenflipclock", "com.mad.zenflipclock.Clock"},
                {"Huge Digital Clock" , "com.cama.app.huge80sclock", "com.cama.app.huge80sclock.Clock"},
                {"Alarm Clock for Me" , "com.apalon.myclockfree", "com.apalon.myclockfree.Clock"},
                {"Digital Clock : Bed/Desk Clock" , "com.panagola.app.digitalclock", "com.panagola.app.digitalclock.Clock"},
                {"OmniClock" , "com.maxwen.deskclock", "com.maxwen.deskclock.DeskClock"},

                {"Calendar", "com.google.android.calendar", "com.google.android.calendar.Calendar"},
                //{"SD DigiClock", "com.sd.sddigiclock", "com.sd.sddigiclock.DigiClockPrefs"},
        };


        for (String[] clockImpl : clockImpls) {
            try {
                mPackageManager.getApplicationIcon(clockImpl[1]);
                allClockApps.add(clockImpl);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        removeArrayListDuplicates(allClockApps);
        //Only second entry seems to be added, so adding a second entry of each app
        //allClockApps.addAll(allClockApps);
        for (String[] app:allClockApps) {
            packagesList.add(app[1]);
        }
        //removeArrayListDuplicates(packagesList);

        SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);

        String allPreviousClickApps = prefs.getString("allPreviousClickApps", "");
        String[] previousClickApps = allPreviousClickApps.split(";");
        ArrayList<String> previousClickAppList = new ArrayList<>(Arrays.asList(previousClickApps));
        removeArrayListDuplicates(previousClickAppList);

        for(String pkgName:previousClickAppList) {
            boolean appExists = false;
            for (String str : packagesList) {
                if (str.equals(pkgName)) {
                    appExists = true;
                }
            }
            if (!appExists) {
                packagesList.add(pkgName);
                //packagesList.add(pkgName);
                Log.d(TAG, "Full History apps adding: " + pkgName);
            }
        }

        ArrayList<String> historyApps = new ArrayList<String>();
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if(entry.getKey().contains("ClockButtonApp")){
                Log.d(TAG, entry.getKey() + ": " + entry.getValue().toString());
                historyApps.add(entry.getValue().toString());
            }
            //Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
        }

        removeArrayListDuplicates(historyApps);
        ArrayList<String> duplicateHistoryApps = historyApps;
        //Only second entry seems to be added, so adding a second entry of each app
        historyApps.addAll(duplicateHistoryApps);

        for (String pkgName:historyApps) {
            try {
                mPackageManager.getApplicationIcon(pkgName);
                ApplicationInfo appInfo = mPackageManager.getApplicationInfo(pkgName, 0);
                String appLabel = mPackageManager.getApplicationLabel(appInfo).toString();
                //allClockApps.add(new String[]{appLabel, pkgName});
                boolean appExists = false;
                for (String str : packagesList) {
                    if (str.equals(pkgName)) {
                        appExists = true;
                    }
                }
                if (!appExists) {
                    packagesList.add(pkgName);
                    //packagesList.add(pkgName);
                    Log.d(TAG, "History apps adding: " + pkgName);
                }
                //mHandler.post(loadPackageInfo(mPackageManager.getPackageInfo(pkgName,0)));
                //allClockApps.add(clockImpls[i]);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        //packagesList.addAll(historyApps);

        mPackageInfos = new ArrayList<>();

        for (int i = 0; i < packagesList.size(); i++) {
            try {
                PackageInfo packageInfo = mPackageManager.getPackageInfo(packagesList.get(i), 0);
                //mHandler.post(loadPackageInfo(packageInfo));
                mPackageInfos.add(packageInfo);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        //removeListDuplicates(mPackageInfos);
        //List<PackageInfo> pinfos = mPackageInfos;
        //mPackageInfos.addAll(pinfos);
        Sortbylabel sbl = new Sortbylabel();
        Collections.sort(mPackageInfos, sbl);

        for (int i = 0; i < mPackageInfos.size(); i++) {
            mHandler.post(loadPackageInfo(mPackageInfos.get(i)));
            Log.d("SDDC", "App_name = " + mPackageInfos.get(i).applicationInfo.loadLabel(mPackageManager).toString());
        }
    }

    private void removeArrayListDuplicates(List<?> list)
    {
        int count = list.size();

        for (int i = 0; i < count; i++)
        {
            for (int j = i + 1; j < count; j++)
            {
                if (list.get(i).equals(list.get(j)))
                {
                    list.remove(j--);
                    count--;
                }
            }
        }
    }

    private void removeListDuplicates(List<?> list)
        {
            int count = list.size();

            for (int i = 0; i < count; i++)
            {
                for (int j = i + 1; j < count; j++)
                {
                    if (list.get(i).equals(list.get(j)))
                    {
                        list.remove(j--);
                        count--;
                    }
                }
            }
    }
    /*
    private Runnable loadPackageInfo(String[] mPackage) {
        return new Runnable() {
            @Override
            public void run() {
                String pkgName = mPackage[1];

                PackageInfo pinfo;
                try {
                    Log.d(TAG, "Trying to find package info for package: "+pkgName);
                    pinfo = mPackageManager.getPackageInfo(pkgName, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Could not find package info for package: "+pkgName);
                    return;
                }

                final String packageName = pinfo.packageName;

                synchronized (mLock) {
                    if (mExtraData.containsKey(packageName)) {
                        return;
                    }
                }

                final CharSequence appName = pinfo.applicationInfo.loadLabel(mPackageManager);
                final Drawable drawable = pinfo.applicationInfo.loadIcon(mPackageManager);

                final ExtraPackageInfo extraPackageInfo = new ExtraPackageInfo(packageName, appName, drawable);

                View pendingView;
                synchronized (mLock) {
                    // We save this package's info,
                    mExtraData.put(packageName, extraPackageInfo);
                    // and we clear this view from the views pending updates.
                    pendingView = mPendingViews.remove(packageName);
                    if (pendingView != null) {
                        mPendingViewPackageNames.remove(pendingView);
                    }
                }

                if (pendingView != null) {
                    // Now that this one is loaded the icon info, and it was pending updates, we can update it.
                    // We have to post this to the view's thread.
                    final View view = pendingView;
                    pendingView.post(new Runnable() {
                        @Override
                        public void run() {
                            //updateView(packageInfo, view);
                            updateView(mPackage, view);
                        }
                    });

                }

                if (mPackageInfos.size() == mExtraData.size()) {
                    // We're done loading all of the package infos...
                    mHandlerThread.quit();
                    // TODO: You probably don't want to kill the handler thread if you're going to have a BroadcastListener that will update this AppListAdapter as new packages are installed.
                }

            }
        };
    }
    */

    private Runnable loadPackageInfo(final PackageInfo packageInfo) {
        return new Runnable() {
            @Override
            public void run() {
                final String packageName = packageInfo.packageName;

                synchronized (mLock) {
                    if (mExtraData.containsKey(packageName)) {
                        return;
                    }
                }

                final CharSequence appName = packageInfo.applicationInfo.loadLabel(mPackageManager);
                final Drawable drawable = packageInfo.applicationInfo.loadIcon(mPackageManager);

                final ExtraPackageInfo extraPackageInfo = new ExtraPackageInfo(packageName, appName, drawable);

                View pendingView;
                synchronized (mLock) {
                    // We save this package's info,
                    mExtraData.put(packageName, extraPackageInfo);
                    // and we clear this view from the views pending updates.
                    pendingView = mPendingViews.remove(packageName);
                    if (pendingView != null) {
                        mPendingViewPackageNames.remove(pendingView);
                    }
                }

                if (pendingView != null) {
                    // Now that this one is loaded the icon info, and it was pending updates, we can update it.
                    // We have to post this to the view's thread.
                    final View view = pendingView;
                    pendingView.post(new Runnable() {
                        @Override
                        public void run() {
                            updateView(packageInfo, view);
                        }
                    });

                }

                if (mPackageInfos.size() == mExtraData.size()) {
                    // We're done loading all of the package infos...
                    mHandlerThread.quit();
                    // TODO: You probably don't want to kill the handler thread if you're going to have a BroadcastListener that will update this AppListAdapter as new packages are installed.
                }
            }
        };
    }




    private static class ExtraPackageInfo {
        public ExtraPackageInfo(String packageName, CharSequence appName, Drawable drawable) {
            this.packageName = packageName;
            this.appName = appName;
            this.drawable = drawable;
        }
        public final String packageName;
        public final CharSequence appName;
        public final Drawable drawable;
    }


    /*
    private View updateView(final String[]mPackage, View view) {
        Log.d(TAG, "Updating view: "+ mPackage[1]);
        //final String packageName = packageInfo.packageName;
        final String packageName = mPackage[1];
        final TextView packageTextView = (TextView)view.findViewById(R.id.textView_package_name);
        final TextView appNameTextView = (TextView)view.findViewById(R.id.textView_app_name);
        final ImageView imageView = (ImageView)view.findViewById(R.id.imageView_app_icon);


        ExtraPackageInfo extraPackageInfo;
        synchronized (mLock) {
            extraPackageInfo = mExtraData.get(packageName);
            if (extraPackageInfo == null) {
                // If the extra info isn't loaded yet, we set the package name text,
                // and put loading this particular package at the front of the queue.
                packageTextView.setText(packageName);
                appNameTextView.setText(null);
                imageView.setImageDrawable(null);
                mPendingViews.put(packageName, view);
                mPendingViewPackageNames.put(view, packageName);
                //mHandler.postAtFrontOfQueue(loadPackageInfo(packageInfo));

                return view;
            }
        }

        synchronized (mLock) {
            Drawable icon;
            try {
                icon = mPackageManager.getApplicationIcon(packageName);
                if (icon != null) {
                    Drawable img = resize(icon);
                    imageView.setImageDrawable(img);
                }
                Log.i(TAG, "Icon for " +packageName + " added");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        packageTextView.setText(packageName);
        appNameTextView.setText(mPackage[0]);

        return view;
    }
    */

    private View updateView(final PackageInfo packageInfo, View view) {
        final String packageName = packageInfo.packageName;
        final TextView packageTextView = (TextView)view.findViewById(R.id.textView_package_name);
        final TextView appNameTextView = (TextView)view.findViewById(R.id.textView_app_name);
        final ImageView imageView = (ImageView)view.findViewById(R.id.imageView_app_icon);

        ExtraPackageInfo extraPackageInfo;
        synchronized (mLock) {
            extraPackageInfo = mExtraData.get(packageName);
            if (extraPackageInfo == null) {
                // If the extra info isn't loaded yet, we set the package name text,
                // and put loading this particular package at the front of the queue.
                packageTextView.setText(packageName);
                appNameTextView.setText(null);
                imageView.setImageDrawable(null);
                mPendingViews.put(packageName, view);
                mPendingViewPackageNames.put(view, packageName);
                //mHandler.postAtFrontOfQueue(loadPackageInfo(packageInfo));

                return view;
            }
        }

        packageTextView.setText(extraPackageInfo.packageName);
        appNameTextView.setText(extraPackageInfo.appName);
        Drawable img = resize(extraPackageInfo.drawable);
        imageView.setImageDrawable(img);
        return view;
    }


    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }
    @Override
    public boolean isEnabled(int position) {
        return true;
    }
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        // TODO: You should add a listener for new apps (un)installed so that you can do something with this observer when a new app is installed and update the ListView as necessary
        // Set a global BroadcastListener in the Manifest with intent-filters for Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REMOVED, etc.
    }
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        // TODO: unregister the data set observer
    }
    @Override
    public int getCount() {
        return mPackageInfos.size();
        //return clockImpls.length;
    }
    @Override
    public Object getItem(int position) {
        return mPackageInfos.get(position);
        //return clockImpls[position];
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public boolean hasStableIds() {
        return true;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.layout_app_listview, parent, false);
        } else {
            synchronized (mLock) {
                // We make sure that if we're still waiting for the convertView to load the image, we remove it from the queue of pending views,
                // in order to avoid a race condition for the view.
                String oldPackageName = mPendingViewPackageNames.remove(convertView);
                if (oldPackageName != null) {
                    mPendingViews.remove(oldPackageName);
                }
            }
        }

        return updateView(mPackageInfos.get(position), convertView);
        //return updateView(clockImpls[position], convertView);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }
    @Override
    public int getViewTypeCount() {
        return 1;
    }
    @Override
    public boolean isEmpty() {
        return mPackageInfos.isEmpty();

    }

    @NonNull
    private Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }



    private Drawable resize(Drawable image) {
        Bitmap b = getBitmapFromDrawable(image);
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 96, 96, false);
        return new BitmapDrawable(mContext.getResources(), bitmapResized);
    }

    class Sortbylabel implements Comparator<PackageInfo>
    {
        // Used for sorting in ascending order of
        // app name
        public int compare(PackageInfo a, PackageInfo b)
        {
            return a.applicationInfo.loadLabel(mPackageManager).toString().compareToIgnoreCase(b.applicationInfo.loadLabel(mPackageManager).toString());
        }
    }
}
