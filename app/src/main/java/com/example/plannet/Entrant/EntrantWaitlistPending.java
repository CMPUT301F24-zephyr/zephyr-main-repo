package com.example.plannet.Entrant;

import com.example.plannet.Event.EventWaitlistPending;

import java.util.ArrayList;

/**
 * Class for storing the events the entrant is waiting for
 */
public class EntrantWaitlistPending {
    private ArrayList<EventWaitlistPending> joinedWaitLists;

    /**
     * Constructor for class that makes new array list object
     */
    public EntrantWaitlistPending() {
        this.joinedWaitLists = new ArrayList<>();
    }

    /**
     * Adds waitlist to list of all events the entrant is waiting for
     *
     * @param waitlist
     * waitlist to be added to the list
     */
    public void addWaitlist(EventWaitlistPending waitlist){
        if (waitlist != null && !joinedWaitLists.contains(waitlist)) {
            joinedWaitLists.add(waitlist);
        }
    }

    /**
     * removes waitlist from list of all events the entrant is waiting for
     *
     * @param waitlist
     * waitlist to be removed from the list
     */
    public void removeWaitlist(EventWaitlistPending waitlist){
        if (waitlist != null && joinedWaitLists.contains(waitlist)) {
            joinedWaitLists.remove(waitlist);
        }
    }

}
