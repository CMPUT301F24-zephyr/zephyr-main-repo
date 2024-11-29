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
import com.example.plannet.databinding.FragmentOrganizerViewEntrantBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays info for an entrant on the waitlist for an organizer's event.
 */
public class OrganizerViewEntrantInfoFragment extends Fragment {

    private FragmentOrganizerViewEntrantBinding binding;
    private EntrantProfile entrant = null;  // Default null
    private Event event = null;  // Default null
    private FirebaseConnector dbConnector = new FirebaseConnector();
    private double latitude;
    private double longitude;

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
        binding = FragmentOrganizerViewEntrantBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Log.d("Organizer View Entrant Info", "Arguments received: " + getArguments());
        if (getArguments() != null){
            Log.d("Organizer View Entrant Info", "Arguments found in bundle.");
            // Retrieve the arguments we passed in the bundle before coming here
            entrant = (EntrantProfile) getArguments().getSerializable("entrant");
            event = (Event) getArguments().getSerializable("event");

            if (event != null) {
                Log.d("Organizer View Entrant Info", "Event found with name: " + event.getEventName());
            }

            if (entrant != null) {
                Log.d("Organizer View Entrant Info", "Entrant found with name: " + entrant.getName());
                // Fill the fields on the page:
                binding.inviteText.setText(entrant.getWaitlistStatus().toUpperCase());
                binding.firstnameText.setText(entrant.getFirstName());
                binding.lastnameText.setText(entrant.getLastName());
                binding.phoneText.setText(entrant.getPhoneNumber());
                binding.emailText.setText(entrant.getEmail());
                // Remember to add functionality for clicking the map and seeing their location
                //binding.profilePicture.setImageResource(NEW IMAGE);  // Remember to change the profile picture when that is implemented!
            }
        }
        // set on click listener for map_icon to send to Map fragment class with latitude+longitude in viewmodel and show on map
        binding.mapIcon.setOnClickListener(v -> {
            if (binding != null) {
                //Log.e("EntrantInfo", "latitude = " + entrant.getLatitude() + "longitude = " + entrant.getLongitude());
                latitude = entrant.getLatitude();
                longitude = entrant.getLongitude();
                // Create a Bundle to pass data
                Bundle bundle = new Bundle();
                bundle.putDouble("latitude", latitude);
                bundle.putDouble("longitude", longitude);

                // Navigate to OrganizerViewEntrantMapFragment with the Bundle
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_organizerViewEntrantInfoFragment_to_organizerViewEntrantMapFragment, bundle);
            }
        });

        // button listener for back button
        binding.backArrow.setOnClickListener(v -> {
            if (binding != null) {
                try {
                    // Bundle the event back, otherwise the last page forgets what event it was
                    Bundle passedEventBundle = new Bundle();
                    Log.d("Organizer View Entrant Info", "Bundling event with name: " + event.getEventName());
                    passedEventBundle.putSerializable("event", event);

                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.action_organizerViewEntrantInfoFragment_to_organizerViewEntrantsFragment, passedEventBundle);
                } catch (Exception e) {
                    Log.e("Organizer View Entrant Info", "Error navigating back", e);
                }
            } else {
                Log.e("Organizer View Entrant Info", "Binding is null in back arrow listener.");
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