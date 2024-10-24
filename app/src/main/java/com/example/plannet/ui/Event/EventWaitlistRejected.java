package com.example.plannet.ui.Event;

import com.example.plannet.ui.Entrant.Entrant;

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
