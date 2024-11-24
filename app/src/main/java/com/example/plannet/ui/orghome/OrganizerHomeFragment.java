package com.example.plannet.ui.orghome;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.plannet.FirebaseConnector;
import com.example.plannet.R;
import com.example.plannet.databinding.FragmentHomeOrganizerBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Organizer Homescreen fragment. Provides the general navigation options for an organizer.
 */
public class OrganizerHomeFragment extends Fragment {

    private FragmentHomeOrganizerBinding binding;
    private OrganizerHomeViewModel organizerHomeViewModel;
    private DocumentReference userQrRef;
    String userID1;


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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        userID1 = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
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

        checkIfUserIsAdmin(userID1);

        // Set up button click listeners
        binding.buttonNewEvent.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_home_to_createEvent);
        });


        binding.buttonDraw.setOnClickListener(v -> {
            // Future navigation reference
            // navController.navigate(R.id.action_homeFragment_to_drawFragment);
        });

        binding.buttonSwitch.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_home_to_entrant);
        });

        binding.buttonAdmin.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_home_to_admin);
        });

        // Fetch and display facility title if available
        userQrRef.get().addOnSuccessListener(documentSnapshot -> {
            if (binding != null && documentSnapshot.exists()) {
                String facilityName = documentSnapshot.getString("facility.name");
                binding.title.setText("Facility: " + (facilityName != null ? facilityName : "Not Set"));
            }
        }).addOnFailureListener(e -> {
            // Handle failure, maybe show a toast or log the error
        });
    }


    /**
     * checks if a user is listed as an admin on firebase by looping through the documents and comparing userIDs
     * @param userID
     */
    public void checkIfUserIsAdmin(String userID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference adminCollection = db.collection("admin");

        adminCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                boolean isAdmin = false;

                if (querySnapshot != null) {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        if (document.getId().equals(userID)) {
                            isAdmin = true;
                            // Show admin button here
                            //
                            binding.buttonAdmin.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                }

                if (isAdmin) {
                    Log.d("AdminCheck", "User is an admin.");
                    // Perform admin-specific actions here
                } else {
                    Log.d("AdminCheck", "User is not an admin.");
                    // Perform non-admin-specific actions here
                }
            } else {
                Log.e("FirebaseError", "Error fetching admin documents", task.getException());
            }
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