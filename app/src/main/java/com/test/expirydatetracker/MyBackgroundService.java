package com.test.expirydatetracker;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MyBackgroundService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // This method is called when the service is started.

        // Perform the datetime matching and notification sending logic here
        checkForDatetimeMatches();

        // You can also schedule this service to run periodically using AlarmManager,
        // WorkManager, or JobScheduler to continuously check for matches.

        // Return START_STICKY to ensure the service is restarted if it's killed by the system
        return START_STICKY;
    }

    // Check for datetime matches in your background service

    public void checkForDatetimeMatches() {
        // Get the current datetime
        Calendar currentDatetime = Calendar.getInstance();

        // Initialize your database helper and get a readable database
        DBhelper dbHelper = new DBhelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define the SQL query with the date comparison
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        String formattedCurrentDatetime = dateFormat.format(currentDatetime.getTime());
        String query = "SELECT * FROM product WHERE future <= '" + formattedCurrentDatetime + "'";

        // Execute the query
        Cursor cursor = db.rawQuery(query, null);

        // Check if there are any matches
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Retrieve event details
                @SuppressLint("Range") int eventId = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String eventName = cursor.getString(cursor.getColumnIndex("name"));

                // Create a notification for each match
                Notification.Builder builder = new Notification.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("Event Reminder")
                        .setContentText("Event '" + eventName + "' is happening now!")
                        .setPriority(Notification.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                notificationManager.notify(eventId, builder.build());
            } while (cursor.moveToNext());

            // Close the cursor
            cursor.close();
        }

       // startForeground(NOTIFICATION_ID, notification);
        // Close the database
        db.close();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

