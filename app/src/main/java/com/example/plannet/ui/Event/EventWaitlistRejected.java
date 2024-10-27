package com.example.plannet.ui.Event;

import com.example.plannet.ui.Entrant.EntrantProfile;

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
}
