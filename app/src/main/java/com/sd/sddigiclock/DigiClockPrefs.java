package com.sd.sddigiclock;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import static com.android.billingclient.api.BillingClient.SkuType.INAPP;
import com.android.billingclient.api.SkuDetailsResponseListener;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;

/*
 * Author Brian Kimmel
 * Copyright Silent Designs, all rights reserved
 */
public class DigiClockPrefs extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
	private final static String TAG = "DCP";


	private static final String DONATE_ONE_ID = "donate_one";
	private static final String DONATE_TWO_ID = "donate_two";
	private static final String DONATE_FIVE_ID = "donate_five";
	static String PRODUCT_ID = DONATE_ONE_ID;

	public static DigiClockPrefs DCP;

	private BottomNavigationView bottomNavigationView;

	private Button btccolor;
	private Button btclockclickapp;
	private Button btshowdate;
	private Button btsampm;
	private Button bts24;
	private SeekBar btctsize;

	private SeekBar btdtsize;

	private Button btstartbgservice;

	private Button btdcolor;
	private Button btdatematchcolor;
	private boolean dateMatchClockColor;
	private static PendingIntent service = null;
	private Button btchoosebg;
	private ImageButton btsave;
	private ImageButton btcancel;
	private LinearLayout saveLinearLayout;
	private LinearLayout cancelLinearLayout;

	private Button btdtformat;

	static int clocktextsize;
	static int datetextsize;

	static boolean dateshown;
	static boolean ampmshown;
	static boolean show24;

	static int cColor;
	static int dColor;
	static int dateFormatIndex = 0;

	private TabHost tabhost;

	private View dlgLayout;
	@SuppressWarnings("unused")
		private ScrollView bgcview;

	ImageView [] checkboxes;
	ImageView [] checkboxesfonts;

	private int Bg;
	int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	private TabHost tabs;
	private LinearLayout tab1;
	private LinearLayout tab2;
	private LinearLayout tab3;
	private LinearLayout tab4;
	//private TextView Font1;
	private TextView Font2;
	private int mFont;
	private String Fontfile;
	private int bgColor;
	private String clockapp;
	private LinearLayout bg0;
	private LinearLayout bg1;
	private LinearLayout bg2;
	private LinearLayout bg3;
	private LinearLayout bglayout0;
	private LinearLayout bglayout1;
	private LinearLayout bglayout2;
	private LinearLayout bglayout3;
    private PopupWindow mPopupWindow;
    private FrameLayout mDateFormatFrameLayout;
	private AlertDialog.Builder builder;
	private LayoutInflater inflater;
	private AlertDialog myDialog;
	private String day;
	private int year;
	private String month_name;
	private boolean classicMode = false;
	static  AlarmManager alarmManager;

	public static boolean active = false;
	public static boolean overSize = false;

	private Handler mHandler;

	Toolbar toolBar;
	private TabHost tabHost;
	private Button btusehomecolors;
	private boolean usehomecolors;
	private LinearLayout useHomeColorsLayout;
	private LinearLayout clockTextColorLayout;
	private LinearLayout clockShow24HourLayout;
	private LinearLayout clockShowAMPMLayout;
	private LinearLayout clockClickAppLayout;
	private LinearLayout dateMatchClockLayout;
	private LinearLayout dateTextColorLayout;
	private LinearLayout dateFormatLayout;
	private LinearLayout dateShowLayout;

	private AppBarConfiguration appBarConfiguration;
	private DrawerLayout mDrawer;
	private Toolbar toolbar;
	private NavigationView nvDrawer;
	private ActionBarDrawerToggle drawerToggle;
	private NavController navController;
	private Dialog donateDialog;
	private Dialog helpDialog;
	private Dialog aboutDialog;
	private DigiClockBroadcastReceiver digiClockBroadcastReceiver;
	private boolean batterySave;
	private AdView mAdView;
	private Intent resultValue;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();

		if (extras != null) {
			appWidgetId = extras.getInt(
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		//resultValue = new Intent();
		//resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		//setResult(RESULT_CANCELED, resultValue);

		// If they gave us an intent without the widget id, just bail.
		if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish();
		}

		if(!extras.containsKey("Refresh")){
			Log.d("DCP", "Refresh not found");
		}
		String refreshOnly = extras.getString("Refresh", "No");
		Log.d("DCP", "Check if Refresh Only: " + refreshOnly);
		if(refreshOnly.equals("Yes")){
			Log.d("DCP", "Refresh Only");
			saveAndExit();
		}
		//addPreferencesFromResource(R.xml.dc_prefs);

		setContentView(R.layout.digiclock_prefs_layout);

		MobileAds.initialize(this, new OnInitializationCompleteListener() {
			@Override
			public void onInitializationComplete(InitializationStatus initializationStatus) {
			}
		});

		mAdView = findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

		billingClient = BillingClient.newBuilder(this)
				.setListener(purchasesUpdatedListener)
				.enablePendingPurchases()
				.build();

		//getSupportActionBar().setTitle(R.string.p_Title);
		//getSupportActionBar().setHomeAsUpIndicator(R.drawable.logo);// set drawable icon
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		toolbar = findViewById(R.id.toolbar_main);
		setSupportActionBar(toolbar);
		//toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

		////DrawerLayout drawerLayout= findViewById(R.id.drawer_layout);
		////NavigationView navigationView= findViewById(R.id.nav_view);
		navController = Navigation.findNavController(this, R.id.nav_host_fragment);
		Bundle bundle = new Bundle();
		bundle.putInt("appWidgetID", appWidgetId);
		//navController.navigate(R.id.settingsHomeFragment, bundle);


		// Passing each menu ID as a set of Ids because each
		// menu should be considered as top level destinations.
		////appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
		//appBarConfiguration = new AppBarConfiguration.Builder(
		//		R.id.nav_home, R.id.nav_clock, R.id.nav_date, R.id.nav_background, R.id.nav_Font).build();
		//setupActionBarWithNavController(navController, appBarConfiguration);
		////NavigationUI.setupWithNavController(
		////		toolbar, navController, appBarConfiguration);

		// This will display an Up icon (<-), we will replace it with hamburger later
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// Find our drawer view
		mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerToggle = setupDrawerToggle();
		// Setup toggle to display hamburger icon with nice animation
		drawerToggle.setDrawerIndicatorEnabled(true);
		drawerToggle.syncState();
		drawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white, this.getTheme()));
		// Tie DrawerLayout events to the ActionBarToggle
		mDrawer.addDrawerListener(drawerToggle);

		nvDrawer = (NavigationView) findViewById(R.id.nav_view);
		// Setup drawer view
		setupDrawerContent(nvDrawer);



		bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

		bottomNavigationView.setOnItemSelectedListener(this);
		bottomNavigationView.setSelectedItemId(R.id.home);


		alarmManager = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);

		DCP = this;

		mHandler = new Handler();
		if(donateDialog !=null)
			donateDialog.dismiss();
		if(helpDialog !=null)
			helpDialog.dismiss();
		if(aboutDialog !=null)
			aboutDialog.dismiss();



		overSize = false;
		LoadPrefs();

		//setButtons();
		//IntentFilter intentFilter = new IntentFilter();
		//intentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
		//OrientationReceiver receiver = new OrientationReceiver();
		//getApplicationContext().registerReceiver(receiver, intentFilter);


    }


	//initiate purchase on button click
	public void purchase(View view) {
		//check if service is already connected
		if (billingClient.isReady()) {
			initiatePurchase();
		}
		//else reconnect service
		else{
			billingClient = BillingClient.newBuilder(DCP).enablePendingPurchases().setListener(DCP.purchasesUpdatedListener).build();
			billingClient.startConnection(new BillingClientStateListener() {
				@Override
				public void onBillingSetupFinished(BillingResult billingResult) {
					if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
						initiatePurchase();
					} else {
						Toast.makeText(getApplicationContext(),"Error "+billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
					}
				}
				@Override
				public void onBillingServiceDisconnected() {
				}
			});
		}
	}

	public void initiatePurchase(){
		List<String> skuList = new ArrayList<>();
		skuList.add(PRODUCT_ID);
		SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
		params.setSkusList(skuList).setType(INAPP);
		billingClient.querySkuDetailsAsync(params.build(),
				new SkuDetailsResponseListener() {
					@Override
					public void onSkuDetailsResponse(BillingResult billingResult,
													 List<SkuDetails> skuDetailsList) {
						if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
							if (skuDetailsList != null && skuDetailsList.size() > 0) {
								BillingFlowParams flowParams = BillingFlowParams.newBuilder()
										.setSkuDetails(skuDetailsList.get(0))
										.build();
								billingClient.launchBillingFlow(DigiClockPrefs.this, flowParams);
							}
							else{
								//try to add item/product id "purchase" inside managed product in google play console
								Toast.makeText(getApplicationContext(),"Purchase Item not Found",Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(getApplicationContext(),
									" Error "+billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	@Override
	public void onStart() {
		super.onStart();
		active = true;
		if(donateDialog !=null)
			donateDialog.dismiss();
		if(helpDialog !=null)
			helpDialog.dismiss();
		if(aboutDialog !=null)
			aboutDialog.dismiss();
	}

	@Override
	public void onStop() {
		super.onStop();
		active = false;
		if(donateDialog !=null)
			donateDialog.dismiss();
		if(helpDialog !=null)
			helpDialog.dismiss();
		if(aboutDialog !=null)
			aboutDialog.dismiss();
	}


	// `onPostCreate` called when activity start-up is complete after `onStart()`
	// NOTE 1: Make sure to override the method with only a single `Bundle` argument
	// Note 2: Make sure you implement the correct `onPostCreate(Bundle savedInstanceState)` method.
	// There are 2 signatures and only `onPostCreate(Bundle state)` shows the hamburger icon.
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		drawerToggle.onConfigurationChanged(newConfig);
	}

	private ActionBarDrawerToggle setupDrawerToggle() {
		// NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
		// and will not render the hamburger icon without it.
		return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionbar_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
			case android.R.id.home:
				mDrawer.openDrawer(GravityCompat.START);
				return true;
			case R.id.action_save:
				saveAndExit();
				break;
			case R.id.action_cancel:
				DCP.setResult(RESULT_CANCELED);
					finish();
				break;
			/*
			case R.id.action_mode:
				// custom dialog
				//final Dialog dialog = new Dialog(DCP);
				//dialog.setContentView(R.layout.mode_select);
				//dialog.setTitle(DCP.getResources().getString(R.string.p_Title));


				// set the custom dialog components - text, image and button
				final RadioGroup modegroup = (RadioGroup)dialog.findViewById(R.id.radioGroupMode);
				if(modegroup == null)
					Log.e("DCP", "RADIO GROUP IS NULL!!!!!!!!!!!!");
				if(classicMode) {
					modegroup.check(R.id.radioButtonClassicMode);
				}else{
					modegroup.check(R.id.radioButtonAdvancedMode);
				}
				modegroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch(checkedId){
							case R.id.radioButtonClassicMode:
								classicMode = true;
								//removed classicmode
								//setClassicMode( classicMode);
								break;
							case R.id.radioButtonAdvancedMode:
								classicMode = false;
								//removed classicmode
								//setClassicMode( classicMode);
								break;
						}
					}
				});


				Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
				// if button is clicked, close the custom dialog
				dialogButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

				dialog.show();
				break;

			case R.id.action_help:
				// custom dialog
				final Dialog helpdialog = new Dialog(DCP);
				helpdialog.setContentView(R.layout.help_dialog);
				//dialog.setTitle(DCP.getResources().getString(R.string.p_Title));

				// set the custom dialog components - text, image and button


				Button helpDialogButton = (Button) helpdialog.findViewById(R.id.helpDialogButtonOK);
				// if button is clicked, close the custom dialog
				helpDialogButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						helpdialog.dismiss();
					}
				});

				helpdialog.show();
				break;
			case R.id.action_about:
				new AlertDialog.Builder(DCP)
						.setTitle(getResources().getString(R.string.app_name))
						.setMessage(getResources().getString(R.string.p_about_info))

						// A null listener allows the button to dismiss the dialog and take no further action.
						.setNegativeButton(android.R.string.ok, null)
						.setIcon(getResources().getDrawable(R.drawable.logo, DCP.getTheme()))
						.show();
				break;

				*/
		default:
			break;
		}

		return true;
	}

	private void setupDrawerContent(NavigationView navigationView) {
		navigationView.setNavigationItemSelectedListener(
				new NavigationView.OnNavigationItemSelectedListener() {
					@Override
					public boolean onNavigationItemSelected(MenuItem menuItem) {
						selectDrawerItem(menuItem);
						return true;
					}
				});
	}

	public void selectDrawerItem(MenuItem menuItem) {
		// Create a new fragment and specify the fragment to show based on nav item clicked
		Fragment fragment = null;
		Class fragmentClass;
		String FragTag = "";
		switch(menuItem.getItemId()) {
			case R.id.nav_home:
				fragmentClass = SettingsHomeFragment.class;
				navController.navigate(R.id.settingsHomeFragment);
				bottomNavigationView.setSelectedItemId(R.id.home);
				FragTag = "Home";
				break;
			case R.id.nav_clock:
				fragmentClass = ClockSettingsFragment.class;
				navController.navigate(R.id.clockSettingsFragment);
				bottomNavigationView.setSelectedItemId(R.id.clock);
				FragTag = "Clock";
				break;
			case R.id.nav_date:
				fragmentClass = DateSettingsFragment.class;
				navController.navigate(R.id.dateSettingsFragment);
				bottomNavigationView.setSelectedItemId(R.id.date);
				FragTag = "Date";
				break;
			case R.id.nav_background:
				fragmentClass = BackgroundSettingsFragment.class;
				navController.navigate(R.id.backgroundSettingsFragment);
				bottomNavigationView.setSelectedItemId(R.id.background);
				FragTag = "Background";
				break;
			case R.id.nav_font:
				fragmentClass = FontSettingsFragment.class;
				navController.navigate(R.id.fontSettingsFragment);
				bottomNavigationView.setSelectedItemId(R.id.font);
				FragTag = "Font";
				break;
			case R.id.nav_help:
				//final Dialog helpDialog = new Dialog(DCP);
				helpDialog = new Dialog(DCP);
				helpDialog.setContentView(R.layout.help_dialog);
				//dialog.setTitle(DCP.getResources().getString(R.string.p_Title));

				// set the custom dialog components - text, image and button


				Button helpDialogButton = (Button) helpDialog.findViewById(R.id.helpDialogButtonOK);
				// if button is clicked, close the custom dialog
				helpDialogButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						helpDialog.dismiss();
					}
				});

				helpDialog.show();
				mDrawer.closeDrawers();
				return;
			case R.id.nav_about:

				//final Dialog aboutDialog = new Dialog(DCP);
				aboutDialog = new Dialog(DCP);
				aboutDialog.setContentView(R.layout.about_layout);
				Button aboutDialogButton = (Button) aboutDialog.findViewById(R.id.buttonAboutOK);
				// if button is clicked, close the custom dialog
				aboutDialogButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						aboutDialog.dismiss();
					}
				});

				aboutDialog.show();

				mDrawer.closeDrawers();
				return;
			case R.id.nav_donate:

				//final Dialog donateDialog = new Dialog(DCP);
				donateDialog = new Dialog(DCP);
				donateDialog.setContentView(R.layout.donate_layout);
				Button donatedialogButton = (Button) donateDialog.findViewById(R.id.donateDialogButtonOK);
				// if button is clicked, close the custom dialog
				donatedialogButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						donateDialog.dismiss();
					}
				});

				Button donateOneButton = (Button) donateDialog.findViewById(R.id.buttonDonateOne);
				// if button is clicked, close the custom dialog
				donateOneButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						donateDialog.dismiss();
						PRODUCT_ID = DONATE_ONE_ID;
						purchase(v);
					}
				});

				Button donateTwoButton = (Button) donateDialog.findViewById(R.id.buttonDonateTwo);
				// if button is clicked, close the custom dialog
				donateTwoButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						donateDialog.dismiss();
						PRODUCT_ID = DONATE_TWO_ID;
						purchase(v);
					}
				});

				Button donateFiveButton = (Button) donateDialog.findViewById(R.id.buttonDonateFive);
				// if button is clicked, close the custom dialog
				donateFiveButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						donateDialog.dismiss();
						PRODUCT_ID = DONATE_FIVE_ID;
						purchase(v);
					}
				});

				donateDialog.show();

				mDrawer.closeDrawers();
				return;
			default:
				fragmentClass = DateSettingsFragment.class;
				bottomNavigationView.setSelectedItemId(R.id.home);
				FragTag = "Home";
		}

		try {
			fragment = (Fragment) fragmentClass.newInstance();
			Bundle bundle = new Bundle();
			bundle.putInt("appWidgetID", appWidgetId);
			fragment.setArguments(bundle);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Insert the fragment by replacing any existing fragment
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, fragment, FragTag).commit();

		// Highlight the selected item has been done by NavigationView
		menuItem.setChecked(true);
		// Set action bar title
		setTitle(menuItem.getTitle());
		// Close the navigation drawer
		mDrawer.closeDrawers();


	}

	public void goHome(){
		Fragment fragment = null;
		Class fragmentClass;
		String FragTag = "";
		fragmentClass = SettingsHomeFragment.class;
		navController.navigate(R.id.settingsHomeFragment);
		bottomNavigationView.setSelectedItemId(R.id.home);
		FragTag = "Home";
		try {
			fragment = (Fragment) fragmentClass.newInstance();
			Bundle bundle = new Bundle();
			bundle.putInt("appWidgetID", appWidgetId);
			fragment.setArguments(bundle);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Insert the fragment by replacing any existing fragment
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, fragment, FragTag).commit();
		setTitle(FragTag);
	}


	private void LoadPrefs() {
		SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);

		dateshown = prefs.getBoolean("ShowDate"+appWidgetId, true);
		ampmshown = prefs.getBoolean("ShowAMPM"+appWidgetId, true);
		show24 = prefs.getBoolean("Show24"+appWidgetId, false);
		usehomecolors = prefs.getBoolean("UseHomeColors"+appWidgetId, false);

		clocktextsize = prefs.getInt("ClockTextSize"+appWidgetId, 15);
		datetextsize = prefs.getInt("DateTextSize"+appWidgetId, 12);
		dateFormatIndex = prefs.getInt("DateFormat" +appWidgetId, 2);

		cColor = prefs.getInt("cColor"+appWidgetId, -1);
		dColor = prefs.getInt("dColor"+appWidgetId, -1);
		dateMatchClockColor = prefs.getBoolean("DateMatchClockColor"+appWidgetId, true);
		bgColor = prefs.getInt("bgColor"+appWidgetId, Color.BLACK);

		Bg = prefs.getInt("Bg"+appWidgetId, 3);
		Fontfile = prefs.getString("Font"+appWidgetId, "Roboto-Regular.ttf");
		mFont = prefs.getInt("Fontnum"+appWidgetId, 0);
		clockapp = prefs.getString("ClockButtonApp"+appWidgetId, "NONE");
		//Log.d("SDDC", "clock app = "+ clockapp);
		classicMode = false;
		batterySave = prefs.getBoolean("IgnoreBatterySave", false);
	}

	/*
	private void setClassicMode(boolean isClassicMode){
		SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
		SharedPreferences.Editor edit = prefs.edit();

		classicMode = isClassicMode;
		edit.putBoolean("ClassicMode" + appWidgetId, classicMode);

		if(classicMode && Bg == 2){
			Bg = 3;
			edit.putInt("Bg"+appWidgetId, Bg);
			for(int i =0; i<checkboxes.length; i++){
				//Log.i("SDC", "i = " + Integer.toString(i) + ", Bg = " + Integer.toString(Bg));
				if (i == Bg){
					checkboxes[i].setImageResource(R.drawable.checkedbox);
				}
				else{
					checkboxes[i].setImageResource(R.drawable.checkbox);

				}
				//Log.i("SDC", "i = " + Integer.toString(i) + "Bg = " + Integer.toString(Bg));
			}
		}

		edit.commit();

		if(!classicMode){
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					tabHost.getTabWidget().getChildTabViewAt(3).setVisibility(View.VISIBLE);
					LinearLayout bgselect2 = (LinearLayout)DCP.findViewById(R.id.BGselect2);
					bgselect2.setVisibility(View.VISIBLE);
					//if(btusehomecolors !=null){
						//btusehomecolors.setVisibility(View.VISIBLE);
					//}
				}
			});
		}else{
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					tabHost.getTabWidget().getChildTabViewAt(3).setVisibility(View.GONE);
					LinearLayout bgselect2 = (LinearLayout)DCP.findViewById(R.id.BGselect2);
					bgselect2.setVisibility(View.GONE);
					//if(btusehomecolors !=null){
					//	btusehomecolors.setVisibility(View.GONE);
					//}
				}
			});
		}
	}
	*/

	private void setButtons() {
		//Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		//DCP.setSupportActionBar(toolbar);
		//toolBar = (Toolbar) findViewById(R.id.prefs_toolbar);
		//setSupportActionBar(toolBar);


		tabHost=(TabHost)findViewById(R.id.tabHost);
		tabHost.setup();

		TabSpec spec1=tabHost.newTabSpec("Tab 1");
		spec1.setContent(R.id.tab1);
		spec1.setIndicator(DCP.getResources().getString(R.string.clock));

		TabSpec spec2=tabHost.newTabSpec("Tab 2");
		spec2.setIndicator(DCP.getResources().getString(R.string.date));
		spec2.setContent(R.id.tab2);

		TabSpec spec3=tabHost.newTabSpec("Tab 3");
		spec3.setIndicator(DCP.getResources().getString(R.string.background));
		spec3.setContent(R.id.tab3);

		TabSpec spec4=tabHost.newTabSpec("Tab 4");
		spec4.setIndicator(DCP.getResources().getString(R.string.font));
		spec4.setContent(R.id.tab4);

		tabHost.addTab(spec1);
		tabHost.addTab(spec2);
		tabHost.addTab(spec3);
		tabHost.addTab(spec4);



		setTabColor(tabHost);
		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

			@Override
			public void onTabChanged(String arg0) {

				setTabColor(tabHost);
			}
		});

		//Backgrounds

		bglayout0 = (LinearLayout)DCP.findViewById(R.id.LinearLayout01);
		bglayout1 = (LinearLayout)DCP.findViewById(R.id.LinearLayout03);
		bglayout2 = (LinearLayout)DCP.findViewById(R.id.LinearLayout06);
		bglayout3 = (LinearLayout)DCP.findViewById(R.id.LinearLayout04);
		setBGs(bgColor);
		//tabs = (TabHost)DCP.findViewById(R.id.tabHost);

		//Clock

		btctsize = (SeekBar)DCP.findViewById(R.id.ClockSizeSB);

		useHomeColorsLayout = (LinearLayout) findViewById(R.id.LinearLayoutUseHomeColors);
		btusehomecolors = (Button)DCP.findViewById(R.id.UseHomeColors);
		btusehomecolors.setTransformationMethod(null);

		btccolor = (Button)DCP.findViewById(R.id.ClockTextColor);
		btccolor.setTransformationMethod(null);
		clockTextColorLayout = (LinearLayout) findViewById(R.id.LinearLayoutClockTextColor);

		bts24 = (Button)DCP.findViewById(R.id.TwentyFour);
		clockShow24HourLayout = (LinearLayout) findViewById(R.id.LinearLayoutShow24);

		btsampm = (Button)DCP.findViewById(R.id.ShowAMPM);
		btsampm.setTransformationMethod(null);
		clockShowAMPMLayout = (LinearLayout) findViewById(R.id.LinearLayoutShowAMPM);

		btclockclickapp = (Button)DCP.findViewById(R.id.ClockClickApp);
		btclockclickapp.setTransformationMethod(null);
		clockClickAppLayout = (LinearLayout) findViewById(R.id.LinearLayoutClockClipApp);

		//Date

		btdtsize = (SeekBar)DCP.findViewById(R.id.DateSizeSB);

		btdcolor = (Button)DCP.findViewById(R.id.DateTextColor);
		btdcolor.setTransformationMethod(null);
		dateTextColorLayout = (LinearLayout) findViewById(R.id.LinearLayoutDateTextColor);

		btdatematchcolor = (Button)DCP.findViewById(R.id.matchClockColor);
		btdatematchcolor.setTransformationMethod(null);
		dateMatchClockLayout = (LinearLayout) findViewById(R.id.LinearLayoutMatchClockColor);

		btshowdate = (Button)DCP.findViewById(R.id.ShowDate);
		btshowdate.setTransformationMethod(null);
		dateShowLayout = (LinearLayout) findViewById(R.id.LinearLayoutShowDate);

		btdtformat = (Button)DCP.findViewById(R.id.DateFormat);
		btdtformat.setTransformationMethod(null);
		dateFormatLayout = (LinearLayout)findViewById(R.id.LinearLayoutDateFormat);

		btstartbgservice = (Button)DCP.findViewById(R.id.buttonStartBGService);

		btsave = (ImageButton)DCP.findViewById(R.id.btSave);
		btcancel = (ImageButton)DCP.findViewById(R.id.btCancel);

		/*
		if(isMyServiceRunning(WidgetBackgroundService.class)){
			btstartbgservice.setText(R.string.p_bg_service_running_summary);
			btstartbgservice.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkedbox,0);
		}else{
			btstartbgservice.setText(R.string.p_bg_service_stopped_summary);
			btstartbgservice.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkbox,0);
		}

		 */
		saveLinearLayout = (LinearLayout)DCP.findViewById(R.id.saveLinearLayout);
		cancelLinearLayout = (LinearLayout)DCP.findViewById(R.id.cancelLinearLayout);
		//mDateFormatFrameLayout = (FrameLayout)DCP.findViewById(R.id.DateFormatFrameLayout);

		btctsize.setProgress(clocktextsize);
		btdtsize.setProgress(datetextsize);




		if(dateshown){
			btshowdate.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkedbox,0);
		}else{
			btshowdate.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkbox,0);
		}

		if(dateMatchClockColor){
			btdatematchcolor.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkedbox,0);
			btdcolor.setEnabled(false);
			btdcolor.setTextColor(getResources().getColor(R.color.disabled_text, DCP.getTheme()));
			TextView dcolorsum = (TextView) findViewById(R.id.textViewSummaryDateTextColor);
			dcolorsum.setTextColor(getResources().getColor(R.color.disabled_text, DCP.getTheme()));
		}else{
			btdatematchcolor.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkbox,0);
			btdcolor.setEnabled(true);
			btdcolor.setTextColor(getResources().getColor(R.color.white, DCP.getTheme()));
			TextView dcolorsum = (TextView) findViewById(R.id.textViewSummaryDateTextColor);
			dcolorsum.setTextColor(getResources().getColor(R.color.white, DCP.getTheme()));
		}

		if (Build.VERSION.SDK_INT >= 31) {
			if(usehomecolors){
				btusehomecolors.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkedbox,0);
				btdcolor.setEnabled(false);
				btdcolor.setTextColor(getResources().getColor(R.color.disabled_text, DCP.getTheme()));
				TextView dtcsum = (TextView)findViewById(R.id.textViewSummaryDateTextColor);
				dtcsum.setTextColor(getResources().getColor(R.color.disabled_text, DCP.getTheme()));

				btccolor.setEnabled(false);
				btccolor.setTextColor(getResources().getColor(R.color.disabled_text, DCP.getTheme()));
				TextView ctcsum = (TextView)findViewById(R.id.textViewSummaryClockTextColor);
				ctcsum.setTextColor(getResources().getColor(R.color.disabled_text, DCP.getTheme()));

				btdatematchcolor.setEnabled(false);
				btdatematchcolor.setTextColor(getResources().getColor(R.color.disabled_text, DCP.getTheme()));
				TextView dmcsum = (TextView)findViewById(R.id.textViewSummaryMatchClockColor);
				dmcsum.setTextColor(getResources().getColor(R.color.disabled_text, DCP.getTheme()));
			}else{
				btusehomecolors.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkbox,0);
				btdcolor.setEnabled(true);
				btdcolor.setTextColor(getResources().getColor(R.color.white, DCP.getTheme()));
				TextView dtcsum = (TextView)findViewById(R.id.textViewSummaryDateTextColor);
				dtcsum.setTextColor(getResources().getColor(R.color.white, DCP.getTheme()));

				btccolor.setEnabled(true);
				btccolor.setTextColor(getResources().getColor(R.color.white, DCP.getTheme()));
				TextView ctcsum = (TextView)findViewById(R.id.textViewSummaryClockTextColor);
				ctcsum.setTextColor(getResources().getColor(R.color.white, DCP.getTheme()));

				btdatematchcolor.setEnabled(true);
				btdatematchcolor.setTextColor(getResources().getColor(R.color.white, DCP.getTheme()));
				TextView dmcsum = (TextView)findViewById(R.id.textViewSummaryMatchClockColor);
				dmcsum.setTextColor(getResources().getColor(R.color.white, DCP.getTheme()));
			}
			setHomeColorsOnClickListener(btusehomecolors);
			setHomeColorsOnClickListener(useHomeColorsLayout);
		}else{
			useHomeColorsLayout.setVisibility(View.GONE);


			btdcolor.setEnabled(true);
			btdcolor.setTextColor(getResources().getColor(R.color.white, DCP.getTheme()));
			btccolor.setEnabled(true);
			btccolor.setTextColor(getResources().getColor(R.color.white, DCP.getTheme()));
			TextView ctcsum = (TextView)findViewById(R.id.textViewSummaryClockTextColor);
			ctcsum.setTextColor(getResources().getColor(R.color.white, DCP.getTheme()));
			btdatematchcolor.setEnabled(true);
			btdatematchcolor.setTextColor(getResources().getColor(R.color.white, DCP.getTheme()));
			TextView dtcsum = (TextView)findViewById(R.id.textViewSummaryDateTextColor);
			dtcsum.setTextColor(getResources().getColor(R.color.white, DCP.getTheme()));

			usehomecolors = false;
			SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
			SharedPreferences.Editor edit = prefs.edit();
			edit.putBoolean("UseHomeColors"+appWidgetId, usehomecolors);
			edit.commit();
		}

		if(ampmshown){
			btsampm.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkedbox,0);
		}else{
			btsampm.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkbox,0);
		}

		if(show24){
			bts24.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkedbox,0);
		}else{
			bts24.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkbox,0);
		}

		setShowAMPMListener(btsampm);
		setShowAMPMListener(clockShowAMPMLayout);

		setShow24Listener(bts24);
		setShow24Listener(clockShow24HourLayout);

		setClockTextColorListener(btccolor);
		setClockTextColorListener(clockTextColorLayout);

		setShowDateListener(btshowdate);
		setShowDateListener(dateShowLayout);

		setMatchClockColorListener(btdatematchcolor);
		setMatchClockColorListener(dateMatchClockLayout);

		setClockClickAppListener(btclockclickapp);
		setClockClickAppListener(clockClickAppLayout);

		/*
		btstartbgservice.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(isMyServiceRunning(WidgetBackgroundService.class)){
					//stop service
					btstartbgservice.setText(R.string.p_bg_service_stopped_summary);
					btstartbgservice.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkbox,0);
					DCP.stopService(new Intent(DCP,
							WidgetBackgroundService.class));
					Toast.makeText(DCP, R.string.p_toast_bg_service_stopped, Toast.LENGTH_LONG).show();
				}else {
					//run service
					btstartbgservice.setText(R.string.p_bg_service_running_summary);
					btstartbgservice.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkedbox,0);
					//Intent serviceBG = new Intent(DCP.getApplicationContext(), WidgetBackgroundService.class);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
						//DCP.startForegroundService(serviceBG);
						OneTimeWorkRequest.Builder myWorkBuilder =
								new OneTimeWorkRequest.Builder(UpdateWidgetWorker.class);
						OneTimeWorkRequest myWork = myWorkBuilder
								.build();
						WorkManager.getInstance(getApplicationContext())
								.enqueueUniqueWork("UpdateWidgetWork", ExistingWorkPolicy.REPLACE, myWork);
						Log.d("DigiClockProvider", "Start service android 12");
					} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
						// for Android 8 start the service in foreground
						//DCP.getApplicationContext().startForegroundService(serviceBG);
						OneTimeWorkRequest.Builder myWorkBuilder =
								new OneTimeWorkRequest.Builder(UpdateWidgetWorker.class);
						OneTimeWorkRequest myWork = myWorkBuilder
								.build();
						WorkManager.getInstance(getApplicationContext())
								.enqueueUniqueWork("UpdateWidgetWork", ExistingWorkPolicy.REPLACE, myWork);
					} else {
						//DCP.getApplicationContext().startService(serviceBG);
					}
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						//DigiClockProvider.scheduleJob(DCP.getApplicationContext());
					} else {
						AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(DCP.getApplicationContext());
						appWidgetAlarm.startAlarm();
					}
					Log.i("DigiClockPrefs", "Start BG Service");
				}
			}
		});

		 */

		setDateTextColorListener(btdcolor);
		setDateTextColorListener(dateTextColorLayout);



		builder = new AlertDialog.Builder(DCP);


		setDateFormatListener(btdtformat);
		setDateFormatListener(dateFormatLayout);


		btsave.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				saveAndExit();
			}
		});

		saveLinearLayout.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		saveAndExit();
	    	}
	    });

		btcancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();

			}
		});

		cancelLinearLayout.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
		        setResult(RESULT_CANCELED);
	            finish();

	    	}
	    });

		//bgcview = (ScrollView) dlgLayout.findViewById(R.id.BGSscrollview);

        ImageView cb0 = (ImageView)DCP.findViewById(R.id.ivCB0);
        ImageView cb1 = (ImageView)DCP.findViewById(R.id.ivCB1);
        ImageView cb2 = (ImageView)DCP.findViewById(R.id.ivCB2);
        ImageView cb3 = (ImageView)DCP.findViewById(R.id.ivCB3);

        checkboxes = new ImageView []{cb0, cb1, cb2, cb3};

        SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
        Bg = prefs.getInt("Bg"+appWidgetId, 3);

		/*
        //Log.i("SDC", "Bg = " + Integer.toString(Bg));
		if(classicMode && Bg == 2) {
			Bg = 3;
			SharedPreferences.Editor edit = prefs.edit();
			edit.putInt("Bg" + appWidgetId, Bg);
			edit.commit();
		}
		*/

        for(int i =0; i<checkboxes.length; i++){
        	//Log.i("SDC", "i = " + Integer.toString(i) + ", Bg = " + Integer.toString(Bg));
			if (i == Bg){
				checkboxes[i].setImageResource(R.drawable.checkedbox);
			}
			else{
				checkboxes[i].setImageResource(R.drawable.checkbox);

			}
			//Log.i("SDC", "i = " + Integer.toString(i) + "Bg = " + Integer.toString(Bg));
		}

        bg0 = (LinearLayout)DCP.findViewById(R.id.BGselect0);
        bg0.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Bg = 0;
	    		Log.d("DCP", "BG = " + Bg);
	    		for(int i =0; i<checkboxes.length; i++){
	    			if (i == Bg){
	    				checkboxes[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxes[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("Bg"+appWidgetId, Bg);
                edit.commit();
	    	}
        });

        bg1 = (LinearLayout)DCP.findViewById(R.id.BGselect1);
        bg1.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Bg = 1;
				Log.d("DCP", "BG = " + Bg);
	    		for(int i =0; i<checkboxes.length; i++){
	    			if (i == Bg){
	    				checkboxes[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxes[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("Bg"+appWidgetId, Bg);
                edit.commit();
	    	}
        });

        bg2 = (LinearLayout)DCP.findViewById(R.id.BGselect2);
        //bg2.setBackgroundColor(bgColor);
        bg2.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Bg = 2;
				Log.d("DCP", "BG = " + Bg);
	    		for(int i =0; i<checkboxes.length; i++){
	    			if (i == Bg){
	    				checkboxes[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxes[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}

	    		AmbilWarnaDialog dialog = new AmbilWarnaDialog(DCP, bgColor, new OnAmbilWarnaListener() {
	    	        @Override
	    	        public void onOk(AmbilWarnaDialog dialog, int color) {
	    	                bgColor = color;
	    	                SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
	    	                SharedPreferences.Editor edit = prefs.edit();
	    	                edit.putInt("bgColor"+appWidgetId, bgColor);
	    	                edit.commit();
	    	                setBGs(color);

	    	        }

	    	        @Override
	    	        public void onCancel(AmbilWarnaDialog dialog) {
	    	                // cancel was selected by the user
	    	        }
	    		});

	    		dialog.show();
	    		setBGs(bgColor);
	    		SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("bgColor"+appWidgetId, bgColor);
                edit.putInt("Bg"+appWidgetId, Bg);
                edit.commit();

	    	}

        });

        bg3 = (LinearLayout)DCP.findViewById(R.id.BGSelect3);
        //bg3.setBackgroundColor(bgColor);
        bg3.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
				Bg = 3;
				Log.d("DCP", "BG = " + Bg);

				for(int i =0; i<checkboxes.length; i++){
	    			if (i == Bg){
	    				checkboxes[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxes[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}

	    		AmbilWarnaDialog dialog = new AmbilWarnaDialog(DCP, bgColor, new OnAmbilWarnaListener() {
	    	        @Override
	    	        public void onOk(AmbilWarnaDialog dialog, int color) {
	    	                bgColor = color;
	    	                SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
	    	                SharedPreferences.Editor edit = prefs.edit();
	    	                edit.putInt("bgColor"+appWidgetId, bgColor);
	    	                edit.commit();
							setBGs(color);
	    	        }

	    	        @Override
	    	        public void onCancel(AmbilWarnaDialog dialog) {
	    	                // cancel was selected by the user
	    	        }
	    		});

	    		dialog.show();

	    		setBGs(bgColor);
	    		SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("bgColor"+appWidgetId, bgColor);
                edit.putInt("Bg"+appWidgetId, Bg);
                edit.commit();
	    	}

        });

        //removed classicmode
        //setClassicMode(classicMode);


        /*
        LinearLayout bg3 = (LinearLayout)DCP.findViewById(R.id.BGSelect3);
        bg3.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Bg = 3;
	    		for(int i =0; i<checkboxes.length; i++){
	    			if (i == Bg){
	    				checkboxes[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxes[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("Bg"+appWidgetId, Bg);
                edit.commit();
	    	}
        });
        */


        ImageView fcb1 = (ImageView)DCP.findViewById(R.id.ivFCB1);
        ImageView fcb2 = (ImageView)DCP.findViewById(R.id.ivFCB2);
        ImageView fcb3 = (ImageView)DCP.findViewById(R.id.ivFCB3);
        ImageView fcb4 = (ImageView)DCP.findViewById(R.id.ivFCB4);
        ImageView fcb5 = (ImageView)DCP.findViewById(R.id.ivFCB5);
        ImageView fcb6 = (ImageView)DCP.findViewById(R.id.ivFCB6);
        ImageView fcb7 = (ImageView)DCP.findViewById(R.id.ivFCB7);
        ImageView fcb8 = (ImageView)DCP.findViewById(R.id.ivFCB8);
        ImageView fcb9 = (ImageView)DCP.findViewById(R.id.ivFCB9);
        ImageView fcb10 = (ImageView)DCP.findViewById(R.id.ivFCB10);
        ImageView fcb11 = (ImageView)DCP.findViewById(R.id.ivFCB11);
        ImageView fcb12 = (ImageView)DCP.findViewById(R.id.ivFCB12);
        //ImageView fcb13 = (ImageView)DCP.findViewById(R.id.ivFCB13);
        //ImageView fcb14 = (ImageView)DCP.findViewById(R.id.ivFCB14);


        checkboxesfonts = new ImageView []{fcb1, fcb2, fcb3, fcb4, fcb5,
        		fcb6, fcb7, fcb8, fcb9, fcb10,
        		fcb11, fcb12};
        mFont = prefs.getInt("Fontnum"+appWidgetId, 0);
        for(int i =0; i<checkboxesfonts.length; i++){
        	//Log.i("SDC", "i = " + Integer.toString(i) + ", Bg = " + Integer.toString(Bg));
			if (i == mFont){
				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
			}
			else{
				checkboxesfonts[i].setImageResource(R.drawable.checkbox);

			}
			//Log.i("SDC", "i = " + Integer.toString(i) + "Bg = " + Integer.toString(Bg));
		}



        FrameLayout fontview1 = (FrameLayout)DCP.findViewById(R.id.FontSelect1);
        fontview1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 0;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		//Fontfile = "Roboto-Regular.ttf";
	    		SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}
		        });

        FrameLayout fontview2 = (FrameLayout)DCP.findViewById(R.id.FontSelect2);
        fontview2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 1;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
                Fontfile = "Chantelli_Antiqua.ttf";
                SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}
        });

        FrameLayout fontview3 = (FrameLayout)DCP.findViewById(R.id.FontSelect3);
        fontview3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 2;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		Fontfile = "Roboto-Regular.ttf";
	    		SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}
        });

        FrameLayout fontview4 = (FrameLayout)DCP.findViewById(R.id.FontSelect4);
        fontview4.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 3;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		Fontfile = "DroidSans.ttf";
	    		SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}
        });

        FrameLayout fontview5 = (FrameLayout)DCP.findViewById(R.id.FontSelect5);
        fontview5.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 4;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		Fontfile = "DroidSerif-Regular.ttf";
	    		SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}
        });

        FrameLayout fontview6 = (FrameLayout)DCP.findViewById(R.id.FontSelect6);
        fontview6.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 5;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		Fontfile = "256BYTES.TTF";
	    		SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}
        });

        FrameLayout fontview7 = (FrameLayout)DCP.findViewById(R.id.FontSelect7);
        fontview7.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 6;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		Fontfile = "weezerfont.ttf";
	    		SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}
        });

        FrameLayout fontview8 = (FrameLayout)DCP.findViewById(R.id.FontSelect8);
        fontview8.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 7;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		Fontfile = "CARBONBL.ttf";
	    		SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}
        });

        FrameLayout fontview9 = (FrameLayout)DCP.findViewById(R.id.FontSelect9);
        fontview9.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 8;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		Fontfile = "DistantGalaxy.ttf";
	    		SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}


        });


        FrameLayout fontview10 = (FrameLayout)DCP.findViewById(R.id.FontSelect10);
        fontview10.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 9;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		Fontfile = "GOODTIME.ttf";
	    		SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}
		        });

        FrameLayout fontview11 = (FrameLayout)DCP.findViewById(R.id.FontSelect11);
        fontview11.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 10;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		Fontfile = "Jester.ttf";
	    		SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}
        });

        FrameLayout fontview12 = (FrameLayout)DCP.findViewById(R.id.FontSelect12);
        fontview12.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 11;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		Fontfile = "DS-DIGIB.TTF";
	    		SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}
        });
        /*
        FrameLayout fontview13 = (FrameLayout)DCP.findViewById(R.id.FontSelect13);
        fontview13.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 12;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		Fontfile = "KOMIKAX.TTF";
	    		SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}

        });

        FrameLayout fontview14 = (FrameLayout)DCP.findViewById(R.id.FontSelect14);
        fontview14.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 13;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		Fontfile = "weezerfont.ttf"; //<--moved to 7
	    		SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}
		        });
		*/



        TextView txt = (TextView) findViewById(R.id.Font1);
        txt.setTypeface(Typeface.DEFAULT);

        txt = (TextView) findViewById(R.id.Font2);
        Typeface font = Typeface.createFromAsset(getAssets(), "Chantelli_Antiqua.ttf");
        txt.setTypeface(font);

        txt = (TextView) findViewById(R.id.Font3);
        font = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        txt.setTypeface(font);
        txt.setText("Roboto");

        txt = (TextView) findViewById(R.id.Font4);
        font = Typeface.createFromAsset(getAssets(), "DroidSans.ttf");
        txt.setTypeface(font);

        txt = (TextView) findViewById(R.id.Font5);
        font = Typeface.createFromAsset(getAssets(), "DroidSerif-Regular.ttf");
        txt.setTypeface(font);

        txt = (TextView) findViewById(R.id.Font6);
        font = Typeface.createFromAsset(getAssets(), "256BYTES.TTF");
        txt.setTypeface(font);
        txt = (TextView) findViewById(R.id.Font7);
        font = Typeface.createFromAsset(getAssets(), "weezerfont.ttf");
        txt.setTypeface(font);
        txt = (TextView) findViewById(R.id.Font8);
        font = Typeface.createFromAsset(getAssets(), "CARBONBL.ttf");
        txt.setTypeface(font);
        txt = (TextView) findViewById(R.id.Font9);
        font = Typeface.createFromAsset(getAssets(), "DistantGalaxy.ttf");
        txt.setTypeface(font);
        txt = (TextView) findViewById(R.id.Font10);
        font = Typeface.createFromAsset(getAssets(), "GOODTIME.ttf");
        txt.setTypeface(font);
        txt = (TextView) findViewById(R.id.Font11);
        font = Typeface.createFromAsset(getAssets(), "Jester.ttf");
        txt.setTypeface(font);

        txt = (TextView) findViewById(R.id.Font12);
        font = Typeface.createFromAsset(getAssets(), "DS-DIGIB.TTF");
        txt.setTypeface(font);
		//txt = (TextView) findViewById(R.id.Font13);
		//font = Typeface.createFromAsset(getAssets(), "KOMIKAX_.ttf");
		//txt.setTypeface(font);
        //txt = (TextView) findViewById(R.id.Font14);
        //font = Typeface.createFromAsset(getAssets(), "weezerfont.ttf");
        //txt.setTypeface(font);
	}

	private void saveAndExit() {
		SharedPreferences prefs = this.getSharedPreferences("prefs", 0);
		SharedPreferences.Editor edit = prefs.edit();

		//if(!UpdateWidgetService.isOversize) {
			//clocktextsize = btctsize.getProgress();
			//edit.putInt("ClockTextSize" + appWidgetId, clocktextsize);


			//datetextsize = btdtsize.getProgress();
			//edit.putInt("DateTextSize" + appWidgetId, datetextsize);
		//}

		//edit.putInt("DateFormat"+appWidgetId, dateFormatIndex);

		//edit.commit();


		/*
		final Intent intent = new Intent(getApplicationContext(), UpdateWidgetService.class);

		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

		service = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
		setResult(RESULT_OK, intent);
		getApplicationContext().startService(intent);
*/

		/*
		Intent serviceBG = new Intent(getApplicationContext(), WidgetBackgroundService.class);
		if(batterySave) {
			//serviceBG.setAction("android.settings.IGNORE_BATTERY_OPTIMIZATION_SETTINGS");
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			try {
				getApplicationContext().startForegroundService(serviceBG);
				Log.d("DigiClockProvider", "Start service android 12");

			}catch(android.app.ForegroundServiceStartNotAllowedException e){
				Log.d(TAG, e.getMessage());
			}
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			// for Android 8 start the service in foreground
			getApplicationContext().startForegroundService(serviceBG);

		} else {
			getApplicationContext().startService(serviceBG);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			DigiClockProvider.scheduleJob(getApplicationContext());
		} else {
			AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(getApplicationContext());
			appWidgetAlarm.startAlarm();
		}
		*/


		OneTimeWorkRequest.Builder myWorkBuilder =
				new OneTimeWorkRequest.Builder(UpdateWidgetWorker.class);
		OneTimeWorkRequest myWork = myWorkBuilder
				.build();
		//WorkManager.getInstance(getApplicationContext()).cancelAllWorkByTag("UpdateWidgetWork");
		WorkManager.getInstance(getApplicationContext())
				.enqueueUniqueWork("UpdateWidgetWork", ExistingWorkPolicy.REPLACE, myWork);
		Log.d(TAG, "Start OneTimeWorkRequest");



		Intent updateIntent = new Intent(getApplicationContext(), DigiClockProvider.class);
		updateIntent.setAction(DigiClockProvider.SETTINGS_CHANGED);
		updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		updateIntent.setPackage(getPackageName());
		setResult(RESULT_OK, updateIntent);


		//getApplicationContext().sendBroadcast(updateIntent);


		//UpdateWidgetView.updateView(getApplicationContext(), appWidgetId);

		//setResult(RESULT_OK, resultValue);
		//String key = String.format("appwidget%d_configured", appWidgetId);
		//prefs.edit().putBoolean(key, true).commit();

		finish();
	}

	private void setHomeColorsOnClickListener(View view){
		view.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(!usehomecolors){
					btusehomecolors.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkedbox,0);
					usehomecolors = true;

					btdcolor.setEnabled(false);
					btdcolor.setTextColor(getResources().getColor(R.color.disabled_text, DCP.getTheme()));
					//ColorDrawable customcolor = new ColorDrawable();
					//customcolor.setColor(dColor);
					//btdcolor.setCompoundDrawablesWithIntrinsicBounds(null, null, customcolor, null);

					btccolor.setEnabled(false);
					btccolor.setTextColor(getResources().getColor(R.color.disabled_text, DCP.getTheme()));
					//customcolor.setColor(cColor);
					//btccolor.setCompoundDrawablesWithIntrinsicBounds(null, null, customcolor, null);

					TextView ctcsum = (TextView)findViewById(R.id.textViewSummaryClockTextColor);
					ctcsum.setTextColor(getResources().getColor(R.color.disabled_text, DCP.getTheme()));

					TextView dtcsum = (TextView)findViewById(R.id.textViewSummaryDateTextColor);
					dtcsum.setTextColor(getResources().getColor(R.color.disabled_text, DCP.getTheme()));

					btdatematchcolor.setEnabled(false);
					btdatematchcolor.setTextColor(getResources().getColor(R.color.disabled_text, DCP.getTheme()));
					TextView dmcsum = (TextView)findViewById(R.id.textViewSummaryMatchClockColor);
					dmcsum.setTextColor(getResources().getColor(R.color.disabled_text, DCP.getTheme()));

					SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
					SharedPreferences.Editor edit = prefs.edit();
					edit.putBoolean("UseHomeColors"+appWidgetId, usehomecolors);
					edit.commit();
				}else{
					btusehomecolors.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkbox,0);
					usehomecolors = false;

					btdcolor.setEnabled(true);
					btdcolor.setTextColor(getResources().getColor(R.color.white, DCP.getTheme()));
					//ColorDrawable customcolor = new ColorDrawable();
					//switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
					//	case Configuration.UI_MODE_NIGHT_YES:
					//		customcolor.setColor(getColor(R.color.accent_1_100));
					//		break;
					//	case Configuration.UI_MODE_NIGHT_NO:
					//		customcolor.setColor(getColor(R.color.accent_1_600));
					//		break;
					//}
					//btdcolor.setCompoundDrawablesWithIntrinsicBounds(null, null, customcolor, null);

					btccolor.setEnabled(true);
					btccolor.setTextColor(getResources().getColor(R.color.white, DCP.getTheme()));
					//btccolor.setCompoundDrawablesWithIntrinsicBounds(null, null, customcolor, null);

					TextView ctcsum = (TextView)findViewById(R.id.textViewSummaryClockTextColor);
					ctcsum.setTextColor(getResources().getColor(R.color.white, DCP.getTheme()));
					TextView dtcsum = (TextView)findViewById(R.id.textViewSummaryDateTextColor);
					dtcsum.setTextColor(getResources().getColor(R.color.white, DCP.getTheme()));

					btdatematchcolor.setEnabled(true);
					btdatematchcolor.setTextColor(getResources().getColor(R.color.white, DCP.getTheme()));
					TextView dmcsum = (TextView)findViewById(R.id.textViewSummaryMatchClockColor);
					dmcsum.setTextColor(getResources().getColor(R.color.white, DCP.getTheme()));

					SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
					SharedPreferences.Editor edit = prefs.edit();
					edit.putBoolean("UseHomeColors"+appWidgetId, usehomecolors);
					edit.commit();
				}
			}
		});
	}

	private void setClockTextColorListener(View view){
		view.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AmbilWarnaDialog dialog = new AmbilWarnaDialog(DCP, cColor, new OnAmbilWarnaListener() {
					@Override
					public void onOk(AmbilWarnaDialog dialog, int color) {
						cColor = color;
						SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
						SharedPreferences.Editor edit = prefs.edit();
						edit.putInt("cColor"+appWidgetId, cColor);
						edit.commit();
					}

					@Override
					public void onCancel(AmbilWarnaDialog dialog) {
						// cancel was selected by the user
					}
				});

				if(!usehomecolors) {
					dialog.show();
				}
			}
		});
	}

	private void setShow24Listener(View view){
		view.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(!show24){
					bts24.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkedbox,0);
					show24 = true;
					SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
					SharedPreferences.Editor edit = prefs.edit();
					edit.putBoolean("Show24"+appWidgetId, show24);
					edit.commit();
				}else{
					bts24.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkbox,0);
					show24 = false;
					SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
					SharedPreferences.Editor edit = prefs.edit();
					edit.putBoolean("Show24"+appWidgetId, show24);
					edit.commit();
				}
			}
		});
	}

	private void setShowAMPMListener(View view) {
		view.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!ampmshown) {
					btsampm.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkedbox, 0);
					ampmshown = true;
					SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
					SharedPreferences.Editor edit = prefs.edit();
					edit.putBoolean("ShowAMPM" + appWidgetId, ampmshown);
					edit.commit();
				} else {
					btsampm.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkbox, 0);
					ampmshown = false;
					SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
					SharedPreferences.Editor edit = prefs.edit();
					edit.putBoolean("ShowAMPM" + appWidgetId, ampmshown);
					edit.commit();
				}
			}
		});
	}

	private void setClockClickAppListener(View view){
		view.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent appchooserintent=new Intent(DigiClockPrefs.this, AppSelector.class);
				Bundle bundle  = new Bundle();
				bundle.putInt("AppWidgetId", appWidgetId);
				appchooserintent.putExtras(bundle);
				appchooserintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(appchooserintent);

			}
		});
	}

	private void setDateTextColorListener(View view){
		view.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AmbilWarnaDialog dialog = new AmbilWarnaDialog(DCP, dColor, new OnAmbilWarnaListener() {
					@Override
					public void onOk(AmbilWarnaDialog dialog, int color) {
						dColor = color;
						SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
						SharedPreferences.Editor edit = prefs.edit();
						edit.putInt("dColor"+appWidgetId, dColor);
						edit.commit();
					}

					@Override
					public void onCancel(AmbilWarnaDialog dialog) {
						// cancel was selected by the user
					}
				});

				if(!usehomecolors && !dateMatchClockColor) {
					dialog.show();
				}
			}
		});
	}

	private void setShowDateListener(View view){
		view.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(!dateshown){
					btshowdate.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkedbox,0);
					dateshown = true;

					SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
					SharedPreferences.Editor edit = prefs.edit();
					edit.putBoolean("ShowDate"+appWidgetId, dateshown);
					edit.commit();

				}else{
					btshowdate.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkbox,0);
					dateshown = false;
					SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
					SharedPreferences.Editor edit = prefs.edit();
					edit.putBoolean("ShowDate"+appWidgetId, dateshown);
					edit.commit();
				}
			}
		});
	}

	private void setMatchClockColorListener(View view) {
		view.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(!usehomecolors) {
					if (!dateMatchClockColor) {
						btdcolor.setTextColor(getResources().getColor(R.color.disabled_text, DCP.getTheme()));
						TextView dcolorsum = (TextView) findViewById(R.id.textViewSummaryDateTextColor);
						dcolorsum.setTextColor(getResources().getColor(R.color.disabled_text, DCP.getTheme()));
						btdatematchcolor.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkedbox, 0);
						dateMatchClockColor = true;
						btdcolor.setEnabled(false);
						SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
						SharedPreferences.Editor edit = prefs.edit();
						edit.putBoolean("DateMatchClockColor" + appWidgetId, dateMatchClockColor);
						edit.commit();

					} else {
						btdcolor.setTextColor(Color.WHITE);
						TextView dcolorsum = (TextView) findViewById(R.id.textViewSummaryDateTextColor);
						dcolorsum.setTextColor(getResources().getColor(R.color.white, DCP.getTheme()));
						btdatematchcolor.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkbox, 0);
						dateMatchClockColor = false;
						btdcolor.setEnabled(true);
						SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
						SharedPreferences.Editor edit = prefs.edit();
						edit.putBoolean("DateMatchClockColor" + appWidgetId, dateMatchClockColor);
						edit.commit();
					}
				}
			}
		});
	}

	private void setDateFormatListener(View view){
		view.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder alt_bld = new AlertDialog.Builder(DCP);
				//alt_bld.setIcon(R.drawable.icon);
				final String[] formats = DCP.getResources().getStringArray(R.array.date_formats);
				final String[] localFormats = new String[formats.length];

				for(int i = 0; i < formats.length; i++){
					localFormats[i] = getFormattedDate(i);
				}

				//final CharSequence[] grpname= DCP.getResources().obtainTypedArray(formats);

				final int selected = dateFormatIndex;
				alt_bld.setTitle("Select a Date Format");
				alt_bld.setSingleChoiceItems(localFormats, selected, new DialogInterface
						.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {

						dateFormatIndex = item;
						SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
						SharedPreferences.Editor edit = prefs.edit();
						edit.putInt("DateFormat"+appWidgetId, dateFormatIndex);
						edit.commit();
						Toast.makeText(getApplicationContext(),
								"Date Format = "+localFormats[item], Toast.LENGTH_SHORT).show();
						dialog.dismiss();// dismiss the alertbox after chose option

					}
				});
				AlertDialog alert = alt_bld.create();
				alert.show();

			}
		});
	}

	@Override
	public void onPause(){
		super.onPause();
		active = false;
		if(donateDialog !=null)
			donateDialog.dismiss();
		if(helpDialog !=null)
			helpDialog.dismiss();
		if(aboutDialog !=null)
			aboutDialog.dismiss();
	}

	@Override
	public void onBackPressed() {

		SettingsHomeFragment homeFragment = (SettingsHomeFragment)getSupportFragmentManager().findFragmentByTag("Home");
		if (homeFragment != null &&homeFragment.isVisible()) {
			// add your code here
			//Log.d(TAG, "onBackPressed: THIS IS HOME SCREEN");

			super.onBackPressed();
			finish();

		}else{
			//Log.d(TAG, "onBackPressed: NOT HOME SCREEN");
			navController.navigate(R.id.settingsHomeFragment);
			bottomNavigationView.setSelectedItemId(R.id.home);
		}
		/*
		int count = getSupportFragmentManager().getBackStackEntryCount();

		if (count == 0) {

			if(navController.getCurrentDestination().getId() == navController.getGraph().getStartDestination()){
				super.onBackPressed();
				Log.d(TAG, "onBackPressed: THIS IS HOME SCREEN");
				navController.navigate(R.id.settingsHomeFragment);
				navController.navigate(R.id.settingsHomeFragment);
				bottomNavigationView.setSelectedItemId(R.id.home);
				//finish();
			}else{
				Log.d(TAG, "onBackPressed: NOT HOME SCREEN");
				navController.navigate(R.id.settingsHomeFragment);
				navController.navigate(R.id.settingsHomeFragment);
				bottomNavigationView.setSelectedItemId(R.id.home);
			}
		} else {
			getSupportFragmentManager().popBackStack();
			finish();
		}
		*/
	}

	/*
	private void sBGchoose() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        dlgLayout = inflater.inflate(R.layout.bgselect, null);
        bgcview = (ScrollView) dlgLayout.findViewById(R.id.BGSscrollview);
        
        ImageView cb0 = (ImageView)dlgLayout.findViewById(R.id.ivCB0);
        ImageView cb1 = (ImageView)dlgLayout.findViewById(R.id.ivCB1);
        ImageView cb2 = (ImageView)dlgLayout.findViewById(R.id.ivCB2);
        ImageView cb3 = (ImageView)dlgLayout.findViewById(R.id.ivCB3);
        
        checkboxes = new ImageView []{cb0, cb1, cb2, cb3};
        
        SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
        Bg = prefs.getInt("Bg"+appWidgetId, 3);
        
        //Log.i("SDC", "Bg = " + Integer.toString(Bg));
        
        
        for(int i =0; i<checkboxes.length; i++){
        	//Log.i("SDC", "i = " + Integer.toString(i) + ", Bg = " + Integer.toString(Bg));
			if (i == Bg){
				checkboxes[i].setImageResource(R.drawable.checkedbox);
			}
			else{
				checkboxes[i].setImageResource(R.drawable.checkbox);
				
			}
			//Log.i("SDC", "i = " + Integer.toString(i) + "Bg = " + Integer.toString(Bg));
		}
        
        LinearLayout bg0 = (LinearLayout)dlgLayout.findViewById(R.id.BGselect0);
        bg0.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Bg = 0;
	    		for(int i =0; i<checkboxes.length; i++){
	    			if (i == Bg){
	    				checkboxes[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxes[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("Bg"+appWidgetId, Bg);
                edit.commit();
	    	}
        });
        
        LinearLayout bg1 = (LinearLayout)dlgLayout.findViewById(R.id.BGselect1);
        bg1.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Bg = 1;
	    		for(int i =0; i<checkboxes.length; i++){
	    			if (i == Bg){
	    				checkboxes[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxes[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("Bg"+appWidgetId, Bg);
                edit.commit();
	    	}
        });
        
        LinearLayout bg2 = (LinearLayout)dlgLayout.findViewById(R.id.BGselect2);
        bg2.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Bg = 2;
	    		for(int i =0; i<checkboxes.length; i++){
	    			if (i == Bg){
	    				checkboxes[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxes[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("Bg"+appWidgetId, Bg);
                edit.commit();
	    	}
        });
        LinearLayout bg3 = (LinearLayout)dlgLayout.findViewById(R.id.BGSelect3);
        bg3.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Bg = 3;
	    		for(int i =0; i<checkboxes.length; i++){
	    			if (i == Bg){
	    				checkboxes[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxes[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("Bg"+appWidgetId, Bg);
                edit.commit();
	    	}
        });
        
        builder.setCancelable(false).setView(
                        dlgLayout).setPositiveButton(R.string.bgs_ok,
                        new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                        return;
                                }
                        }).setNegativeButton(null, null).setTitle(R.string.bgs_title).show();

    }
	*/


	public static void setTitle(String title){
		//setTitle(title);
	}

	public static void updateWidget(Context context, AppWidgetManager manager,
			int appWidgetId) {
		final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        final Calendar TIME = Calendar.getInstance();
        TIME.set(Calendar.MINUTE, 0);
        TIME.set(Calendar.SECOND, 0);
        TIME.set(Calendar.MILLISECOND, 0);

		/*
        final Intent intent = new Intent(context, UpdateWidgetService.class);

        //Bundle extras = intent.getExtras();
		//if (extras != null) {
		//    appWidgetId = extras.getInt(
		//            AppWidgetManager.EXTRA_APPWIDGET_ID, 
		//            AppWidgetManager.INVALID_APPWIDGET_ID);
		//}

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        if (service == null)
        {
            service = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        }

		 */

		//final PendingIntent pending = PendingIntent.getService(context, 0, intent, 0);
		//m.cancel(pending);

		//if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			//m.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, TIME.getTimeInMillis() + 60L * 1000L, service);
		//}
		//m.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, TIME.getTime().getTime(), 60*5*1000, service);
		//m.setRepeating(AlarmManager.RTC_WAKEUP, TIME.getTimeInMillis(),60L * 1000L, service);
		Log.i("DCPrefs", "DigiClockPrefs----------Setting Alarm for 5 minutes");


	}

	public static void setTabColor(TabHost tabhost) {

		for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
			tabhost.getTabWidget().getChildAt(i)
					.setBackgroundResource(R.drawable.grey); // unselected
			TextView tv = (TextView) tabhost.getTabWidget().getChildAt(i).findViewById(android.R.id.title); //Unselected Tabs
			tv.setTextColor(DCP.getResources().getColor(R.color.tab_unselected_text, DCP.getTheme()));
		}
		tabhost.getTabWidget().setCurrentTab(0);
		tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab())
				.setBackgroundResource(R.drawable.blank); // selected
		TextView tv = (TextView) tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).findViewById(android.R.id.title); //Unselected Tabs
		tv.setTextColor(DCP.getResources().getColor(R.color.white, DCP.getTheme()));
		// //have
		// to
		// change
	}

	public void setBGs(int color){
		Paint paint = new Paint();
	    paint = new Paint();



	    //paint.setColor(dColor);
	    //min. rect of text
	    int height = 75;
		int width = 400;
	    Shader shader = null;
	    int aw = Color.argb(200, 255, 255, 255);
	    int ab = Color.argb(200, 0, 0, 0);
	    Bitmap bm;
	    Canvas canvas;
	    BitmapDrawable d;
	    for(int i = 0; i < 4; i++){
	    	switch(i){
	    	case 0:
	    		shader = new LinearGradient(0, 0, 0, height,
			            new int[]{aw, color, color, ab},
			            new float[]{0,0.45f,.55f,1}, Shader.TileMode.REPEAT);
	    		paint.setShader(shader);
	    		bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    	    canvas = new Canvas(bm);
	    	    canvas.drawPaint(paint);
	    	    d = new BitmapDrawable(DCP.getResources(), bm);
	    	    bglayout0.setBackground(d);
	    	    break;
	    	case 1:
				shader = new LinearGradient(0, 0, 0, height,
			            new int[]{color, color, color, color},
			            new float[]{0.0f,0.45f,.55f,1.0f}, Shader.TileMode.REPEAT);
				paint.setShader(shader);
	    		bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    	    canvas = new Canvas(bm);
	    	    canvas.drawPaint(paint);
	    	    d = new BitmapDrawable(DCP.getResources(), bm);
	    	    bglayout1.setBackground(d);
	    	    break;
			case 2:
				shader = new LinearGradient(0, 0, 0, height,
			            new int[]{Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT},
			            new float[]{0.0f,0.45f,.55f,1.0f}, Shader.TileMode.REPEAT);
				paint.setShader(shader);
	    		bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    	    canvas = new Canvas(bm);
	    	    canvas.drawPaint(paint);
	    	    d = new BitmapDrawable(DCP.getResources(), bm);
	    	    bglayout2.setBackground(d);
	    	    //bglayout2.postInvalidate();
	    	    break;
			case 3:
				shader = new LinearGradient(0, 0, 0, height,
			            new int[]{aw, Color.TRANSPARENT, Color.TRANSPARENT, ab},
			            new float[]{0.0f,0.45f,.55f,1.0f}, Shader.TileMode.REPEAT);
				paint.setShader(shader);
	    		bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    	    canvas = new Canvas(bm);
	    	    canvas.drawPaint(paint);
	    	    d = new BitmapDrawable(DCP.getResources(), bm);
	    	    bglayout3.setBackground(d);
	    	    //bglayout3.postInvalidate();
	    	    break;

	    	}
		}
	}

	public static String getFormattedDate(int index){
		String sdate = "";
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
		String weekDay = dayFormat.format(cal.getTime());

		int day = cal.get(Calendar.DAY_OF_MONTH);
		int year = cal.get(Calendar.YEAR);


		//SimpleDateFormat month_date = new SimpleDateFormat("MMMMM");
		//month_name = month_date.format(cal.getTime());
		DateFormat dateformat = new DateFormat();
		String month_name = (String) DateFormat.format("M",  cal); // Jun
		//Log.d("SDDC", "CurrentMonth: "+ month_name);

		SimpleDateFormat yearFormat = new SimpleDateFormat("yy", Locale.US);
		String year_name = yearFormat.format(cal.getTime());

		switch(index) {
			case 0: //0 Tue January 23, 2018
				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("MMMM", cal);

				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + ", " + year_name);
				break;
			case 1:  //1        Tue Jan 23, 2018
				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("MMM", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + ", " + year_name);
				break;
			case 2:  //2       Tue 1-23-2018

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "-" + String.valueOf(day) + "-" + year_name);
				break;
			case 3:  //3       Tue 1/23/2018

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "/" + String.valueOf(day) + "/" + year_name);
				break;
			case 4:  //4       Tuesday January 23, 2018

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("MMMM", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + ", " + year_name);
				break;
			case 5:  //5		Tuesday Jan 23, 2018

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("MMM", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + ", " + year_name);
				break;
			case 6:  //6		Tuesday 1-23-2018

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "-" + String.valueOf(day) + "-" + year_name);
				break;
			case 7:  //7		Tuesday 1/23/2018

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "/" + String.valueOf(day) + "/" + year_name);
				break;
			case 8:  //8		January 23, 2018

				//dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("MMMM", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + ", " + year_name);
				break;
			case 9:  //9		Jan 23, 2018

				//dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("MMM", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + ", " + year_name);
				break;
			case 10:  //10		1-23-2018

				//dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "-" + String.valueOf(day) + "-" + year_name);
				break;
			case 11:  //11		1/23/2018

				//dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "/" + String.valueOf(day) + "/" + year_name);
				break;
			case 12:  //12		1-23-18

				//dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "-" + String.valueOf(day) + "-" + year_name);
				break;
			case 13:  //13		1/23/18
//dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "/" + String.valueOf(day) + "/" + year_name);
				break;
			case 14:  //14		January 23

				//dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("MMMM", cal);


				//yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + "" + year_name);
				break;
			case 15:  //15		1-23

				//dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				//yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "-" + String.valueOf(day) + "" + year_name);
				break;
			case 16:  //16		1/23

				//dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				//yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "/" + String.valueOf(day) + "" + year_name);
				break;
			case 17:  //17		Tue January 23

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("MMMM", cal);


				//yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + "" + year_name);
				break;
			case 18:  //18		Tue Jan 23

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("MMM", cal);


				//yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + "" + year_name);
				break;
			case 19:  //19		Tue 1-23

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				//yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "-" + String.valueOf(day) + "" + year_name);
				break;
			case 20:  //20		Tue 1/23

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				//yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "/" + String.valueOf(day) + "" + year_name);
				break;
			case 21:  //21		Tuesday Jan 23

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("MMM", cal);


				//yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + "" + year_name);
				break;
			case 22:  //22		Tuesday 1/23

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				//yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "/" + String.valueOf(day) + "" + year_name);
				break;
			case 23:  //23		Tue 23-1-2018

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "-" + month_name + "-" + year_name);
				break;
			case 24:  //24		Tue 23/1/2018

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "/" + month_name + "/" + year_name);
				break;
			case 25:  //25		Tuesday 23-1-2018

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "-" + month_name + "-" + year_name);
				break;
			case 26:  //26		Tuesday 23/1/2018

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "/" + month_name + "/" + year_name);
				break;
			case 27:  //27		23-1-2018

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "-" + month_name + "-" + year_name);
				break;
			case 28:  //28		23/1/2018

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "/" + month_name + "/" + year_name);
				break;
			case 29:  //29		23-1-18

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();

				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "-" + month_name + "-" + year_name);
				break;
			case 30:  //30		23/1/18

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();


				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "/" + month_name + "/" + year_name);
				break;
			case 31:  //31		23-1

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "-" + month_name + "" + year_name);
				break;
			case 32:  //32		23/1

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "/" + month_name + "" + year_name);
				break;
			case 33:  //33		Tue 23-1

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "-" + month_name + "" + year_name);
				break;
			case 34:  //34		Tue 23/1

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "/" + month_name + "" + year_name);
				break;
			case 35:  //35		Tuesday 23-1

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "-" + month_name + "" + year_name);
				break;
			case 36:  //36		Tuesday 23/1

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "/" + month_name + "" + year_name);
				break;
			case 37:  //37		Tue 1-23-18

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "-" + String.valueOf(day) + "-" + year_name);
				break;
			case 38:  //38		Tue 1/23/18

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "/" + String.valueOf(day) + "/" + year_name);
				break;
			case 39:  //39		Tuesday 1-23-18

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "-" + String.valueOf(day) + "-" + year_name);
				break;
			case 40:  //40		Tuesday 1/23/18

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "/" + String.valueOf(day) + "/" + year_name);
				break;
			case 41:  //41		Tue 23-1-18

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "-" + month_name + "-" + year_name);
				break;
			case 42:  //42		Tue 23/1/18

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "/" + month_name + "/" + year_name);
				break;
			case 43:  //43		Tuesday 23-1-18

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "-" + month_name + "-" + year_name);
				break;
			case 44:  //44		Tuesday 23/1/18

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "/" + month_name + "/" + year_name);
				break;
		}
		return sdate;
	}

	static void registerOneTimeAlarm(PendingIntent alarmIntent, long delayMillis, boolean triggerNow) {
		int SDK_INT = Build.VERSION.SDK_INT;
		long timeInMillis = (System.currentTimeMillis() + (triggerNow ? 0 : delayMillis));

		if (SDK_INT < Build.VERSION_CODES.KITKAT) {
			alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, alarmIntent);
		} else if (Build.VERSION_CODES.KITKAT <= SDK_INT && SDK_INT < Build.VERSION_CODES.M) {
			alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, alarmIntent);
		} else if (SDK_INT >= Build.VERSION_CODES.M) {
			alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, alarmIntent);
		}
	}

	private boolean isMyServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
		Fragment fragment = null;
		Class fragmentClass;
		String FragTag = "";
		switch(menuItem.getItemId()) {
			case R.id.home:
				fragmentClass = SettingsHomeFragment.class;
				nvDrawer.setCheckedItem(R.id.nav_home);
				FragTag = "Home";
				break;
			case R.id.clock:
				fragmentClass = ClockSettingsFragment.class;
				nvDrawer.setCheckedItem(R.id.nav_clock);
				FragTag = "Clock";
				break;
			case R.id.date:
				fragmentClass = DateSettingsFragment.class;
				nvDrawer.setCheckedItem(R.id.nav_date);
				FragTag = "Date";
				break;
			case R.id.background:
				fragmentClass = BackgroundSettingsFragment.class;
				nvDrawer.setCheckedItem(R.id.nav_background);
				FragTag = "Background";
				break;
			case R.id.font:
				fragmentClass = FontSettingsFragment.class;
				nvDrawer.setCheckedItem(R.id.nav_font);
				FragTag = "Font";
				break;
			default:
				fragmentClass = DateSettingsFragment.class;
				nvDrawer.setCheckedItem(R.id.nav_home);
		}

		try {
			fragment = (Fragment) fragmentClass.newInstance();
			Bundle bundle = new Bundle();
			bundle.putInt("appWidgetID", appWidgetId);
			fragment.setArguments(bundle);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Insert the fragment by replacing any existing fragment
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, fragment, FragTag).commit();

		// Highlight the selected item has been done by NavigationView
		menuItem.setChecked(true);
		// Set action bar title
		setTitle(menuItem.getTitle());

		return false;
	}

	private final PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
		@Override
		public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {

			//if item newly purchased
			if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
				handlePurchases(purchases);
			}
			//if item already purchased then check and reflect changes
			else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
				Log.d(TAG, "Item Already Owned -- ok to purchase again");
				//Purchase.PurchasesResult queryAlreadyPurchasesResult = billingClient.queryPurchases(INAPP);
				//List<Purchase> alreadyPurchases = queryAlreadyPurchasesResult.getPurchasesList();
				//if(alreadyPurchases!=null){
				//	handlePurchases(alreadyPurchases);
				//}

				/*
				ImmutableList<QueryProductDetailsParams.Product> productList = ImmutableList.of(
						//Product 1 = index is 0
						QueryProductDetailsParams.Product.newBuilder()
								.setProductId(DONATE_ONE_ID)
								.setProductType(BillingClient.ProductType.INAPP)
								.build(),
						//Product 2 = index is 1
						QueryProductDetailsParams.Product.newBuilder()
								.setProductId(DONATE_TWO_ID)
								.setProductType(BillingClient.ProductType.SUBS)
								.build(),
						//Product 3 = index is 2
						QueryProductDetailsParams.Product.newBuilder()
								.setProductId(DONATE_FIVE_ID)
								.setProductType(BillingClient.ProductType.SUBS)
								.build()
				);

				QueryProductDetailsParams queryProductDetailsParams =
						null;
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
					queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
							.setProductList(productList)
							.build();
				}
				assert queryProductDetailsParams != null;
				billingClient.queryProductDetailsAsync(
						queryProductDetailsParams,
						new ProductDetailsResponseListener() {
							public void onProductDetailsResponse(BillingResult billingResultOwned,
																 List<ProductDetails> productDetailsList) {
								// check billingResult
								if(billingResultOwned.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED){
									Log.d(TAG, "Item Already Owned -- ok to purchase again");
								}
								// process returned productDetailsList
							}
						}
				);
				*/
			}
			//if purchase cancelled
			else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
				Toast.makeText(getApplicationContext(),"Purchase Canceled",Toast.LENGTH_SHORT).show();
			}
			// Handle any other error msgs
			else {
				Toast.makeText(getApplicationContext(),"Error "+billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
			}
		}
	};



	private BillingClient billingClient;

	void handlePurchases(List<Purchase>  purchases) {


		for(Purchase purchase:purchases) {
			//if item is purchased
			if (purchase.getSkus().contains(PRODUCT_ID) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
			{
				if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
					// Invalid purchase
					// show error to user
					Toast.makeText(getApplicationContext(), "Error : Invalid Purchase", Toast.LENGTH_SHORT).show();
					return;
				}
				// else purchase is valid
				//if item is purchased and not acknowledged
				if (!purchase.isAcknowledged()) {
					AcknowledgePurchaseParams acknowledgePurchaseParams =
							AcknowledgePurchaseParams.newBuilder()
									.setPurchaseToken(purchase.getPurchaseToken())
									.build();
					billingClient.acknowledgePurchase(acknowledgePurchaseParams, ackPurchase);
				}
				//else item is purchased and also acknowledged
				else {
					// Grant entitlement to the user on item purchase
					// restart activity
					//if(!getPurchaseValueFromPref()){
					//savePurchaseValueToPref(true);
					Toast.makeText(getApplicationContext(), "Item Purchased", Toast.LENGTH_SHORT).show();
					//this.recreate();
					//}
				}
			}
			//if purchase is pending
			else if( purchase.getSkus().contains(PRODUCT_ID) && purchase.getPurchaseState() == Purchase.PurchaseState.PENDING)
			{
				Toast.makeText(getApplicationContext(),
						"Purchase is Pending. Please complete Transaction", Toast.LENGTH_SHORT).show();
			}
			//if purchase is unknown
			else if(purchase.getSkus().contains(PRODUCT_ID) && purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE)
			{
				//savePurchaseValueToPref(false);
				Toast.makeText(getApplicationContext(), "Purchase Status Unknown", Toast.LENGTH_SHORT).show();
			}
		}
	}

	AcknowledgePurchaseResponseListener ackPurchase = new AcknowledgePurchaseResponseListener() {
		@Override
		public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
			if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK){
				//if purchase is acknowledged
				// Grant entitlement to the user. and restart activity
				//savePurchaseValueToPref(true);  //<---Use if purchase item needs to be save to preferences
				Toast.makeText(getApplicationContext(), "Item Purchased", Toast.LENGTH_SHORT).show();
				//DigiClockPrefs.this.recreate();
			}
		}
	};

	/**
	 * Verifies that the purchase was signed correctly for this developer's public key.
	 * <p>Note: It's strongly recommended to perform such check on your backend since hackers can
	 * replace this method with "constant true" if they decompile/rebuild your app.
	 * </p>
	 */
	private boolean verifyValidSignature(String signedData, String signature) {
		try {
			// To get key go to Developer Console > Select your app > Development Tools > Services & APIs.
			String base64Key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApXFNjrW6Q1fU3QjLYtIycLU47PLARsgs2v042t38beMq/Vb5sM8g/0PNGM+zoy1Z1Il5x6kOlKHbvA2P00dejXv96DJuAkChNI++eBHS8i8xV6ApchmCZalKP5qOjdNNyDTkUxq2OfHrKnkzaXyLSPU+LT3r1Lvzukk82QD2y0OZGcBv8ZvbnuGxWjTjrV6nv+yJkIwM856QA+RfnrJUZARc8+UKhsttJS6SnVauXeHqGcMvCtq651g10lHLkKUdwHQtZKcorAeQ3lX9//5vhAnE+R0W4lJWH7qbk4AZaq0b47GRr/Ha9WIfvhkLTRGV4ecvIdQ6FOU/B5UADOLrbQIDAQAB";
			return Security.verifyPurchase(base64Key, signedData, signature);
		} catch (IOException e) {
			return false;
		}
	}
}