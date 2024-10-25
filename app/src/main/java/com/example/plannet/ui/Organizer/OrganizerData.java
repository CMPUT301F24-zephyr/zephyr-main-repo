package com.example.plannet.ui.Organizer;

public class OrganizerData {
    private String userID;
    private String facility;
    private String qrCodeHash;

    // Constructor
    public OrganizerData(String userID, String facility, String qrCodeHash) {
        this.userID = userID;
        this.facility = facility;
        this.qrCodeHash = qrCodeHash;
    }

    // Getter/Setter for userID
    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }

    // Getter/Setter for facility
    public String getFacility() {
        return facility;
    }
    public void setFacility(String facility) {
        this.facility = facility;
    }

    // Getter/Setter for qrCodeHash
    public String getQrCodeHash() {
        return qrCodeHash;
    }
    public void setQrCodeHash(String qrCodeHash) {
        this.qrCodeHash = qrCodeHash;
    }
}
