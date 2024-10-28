package com.example.plannet.Event;

import com.example.plannet.Entrant.Entrant;

import java.util.ArrayList;

public class EventWaitlistChosen {
    private ArrayList<Entrant> chosenEntrants;

    public EventWaitlistChosen() {
        this.chosenEntrants = new ArrayList<>();
    }

    public void addChosenEntrant(Entrant entrant) {
        if (entrant != null) {
            this.chosenEntrants.add(entrant);
        }
    }

    public void removeChosenEntrant(Entrant entrant) {
        if (entrant != null) {
            this.chosenEntrants.remove(entrant);
        }
    }
}
