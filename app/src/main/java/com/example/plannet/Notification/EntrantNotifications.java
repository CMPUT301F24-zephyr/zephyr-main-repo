package com.example.plannet.Notification;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EntrantNotifications {

    private final FirebaseFirestore firebaseDB;

    public EntrantNotifications() {
        // Initialize Firestore
        this.firebaseDB = FirebaseFirestore.getInstance();
    }

    /**
     * Helper method to queue a notification for the user in Firebase.
     *
     * @param userID  The ID of the user to notify.
     * @param message The notification message.
     */
    public void queueNotification(String userID, String message) {
        if (userID == null || message == null || message.isEmpty()) {
            Log.e("EntrantNotifications", "Invalid input.");
            return;
        }

        // Notif data
        Map<String, Object> notification = new HashMap<>();
        notification.put("userIDs", userID);
        notification.put("message", message);

        // Add the notification to Firestore
        firebaseDB.collection("notifications")
                .add(notification)
                .addOnSuccessListener(documentReference ->
                        Log.d("EntrantNotifications", "Notification queued: " + message))
                .addOnFailureListener(e ->
                        Log.e("EntrantNotifications", "Error queuing notification", e));
    }
}

