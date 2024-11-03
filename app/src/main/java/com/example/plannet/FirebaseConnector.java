package com.example.plannet;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.SetOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FirebaseConnector {

    FirebaseFirestore db;

    public FirebaseConnector() {
        db = FirebaseFirestore.getInstance(); // lab5
    }

    public void addData(String collectionPath, String eventID, HashMap<String, Object> data,
                        OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        db.collection(collectionPath)
                .document(eventID)
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
    public void addFacilityToDB(String deviceID, String facilityName, String facilityLocation) {
        Map<String, Object> facilityDetails = new HashMap<>();
        facilityDetails.put("name", facilityName);
        //facilityDetails.put("capacity", facilityCapacity); // Uncomment if capacity is needed
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
        // Add the event under the user's events sub-collection with a specific eventID as the document reference
        db.collection("users").document(deviceID).collection("events").document(eventID)
                .set(eventDetails)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseConnector", "Event added with ID: " + eventID);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseConnector", "Error adding event with ID: " + eventID, e);
                });
    }
}
