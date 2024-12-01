package com.example.plannet;

import android.util.Log;
import android.widget.Toast;

import androidx.core.util.Consumer;

import com.example.plannet.Event.Event;
import com.example.plannet.Entrant.EntrantProfile;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Date;

/**
 * Firebase connector class for interacting with the Firebase Realtime Database.
 */
public class FirebaseConnector {

    private FirebaseFirestore db;

    public FirebaseConnector() {
        db = FirebaseFirestore.getInstance(); // lab5
    }

    /**
     * check if device is already on DB
     * @param collectionName
     * @param deviceID
     * @param callback
     */
    public void checkIfDeviceIDinDB(String collectionName, String deviceID, CheckDeviceIDCallback callback) {
        db.collection(collectionName)
                .document(deviceID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        callback.onDeviceIDFound();
                    } else {
                        callback.onDeviceIDNotFound();
                    }
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * add data from a given collectionPath & documentID
     * @param collectionPath
     * @param documentID
     * @param data
     * @param onSuccess
     * @param onFailure
     */
    public void addData(String collectionPath, String documentID, HashMap<String, Object> data,
                        OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        db.collection(collectionPath)
                .document(documentID)
                .set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    /**
     * Deletes data from a specified collection in the database.
     * @param collectionPath
     * @param documentID
     * @param onSuccess
     * @param onFailure
     */
    public void deleteData(String collectionPath, String documentID,
                           OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        db.collection(collectionPath)
                .document(documentID)
                .delete()
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

    /**
     * add event information to DB
     * @param deviceID
     * @param eventID
     * @param eventDetails
     */
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

    /**
     * Adds a user to the Firestore database
     * @param uniqueID
     */
    public void addUserToFirestore(String uniqueID) {
        Map<String, Object> user = new HashMap<>();
        user.put("UUID", uniqueID);

        db.collection("users").document(uniqueID)
                .set(user)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User added to Firestore"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error saving user", e));
    }

    /**
     * Adds user information to the Firestore database
     * @param uniqueID
     * @param userInfo
     * @param onSuccessListener
     * @param onFailureListener
     */
    public void addUserInfoToFirestore(String uniqueID, Map<String, Object> userInfo,
                                       OnSuccessListener<Void> onSuccessListener,
                                       OnFailureListener onFailureListener) {
        db.collection("users").document(uniqueID)
                .collection("userInfo").document("profile")
                .set(userInfo, SetOptions.merge())
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    /**
     * Checks if the facility data is valid for a user
     * @param userID
     * @param onSuccess
     * @param onFailure
     */
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

    /**
     * Returns user information from the Firestore database
     * @param userID
     * @param onSuccess
     * @param onFailure
     */
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

    /**
     * upload user location to DB
     */
    public void updateUserLocation(String uniqueID, Map<String, Object> userInfo,
                                       OnSuccessListener<Void> onSuccessListener,
                                       OnFailureListener onFailureListener) {
        db.collection("users").document(uniqueID)
                .collection("userInfo").document("profile")
                .set(userInfo, SetOptions.merge())
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    /**
     * fetch user location
     */
    public void fetchUserLocation(String uniqueID,
                                  OnSuccessListener<Map<String, Object>> onSuccessListener,
                                  OnFailureListener onFailureListener) {
        db.collection("users").document(uniqueID)
                .collection("userInfo").document("profile")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> data = documentSnapshot.getData();
                        if (data != null && data.containsKey("latitude") && data.containsKey("longitude")) {
                            onSuccessListener.onSuccess(data);
                        } else {
                            onFailureListener.onFailure(
                                    new Exception("Latitude or longitude field is missing")
                            );
                        }
                    } else {
                        onFailureListener.onFailure(
                                new Exception("Document does not exist")
                        );
                    }
                })
                .addOnFailureListener(onFailureListener);
    }

    /**
     * Gets event information from the Firestore database
     * @param eventID
     * @param onSuccess
     * @param onFailure
     */
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


    /**
     * get all organizer events using their userID
     * Resource for callback: https://www.baeldung.com/java-callback-functions
     * @param userID
     * @param callback
     */
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
                                                                    eventName,
                                                                    name,
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
     * Returns user events
     * @param userID
     * @param filterStatus
     * @param onSuccess
     * @param onFailure
     */
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

    /**
     * get a user's joined events list from DB
     * @param userID
     * @param onSuccess
     * @param onFailure
     */
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

    /**
     * returns subcollection of a collection
     * @param path
     * @param onSuccess
     * @param onFailure
     */
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
        List<EntrantProfile> entrants = new ArrayList<>();
        Log.d("Firestore WaitlistEntrants", "Getting users from waitlist: waitlist_" + status + " For event with ID: " + eventID);

        // pending entrants
        db.collection("events")
                .document(eventID)
                .collection("waitlist_" + status)  // i.e. "pending"
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firestore WaitlistEntrants", "Successfully found collection. Number of documents in collection: " + task.getResult().size());
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            // For each document in this collection...
                            String userID = doc.getId();
                            String email = doc.getString("email");
                            String phone = doc.getString("phone");
                            boolean notifs = doc.getBoolean("notificationsEnabled");
                            String firstName = doc.getString("firstName");
                            String lastName = doc.getString("lastName");
                            String profilePic = doc.getString("profilePictureUrl");

                            // Handle potential null values for latitude and longitude
                            Double latitudeObj = doc.getDouble("entrantlatitude");
                            Double longitudeObj = doc.getDouble("entrantlongitude");

                            double latitude = latitudeObj != null ? latitudeObj : 0.0;
                            double longitude = longitudeObj != null ? longitudeObj : 0.0;
                            Log.d("Firestore WaitlistEntrants", "waitlist_" + status + " - user received with ID: " + userID);

                            // Now we create the object:
                            EntrantProfile entrant = new EntrantProfile(userID, firstName, lastName, email, phone, profilePic, notifs, status);
                            entrants.add(entrant);
                        }

                        // Pass the result to the callback
                        // This will be changed to return the list of objects of EntrantProfile here
                        callback.getWaitlist(entrants);
                    } else {
                        Log.e("Firestore WaitlistEntrants", "waitlist_pending: ERROR GETTING NAMES");
                    }
                });
    }

    /**
     * method to get all the userIDs in a waitlist
     * @param eventID
     * @param waitlist
     * @param callback
     */
    public void RetrieveUserIDsFromEvent(String eventID, String waitlist, FirestoreCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // ref document "events -> eventID -> waitlist" collection
        CollectionReference waitlistRef = db.collection("events")
                .document(eventID)
                .collection(waitlist);

        waitlistRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<String> userIDs = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    userIDs.add(document.getId());
                }

                callback.onSuccess(userIDs.toArray(new String[0]));
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    /**
     * Retrieves a picture from firebase image storage with a specified ID.
     *
     * @param type
     *      "poster" or "profile", to specify if searching for a poster or a profile picture
     * @param id
     *      The ID of the picture in firebase. This is the userID for a profile picture and the eventID for a poster.
     * @param onSuccessListener
     *      Success listener as firebase is asynchronous
     * @param onFailureListener
     *      Failure listener as firebase is asynchronous
     */
    public void getPicture(String type, String id, OnSuccessListener<String> onSuccessListener, OnFailureListener onFailureListener) {
        // Get the firebase storage and reference. Keep in this method since it is only used for pictures
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        if (type.equals("poster")){
            StorageReference path = storageRef.child(id);  // For posters, event.getImage() is the path to the image.
            path.getDownloadUrl().addOnSuccessListener(uri -> {
                Log.d("Firestore", "Poster found for event with poster path: " + id);
                onSuccessListener.onSuccess(uri.toString());
            }).addOnFailureListener(onFailureListener);
        }
        else if (type.equals("profile")){
            StorageReference path = storageRef.child("profile_pictures/" + id + ".jpg");
            path.getDownloadUrl().addOnSuccessListener(uri -> {
                Log.d("Firestore", "Profile picture found for user with id: " + id);
                onSuccessListener.onSuccess(uri.toString());
            }).addOnFailureListener(onFailureListener);
        }
    }

    /**
     * Adds data to a specified waitlist subcollection in the database.
     * @param eventID
     * @param userID
     * @param entrantData
     * @param waitlistType
     * @param onSuccess
     * @param onFailure
     */
    public void addDataToWaitlist(String eventID, String userID, Map<String, Object> entrantData, String waitlistType,
                                  OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        String path = "events/" + eventID + "/waitlist_" + waitlistType;
        addData(path, userID, new HashMap<>(entrantData), onSuccess, onFailure);
    }

    /**
     * Removes data from a specified waitlist subcollection in the database.
     * @param eventID
     * @param userID
     * @param waitlistType
     * @param onSuccess
     * @param onFailure
     */
    public void removeDataFromWaitlist(String eventID, String userID, String waitlistType,
                                       OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        String path = "events/" + eventID + "/waitlist_" + waitlistType;
        deleteData(path, userID, onSuccess, onFailure);
    }

    /**
     * Adds data to a specified waitlist subcollection in the database under the user collection
     * @param userID
     * @param waitlistType
     * @param eventID
     * @param eventData
     * @param onSuccess
     * @param onFailure
     */
    public void updateUserWaitlist(String userID, String waitlistType, String eventID, Map<String, Object> eventData,
                                   OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        // Correct the path to reference a valid collection
        String path = "users/" + userID + "/waitlists/" + waitlistType + "/events";
        addData(path, eventID, new HashMap<>(eventData), onSuccess, onFailure);
    }

    /**
     * Removes data from a specified waitlist subcollection in the database under the user collection
     * @param userID
     * @param waitlistType
     * @param eventID
     * @param onSuccess
     * @param onFailure
     */
    public void removeUserWaitlistEvent(String userID, String waitlistType, String eventID,
                                        OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        String path = "users/" + userID + "/waitlists/" + waitlistType + "/events";
        deleteData(path, eventID, onSuccess, onFailure);
    }

    /**
     * Copied code from entrantViewEventFragment for moving entrants between waitlists when lottery is run.
     * Also used for moving entrants if they are cancelled by an organizer
     *
     * @param eventId
     *      Id of the event
     * @param userId
     *      ID of the user
     * @param oldStatus
     *      The original status of the user waitlist (i.e. "pending")
     * @param newStatus
     *      The new status of the user waitlist (i.e. "chosen")
     */
    public void moveToWaitlist(String eventId, String userId, String oldStatus, String newStatus) {
        getUserEventsByID(eventId,
                eventData -> {

                    String eventName = (String) eventData.getOrDefault("eventName", "N/A");
                    String facilityName = (String) eventData.getOrDefault("facility", "N/A");
                    String eventDates = "Event Dates: " +
                            (eventData.get("RunTimeStartDate") != null ?
                                    new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(((Timestamp) eventData.get("RunTimeStartDate")).toDate()) : "N/A")
                            + " - " +
                            (eventData.get("RunTimeEndDate") != null ?
                                    new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(((Timestamp) eventData.get("RunTimeEndDate")).toDate()) : "N/A");
                    String cost = (String) eventData.getOrDefault("eventPrice", "Free");

                    getUserInfo(userId, userInfo -> {
                        if (userInfo == null) {
                            Log.d("FirebaseConnector moveToWaitlist", "moveToWaitlist User information not found.");
                            return;
                        }

                        Map<String, Object> declinedEntrantData = new HashMap<>();
                        declinedEntrantData.put("firstName", userInfo.getOrDefault("firstName", "N/A"));
                        declinedEntrantData.put("lastName", userInfo.getOrDefault("lastName", "N/A"));
                        declinedEntrantData.put("email", userInfo.getOrDefault("email", "N/A"));
                        declinedEntrantData.put("phone", userInfo.getOrDefault("phone", "N/A"));
                        declinedEntrantData.put("profilePictureUrl", userInfo.getOrDefault("profilePictureUrl", null));
                        declinedEntrantData.put("notificationsEnabled", userInfo.getOrDefault("notifsActivated", false));
                        declinedEntrantData.put("entrantlatitude", userInfo.getOrDefault("latitude", null));
                        declinedEntrantData.put("entrantlongitude", userInfo.getOrDefault("longitude", null));

                        //remove the entrant from the old waitlist in events
                        removeDataFromWaitlist(eventId, userId, oldStatus,
                                success -> {
                                    Log.d("unregisterForEvent", "Successfully removed user from event's "+ oldStatus +" waitlist.");

                                    //remove the old waitlist entry from the user's collection
                                    removeUserWaitlistEvent(userId, oldStatus, eventId,
                                            removed -> {
                                                Log.d("FirebaseConnector moveToWaitlist", "Successfully removed "+ oldStatus +" waitlist from user's collection.");

                                                //add entrant to the "declined" waitlist in events
                                                addDataToWaitlist(eventId, userId, declinedEntrantData, newStatus,
                                                        added -> {
                                                            Log.d("FirebaseConnector moveToWaitlist", "Successfully added user to event's "+ newStatus +"  waitlist.");

                                                            //update user's waitlist entry for "declined"
                                                            Map<String, Object> declinedWaitlistData = new HashMap<>();
                                                            declinedWaitlistData.put("eventID", eventId);
                                                            declinedWaitlistData.put("status", "declined");
                                                            declinedWaitlistData.put("timestamp", System.currentTimeMillis());
                                                            declinedWaitlistData.put("eventName", eventName);
                                                            declinedWaitlistData.put("facilityName", facilityName);
                                                            declinedWaitlistData.put("eventDates", eventDates);
                                                            declinedWaitlistData.put("cost", cost);

                                                            updateUserWaitlist(userId, newStatus, eventId, declinedWaitlistData,
                                                                    updated -> {
                                                                // Removed code for local storing new status as we only need remote.
                                                                    },
                                                                    error -> Log.e("waitlist change", "Failed to change waitlist.", error));
                                                        },
                                                        error -> Log.e("FirebaseConnector moveToWaitlist", "Failed to change waitlist.", error));
                                            },
                                            error -> Log.e("FirebaseConnector moveToWaitlist", "Failed to remove "+oldStatus+" waitlist from user's collection.", error));
                                },
                                error -> Log.e("FirebaseConnector moveToWaitlist", "Failed to remove user from event's "+oldStatus+" waitlist.", error));
                    }, error -> Log.e("FirebaseConnector moveToWaitlist", "Error fetching user profile", error));
                },
                error -> {
                    Log.e("unregisterForEvent", "Error fetching event details", error);
                });
    }
    /**
     * Updates the status of an invite.
     *
     * @param userID       The ID of the user (document in Firestore).
     * @param inviteID     The ID of the invite to update.
     * @param newStatus    The new status of the invite (e.g., "accepted" or "declined").
     * @param onSuccess    Callback for successful operation.
     * @param onFailure    Callback for failure.
     */
    public void updateInviteStatus(String userID, String inviteID, String newStatus,
                                   Runnable onSuccess, Consumer<Exception> onFailure) {
        if (inviteID == null || inviteID.isEmpty()) {
            Log.e("FirebaseConnector", "Invalid document path: inviteId is null or empty.");
            return;
        }
        db.collection("notifications")
                .document(userID)
                .collection("invites")
                .document(inviteID)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseConnector", "Invite status updated to: " + newStatus);

                    // Step 2: Delete the invite after the status is updated
                    db.collection("notifications")
                            .document(userID)
                            .collection("invites")
                            .document(inviteID)
                            .delete()
                            .addOnSuccessListener(deleteVoid -> {
                                Log.d("FirebaseConnector", "Invite deleted successfully.");
                                onSuccess.run();
                            })
                            .addOnFailureListener(deleteError -> {
                                Log.e("FirebaseConnector", "Failed to delete invite.", deleteError);
                                onFailure.accept(deleteError);
                            });
                })
                .addOnFailureListener(error -> {
                    Log.e("FirebaseConnector", "Failed to update invite status.", error);
                    onFailure.accept(error);
                });
    }
}