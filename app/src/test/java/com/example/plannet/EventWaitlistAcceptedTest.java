package com.example.plannet;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.example.plannet.Entrant.EntrantProfile;
import com.example.plannet.Event.EventWaitlistAccepted;

public class EventWaitlistAcceptedTest {

    private EventWaitlistAccepted acceptedWaitlist;
    private EntrantProfile entrant;

    @Before
    public void setUp() {
        acceptedWaitlist = new EventWaitlistAccepted("eventid123");
        entrant = new EntrantProfile("someuserid", "team", "zephyr", "team@zephyr.com", "780123", "profile.jpg", true, "Accepted");
    }

    @Test
    public void testAddEntrant() {
        assertTrue(acceptedWaitlist.addEntrant(entrant));
    }

    @Test
    public void testRemoveEntrant() {
        acceptedWaitlist.addEntrant(entrant);
        assertTrue(acceptedWaitlist.removeEntrant(entrant));
    }

    @Test
    public void testGetEventID() {
        assertEquals("eventid123", acceptedWaitlist.getEventID());
    }
}
