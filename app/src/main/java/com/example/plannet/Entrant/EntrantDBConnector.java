package com.example.plannet.Entrant;

import com.example.plannet.Event.EventWaitlistPending;
import com.example.plannet.FirebaseConnector;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


import java.util.HashMap;

public class EntrantDBConnector {
    private FirebaseConnector fireCon;

    public EntrantDBConnector() {
        fireCon = new FirebaseConnector();
    }

    public void addEntrantToWaitlist(String collectionPath, String eventID, EntrantProfile entrant,
                                     OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("deviceID", entrant.getDeviceID());
        data.put("entrant", entrant);
        // add onsuccess and onfailure for addign data to firebase??
        fireCon.addData(collectionPath, eventID, data, onSuccess, onFailure);

    }

    public void removeEntrantFromWaitlist(String collectionPath, String documentID,
                                          OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        fireCon.deleteData(collectionPath, documentID, onSuccess, onFailure);
    }
}