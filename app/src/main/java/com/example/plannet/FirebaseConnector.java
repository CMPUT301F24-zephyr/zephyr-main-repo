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

    public void getUserEventsByID(String eventID, OnSuccessListener<Map<String, Object>> onSuccess, OnFailureListener onFailure) {
        db.collection("events").document(eventID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d("FirebaseConnector", "Fetched event: " + eventID);
                        onSuccess.onSuccess(documentSnapshot.getData());
                    } else {
                        Log.e("FirebaseConnector", "Event not found: " + eventID);
                        onFailure.onFailure(new Exception("Event not found"));
                    }
                })
                .addOnFailureListener(error -> {
                    Log.e("FirebaseConnector", "Error fetching event: " + eventID, error);
                    onFailure.onFailure(error);
                });
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

    public void getUserEvents(String userID, String filterStatus,
                              OnSuccessListener<List<Map<String, Object>>> onSuccess,
                              OnFailureListener onFailure) {
        // The path to the collection where events are stored
        String collectionPath = "users/" + userID + "/waitlists/" + filterStatus + "/events";

        db.collection(collectionPath)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Map<String, Object>> events = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        events.add(document.getData()); // Add event data to the list
                    }
                    onSuccess.onSuccess(events); // Pass the events back via the callback
                })
                .addOnFailureListener(onFailure); // Handle failures
    }

    public void getJoinedEvents(String userID, OnSuccessListener<List<String>> onSuccess, OnFailureListener onFailure) {
        db.collection("users").document(userID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Fetch events from user's document
                        List<String> joinedEvents = (List<String>) documentSnapshot.get("joinedEvents");
                        if (joinedEvents == null) joinedEvents = new ArrayList<>();
                        onSuccess.onSuccess(joinedEvents);
                    } else {
                        onFailure.onFailure(new Exception("User document not found"));
                    }
                })
                .addOnFailureListener(onFailure);
    }

    public void getSubCollection(String path, OnSuccessListener<List<Map<String, Object>>> onSuccess, OnFailureListener onFailure) {
        db.collection(path)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Map<String, Object>> result = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        result.add(doc.getData());
                    }
                    onSuccess.onSuccess(result);
                })
                .addOnFailureListener(onFailure);
    }



}