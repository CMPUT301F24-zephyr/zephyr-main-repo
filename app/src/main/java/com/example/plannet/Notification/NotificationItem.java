package com.example.plannet.Notification;

public class NotificationItem {
    private String title;
    private String message;

    public NotificationItem(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
