package com.example.plannet.Event;

import com.example.plannet.Entrant.EntrantProfile;

import java.util.ArrayList;

/**
 * Stores a list of all entrants on an event waitlist chosen by the lottery.
 */
public class EventWaitlistChosen {
    private ArrayList<EntrantProfile> chosenEntrants;

    /**
     * Constructor. Initializes an empty ArrayList.
     */
    public EventWaitlistChosen() {
        this.chosenEntrants = new ArrayList<>();
    }

    /**
     * Add an entrant to the list of chosen entrants when they are selected in the lottery
     *
     * @param entrant
     *      The entrant profile to add to the chosen list.
     */
    public void addChosenEntrant(EntrantProfile entrant) {
        if (entrant != null) {
            this.chosenEntrants.add(entrant);
        }
    }

    /**
     * Remove an entrant from the list of chosen entrants when they accept or reject an invite.
     *
     * @param entrant
     *      The entrant profile to remove from the chosen list.
     */
    public void removeChosenEntrant(EntrantProfile entrant) {
        if (entrant != null) {
            this.chosenEntrants.remove(entrant);
        }
    }
}
