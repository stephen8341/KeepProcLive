package com.henrik.keeplive;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.marswin89.marsdaemon.proc.GuardService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GuardService.start(getApplicationContext());
    }
}
