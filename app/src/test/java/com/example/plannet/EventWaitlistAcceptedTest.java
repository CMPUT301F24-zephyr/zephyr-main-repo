package com.example.plannet;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.example.plannet.Entrant.EntrantProfile;
import com.example.plannet.Event.EventWaitlistAccepted;

public class EventWaitlistAcceptedTest {

    /**
     * testing waitlistaccepted class
     */
    private EventWaitlistAccepted acceptedWaitlist;
    private EntrantProfile entrant;

    /**
     * creating a mock class
     */
    @Before
    public void setUp() {
        acceptedWaitlist = new EventWaitlistAccepted("eventid123");
        entrant = new EntrantProfile("someuserid", "team", "zephyr", "team@zephyr.com", "780123", "profile.jpg", true, "Accepted");
    }

    /**
     * simulate adding an entrant
     */
    @Test
    public void testAddEntrant() {
        assertTrue(acceptedWaitlist.addEntrant(entrant));
    }

    /**
     * simulate removing an entrant
     */
    @Test
    public void testRemoveEntrant() {
        acceptedWaitlist.addEntrant(entrant);
        assertTrue(acceptedWaitlist.removeEntrant(entrant));
    }

    /**
     * get event getter testing
     */
    @Test
    public void testGetEventID() {
        assertEquals("eventid123", acceptedWaitlist.getEventID());
    }
}
