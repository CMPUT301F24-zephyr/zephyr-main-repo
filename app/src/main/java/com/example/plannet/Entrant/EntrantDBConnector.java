package com.example.plannet.Entrant;

import com.example.plannet.Event.EventWaitlistPending;
import com.example.plannet.FirebaseConnector;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;


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
     * Adds entrant to waitlist on firebase
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
    public void saveUserInfo(String userID, String firstName, String lastName, String phone, String email, String profilePictureUrl,
                             OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        // Prepare the data to save
        Map<String, Object> userData = new HashMap<>();
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);
        userData.put("phone", phone);
        userData.put("email", email);
        userData.put("profilePictureUrl", profilePictureUrl);

        fireCon.addUserInfoToFirestore(userID, userData, onSuccessListener, onFailureListener);
    }

    /**
     * retrieves the user information from DB
     * @param userID
     * @param onSuccess
     * @param onFailure
     */
    public void getUserInfo(String userID, OnSuccessListener<Map<String, Object>> onSuccess, OnFailureListener onFailure) {
        fireCon.getUserInfo(userID, onSuccess, onFailure);
    }

    /**
     * Updates waitlist in the Firestore database.
     * @param userID
     * @param waitlistType
     * @param eventID
     * @param eventData
     * @param onSuccess
     * @param onFailure
     */
    public void updateWaitlist(String userID, String waitlistType, String eventID, HashMap<String, Object> eventData,
                               OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        // The path to the "events" sub-collection within the specified waitlist type
        String collectionPath = "users/" + userID + "/waitlists/" + waitlistType + "/events";

        // Add the eventID as a document within the "events" sub-collection
        fireCon.addData(collectionPath, eventID, eventData, onSuccess, onFailure);
    }
}