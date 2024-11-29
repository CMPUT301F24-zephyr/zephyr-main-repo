package com.example.plannet.Event;

import com.example.plannet.Entrant.EntrantProfile;

import java.util.ArrayList;

/**
 * Stores a list of all rejected entrants (chosen and declined) for an event's waitlist.
 */
public class EventWaitlistRejected {
    private ArrayList<EntrantProfile> rejectedEntrants;
    private String eventID;
    private String type;


    public EventWaitlistRejected(String eventID) {
        this.rejectedEntrants = new ArrayList<>();
        this.eventID = eventID;
        this.type = "rejected";
    }


    public String getEventID() {
        return eventID;
    }

    public String getType() {
        return type;
    }

    /**
     * adds an entrant to rejected entrants list
     * @param entrant
     * @return
     */
    public boolean addEntrant(EntrantProfile entrant) {
        if (entrant != null && !this.rejectedEntrants.contains(entrant)) {

            this.rejectedEntrants.add(entrant);
            return true;
        }
        return false;
    }

    /**
     * removes an entrant to rejected entrants list
     * @param entrant
     * @return
     */
    public boolean removeEntrant(EntrantProfile entrant) {
        if (entrant != null && this.rejectedEntrants.contains(entrant)) {
            this.rejectedEntrants.remove(entrant);
            return true;
        }
        return false;
    }
}
