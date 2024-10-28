package com.example.plannet.Event;

import com.example.plannet.Entrant.EntrantProfile;

import java.util.ArrayList;

public class EventWaitlistChosen {
    private ArrayList<EntrantProfile> chosenEntrants;

    public EventWaitlistChosen() {
        this.chosenEntrants = new ArrayList<>();
    }

    public void addChosenEntrant(EntrantProfile entrant) {
        if (entrant != null) {
            this.chosenEntrants.add(entrant);
        }
    }

    public void removeChosenEntrant(EntrantProfile entrant) {
        if (entrant != null) {
            this.chosenEntrants.remove(entrant);
        }
    }
}
