package com.example.plannet;

import java.util.List;

/**
 * Interface used for callbacks when getting entrants from the database for an event.
 */
public interface GetEventWaitlistCallback {
    void getWaitlist(List<String> entrants);
}