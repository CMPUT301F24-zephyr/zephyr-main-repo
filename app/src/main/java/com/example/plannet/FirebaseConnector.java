package com.example.plannet;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.SetOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
}