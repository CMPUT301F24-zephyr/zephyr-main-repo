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
            String collectionPath = "events/" + waitlist.getEventID() + "/waitlist_" + waitlist.getType();

            // Trying to debug
            if (collectionPath != null && waitlist.getEventID() != null) {
                DBConnector.addEntrantToWaitlist(
                        collectionPath,waitlist.getEventID()
                        ,
                        entrant,
                        // Success listener with Toast
                        aVoid -> Toast.makeText(context, "Entrant added to Firebase waitlist successfully", Toast.LENGTH_SHORT).show(),
                        // Failure listener with Toast
                        e -> Toast.makeText(context, "An error occurred while adding to the waitlist. Please try again.", Toast.LENGTH_LONG).show()
                );
            } else {
                Toast.makeText(context, "Error: Unable to add to the waitlist. Please contact support.", Toast.LENGTH_LONG).show();
            }


            this.EntrantPending.addWaitlist(waitlist);
        } else {
            Toast.makeText(context, "Entrant is already in the waitlist.", Toast.LENGTH_SHORT).show();
        }
    }


    public void leaveWaitlist(Context context, EntrantProfile entrant, EventWaitlistPending waitlist){
        if (waitlist.removeEntrant(entrant)) {
            // remove from firebase
            String collectionPath = "events/" + waitlist.getEventID() + "/waitlist_" + waitlist.getType();

            if (collectionPath != null && waitlist.getEventID() != null) {
                DBConnector.removeEntrantFromWaitlist(
                        collectionPath,
                        waitlist.getEventID(),
                        // Success listener with Toast
                        aVoid -> Toast.makeText(context, "Entrant removed from Firebase waitlist successfully", Toast.LENGTH_SHORT).show(),
                        // Failure listener with Toast
                        e -> Toast.makeText(context, "An error occurred while removing from the waitlist. Please try again.", Toast.LENGTH_LONG).show()
                );
            } else {
                Toast.makeText(context, "Error: Unable to remove from the waitlist. Please contact support.", Toast.LENGTH_LONG).show();
            }
            this.EntrantPending.removeWaitlist(waitlist);
        }
        else{
            // not in waitlist
        }
    }
}
