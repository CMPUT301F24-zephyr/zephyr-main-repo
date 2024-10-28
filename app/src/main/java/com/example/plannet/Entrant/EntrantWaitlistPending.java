package com.example.plannet.Entrant;

import com.example.plannet.Event.EventWaitlistPending;

import java.util.ArrayList;

public class EntrantWaitlistPending {
    private ArrayList<EventWaitlistPending> joinedWaitLists;

    public EntrantWaitlistPending() {
        this.joinedWaitLists = new ArrayList<>();
    }

    public void addWaitlist(EventWaitlistPending waitlist){
        if (waitlist != null) {
            joinedWaitLists.add(waitlist);
        }
    }

    public void removeWaitlist(EventWaitlistPending waitlist){
        if (waitlist != null) {
            joinedWaitLists.remove(waitlist);
        }
    }

}
