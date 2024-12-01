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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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
import com.example.plannet.ui.entrantprofile.entrantViewEventFragment;
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
        if (position >= 0 && position < inviteList.size()) {
            Invite selectedInvite = inviteList.get(position);

            if (selectedInvite != null) {
                Log.d("InviteListFragment", "Selected Invite: " + selectedInvite);

                Bundle bundle = new Bundle();
                bundle.putString("eventID", selectedInvite.getEventID());
                if (selectedInvite.getStatus().equals("pending")) {
                    bundle.putString("eventStatus", "chosen");
                }
                else {
                    bundle.putString("eventStatus", selectedInvite.getStatus());
                }
                bundle.putString("inviteID", selectedInvite.getId());

                Log.d("InviteListFragment", "Bundle Content: " + bundle.toString());

                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_notifications_to_viewEventFragment, bundle);
            } else {
                Log.e("InviteListFragment", "Selected Invite is null!");
            }
        }

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
                        String eventID = doc.getString("eventID");
                        String eventTitle = doc.getString("eventName");
                        String eventLocation = doc.getString("location");
                        String status = doc.getString("status");
                        inviteList.add(new Invite(inviteID, eventID, eventTitle, eventLocation, status));
                    }
                    inviteAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching invites", e));
    }

    private void navToEventDetails(Invite invite) {
        // Pass event ID and status to entrantViewEventFragment
        Bundle bundle = new Bundle();
        bundle.putString("eventID", invite.getEventID());
        bundle.putString("eventStatus", invite.getStatus());

        entrantViewEventFragment eventFragment = new entrantViewEventFragment();
        eventFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, eventFragment)
                .addToBackStack(null)
                .commit();
    }

}

