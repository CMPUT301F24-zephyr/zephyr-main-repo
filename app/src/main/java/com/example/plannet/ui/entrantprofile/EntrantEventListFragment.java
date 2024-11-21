package com.example.plannet.ui.entrantprofile;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.plannet.Entrant.EntrantProfile;
import com.example.plannet.Event.EventData;
import com.example.plannet.FirebaseConnector;
import com.example.plannet.R;

import java.util.ArrayList;
import java.util.List;

public class EntrantEventListFragment extends Fragment {
    private ListView eventListView;
    private EventListAdapter eventListAdapter;
    private List<EventData> eventDataList;

    private String currentFilter = "Pending"; // Default filter

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.entrant_event_list, container, false);

        eventListView = root.findViewById(R.id.event_item_container);
        eventDataList = new ArrayList<>();
        eventListAdapter = new EventListAdapter(requireContext(), eventDataList);
        eventListView.setAdapter(eventListAdapter);

        fetchEvents();

        return root;
    }

    private void fetchEvents() {
        FirebaseConnector firebaseConnector = new FirebaseConnector();
        EntrantProfile profile = EntrantProfile.getInstance();

        if (profile == null) {
            Log.e("EntrantEventListFragment", "EntrantProfile is null.");
            return;
        }

        // Fetch event IDs from the waitlist
        List<String> eventIDs = profile.getWaitlistPending().getWaitlist(); // Example: Pending waitlist
        if (eventIDs.isEmpty()) {
            Toast.makeText(getContext(), "No events found.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Clear the current event data list
        eventDataList.clear();

        // Fetch each event by its ID
        for (String eventID : eventIDs) {
            firebaseConnector.getUserEventsByID(eventID, event -> {
                String eventName = (String) event.get("eventName");
                String eventLocation = (String) event.get("facility");
                String eventStatus = currentFilter;

                // Add event data to the list
                eventDataList.add(new EventData(eventName, eventStatus, eventLocation));
                eventListAdapter.notifyDataSetChanged(); // Refresh the ListView
            }, error -> {
                Log.e("EntrantEventListFragment", "Error fetching event details for ID: " + eventID, error);
            });
        }
    }

}
