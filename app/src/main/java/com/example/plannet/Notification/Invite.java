package com.example.plannet.Notification;


public class Invite {
    /**
     * this class is for sending invitations to user notifs
     */
    private String id;// Unique identifier for the invite
    private String eventID; // ID of the  event
    private String eventTitle; // Title of the event
    private String eventLocation; // Location of the event
    private String status; // Status of the invite

    // Default constructor
    public Invite() {
    }

    /**
     * constructor for Invite to get event info
     * @param id
     * @param eventID
     * @param eventTitle
     * @param eventLocation
     * @param status
     */
    public Invite(String id, String eventID, String eventTitle, String eventLocation, String status) {
        this.id = id;
        this.eventID = eventID;
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
    public String getEventID() {
        return eventID;
    }
    public void setEventID(String eventID) {
        this.eventID = eventID;
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
