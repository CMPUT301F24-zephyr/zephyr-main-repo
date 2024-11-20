package com.example.plannet.Entrant;

import java.util.ArrayList;
import java.util.List;

public class EntrantManager {
    private static EntrantManager instance;

    private EntrantWaitlistPending pendingWaitlist;
    private EntrantWaitlistPending acceptedWaitlist;
    private EntrantWaitlistPending rejectedWaitlist;

    private EntrantManager() {
        pendingWaitlist = new EntrantWaitlistPending();
        acceptedWaitlist = new EntrantWaitlistPending();
        rejectedWaitlist = new EntrantWaitlistPending();
    }

    public static synchronized EntrantManager getInstance() {
        if (instance == null) {
            instance = new EntrantManager();
        }
        return instance;
    }

    public EntrantWaitlistPending getPendingWaitlist() {
        return pendingWaitlist;
    }

    public EntrantWaitlistPending getAcceptedWaitlist() {
        return acceptedWaitlist;
    }

    public EntrantWaitlistPending getRejectedWaitlist() {
        return rejectedWaitlist;
    }

    public List<String> getAllEventIDs() {
        List<String> allEventIDs = new ArrayList<>();
        allEventIDs.addAll(pendingWaitlist.getWaitlist());
        allEventIDs.addAll(acceptedWaitlist.getWaitlist());
        allEventIDs.addAll(rejectedWaitlist.getWaitlist());
        return allEventIDs;
    }

    public void clearAllWaitlists() {
        pendingWaitlist.clearWaitlist();
        acceptedWaitlist.clearWaitlist();
        rejectedWaitlist.clearWaitlist();
    }

    public void addToPending(String eventID) {
        pendingWaitlist.addWaitlist(eventID);
    }

    public void addToAccepted(String eventID) {
        acceptedWaitlist.addWaitlist(eventID);
    }

    public void addToRejected(String eventID) {
        rejectedWaitlist.addWaitlist(eventID);
    }
}
