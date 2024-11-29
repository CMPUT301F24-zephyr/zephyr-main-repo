package com.example.plannet.Entrant;
import android.content.Context;
import android.provider.Settings;

import java.io.Serializable;

/**
 *  Class for storing entrant details
 */
public class EntrantProfile implements Serializable {

    private static EntrantProfile instance; // Singleton instance for later use
    private String userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String profilePictureUrl;
    private String deviceID;
    private boolean notifsActivated;
    private EntrantWaitlistPending waitlistPending;
    private EntrantWaitlistAccepted waitlistAccepted;
    private EntrantWaitlistRejected waitlistRejected;

    // For keeping separate first and last name if needed
    private String firstName;
    private String lastName;

    // For organizer's... organization (simple fix!)
    private String waitlistStatus;

    // Empty constructor for Firebase
    public EntrantProfile() {}

    /**
     * Constructor for creating a new EntrantProfile object.
     *
     * @param context            Context passed for toast
     * @param userId             userId of user
     * @param name               entrant name
     * @param email              entrant email
     * @param phoneNumber        entrant phone number
     * @param profilePictureUrl  entrant profile picture url
     * @param notifsActivated    notifications activation status
     */
    private EntrantProfile(Context context, String userId, String name, String email, String phoneNumber, String profilePictureUrl, boolean notifsActivated) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePictureUrl = profilePictureUrl;
        this.deviceID = getAndroidID(context);
        this.notifsActivated = notifsActivated;
        this.waitlistPending = new EntrantWaitlistPending();
        this.waitlistAccepted = new EntrantWaitlistAccepted();
        this.waitlistRejected = new EntrantWaitlistRejected();
    }

    public EntrantProfile(String userId, String firstName, String lastName, String email, String phoneNumber, String profilePictureUrl, boolean notifsActivated, String waitlistStatus) {
        // This constructor has firstName and lastName rather than just Name, and ignores deviceID. Used by organizers.
        // Additionally does not care about waitlist status, as it is just used to store info about an entrant for organizer viewing
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePictureUrl = profilePictureUrl;
        this.notifsActivated = notifsActivated;
        this.name = firstName + " " + lastName;
        this.waitlistStatus = waitlistStatus;  // e.g., "Pending"
    }

    /**
     * Creates a new singleton instance of EntrantProfile.
     *
     * @param context            Context passed for toast
     * @param userId             userId of user
     * @param name               entrant name
     * @param email              entrant email
     * @param phoneNumber        entrant phone number
     * @param profilePictureUrl  entrant profile picture url
     * @param notifsActivated    notifications activation status
     * @return                   The singleton instance of EntrantProfile
     */
    public static EntrantProfile getInstance(Context context, String userId, String name, String email, String phoneNumber, String profilePictureUrl, boolean notifsActivated) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        if (instance == null) {
            instance = new EntrantProfile(context, userId, name, email, phoneNumber, profilePictureUrl, notifsActivated);
        }
        return instance;
    }

    /**
     * Gets the singleton instance if it has already been initialized.
     *
     * @return The singleton instance of EntrantProfile.
     */
    public static EntrantProfile getInstance() {
        if (instance == null) {
            throw new IllegalStateException("EntrantProfile is not initialized. Call getInstance with parameters first.");
        }
        return instance;
    }

    /**
     * Clears the instance for cases like user logout.
     */
    public static void clearInstance() {
        instance = null;
    }

    public EntrantWaitlistPending getWaitlistPending() {
        return waitlistPending;
    }

    public EntrantWaitlistAccepted getWaitlistAccepted() {
        return waitlistAccepted;
    }

    public EntrantWaitlistRejected getWaitlistRejected() {
        return waitlistRejected;
    }

    private String getAndroidID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public boolean isNotifsActivated() {
        return notifsActivated;
    }

    public void setNotifsActivated(boolean notifsActivated) {
        this.notifsActivated = notifsActivated;
    }

    public String getWaitlistStatus() {
        return waitlistStatus;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }
}