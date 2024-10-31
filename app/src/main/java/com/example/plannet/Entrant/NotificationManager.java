package com.example.plannet.Entrant;

import android.content.Context;
import android.widget.Toast;
import com.example.plannet.FirebaseConnector;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class NotificationManager {
    private FirebaseConnector firebaseConnector;

    // Constructor
    public NotificationManager() {
        this.firebaseConnector = new FirebaseConnector();
    }

    // Method to send notification to the entrant upon being selected
    public void sendSelectionNotification(EntrantProfile entrant, Context context) {
        if (entrant.isNotificationsEnabled()) {  // Check if notifications are enabled
            String message = "Congratulations! You've been selected for the event.";
            sendNotificationToFirebase(entrant, message, context);
            Toast.makeText(context, "Selection notification sent.", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to send notification to the entrant if they weren't selected
    public void sendRejectionNotification(EntrantProfile entrant, Context context) {
        if (entrant.isNotificationsEnabled()) {  // Check if notifications are enabled
            String message = "Unfortunately, you weren't selected this time.";
            sendNotificationToFirebase(entrant, message, context);
            Toast.makeText(context, "Rejection notification sent.", Toast.LENGTH_SHORT).show();
        }
    }

    // Private helper method to send notifications to Firebase
    private void sendNotificationToFirebase(EntrantProfile entrant, String message, Context context) {
        firebaseConnector.addData(
                "notifications/" + entrant.getUserId(),  // Collection path based on entrant ID
                "message",
                message,
                aVoid -> Toast.makeText(context, "Notification stored in Firebase", Toast.LENGTH_SHORT).show(),
                e -> Toast.makeText(context, "Error storing notification: " + e.getMessage(), Toast.LENGTH_LONG).show()
        );
    }
}
