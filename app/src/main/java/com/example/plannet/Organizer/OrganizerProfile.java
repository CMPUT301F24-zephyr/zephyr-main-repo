package com.example.plannet.Organizer;

import android.provider.Settings;

import java.util.ArrayList;

public class OrganizerProfile {
    private String userID;
    private Facility facility;
    private ArrayList<String> qrCodeHashes;

    // Constructor
    // example for calling this class:
    //    Facility facility = new Facility("Example Facility", "Example Location");
    //    OrganizerProfile profile = new OrganizerProfile("userID123", facility);
    public OrganizerProfile(String userID, Facility facility) {
        this.userID = userID;
        this.facility = facility;
        this.qrCodeHashes = new ArrayList<>();
    }

    // Getter for userID
    public String getUserID() {
        return userID;
    }

    // Getter/Setter for facility
    public Facility getFacility() {
        return facility;
    }
    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    // Getter for qrCodeHashes
    public ArrayList<String> getQrCodeHashes() {
        return qrCodeHashes;
    }
    // Add a QR code hash to the list
    public void addQrCodeHash(String qrCodeHash) {
        this.qrCodeHashes.add(qrCodeHash);
    }

    // Remove a QR code hash from the list
    public void removeQrCodeHash(String qrCodeHash) {
        this.qrCodeHashes.remove(qrCodeHash);
    }

    // Additional methods to access facility details directly
    public String getFacilityName() {
        return facility.getFacilityName();
    }

    public String getLocation() {
        return facility.getFacilityLocation();
    }
}
