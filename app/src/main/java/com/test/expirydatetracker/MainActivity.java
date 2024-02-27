package com.test.expirydatetracker;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {


    public static final String CHANNEL_ID ="MyChannel";
    private static final int NOTIFICATION_ID = 1;
    AlarmManager alarmManager;

    public static String ename;

    private DBhelper dBhelper;
    private SQLiteDatabase db;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        dBhelper = new DBhelper(this);
        db = dBhelper.getWritableDatabase();
        PeriodicWorkRequest queryWorkRequest =
                new PeriodicWorkRequest.Builder(QueryWorker.class, 1, TimeUnit.SECONDS)
                        .build();

        requestIgnoreBatteryOptimizations();
        // Enqueue the work request
        WorkManager.getInstance(this).enqueue(queryWorkRequest);


        Intent iHome = new Intent(MainActivity.this, Registration.class);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(iHome);
                startQueryService();
            }

        },2500);


    }
    private void requestIgnoreBatteryOptimizations() {
        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }


    private void startQueryService() {
        Intent queryServiceIntent = new Intent(this, QueryService.class);
        startService(queryServiceIntent);
    }

}