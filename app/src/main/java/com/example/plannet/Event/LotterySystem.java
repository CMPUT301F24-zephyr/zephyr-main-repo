package com.example.plannet.Event;


import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.plannet.Entrant.EntrantProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LotterySystem {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    /**
     * Performs a lottery draw to randomly select a number of participants from the pending waitlist.
     */
    public List<EntrantProfile> drawParticipants(EventWaitlistPending pendingWaitlist, int numToDraw) {
        Random random = new Random();

        List<EntrantProfile> selectedParticipants = new ArrayList<>();
        List<EntrantProfile> waitingList = pendingWaitlist.getWaitlistEntrants();// Get the pending entreants from the waiting list

        // Ensure we do not draw more participants than the number of entrants in the waiting list
        if (numToDraw > waitingList.size()) {
            numToDraw = waitingList.size();
        }

        // Randomly select participants until we have draw the number of people we want
        while (selectedParticipants.size() < numToDraw) {
            int randomIndex = random.nextInt(waitingList.size());
            EntrantProfile selectedEntrant = waitingList.get(randomIndex);

            //check the selected list to make sure there is no duplication
            if (!selectedParticipants.contains(selectedEntrant)) {
                selectedParticipants.add(selectedEntrant);
            }
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
        rejectedWaitlist.addEntrant(entrant);         // Add to rejected list

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
            EntrantProfile newParticipant = drawParticipants(pendingWaitlist, 1).get(0);// Randomly select one entrant
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

            return newParticipant;
        }

        return null; // we are done
    }
}
