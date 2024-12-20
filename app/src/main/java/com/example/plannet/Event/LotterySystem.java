package com.example.plannet.Event;


import com.example.plannet.Notification.EntrantNotifications;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.plannet.Entrant.EntrantProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * lottery system class which selects entrants from a draw and sends notifications
 */
public class LotterySystem {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    /**
     * Performs a lottery draw to randomly select a number of participants from the pending waitlist.
     * Returns a list of profiles that are selected.
     * @param numToDraw
     *      The number of participants to draw
     * @param pendingWaitlist
     *      A list of EntrantProfiles to draw from
     * @return
     *      A list of EntrantProfiles that are selected
     */
    public List<EntrantProfile> drawParticipants(List<EntrantProfile> pendingWaitlist, int numToDraw) {
        Random random = new Random();

        List<EntrantProfile> selectedParticipants = new ArrayList<>();

        // Ensure we do not draw more participants than the number of entrants in the waiting list
        if (numToDraw > pendingWaitlist.size()) {
            numToDraw = pendingWaitlist.size();
        }

        // Randomly select participants until we have draw the number of people we want
        while (selectedParticipants.size() < numToDraw) {
            int randomIndex = random.nextInt(pendingWaitlist.size());
            EntrantProfile selectedEntrant = pendingWaitlist.remove(randomIndex);  // .remove is like .pop() in other languages
            selectedParticipants.add(selectedEntrant);
        }

        return selectedParticipants;
    }

    /**
     * Processes selected participants by moving them from the pending waitlist to the chosen waitlist.
     *
     */
    public void processSelectedParticipants(EventWaitlistPending pendingWaitlist,
                                            EventWaitlistChosen chosenWaitlist,
                                            List<EntrantProfile> selectedEntrants) {
        String eventId = pendingWaitlist.getEventID();

        for (EntrantProfile entrant : selectedEntrants) {
            // Add to chosen list in memory
            chosenWaitlist.addChosenEntrant(entrant);
            // Remove from pending list in memory
            pendingWaitlist.removeEntrant(entrant);

            // Queue Notification
            db.collection("events")
                    .document(eventId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String eventName = documentSnapshot.getString("eventName");
                            EntrantNotifications entrantNotifications = new EntrantNotifications();
                            entrantNotifications.queueNotification(
                                    entrant.getUserId(),
                                    eventName,
                                    "Congrats! You have been chosen for the event: " + eventName + ". Please respond to your invitation.",
                                    eventId
                            );
                        }
                    });

            // Update Firestore: Add to chosen list
            db.collection("events")
                    .document(eventId)
                    .update("chosenEntrants", FieldValue.arrayUnion(entrant))
                    .addOnSuccessListener(aVoid -> System.out.println("Participant added to chosen list: " + entrant.getName()))
                    .addOnFailureListener(e -> System.err.println("Error adding to chosen list: " + e.getMessage()));

            // Update Firestore: Remove from pending list
            db.collection("events")
                    .document(eventId)
                    .update("pendingEntrants", FieldValue.arrayRemove(entrant))
                    .addOnSuccessListener(aVoid -> System.out.println("Participant removed from pending list: " + entrant.getName()))
                    .addOnFailureListener(e -> System.err.println("Error removing from pending list: " + e.getMessage()));

        }
    }

    /**
     * If a participant who was previously chosen want to reject the event, we remove him from the chosen list to the rejected list
     */
    public void rejectParticipant(EventWaitlistChosen chosenWaitlist,
                                  EventWaitlistRejected rejectedWaitlist,
                                  EntrantProfile entrant) {

        String eventId = rejectedWaitlist.getEventID();
        chosenWaitlist.removeChosenEntrant(entrant);  // Remove from chosen list
        rejectedWaitlist.addEntrant(entrant);         // Add to rejected

        // Queue Notification
        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String eventName = documentSnapshot.getString("eventName");
                        EntrantNotifications entrantNotifications = new EntrantNotifications();
                        entrantNotifications.queueNotification(
                                entrant.getUserId(),
                                "Better Luck Next Time!",
                                "Unfortunately, you weren't chosen for the event " + eventName + "this round.",
                                eventId
                        );
                    }
                });

        // Update Firestore: Remove from chosen list
        db.collection("events")
                .document(eventId)
                .update("chosenEntrants", FieldValue.arrayRemove(entrant))
                .addOnSuccessListener(aVoid -> System.out.println("Participant removed from chosen list: " + entrant.getName()))
                .addOnFailureListener(e -> System.err.println("Error removing from chosen list: " + e.getMessage()));

        // Update Firestore: Add to rejected list
        db.collection("events")
                .document(eventId)
                .update("rejectedEntrants", FieldValue.arrayUnion(entrant))
                .addOnSuccessListener(aVoid -> System.out.println("Participant added to rejected list: " + entrant.getName()))
                .addOnFailureListener(e -> System.err.println("Error adding to rejected list: " + e.getMessage()));

    }

    /**
     * After the previous participant reject the event, we redraw a new participant from the waiting list
     * We repeat the same step: Selects a new participant from the pending waitlist and moves them to the chosen list.
     * In the end, we return the new participant list or null if no participants are left in the pending list
     */
    public EntrantProfile redrawParticipant(EventWaitlistPending pendingWaitlist,
                                            EventWaitlistChosen chosenWaitlist) {
        List<EntrantProfile> pendingList = pendingWaitlist.getWaitlistEntrants();

        // Ensure there are entrants left in the pending list
        //if there are still entrants left, repeat the steps of adding and removing entrants
        if (!pendingList.isEmpty()) {
            // Jon changed pendingWaitlist on this line to PendingList. It was causing errors.
            EntrantProfile newParticipant = drawParticipants(pendingList, 1).get(0);// Randomly select one entrant
            chosenWaitlist.addChosenEntrant(newParticipant);
            pendingWaitlist.removeEntrant(newParticipant);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String eventId = pendingWaitlist.getEventID();

            // Update Firestore: Add to chosen list
            db.collection("events")
                    .document(eventId)
                    .update("chosenEntrants", FieldValue.arrayUnion(newParticipant))
                    .addOnSuccessListener(aVoid -> System.out.println("Participant added to chosen list: " + newParticipant.getName()))
                    .addOnFailureListener(e -> System.err.println("Error adding to chosen list: " + e.getMessage()));

            // Update Firestore: Remove from pending list
            db.collection("events")
                    .document(eventId)
                    .update("pendingEntrants", FieldValue.arrayRemove(newParticipant))
                    .addOnSuccessListener(aVoid -> System.out.println("Participant removed from pending list: " + newParticipant.getName()))
                    .addOnFailureListener(e -> System.err.println("Error removing from pending list: " + e.getMessage()));

            // Queue Notification
            db.collection("events")
                    .document(eventId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String eventName = documentSnapshot.getString("eventName");
                            EntrantNotifications entrantNotifications = new EntrantNotifications();
                            entrantNotifications.queueNotification(
                                    newParticipant.getUserId(),
                                    eventName,
                                    "Congrats! You have been chosen for the event: " + eventName + ". Please respond to your invitation.",
                                    eventId
                            );
                        } else {
                            System.err.println("Event does not exist: " + eventId);
                        }
                    })
                    .addOnFailureListener(e -> System.err.println("Error fetching event name: " + e.getMessage()));

            return newParticipant;
        }

        return null; // we are done
    }
}
