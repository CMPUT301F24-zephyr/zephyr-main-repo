package com.example.plannet.ui.Event;

import com.example.plannet.ui.Entrant.Entrant;

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
