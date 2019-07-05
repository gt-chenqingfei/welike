package com.redefine.welike.keepalive;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobAwakeService extends JobService {

    private int kJobId = 0;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("lizard", "JobAwakeService-->onStartCommand");
        scheduleJob(getJobInfo());
        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        boolean isLocalAlive = isServiceWork(this, LocalService.class.getName());
        boolean isRemoteAlive = isServiceWork(this, RemoteService.class.getName());

        if (!isLocalAlive || !isRemoteAlive) {
            this.startService(new Intent(this, LocalService.class));
            this.startService(new Intent(this, RemoteService.class));
        }

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        scheduleJob(getJobInfo());

        return true;
    }

    public void scheduleJob(JobInfo t) {
        JobScheduler tm =
                (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (null != tm) {
            try {
                int jobcount= tm.getAllPendingJobs().size();
                if(jobcount>50){
                    tm.cancelAll();
                }
                tm.schedule(t);
            } catch (Throwable e) {
                Log.e("lizard", "JobScheduler" + e);
            }
        }
    }

    public JobInfo getJobInfo() {
       // Log.d("lizard", "getJobInfo");
        JobInfo.Builder builder = new JobInfo.Builder(kJobId++, new ComponentName(this, JobAwakeService.class));
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setPersisted(true);
        builder.setRequiresCharging(false);
        builder.setRequiresDeviceIdle(false);
        //间隔100毫秒
        builder.setPeriodic(3000);
        return builder.build();
    }

    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (myAM == null) {
            return false;
        }
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
        if (null==myList || myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }
}

