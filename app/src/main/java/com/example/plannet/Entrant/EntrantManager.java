//package com.example.plannet.Entrant;
//
//import android.widget.Toast;
//import android.content.Context;
//
//import com.example.plannet.Event.EventWaitlistAccepted;
//import com.example.plannet.Event.EventWaitlistPending;
//import com.example.plannet.Event.EventWaitlistRejected;
//
///**
// * Handles addition and removal of entrants to waitlists
// */
//public class EntrantManager {
//
//    private EntrantDBConnector DBConnector;
//    private EntrantWaitlistPending EntrantPending;
//    private EntrantWaitlistAccepted EntrantAccepted;
//    private EntrantWaitlistRejected EntrantRejected;
//
//    /**
//     * Constructor for EntrantManager.
//     *
//     * @param dbConnector
//     * stores the dbConnector object
//     */
//    public EntrantManager(EntrantDBConnector dbConnector) {
//        this.DBConnector = dbConnector;
//        this.EntrantPending = new EntrantWaitlistPending();
//        this.EntrantAccepted = new EntrantWaitlistAccepted();
//        this.EntrantRejected = new EntrantWaitlistRejected();
//    }
//
//    /**
//     * Add entrant to a specific pending waitlist
//     *
//     * @param context
//     *      Context for Toast
//     * @param entrant
//     *      Entrant object passes so it can be added to the waitlist
//     * @param waitlist
//     *  waitlist pending object passed so we can extract the eventID and determine collection path
//     */
//    public void joinWaitlistPending(Context context, EntrantProfile entrant, EventWaitlistPending waitlist) {
//
//        if (waitlist.addEntrant(entrant)) {
//            //define collection path
//            String collectionPath = "events/" + waitlist.getEventID() + "/waitlist_" + waitlist.getType();
//
//            // Trying to debug
//            if (collectionPath != null && waitlist.getEventID() != null) {
//                DBConnector.addEntrantToWaitlist(
//                        collectionPath,waitlist.getEventID()
//                        ,
//                        entrant,
//                        // Success listener with Toast
//                        aVoid -> Toast.makeText(context, "Entrant added to Firebase waitlist successfully", Toast.LENGTH_SHORT).show(),
//                        // Failure listener with Toast
//                        e -> Toast.makeText(context, "An error occurred while adding to the waitlist. Please try again.", Toast.LENGTH_LONG).show()
//                );
//            } else {
//                Toast.makeText(context, "Error: Unable to add to the waitlist. Please contact support.", Toast.LENGTH_LONG).show();
//            }
//
//
//            this.EntrantPending.addWaitlist(waitlist);
//        } else {
//            Toast.makeText(context, "Entrant is already in the waitlist.", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    /**
//     * Adds entrant to the accepted waitlist
//     *
//     * @param context
//     *      Context for toast
//     * @param entrant
//     *      Stores the entrant object to be added to firebase
//     * @param waitlist
//     *      The waitlist object to extract the eventID and collection path
//     */
//    public void joinWaitlistAccepted(Context context, EntrantProfile entrant, EventWaitlistAccepted waitlist) {
//        if (waitlist.addEntrant(entrant)) {
//            String collectionPath = "events/" + waitlist.getEventID() + "/waitlist_" + waitlist.getType();
//
//            if (collectionPath != null && waitlist.getEventID() != null) {
//                DBConnector.addEntrantToWaitlist(
//                        collectionPath,
//                        waitlist.getEventID(),
//                        entrant,
//                        aVoid -> Toast.makeText(context, "Entrant added to accepted list successfully", Toast.LENGTH_SHORT).show(),
//                        e -> Toast.makeText(context, "An error occurred while adding to the accepted list. Please try again.", Toast.LENGTH_LONG).show()
//                );
//            } else {
//                Toast.makeText(context, "Error: Unable to add to the accepted list. Please contact support.", Toast.LENGTH_LONG).show();
//            }
//
//            this.EntrantAccepted.addWaitlist(waitlist);
//        } else {
//            Toast.makeText(context, "Entrant is already in the accepted list.", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    /**
//     * Adds entrant to the rejected waitlist
//     *
//     * @param context
//     *      Context for toast
//     * @param entrant
//     *      entrant object to be added to firebase
//     * @param waitlist
//     *      waitlist object passed to extract eventID and collection path
//     */
//    public void joinWaitlistRejected(Context context, EntrantProfile entrant, EventWaitlistRejected waitlist) {
//        if (waitlist.addEntrant(entrant)) {
//            String collectionPath = "events/" + waitlist.getEventID() + "/waitlist_" + waitlist.getType();
//
//            if (collectionPath != null && waitlist.getEventID() != null) {
//                DBConnector.addEntrantToWaitlist(
//                        collectionPath,
//                        waitlist.getEventID(),
//                        entrant,
//                        aVoid -> Toast.makeText(context, "Entrant added to rejected list successfully", Toast.LENGTH_SHORT).show(),
//                        e -> Toast.makeText(context, "An error occurred while adding to the rejected list. Please try again.", Toast.LENGTH_LONG).show()
//                );
//            } else {
//                Toast.makeText(context, "Error: Unable to add to the rejected list. Please contact support.", Toast.LENGTH_LONG).show();
//            }
//
//            this.EntrantRejected.addWaitlist(waitlist);
//        } else {
//            Toast.makeText(context, "Entrant is already in the rejected list.", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    /**
//     * Removes entrant from waitlist Pending
//     *
//     * @param context
//     *      Context for Toast
//     * @param entrant
//     *      Entrant object to be removed from firebase
//     * @param waitlist
//     *      waitlist object passed to extract eventID and collection path of object to be removed
//     */
//    public void leaveWaitlistPending(Context context, EntrantProfile entrant, EventWaitlistPending waitlist){
//        if (waitlist.removeEntrant(entrant)) {
//            // remove from firebase
//            String collectionPath = "events/" + waitlist.getEventID() + "/waitlist_" + waitlist.getType();
//
//            if (collectionPath != null && waitlist.getEventID() != null) {
//                DBConnector.removeEntrantFromWaitlist(
//                        collectionPath,
//                        waitlist.getEventID(),
//                        // Success listener with Toast
//                        aVoid -> Toast.makeText(context, "Entrant removed from Firebase waitlist successfully", Toast.LENGTH_SHORT).show(),
//                        // Failure listener with Toast
//                        e -> Toast.makeText(context, "An error occurred while removing from the waitlist. Please try again.", Toast.LENGTH_LONG).show()
//                );
//            } else {
//                Toast.makeText(context, "Error: Unable to remove from the waitlist. Please contact support.", Toast.LENGTH_LONG).show();
//            }
//            this.EntrantPending.removeWaitlist(waitlist);
//        }
//        else{
//            //not in waitlist
//        }
//    }
//}
