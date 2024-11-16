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

import java.util.HashMap;
import java.util.Map;

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
                    // I've constructed a hash map of the user's info here instead of passing the object for the usere
                    //coz we don't really need to store the object on Firebase

                    //this portion of the code only runs after Firestore returns the data
                    HashMap<String, Object> entrantData = new HashMap<>();
                    entrantData.put("name", userInfo.get("firstName") + " " + userInfo.get("lastName"));
                    entrantData.put("email", userInfo.get("email"));
                    entrantData.put("phone", userInfo.get("phone"));
                    entrantData.put("profilePictureUrl", userInfo.get("profilePictureUrl"));
                    entrantData.put("notificationsEnabled", userInfo.get("notifsActivated") != null && (Boolean) userInfo.get("notifsActivated"));

                    // after firestore returns the data, we call addEntrantToWaitlist to add the entrant to the waitlist
                    //the register button only works for pending waitlist
                    dbConnector.addEntrantToWaitlist(eventID, userID, entrantData, "pending",
                            aVoid -> Toast.makeText(getContext(), "Successfully registered for the pending waitlist", Toast.LENGTH_SHORT).show(),
                            e -> {
                                Toast.makeText(getContext(), "Failed to register for the waitlist", Toast.LENGTH_SHORT).show();
                                Log.e("EventDetailsFragment", "Error registering for waitlist", e);
                            });
                },
                e -> {
                    Toast.makeText(getContext(), "Error fetching user profile", Toast.LENGTH_SHORT).show();
                    Log.e("EventDetailsFragment", "Failed to fetch user profile", e);
                });
    }


}