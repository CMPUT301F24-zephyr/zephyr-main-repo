package com.example.plannet.Event;

import com.example.plannet.Entrant.EntrantProfile;

import java.util.ArrayList;

/**
 * Stores a list of pending entrants on the waitlist for an event (not yet chosen by lottery).
 */
public class EventWaitlistPending {
    private ArrayList<EntrantProfile> waitlistEntrants;
    private String eventID;
    private String type;

    public String getType() {
        return type;
    }

    /**
     * Constructor. Creates a new ArrayList with no entrants for a new event.
     *
     * @param eventID
     *      The ID of the event with this waitlist.
     */
    public EventWaitlistPending(String eventID) {
        this.waitlistEntrants = new ArrayList<>();
        this.eventID = eventID;
        this.type = "pending";
    }

    /**
     * Adds an entrant to the pending waitlist.
     *
     * @param entrant
     *      The entrant to be added to the event waitlist.
     * @return
     *      True if the entrant was successfully added, false otherwise.
     */
    public Boolean addEntrant(EntrantProfile entrant) {
        if (entrant != null && !this.waitlistEntrants.contains(entrant)) {
            this.waitlistEntrants.add(entrant);
            return true;
        }
        return false;
    }

    /**
     * Get the ID of the event with this waitlist.
     *
     * @return
     *      The String ID of the waitlist.
     */
    public String getEventID() {
        return eventID;
    }

    /**
     * Remove an entrant from the pending waitlist.
     *
     * @param entrant
     *      The entrant to be removed from the waitlist.
     * @return
     *      True if the entrant was successfully removed, otherwise false.
     */
    public Boolean removeEntrant(EntrantProfile entrant) {
        if (entrant != null && this.waitlistEntrants.contains(entrant)) {
            this.waitlistEntrants.remove(entrant);
            return true;
        }
        return false;
    }
}
