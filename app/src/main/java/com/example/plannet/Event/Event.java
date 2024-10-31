package com.example.plannet.Event;

import android.content.Context;
import android.media.Image;
import com.example.plannet.Entrant.EntrantProfile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event {
    private String eventName;
    private Image image;
    private int price;
    private int maxEntrants;
    private int limitWaitlist = 0; // default value

    private Date eventDate;
    private Date registrationDateDeadline;
    private Date registrationStartDate;
    private String description;
    private boolean geolocation;
    private int facilityID;
    private String eventID;
    private EventWaitlistPending eventPending;
    private EventWaitlistAccepted eventAccepted;
    private EventNotificationManager notificationManager; // New Notification Manager

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
        this.eventID = generateEventID();

        this.eventPending = new EventWaitlistPending(this.eventID);
        this.eventAccepted = new EventWaitlistAccepted(new ArrayList<>());
        this.notificationManager = new EventNotificationManager(); // Initialize Notification Manager
    }

    // Method to generate unique Event ID
    public String generateEventID() {
        long timestamp = System.currentTimeMillis();
        String eventIDName = this.eventName.replaceAll(" ", "").toLowerCase();
        return (eventIDName.length() > 10 ? eventIDName.substring(0, 10) : eventIDName) + timestamp;
    }

    // Method to notify all entrants on the waitlist
    public void notifyWaitlist(String message, Context context) {
        List<EntrantProfile> waitlist = eventPending.getWaitlistEntrants();
        notificationManager.sendNotificationToWaitlist(waitlist, message, context);
    }

    // Method to notify all accepted entrants
    public void notifyAcceptedEntrants(String message, Context context) {
        List<EntrantProfile> acceptedList = eventAccepted.getAcceptedEntrants();
        notificationManager.sendNotificationToAccepted(acceptedList, message, context);
    }

    // Getters and setters
    public String getEventID() {
        return eventID;
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

    public void set
