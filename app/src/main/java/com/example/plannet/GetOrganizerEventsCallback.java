package com.example.plannet;

import com.example.plannet.Event.Event;
import java.util.ArrayList;

/**
 * Interface used for callbacks when getting events in the database for an organizer.
 */
public interface GetOrganizerEventsCallback {
    void getEvents(ArrayList<Event> events);
}