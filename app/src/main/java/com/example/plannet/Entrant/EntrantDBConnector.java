package com.example.plannet.Entrant;

import com.example.plannet.Event.EventWaitlistPending;
import com.example.plannet.FirebaseConnector;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles connections to the Firestore database for entrant data, such as waitlist status and user info.
 */
public class EntrantDBConnector {
    private FirebaseConnector fireCon;

    /**
     * Constructor. Initializes a new FirebaseConnector class object.
     */
    public EntrantDBConnector() {
        fireCon = new FirebaseConnector();
    }

    /**
     *
     * @param eventID
     * @param entrantID
     * @param entrantData
     * @param onSuccess
     * @param onFailure
     */
    public void addEntrantToWaitlist(String eventID, String entrantID, HashMap<String, Object> entrantData,
                                     String waitlistType, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        String collectionPath = "events/" + eventID + "/waitlist_" + waitlistType; // Dynamically choose sub-collection
        fireCon.addData(collectionPath, entrantID, entrantData, onSuccess, onFailure);
    }



    /**
     * Remove an entrant from a specified waitlist in the Firestore database.
     *
     * @param collectionPath
     *      The path of the collection in Firestore where waitlist is stored.
     * @param documentID
     *      The string ID of the document to be removed.
     * @param onSuccess
     *      Triggered if the operation is successful.
     * @param onFailure
     *      Triggered if the operation is unsuccessful.
     */
    public void removeEntrantFromWaitlist(String collectionPath, String documentID,
                                          OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        fireCon.deleteData(collectionPath, documentID, onSuccess, onFailure);
    }

    /**
     * Saves user information in the Firestore Database.
     *
     * @param userID
     *      The unique ID of the user.
     * @param firstName
     *      The first name of the user.
     * @param lastName
     *      The last name of the user.
     * @param phone
     *      The phone number of the user.
     * @param email
     *      The email address of the user.
     * @param onSuccessListener
     *      Triggered if the operation is successful.
     * @param onFailureListener
     *      Triggered if the operation is unsuccessful.
     */
    public void saveUserInfo(String userID, String firstName, String lastName, String phone, String email,
                             OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("firstName", firstName);
        userInfo.put("lastName", lastName);
        userInfo.put("phone", phone);
        userInfo.put("email", email);
        fireCon.addUserInfoToFirestore(userID, userInfo, onSuccessListener, onFailureListener);
    }

    public void getUserInfo(String userID, OnSuccessListener<Map<String, Object>> onSuccess, OnFailureListener onFailure) {
        fireCon.getUserInfo(userID, onSuccess, onFailure);
    }

    public void updateWaitlist(String userID, String waitlistType, String eventID, HashMap<String, Object> eventData,
                               OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        // The path to the "events" sub-collection within the specified waitlist type
        String collectionPath = "users/" + userID + "/waitlists/" + waitlistType + "/events";

        // Add the eventID as a document within the "events" sub-collection
        fireCon.addData(collectionPath, eventID, eventData, onSuccess, onFailure);
    }



    public void getPendingWaitlist(String userID, OnSuccessListener<List<Map<String, Object>>> onSuccess, OnFailureListener onFailure) {
        String path = "users/" + userID + "/waitlists/pending/events";
        fireCon.getSubCollection(path, onSuccess, onFailure);
    }



}