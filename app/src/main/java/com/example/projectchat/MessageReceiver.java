package com.example.projectchat;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class MessageReceiver extends BroadcastReceiver {
    WorkManager workManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("OnbroadcastRes", "broadcast");
        context.startService(new Intent(context.getApplicationContext(), MyServicePush.class));
      /*  ComponentName serviceComponent = new ComponentName(context, MyJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(30 * 1000); // Wait at least 30s
        builder.setOverrideDeadline(60 * 1000); // Maximum delay 60s

        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());*/


        /* PeriodicWorkRequest periodicWorkRequest=new PeriodicWorkRequest.Builder(MyMainWorker.class, 1, TimeUnit.MINUTES)
             .addTag("checkmess")
           .setInitialDelay(5, TimeUnit.SECONDS)
         .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
        .build();
        workManager = WorkManager.getInstance(context.getApplicationContext());
        workManager.cancelAllWork();
        workManager.pruneWork();
        workManager.cancelAllWorkByTag("chk");
        OneTimeWorkRequest oneTimeWorkRequest=new OneTimeWorkRequest.Builder(MyMainWorker.class).build();

         workManager.enqueueUniquePeriodicWork("checkmess", ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);

         JobExersize.scheduleJob(context);
        ActivityManager manager=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningAppProcessInfo service:manager.getRunningAppProcesses())
        {
            if (service.processName.equals("com.example.projectchat:externalProcess")){
                Log.d("runningServices", ""+service.processName);
            }
            else{
                JobExersize.scheduleJob(context);
            }
        }*/
    }
}


