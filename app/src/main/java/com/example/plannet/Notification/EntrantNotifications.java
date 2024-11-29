package com.example.plannet.Notification;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

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
     * @param title The notification title.
     * @param body The notification message.
     */
    public void queueNotification(String userID, String title, String body) {
        if (userID == null ||title== null || body== null) {
            Log.e("EntrantNotifications", "Invalid input.");
            return;
        }

        // Notif data
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", title);
        notification.put("body", body);


        // Add the notification to Firestore
        firebaseDB.collection("notifications")
                .document(userID)
                .set(notification, SetOptions.merge())
                .addOnSuccessListener(aVoid ->
                        Log.d("EntrantNotifications", "Notification queued: " + title))
                .addOnFailureListener(e ->
                        Log.e("EntrantNotifications", "Error queuing notification", e));
    }
}

