package com.example.plannet.ui.orgevents;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.plannet.Event.Event;
import com.example.plannet.FirebaseConnector;
import com.example.plannet.R;
import com.example.plannet.databinding.FragmentOrganizerEntrantListBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Displays a list of the entrants of an event created by an organizer, as well as their status.
 * This list can be sorted using buttons at the top of the page.
 */
public class OrganizerViewEntrantsFragment extends Fragment {

    private FragmentOrganizerEntrantListBinding binding;
    private Event event = null;  // Default null
    private FirebaseConnector dbConnector = new FirebaseConnector();

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

        // Using DocumentReference collection to retrieve DB info - lab5
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d("Organizer View Entrants", "Arguments received: " + getArguments());
        if (getArguments() != null){
            Log.d("Organizer View Entrants", "Arguments found in bundle.");
            // Retrieve the argument we passed in the bundle before coming here
            event = (Event) getArguments().getSerializable("event");

            if (event != null) {
                Log.d("Organizer View Entrants", "Event found with name: " + event.getEventName());
                binding.title.setText("Event: " + event.getEventName());

                // Get the IDs of entrants:
                dbConnector.getEventWaitlistEntrants(event.getEventID(), );
            }
        }

        // button listener for back button
        binding.backArrow.setOnClickListener(v -> {
            if (binding != null) {
                try {
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.action_organizerViewEntrantsFragment_to_organizerViewEventFragment);
                } catch (Exception e) {
                    Log.e("Organizer View Entrants", "Error navigating back", e);
                }
            } else {
                Log.e("Organizer View Entrants", "Binding is null in back arrow listener.");
            }
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