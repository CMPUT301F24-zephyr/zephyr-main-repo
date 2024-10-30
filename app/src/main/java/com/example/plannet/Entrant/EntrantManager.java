package com.example.plannet.Entrant;

import android.widget.Toast;
import android.content.Context;

import com.example.plannet.Event.EventWaitlistPending;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;



public class EntrantManager {

    private EntrantDBConnector DBConnector;
    private EntrantWaitlistPending EntrantPending;
    private EntrantWaitlistAccepted EntrantAccepted;
    private EntrantWaitlistRejected EntrantRejected;

    public EntrantManager(EntrantDBConnector dbConnector) {
        this.DBConnector = dbConnector;
        this.EntrantPending = new EntrantWaitlistPending();
        this.EntrantAccepted = new EntrantWaitlistAccepted();
        this.EntrantRejected = new EntrantWaitlistRejected();
    }

    public void joinWaitlist(Context context, EntrantProfile entrant, EventWaitlistPending waitlist) {

        if (waitlist.addEntrant(entrant)) {
            //define collection path
            String collectionPath = "events/" + waitlist.getEventID() + "/waitlist/" + waitlist.getType();

            // Trying to debug
            DBConnector.addEntrantToWaitlist(
                    collectionPath,
                    entrant.getDeviceID(),
                    entrant,
                    // Success listener with Toast
                    aVoid -> Toast.makeText(context, "Entrant added to Firebase waitlist successfully", Toast.LENGTH_SHORT).show(),
                    // Failure listener with Toast
                    e -> Toast.makeText(context, "Error adding entrant to Firebase: " + e.getMessage(), Toast.LENGTH_LONG).show()
            );

            this.EntrantPending.addWaitlist(waitlist);
        } else {
            Toast.makeText(context, "Entrant is already in the waitlist.", Toast.LENGTH_SHORT).show();
        }
    }


    public void leaveWaitlist(EntrantProfile entrant, EventWaitlistPending waitlist){
        if (waitlist.removeEntrant(entrant)) {
            // remove from firebase
            this.EntrantPending.removeWaitlist(waitlist);
        }
        else{
            // not in waitlist
        }
    }
}
