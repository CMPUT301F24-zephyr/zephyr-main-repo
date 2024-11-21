package com.example.plannet.ui.orgevents;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.plannet.ArrayAdapters.OrganizerEntrantListArrayAdapter;
import com.example.plannet.Entrant.EntrantProfile;
import com.example.plannet.Event.Event;
import com.example.plannet.FirebaseConnector;
import com.example.plannet.R;
import com.example.plannet.databinding.FragmentOrganizerEntrantListBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays a list of the entrants of an event created by an organizer, as well as their status.
 * This list can be sorted using buttons at the top of the page.
 */
public class OrganizerViewEntrantsFragment extends Fragment {

    private FragmentOrganizerEntrantListBinding binding;
    private Event event = null;  // Default null
    private FirebaseConnector dbConnector = new FirebaseConnector();
    private List<String> filters = new ArrayList<>();

    /**
     * onCreateView for what happens when the view is first created.
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
     *      the View
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOrganizerEntrantListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Empty ArrayAdapter
        ArrayList<EntrantProfile> entrantProfiles = new ArrayList<>();
        OrganizerEntrantListArrayAdapter adapter = new OrganizerEntrantListArrayAdapter(getContext(), entrantProfiles);
        binding.entrantList.setAdapter(adapter);

        Log.d("Organizer View Entrants", "Arguments received: " + getArguments());
        if (getArguments() != null){
            Log.d("Organizer View Entrants", "Arguments found in bundle.");
            // Retrieve the argument we passed in the bundle before coming here
            event = (Event) getArguments().getSerializable("event");

            if (event != null) {
                Log.d("Organizer View Entrants", "Event found with name: " + event.getEventName());
                binding.title.setText("Event: " + event.getEventName());

                // Get entrants
                List<String> statuses = List.of("pending", "chosen", "enrolled", "cancelled");
                for (String status : statuses) {
                    // Get entrants for each status
                    dbConnector.getEventWaitlistEntrants(event.getEventID(), status, entrants -> {
                        adapter.addAll(entrants);
                        adapter.notifyDataSetChanged();
                    });
                }
            }
        }

        // Filter buttons:
        // Idea for filtering system: CodeWithCal https://www.youtube.com/watch?v=liGwWbR-2D8&ab_channel=CodeWithCal
        // Pending
        binding.pendingButton.setOnClickListener(v -> {
            if (filters.contains("pending")){
                // This button is active, we are making it inactive/removing the filter.
                filters.remove("pending");
                binding.pendingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.buttonGrey)));
                binding.pendingButton.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.white)));
            }
            else {
                filters.add("pending");
                binding.pendingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.white)));
                binding.pendingButton.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.black)));
            }
            // Once the filters are properly set:
            Log.d("View Entrants", "Filtering with filters: " + filters.toString());
            adapter.filter(filters);
        });

        // Chosen
        binding.chosenButton.setOnClickListener(v -> {
            if (filters.contains("chosen")){
                // This button is active, we are making it inactive/removing the filter.
                filters.remove("chosen");
                binding.chosenButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.buttonGrey)));
            }
            else {
                filters.add("chosen");
                binding.chosenButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.chosen)));
            }
            // Once the filters are properly set:
            Log.d("View Entrants", "Filtering with filters: " + filters.toString());
            adapter.filter(filters);
        });

        // Enrolled
        binding.enrolledButton.setOnClickListener(v -> {
            if (filters.contains("enrolled")){
                // This button is active, we are making it inactive/removing the filter.
                filters.remove("enrolled");
                binding.enrolledButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.buttonGrey)));
            }
            else {
                filters.add("enrolled");
                binding.enrolledButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.enrolled)));
            }
            // Once the filters are properly set:
            Log.d("View Entrants", "Filtering with filters: " + filters.toString());
            adapter.filter(filters);
        });

        // Cancelled
        binding.cancelledButton.setOnClickListener(v -> {
            if (filters.contains("cancelled")){
                // This button is active, we are making it inactive/removing the filter.
                filters.remove("cancelled");
                binding.cancelledButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.buttonGrey)));
            }
            else {
                filters.add("cancelled");
                binding.cancelledButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.cancelled)));
            }
            // Once the filters are properly set:
            Log.d("View Entrants", "Filtering with filters: " + filters.toString());
            adapter.filter(filters);
        });

        // button listener for back button
        binding.backArrow.setOnClickListener(v -> {
            if (binding != null) {
                try {
                    // Bundle the event back, otherwise the page forgets what event it was
                    Bundle passedEventBundle = new Bundle();
                    Log.d("Organizer View Entrants", "Bundling event with name: " + event.getEventName());
                    passedEventBundle.putSerializable("event", event);

                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.action_organizerViewEntrantsFragment_to_organizerViewEventFragment, passedEventBundle);
                } catch (Exception e) {
                    Log.e("Organizer View Entrants", "Error navigating back", e);
                }
            } else {
                Log.e("Organizer View Entrants", "Binding is null in back arrow listener.");
            }
        });

        // Click an entrant to view their info
        binding.entrantList.setOnItemClickListener((adapterView, view, i, l) -> {
            // Get the selected event
            EntrantProfile clickedEntrant = (EntrantProfile) adapterView.getItemAtPosition(i);

            // Create a bundle to access the entrant from the new page
            Bundle bundle = new Bundle();
            Log.d("Organizer View Entrants", "Bundling entrant with name: " + clickedEntrant.getName());
            bundle.putSerializable("entrant", clickedEntrant);

            // Additionally must bundle the event in case the user wants to return to this page
            Log.d("Organizer View Entrants", "Bundling event with name: " + event.getEventName());
            bundle.putSerializable("event", event);

            // Navigate to the new fragment
            NavController navController = Navigation.findNavController(view);
            // Don't forget to pass the bundle!
            navController.navigate(R.id.action_organizerViewEntrantsFragment_to_organizerViewEntrantInfoFragment, bundle);
        });

        return root;
    }

    /**
     * Method for setting the binding to null when the view is destroyed to avoid errors.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView(); // Ensure the superclass method is called
        binding = null;        // Clean up the binding
    }
}