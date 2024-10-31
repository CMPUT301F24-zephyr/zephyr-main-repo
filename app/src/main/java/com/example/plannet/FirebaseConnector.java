package com.example.plannet;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;

public class FirebaseConnector {
    private FirebaseFirestore db;

    public FirebaseConnector() {
        this.db = FirebaseFirestore.getInstance();
    }

    // Method to add data to Firebase Firestore with a HashMap
    public void addData(String collectionPath, String documentId, String message,
                        OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("message", message);  // Storing the message in the "message" field

        db.collection(collectionPath)
                .document(documentId)
                .set(data)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }
}
