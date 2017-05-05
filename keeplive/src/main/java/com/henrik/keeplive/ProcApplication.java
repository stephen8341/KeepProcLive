package com.henrik.keeplive;

import android.app.Application;
import android.content.Context;

import com.marswin89.marsdaemon.DaemonClient;

public class ProcApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        DaemonClient.startDaemon(base);
    }
}
