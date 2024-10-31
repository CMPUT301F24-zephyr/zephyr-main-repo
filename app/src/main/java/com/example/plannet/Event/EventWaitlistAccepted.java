package com.example.plannet.Event;


import com.example.plannet.Entrant.EntrantProfile;

import java.util.ArrayList;

public class EventWaitlistAccepted {
    private ArrayList<EntrantProfile> acceptedEntrants;

    public EventWaitlistAccepted(ArrayList<EntrantProfile> acceptedEntrants) {
        this.acceptedEntrants = new ArrayList<>();
    }

    public void addEntrant(EntrantProfile entrant) {
        if (entrant != null) {
            this.acceptedEntrants.add(entrant);
        }
    }
}
