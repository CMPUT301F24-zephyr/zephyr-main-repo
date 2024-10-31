package com.example.plannet.Event;

import com.example.plannet.Entrant.EntrantProfile;
import java.util.ArrayList;

public class EventWaitlistPending {
    private ArrayList<EntrantProfile> waitlistEntrants;
    private String eventID;
    private String type;

    public EventWaitlistPending(String eventID) {
        this.waitlistEntrants = new ArrayList<>();
        this.eventID = eventID;
        this.type = "pending";
    }

    public Boolean addEntrant(EntrantProfile entrant) {
        if (entrant != null && !this.waitlistEntrants.contains(entrant)) {
            this.waitlistEntrants.add(entrant);
            return true;
        }
        return false;
    }

    public ArrayList<EntrantProfile> getWaitlistEntrants() {
        return waitlistEntrants;
    }

    public String getEventID() {
        return eventID;
    }

    public Boolean removeEntrant(EntrantProfile entrant) {
        if (entrant != null && this.waitlistEntrants.contains(entrant)) {
            this.waitlistEntrants.remove(entrant);
            return true;
        }
        return false;
    }
}
