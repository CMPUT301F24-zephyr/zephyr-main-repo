package com.example.plannet.ui.orgevents;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.plannet.Event.Event;
import com.example.plannet.R;
import com.example.plannet.databinding.FragmentOrganizerViewEventBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;

/**
 * Displays the details of an event created by an organizer.
 */
public class OrganizerViewEventFragment extends Fragment {

    private FragmentOrganizerViewEventBinding binding;
    private Event event = null;  // Default null for error catching

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
        binding = FragmentOrganizerViewEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd-yyyy");

        String userID1 = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Using DocumentReference collection to retrieve DB info - lab5
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userQrRef = db.collection("users").document(userID1);

        userQrRef.get().addOnSuccessListener(documentSnapshot -> {
            if (binding != null) {
                if (documentSnapshot.exists()) {
                    // Get the facility location
                    String facilityLocation = documentSnapshot.getString("facility.location");
                    if (facilityLocation != null) {
                        binding.facilityAddress.setText(facilityLocation);
                    } else {
                        binding.facilityAddress.setText("Location unspecified");
                    }
                }
            }
        }).addOnFailureListener(e -> {
            // Make a toast of error?
        });

        Log.d("Organizer View Event", "Arguments received: " + getArguments());
        if (getArguments() != null){
            Log.d("View Event", "Arguments found in bundle.");
            // Retrieve the argument we passed in the bundle before coming here
            event = (Event) getArguments().getSerializable("event");

            if (event != null) {
                Log.d("View Event", "Event found with name: " + event.getEventName());
                // We want to set all the fields so it displays correctly
                binding.title.setText("Event: " + event.getEventName());
                binding.facilityName.setText(event.getFacility());
                if (event.getRegistrationStartDate().equals(event.getRegistrationDateDeadline())){
                    // The event is only 1 day long
                    binding.eventDates.setText("Event Date: " + formatter.format(event.getEventDate()));
                }
                else {
                    binding.eventDates.setText("Event Dates: " + formatter.format(event.getEventDate()) + " - " + formatter.format(event.getRegistrationStartDate()));
                }
                binding.capacity.setText("Capacity: [" + String.valueOf(event.getMaxEntrants()) + "]");
                if (event.getPrice() == "0" || event.getPrice() == ""){
                    binding.cost.setText("Cost: [Free!]");
                }
                else {
                    binding.cost.setText("Cost: [$" + event.getPrice() + "]");
                }
                binding.endDate.setText(formatter.format(event.getRegistrationDateDeadline()));
                binding.descriptionWriting.setText(event.getDescription());
            }
        }
        else {
            Log.e("Organizer View Event", "No arguments recieved. Event/bundle did not pass correctly from previous fragment.");
            binding.title.setText("ERROR!");
            Toast.makeText(getContext(), "Error passing event. Please click the back arrow.", Toast.LENGTH_SHORT).show();
        }

        // button listener for back button
        binding.backArrow.setOnClickListener(v -> {
            if (binding != null) {
                try {
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.action_organizerViewEventFragment_to_organizerHashedQrListFragment);
                } catch (Exception e) {
                    Log.e("OrganizerViewEvent", "Error navigating back", e);
                }
            } else {
                Log.e("OrganizerViewEvent", "Binding is null in back arrow listener.");
            }
        });

        // button listener for clicking "Entrants" button
        binding.entrantsButton.setOnClickListener(v -> {
            if (event != null) {
                // Bundle the event again to send to new page
                Bundle passedEventBundle = new Bundle();
                Log.d("Organizer View Event", "Bundling event with name: " + event.getEventName());
                passedEventBundle.putSerializable("event", event);

                // Navigate to the new fragment
                NavController navController = Navigation.findNavController(v);
                // Don't forget to pass the bundle!
                navController.navigate(R.id.action_organizerViewEventFragment_to_organizerViewEntrantsFragment, passedEventBundle);
            }
            else {
                // Somehow the user got to this page and the event did not bundle
                Toast.makeText(getContext(), "Error passing event. Please click the back arrow.", Toast.LENGTH_SHORT).show();
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