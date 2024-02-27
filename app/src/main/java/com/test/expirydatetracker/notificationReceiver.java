package com.test.expirydatetracker;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class notificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DBhelper dBhelper = new DBhelper(context);
        SQLiteDatabase db = dBhelper.getWritableDatabase();
        long currentTimeMillis = System.currentTimeMillis();
        String[] projection = {DBhelper.ID_COL,DBhelper.NAME_COL,DBhelper.FUTURE_COL};
        Cursor cursor = db.query(DBhelper.TABLE,projection,DBhelper.FUTURE_COL + "> ?",new String[]{String.valueOf(currentTimeMillis)},null,null,null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") long eventDateMillis = cursor.getLong(cursor.getColumnIndex(DBhelper.FUTURE_COL));
                @SuppressLint("Range") int eventId = cursor.getInt(cursor.getColumnIndex(DBhelper.ID_COL));

                if (eventDateMillis == currentTimeMillis) {
                    // Event has occurred, send a notification
                    sendNotification(context, eventId);
                }
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();

    }
    @SuppressLint("MissingPermission")
    private void sendNotification(Context context, int eventId) {
        // Customize your notification here
        String contentText = "Event has occurred!";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Event Notification")
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(eventId, builder.build());
    }
}
