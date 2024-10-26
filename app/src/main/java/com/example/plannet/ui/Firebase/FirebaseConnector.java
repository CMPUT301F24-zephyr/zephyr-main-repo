package com.example.plannet.ui.Firebase;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FirebaseConnector {
    // Connect to firebase
    // Navigate firebase database using tags provided by DBConnector classes
    private FirebaseFirestore db;
    private CollectionReference eventRef; // link for collection we are going to use //


    public FirebaseConnector() {
        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();
    }

    // Add any type of data to Firestore (generalized)
    public void addData(String collection, String document, Map<String, Object> data, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        db.collection(collection)
                .document(document)
                .set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }
    // Update any type of data in Firestore
    public void updateData(String collection, String document, Map<String, Object> updates, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        db.collection(collection)
                .document(document)
                .update(updates)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    // Getter for Data
    public void getData(String collection, OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
        db.collection(collection)
                .get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }
    // Delete any type of data in Firestore
    public void deleteData(String collection, String document, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        db.collection(collection)
                .document(document)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    // Listen for real-time updates on a collection
    public ListenerRegistration listenToCollection(String collection, EventListener<QuerySnapshot> listener) {
        eventRef = db.collection(collection);
        return eventRef.addSnapshotListener(listener);
    }

}