package com.example.plannet.Event;

import android.media.Image;

import java.util.Date;

public class Event {
    private String eventName;
    private Image image;
    private int price;
    private int maxEntrants;
    // we can add a condition in MainActivity or another class where if
    // limitWaitlist != 0, we choose that number instead for max waitlist
    private int limitWaitlist = 0; // default value

    private Date eventDate;
    private Date registrationDateDeadline;
    private Date registrationStartDate;
    private String description;
    private boolean geolocation;
    private int facilityID;

    // Constructor
    public Event(String eventName, Image image, int price, int maxEntrants,
                 int limitWaitlist, Date eventDate, Date registrationDateDeadline,
                 Date registrationStartDate, String description, boolean geolocation,
                 int facilityID) {
        this.eventName = eventName;
        this.image = image;
        this.price = price;
        this.maxEntrants = maxEntrants;
        this.limitWaitlist = limitWaitlist;
        this.eventDate = eventDate;
        this.registrationDateDeadline = registrationDateDeadline;
        this.registrationStartDate = registrationStartDate;
        this.description = description;
        this.geolocation = geolocation;
        this.facilityID = facilityID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
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

    public int getFacilityID() {
        return facilityID;
    }

    public void setFacilityID(int facilityID) {
        this.facilityID = facilityID;
    }
}
