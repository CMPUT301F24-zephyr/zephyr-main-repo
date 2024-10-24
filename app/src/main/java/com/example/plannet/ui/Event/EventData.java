package com.example.plannet.ui.Event;

public class EventData {
    private String eventName;
    private String eventDescription;
    private String posterPath;

    public EventData(String eventName, String eventDescription, String posterPath) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.posterPath = posterPath;
    }

    public String getEventName() {
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
