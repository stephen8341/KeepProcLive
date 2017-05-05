package com.henrik.keeplive.onepx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Created by henrikwu on 2017/5/4.
 */

public class OnepxReceiver extends BroadcastReceiver {
    private static OnepxReceiver receiver;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Intent it = new Intent(context, KeepLiveActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(it);
            Log.d(getClass().getName(), "1px--screen off-");
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            context.sendBroadcast(new Intent("finish activity"));
            Log.d(getClass().getName(), "1px--screen on-");
        }
    }

    public static void register1pxReceiver(Context context) {
        if (receiver == null) {
            receiver = new OnepxReceiver();
        }
        context.registerReceiver(receiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        context.registerReceiver(receiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
    }

    public static void unregister1pxReceiver(Context context) {
        context.unregisterReceiver(receiver);
    }
}