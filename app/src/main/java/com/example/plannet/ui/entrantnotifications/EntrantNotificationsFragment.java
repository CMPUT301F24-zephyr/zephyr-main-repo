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

/**
 * notification preferences for entrant
 */
public class EntrantNotificationsFragment extends Fragment {
    private FirebaseFirestore firebaseDB;
    private String userID;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrant_notifications, container, false);

        // Initialize Firestore
        firebaseDB = FirebaseFirestore.getInstance();

        userID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        Switch notifSwitch = view.findViewById(R.id.notif_switch);

        // Firestore path: users -> userID -> userInfo -> profile
        String profilePath = String.format("users/%s/userInfo/profile", userID);

        // Retrieve and display current state from Firestore
        firebaseDB.document(profilePath)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("notifsActivated")) {
                        boolean isActivated = documentSnapshot.getBoolean("notifsActivated");
                        notifSwitch.setChecked(isActivated);
                    } else {
                        // default value = true
                        notifSwitch.setChecked(true);
                        firebaseDB.document(profilePath)
                                .set(new HashMap<String, Object>() {{
                                    put("notifsActivated", true);
                                }}, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Default notifsActivated set to true"))
                                .addOnFailureListener(e -> Log.e("Firestore", "Error setting default value", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching user data", e));

        // Update Firestore when the Switch is toggled
        notifSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            firebaseDB.document(profilePath)
                    .update("notifsActivated", isChecked)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Notification preference updated: " + isChecked))
                    .addOnFailureListener(e -> Log.e("Firestore", "Error updating notification preference", e));
        });

        return view;
    }
}