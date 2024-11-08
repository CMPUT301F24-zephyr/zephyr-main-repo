package com.example.plannet.Organizer;
import com.example.plannet.Event.Event;
import java.util.ArrayList;
import java.util.List;
public class OrganizerCreatedEvents {
    private String organizationName;
    private ArrayList<Event> events;
    // Constructor
    public OrganizerCreatedEvents() {
        this.organizationName = organizationName;
        this.events = new ArrayList<>();
    }
    public String getOrganizationName() {
        return organizationName;
    }
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
    public void addEvent(Event event) {
        events.add(event);
    }
    public void removeEvent(Event event) {
        events.remove(event);
    }
    public ArrayList<Event> getEvents() {
        return events;
    }
}