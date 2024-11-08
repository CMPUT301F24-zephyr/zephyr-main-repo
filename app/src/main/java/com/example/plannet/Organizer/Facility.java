package com.example.plannet.Organizer;

import android.media.Image;

import com.example.plannet.Event.EventWaitlistPending;

import java.util.Date;

/**
 * Stores the info of an organizer's facility, including the name and location as Strings.
 */
public class Facility {
    private String facilityName;
    private String facilityLocation;

    /**
     * Constructor. Creates a facility with provided information.
     *
     * @param facilityName
     *      (String) The name of the facility.
     * @param facilityLocation
     *      (String) The location of the facility.
     */
    public Facility(String facilityName, String facilityLocation) {
        this.facilityName = facilityName;
        this.facilityLocation = facilityLocation;
    }

    // Getters and setters
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
