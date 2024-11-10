package com.example.plannet.ui.orghome;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.plannet.R;
import com.example.plannet.databinding.FragmentHomeOrganizerBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Organizer Homescreen fragment. Provides the general navigation options for an organizer.
 */
public class OrganizerHomeFragment extends Fragment {

    private FragmentHomeOrganizerBinding binding;
    private OrganizerHomeViewModel organizerHomeViewModel;
    private DocumentReference userQrRef;

    /**
     * configurations upon creating fragment such as viewmodels and initializations
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewModel
        organizerHomeViewModel = new ViewModelProvider(this).get(OrganizerHomeViewModel.class);

        // Initialize Firebase DocumentReference to retrieve DB info
        String userID1 = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        userQrRef = db.collection("users").document(userID1);
    }

    /**
     * set up views and inflator
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
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout using View Binding
        binding = FragmentHomeOrganizerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * configure buttonlisteners & db operations
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up button click listeners
        binding.buttonNewEvent.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_home_to_createEvent);
        });

        binding.buttonQrCodes.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_navigation_home_to_organizerEventListFragment);
        });

        binding.buttonDraw.setOnClickListener(v -> {
            // Future navigation reference
            // navController.navigate(R.id.action_homeFragment_to_drawFragment);
        });

        binding.buttonSwitch.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_home_to_entrant);
        });

        // Fetch and display facility title if available
        userQrRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String facilityName = documentSnapshot.getString("facility.name");
                binding.title.setText("Facility: " + (facilityName != null ? facilityName : "null"));
            }
        }).addOnFailureListener(e -> {
            // Handle failure, maybe show a toast or log the error
        });
    }

    /**
     * Method for setting the binding to null when the view is destroyed to avoid errors.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Clear the binding reference
    }
}