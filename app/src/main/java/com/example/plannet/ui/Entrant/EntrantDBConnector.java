package com.example.plannet.ui.Entrant;

import com.example.plannet.ui.Event.EventWaitlistPending;
import com.example.plannet.ui.Firebase.FirebaseConnector;

import java.util.HashMap;

public class EntrantDBConnector {
    private FirebaseConnector fireCon;

    public EntrantDBConnector() {
        fireCon = new FirebaseConnector();
    }

    public void addEntrantToWaitlist(EntrantProfile entrant, EventWaitlistPending waitlist) {
        HashMap<String, Object> data = new HashMap<>();
        data.put(entrant.getName(), entrant);
        //fireCon.addData("waitlists", entrant.getDeviceID(), data);

    }

}
