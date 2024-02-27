package com.test.expirydatetracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class QueryWorker extends Worker {

    public QueryWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams
    ) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        checkDatabaseForEvents();
        Log.d("WorkerClass", "its work ");
        return Result.success();
    }

    private void checkDatabaseForEvents() {
        // Implement your database check logic here
        // This code is similar to the previous service
        SQLiteDatabase db = new DBhelper(getApplicationContext()).getReadableDatabase();

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
        // Create a notification here
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(ename)
                .setContentText(ename + " is Expire.")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(1, builder.build());
    }
}
