package com.example.plannet;

/**
 * interface to retrieve data from firestore with onSuccess/onFailure as response
 */
public interface FirestoreCallback {
    void onSuccess(String[] userIDs);
    void onFailure(Exception e);
}
