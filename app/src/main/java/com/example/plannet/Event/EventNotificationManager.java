package com.example.plannet.Event;

import android.content.Context;
import android.widget.Toast;
import com.example.plannet.FirebaseConnector;
import com.example.plannet.Entrant.EntrantProfile;
import java.util.List;

public class EventNotificationManager {
    private FirebaseConnector firebaseConnector;

    public EventNotificationManager() {
        this.firebaseConnector = new FirebaseConnector();
    }

    // Method to send a notification to all entrants on the waitlist
    public void sendNotificationToWaitlist(List<EntrantProfile> waitlist, String message, Context context) {
        for (EntrantProfile entrant : waitlist) {
            if (entrant.isNotificationsEnabled()) {
                sendNotificationToFirebase(entrant, message, context);
            }
        }
    }

    // Method to send a notification to all accepted entrants
    public void sendNotificationToAccepted(List<EntrantProfile> acceptedList, String message, Context context) {
        for (EntrantProfile entrant : acceptedList) {
            if (entrant.isNotificationsEnabled()) {
                sendNotificationToFirebase(entrant, message, context);
            }
        }
    }

    // Helper method to send notifications to Firebase
    private void sendNotificationToFirebase(EntrantProfile entrant, String message, Context context) {
        firebaseConnector.addData(
                "notifications/" + entrant.getUserId(),
                "message",
                message,
                aVoid -> Toast.makeText(context, "Notification sent to " + entrant.getName(), Toast.LENGTH_SHORT).show(),
                e -> Toast.makeText(context, "Error sending notification: " + e.getMessage(), Toast.LENGTH_LONG).show()
        );
    }
}
