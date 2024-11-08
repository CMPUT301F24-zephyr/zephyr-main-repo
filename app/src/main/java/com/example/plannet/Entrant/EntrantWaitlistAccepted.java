package com.example.plannet.Entrant;

import com.example.plannet.Event.EventWaitlistAccepted;

import java.util.ArrayList;

/**
 * Class for storing the events the entrant has accepted
 */
public class EntrantWaitlistAccepted {
    private ArrayList<EventWaitlistAccepted> acceptedWaitLists;

    /**
     * Construcor for class that makes new array list object
     */
    public EntrantWaitlistAccepted() {
        this.acceptedWaitLists = new ArrayList<>();
    }

    /**
     * Adds watlist to list of all events the entrant has accepted
     *
     * @param waitlist
     * waitlist to be added
     */
    public void addWaitlist(EventWaitlistAccepted waitlist){
        if (waitlist != null) {
            acceptedWaitLists.add(waitlist);
        }
    }

    /**
     * Removed waitlist from list of all events the entrant has accepted
     *
     * @param waitlist
     * waitlist to be removed
     */
    public void removeWaitlist(EventWaitlistAccepted waitlist){
        if (waitlist != null) {
            acceptedWaitLists.remove(waitlist);
        }
    }
}
