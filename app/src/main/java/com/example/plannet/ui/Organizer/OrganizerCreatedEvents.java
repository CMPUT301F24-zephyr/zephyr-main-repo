package com.example.plannet.ui.Organizer;

import java.util.ArrayList;
import java.util.List;

public class OrganizerCreatedEvents {
    private List<String> eventNames;

    // Constructor
    public OrganizerCreatedEvents() {
        this.eventNames = new ArrayList<>();
    }

    // Methods to add/remove an event
    public void addEvent(String eventName) {
        eventNames.add(eventName);
    }
    public boolean removeEvent(String eventName) {
        return eventNames.remove(eventName);
    }

    // Getter and setter for the list of event names
    public List<String> getEventNames() {
        return new ArrayList<>(eventNames);
    }
    public void setEventNames(List<String> eventNames) {
        this.eventNames = new ArrayList<>(eventNames);
    }
}
