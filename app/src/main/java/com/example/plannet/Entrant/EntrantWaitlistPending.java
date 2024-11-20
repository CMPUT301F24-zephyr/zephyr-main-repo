package com.example.plannet.Entrant;

import java.util.ArrayList;

/**
 * Class for storing the event IDs the entrant is waiting for.
 */
public class EntrantWaitlistPending {
    private ArrayList<String> joinedWaitLists;

    /**
     * Constructor for class that makes a new ArrayList object
     */
    public EntrantWaitlistPending() {
        this.joinedWaitLists = new ArrayList<>();
    }

    /**
     * Adds an event ID to the list of all events the entrant is waiting for.
     *
     * @param eventID The event ID to be added to the list.
     */
    public void addWaitlist(String eventID) {
        if (eventID != null && !joinedWaitLists.contains(eventID)) {
            joinedWaitLists.add(eventID);
        }
    }

    /**
     * Removes an event ID from the list of all events the entrant is waiting for.
     *
     * @param eventID The event ID to be removed from the list.
     */
    public void removeWaitlist(String eventID) {
        if (eventID != null && joinedWaitLists.contains(eventID)) {
            joinedWaitLists.remove(eventID);
        }
    }

    public ArrayList<String> getWaitlist() {
        return joinedWaitLists;
    }

    public void clearWaitlist() {
        this.joinedWaitLists.clear();
    }

}
