package com.example.plannet.Organizer;

import android.media.Image;

import com.example.plannet.Event.EventWaitlistPending;

import java.util.Date;

public class Facility {
    private String facilityName;
    private String facilityLocation;

    // Constructor
    public Facility(String facilityName, String facilityLocation) {
        this.facilityName = facilityName;
        this.facilityLocation = facilityLocation;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getFacilityLocation() {
        return facilityLocation;
    }

    public void setFacilityLocation(String facilityLocation) {
        this.facilityLocation = facilityLocation;
    }
}
