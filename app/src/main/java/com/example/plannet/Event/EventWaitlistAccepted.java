package com.example.plannet.Event;


import com.example.plannet.Entrant.EntrantProfile;

import java.util.ArrayList;

public class EventWaitlistAccepted {
    private ArrayList<EntrantProfile> acceptedEntrants;
    private String eventID;
    private String type;

    public EventWaitlistAccepted(String eventID) {
        this.acceptedEntrants = new ArrayList<>();
        this.eventID = eventID;
        this.type = "accepted";
    }

    public String getEventID() {
        return eventID;
    }

    public String getType() {
        return type;
    }

    public boolean addEntrant(EntrantProfile entrant) {
        if (entrant != null && !this.acceptedEntrants.contains(entrant)) {
            this.acceptedEntrants.add(entrant);
            return true;
        }
        return false;
    }
    public boolean removeEntrant(EntrantProfile entrant) {
        if (entrant != null && this.acceptedEntrants.contains(entrant)) {
            this.acceptedEntrants.remove(entrant);
            return true;
        }
        return false;
    }

}
