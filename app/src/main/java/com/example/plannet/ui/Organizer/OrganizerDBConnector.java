package com.example.plannet.ui.Organizer;

import com.example.plannet.ui.Firebase.FirebaseConnector;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrganizerDBConnector {
    // Collaborator(s)
    private FirebaseConnector firebaseConnector;

    // Constructor
    public OrganizerDBConnector(FirebaseConnector firebaseConnector) {
        this.firebaseConnector = firebaseConnector;
    }

    // Add an organizer
    public void addOrganizer(String userID, String facilityName, String location, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        Map<String, Object> organizerData = new HashMap<>();
        organizerData.put("name", facilityName);
        organizerData.put("location", location);
        organizerData.put("events", new ArrayList<>()); // empty list for events initially

        firebaseConnector.addData("organizers", userID, organizerData, onSuccess, onFailure);
    }

    // Delete an organizer
    public void deleteOrganizer(String organizerId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        firebaseConnector.deleteData("organizers", organizerId, onSuccess, onFailure);
    }

    // Update organizer info
    public void updateOrganizerInfo(String organizerId, String name, String contactInfo, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        Map<String, Object> updatedInfo = new HashMap<>();
        updatedInfo.put("name", name);
        updatedInfo.put("contactInfo", contactInfo);

        firebaseConnector.updateData("organizers", organizerId, updatedInfo, onSuccess, onFailure);
    }

    // Update organizer event list
    public void updateOrganizerEventList(String organizerId, List<String> eventList, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        Map<String, Object> eventUpdate = new HashMap<>();
        eventUpdate.put("events", eventList);

        firebaseConnector.updateData("organizers", organizerId, eventUpdate, onSuccess, onFailure);
    }

    // Listen for real-time changes in organizers
    public ListenerRegistration listenToOrganizers(EventListener<QuerySnapshot> listener) {
        return firebaseConnector.listenToCollection("organizers", listener);
    }
}
