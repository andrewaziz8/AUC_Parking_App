package com.example.parkingapp;

import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationManager;

public class NotificationDismissReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the notification ID from the intent
        int notificationId = intent.getIntExtra("notificationId", -1); // Default is -1 if the ID is not passed

        // If notificationId is valid, cancel the notification
        if (notificationId != -1) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancel(notificationId); // Cancel the notification with this ID
            }
        }

    }
}
