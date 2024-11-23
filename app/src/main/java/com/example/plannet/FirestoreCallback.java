package com.example.plannet;

/**
 * interface to retrieve data from firestore and store them in a String array
 */
public interface FirestoreCallback {
    void onSuccess(String[] userIDs);
    void onFailure(Exception e);
}
