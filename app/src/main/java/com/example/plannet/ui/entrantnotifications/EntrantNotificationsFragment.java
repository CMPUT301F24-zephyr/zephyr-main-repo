package com.example.plannet.ui.entrantnotifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.example.plannet.Notification.NotificationService;
import com.example.plannet.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * notification preferences for entrant using system/shared preferences
 */
public class EntrantNotificationsFragment extends Fragment {
    private FirebaseFirestore firebaseDB;
    private String userID;


    @Override
    public void onResume() {
        super.onResume();
        View view = getView();

        if (view != null) {
            SharedPreferences preferences = requireContext().getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
            boolean isEnabled = preferences.getBoolean("notificationsEnabled", false);

            Switch notifSwitch = view.findViewById(R.id.notif_switch);
            notifSwitch.setChecked(isEnabled);
        }
    }
    /**
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
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrant_notifications, container, false);

        // Initialize Firestore
        firebaseDB = FirebaseFirestore.getInstance();

        userID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        SharedPreferences preferences = requireContext().getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        boolean isEnabled = preferences.getBoolean("notificationsEnabled", false);

        Switch notifSwitch = view.findViewById(R.id.notif_switch);
        notifSwitch.setChecked(isEnabled);

        // Switch listener to handle changes and update as needed
        notifSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("notificationsEnabled", isChecked);
            editor.apply();

            if (isChecked) {
                enableNotifications();
            } else {
                disableNotifications();
            }
        });

        return view;
    }

    /**
     * method to enable notifications which makes a new NotificationService intent (activity)
     * followed by a toast message
     */
    private void enableNotifications() {
        Intent intent = new Intent(requireContext(), NotificationService.class);
        requireContext().startService(intent);
        Toast.makeText(requireContext(), "Notifications enabled", Toast.LENGTH_SHORT).show();
    }
    /**
     * method to disable notifications which makes a new NotificationService intent (activity)
     * followed by a toast message
     */
    private void disableNotifications() {
        Intent intent = new Intent(requireContext(), NotificationService.class);
        requireContext().stopService(intent);
        Toast.makeText(requireContext(), "Notifications disabled", Toast.LENGTH_SHORT).show();
    }

}