package com.example.plannet.Event;

import com.example.plannet.Entrant.Entrant;

import java.util.ArrayList;

public class EventWaitlistPending {
    private ArrayList<Entrant> waitlistEntrants;

    public EventWaitlistPending() {
        this.waitlistEntrants = new ArrayList<>();
    }

    public void addEntrant(Entrant entrant) {
        if (entrant != null) {
            this.waitlistEntrants.add(entrant);
        }
    }

    public void removeEntrant(Entrant entrant) {
        if (entrant != null) {
            this.waitlistEntrants.remove(entrant);
        }
    }
}
