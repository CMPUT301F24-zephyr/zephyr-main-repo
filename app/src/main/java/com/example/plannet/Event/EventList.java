package com.example.plannet.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Stores a list of events organized by a facility. Events can be added, removed, or found.
 */
public class EventList {
    // Mapping each facilityID to its list of events
    private HashMap<String, List<Event>> organizerEvents;

    /**
     * Constructor. Initializes an empty EventList.
     */
    public EventList() {
        organizerEvents = new HashMap<>();
    }

    /**
     * Add an event to the EventList.
     *
     * @param event
     *      The event object to be added.
     */
    public void addEvent(Event event) {
        String facility = event.getFacility(); // Get facility name from event
        organizerEvents.putIfAbsent(facility, new ArrayList<>()); // Add a new list if none exists
        organizerEvents.get(facility).add(event); // Add the event to the corresponding facility's list
    }

    /**
     * Remove an event from the EventList.
     *
     * @param eventID
     *      The (String) ID of the event to be removed.
     * @return
     *      True if the event was removed successfully, otherwise false.
     */
    public boolean removeEvent(String eventID) {
        for (List<Event> events : organizerEvents.values()) {
            if (events.removeIf(event -> event.getEventID().equals(eventID))) {
                return true; // Return true if event was found and removed
            }
        }
        return false; // Return false if event was not found
    }

    /**
     * Get a list of all events in the system. Useful for admin implementation.
     *
     * @return
     *      A list of all event objects.
     */
    public List<Event> getAllEvents() {
        List<Event> allEvents = new ArrayList<>();
        for (List<Event> events : organizerEvents.values()) {
            allEvents.addAll(events);
        }
        return allEvents;
    }

    /**
     * Find an event by its ID across all facilities.
     *
     * @param eventID
     *      The ID of the event to be found.
     * @return
     *      The event if found, otherwise null (no event with that ID was found).
     */
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
