package com.example.plannet.ui.orgnotifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.plannet.Event.Event;
import com.example.plannet.FirebaseConnector;
import com.example.plannet.FirestoreCallback;
import com.example.plannet.databinding.FragmentOrganizerSendNotificationBinding;

/**
 * class for org notification manager
 * has to do with sending all types of notifications to entrants
 */
public class SendNotificationFragment extends Fragment {

    private FragmentOrganizerSendNotificationBinding binding;
    FirebaseConnector fireCon = new FirebaseConnector();
    private Event event = null;  // Default null for error catching


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
        CheckBox waitingListCheckBox = binding.checkboxWaitingList;
        CheckBox selectedEntrantsCheckBox = binding.checkboxSelectedEntrants;
        CheckBox cancelledEntrantsCheckBox = binding.checkboxCancelledEntrants;


        // back button listener
        binding.backArrow.setOnClickListener(v -> requireActivity().onBackPressed());


        // Set OnClickListener for the Button
        binding.sendNotificationButton.setOnClickListener(v -> {
            String body = binding.messageInput.getText().toString();

            // make a switch case/if statements depending on waitlist checks.
            boolean isWaitingListChecked = waitingListCheckBox.isChecked();
            boolean isSelectedEntrantsChecked = selectedEntrantsCheckBox.isChecked();
            boolean isCancelledEntrantsChecked = cancelledEntrantsCheckBox.isChecked();
            if (body.isEmpty()) {
                Toast.makeText(getContext(), "Must write a body message!", Toast.LENGTH_SHORT).show();
            } else {

                if (isWaitingListChecked) {
                    // send notif to pending users (ones who just signed up)
                    sendNotification(eventID, "waitlist_pending", body);
                }
                if (isSelectedEntrantsChecked) {
                    // send notif to selected/lottery winners entrants
                    sendNotification(eventID, "waitlist_selected", body);
                }
                if (isCancelledEntrantsChecked) {
                    // send notif to cancelled/rejected entrants
                    // this is simply waitlist_pending - waitlist_selected
                    sendNotification(eventID, "waitlist_cancelled", body);
                }
                if (!isWaitingListChecked && !isSelectedEntrantsChecked && !isCancelledEntrantsChecked){
                    Toast.makeText(getContext(), "Must make a selection first!", Toast.LENGTH_SHORT).show();
                }
            }
        });

            return root;
    }

    private void sendNotification(String eventID, String waitlist, String body) {
        // Send notifications to entrants in specific waitlists
        fireCon.RetrieveAndStoreUserIDs(eventID, waitlist, body, new FirestoreCallback() {
            @Override
            public void onSuccess(String[] userIDs) {
                // Handle success case
                for (String userID : userIDs) {
                    Log.d("SendNotification", "User ID: " + userID);
                }
                Toast.makeText(getContext(), "Notification success!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure case
                Log.e("SendNotification", "Error fetching user IDs: " + e.getMessage());
                Toast.makeText(getContext(), "Notification delivery failed", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}