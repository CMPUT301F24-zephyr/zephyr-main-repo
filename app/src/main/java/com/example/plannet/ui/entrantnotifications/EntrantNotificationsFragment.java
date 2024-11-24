package com.example.plannet.ui.entrantnotifications;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.example.plannet.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

public class EntrantNotificationsFragment extends Fragment {
    private FirebaseFirestore db;
    private String userID;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrant_notifications, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        userID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Initialize the Switch
        Switch notifSwitch = view.findViewById(R.id.notif_switch);

        String profilePath = String.format("users/%s/userInfo/profile", userID);

        // Retrieve and display current state from Firestore
        db.document(profilePath)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("notifsActivated")) {
                        boolean isActivated = documentSnapshot.getBoolean("notifsActivated");
                        notifSwitch.setChecked(isActivated); // Set switch based on value on db
                    } else {
                        // If the field does not exist, set it to false by default
                        notifSwitch.setChecked(false);
                        db.document(profilePath)
                                .set(new HashMap<String, Object>() {{
                                    put("notifsActivated", false);
                                }}, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Default notifsActivated set to false"))
                                .addOnFailureListener(e -> Log.e("Firestore", "Error setting default value", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching user data", e));

        // Update Firestore when the Switch is toggled
        notifSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            db.document(profilePath)
                    .update("notifsActivated", isChecked)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Notification preference updated: " + isChecked))
                    .addOnFailureListener(e -> Log.e("Firestore", "Error updating notification preference", e));
        });

        return view;
    }
}