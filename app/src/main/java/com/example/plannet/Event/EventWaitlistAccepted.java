package com.example.plannet.Event;

import com.example.plannet.Entrant.EntrantProfile;
import java.util.ArrayList;

public class EventWaitlistAccepted {
    private ArrayList<EntrantProfile> acceptedEntrants;

    public EventWaitlistAccepted(ArrayList<EntrantProfile> acceptedEntrants) {
        this.acceptedEntrants = acceptedEntrants;
    }

    public void addEntrant(EntrantProfile entrant) {
        if (entrant != null) {
            this.acceptedEntrants.add(entrant);
        }
    }

    public ArrayList<EntrantProfile> getAcceptedEntrants() {
        return acceptedEntrants;
    }
}
