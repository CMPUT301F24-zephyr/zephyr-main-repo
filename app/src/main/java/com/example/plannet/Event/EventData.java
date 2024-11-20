package com.example.plannet.Event;

public class EventData {
    private String eventName;
    private String eventDescription;
    private String posterPath;
    private String status;
    private String location;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public EventData(String eventName, String eventDescription, String posterPath) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.posterPath = posterPath;
        this.status = status;
        this.location = location;
    }

    public String getName() {
        return eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
}
