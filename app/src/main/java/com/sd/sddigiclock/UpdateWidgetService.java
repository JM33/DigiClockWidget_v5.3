package com.sd.sddigiclock;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/*
 * Author Brian Kimmel
 * Copyright Silent Designs, all rights reserved
 */
public class UpdateWidgetService extends Service {
	private static final String TAG = "UpdateWidgetService";
	private static final int JOB_ID = 101;
	private static final String CHANNEL_ID = "101";
	public static Context mContext;
	private String ampm;
	private String shours;
	private String sminutes;
	private String sdate;
	private String month_name;
	
	private int clocktextsize;
	private int datetextsize;
	private int cColor;
	private int dColor;
	private boolean dateMatchClockColor;
	//private PackageManager packageManager;
	public static Intent alarmClockIntent;
	
	private Intent prefsIntent;
	
	private int day;
	private int year;
	private int month;
	private int Bg;
	boolean ampmshown;
	boolean dateshown;
	
	int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	private boolean fillbg;
	private static PackageManager packageManager;
	private boolean show24;
	private int mFont;
	private String Fontfile;
	private int bgColor;
	private static RemoteViews view;
	private WindowManager mWindowManager;
	private Display mDisplay;
	private int dateheight;
	private int clockheight;
	private int dateFormatIndex;
	private static boolean classicMode;

	private static String clockButtonApp;

	static List<ApplicationInfo> packages;

	private static Handler mHandler;
	PendingIntent service;

	public static AlarmManager alarmManager;
	//private String dateFormat;

	boolean mIsPortraitOrientation;
	public static boolean isOversize;
	public int backgroundRadius = 150;
	private int mWidgetWidthPerOrientation;
	private int mWidgetHeightPerOrientation;
	public boolean useHomeColors;


	@Override
	    public void onCreate()  
	    {

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				startMyOwnForeground();
				Log.i(TAG, "Started service as foreground");
			}
	        mContext = this.getApplicationContext();

			mContext.setTheme(R.style.Theme_MaterialYouColors);


	        mHandler = new Handler();
	        
	        mWindowManager =  (WindowManager) getSystemService(WINDOW_SERVICE);
	        mDisplay = mWindowManager.getDefaultDisplay();
	        packageManager = this.getPackageManager();
	        
	        alarmClockIntent = new Intent();
	        prefsIntent = new Intent();
	        /*
	        mContext = this.getApplicationContext();
	        TimeText = new TextView(mContext);
	        TimeText.setId(R.id.update);
	        FrameLayout Flayout = new FrameLayout(mContext);
	        Flayout.setId(R.id.FrameLayout);
	        */
	        //Log.i(LOG, "Service onCreate");

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

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			startMyOwnForeground();
			Log.i(TAG, "Started service as foreground");
		}

		if(intent == null){
			Log.d(TAG, "No Intent onStartCommand");
			return START_REDELIVER_INTENT;
		}
		if (intent.getExtras() != null) {
			Bundle extras = intent.getExtras();
			appWidgetId = extras.getInt(
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
			Log.i(TAG, "Service Started awId =" + Integer.toString(appWidgetId));
		}
		if(appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID){
			return super.onStartCommand(intent, flags, startId);
		}

		UpdateWidgetView.updateView(mContext, appWidgetId);

		/*

		SharedPreferences prefs = getApplicationContext().getSharedPreferences(
				"prefs", 0);
		classicMode = prefs.getBoolean("ClassicMode"+appWidgetId, false);
		//Log.d("UWS", "CLASSIC MODE = " + classicMode + "!!!!!!!!!!!!!!!!!!!!XXXXXXXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxXXXXXXXX!!!!!!!!!!!!!");
		dateshown = prefs.getBoolean("ShowDate"+appWidgetId, true);
		ampmshown = prefs.getBoolean("ShowAMPM"+appWidgetId, true);
		//classicMode = ampmshown;
		show24 = prefs.getBoolean("Show24"+appWidgetId, false);
		fillbg = true;
		clocktextsize = prefs.getInt("ClockTextSize"+appWidgetId, 15);
		datetextsize = prefs.getInt("DateTextSize"+appWidgetId, 12);
		dateMatchClockColor = prefs.getBoolean("DateMatchClockColor"+appWidgetId, true);
		dateFormatIndex = prefs.getInt("DateFormat" +appWidgetId, 2);

		cColor = prefs.getInt("cColor"+appWidgetId, -1);
		if(dateMatchClockColor){
			dColor = cColor;
		}else {
			dColor = prefs.getInt("dColor" + appWidgetId, -1);
		}
		bgColor = prefs.getInt("bgColor"+appWidgetId, Color.BLACK);


		Bg = prefs.getInt("Bg"+appWidgetId, 3);
		Fontfile = prefs.getString("Font"+appWidgetId, "Roboto-Regular.ttf");
		mFont = prefs.getInt("Fontnum"+appWidgetId, 0);
		useHomeColors = prefs.getBoolean("UseHomeColors"+appWidgetId, false);

		clockButtonApp = prefs.getString("ClockButtonApp"+appWidgetId, "NONE");
		//Log.d("SDDC", "ClockApp saved = " + clockButtonApp);
		setText();



		view = new RemoteViews(getPackageName(), R.layout.widget_layout);


			view.setViewVisibility(R.id.linearLayoutAdvanced, View.VISIBLE);
			view.setViewVisibility(R.id.BackGround, View.VISIBLE);
			view.setViewVisibility(R.id.linearLayoutClassic, View.GONE);


			Bitmap updateBitmap = WidgetImage.buildClockImage(mContext, appWidgetId);

			int maxsize = (int)(getScreenWidth() * getScreenHeight() * 4 * 1.5f);
			//Log.d("UpdateWidgetService", "SW - " + getScreenWidth() + " x SH - " + getScreenHeight() + " x 4 x 1.5 = " +maxsize);
			//Log.d("UpdateWidgetService", "Bitmap = " + updateBitmap.getByteCount());
			isOversize = false;
			if(updateBitmap!=null && updateBitmap.getByteCount() > maxsize){
				Toast.makeText(mContext, mContext.getResources().getString(R.string.oversize), Toast.LENGTH_LONG).show();
				isOversize = true;
			}
			//ByteArrayOutputStream out = new ByteArrayOutputStream();
			//updateBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
			//Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
			if(updateBitmap !=null) {
				view.setImageViewBitmap(R.id.BackGround, updateBitmap);
			}




		ComponentName cnpref = new ComponentName("com.sd.sddigiclock", "com.sd.sddigiclock.DigiClockPrefs");
		prefsIntent.setComponent(cnpref);
		prefsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		prefsIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		prefsIntent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));


		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			PendingIntent pendingIntent = PendingIntent.getActivity(mContext,
					0, prefsIntent, PendingIntent.FLAG_MUTABLE);
			view.setOnClickPendingIntent(R.id.SettingsButton, pendingIntent);
		}else{
			PendingIntent pendingIntent = PendingIntent.getActivity(mContext,
					0, prefsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			view.setOnClickPendingIntent(R.id.SettingsButton, pendingIntent);
		}





// the getLaunchIntentForPackage returns an intent that you can use with startActivity()


		String clockImpls[][] = {
				{"HTC Alarm Clock", "com.htc.android.worldclock", "com.htc.android.worldclock.WorldClockTabControl" },
				{"Standard Alarm Clock", "com.android.deskclock", "com.android.deskclock.DeskClock"},
				{"Froyo Nexus Alarm Clock", "com.google.android.deskclock", "com.android.deskclock.DeskClock"},
				{"Moto Blur Alarm Clock", "com.motorola.blur.alarmclock",  "com.motorola.blur.alarmclock.AlarmClock"},
				//{"Samsung Galaxy Clock","com.sec.android.app.clockpackage","com.sec.android.app.clockpackage.ClockPackage",} //
				//{"ICS Nexus Alarm Clock", "com.google.android.deskclock", "com.android.deskclockgoogle.DeskClockGoogle"}
		};
		boolean foundClockImpl = false;

		Intent appchooserintent=new Intent(UpdateWidgetService.this,AppSelector.class);

		Bundle bundle = new Bundle();
		bundle.putInt("AppWidgetId", appWidgetId);
		appchooserintent.putExtras(bundle);



		if(clockButtonApp.equals("NONE")){
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
				PendingIntent pendingIntentC = PendingIntent.getActivity(mContext, 0, prefsIntent, PendingIntent.FLAG_MUTABLE);
				view.setOnClickPendingIntent(R.id.ClockButton, pendingIntentC);
			}else{
				PendingIntent pendingIntentC = PendingIntent.getActivity(mContext, 0, prefsIntent, 0);
				view.setOnClickPendingIntent(R.id.ClockButton, pendingIntentC);
			}

		}else{
			setClockButtonApp(clockButtonApp, appWidgetId, mContext);

		}



		Intent refreshIntent = new Intent(mContext, DigiClockPrefs.class);
		//refreshIntent.setComponent(cnpref);
		refreshIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		refreshIntent.putExtra("Refresh", "Yes");
		//refreshIntent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
		refreshIntent.setData(Uri.withAppendedPath(Uri.parse("myapp://widget/id/#togetituniqie" + appWidgetId), String.valueOf(appWidgetId)));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			PendingIntent pendingIntentR = PendingIntent.getActivity(mContext, 0, refreshIntent, PendingIntent.FLAG_MUTABLE);
			view.setOnClickPendingIntent(R.id.refreshButton, pendingIntentR);
		}else{
			PendingIntent pendingIntentR = PendingIntent.getService(mContext, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			view.setOnClickPendingIntent(R.id.refreshButton, pendingIntentR);
		}





		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		//ComponentName thisWidget = new ComponentName(this, DigiClockProvider.class);
		if(!isOversize) {
			manager.updateAppWidget(appWidgetId, view);
			Log.i(TAG, "Update widget : " +appWidgetId);
		}

		//registerOneTimeAlarm(service, 1000*60, false);
		Log.i(TAG, "UpdateWidgetService Setting Alarm for 1 minute!!!!?X");
		//return START_STICKY;


		 */
		return super.onStartCommand(intent, flags, startId);
	}




	@Override
	    public IBinder onBind(Intent intent)  
	    {  
	        return null;  
	    }





}
