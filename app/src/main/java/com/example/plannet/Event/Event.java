package com.example.plannet.Event;

import android.media.Image;

import java.io.Serializable;
import java.util.Date;

/**
 * Stores event attributes such as name, image, price, maximum entrants,
 * waitlist limit, dates, description, geolocation status, and facility information.
 */
public class Event implements Serializable {
    private String eventName;
    private String image;  // SET TO STRING FOR TESTING PURPOSES! PLEASE FIX WHEN WE GET IMAGES WORKING
    private String price;
    private int maxEntrants;
    // we can add a condition in MainActivity or another class where if
    // limitWaitlist != 0, we choose that number instead for max waitlist
    private int limitWaitlist = 0; // default value

    private Date eventDate;
    private Date registrationDateDeadline;
    private Date registrationStartDate;
    private String description;
    private boolean geolocation;
    private String facility;
    private String eventID;
    private EventWaitlistPending eventPending;

    public Event() {}

    /**
     * Constructor. Creates an event and fills all the attributes.
     * NOTE for part 4 polishing: add default values for optional attributes
     *
     * @param eventName
     *      The name of the event.
     * @param price
     *      The price for the event as a String (optional).
     * @param maxEntrants
     *      The maximum number of entrants allowed (optional).
     * @param limitWaitlist
     *      The limit for waitlist entries (optional).
     * @param eventDate
     *      The date the event will happen.
     * @param registrationDateDeadline
     *      The registration deadline.
     * @param registrationStartDate
     *      The start date for registration.
     * @param description
     *      A description of the event (String).
     * @param geolocation
     *      True if geolocation is required, false if not.
     * @param facility
     *      The facility where the event is.
     */
    public Event(String eventName, String price, int maxEntrants,
                 int limitWaitlist, Date eventDate, Date registrationDateDeadline,
                 Date registrationStartDate, String description, boolean geolocation,
                 String facility) {
        this.eventName = eventName;
        this.price = price;
        this.maxEntrants = maxEntrants;
        this.limitWaitlist = limitWaitlist;
        this.eventDate = eventDate;
        this.registrationDateDeadline = registrationDateDeadline;
        this.registrationStartDate = registrationStartDate;
        this.description = description;
        this.geolocation = geolocation;
        this.facility = facility;
        this.eventID = generateEventID();
        this.image = "posters/" + eventID + ".jpg";

        this.eventPending = new EventWaitlistPending(this.eventID);
    }

    // 2nd constructor for creating local object with an ID that is already on firebase.
    public Event(String eventID, String eventName, String price, int maxEntrants,
                 int limitWaitlist, Date eventDate, Date registrationDateDeadline,
                 Date registrationStartDate, String description, boolean geolocation,
                 String facility) {
        this.eventName = eventName;
        this.image = "posters/" + eventID + ".jpg";
        this.price = price;
        this.maxEntrants = maxEntrants;
        this.limitWaitlist = limitWaitlist;
        this.eventDate = eventDate;
        this.registrationDateDeadline = registrationDateDeadline;
        this.registrationStartDate = registrationStartDate;
        this.description = description;
        this.geolocation = geolocation;
        this.facility = facility;
        this.eventID = eventID;
    }

    /**
     * Called whenever an event is created. Generates a unique event ID by combining the event
     * name and the current time.
     *
     * @return
     *      The generated unique event ID as a String.
     */
    public String generateEventID() {
        long timestamp = System.currentTimeMillis();   //https://currentmillis.com/tutorials/system-currentTimeMillis.html
        String EventIDName = this.eventName.replaceAll(" ", "").toLowerCase();
        String FinalEventID = (EventIDName.length() > 10 ? EventIDName.substring(0, 10) : EventIDName) + timestamp;
        return FinalEventID;
    }

    public String getEventID() {
        return eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getMaxEntrants() {
        return maxEntrants;
    }

    public void setMaxEntrants(int maxEntrants) {
        this.maxEntrants = maxEntrants;
    }

    public int getLimitWaitlist() {
        return limitWaitlist;
    }

    public void setLimitWaitlist(int limitWaitlist) {
        this.limitWaitlist = limitWaitlist;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Date getRegistrationDateDeadline() {
        return registrationDateDeadline;
    }

    public void setRegistrationDateDeadline(Date registrationDateDeadline) {
        this.registrationDateDeadline = registrationDateDeadline;
    }

    public Date getRegistrationStartDate() {
        return registrationStartDate;
    }

    public void setRegistrationStartDate(Date registrationStartDate) {
        this.registrationStartDate = registrationStartDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isGeolocation() {
        return geolocation;
    }

    public void setGeolocation(boolean geolocation) {
        this.geolocation = geolocation;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

}


