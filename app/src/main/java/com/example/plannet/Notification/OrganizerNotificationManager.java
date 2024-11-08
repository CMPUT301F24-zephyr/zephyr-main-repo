package com.example.plannet.Notification;

import android.content.Context;
import com.example.plannet.Entrant.EntrantProfile;
import com.example.plannet.Event.Event;
import java.util.List;

public class OrganizerNotificationManager {
    private Context context;

    public OrganizerNotificationManager(Context context) {
        this.context = context;
    }

    // Add notifications to the in-app notification storage for entrants
    public void sendCustomNotificationToEntrants(Event event, List<EntrantProfile> entrants, String message) {
        InAppNotificationRepository notificationRepository = InAppNotificationRepository.getInstance(context);
        for (EntrantProfile entrant : entrants) {
            notificationRepository.addNotification(entrant.getUserId(), "Notification for " + event.getEventName(), message);
        }
    }
}
