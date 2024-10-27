package com.example.plannet.ui.Event;

import com.example.plannet.ui.Entrant.EntrantProfile;

import java.util.ArrayList;

public class EventWaitlistPending {
    private ArrayList<EntrantProfile> waitlistEntrants;

    public EventWaitlistPending() {
        this.waitlistEntrants = new ArrayList<>();
    }

    public void addEntrant(EntrantProfile entrant) {
        if (entrant != null) {
            this.waitlistEntrants.add(entrant);
        }
    }

    public void removeEntrant(EntrantProfile entrant) {
        if (entrant != null) {
            this.waitlistEntrants.remove(entrant);
        }
    }
}
