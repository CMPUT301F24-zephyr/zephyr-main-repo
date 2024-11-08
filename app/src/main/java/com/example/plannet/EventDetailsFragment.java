package com.example.plannet;

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
import androidx.lifecycle.ViewModelProvider;

import com.example.plannet.Entrant.EntrantDBConnector;
import com.example.plannet.Entrant.EntrantProfile;
import com.example.plannet.R;
import com.example.plannet.Event.Event;
import com.example.plannet.ui.orgevents.EventsViewModel;

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

        // Initialize Views
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

        // Get from QRCodeScannerFragment
        Bundle eventBundle = getArguments();
        if (eventBundle != null) {
            title.setText("Event: " + eventBundle.getString("eventName"));
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

        // Setup listeners:
        backArrow.setOnClickListener(v -> requireActivity().onBackPressed());
        registerButton.setOnClickListener(v -> registerForEvent());

        return root;
    }

    private void registerForEvent() {
        if (eventID != null) {
            dbConnector.getUserInfo(userID,
                    userInfo -> {
                        // Create EntrantProfile object with necessary fields
                        String firstName = (String) userInfo.get("firstName");
                        String lastName = (String) userInfo.get("lastName");
                        String email = (String) userInfo.get("email");
                        String phone = (String) userInfo.get("phone");
                        String profilePictureUrl = (String) userInfo.get("profilePictureUrl");
                        boolean notifsActivated = userInfo.get("notifsActivated") != null && (Boolean) userInfo.get("notifsActivated");

                        // Combine first and last name as needed
                        String name = firstName + " " + lastName;

                        // Create the EntrantProfile object using the context for device ID
                        EntrantProfile entrantProfile = new EntrantProfile(
                                requireContext(), // Pass context for deviceID generation
                                userID,
                                name,
                                email,
                                phone,
                                profilePictureUrl,
                                userID, // For deviceID (or generate if different)
                                notifsActivated
                        );

                        dbConnector.addEntrantToWaitlist("events", eventID, entrantProfile,
                                aVoid -> Toast.makeText(getContext(), "Successfully registered for the waitlist", Toast.LENGTH_SHORT).show(),
                                e -> {
                                    Toast.makeText(getContext(), "Failed to register for the waitlist", Toast.LENGTH_SHORT).show();
                                    Log.e("EventDetailsFragment", "Error registering for waitlist", e);
                                });
                    },
                    e -> {
                        Toast.makeText(getContext(), "Error fetching user profile", Toast.LENGTH_SHORT).show();
                        Log.e("EventDetailsFragment", "Failed to fetch user profile", e);
                    });
        } else {
            Toast.makeText(getContext(), "Event ID not found", Toast.LENGTH_SHORT).show();
        }
    }
}