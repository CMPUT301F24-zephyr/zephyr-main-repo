package com.example.plannet.Entrant;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for storing the events the entrant has rejected
 */
public class EntrantWaitlistRejected {
    private List<String> rejectedWaitLists;

    /**
     * Constructor for class that initializes a new list
     */
    public EntrantWaitlistRejected() {
        this.rejectedWaitLists = new ArrayList<>();
    }

    /**
     * Adds eventID to the list of rejected events
     *
     * @param eventID
     * The ID of the event to be added
     */
    public void addWaitlist(String eventID) {
        if (eventID != null && !rejectedWaitLists.contains(eventID)) {
            rejectedWaitLists.add(eventID);
        }
    }

    /**
     * Removes eventID from the list of rejected events
     *
     * @param eventID
     * The ID of the event to be removed
     */
    public void removeWaitlist(String eventID) {
        if (eventID != null) {
            rejectedWaitLists.remove(eventID);
        }
    }

    /**
     * Retrieves the list of rejected events
     *
     * @return List of rejected event IDs
     */
    public List<String> getWaitlist() {
        return rejectedWaitLists;
    }

    public void clearWaitlist() {
        this.rejectedWaitLists.clear();
    }
}
