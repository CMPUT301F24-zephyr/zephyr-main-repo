package com.example.plannet.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

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
        if (db != null) {
            Log.d("NotificationHandler", "Firestore initialized successfully.");}
        createNotificationChannel();
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
     * notification listener to grab notifications from the database
     */
    private void startListeningForNotifications() {
        db.collection("notifications")
                .document(userID)
                .collection("Notifications")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e("NotificationService", "Error listening for notifications", error);
                        return;
                    }

                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            String title = doc.getString("title");
                            String body = doc.getString("body");
                            String eventID = doc.getString("eventID");
                            String docId = doc.getId();

                            Log.d("NotificationService", "New notification detected: " + title);
                            showSystemNotification(title, body);

                            if (eventID != null) {
                                // Add invite to invites subcollection
                                Map<String, Object> inviteData = new HashMap<>();
                                inviteData.put("eventName", title);
                                inviteData.put("body", body);
                                inviteData.put("status", "pending");
                                inviteData.put("eventID", eventID);

                                db.collection("notifications")
                                        .document(userID)
                                        .collection("invites")
                                        .add(inviteData)
                                        .addOnSuccessListener(aVoid -> Log.d("NotificationService", "Invite added to subcollection"))
                                        .addOnFailureListener(e -> Log.e("NotificationService", "Error adding invite to subcollection", e));
                            }

                            // Delete notification when unloaded
                            db.collection("notifications")
                                    .document(userID)
                                    .collection("Notifications")
                                    .document(docId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> Log.d("NotificationService", "Notification deleted for userID: " + userID))
                                    .addOnFailureListener(e -> Log.e("NotificationService", "Error deleting notification", e));
                        }
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
