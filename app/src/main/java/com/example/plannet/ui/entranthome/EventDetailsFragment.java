package com.example.plannet.ui.entranthome;

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
import androidx.fragment.app.Fragment;

import com.example.plannet.Entrant.EntrantDBConnector;
import com.example.plannet.Entrant.EntrantProfile;
import com.example.plannet.R;

import java.util.HashMap;

public class EventDetailsFragment extends Fragment {

    private TextView title, facilityName, facilityAddress, eventDates, capacity, cost, end_date, descriptionWriting;
    private Button registerButton;
    private ImageView backArrow, poster;
    private EntrantDBConnector dbConnector;
    private String eventID;
    private String userID;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.entrant_view_event, container, false);
        dbConnector = new EntrantDBConnector();


        title = root.findViewById(R.id.title);
        facilityName = root.findViewById(R.id.facility_name);
        facilityAddress = root.findViewById(R.id.facility_address);
        eventDates = root.findViewById(R.id.event_dates);
        capacity = root.findViewById(R.id.capacity);
        cost = root.findViewById(R.id.cost);
        end_date = root.findViewById(R.id.end_date);
        descriptionWriting = root.findViewById(R.id.description_writing);
        registerButton = root.findViewById(R.id.entrants_button);
        backArrow = root.findViewById(R.id.back_arrow);
        poster = root.findViewById(R.id.poster);


        Bundle eventBundle = getArguments();
        if (eventBundle != null) {
            title.setText(eventBundle.getString("eventName"));
            facilityName.setText(eventBundle.getString("facility"));
            facilityAddress.setText(eventBundle.getString("address"));
            end_date.setText(eventBundle.getString("registrationDateDeadline"));
            eventDates.setText("Event Dates: " + eventBundle.getString("registrationStartDate") + " to " + eventBundle.getString("eventDate"));
            capacity.setText("Capacity: " + eventBundle.getInt("maxEntrants"));
            cost.setText("Cost: " + eventBundle.getString("price"));
            end_date.setText(eventBundle.getString("registrationDateDeadline"));
            descriptionWriting.setText(eventBundle.getString("description"));
        }
        if (getArguments() != null) {
            eventID = getArguments().getString("eventID");
        }

        userID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);


        backArrow.setOnClickListener(v -> requireActivity().onBackPressed());
        registerButton.setOnClickListener(v -> registerForEvent());

        return root;
    }

    private void registerForEvent() {
        if (eventID == null) {
            Toast.makeText(getContext(), "Event ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        dbConnector.getUserInfo(userID,
                userInfo -> {
                    // Prepare entrant data for both event and user waitlists
                    HashMap<String, Object> entrantData = new HashMap<>();
                    entrantData.put("firstName", userInfo.get("firstName"));
                    entrantData.put("lastName",userInfo.get("lastName"));
                    entrantData.put("email", userInfo.get("email"));
                    entrantData.put("phone", userInfo.get("phone"));
                    entrantData.put("profilePictureUrl", userInfo.get("profilePictureUrl"));
                    entrantData.put("notificationsEnabled", userInfo.get("notifsActivated") != null && (Boolean) userInfo.get("notifsActivated"));

                    dbConnector.addEntrantToWaitlist(eventID, userID, entrantData, "pending",
                            aVoid -> {
                                // Add event details to the user's pending waitlist
                                HashMap<String, Object> eventData = new HashMap<>();
                                eventData.put("eventID", eventID);
                                eventData.put("eventName", title.getText().toString());
                                eventData.put("facilityName", facilityName.getText().toString());
                                eventData.put("eventDates", eventDates.getText().toString());
                                eventData.put("cost", cost.getText().toString());
                                eventData.put("registrationDate", System.currentTimeMillis());

                                dbConnector.updateWaitlist(userID, "pending", eventID, eventData,
                                        waitlistSuccess -> {
                                            Toast.makeText(getContext(), "Successfully added to pending waitlist (both user and event).", Toast.LENGTH_SHORT).show();
                                        },
                                        waitlistFailure -> {
                                            Toast.makeText(getContext(), "Failed to update user's pending waitlist.", Toast.LENGTH_SHORT).show();
                                            Log.e("EventDetailsFragment", "Error updating user's pending waitlist", waitlistFailure);
                                        });
                            },
                            eventWaitlistFailure -> {
                                Toast.makeText(getContext(), "Failed to register for event's pending waitlist.", Toast.LENGTH_SHORT).show();
                                Log.e("EventDetailsFragment", "Error registering for event's pending waitlist", eventWaitlistFailure);
                            });
                },
                userInfoFailure -> {
                    Toast.makeText(getContext(), "Error fetching user profile", Toast.LENGTH_SHORT).show();
                    Log.e("EventDetailsFragment", "Failed to fetch user profile", userInfoFailure);
                });
    }

}