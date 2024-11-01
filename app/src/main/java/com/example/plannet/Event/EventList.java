package com.example.plannet.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventList {
    // Mapping each facilityID to its list of events
    private HashMap<String, List<Event>> organizerEvents;

    // Constructor
    public EventList() {
        organizerEvents = new HashMap<>();
    }

    // Add an event
    public void addEvent(Event event) {
        String facility = event.getFacility(); // Get facility name from event
        organizerEvents.putIfAbsent(facility, new ArrayList<>()); // Add a new list if none exists
        organizerEvents.get(facility).add(event); // Add the event to the corresponding facility's list
    }

    // Remove an event by its ID from facility
    public boolean removeEvent(String eventID) {
        for (List<Event> events : organizerEvents.values()) {
            if (events.removeIf(event -> event.getEventID().equals(eventID))) {
                return true; // Return true if event was found and removed
            }
        }
        return false; // Return false if event was not found
    }

    // Get all events across all facilities
    public List<Event> getAllEvents() {
        List<Event> allEvents = new ArrayList<>();
        for (List<Event> events : organizerEvents.values()) {
            allEvents.addAll(events);
        }
        return allEvents;
    }

    // Find an event by its ID across all facilities
    public Event findEventByID(String eventID) {
        for (List<Event> events : organizerEvents.values()) {
            for (Event event : events) {
                if (event.getEventID().equals(eventID)) {
                    return event;
                }
            }
        }
        return null; // Return null if no event is found
    }
}
