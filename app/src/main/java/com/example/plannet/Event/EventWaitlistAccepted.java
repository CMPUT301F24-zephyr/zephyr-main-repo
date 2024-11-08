package com.example.plannet.Event;


import com.example.plannet.Entrant.EntrantProfile;

import java.util.ArrayList;

/**
 * Stores a list of all accepted entrants for an events waitlist.
 */
public class EventWaitlistAccepted {
    private ArrayList<EntrantProfile> acceptedEntrants;

    /**
     * Constructor. Initializes an empty ArrayList.
     *
     * @param acceptedEntrants
     *      ArrayList of entrant profiles that have accepted the event invite.
     */
    public EventWaitlistAccepted(ArrayList<EntrantProfile> acceptedEntrants) {
        this.acceptedEntrants = new ArrayList<>();
    }

    /**
     * Add an entrant to the list of accepted entrants when they accept the invite.
     *
     * @param entrant
     *      The entrant profile to add to the accepted list.
     */
    public void addEntrant(EntrantProfile entrant) {
        if (entrant != null) {
            this.acceptedEntrants.add(entrant);
        }
    }
}
