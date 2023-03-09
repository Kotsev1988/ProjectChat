package com.example.projectchat;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

public class JobExersize {
    public static void scheduleJob(Context context) {
        Log.d("jobExersize", "jobExersize");

        ComponentName componentName = new ComponentName(context, MyJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(10, componentName);
        //builder.setMinimumLatency(1*5000);
        //builder.setOverrideDeadline(2*4000);
        builder.setPeriodic(15 * 60 * 1000);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
            jobScheduler.schedule(builder.build());
        } else if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(builder.build());
        }
    }

    public static void cancellJob(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
            jobScheduler.cancel(10);
        } else if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(context.JOB_SCHEDULER_SERVICE);
            jobScheduler.cancel(10);
        }
    }
}
