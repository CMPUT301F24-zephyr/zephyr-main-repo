package com.example.plannet.ui.entrantprofile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Locale;
import java.util.Map;

public class entrantViewEventFragment extends Fragment {

    private FirebaseConnector firebaseConnector;

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

        // Back arrow functionality
        backArrow.setOnClickListener(v -> requireActivity().onBackPressed());

        // Retrieve data from Bundle
        if (getArguments() != null) {
            String eventId = getArguments().getString("eventID");

            if (eventId != null) {
                // Fetch detailed event information from Firebase
                fetchEventDetails(eventId, titleTextView, posterImageView, facilityNameTextView,
                        facilityAddressTextView, eventDatesTextView, capacityTextView,
                        costTextView, registrationEndsTextView, descriptionTextView);
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
                    // Format timestamps for display
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

                    // Update UI with Firebase data
                    titleTextView.setText("Event: " + eventData.getOrDefault("eventName", "No Event Name"));
                    facilityNameTextView.setText((String) eventData.getOrDefault("facility", "No Facility Name"));
                    facilityAddressTextView.setText("No Address Available"); // Placeholder if address is missing
                    descriptionTextView.setText((String) eventData.getOrDefault("description", "No Description"));
                    capacityTextView.setText("Capacity: " + eventData.getOrDefault("eventMaxEntrants", "N/A"));
                    costTextView.setText("Cost: " + eventData.getOrDefault("eventPrice", "Free"));

                    // Handle timestamps
                    Timestamp startDate = (Timestamp) eventData.get("RunTimeStartDate");
                    Timestamp endDate = (Timestamp) eventData.get("RunTimeEndDate");
                    eventDatesTextView.setText("Event Dates: " +
                            (startDate != null ? dateFormat.format(startDate.toDate()) : "N/A") +
                            " - " +
                            (endDate != null ? dateFormat.format(endDate.toDate()) : "N/A"));

                    Timestamp regEndDate = (Timestamp) eventData.get("LastRegDate");
                    registrationEndsTextView.setText("Registration Ends: " +
                            (regEndDate != null ? dateFormat.format(regEndDate.toDate()) : "N/A"));

                    // Load poster image using Glide
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
                    // Handle errors while fetching data
                    Toast.makeText(requireContext(), "Failed to fetch event details!", Toast.LENGTH_SHORT).show();
                });
    }
}
