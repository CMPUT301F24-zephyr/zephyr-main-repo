package com.example.plannet.ui.Event;

import com.example.plannet.ui.Entrant.Entrant;

import java.util.ArrayList;

public class EventWaitlistAccepted {
    private ArrayList<Entrant> acceptedEntrants;

    public EventWaitlistAccepted(ArrayList<Entrant> acceptedEntrants) {
        this.acceptedEntrants = new ArrayList<>();
    }

    public void addEntrant(Entrant entrant) {
        if (entrant != null) {
            this.acceptedEntrants.add(entrant);
        }
    }
}
