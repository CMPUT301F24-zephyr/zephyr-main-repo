package com.example.plannet.Event;

import com.example.plannet.Entrant.Entrant;

import java.util.ArrayList;

public class EventWaitlistRejected {
    private ArrayList<Entrant> rejectedEntrants;

    public EventWaitlistRejected() {
        this.rejectedEntrants = new ArrayList<>();
    }

    public void addEntrant(Entrant entrant) {
        if (entrant != null) {
            this.rejectedEntrants.add(entrant);
        }
    }
}
