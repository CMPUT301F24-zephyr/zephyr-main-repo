package com.example.plannet.Entrant;

import android.content.Context;
import android.widget.Toast;
import com.example.plannet.Event.EventWaitlistPending;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class EntrantManager {
    private EntrantDBConnector DBConnector;
    private EntrantWaitlistPending EntrantPending;
    private EntrantWaitlistAccepted EntrantAccepted;
    private EntrantWaitlistRejected EntrantRejected;
    private NotificationManager notificationManager;  // Notification manager for sending notifications

    public EntrantManager(EntrantDBConnector dbConnector) {
        this.DBConnector = dbConnector;
        this.EntrantPending = new EntrantWaitlistPending();
        this.EntrantAccepted = new EntrantWaitlistAccepted();
        this.EntrantRejected = new EntrantWaitlistRejected();
        this.notificationManager = new NotificationManager();  // Initialize NotificationManager
    }

    // Method for an entrant to join a waitlist
    public void joinWaitlist(Context context, EntrantProfile entrant, EventWaitlistPending waitlist) {
        if (waitlist.addEntrant(entrant)) {
            String collectionPath = "events/" + waitlist.getEventID() + "/waitlist/" + waitlist.getType();

            // Add entrant to Firebase waitlist
            DBConnector.addEntrantToWaitlist(
                    collectionPath,
                    entrant.getDeviceID(),
                    entrant,
                    // Success listener with Toast and notification
                    aVoid -> {
                        Toast.makeText(context, "Entrant added to Firebase waitlist successfully", Toast.LENGTH_SHORT).show();
                        notificationManager.sendSelectionNotification(entrant, context);
                    },
                    // Failure listener with Toast
                    e -> Toast.makeText(context, "Error adding entrant to Firebase: " + e.getMessage(), Toast.LENGTH_LONG).show()
            );

            this.EntrantPending.addWaitlist(waitlist);
        } else {
            Toast.makeText(context, "Entrant is already in the waitlist.", Toast.LENGTH_SHORT).show();
        }
    }

    // Method for an entrant to leave a waitlist
    public void leaveWaitlist(EntrantProfile entrant, EventWaitlistPending waitlist) {
        if (waitlist.removeEntrant(entrant)) {
            // Remove from Firebase and notify
            this.EntrantPending.removeWaitlist(waitlist);
            // Notify entrant of removal from the waitlist if notifications are enabled
            if (entrant.isNotificationsEnabled()) {
                notificationManager.sendRejectionNotification(entrant, null);
            }
        } else {
            Toast.makeText(null, "Entrant is not in the waitlist.", Toast.LENGTH_SHORT).show();
        }
    }

    // Additional method to notify entrant of rejection in the lottery draw
    public void notifyEntrantRejection(EntrantProfile entrant, Context context) {
        notificationManager.sendRejectionNotification(entrant, context);
    }
}
