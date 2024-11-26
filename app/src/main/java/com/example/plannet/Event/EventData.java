package com.example.plannet.Event;

public class EventData {
    private String eventName;
    private String eventDescription;
    private String posterPath;
    private String status;
    private String location;
    private String eventId;

    public String getEventId() {
        return eventId;
    }

    @Override
    public String toString() {
        return "EventData{" +
                "eventId='" + eventId + '\'' +
                ", eventName='" + eventName + '\'' +
                ", eventDescription='" + eventDescription + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", status='" + status + '\'' +
                ", location='" + location + '\'' +
                '}';
    }


    // Full constructor
    public EventData(String eventName, String eventDescription, String posterPath, String status, String location) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.posterPath = posterPath;
        this.status = status;
        this.location = location;
    }

    public EventData(String eventId, String eventName, String eventDescription, String posterPath, String status, String location) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.posterPath = posterPath;
        this.status = status;
        this.location = location;
    }

    // Getters and setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
}
