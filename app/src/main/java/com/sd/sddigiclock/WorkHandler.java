package com.sd.sddigiclock;

import android.content.Context;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.lang.ref.Reference;
import java.util.concurrent.TimeUnit;

public class WorkHandler {
    public final static String TAG = "WorkHandler";
    public final static long updateMinutes = 1;
    private static WorkManager workManager;

    public static void onDoWork(Context context) {
        try{
            if(workManager == null){
                workManager = WorkManager.getInstance(context);
            }
            createWork(updateMinutes);
        }catch(Exception ex){
            Log.e(TAG, ex.toString());
        }

    }

    private static void createWork(long delay) {
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(false)
                .build();
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder
                (WidgetWorker.class)
                .setConstraints(constraints)
                .setInitialDelay(updateMinutes, TimeUnit.MINUTES)
                .build();

        workManager.enqueue(oneTimeWorkRequest);
    }
}
