package com.henrik.keeplive.onepx;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by henrikwu on 2017/5/4.
 */

public class KeepLiveActivity extends Activity {
    private BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                LogUtils.i(intent.getAction());
//                LogUtils.i(context.toString());
//                Intent intent1 = new Intent(Intent.ACTION_MAIN);
//                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent1.addCategory(Intent.CATEGORY_HOME);
//                startActivity(intent1);
                Log.d(getClass().getName(), "OnepxActivity finish   ================");
                finish();
            }
        };
        registerReceiver(br, new IntentFilter("finish activity"));
        checkScreenOn("onCreate");
        Log.d(getClass().getName(), "===onCreate===");
    }

    private void checkScreenOn(String methodName) {
        Log.d(getClass().getName(), "from call method: " + methodName);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        Log.d(getClass().getName(), "isScreenOn: " + isScreenOn);
        if (isScreenOn) {
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        Log.d(getClass().getName(), "===onDestroy===");
        try {
            unregisterReceiver(br);
        } catch (IllegalArgumentException e) {
            Log.e(getClass().getName(), "receiver is not resisted: " + e);
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkScreenOn("onResume");
    }

}
