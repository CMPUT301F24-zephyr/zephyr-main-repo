package com.example.plannet.ui.entrantnotifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.plannet.Notification.Invite;
import com.example.plannet.Notification.NotificationService;
import com.example.plannet.ArrayAdapters.InviteListArrayAdapter;
import com.example.plannet.Notification.Invite;
import com.example.plannet.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * notification preferences for entrant using system/shared preferences
 */
public class EntrantNotificationsFragment extends Fragment {
    private FirebaseFirestore firebaseDB;
    private String userID;
    private ArrayList<Invite> inviteList = new ArrayList<>();
    private InviteListArrayAdapter inviteAdapter;


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
        // Initialize the ListView and Adapter
        ListView inviteListView = view.findViewById(R.id.invite_list_view);
        inviteAdapter = new InviteListArrayAdapter(requireContext(),R.layout.listitem_invite, inviteList);
        inviteListView.setAdapter(inviteAdapter);

        inviteListView.setOnItemClickListener((parent, view1, position, id) -> {
            Invite selectedInvite = inviteList.get(position);
            showInviteDialog(selectedInvite);
        });

        loadInvites();

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

    private void loadInvites() {
        firebaseDB.collection("notifications")
                .document(userID)
                .collection("invites")
                //.whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    inviteList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String inviteID = doc.getId();
                        String eventTitle = doc.getString("eventName");
                        String eventLocation = doc.getString("location");
                        String status = doc.getString("status");
                        inviteList.add(new Invite(inviteID, eventTitle, eventLocation, status));
                    }
                    inviteAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching invites", e));
    }

    private void showInviteDialog(Invite invite) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Invitation")
                .setMessage("Event: " + invite.getEventTitle() + "\nLocation: " + invite.getEventLocation())
                .setPositiveButton("Accept", (dialog, which) -> handleInviteAction(invite, "accepted"))
                .setNegativeButton("Decline", (dialog, which) -> handleInviteAction(invite, "declined"))
                .show();
    }

    private void handleInviteAction(Invite invite, String action) {
        firebaseDB.collection("notifications")
                .document(userID)
                .collection("invites")
                .document(invite.getId())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        firebaseDB.collection("notifications")
                                .document(userID)
                                .collection("invites")
                                .document(doc.getId())
                                .update("status", action)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("InviteAction", "Invite " + action + " successfully.");
                                    loadInvites();
                                })
                                .addOnFailureListener(e -> Log.e("InviteAction", "Error updating invite status", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("InviteAction", "Error fetching invite", e));
    }
}

}