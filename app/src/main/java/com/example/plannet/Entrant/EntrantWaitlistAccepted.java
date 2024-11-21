package com.example.plannet.Entrant;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for storing the events the entrant has accepted
 */
public class EntrantWaitlistAccepted {
    private List<String> acceptedWaitLists;

    /**
     * Constructor for class that initializes a new list
     */
    public EntrantWaitlistAccepted() {
        this.acceptedWaitLists = new ArrayList<>();
    }

    /**
     * Adds eventID to the list of accepted events
     *
     * @param eventID
     * The ID of the event to be added
     */
    public void addWaitlist(String eventID) {
        if (eventID != null && !acceptedWaitLists.contains(eventID)) {
            acceptedWaitLists.add(eventID);
        }
    }

    /**
     * Removes eventID from the list of accepted events
     *
     * @param eventID
     * The ID of the event to be removed
     */
    public void removeWaitlist(String eventID) {
        if (eventID != null) {
            acceptedWaitLists.remove(eventID);
        }
    }

    /**
     * Retrieves the list of accepted events
     *
     * @return List of accepted event IDs
     */
    public List<String> getWaitlist() {
        return acceptedWaitLists;
    }

    public void clearWaitlist() {
        this.acceptedWaitLists.clear();
    }
}
