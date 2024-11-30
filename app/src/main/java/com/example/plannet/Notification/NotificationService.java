package com.example.plannet.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class NotificationService extends Service {

    private FirebaseFirestore db;
    private static final String CHANNEL_ID = "NotificationServiceChannel";
    private String userID;

    @Override
    public void onCreate() {
        super.onCreate();
        //startForegroundService();
        Log.d("NotificationService", "Service started");
        userID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("NotificationService", "UserID: " + userID);
        db = FirebaseFirestore.getInstance();
        if (db != null) {
            Log.d("NotificationHandler", "Firestore initialized successfully.");}
        createNotificationChannel();
        testNotification();
        startListeningForNotifications();
    }

    public void testNotification() {
        showSystemNotification("Test Title", "This is a test notification");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Notification Service",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

//    private void startForegroundService() {
//        // Build the notification
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle("Notification Service Running")
//                .setContentText("Listening for updates...")
//                .setSmallIcon(android.R.drawable.ic_dialog_info)
//                .setPriority(NotificationCompat.PRIORITY_LOW);
//
//        // Start the service in the foreground
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10+
////            startForeground(1, notificationBuilder.build(), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
////        } else {
////            startForeground(1, notificationBuilder.build());
////        }
//    }


    public void startListeningForNotifications() {
        db.collection("notifications")
                .document(userID)//.whereArrayContains("userIDs", userID)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        error.printStackTrace();
                        return;
                    }

                    if (snapshots != null && snapshots.exists()) {
                        String title = snapshots.getString("title");
                        String body = snapshots.getString("body");
                        Log.d("NotificationService", "New notification detected: " + title);
                        showSystemNotification(title, body);

                        db.collection("notifications").document(userID)
                                .delete()
                                .addOnSuccessListener(aVoid -> Log.d("NotificationService", "Notification deleted for userID: " + userID))
                                .addOnFailureListener(e -> Log.e("NotificationService", "Error deleting notification", e));
                    }
                });
    }

    private void showSystemNotification(String title, String body) {
        Log.d("NotificationService", "Displaying notification with message: " + title);
//
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        if (manager == null) {
//            Log.e("NotificationService", "NotificationManager is null. Cannot display notification.");
//            return;
//        }

        // Build and display the notification
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (manager != null) {
            manager.notify((int) System.currentTimeMillis(), notification.build());
            Log.d("NotificationHandler", "Notification displayed successfully.");
        }
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
