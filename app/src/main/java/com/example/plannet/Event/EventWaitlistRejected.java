package com.example.plannet.Event;

import com.example.plannet.Entrant.EntrantProfile;
import java.util.ArrayList;

public class EventWaitlistRejected {
    private ArrayList<EntrantProfile> rejectedEntrants;

    public EventWaitlistRejected() {
        this.rejectedEntrants = new ArrayList<>();
    }

    public void addEntrant(EntrantProfile entrant) {
        if (entrant != null) {
            this.rejectedEntrants.add(entrant);
        }
    }

    public ArrayList<EntrantProfile> getRejectedEntrants() {
        return rejectedEntrants;
    }
}
