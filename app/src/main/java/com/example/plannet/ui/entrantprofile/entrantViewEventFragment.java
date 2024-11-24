package com.example.plannet.ui.entrantprofile;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.plannet.FirebaseConnector;
import com.example.plannet.R;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *  The fragment for event view details when user clicks on an event in their
 *  list
 */


public class entrantViewEventFragment extends Fragment {

    private FirebaseConnector firebaseConnector;
    private String currentStatus = null; // Store the user's current status in the event

    private TextView titleTextView, facilityNameTextView, eventDatesTextView, costTextView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.entrant_view_event, container, false);

        // Initialize FirebaseConnector
        firebaseConnector = new FirebaseConnector();

        // UI Elements
        TextView titleTextView = root.findViewById(R.id.title);
        ImageView posterImageView = root.findViewById(R.id.poster);
        TextView facilityNameTextView = root.findViewById(R.id.facility_name);
        TextView facilityAddressTextView = root.findViewById(R.id.facility_address);
        TextView eventDatesTextView = root.findViewById(R.id.event_dates);
        TextView capacityTextView = root.findViewById(R.id.capacity);
        TextView costTextView = root.findViewById(R.id.cost);
        TextView registrationEndsTextView = root.findViewById(R.id.end_date);
        TextView descriptionTextView = root.findViewById(R.id.description_writing);
        ImageView backArrow = root.findViewById(R.id.back_arrow);
        Button actionButton = root.findViewById(R.id.entrants_button);




        // Back arrow functionality
        backArrow.setOnClickListener(v -> requireActivity().onBackPressed());

        if (getArguments() != null) {
            String eventId = getArguments().getString("eventID");
            String userId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            currentStatus = getArguments().getString("eventStatus"); // Use the passed status if available

            if (eventId != null) {
                // Fetch detailed event information from Firebase
                fetchEventDetails(eventId, titleTextView, posterImageView, facilityNameTextView,
                        facilityAddressTextView, eventDatesTextView, capacityTextView,
                        costTextView, registrationEndsTextView, descriptionTextView);


                updateActionButton(actionButton, eventId, userId);
            } else {
                Toast.makeText(requireContext(), "Event ID is missing!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "No event data received!", Toast.LENGTH_SHORT).show();
        }


        return root;
    }

    /**
     * Fetches detailed event information from Firebase and updates the UI.
     */
    private void fetchEventDetails(String eventId, TextView titleTextView, ImageView posterImageView,
                                   TextView facilityNameTextView, TextView facilityAddressTextView,
                                   TextView eventDatesTextView, TextView capacityTextView,
                                   TextView costTextView, TextView registrationEndsTextView,
                                   TextView descriptionTextView) {

        firebaseConnector.getUserEventsByID(eventId,
                eventData -> {

                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());


                    titleTextView.setText("Event: " + eventData.getOrDefault("eventName", "No Event Name"));
                    facilityNameTextView.setText((String) eventData.getOrDefault("facility", "No Facility Name"));
                    facilityAddressTextView.setText("No Address Available"); // Placeholder if address is missing
                    descriptionTextView.setText((String) eventData.getOrDefault("description", "No Description"));
                    capacityTextView.setText("Capacity: " + eventData.getOrDefault("eventMaxEntrants", "N/A"));
                    costTextView.setText("Cost: " + eventData.getOrDefault("eventPrice", "Free"));


                    Timestamp startDate = (Timestamp) eventData.get("RunTimeStartDate");
                    Timestamp endDate = (Timestamp) eventData.get("RunTimeEndDate");
                    eventDatesTextView.setText("Event Dates: " +
                            (startDate != null ? dateFormat.format(startDate.toDate()) : "N/A") +
                            " - " +
                            (endDate != null ? dateFormat.format(endDate.toDate()) : "N/A"));

                    Timestamp regEndDate = (Timestamp) eventData.get("LastRegDate");
                    registrationEndsTextView.setText("Registration Ends: " +
                            (regEndDate != null ? dateFormat.format(regEndDate.toDate()) : "N/A"));


                    String posterPath = (String) eventData.get("eventPoster");
                    if (posterPath != null && !posterPath.isEmpty()) {
                        Glide.with(this)
                                .load(posterPath)
                                .placeholder(R.drawable.no_poster)
                                .into(posterImageView);
                    } else {
                        posterImageView.setImageResource(R.drawable.no_poster);
                    }
                },
                error -> {

                    Toast.makeText(requireContext(), "Failed to fetch event details!", Toast.LENGTH_SHORT).show();
                });
    }


    private void updateActionButton(Button actionButton, String eventId, String userId) {
        if ("pending".equals(currentStatus) || "chosen".equals(currentStatus) || "accepted".equals(currentStatus)) {
            actionButton.setText("Unregister");
            actionButton.setOnClickListener(v -> unregisterForEvent(eventId, userId, actionButton));
        } else {
            actionButton.setText("Register");
            actionButton.setOnClickListener(v -> registerForEvent(eventId, userId, titleTextView, facilityNameTextView, eventDatesTextView, costTextView));
        }
    }


    private void registerForEvent(String eventId, String userId, TextView titleTextView, TextView facilityNameTextView, TextView eventDatesTextView, TextView costTextView) {
        if (eventId == null) {
            Toast.makeText(getContext(), "Event ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseConnector.getUserInfo(userId, userInfo -> {
            if (userInfo == null) {
                Toast.makeText(getContext(), "User information not found.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Prepare entrant data for both event and user waitlists
            HashMap<String, Object> entrantData = new HashMap<>();
            entrantData.put("firstName", userInfo.get("firstName"));
            entrantData.put("lastName", userInfo.get("lastName"));
            entrantData.put("email", userInfo.get("email"));
            entrantData.put("phone", userInfo.get("phone"));
            entrantData.put("profilePictureUrl", userInfo.get("profilePictureUrl"));
            entrantData.put("notificationsEnabled", userInfo.get("notifsActivated") != null && (Boolean) userInfo.get("notifsActivated"));
            entrantData.put("entrantlatitude", userInfo.getOrDefault("latitude", "N/A"));
            entrantData.put("entrantlongitude", userInfo.getOrDefault("longitude", "N/A"));

            firebaseConnector.addDataToWaitlist(eventId, userId, entrantData, "pending",
                    success -> {
                        // Add event details to the user's pending waitlist
                        HashMap<String, Object> eventData = new HashMap<>();
                        eventData.put("eventID", eventId);
                        eventData.put("eventName", titleTextView.getText().toString());
                        eventData.put("facilityName", facilityNameTextView.getText().toString());
                        eventData.put("eventDates", eventDatesTextView.getText().toString());
                        eventData.put("cost", costTextView.getText().toString());
                        eventData.put("registrationDate", System.currentTimeMillis());

                        firebaseConnector.updateUserWaitlist(userId, "pending", eventId, eventData,
                                waitlistSuccess -> {
                                    Toast.makeText(getContext(), "Successfully added to pending waitlist (both user and event).", Toast.LENGTH_SHORT).show();
                                },
                                waitlistFailure -> {
                                    Toast.makeText(getContext(), "Failed to update user's pending waitlist.", Toast.LENGTH_SHORT).show();
                                    Log.e("entrantViewEventFragment", "Error updating user's pending waitlist", waitlistFailure);
                                });
                    },
                    eventWaitlistFailure -> {
                        Toast.makeText(getContext(), "Failed to register for event's pending waitlist.", Toast.LENGTH_SHORT).show();
                        Log.e("entrantViewEventFragment", "Error registering for event's pending waitlist", eventWaitlistFailure);
                    });
        }, userInfoFailure -> {
            Toast.makeText(getContext(), "Error fetching user profile", Toast.LENGTH_SHORT).show();
            Log.e("entrantViewEventFragment", "Failed to fetch user profile", userInfoFailure);
        });
    }




    private void unregisterForEvent(String eventId, String userId, Button actionButton) {
        if (eventId == null || userId == null) {
            Toast.makeText(getContext(), "Missing event or user ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseConnector.getUserEventsByID(eventId,
                eventData -> {

                    String eventName = (String) eventData.getOrDefault("eventName", "N/A");
                    String facilityName = (String) eventData.getOrDefault("facility", "N/A");
                    String eventDates = "Event Dates: " +
                            (eventData.get("RunTimeStartDate") != null ?
                                    new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(((Timestamp) eventData.get("RunTimeStartDate")).toDate()) : "N/A")
                            + " - " +
                            (eventData.get("RunTimeEndDate") != null ?
                                    new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(((Timestamp) eventData.get("RunTimeEndDate")).toDate()) : "N/A");
                    String cost = (String) eventData.getOrDefault("eventPrice", "Free");

                    firebaseConnector.getUserInfo(userId, userInfo -> {
                        if (userInfo == null) {
                            Toast.makeText(getContext(), "User information not found.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Map<String, Object> declinedEntrantData = new HashMap<>();
                        declinedEntrantData.put("firstName", userInfo.getOrDefault("firstName", "N/A"));
                        declinedEntrantData.put("lastName", userInfo.getOrDefault("lastName", "N/A"));
                        declinedEntrantData.put("email", userInfo.getOrDefault("email", "N/A"));
                        declinedEntrantData.put("phone", userInfo.getOrDefault("phone", "N/A"));
                        declinedEntrantData.put("profilePictureUrl", userInfo.getOrDefault("profilePictureUrl", null));
                        declinedEntrantData.put("notificationsEnabled", userInfo.getOrDefault("notifsActivated", false));
                        declinedEntrantData.put("entrantlatitude", userInfo.getOrDefault("latitude", "N/A"));
                        declinedEntrantData.put("entrantlongitude", userInfo.getOrDefault("longitude", "N/A"));

                        //remove the entrant from the "pending" waitlist in events
                        firebaseConnector.removeDataFromWaitlist(eventId, userId, "pending",
                                success -> {
                                    Log.d("unregisterForEvent", "Successfully removed user from event's pending waitlist.");

                                    //remove the pending waitlist entry from the user's collection
                                    firebaseConnector.removeUserWaitlistEvent(userId, "pending", eventId,
                                            removed -> {
                                                Log.d("unregisterForEvent", "Successfully removed pending waitlist from user's collection.");

                                                //add entrant to the "declined" waitlist in events
                                                firebaseConnector.addDataToWaitlist(eventId, userId, declinedEntrantData, "declined",
                                                        added -> {
                                                            Log.d("unregisterForEvent", "Successfully added user to event's declined waitlist.");

                                                            //update user's waitlist entry for "declined"
                                                            Map<String, Object> declinedWaitlistData = new HashMap<>();
                                                            declinedWaitlistData.put("eventID", eventId);
                                                            declinedWaitlistData.put("status", "declined");
                                                            declinedWaitlistData.put("timestamp", System.currentTimeMillis());
                                                            declinedWaitlistData.put("eventName", eventName);
                                                            declinedWaitlistData.put("facilityName", facilityName);
                                                            declinedWaitlistData.put("eventDates", eventDates);
                                                            declinedWaitlistData.put("cost", cost);

                                                            firebaseConnector.updateUserWaitlist(userId, "declined", eventId, declinedWaitlistData,
                                                                    updated -> {
                                                                        Log.d("unregisterForEvent", "Successfully updated user's declined waitlist.");
                                                                        currentStatus = "declined";
                                                                        Toast.makeText(getContext(), "Unregistered successfully.", Toast.LENGTH_SHORT).show();
                                                                        updateActionButton(actionButton, eventId, userId);
                                                                    },
                                                                    error -> Log.e("unregisterForEvent", "Failed to update user's declined waitlist.", error));
                                                        },
                                                        error -> Log.e("unregisterForEvent", "Failed to add user to event's declined waitlist.", error));
                                            },
                                            error -> Log.e("unregisterForEvent", "Failed to remove pending waitlist from user's collection.", error));
                                },
                                error -> Log.e("unregisterForEvent", "Failed to remove user from event's pending waitlist.", error));
                    }, error -> Log.e("unregisterForEvent", "Error fetching user profile", error));
                },
                error -> {
                    Toast.makeText(getContext(), "Failed to fetch event details for unregistration.", Toast.LENGTH_SHORT).show();
                    Log.e("unregisterForEvent", "Error fetching event details", error);
                });
    }
}
