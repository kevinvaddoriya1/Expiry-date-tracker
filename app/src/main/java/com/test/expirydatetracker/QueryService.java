package com.test.expirydatetracker;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class QueryService extends Service {

    private Handler handler;
    private static final long QUERY_INTERVAL = 1000;
    private DBhelper dBhelper;
    private boolean isRunning = false;
    private final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        dBhelper = new DBhelper(this);
    }

    @Nullable
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRunning) {
            isRunning = true;
            startQuery();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startQuery() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkDatabaseForEvents();
                startQuery(); // Schedule the next check
            }
        }, QUERY_INTERVAL);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("your_channel_id", "Your Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void checkDatabaseForEvents() {
        createNotificationChannel();
        PeriodicWorkRequest queryWorkRequest =
                new PeriodicWorkRequest.Builder(QueryWorker.class, 1, TimeUnit.SECONDS)
                        .build();

        // Enqueue the work request
        WorkManager.getInstance(this).enqueue(queryWorkRequest);
        SQLiteDatabase db = dBhelper.getReadableDatabase();

        // Define the SQL query with the date comparison
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        String formattedCurrentDatetime = dateFormat.format(new Date());
        String query = String.format("SELECT * FROM product WHERE future = '%s'", formattedCurrentDatetime);

        // Execute the query
        Cursor cursor = db.rawQuery(query, null);
        Log.d("kevin", "checkDatabaseForEvents: ");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String ename = cursor.getString(1);
                Log.d("kevin", "checkDatabaseForEvents: " + ename);
                triggerNotification(ename);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
    }

    @SuppressLint("MissingPermission")
    private void triggerNotification(String ename) {
        // Create a notification here using your MyBroadcast class
        Intent appIntent = new Intent(this, Home.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "your_channel_id")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(ename)
                .setContentText(ename + " is Expire.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
