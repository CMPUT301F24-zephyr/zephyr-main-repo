package com.example.plannet.ui.firsttimeuser;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.plannet.FirebaseConnector;
import com.example.plannet.R;

/**
 * Fragment that is displayed when a user first opens the app (their android ID is not recognized).
 * Provides a welcome message and saves their user ID when they enter the app.
 */
public class FirstTimeUserFragment extends Fragment {

    private FirebaseConnector db;
    private SharedPreferences sharedPreferences;

    /**
     * OnCreateView method that happens when the view is created.
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
     *      The view after code is applied.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first_time_user, container, false);

        // Initialize Firestore and SharedPreferences
        db = new FirebaseConnector();
        //sharedPreferences = requireActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        //Log.d("FirstTimeUserFragment", "CHECKPOINT");
        // Set up button click listener
        Button getStartedButton = view.findViewById(R.id.button_welcome);
        getStartedButton.setOnClickListener(v -> {
            String uniqueID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            //sharedPreferences.edit().putString("unique_id", uniqueID).apply();

            // Add user to Firestore
            db.addUserToFirestore(uniqueID);

            // Navigate to entrant profile fragment
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_firstTimeUser_to_entrantProfile);
        });

        return view;
    }
}