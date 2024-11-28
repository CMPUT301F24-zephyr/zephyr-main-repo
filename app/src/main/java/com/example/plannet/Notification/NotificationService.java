package com.example.plannet.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

public class NotificationService extends Service {

    private FirebaseFirestore db;
    private static final String CHANNEL_ID = "NotificationServiceChannel";
    private String userID;

    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundService();
        userID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        db = FirebaseFirestore.getInstance();
        createNotificationChannel();
        startListeningForNotifications();
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

    private void startForegroundService() {
        // Build the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Notification Service Running")
                .setContentText("Listening for updates...")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        // Start the service in the foreground
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10+
            startForeground(1, notificationBuilder.build(), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(1, notificationBuilder.build());
        }
    }


    private void startListeningForNotifications() {
        String userID = "7c2b80ce1fae5be1";
        db.collection("notifications")
                .whereArrayContains("userIDs", userID)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        error.printStackTrace();
                        return;
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        for (DocumentChange change : snapshots.getDocumentChanges()) {
                            if (change.getType() == DocumentChange.Type.ADDED) {
                                String message = change.getDocument().getString("message");
                                String docId = change.getDocument().getId();
                                Log.d("NotificationService", "New notification detected: " + message);
                                showSystemNotification(message);

                                db.collection("notifications").document(docId)
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            // Notification deleted
                                        })
                                        .addOnFailureListener(Throwable::printStackTrace);
                            }
                        }
                    }
                });
    }

    private void showSystemNotification(String message) {
        Log.d("NotificationService", "Displaying notification with message: " + message);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager == null) {
            Log.e("NotificationService", "NotificationManager is null. Cannot display notification.");
            return;
        }

        // Build and display the notification
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("New Notification")
                .setContentText(message)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        try {
            manager.notify((int) System.currentTimeMillis(), notification.build());
            Log.d("NotificationService", "Notification displayed successfully.");
        } catch (Exception e) {
            Log.e("NotificationService", "Error displaying notification: ", e);
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
