package com.example.plannet.ui.Entrant;

import com.example.plannet.ui.Event.EventWaitlistRejected;

import java.util.ArrayList;

public class EntrantWaitlistRejected {
    private ArrayList<EventWaitlistRejected> rejectedWaitLists;

    public EntrantWaitlistRejected() {
        this.rejectedWaitLists = new ArrayList<>();
    }

    public void addWaitlist(EventWaitlistRejected waitlist){
        if (waitlist != null) {
            rejectedWaitLists.add(waitlist);
        }
    }

    public void removeWaitlist(EventWaitlistRejected waitlist){
        if (waitlist != null) {
            rejectedWaitLists.remove(waitlist);
        }
    }
}
