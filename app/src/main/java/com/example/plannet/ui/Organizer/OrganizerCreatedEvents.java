package com.example.plannet.ui.Organizer;

import java.util.ArrayList;
import java.util.List;

public class OrganizerCreatedEvents {
    private List<String> eventNames;

    // Constructor
    public OrganizerCreatedEvents() {
        this.eventNames = new ArrayList<>();
    }

    // Add/remove an event
    public void addEvent(String eventName) {
        eventNames.add(eventName);
    }
}
