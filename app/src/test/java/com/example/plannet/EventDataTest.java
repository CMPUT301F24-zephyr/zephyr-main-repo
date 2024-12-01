package com.example.plannet;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.plannet.Event.EventData;

public class EventDataTest {
    /**
     * testing the EventData class and its getters/setters
     */
    @Test
    public void testEventDataConstructor() {
        EventData eventData = new EventData("eventid123", "some random event", "very long description", "poster.jpg", "Active", "Location");

        assertEquals("eventid123", eventData.getEventId());
        assertEquals("some random event", eventData.getName());
        assertEquals("very long description", eventData.getEventDescription());
        assertEquals("poster.jpg", eventData.getPosterPath());
        assertEquals("Active", eventData.getStatus());
        assertEquals("Location", eventData.getLocation());
    }

    @Test
    public void testSetters() {
        EventData eventData = new EventData("some random event", "very long description", "poster.jpg", "Active", "Location");
        eventData.setEventName("Updated Event Name");
        assertEquals("Updated Event Name", eventData.getName());

        eventData.setLocation("New Location");
        assertEquals("New Location", eventData.getLocation());
    }
}