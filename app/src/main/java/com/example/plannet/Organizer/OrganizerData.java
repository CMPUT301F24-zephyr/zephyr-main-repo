package com.example.plannet.Organizer;

import java.util.ArrayList;

public class OrganizerData {
    private String userID;
    private static String facility;
    private ArrayList<String> qrCodeHashes;

    // Constructor
    public OrganizerData(String userID, String facility, String qrCodeHash) {
        this.userID = userID;
        this.facility = facility;
        this.qrCodeHashes = new ArrayList<>();
    }

    // Getter/Setter for userID
    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }

    // Getter/Setter for facility
    public static String getFacility() {
        return facility;
    }
    public static void setFacility(String facility) {
        OrganizerData.facility = facility;
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

}
