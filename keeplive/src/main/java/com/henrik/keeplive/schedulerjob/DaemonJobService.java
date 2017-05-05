/**
 * create by henrikwu
 * 利用JobScheduler拉活进程，在5.0以上设备，这个方法很好用（除了小米）
 */
package com.henrik.keeplive.schedulerjob;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;


/**
 * android5.0以上用JobScheduler
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class DaemonJobService extends JobService {
    private static final String TAG = "DaemonJobService";;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service destroyed");
    }

    /**
     * When the app's MainActivity is created, it starts this service. This is so that the
     * activity and this service can communicate back and forth. See "setUiCalback()"
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, "on start job: " + params.getJobId());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "on stop job: " + params.getJobId());
        return true;
    }

    /**
     * Android5.0以上可以使用JobScheduler来拉活进程
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void startScheduleDaemonJob(Context context) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            return;
        }
        try {
            int jobId = 1;
            JobInfo.Builder builder = new JobInfo.Builder(jobId,new ComponentName(context, DaemonJobService.class));
            builder.setPeriodic(15*60*1000);
            builder.setPersisted(true);
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(builder.build());
        }catch (Exception e){
        }
    }
}
