package com.example.plannet.Entrant;

import com.example.plannet.Event.EventWaitlistRejected;

import java.util.ArrayList;

/**
 * Class for storing the events the entrant has rejected
 */
public class EntrantWaitlistRejected {
    private ArrayList<EventWaitlistRejected> rejectedWaitLists;

    public EntrantWaitlistRejected() {
        this.rejectedWaitLists = new ArrayList<>();
    }

    /**
     * adds waitlist to list of all events the entrant has rejected
     *
     * @param waitlist
     * waitlist to be added to the list
     */
    public void addWaitlist(EventWaitlistRejected waitlist){
        if (waitlist != null) {
            rejectedWaitLists.add(waitlist);
        }
    }

    /**
     * removes waitlist from list of all events the entrant has rejected
     *
     * @param waitlist
     * waitlist to be removed from the list
     */
    public void removeWaitlist(EventWaitlistRejected waitlist){
        if (waitlist != null) {
            rejectedWaitLists.remove(waitlist);
        }
    }
}
