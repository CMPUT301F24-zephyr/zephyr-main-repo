package com.example.plannet.Notification;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InAppNotificationRepository {
    private static InAppNotificationRepository instance;
    private Map<String, List<NotificationItem>> userNotifications; // Key: User ID, Value: List of Notifications

    private InAppNotificationRepository() {
        userNotifications = new HashMap<>();
    }

    public static synchronized InAppNotificationRepository getInstance(Context context) {
        if (instance == null) {
            instance = new InAppNotificationRepository();
        }
        return instance;
    }

    public void addNotification(String userId, String title, String message) {
        NotificationItem notification = new NotificationItem(title, message);
        if (!userNotifications.containsKey(userId)) {
            userNotifications.put(userId, new ArrayList<>());
        }
        userNotifications.get(userId).add(notification);
    }

    public List<NotificationItem> getNotifications(String userId) {
        return userNotifications.getOrDefault(userId, new ArrayList<>());
    }
}
