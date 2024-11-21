package com.example.plannet;

import android.util.Log;

import com.example.plannet.Event.Event;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

public class FirebaseConnector {

    private FirebaseFirestore db;

    public FirebaseConnector() {
        db = FirebaseFirestore.getInstance(); // lab5
    }

    public void addData(String collectionPath, String documentID, HashMap<String, Object> data,
                        OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        db.collection(collectionPath)
                .document(documentID)
                .set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void deleteData(String collectionPath, String documentID,
                           OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        db.collection(collectionPath)
                .document(documentID)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void checkIfDeviceExists(String deviceID, OnSuccessListener<DocumentSnapshot> onSuccess, OnFailureListener onFailure) {
        db.collection("users").document(deviceID).get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    /**
     * add OR update a facility name/location to DB
     * @param deviceID
     * device ID information
     * @param facilityName
     * name of facility
     * @param facilityLocation
     * location of facility
     */
    public void addFacilityToDB(String deviceID, String facilityName, String facilityLocation) {
        Map<String, Object> facilityDetails = new HashMap<>();
        facilityDetails.put("name", facilityName);
        facilityDetails.put("location", facilityLocation);

        // Adding facility field to user's document with merge option
        db.collection("users").document(deviceID)
                .set(Collections.singletonMap("facility", facilityDetails), SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseConnector", "Facility added/updated for user: " + deviceID);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseConnector", "Failed to add/update facility info", e);
                });
    }

    public void addEventToDB(String deviceID, String eventID, Map<String, Object> eventDetails) {
        // Add the eventID to the user's createdEvents array
        db.collection("users").document(deviceID)
                .update("createdEvents", FieldValue.arrayUnion(eventID))
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseConnector", "EventID " + eventID + " added to createdEvents for user: " + deviceID);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseConnector", "Failed to add eventID to createdEvents", e);
                });

        // Store full event details in the global events collection
        db.collection("events").document(eventID)
                .set(eventDetails)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseConnector", "Event details added for eventID: " + eventID);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseConnector", "Error adding event details to events collection with ID: " + eventID, e);
                });
    }

    public void addJoinedEvent(String deviceID, String eventID) {
        // Adds eventID to the joinedEvents array under the user's document
        db.collection("users").document(deviceID)
                .update("joinedEvents", FieldValue.arrayUnion(eventID))
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseConnector", "EventID " + eventID + " added to joinedEvents for user: " + deviceID);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseConnector", "Failed to add eventID to joinedEvents", e);
                });
    }
    public void addUserToFirestore(String uniqueID) {
        Map<String, Object> user = new HashMap<>();
        user.put("UUID", uniqueID);

        db.collection("users").document(uniqueID)
                .set(user)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User added to Firestore"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error saving user", e));
    }
    public void addUserInfoToFirestore(String uniqueID, Map<String, Object> userInfo,
                                       OnSuccessListener<Void> onSuccessListener,
                                       OnFailureListener onFailureListener) {
        db.collection("users").document(uniqueID)
                .collection("userInfo").document("profile")
                .set(userInfo)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void checkIfFacilityDataIsValid(String userID, OnSuccessListener<Map<String, Object>> onSuccess, OnFailureListener onFailure) {
        db.collection("users").document(userID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Object facilityObj = documentSnapshot.get("facility");
                        if (facilityObj instanceof Map) {
                            Map<String, Object> facilityMap = (Map<String, Object>) facilityObj;
                            onSuccess.onSuccess(facilityMap);  // Pass facility data to the success listener
                        } else {
                            onFailure.onFailure(new Exception("Facility data is missing or not in expected format"));
                        }
                    } else {
                        onFailure.onFailure(new Exception("No document found for userID: " + userID));
                    }
                })
                .addOnFailureListener(onFailure);
    }

    public void getUserInfo(String userID, OnSuccessListener<Map<String, Object>> onSuccess, OnFailureListener onFailure) {
        db.collection("users").document(userID)
                .collection("userInfo").document("profile")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        onSuccess.onSuccess(documentSnapshot.getData()); // Pass user data to onSuccess listener
                    } else {
                        onFailure.onFailure(new Exception("User document does not exist"));
                    }
                })
                .addOnFailureListener(onFailure);
    }

    // See OrganizerHashedQrListFragment.java for how to use
    // Resource for callback: https://www.baeldung.com/java-callback-functions
    public void getOrganizerEventsList(String userID, GetOrganizerEventsCallback callback) {
        ArrayList<Event> events = new ArrayList<>();

        db.collection("users").document(userID).get()
                .addOnCompleteListener(organizerTask -> {
                    if (organizerTask.isSuccessful()) {
                        DocumentSnapshot document = organizerTask.getResult();
                        if (document.exists()) {
                            List<String> eventNames = (List<String>) document.get("createdEvents");
                            if (eventNames != null) {
                                final int totalEvents = eventNames.size();
                                final int[] completedEvents = {0};

                                for (String eventName : eventNames) {
                                    Log.d("Firestore", "Got event for organizer: " + userID + " with name: " + eventName);
                                    db.collection("events").document(eventName).get()
                                            .addOnCompleteListener(eventTask -> {
                                                if (eventTask.isSuccessful()) {
                                                    DocumentSnapshot eventDocument = eventTask.getResult();
                                                    if (eventDocument.exists()) {
                                                        try {
                                                            // Get all the attributes from firebase
                                                            String name = (String) eventDocument.get("eventName");
                                                            String price = (String) eventDocument.get("eventPrice");
                                                            int maxEntrants = ((Long) eventDocument.get("eventMaxEntrants")).intValue();
                                                            int waitlistLimit = ((Long) eventDocument.get("eventLimitWaitlist")).intValue();
                                                            Timestamp lastRegDateTimestamp = (Timestamp) eventDocument.get("LastRegDate");
                                                            Timestamp runTimeStartTimestamp = (Timestamp) eventDocument.get("RunTimeStartDate");
                                                            Timestamp runTimeEndTimestamp = (Timestamp) eventDocument.get("RunTimeEndDate");
                                                            Date lastRegDate = lastRegDateTimestamp.toDate();
                                                            Date runTimeStartDate = runTimeStartTimestamp.toDate();
                                                            Date runTimeEndDate = runTimeEndTimestamp.toDate();
                                                            String description = (String) eventDocument.get("description");
                                                            boolean geolocation = (Boolean) eventDocument.get("geolocation");
                                                            String facility = (String) eventDocument.get("facility");

                                                            // Create an Event object using those attributes
                                                            Event currentEvent = new Event(
                                                                    name,
                                                                    "IMAGE PLACEHOLDER",
                                                                    price,
                                                                    maxEntrants,
                                                                    waitlistLimit,
                                                                    lastRegDate,
                                                                    runTimeEndDate,
                                                                    runTimeStartDate,
                                                                    description,
                                                                    geolocation,
                                                                    facility
                                                            );

                                                            // Then add it to the list
                                                            events.add(currentEvent);
                                                            Log.d("Firestore", "added event with info, id: " + currentEvent.getEventID() + " name: " + currentEvent.getEventName());
                                                        } catch (Exception e){
                                                            Log.e("Firestore", "Error processing event: " + eventName, e);
                                                        }
                                                    }
                                                } else {
                                                    Log.e("Firestore", "Failed to retrieve event details for: " + eventName, eventTask.getException());
                                                }

                                                // Track completion for callback
                                                // https://www.geeksforgeeks.org/synchronization-in-java/
                                                synchronized (completedEvents) {
                                                    completedEvents[0]++;
                                                    if (completedEvents[0] == totalEvents){
                                                        // Then we now have completed all tasks! We can callback
                                                        callback.getEvents(events);
                                                    }
                                                }
                                            });
                                }
                            } else {
                                Log.d("Firestore", "No events found for organizer: " + userID);
                                callback.getEvents(events);  // Returns nothing
                            }
                        } else {
                            Log.d("Firestore", "No document found for user: " + userID);
                            callback.getEvents(events);  // Returns nothing
                        }
                    } else {
                        Log.e("Firestore", "Failed to retrieve organizer's document", organizerTask.getException());
                        callback.getEvents(events);  // Returns nothing
                    }
                });
    }

    /**
     * Creates a list of entrant IDs on the waitlist for an event, accessed from firebase.
     *
     * @param eventID
     *      The String ID of the event to check
     * @param status
     *      The string title of the "status" collection to check (i.e. waitlist_pending" to get all the pending entrants)
     * @param callback
     *      Used for waiting for the asynchronous firebase calls, rather than a return statement.
     */
    public void getEventWaitlistEntrants(String eventID, String status, GetEventWaitlistCallback callback) {
        List<String> entrantIDs = new ArrayList<>();

        // pending entrants
        db.collection("events")
                .document(eventID)
                .collection(status)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            // For each document in this collection...
                            Log.d("Firestore.getEventWaitlistEntrants", "waitlist_pending - ID recieved: " + doc.getId());
                            entrantIDs.add(doc.getId());
                            // Rather than just a list of IDs, I want to create an object of type EntrantProfile here
                        }
                        // Pass the result to the callback
                        // This will be changed to return the list of objects of EntrantProfile here
                        callback.getWaitlist(entrantIDs);
                    } else {
                        Log.e("Firestore.getEventWaitlistEntrants", "waitlist_pending: ERROR GETTING NAMES");
                    }
                });
        // Repeat for waitlist_accepted, waitlist_declined, etc. ADD THIS CODE WHEN THOSE EXIST!!!
        // HEY
        // HEY
        // DON'T FORGET
    }
}