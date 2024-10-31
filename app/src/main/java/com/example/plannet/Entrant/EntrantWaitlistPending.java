package com.example.plannet.Entrant;

import java.util.ArrayList;

public class EntrantWaitlistPending {
    private ArrayList<String> pendingEventIDs;  // List of event IDs for which the entrant is pending
    private String entrantID;

    // Constructor
    public EntrantWaitlistPending(String entrantID) {
        this.pendingEventIDs = new ArrayList<>();
        this.entrantID = entrantID;
    }

    // Method to add an event ID to the pending waitlist
    public boolean addPendingEvent(String eventID) {
        if (eventID != null && !pendingEventIDs.contains(eventID)) {
            pendingEventIDs.add(eventID);
            return true;
        }
        return false;
    }

    // Method to remove an event ID from the pending waitlist
    public boolean removePendingEvent(String eventID) {
        if (eventID != null && pendingEventIDs.contains(eventID)) {
            pendingEventIDs.remove(eventID);
            return true;
        }
        return false;
    }

    // Get the list of pending event IDs
    public ArrayList<String> getPendingEventIDs() {
        return pendingEventIDs;
    }

    // Get the entrant's ID
    public String getEntrantID() {
        return entrantID;
    }

    // Check if the entrant is pending for a specific event ID
    public boolean isPendingForEvent(String eventID) {
        return pendingEventIDs.contains(eventID);
    }
}
