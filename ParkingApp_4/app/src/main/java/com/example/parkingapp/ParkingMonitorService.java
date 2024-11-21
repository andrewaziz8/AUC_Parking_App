package com.example.parkingapp;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ParkingMonitorService extends Service {

    private FirebaseFirestore db;

    @Override
    public void onCreate() {
        super.onCreate();
        db = FirebaseFirestore.getInstance();

        // Start monitoring Firestore
        startMonitoringFirestore();
    }

    private void startMonitoringFirestore() {
        db.collection("spots")
                .whereEqualTo("isAvailable", true)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null && !value.isEmpty()) {
                        for (QueryDocumentSnapshot document : value) {
                            String spotId = document.getString("id"); // Get the spot ID
                            sendSpotAvailableNotification(spotId);
                        }
                    }
                });
    }

    private void sendSpotAvailableNotification(String spotId) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

//        // Create an intent to dismiss the notification (to make user be able to remove the notificaiton)
//        Intent dismissIntent = new Intent(this, NotificationDismissReceiver.class);
//        dismissIntent.putExtra("notificationId", 1); // You can use any unique notification ID here
//
//        // PendingIntent for dismiss action
//        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(this, 0, dismissIntent, PendingIntent.FLAG_IMMUTABLE); //FLAG_UPDATE_CURRENT

        // Ensure the notification channel exists
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "parking_notifications",
                    "Parking Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        Notification notification = new NotificationCompat.Builder(this, "parking_notifications")
                .setSmallIcon(R.drawable.img) // Use an appropriate drawable resource
                .setContentTitle("Parking Spot Available!")
                .setContentText("Spot " + spotId + " is now available. Check the app now!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
//                .addAction(R.drawable.img, "Dismiss", dismissPendingIntent) // Add the dismiss action, change
                .build();

        if (notificationManager != null) {
            notificationManager.notify(1, notification);
        }

//        // Delay stopping the service by 10 seconds
//        new android.os.Handler().postDelayed(() -> {
//            stopSelf();
//        }, 10000); // 10000 milliseconds = 10 seconds
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Display the persistent notification for the foreground service
        Notification notification = new NotificationCompat.Builder(this, "parking_notifications")
                .setSmallIcon(R.drawable.img) // Use an appropriate drawable resource
                .setContentTitle("Monitoring Parking Spots")
                .setContentText("We'll notify you when a spot becomes available.")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        startForeground(1, notification);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // We don't need to bind this service
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Clear all notifications when the service is destroyed
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // Called when the app is removed from the recent apps
        super.onTaskRemoved(rootIntent);

        // Stop the service and release resources
        stopSelf();

        // Clear all notifications
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }
}

