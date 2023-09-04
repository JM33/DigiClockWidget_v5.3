package com.sd.sddigiclock;

import android.app.Dialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.Objects;

public class AppSelector extends AppCompatActivity {
    private final static String TAG = "AppSelector";
    List<ApplicationInfo> packages;
    String[] allPackageNames;
    AppListAdapter app_list_adapter;

    private PackageManager pm;
    private int appWidgetId;
    private ProgressBar progBar;
    private LinearLayout appSelectLayout;
    private ListView mListView;

    private Context mContext;

    private Button packageHelpButton;
    private Button selectAppHelpButton;
    private Button cancelButton;
    private TextInputEditText packageEditText;
    private PackageManager mPackageManager;
    private Dialog packageHelpDialog;
    private Dialog selectAppHelpDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selector);

        if(this.getIntent().getExtras() != null){
            Bundle bundle = this.getIntent().getExtras();
            appWidgetId = bundle.getInt("AppWidgetId");
        }else{
            return;
        }
        mContext = this.getApplicationContext();
        mPackageManager = mContext.getPackageManager();
        progBar = (ProgressBar)findViewById(R.id.ProgressBar);
        progBar.getIndeterminateDrawable().setColorFilter(0xFF999999,
                android.graphics.PorterDuff.Mode.MULTIPLY);
        appSelectLayout = (LinearLayout)findViewById(R.id.AppSelectorLinearLayout);

        mListView = (ListView)findViewById(R.id.ListViewAppSelect);

        cancelButton = (Button)findViewById(R.id.buttonClockClickAppCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        selectAppHelpButton = (Button)findViewById(R.id.buttonSelectAppHelp);
        selectAppHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open help
                selectAppHelpDialog = new Dialog(DigiClockPrefs.DCP);
                selectAppHelpDialog.setContentView(R.layout.help_select_app_layout);
                Button aboutDialogButton = (Button) selectAppHelpDialog.findViewById(R.id.buttonSelectAppHelpOK);
                // if button is clicked, close the custom dialog
                aboutDialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        selectAppHelpDialog.dismiss();
                        Intent appchooserintent=new Intent(mContext, AppSelector.class);
                        Bundle bundle  = new Bundle();
                        bundle.putInt("AppWidgetId", appWidgetId);
                        appchooserintent.putExtras(bundle);
                        appchooserintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(appchooserintent);
                    }
                });

                selectAppHelpDialog.show();
                finish();
            }
        });

        packageHelpButton = (Button)findViewById(R.id.buttonClickAppCustomPackageHelp);
        packageHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open help
                packageHelpDialog = new Dialog(DigiClockPrefs.DCP);
                packageHelpDialog.setContentView(R.layout.help_package_layout);
                Button aboutDialogButton = (Button) packageHelpDialog.findViewById(R.id.buttonPackageHelpOK);
                // if button is clicked, close the custom dialog
                aboutDialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        packageHelpDialog.dismiss();
                        Intent appchooserintent=new Intent(mContext, AppSelector.class);
                        Bundle bundle  = new Bundle();
                        bundle.putInt("AppWidgetId", appWidgetId);
                        appchooserintent.putExtras(bundle);
                        appchooserintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(appchooserintent);
                    }
                });

                packageHelpDialog.show();
                finish();
            }
        });

        packageEditText = (TextInputEditText)findViewById(R.id.textInputEditTextCustomPackage);
        packageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()>0 && s.subSequence(s.length()-1, s.length()).toString().equalsIgnoreCase("\n")) {
                    String str = s.toString();
                    str = str.substring(0, str.length() - 1);
                    String packageName = str;
                    packageEditText.setText(packageName);
                    packageEditText.setSelection(Objects.requireNonNull(packageEditText.getText()).length());
                    //enter pressed
                    try{
                        PackageInfo pkgInfo = mPackageManager.getPackageInfo(packageName, 0);
                        String pkgName = pkgInfo.packageName;
                        UpdateWidgetView.setClockButtonApp(pkgName, appWidgetId, getApplicationContext());
                        Toast.makeText(mContext, "App added successfully: " + packageName, Toast.LENGTH_LONG).show();
                        ClockSettingsFragment.setCurrentClockAppText();
                        finish();
                        //allClockApps.add(clockImpls[i]);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, "Application does not exist: " + packageName, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "App does not exist: "+packageName);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        new Task().execute();



    }

    private List<ResolveInfo> installedApps() {
        final Intent main_intent = new Intent(Intent.ACTION_MAIN, null);
        main_intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return pm.queryIntentActivities(main_intent, 0);
    }

    private boolean isSystemPackage(ResolveInfo ri) {
        return (ri.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }


    class Task extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            progBar.setVisibility(View.VISIBLE);
            appSelectLayout.setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progBar.setVisibility(View.GONE);
            appSelectLayout.setVisibility(View.VISIBLE);
            //app_list_adapter.notifyDataSetChanged();
            super.onPostExecute(result);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            pm = getPackageManager();

            /*
            //get a list of installed apps.
            packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            allPackageNames = new String[packages.size()];
            final List<ResolveInfo> apps = installedApps();
            String[] allLabels = new String[apps.size()];
            final Drawable [] allIcons = new Drawable[apps.size()];

            for(ResolveInfo ri : apps) {
                if(!isSystemPackage(ri)){
                    // to get drawable icon -->  ri.loadIcon(package_manager)
                    allPackageNames[apps.indexOf(ri)] = ri.activityInfo.applicationInfo.packageName;
                    allLabels[apps.indexOf(ri)] = ri.loadLabel(pm).toString();
                    allIcons[apps.indexOf(ri)] = ri.loadIcon(pm);
                }

            }
            */

            /*
            for (ApplicationInfo packageInfo : packages) {
                //Log.d("UWS", "Installed package :" + packageInfo.packageName);
                //Log.d("UWS", "Source dir : " + packageInfo.sourceDir);
                //Log.d("UWS", "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));

                int index = packages.indexOf(packageInfo);
                allLabels[index] = pm.getApplicationLabel(packageInfo).toString();
                allPackageNames[index] = packageInfo.packageName;

                    allIcons[index] = ri(packageInfo);
                    Log.d("SDDC", "ICON FOUND = " + allIcons[index].toString());
            }


            //ArrayAdapter adapter = new ArrayAdapter<String>(this,
            //        R.layout.listview_text, allLabels);

            // Create an ArrayAdapter from List
            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (this, R.layout.listview_text, allLabels){
                @Override
                public View getView(int position, View convertView, ViewGroup parent){
                    // Get the Item from ListView
                    View view = super.getView(position, convertView, parent);
                    // Initialize a TextView for ListView each Item
                    TextView tv = (TextView) view.findViewById(R.id.label);
                    Drawable icon = allIcons[position];
                    tv.setCompoundDrawables(icon, null, null, null);
                    // Generate ListView Item using TextView
                    return view;
                }
            };
            */

            app_list_adapter = new AppListAdapter(mContext);

            mListView.setAdapter(app_list_adapter);



            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView tv = (TextView)view.findViewById(R.id.textView_package_name);
                    String pname = tv.getText().toString();
                    UpdateWidgetView.setClockButtonApp(pname, appWidgetId, getApplicationContext());
                    //Log.d("SDDC", "Selected: " + pname);
                    finish();
                    final Intent intent = new Intent(getApplicationContext(), UpdateWidgetService.class);

                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                    intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

                    setResult(RESULT_OK, intent);
                    getApplicationContext().startService(intent);
                }

            });

            return null;
        }
    }

}

