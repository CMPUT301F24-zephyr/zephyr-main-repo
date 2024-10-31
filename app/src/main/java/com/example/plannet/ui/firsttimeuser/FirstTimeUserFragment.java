package com.example.plannet.ui.firsttimeuser;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.plannet.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FirstTimeUserFragment extends Fragment {

    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first_time_user, container, false);

        // Initialize Firestore and SharedPreferences
        db = FirebaseFirestore.getInstance();
        sharedPreferences = requireActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        Log.d("FirstTimeUserFragment", "CHECKPOINT");
        // Set up button click listener
        Button getStartedButton = view.findViewById(R.id.button_welcome);
        getStartedButton.setOnClickListener(v -> {
            String uniqueID = UUID.randomUUID().toString();

            // Save UUID to SharedPreferences
            sharedPreferences.edit().putString("unique_id", uniqueID).apply();

            // Send UUID to Firestore
            SendUUIDtoDB(uniqueID);
            // Send user to home fragment
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.navigation_home);
        });

        return view;
    }

    public void SendUUIDtoDB(String uniqueID) {
        Map<String, Object> user = new HashMap<>();
        user.put("UUID", uniqueID);

        db.collection("users").document(uniqueID)
                .set(user)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "UUID successfully sent to Firebase"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error saving UUID", e));
    }
}