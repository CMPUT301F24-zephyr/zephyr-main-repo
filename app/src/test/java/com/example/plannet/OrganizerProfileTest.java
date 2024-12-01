package com.example.plannet;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.example.plannet.Event.Event;
import com.example.plannet.Organizer.Facility;
import com.example.plannet.Organizer.OrganizerProfile;

import java.util.Date;

public class OrganizerProfileTest {
    /**
     * testing organizerprofile class
     */
    private OrganizerProfile organizerProfile;
    private Facility facility;
    private Event event;

    /**
     * mock organizer profile
     */
    @Before
    public void setUp() {
        facility = new Facility("Rogers Place", "Downtown");
        organizerProfile = new OrganizerProfile("someuserid", facility);
        event = new Event("Random Event", "50", 100, 20, new Date(), new Date(), new Date(), "very long description", true, "Rogers Place");
    }

    /**
     * add event to object profile
     */
    @Test
    public void testAddEvent() {
        organizerProfile.addEvent(event);
        assertTrue(organizerProfile.getEvents().contains(event));
    }

    /**
     * remove event from object profile
     */
    @Test
    public void testRemoveEvent() {
        organizerProfile.addEvent(event);
        organizerProfile.removeEvent(event);
        assertFalse(organizerProfile.getEvents().contains(event));
    }

    /**
     * testing getter for addQrCodeHash
     */
    @Test
    public void testAddQrCodeHash() {
        organizerProfile.addQrCodeHash("eventid123");
        assertTrue(organizerProfile.getQrCodeHashes().contains("eventid123"));
    }

    /**
     * testing getter/setter for addQrCodeHash
     */
    @Test
    public void testRemoveQrCodeHash() {
        organizerProfile.addQrCodeHash("eventid123");
        organizerProfile.removeQrCodeHash("eventid123");
        assertFalse(organizerProfile.getQrCodeHashes().contains("eventid123"));
    }

    /**
     * testing setter getter for facility
     */
    @Test
    public void testFacilityDetails() {
        assertEquals("Rogers Place", organizerProfile.getFacilityName());
        assertEquals("Downtown", organizerProfile.getLocation());
    }
}
