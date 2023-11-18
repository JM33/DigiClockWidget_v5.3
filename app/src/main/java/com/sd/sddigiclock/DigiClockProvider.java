package com.sd.sddigiclock;

import static android.app.Activity.RESULT_OK;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import androidx.core.app.AlarmManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OutOfQuotaPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.WorkerParameters;

/*
 * Author Brian Kimmel
 * Copyright Silent Designs, all rights reserved
 */
public class DigiClockProvider extends AppWidgetProvider {

	private static final String TAG = "DigiClockProvider";

	private static PendingIntent service = null;

	public static final String ClockOnClick = "clockOnClickTag";

	public static final String ACTION_TICK = "CLOCK_TICK";

	public static final String REFRESH_WIDGET = "REFRESH_WIDGET";
	public static final String SETTINGS_CHANGED = "SETTINGS_CHANGED";
	public static final String JOB_TICK = "JOB_CLOCK_TICK";
	private static String clockButtonApp;
	private static String sminutes;
	private static String ampm;
	private static String sdate;
	private static Intent prefsIntent;
	private SharedPreferences preferences;

	private static RemoteViews view;
	static boolean dateshown;
	static boolean ampmshown;
	static boolean show24;
	static boolean fillbg;
	static int clocktextsize;
	static int datetextsize;
	static boolean dateMatchClockColor;
	static int dateFormatIndex;

	static int cColor;
	static int dColor;
	static int bgColor;

	static int Bg;
	String Fontfile;
	static int mFont;
	public static float widgetSizeWidth;
	public static float widgetSizeHeight;
	public static DigiClockBroadcastReceiver digiClockBroadcastReceiver;

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		//called when widgets are deleted
		//see that you get an array of widgetIds which are deleted
		//so handle the delete of multiple widgets in an iteration

		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {


		}
		super.onDeleted(context, appWidgetIds);
	}


	@Override
	public void onEnabled(Context context) {
		//runs when all of the first instance of the widget are placed
		//on the home screen



		AppWidgetManager mgr = AppWidgetManager.getInstance(context);
		ComponentName cn = new ComponentName(context, DigiClockProvider.class);
		int [] awids = mgr.getAppWidgetIds(cn);
		onUpdate(context, mgr, awids);

		for (int i = 0; i < awids.length; i++){
			onUpdate(context, mgr, awids);
			Log.i(TAG, "Enabled ID = " + Integer.toString(awids[i]));
		}
		/*
		//PackageManager pm = context.getPackageManager();
		//pm.setComponentEnabledSetting(new ComponentName("com.sd.sddigiclock", ".DigiClockProvider"),
		//		PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
		//		PackageManager.DONT_KILL_APP);

		*/
		Log.i(TAG, "DigiClockProvider onEnabled");

		restartAll(context);

		//digiClockBroadcastReceiver.register(context);


	}

	private void registerDigiClockBroadcastReceiver(Context context){
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.BOOT_COMPLETED");
		intentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
		DigiClockBroadcastReceiver receiver = new DigiClockBroadcastReceiver();
		context.registerReceiver(receiver, intentFilter);
	}

	@Override
	public void onAppWidgetOptionsChanged(Context context,
										  AppWidgetManager appWidgetManager,
										  int appWidgetId,
										  Bundle newOptions) {
		Bundle bundle = appWidgetManager.getAppWidgetOptions(appWidgetId);
		String sizes = bundle.getString(AppWidgetManager.OPTION_APPWIDGET_SIZES);
		Log.i("DCP", "sizes = " + sizes);
		String width = bundle.getString(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
		Log.i("DCP", "w = " + width);
		String height = bundle.getString(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
		Log.i("DCP", "h = " + height);

		ComponentName thisAppWidget = new ComponentName(context.getPackageName(), DigiClockProvider.class.getName());
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
		onUpdate(context, appWidgetManager, appWidgetIds);
		Log.i("DCProvider", "AppWidgetOptionsChanged handled!");
	}
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.w(TAG, "onUpdate method called");

		try{
			for(int appWidgetId: appWidgetIds){
				if(appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID){
					UpdateWidgetView.updateView(context, appWidgetId);
					Log.i(TAG, "DigiClockProvider updated widget ID: " + appWidgetId);
					//Toast.makeText(mContext, "Worker updated widget ID: " + appWidgetId, Toast.LENGTH_SHORT);
				}
			}
		} catch (Exception exception){
			Log.d(TAG, "onUpdate Exception: "+ exception);
		}
		restartAll(context);
	}

	 @Override
	    public void onDisabled(Context context)  
	    {
	    	/*
			//final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

			//m.cancel(service);

			Log.d("SDCP","Clearing all preferences for:" + "prefs");
			SharedPreferences prefs=context.getSharedPreferences("prefs", 0);
			Log.d("SDCP","Number of preferences:" + prefs.getAll().size());
			SharedPreferences.Editor prefsEdit = prefs.edit();
			prefsEdit.clear();
			//finally commit the values
			prefsEdit.commit();

			//PackageManager pm = context.getPackageManager();
			//pm.setComponentEnabledSetting(new ComponentName("com.sd.sddigiclock", ".DigiClockProvider"),
			//		PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
			//		PackageManager.DONT_KILL_APP);
			*/

			/*
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
				jobScheduler.cancelAll();
			} else {
				// stop alarm
				AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(context.getApplicationContext());
				appWidgetAlarm.stopAlarm();
			}

			 */

			//Intent serviceBG = new Intent(context.getApplicationContext(), WidgetBackgroundService.class);
			//serviceBG.putExtra("SHUTDOWN", true);
			//context.getApplicationContext().startService(serviceBG);
			//context.getApplicationContext().stopService(serviceBG);

			//digiClockBroadcastReceiver.unregister(context);
	    }  
	 @Override
     public void onReceive(Context context, Intent intent) {
		//super.onReceive(context, intent);
		//all the intents get handled by this method
	     //mainly used to handle self created intents, which are not
	     //handled by any other method
		 
	     //the super call delegates the action to the other methods
	    
	     //for example the APPWIDGET_UPDATE intent arrives here first
	     //and the super call executes the onUpdate in this case
	     //so it is even possible to handle the functionality of the
	     //other methods here
	     //or if you don't call super you can overwrite the standard
	     //flow of intent handling 
		 //Log.i(LOG, "intent = " + intent.toString());

		 AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		 ComponentName thisAppWidget = new ComponentName(context.getPackageName(), DigiClockProvider.class.getName());
		 int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

		 if (intent.getAction().equals(SETTINGS_CHANGED)) {
			 onUpdate(context, appWidgetManager, appWidgetIds);
			 //if (appWidgetIds.length > 0) {
			 restartAll(context);
			 //}
			 Log.i(TAG, "Settings Change Action");
		 }

		 //onUpdate(context, appWidgetManager, appWidgetIds);

		 if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
				 || intent.getAction().equals(Intent.ACTION_DATE_CHANGED)
				 || intent.getAction().equals(Intent.ACTION_TIME_CHANGED)
		 ) {
			 //restartAll(context);
			 onUpdate(context, appWidgetManager, appWidgetIds);
			 //UpdateWidgetWorker.scheduleUpdate(context);
			 Log.i(TAG, "Intent Action = " + intent.getAction());
		 }


		 if (intent.getAction().equals(JOB_TICK) || intent.getAction().equals(ACTION_TICK)
				 //|| intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
				 ) {
			 //restartAll(context);
			 onUpdate(context, appWidgetManager, appWidgetIds);
			 Log.i(TAG, "Intent Action = " + intent.getAction());
		 }

		 if (intent.getAction().equals(ClockOnClick)){
			 //your onClick action is here
			 Log.d(TAG, "Clock Button Clicked");


			 int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			 Log.d(TAG, "ClockOnClick Widget ID = " + appWidgetId);
			 if(appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID){

				 return;
			 }



			 SharedPreferences prefs = context.getSharedPreferences("prefs", 0);
			 String clockApp = prefs.getString("ClockButtonApp"+appWidgetId, "NONE");
			 //String clockApp = "com.google.android.deskclock";

			 if(!clockApp.equals("NONE")){
				 try {
					 PackageInfo packageInfo = context.getPackageManager().getPackageInfo(clockApp, 0);
					 Log.d(TAG, "Package Found " + " --> " + clockApp);
					 Intent alarmClockIntent = context.getPackageManager().getLaunchIntentForPackage(packageInfo.packageName);
					 alarmClockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					 context.startActivity(alarmClockIntent);
					 return;
				 } catch (PackageManager.NameNotFoundException e) {
					 Log.d(TAG, "Package Not Found " + " --> " + clockApp);
					 e.printStackTrace();
				 }

			 }

			 Log.d(TAG, "Package NOT Found - Starting Settings -- WIDGET ID == " + appWidgetId);
			 ComponentName cnClock = new ComponentName("com.sd.sddigiclock", "com.sd.sddigiclock.DigiClockPrefs");
			 Intent alarmClockIntent = new Intent();
			 alarmClockIntent.setComponent(cnClock);
			 alarmClockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 alarmClockIntent.setData(Uri.parse(alarmClockIntent.toUri(Intent.URI_INTENT_SCHEME)));
			 Bundle extras = new Bundle();
			 extras.putString("Refresh", "No");
			 extras.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			 alarmClockIntent.putExtras(extras);
			 context.startActivity(alarmClockIntent);

		 }
		/*
		goAsync();


		 final String action = intent.getAction();
		 if(AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)){
			 Bundle extras = intent.getExtras();
			 final int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			 if(appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID){
				 this.onDeleted(context, new int[] {appWidgetId});
			 }
		 }else{
			 super.onReceive(context, intent);
		 }
			*/
	 }

	private void restartAll(Context context){
		Log.d(TAG, "restartAll method called");

		Intent refreshIntent = new Intent(context, DigiClockBroadcastReceiver.class);
		refreshIntent.setPackage(context.getPackageName());
		refreshIntent.setAction(DigiClockBroadcastReceiver.REFRESH_WIDGET);
		context.sendBroadcast(refreshIntent);


		/*
		try{

			Calendar calendar = Calendar.getInstance();
			long currentTimeMillis = calendar.getTimeInMillis();
			int sec = calendar.get(Calendar.SECOND);
			int millis = calendar.get(Calendar.MILLISECOND);
			int secsToNextMin = 59-sec;
			int millisToNextSec = 999-millis;
			int millisToNextMin = secsToNextMin*1000 + millisToNextSec;

			Intent refreshIntent = new Intent(context, DigiClockProvider.class);
			refreshIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, DigiClockProvider.class));
			if(ids != null && ids.length > 0) {
				refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
				//context.sendBroadcast(intent);
			}

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
			Log.d(TAG, "restartAll Update Exception: "+ exception);
		}

		*/

		/*
		OneTimeWorkRequest.Builder myWorkBuilder =
				new OneTimeWorkRequest.Builder(UpdateWidgetWorker.class);
		OneTimeWorkRequest myWork = myWorkBuilder
				.build();
		WorkManager.getInstance(context)
				.enqueueUniqueWork("UpdateWidgetWork", ExistingWorkPolicy.KEEP, myWork);
		 */


		/*
		SharedPreferences prefs = context.getSharedPreferences("prefs", 0);
		boolean batterySave = prefs.getBoolean("IgnoreBatterySave", false);

		//Intent serviceBG = new Intent(context.getApplicationContext(), WidgetBackgroundService.class);
		if(batterySave) {
			//serviceBG.setAction("android.settings.IGNORE_BATTERY_OPTIMIZATION_SETTINGS");
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			try {
				//ContextCompat.startForegroundService(context, serviceBG);
				OneTimeWorkRequest.Builder myWorkBuilder =
						new OneTimeWorkRequest.Builder(UpdateWidgetWorker.class);
				OneTimeWorkRequest myWork = myWorkBuilder
						.build();
				WorkManager.getInstance(context)
						.enqueueUniqueWork("UpdateWidgetWork", ExistingWorkPolicy.REPLACE, myWork);
			}catch(android.app.ForegroundServiceStartNotAllowedException e){
				Log.d(TAG, e.getMessage());
			}
			Log.i(TAG, "restartAll");
			//Log.d("DigiClockProvider", "Start service android 12");
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			// for Android 8 start the service in foreground
			//context.startForegroundService(serviceBG);
			//ContextCompat.startForegroundService(context, serviceBG);
			OneTimeWorkRequest.Builder myWorkBuilder =
					new OneTimeWorkRequest.Builder(UpdateWidgetWorker.class);
			OneTimeWorkRequest myWork = myWorkBuilder
					.build();
			WorkManager.getInstance(context)
					.enqueueUniqueWork("UpdateWidgetWork", ExistingWorkPolicy.REPLACE, myWork);
		} else {
			//context.startService(serviceBG);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			scheduleJob(context);
		} else {
			AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(context.getApplicationContext());
			appWidgetAlarm.startAlarm();
		}

		 */


	}



	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public static void scheduleJob(Context context) {

		/*
		Handler mHandler = new Handler();
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				ComponentName serviceComponent = new ComponentName(context.getPackageName(), RepeatingJob.class.getName());
				JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
				builder.setPersisted(true);
				builder.setPeriodic(60000);
				JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
				if(builder.build() != null) {
					int jobResult = jobScheduler.schedule(builder.build());
					if (jobResult == JobScheduler.RESULT_SUCCESS){
					}
				}
			}
		});
		*/


		/*
		new Thread(new Runnable() {
			public void run() {
				ComponentName serviceComponent = new ComponentName(context.getPackageName(), RepeatingJob.class.getName());
				JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
				builder.setPersisted(true);
				builder.setPeriodic(60000);
				JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
				if(builder.build() != null) {
					int jobResult = jobScheduler.schedule(builder.build());
					if (jobResult == JobScheduler.RESULT_SUCCESS){
					}
				}
			}
		}).start();

		*/
	}
	 
	 static void updateAppWidget(Context context, AppWidgetManager appwidgetmanager, int appWidgetId) {
		 SharedPreferences prefs = context.getSharedPreferences("prefs", 0);
		 boolean batterySave = prefs.getBoolean("IgnoreBatterySave", false);


		 UpdateWidgetView.updateView(context, appWidgetId);
		 /*
		 Intent intent = new Intent(context, UpdateWidgetService.class);
		 intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		 if(batterySave) {
			 intent.setAction("android.settings.IGNORE_BATTERY_OPTIMIZATION_SETTINGS");
		 }
		 intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
		 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			 try {
				 //ContextCompat.startForegroundService(context, intent);
				 OneTimeWorkRequest.Builder myWorkBuilder =
						 new OneTimeWorkRequest.Builder(UpdateWidgetWorker.class);
				 OneTimeWorkRequest myWork = myWorkBuilder
						 .build();
				 WorkManager.getInstance(context)
						 .enqueueUniqueWork("UpdateWidgetWork", ExistingWorkPolicy.REPLACE, myWork);
				 Log.i(TAG, "Starting Work");
			 } catch (android.app.ForegroundServiceStartNotAllowedException e) {
				 Log.d(TAG, e.getMessage());
			 }
		 } else {
			 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				 //ContextCompat.startForegroundService(context, intent);
				 OneTimeWorkRequest.Builder myWorkBuilder =
						 new OneTimeWorkRequest.Builder(UpdateWidgetWorker.class);
				 OneTimeWorkRequest myWork = myWorkBuilder
						 .build();
				 WorkManager.getInstance(context)
						 .enqueueUniqueWork("UpdateWidgetWork", ExistingWorkPolicy.REPLACE, myWork);
			 } else {
				 context.startService(intent);
			 }
			 //Toast.makeText(context, "Updated widget " + appWidgetId, Toast.LENGTH_SHORT).show();

		 }


		 OneTimeWorkRequest.Builder myWorkBuilder =
				 new OneTimeWorkRequest.Builder(UpdateWidgetWorker.class);
		 OneTimeWorkRequest myWork = myWorkBuilder
				 .build();
		 WorkManager.getInstance(context)
				 .enqueueUniqueWork("UpdateWidgetWork", ExistingWorkPolicy.REPLACE, myWork);
				  */
	 }


	public static PendingIntent getPendingSelfIntent(Context context, String action, int appWidgetID) {
		Intent intent = new Intent(context, DigiClockProvider.class);
		//Bundle bundle = new Bundle();
		//bundle.putInt("AppWidgetID", appWidgetID);
		//intent.putExtras(bundle);
		intent.setPackage(context.getPackageName());
		intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetID);
		intent.setAction(action);
		//Log.d(TAG, "GETPENDINGSELFINTENT -- Widget ID = " + appWidgetID);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			return PendingIntent.getBroadcast(context, appWidgetID, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
		}else{
			return PendingIntent.getBroadcast(context, appWidgetID, intent, PendingIntent.FLAG_IMMUTABLE);
		}
	}
}

