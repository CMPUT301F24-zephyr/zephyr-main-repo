package com.example.plannet.Entrant;

public class EntrantProfile {
    private String userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String profilePictureUrl; // URL to store the profile picture
    private String deviceID;
    private boolean notificationsEnabled; // New field for notification preference

    // Empty constructor for Firebase
    public EntrantProfile() {}

    // Constructor with all fields
    public EntrantProfile(String userId, String name, String email, String phoneNumber,
                          String profilePictureUrl, String deviceID, boolean notificationsEnabled) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePictureUrl = profilePictureUrl;
        this.deviceID = deviceID;
        this.notificationsEnabled = notificationsEnabled;
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

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    // Getter and setter for notificationsEnabled
    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }
}
