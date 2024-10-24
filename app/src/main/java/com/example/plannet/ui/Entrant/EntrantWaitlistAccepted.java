package com.example.plannet.ui.Entrant;

import com.example.plannet.ui.Event.EventWaitlistAccepted;
import com.example.plannet.ui.Event.EventWaitlistPending;

import java.util.ArrayList;

public class EntrantWaitlistAccepted {
    private ArrayList<EventWaitlistAccepted> acceptedWaitLists;

    public EntrantWaitlistAccepted() {
        this.acceptedWaitLists = new ArrayList<>();
    }

    public void addWaitlist(EventWaitlistAccepted waitlist){
        if (waitlist != null) {
            acceptedWaitLists.add(waitlist);
        }
    }

    public void removeWaitlist(EventWaitlistAccepted waitlist){
        if (waitlist != null) {
            acceptedWaitLists.remove(waitlist);
        }
    }
}
