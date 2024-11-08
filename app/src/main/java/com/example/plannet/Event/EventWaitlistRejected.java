package com.example.plannet.Event;

import com.example.plannet.Entrant.EntrantProfile;

import java.util.ArrayList;

/**
 * Stores a list of all rejected entrants (chosen and declined) for an event's waitlist.
 */
public class EventWaitlistRejected {
    private ArrayList<EntrantProfile> rejectedEntrants;

    /**
     * Constructor. Initializes an empty ArrayList.
     */
    public EventWaitlistRejected() {
        this.rejectedEntrants = new ArrayList<>();
    }

    /**
     * Add an entrant to the rejected list when they decline an invitation.
     *
     * @param entrant
     *      The entrant to be added to the list.
     */
    public void addEntrant(EntrantProfile entrant) {
        if (entrant != null) {
            this.rejectedEntrants.add(entrant);
        }
    }
}
