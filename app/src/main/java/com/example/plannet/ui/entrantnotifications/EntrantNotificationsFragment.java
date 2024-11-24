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
    private FirebaseFirestore db;
    private String userID;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrant_notifications, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        userID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Initialize Switches
        Switch notifSwitchAll = view.findViewById(R.id.notif_switch_all);
        Switch notifSwitchWinner = view.findViewById(R.id.notif_switch_winner);
        Switch notifSwitchLoser = view.findViewById(R.id.notif_switch_loser);

        // Firestore path: users -> userID -> userInfo -> profile
        String profilePath = String.format("users/%s/userInfo/profile", userID);

        // Retrieve and display current state for each switch
        db.document(profilePath)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // retrieve pre-set/default values
                        boolean allNotifs = documentSnapshot.contains("notifsAll") ? documentSnapshot.getBoolean("notifsAll") : false;
                        boolean winnerNotifs = documentSnapshot.contains("notifsWinner") ? documentSnapshot.getBoolean("notifsWinner") : false;
                        boolean loserNotifs = documentSnapshot.contains("notifsLoser") ? documentSnapshot.getBoolean("notifsLoser") : false;

                        notifSwitchAll.setChecked(allNotifs);
                        notifSwitchWinner.setChecked(winnerNotifs);
                        notifSwitchLoser.setChecked(loserNotifs);

                        if (!documentSnapshot.contains("notifsAll") || !documentSnapshot.contains("notifsWinner") || !documentSnapshot.contains("notifsLoser")) {
                            db.document(profilePath)
                                    .set(new HashMap<String, Object>() {{
                                        put("notifsAll", allNotifs);
                                        put("notifsWinner", winnerNotifs);
                                        put("notifsLoser", loserNotifs);
                                    }}, SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Default notification settings initialized."))
                                    .addOnFailureListener(e -> Log.e("Firestore", "Error initializing default values", e));
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching user data", e));

        // listeners to update Firestore when switches are toggled
        notifSwitchAll.setOnCheckedChangeListener((buttonView, isChecked) -> updateNotificationPreference(profilePath, "notifsAll", isChecked));
        notifSwitchWinner.setOnCheckedChangeListener((buttonView, isChecked) -> updateNotificationPreference(profilePath, "notifsWinner", isChecked));
        notifSwitchLoser.setOnCheckedChangeListener((buttonView, isChecked) -> updateNotificationPreference(profilePath, "notifsLoser", isChecked));

        return view;
    }

    /**
     * Method to update Firestore notification preferences
     * @param profilePath
     * @param field
     * @param value
     */
    private void updateNotificationPreference(String profilePath, String field, boolean value) {
        db.document(profilePath)
                .update(field, value)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", field + " updated to: " + value))
                .addOnFailureListener(e -> Log.e("Firestore", "Error updating " + field, e));
    }
}