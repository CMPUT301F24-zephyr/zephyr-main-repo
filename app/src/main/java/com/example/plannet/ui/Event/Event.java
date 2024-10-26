package com.example.plannet.ui.Event;

public class Event {
    private String eventName;
    private String imageURL;
    private int price;
    private int maxEntrants;
    // we can add a condition in MainActivity or another class where if
    // limitWaitlist != 0, we choose that number instead for max waitlist
    private int limitWaitlist = 0; // default value

    private String description;
    private boolean geolocation;
    private int facilityID;


    Event(String eventName, String imageURL, int price, int maxEntrants,
          int limitWaitlist, String description, boolean geolocation, int facilityID) {

        this.eventName = eventName;
        this.imageURL = imageURL;
        this.price = price;
        this.maxEntrants = maxEntrants;
        this.limitWaitlist = limitWaitlist;
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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
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
