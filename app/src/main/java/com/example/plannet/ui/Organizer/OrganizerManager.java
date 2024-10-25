package com.example.plannet.ui.Organizer;

import android.util.Log;

import com.example.plannet.ui.Admin.AdminProfileList;

public class OrganizerManager {
    // Collaborators (other classes)
    private OrganizerData organizerData;
    private OrganizerCreatedEvents organizerCreatedEvents;
    private OrganizerDBConnector organizerDBConnector;
    private AdminProfileList adminProfileList;

    // Constructor
    public OrganizerManager(OrganizerData organizerData, OrganizerCreatedEvents organizerCreatedEvents,
                            OrganizerDBConnector organizerDBConnector, AdminProfileList adminProfileList) {
        this.organizerData = organizerData;
        this.organizerCreatedEvents = organizerCreatedEvents;
        this.organizerDBConnector = organizerDBConnector;
        this.adminProfileList = adminProfileList;
    }

    // Send organizer information to OrganizerDBConnector
    public void sendOrganizerInfoToDB(String organizerId, String name, String contactInfo) {
        // Use OrganizerDBConnector to add the organizer to the Firebase database
        organizerDBConnector.addOrganizer(organizerId, name, contactInfo,
                aVoid -> Log.d("OrganizerManager", "Organizer info sent to DB successfully"),
                e -> Log.e("OrganizerManager", "Error adding event", e));
    }

    // Add an organizer to AdminProfileList
    public void addOrganizerToAdminProfileList(String organizerId) {
        // Assume AdminProfileList has a method to add an organizer by ID
        adminProfileList.addOrganizer(organizerId);
        Log.d("OrganizerManager", "Organizer added to Admin Profile List");
    }

}
