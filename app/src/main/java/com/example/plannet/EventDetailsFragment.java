package com.example.plannet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.plannet.R;
import com.example.plannet.Event.Event;
import com.example.plannet.ui.orgevents.EventsViewModel;

public class EventDetailsFragment extends Fragment {

    private TextView title, facilityName, facilityAddress, eventDates, capacity, cost, end_date, descriptionWriting;
    private Button registerButton;
    private ImageView backArrow, poster;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.entrant_view_event, container, false);

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

        // Setup listeners:
        backArrow.setOnClickListener(v -> requireActivity().onBackPressed());

        return root;
    }
}
