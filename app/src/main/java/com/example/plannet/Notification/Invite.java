package com.example.plannet.Notification;


public class Invite {
    private String id; // Unique identifier for the invite
    private String eventTitle; // Title of the event
    private String eventLocation; // Location of the event
    private String status; // Status of the invite

    // Default constructor
    public Invite() {
    }

    //Constructor
    public Invite(String id, String eventTitle, String eventLocation, String status) {
        this.id = id;
        this.eventTitle = eventTitle;
        this.eventLocation = eventLocation;
        this.status = status;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Invite{" +
                "id='" + id + '\'' +
                ", eventTitle='" + eventTitle + '\'' +
                ", eventLocation='" + eventLocation + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
