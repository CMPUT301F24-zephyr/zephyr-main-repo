package com.example.plannet.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
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
    /**
     * notification service that starts the service in the foreground for notification
     * activity
     */

    private FirebaseFirestore db;
    private static final String CHANNEL_ID = "NotificationServiceChannel";
    private String userID;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("NotificationService", "Service started");
        userID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("NotificationService", "UserID: " + userID);
        db = FirebaseFirestore.getInstance();
        createNotificationChannel();
        startForegroundService();
        promptEnableNotifications();

        startListeningForNotifications();
    }

    /**
     * creates notification channel to service for future requests
     */
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

    /**
     * starts foreground services to listen for updates in the foreground
     */
    private void startForegroundService() {
        // Build the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Notification Service Running")
                .setContentText("Listening for updates...")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        // Start the service in the foreground
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notificationBuilder.build(), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(1, notificationBuilder.build());
        }
    }


    /**
     * notification listener to grab notifications from the database
     */
    private void startListeningForNotifications() {
        db.collection("notifications")
                .document(userID)//.whereArrayContains("userIDs", userID)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        error.printStackTrace();
                        return;
                    }

                    if (snapshots != null) {
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

    /**
     * show the system notification to user
     * @param title
     * @param body
     */
    private void showSystemNotification(String title, String body) {
        Log.d("NotificationService", "Displaying notification with message: " + title);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager == null) {
            Log.e("NotificationService", "NotificationManager is null. Cannot display notification.");
            return;
        }

        // Build and display the notification
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        try {
            manager.notify((int) System.currentTimeMillis(), notification.build());
            Log.d("NotificationService", "Notification displayed successfully.");
        } catch (Exception e) {
            Log.e("NotificationService", "Error displaying notification: ", e);
        }
    }

    /**
     * prompt for user to accept or deny notifications
     */
    private void promptEnableNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Notify user to enable notifications
                Intent intent = new Intent(this, NotificationPermissionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Necessary to launch an activity from a service
                startActivity(intent);
            }
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
