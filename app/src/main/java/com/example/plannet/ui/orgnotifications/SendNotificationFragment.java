package com.example.plannet.ui.orgnotifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.plannet.Event.Event;
import com.example.plannet.FirebaseConnector;
import com.example.plannet.FirestoreCallback;
import com.example.plannet.databinding.FragmentOrganizerSendNotificationBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * class for org notification manager
 * has to do with sending all types of notifications to entrants
 */
public class SendNotificationFragment extends Fragment {

    private FragmentOrganizerSendNotificationBinding binding;
    FirebaseConnector fireCon = new FirebaseConnector();
    private Event event = null;  // Default null for error catching
    private String checked_list;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrganizerSendNotificationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Log.d("Organizer Send Notification", "Arguments received: " + getArguments());
        if (getArguments() != null) {
            Log.d("View Event", "Arguments found in bundle.");
            // Retrieve the argument we passed in the bundle before coming here
            event = (Event) getArguments().getSerializable("event");

            if (event != null) {
                Log.d("View Event", "Event found with name: " + event.getEventName());
                // save the eventID and grab the users to send notifs to
            }
        }
        else {
            Log.e("Organizer Send Notification", "No arguments recieved. Event/bundle did not pass correctly from previous fragment.");
            Toast.makeText(getContext(), "Error passing event.", Toast.LENGTH_SHORT).show();
        }

        String eventID = event.getEventID();
        SwitchCompat waitingListSwitch = binding.switchWaitingList;
        SwitchCompat selectedEntrantsSwitch = binding.switchSelectedEntrants;
        SwitchCompat cancelledEntrantsSwitch = binding.switchCancelledEntrants;

        // Listeners for switches
        waitingListSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedEntrantsSwitch.setChecked(false);
                cancelledEntrantsSwitch.setChecked(false);
                checked_list = "waitlist_pending";
            }
        });
        selectedEntrantsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                waitingListSwitch.setChecked(false);
                cancelledEntrantsSwitch.setChecked(false);
                checked_list = "waitlist_chosen";
            }
        });
        cancelledEntrantsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                waitingListSwitch.setChecked(false);
                selectedEntrantsSwitch.setChecked(false);
                checked_list = "waitlist_declined";
            }
        });
        // back button listener
        binding.backArrow.setOnClickListener(v -> requireActivity().onBackPressed());


        // Set OnClickListener for the Button
        binding.sendNotificationButton.setOnClickListener(v -> {
            String body = binding.bodyInput.getText().toString();
            String title = binding.titleInput.getText().toString();

            // make a switch case/if statements depending on waitlist checks.
            boolean isWaitingListChecked = waitingListSwitch.isChecked();
            boolean isSelectedEntrantsChecked = selectedEntrantsSwitch.isChecked();
            boolean isCancelledEntrantsChecked = cancelledEntrantsSwitch.isChecked();
            if (body.isEmpty() || title.isEmpty()) {
                Toast.makeText(getContext(), "Must write a title/body message!", Toast.LENGTH_SHORT).show();
            } else {
                if (!isWaitingListChecked && !isSelectedEntrantsChecked && !isCancelledEntrantsChecked) {
                    Toast.makeText(getContext(), "Must make a selection first!", Toast.LENGTH_SHORT).show();
                } else {
                    sendNotification(eventID, checked_list, title, body);
                }
            }
        });

            return root;
    }

    private void sendNotification(String eventID, String waitlist, String title, String body) {
        /**
         * Send notifications to entrants in specific waitlists
         * with a title and body message
         */
        fireCon.RetrieveUserIDsFromEvent(eventID, waitlist, new FirestoreCallback() {
            @Override
            public void onSuccess(String[] userIDs) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                for (String userID : userIDs) {
                    Log.d("SendNotification", "User ID: " + userID);

                    // notification data in a Map
                    Map<String, Object> notificationData = new HashMap<>();
                    notificationData.put("title", title);
                    notificationData.put("body", body);

                    db.collection("notifications")
                            .document(userID)
                            .set(notificationData)
                            .addOnSuccessListener(aVoid -> Log.d("SendNotification", "Notification sent to: " + userID))
                            .addOnFailureListener(e -> Log.e("SendNotification", "Failed to send notification to " + userID, e));
                }

                Toast.makeText(getContext(), "Notifications sent successfully!", Toast.LENGTH_SHORT).show();
                binding.bodyInput.setText("");
                binding.titleInput.setText("");
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("SendNotification", "Error fetching user IDs: " + e.getMessage());
                Toast.makeText(getContext(), "Notification delivery failed :(", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}