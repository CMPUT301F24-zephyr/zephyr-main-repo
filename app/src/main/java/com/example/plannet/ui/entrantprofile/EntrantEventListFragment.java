package com.example.plannet.ui.entrantprofile;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.plannet.Event.EventData;
import com.example.plannet.FirebaseConnector;
import com.example.plannet.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Fragment for displaying a list of events for entrants
 */
public class EntrantEventListFragment extends Fragment {
    private ListView eventListView;
    private EventListAdapter eventListAdapter;
    private List<EventData> eventDataList;
    private ImageView backArrow;
    private Set<String> activeFilters; // To keep track of selected filters

    /**
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.entrant_event_list, container, false);

        eventListView = root.findViewById(R.id.event_item_container);
        eventDataList = new ArrayList<>();
        eventListAdapter = new EventListAdapter(requireContext(), eventDataList);
        eventListView.setAdapter(eventListAdapter);
        activeFilters = new HashSet<>(); // Start with no active filters
        activeFilters.add("pending");

        backArrow = root.findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> requireActivity().onBackPressed());
        eventListView.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < eventDataList.size()) {
                EventData selectedEvent = eventDataList.get(position);

                if (selectedEvent != null) {
                    Log.d("EntrantEventListFragment", "Selected Event: " + selectedEvent);

                    Bundle bundle = new Bundle();
                    bundle.putString("eventID", selectedEvent.getEventId());
                    bundle.putString("eventName", selectedEvent.getName());
                    bundle.putString("eventDescription", selectedEvent.getEventDescription());
                    bundle.putString("posterPath", selectedEvent.getPosterPath());
                    bundle.putString("eventStatus", selectedEvent.getStatus());
                    bundle.putString("eventLocation", selectedEvent.getLocation());
                    bundle.putString("eventStatus", selectedEvent.getStatus());

                    Log.d("EntrantEventListFragment", "Bundle Content: " + bundle.toString());

                    NavController navController = Navigation.findNavController(view);
                    navController.navigate(R.id.action_eventList_to_viewEventFragment, bundle);
                } else {
                    Log.e("EntrantEventListFragment", "Selected Event is null!");
                }
            } else {
                Log.e("EntrantEventListFragment", "Invalid position clicked: " + position);
            }
        });


        fetchEvents();

        Button pendingButton = root.findViewById(R.id.filter_pending);
        Button acceptedButton = root.findViewById(R.id.filter_accepted);
        Button chosenButton = root.findViewById(R.id.filter_chosen);
        Button declinedButton = root.findViewById(R.id.filter_declined);


        // Set click listeners for the buttons with specific colors
        pendingButton.setOnClickListener(v -> toggleFilter("pending"));
        acceptedButton.setOnClickListener(v -> toggleFilter("accepted"));
        chosenButton.setOnClickListener(v -> toggleFilter("chosen"));
        declinedButton.setOnClickListener(v -> toggleFilter("declined"));

        return root;
    }

    /**
     * Toggles a filter on or off and updates the list of events accordingly.
     *
     * @param filter The filter to toggle (e.g., "pending").
     */
    private void toggleFilter(String filter) {
        if (activeFilters.contains(filter)) {
            activeFilters.remove(filter);
        } else {
            activeFilters.add(filter);
        }

        refreshButtonStates();

        fetchEvents();
    }

    /**
     * Refreshes the colors of all filter buttons based on the current active filters.
     */
    private void refreshButtonStates() {
        Button pendingButton = getView().findViewById(R.id.filter_pending);
        if (activeFilters.contains("pending")) {
            pendingButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
            pendingButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        } else {
            pendingButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            pendingButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.buttonGrey));
        }

        Button acceptedButton = getView().findViewById(R.id.filter_accepted);
        if (activeFilters.contains("accepted")) {
            acceptedButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.enrolled));
        } else {
            acceptedButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.buttonGrey));
        }

        Button chosenButton = getView().findViewById(R.id.filter_chosen);
        if (activeFilters.contains("chosen")) {
            chosenButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.chosen));
        } else {
            chosenButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.buttonGrey));
        }

        Button declinedButton = getView().findViewById(R.id.filter_declined);
        if (activeFilters.contains("declined")) {
            declinedButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.cancelled));
        } else {
            declinedButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.buttonGrey));
        }
    }


    /**
     * Fetches events from Firebase based on active filters.
     * If no filters are active, fetches all events.
     * Additionally gets the user's profile picture from firebase and sets it.
     */
    private void fetchEvents() {
        FirebaseConnector firebaseConnector = new FirebaseConnector();
        String userID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        firebaseConnector.getPicture("profile", userID,
                URL -> {
                    Glide.with(this)
                            .load(URL)
                            .placeholder(R.drawable.profile)
                            .into((ImageView) this.getView().findViewById(R.id.profile_picture));
                },
                exception -> {
                    Glide.with(this)
                            .load("https://robohash.org/" + userID + ".png")
                            .placeholder(R.drawable.profile)
                            .into((ImageView) this.getView().findViewById(R.id.profile_picture));
                });


        if (activeFilters.isEmpty()) {
            eventDataList.clear();
            eventListAdapter.notifyDataSetChanged();
            return;
        }


        eventDataList.clear();
        for (String filter : activeFilters) {
            String collectionPath = "users/" + userID + "/waitlists/" + filter + "/events";

            firebaseConnector.getSubCollection(collectionPath,
                    events -> {
                        for (Map<String, Object> event : events) {
                            String eventId = (String) event.get("eventID");
                            String eventName = (String) event.get("eventName");
                            String eventLocation = (String) event.get("facilityName");
                            String eventStatus = filter;
                            String eventDescription = (String) event.get("eventDescription");
                            String posterPath = (String) event.get("posterPath");

                            eventDataList.add(new EventData(eventId, eventName, eventDescription, posterPath, eventStatus, eventLocation));
                        }
                        eventListAdapter.notifyDataSetChanged();
                    },
                    error -> Log.e("EntrantEventListFragment", "Error fetching events for filter: " + filter, error)
            );
        }
    }
}
