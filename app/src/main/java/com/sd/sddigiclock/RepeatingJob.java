package com.sd.sddigiclock;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.work.Configuration;

/**
 * Created by Brian on 7/5/2019.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class RepeatingJob extends JobService {
    private final static String TAG = "RepeatingJob";


    @Override
    public boolean onStartJob(JobParameters params) {
        Configuration.Builder builder = new Configuration.Builder();
        builder.setJobSchedulerJobIdRange(100, 1000).build();

        Log.d(TAG, "onStartJob");
        Intent intent=new Intent(DigiClockProvider.JOB_TICK);
        sendBroadcast(intent);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}