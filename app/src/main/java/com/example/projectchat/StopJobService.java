package com.example.projectchat;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

public class StopJobService extends BroadcastReceiver {
    @Override

    public void onReceive(Context context, Intent intent) {
        MyJobService.discon();
        JobExersize.cancellJob(context);
        JobScheduler jobScheduler = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        }

        List<JobInfo> pendingJobs = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            pendingJobs = jobScheduler.getAllPendingJobs();
        }
        if (pendingJobs.isEmpty()) {
            Log.d("Empty", "" + pendingJobs.size());
        }


        Intent start = new Intent(context, MainActivity.class);
        start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(start);

    }
}
