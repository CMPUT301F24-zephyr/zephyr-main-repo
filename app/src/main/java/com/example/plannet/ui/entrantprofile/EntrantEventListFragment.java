package com.example.plannet.ui.entrantprofile;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntrantEventListFragment extends Fragment {
    private ListView eventListView;
    private EventListAdapter eventListAdapter;
    private List<EventData> eventDataList;
    private ImageView backArrow;
    private String currentFilter = "Pending"; // Default filter

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.entrant_event_list, container, false);

        eventListView = root.findViewById(R.id.event_item_container);
        eventDataList = new ArrayList<>();
        eventListAdapter = new EventListAdapter(requireContext(), eventDataList);
        eventListView.setAdapter(eventListAdapter);
        backArrow = root.findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> requireActivity().onBackPressed());


        fetchEvents();

        return root;
    }

    private void fetchEvents() {
        FirebaseConnector firebaseConnector = new FirebaseConnector();
        String userID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        String collectionPath = "users/" + userID + "/waitlists/pending/events";

        firebaseConnector.getSubCollection(collectionPath,
                pendingEvents -> {
                    if (pendingEvents.isEmpty()) {
                        Toast.makeText(getContext(), "No events found in the pending waitlist.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    eventDataList.clear();

                    for (Map<String, Object> event : pendingEvents) {
                        String eventName = (String) event.get("eventName");
                        String eventLocation = (String) event.get("facilityName");
                        String eventStatus = currentFilter;

                        eventDataList.add(new EventData(eventName, eventStatus, eventLocation));
                    }

                    eventListAdapter.notifyDataSetChanged();
                },
                error -> {
                    Toast.makeText(getContext(), "Failed to fetch pending events.", Toast.LENGTH_SHORT).show();
                    Log.e("EntrantEventListFragment", "Error fetching pending waitlist", error);
                });
    }


}
