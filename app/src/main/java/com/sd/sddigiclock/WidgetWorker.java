package com.sd.sddigiclock;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.TimeUnit;

public class WidgetWorker extends Worker {
    private static final String TAG = "WidgetWorker";
    private static final String WORK_RESULT = "work_result";
    private final int[] appWidgetIds;

    private Context mContext;

    public WidgetWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        ComponentName thisAppWidget = new ComponentName(mContext.getPackageName(), DigiClockProvider.class.getName());
        appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
    }

    @NonNull
    @Override
    public Result doWork() {
        try{

            OneTimeWorkRequest.Builder myWorkBuilder =
                    new OneTimeWorkRequest.Builder(UpdateWidgetWorker.class);

            OneTimeWorkRequest myWork = myWorkBuilder
                    .build();
            WorkManager.getInstance(mContext)
                    .enqueueUniqueWork("UpdateWidgetWork", ExistingWorkPolicy.REPLACE, myWork);
            Log.d(TAG, "Finished WidgetWorker doWork");

            return Result.success();
        } catch (Exception exception){
            Log.d(TAG, "doWork Exception: "+ exception);
            return Result.failure();
        }
    }

}
