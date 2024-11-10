package com.example.plannet.Entrant;
import android.content.Context;
import android.provider.Settings;

/**
 *  Class for storing entrant details
 */
public class EntrantProfile {
    private String userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String profilePictureUrl; // URL to store the profile picture
    private String deviceID;
    private boolean notifsActivated;

    //Empty constructor for Firebase
    public EntrantProfile() {}

    /**
     *
     * Constructor for creating a new EntrantProfile object.
     *
     * @param context
     *      Context passed for toast
     * @param userId
     *      userId of user
     * @param name
     *      entrant name
     * @param email
     *      entrant email
     * @param phoneNumber
     *      entrant phone number
     * @param profilePictureUrl
     *      entrant profile pricture url
     * @param deviceID
     *      attribute to store device ID of entrant
     * @param notifsActivated
     */
    public EntrantProfile(Context context, String userId, String name, String email, String phoneNumber, String profilePictureUrl, String deviceID, boolean notifsActivated) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePictureUrl = profilePictureUrl;
        this.deviceID = getAndroidID(context);
        this.notifsActivated = notifsActivated;
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
}
