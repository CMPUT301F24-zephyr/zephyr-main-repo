package com.example.plannet;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.plannet.Organizer.Facility;
import com.example.plannet.Organizer.OrganizerProfile;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ProjectUnitTest {

    private final Facility testFacility = new Facility("","");


    /**
     * tests if facility getters/setters work
     */
    @Test
    public void setFacilityTest(){
        String facilityName, facilityLocation;
        facilityName = "rogers place";
        facilityLocation = "downtown";
        testFacility.setFacilityName(facilityName);
        testFacility.setFacilityLocation(facilityLocation);
        assertEquals(testFacility.getFacilityName(), "rogers place");
        assertEquals(testFacility.getFacilityLocation(), "downtown");
    }

    /**
     * tests if organizerprofile userID works
     */
    @Test
    public void getOrgProfileUserIDTest(){
        String facilityName, facilityLocation;
        facilityName = "rogers place";
        facilityLocation = "downtown";
        testFacility.setFacilityName(facilityName);
        testFacility.setFacilityLocation(facilityLocation);
        OrganizerProfile orgprofile = new OrganizerProfile("123", testFacility);
        assertEquals(orgprofile.getUserID(), "123");
    }

    /**
     * tests if organizerprofile facility works
     */
    @Test
    public void getOrgProfileFacilityTest(){
        String facilityName, facilityLocation;
        facilityName = "rogers place";
        facilityLocation = "downtown";
        testFacility.setFacilityName(facilityName);
        testFacility.setFacilityLocation(facilityLocation);
        OrganizerProfile orgprofile = new OrganizerProfile("123", testFacility);
        assertEquals(orgprofile.getFacility(), testFacility);
    }

}