package com.example.plannet.ui.Entrant;

public class Entrant {
    private String userId;

    // Empty constructor for Firebase
    public Entrant() {}

    public Entrant(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

