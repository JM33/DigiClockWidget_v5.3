package com.sd.sddigiclock;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OutOfQuotaPolicy;
import androidx.work.StopReason;
import androidx.work.WorkInfo;
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
                    .enqueueUniqueWork("UpdateWidgetWork", ExistingWorkPolicy.KEEP, myWork);
            Log.d(TAG, "Finished WidgetWorker doWork");
            return Result.success();
        } catch (Exception exception){
            Log.d(TAG, "doWork Exception: "+ exception);
            return Result.failure();
        }
    }

    @Override
    public void onStopped() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //Toast.makeText(mContext, "STOPPED WORK: " + StopReason(this.getStopReason()), Toast.LENGTH_LONG);
            Log.d(TAG, "Stop Reason: "+ StopReason(this.getStopReason()));
        }

        doWork();
    }

    public static String StopReason(int stopReason) {
        String reason = "STOP_REASON_UNKNOWN";
        switch (stopReason) {
            case WorkInfo.STOP_REASON_NOT_STOPPED :
                reason ="STOP_REASON_NOT_STOPPED";
            case WorkInfo.STOP_REASON_CANCELLED_BY_APP:
                reason ="STOP_REASON_CANCELLED_BY_APP";
            case WorkInfo.STOP_REASON_PREEMPT :
                reason ="STOP_REASON_PREEMPT";
            case WorkInfo.STOP_REASON_TIMEOUT :
                reason ="STOP_REASON_TIMEOUT";
            case WorkInfo.STOP_REASON_DEVICE_STATE:
                reason ="STOP_REASON_DEVICE_STATE";
            case WorkInfo.STOP_REASON_CONSTRAINT_BATTERY_NOT_LOW :
                reason = "STOP_REASON_CONSTRAINT_BATTERY_NOT_LOW";
            case WorkInfo.STOP_REASON_CONSTRAINT_CHARGING:
                reason ="STOP_REASON_CONSTRAINT_CHARGING";
            case WorkInfo.STOP_REASON_CONSTRAINT_CONNECTIVITY:
                reason ="STOP_REASON_CONSTRAINT_CONNECTIVITY";
            case WorkInfo.STOP_REASON_CONSTRAINT_DEVICE_IDLE:
                reason ="STOP_REASON_CONSTRAINT_DEVICE_IDLE";
            case WorkInfo.STOP_REASON_CONSTRAINT_STORAGE_NOT_LOW:
                reason ="STOP_REASON_CONSTRAINT_STORAGE_NOT_LOW";
            case WorkInfo.STOP_REASON_QUOTA :
                reason ="STOP_REASON_QUOTA";
            case WorkInfo.STOP_REASON_BACKGROUND_RESTRICTION :
                reason ="STOP_REASON_BACKGROUND_RESTRICTION";
            case WorkInfo.STOP_REASON_APP_STANDBY :
                reason ="STOP_REASON_APP_STANDBY";
            case WorkInfo.STOP_REASON_USER :
                reason ="STOP_REASON_USER";
            case WorkInfo.STOP_REASON_SYSTEM_PROCESSING :
                reason ="STOP_REASON_SYSTEM_PROCESSING";
            case WorkInfo.STOP_REASON_ESTIMATED_APP_LAUNCH_TIME_CHANGED :
                reason ="STOP_REASON_ESTIMATED_APP_LAUNCH_TIME_CHANGED";
        }
        return reason;
    }

}
