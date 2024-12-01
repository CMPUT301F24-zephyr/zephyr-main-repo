package com.example.plannet;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

import com.example.plannet.Event.Event;

public class EventTest {
    /**
     * testing for Event class and its attributes
     */
    private Event event;
    private Date eventDate;
    private Date regStartDate;
    private Date regEndDate;

    /**
     * creating a mock event
     */
    @Before
    public void setUp() {
        eventDate = new Date();
        regStartDate = new Date(System.currentTimeMillis());
        regEndDate = new Date(System.currentTimeMillis() + 10);
        event = new Event("Mock Event", "100", 50, 0, eventDate, regEndDate, regStartDate, "very cool description", true, "Rogers Place");
    }

    /**
     * testing event constructor
     */
    @Test
    public void testEventConstructor() {
        assertEquals("Mock Event", event.getEventName());
        assertEquals("100", event.getPrice());
        assertEquals(50, event.getMaxEntrants());
        assertEquals(0, event.getLimitWaitlist());
        assertEquals(eventDate, event.getEventDate());
        assertTrue(event.isGeolocation());
        assertEquals("Rogers Place", event.getFacility());
    }

    /**
     * generate an event ID and check if it starts with its name with no space/lowercase
     */
    @Test
    public void testGenerateEventID() {
        String eventID = event.generateEventID();
        assertNotNull(eventID);
        assertTrue(eventID.startsWith("testevent"));
    }

    /**
     * testing some setters and getters
     */
    @Test
    public void testSettersAndGetters() {
        event.setEventName("New Event Name");
        assertEquals("New Event Name", event.getEventName());

        event.setPrice("35");
        assertEquals("35", event.getPrice());

        event.setMaxEntrants(1000);
        assertEquals(1000, event.getMaxEntrants());

        event.setLimitWaitlist(50);
        assertEquals(50, event.getLimitWaitlist());
    }
}
