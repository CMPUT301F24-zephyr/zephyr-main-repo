package com.example.plannet.ui.Event;

import com.example.plannet.ui.Entrant.Entrant;

import java.util.ArrayList;

public class EventWaitlistPending {
    private ArrayList<Entrant> waitlistEntrants;

    public EventWaitlistPending() {
        this.waitlistEntrants = new ArrayList<>();
    }

    public void addEntrant(Entrant entrant) {
        this.waitlistEntrants.add(entrant);
    }

    public void removeEntrant(Entrant entrant) {
        this.waitlistEntrants.remove(entrant);
    }
}
