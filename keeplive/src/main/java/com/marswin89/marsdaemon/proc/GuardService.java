package com.marswin89.marsdaemon.proc;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.henrik.keeplive.accountsync.SyncService;
import com.henrik.keeplive.onepx.OnepxReceiver;
import com.henrik.keeplive.schedulerjob.DaemonJobService;
import com.marswin89.marsdaemon.DaemonConfigurations;


/**
 * GuardService运行在":service"进程中，被{@link com.marswin89.marsdaemon.IDaemonStrategy#onDaemonAssistantCreate(Context, DaemonConfigurations)}启动
 */
public class GuardService extends Service {

    private static String TAG = "GuardService";
    private static String KEY_INNER = "key_inner";

    static final int NOTIFICATION_ID = 99999;
    static Service sCore = null;

    public static void start(Context context) {
        Log.i(TAG, "start GuardService");
        try {
            Intent service = new Intent(context, GuardService.class);
            service.putExtra(KEY_INNER, true);
            context.startService(service);

        } catch (Throwable t) {
            Log.e(TAG, t.getMessage(), t);
        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        try {
//            boolean isStartFromDeamonProcess = true;
//            if (intent == null || intent.getBooleanExtra(KEY_INNER, false)) {
//                isStartFromDeamonProcess = false;
//            }
//            if (isStartFromDeamonProcess) {
//                Log.i(TAG, "start service from deamon process");
//
//            }
//        } catch (Exception e) {
//            Log.w(TAG, e);
//        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "service onCreate");
        sCore = this;
        if (Build.VERSION.SDK_INT < 18) { // 旧版本直接设一个空的通知就能变成前台服务
            startForeground(NOTIFICATION_ID, new Notification());
        } else { // API18以后会展示空白通知, 需要hack一下, 在另一个服务把这个通知移除
            stopSubService(); // 先结束一下, 保证后面service的onStartCommand执行到
            startSubService();
        }

        //开启一像素activity  拉活
        OnepxReceiver.register1pxReceiver(getApplicationContext());
        //利用帐号同步机制  拉活
        SyncService.startAccountSync(getApplicationContext());
        //利用JobScheduler机制  拉活
        DaemonJobService.startScheduleDaemonJob(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "service onDestroy");
        if (Build.VERSION.SDK_INT < 18) {
            stopForeground(true);
        } else {
            stopSubService();
        }
        sCore = null;

        OnepxReceiver.unregister1pxReceiver(this);
        super.onDestroy();
    }

    private void startSubService() {
        try {
            startService(new Intent(getApplicationContext(), subService.class));
        } catch (Throwable t) {
            Log.e(TAG, t.getMessage(), t);
        }
    }

    private void stopSubService() {
        try {
            stopService(new Intent(getApplicationContext(), subService.class));
        } catch (Throwable t) {
            Log.e(TAG, t.getMessage(), t);
        }
    }

    public static class subService extends Service {

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            Log.i(TAG, "subService onCreate");
        }

        @Override
        public void onDestroy() {
            Log.i(TAG, "subService onDestroy");
            try {
                stopForeground(true);
            } catch (Exception ignored) {
            }
            super.onDestroy();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Service core = sCore;
            if (null != intent && null != core) {
                Log.i(TAG, "onStartCommand");
                try {
                    core.startForeground(NOTIFICATION_ID, new Notification());
                    startForeground(NOTIFICATION_ID, new Notification());
                    core.stopForeground(true);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
            return Service.START_NOT_STICKY;
        }
    }
}
