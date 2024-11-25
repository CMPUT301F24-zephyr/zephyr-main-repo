package com.example.plannet.ui.entranthome;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.plannet.Entrant.EntrantProfile;
import com.example.plannet.FirebaseConnector;
import com.example.plannet.R;
import com.example.plannet.databinding.FragmentHomeEntrantBinding;
import com.example.plannet.ui.entranthome.EntrantHomeViewModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

/**
 * this is the home fragment for entrants, which is also set to default
 */

public class EntrantHomeFragment extends Fragment {

    private FragmentHomeEntrantBinding binding;
    private EntrantHomeViewModel entrantHomeViewModel;
    private Button adminButton;


    /**
     * view hierarchy - onCreate contains viewmodel configurations
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize ViewModel
        entrantHomeViewModel = new ViewModelProvider(this).get(EntrantHomeViewModel.class);
    }

    /**
     * onCreateView is 2nd up in view hierarchy which contains everything
     * for inflators/binding
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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment and set up View Binding
        binding = FragmentHomeEntrantBinding.inflate(inflater, container, false);
//        binding.buttonAdminn.setVisibility(View.INVISIBLE);
        adminButton = binding.buttonAdminn;
        String userID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        checkIfUserIsAdmin(userID);

        // Initialize EntrantProfile if not already done
        try {
            EntrantProfile profile = EntrantProfile.getInstance();
            Log.d("EntrantHomeFragment", "EntrantProfile already initialized: " + profile.getName());
            //refreshWaitlists(userID);
        } catch (IllegalStateException e) {
            Log.d("EntrantHomeFragment", "Initializing EntrantProfile for user: " + userID);
            initializeEntrantProfile(userID);
        }
        return binding.getRoot();

    }

    private void refreshWaitlists(String userID) {
        FirebaseConnector firebaseConnector = new FirebaseConnector();
        EntrantProfile profile = EntrantProfile.getInstance();

        // Clear existing waitlists to avoid duplication
        profile.getWaitlistPending().clearWaitlist();
        profile.getWaitlistAccepted().clearWaitlist();
        profile.getWaitlistRejected().clearWaitlist();

        // Fetch and update Pending events
        firebaseConnector.getUserEvents(userID, "pending",
                events -> {
                    if (events.isEmpty()) {
                        Log.d("EntrantHomeFragment", "No pending events found for user: " + userID);
                    } else {
                        for (Map<String, Object> event : events) {
                            Log.d("EntrantHomeFragment", "Fetched event: " + event);
                            String eventID = (String) event.get("eventID");
                            profile.getWaitlistPending().addWaitlist(eventID);
                        }
                    }
                    Log.d("EntrantHomeFragment", "Pending events updated successfully.");
                },
                error -> Log.e("EntrantHomeFragment", "Failed to fetch pending events", error)
        );

        // Fetch and update Accepted events
        firebaseConnector.getUserEvents(userID, "accepted",
                events -> {
                    for (Map<String, Object> event : events) {
                        String eventID = (String) event.get("eventID");
                        profile.getWaitlistAccepted().addWaitlist(eventID);
                    }
                    Log.d("EntrantHomeFragment", "Accepted events updated successfully.");
                },
                error -> Log.e("EntrantHomeFragment", "Failed to fetch accepted events", error));

        // Fetch and update Declined events
        firebaseConnector.getUserEvents(userID, "declined",
                events -> {
                    for (Map<String, Object> event : events) {
                        String eventID = (String) event.get("eventID");
                        profile.getWaitlistRejected().addWaitlist(eventID);
                    }
                    Log.d("EntrantHomeFragment", "Declined events updated successfully.");
                },
                error -> Log.e("EntrantHomeFragment", "Failed to fetch declined events", error));
    }

    private void initializeEntrantProfile(String userID) {
        FirebaseConnector firebaseConnector = new FirebaseConnector();

        firebaseConnector.getUserInfo(userID,
                userInfo -> {
                    EntrantProfile profile = EntrantProfile.getInstance(
                            requireContext(),
                            userID,
                            userInfo.get("firstName") + " " + userInfo.get("lastName"),
                            (String) userInfo.get("email"),
                            (String) userInfo.get("phone"),
                            (String) userInfo.get("profilePictureUrl"),
                            userInfo.get("notifsActivated") != null && (Boolean) userInfo.get("notifsActivated")
                    );

                    // Fetch and populate joined events into waitlists
                    firebaseConnector.getJoinedEvents(userID, joinedEvents -> {
                        for (String eventID : joinedEvents) {
                            // Add events to waitlist
                            profile.getWaitlistPending().addWaitlist(eventID); // Example for pending
                        }
                        Log.d("EntrantHomeFragment", "Events successfully loaded into EntrantProfile.");
                    }, error -> {
                        Log.e("EntrantHomeFragment", "Failed to fetch joined events", error);
                    });
                },
                error -> {
                    Log.e("EntrantHomeFragment", "Failed to fetch user info for EntrantProfile initialization", error);
                    Toast.makeText(getContext(), "Failed to load user profile", Toast.LENGTH_SHORT).show();
                }
        );
    }

    /**
     * last in the sequence. this contains all misc items such as buttonlisteneres, etc..
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.scanQrButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_home_to_qrCodeScan);
        });

        binding.viewEventsButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(EntrantHomeFragment.this)
                    .navigate(R.id.navigation_event_list);
        });

//        binding.viewProfileButton.setOnClickListener(v -> {
//            NavController navController = Navigation.findNavController(v);
//            navController.navigate(R.id.navigation_entrant_profile_display);
//        });

        // switch role button
        binding.buttonSwitch2.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_entranthome_to_orghome);
        });

        // admin button
        adminButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_entranthome_to_admin);
        });
    }

    /**
     * onResume method. Called every time this fragment is shown. This is used to ensure the profile
     * picture updates if the user changes it.
     */
    @Override
    public void onResume() {
        super.onResume();
        FirebaseConnector firebaseConnector = new FirebaseConnector();
        String userID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // fetch profile picture from Firebase every time the view is loaded (in case the user changes it)
        firebaseConnector.getUserInfo(userID,
                userInfo -> {
                    String profilePictureUrl = (String) userInfo.get("profilePictureUrl");
                    if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                        Log.d("EntrantHomeFragment", "Loading profile picture with URL: " + profilePictureUrl);
                        loadProfilePicture(profilePictureUrl);
                    } else {
                        // If not, get default profile picture from a web API that generates pictures using a seed
                        Log.d("EntrantHomeFragment", "No profile picture uploaded, generating profile picture from name: " + userID);
                        generateDefaultProfilePic(userID);
                    }
                },
                error -> Log.e("EntrantHomeFragment", "Failed to refresh profile picture", error)
        );
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
                            adminButton.setVisibility(View.VISIBLE);
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
     * Generates a profile picture using the web API RoboHash (https://robohash.org/) based on the user's ID
     *
     * @param username
     *      The user ID to generate the picture from
     */
    private void generateDefaultProfilePic(String username) {
        Glide.with(this)
                .load("https://robohash.org/" + username + ".png")
                .placeholder(R.drawable.profile)
                .into(binding.profilePicture);
    }

    /**
     * Loads a profile picture if a user has one set
     * @param pictureURL
     */
    private void loadProfilePicture(String pictureURL){
        Glide.with(this)
                .load(pictureURL)
                .placeholder(R.drawable.profile)  // Placeholder while it loads
                .error(R.drawable.profile)  // If error loading, just set to default
                .into(binding.profilePicture);
    }

    /**
     * This method is for clear the binding reference when view is destroyed
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // This method is for clear the binding reference when view is destroyed
        binding = null;
    }
}