package com.example.plannet.ui.orghome;

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

import com.example.plannet.ArrayAdapters.OrganizerEventListArrayAdapter;
import com.example.plannet.Event.Event;
import com.example.plannet.FirebaseConnector;
import com.example.plannet.R;
import com.example.plannet.databinding.FragmentOrganizerQrcodesListBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to display the hashed QR codes from organizer
 */
public class OrganizerHashedQrListFragment extends Fragment {
    private FragmentOrganizerQrcodesListBinding binding;
    private FirebaseConnector dbConnector = new FirebaseConnector();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOrganizerQrcodesListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String userID1 = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Using DocumentReference collection to retrieve DB info - lab5
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userQrRef = db.collection("users").document(userID1);

        // Fetch created Events from Firebase using callback
        dbConnector.getOrganizerEventsList(userID1, events -> {
            // Update the adapter with fetched events
            OrganizerEventListArrayAdapter adapter = new OrganizerEventListArrayAdapter(getContext(), events);
            binding.eventList.setAdapter(adapter);
        });

        userQrRef.get().addOnSuccessListener(documentSnapshot -> {
            if (binding != null) {
                if (documentSnapshot.exists()) {
                    List<String> createdEventIDs = (List<String>) documentSnapshot.get("createdEvents");
                    // Set the facility name in the title
                    String facilityName = documentSnapshot.getString("facility.name");
                    if (facilityName != null) {
                        binding.title.setText("Facility: " + facilityName);
                    } else {
                        binding.title.setText("Facility: null");
                    }
                }
            }
        }).addOnFailureListener(e -> {
            // Make a toast of error?
        });

        // button listener for back button
        binding.backArrow.setOnClickListener(v -> {
            // Functionality for the back arrow
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_organizerEventListFragment_to_navigation_home);
        });

        // Listener for clicking an element of the list
        binding.eventList.setOnItemClickListener((adapterView, view, i, l) -> {
            // Get the selected event
            Event clickedEvent = (Event) adapterView.getItemAtPosition(i);

            // Create a bundle to access the event from the new page
            Bundle passedEventBundle = new Bundle();
            Log.d("OrganizerQR", "Bundling event with name: " + clickedEvent.getEventName());
            passedEventBundle.putSerializable("event", clickedEvent);

            // Navigate to the new fragment
            NavController navController = Navigation.findNavController(view);
            // Don't forget to pass the bundle!
            navController.navigate(R.id.action_organizerHashedQrListFragment_to_organizerViewEventFragment, passedEventBundle);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}