package com.example.plannet.Event;

import com.example.plannet.Entrant.Entrant;

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
