package com.example.plannet.Entrant;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//
//public class EntrantLotterySystem {
//
//    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
//    private final EntrantManager entrantManager = EntrantManager.getInstance();
//
//    /**
//     * Randomly selects a specified number of entrants from the pending waitlist.
//     */
//    public List<String> drawParticipants(int numToSelect) {
//        List<String> pendingList = entrantManager.getPendingWaitlist();
//        Random random = new Random();
//        List<String> selectedParticipants = new ArrayList<>();
//
//        // Ensure we don't select more participants than are available
//        numToSelect = Math.min(numToSelect, pendingList.size());
//
//        // Randomly select participants without duplication
//        while (selectedParticipants.size() < numToSelect) {
//            int randomIndex = random.nextInt(pendingList.size());
//            String selectedEntrant = pendingList.get(randomIndex);
//
//            if (!selectedParticipants.contains(selectedEntrant)) {
//                selectedParticipants.add(selectedEntrant);
//            }
//        }
//
//        return selectedParticipants;
//    }
//
//    /**
//     * Processes selected participants by moving them from the pending to the accepted waitlist
//     */
//    public void processSelectedParticipants(List<String> selectedEntrants) {
//        List<String> pendingList = entrantManager.getPendingWaitlist();
//        List<String> acceptedList = entrantManager.getAcceptedWaitlist();
//
//        for (String entrantId : selectedEntrants) {
//            // Move entrant from pending to accepted list
//            pendingList.remove(entrantId);
//            acceptedList.add(entrantId);
//
//            // Update the entrant's status in Firebase
//            db.collection("entrants")
//                    .document(entrantId)
//                    .update("status", "accepted")
//                    .addOnSuccessListener(aVoid -> System.out.println("Entrant status updated to accepted: " + entrantId))
//                    .addOnFailureListener(e -> System.err.println("Error updating entrant status: " + entrantId));
//        }
//    }
//
//    /**
//     * Rejects a participant by moving them from the accepted waitlist to the rejected waitlist.
//     */
//    public void rejectParticipant(String entrantId) {
//        List<String> acceptedList = entrantManager.getAcceptedWaitlist();
//        List<String> rejectedList = entrantManager.getRejectedWaitlist();
//
//        // Move entrant from accepted to rejected list
//        acceptedList.remove(entrantId);
//        rejectedList.add(entrantId);
//
//        // Update the entrant's status in Firebase
//        db.collection("entrants")
//                .document(entrantId)
//                .update("status", "rejected")
//                .addOnSuccessListener(aVoid -> System.out.println("Entrant status updated to rejected: " + entrantId))
//                .addOnFailureListener(e -> System.err.println("Error updating entrant status: " + entrantId));
//    }
//
//    /**
//     * Redraws a single participant from the pending waitlist and moves them to the accepted waitlist.
//     *
//     * @return The newly selected participant's ID.
//     */
//    public String redrawParticipant() {
//        List<String> pendingList = entrantManager.getPendingWaitlist();
//        List<String> acceptedList = entrantManager.getAcceptedWaitlist();
//
//        if (!pendingList.isEmpty()) {
//            // Randomly select one entrant
//            Random random = new Random();
//            int randomIndex = random.nextInt(pendingList.size());
//            String entrantId = pendingList.get(randomIndex);
//
//            // Move entrant from pending to accepted list
//            pendingList.remove(entrantId);
//            acceptedList.add(entrantId);
//
//            // Update the entrant's status in Firebase
//            db.collection("entrants")
//                    .document(entrantId)
//                    .update("status", "accepted")
//                    .addOnSuccessListener(aVoid -> System.out.println("Entrant redrawn to accepted: " + entrantId))
//                    .addOnFailureListener(e -> System.err.println("Error redrawing entrant to accepted: " + entrantId));
//
//            return entrantId;
//        }
//
//        // We are done
//        return null;
//    }
//}
