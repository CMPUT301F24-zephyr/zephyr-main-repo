package com.example.plannet.ui.orglottery;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.plannet.Entrant.EntrantProfile;
import com.example.plannet.Event.Event;
import com.example.plannet.Event.LotterySystem;
import com.example.plannet.FirebaseConnector;
import com.example.plannet.Notification.EntrantNotifications;
import com.example.plannet.R;
import com.example.plannet.databinding.FragmentOrganizerRunLotteryBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * this is where the organizer runs the lottery
 */
public class OrganizerRunLottery extends Fragment {

    private FragmentOrganizerRunLotteryBinding binding;
    private Event event = null;  // Default null for error catching
    private FirebaseConnector dbConnector = new FirebaseConnector();
    private String eventId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * onCreateView for what happens when the view is first created.
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
     *      the View
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOrganizerRunLotteryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        LotterySystem lotto = new LotterySystem();

        Log.d("OrganizerRunLottery", "Arguments received: " + getArguments());
        if (getArguments() != null){
            Log.d("OrganizerRunLottery", "Arguments found in bundle.");
            // Retrieve the argument we passed in the bundle before coming here
            event = (Event) getArguments().getSerializable("event");

            if (event != null) {
                Log.d("OrganizerRunLottery", "Event found with name: " + event.getEventName());
                binding.eventName.setText("Event: " + event.getEventName());
            }
        }
        else {
            Log.e("OrganizerRunLottery", "No arguments recieved. Event/bundle did not pass correctly from previous fragment.");
            binding.title.setText("ERROR!");
            Toast.makeText(getContext(), "Error passing event. Please click the back arrow.", Toast.LENGTH_SHORT).show();
        }

        // button listener for back button
        binding.backArrow.setOnClickListener(v -> {
            if (binding != null) {
                try {
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.action_organizerRunLottery_to_organizerLotteryManager);
                } catch (Exception e) {
                    Log.e("OrganizerRunLottery", "Error navigating back", e);
                }
            } else {
                Log.e("OrganizerRunLottery", "Binding is null in back arrow listener.");
            }
        });

        // button listener for clicking "Entrants" button
        binding.drawButton.setOnClickListener(v -> {
            String howMany = binding.editTextNumber.getText().toString();
            if (howMany.isEmpty()){
                Toast.makeText(getContext(), "Please enter an amount.", Toast.LENGTH_SHORT).show();
            } else if (Integer.parseInt(howMany) <= 0) {
                Toast.makeText(getContext(), "Please enter a positive number.", Toast.LENGTH_SHORT).show();
            }
            else if (event != null) {
                dbConnector.getEventWaitlistEntrants(event.getEventID(), "pending", pending -> {
                    if (pending.size() < Integer.parseInt(howMany)) {
                        Toast.makeText(getContext(), "Total waitlist length: " + pending.size() + ". Please enter at most " + pending.size() + ".", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        List<EntrantProfile> selected = new ArrayList<>(lotto.drawParticipants(pending, Integer.parseInt(howMany)));
                        for (EntrantProfile entrant : selected) {
                            dbConnector.moveToWaitlist(event.getEventID(), entrant.getUserId(), "pending", "chosen");

                            // Queue Notification
                            db.collection("events")
                                    .document(event.getEventID()) // Fixed extra parenthesis
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            String eventName = documentSnapshot.getString("eventName");

                                            if (eventName != null && !eventName.isEmpty()) {
                                                EntrantNotifications entrantNotifications = new EntrantNotifications();
                                                entrantNotifications.queueNotification(
                                                        entrant.getUserId(),
                                                        eventName,
                                                        "Congrats! You have been chosen for the event: " + eventName + ". Please respond to your invitation.",
                                                        event.getEventID()
                                                );
                                            } else {
                                                Log.e("OrganizerRunLottery", "Event name is null or empty for eventID: " + event.getEventID());
                                            }
                                        } else {
                                            Log.e("OrganizerRunLottery", "Event document does not exist for eventID: " + event.getEventID());
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("OrganizerRunLottery", "Failed to fetch event details for notification", e);
                                    });
                        }

                        // Notify remaining waitlist entrants
                        // Notify remaining waitlist entrants
                        dbConnector.getEventWaitlistEntrants(event.getEventID(), "pending", remainingWaitlist -> {
                            if (remainingWaitlist != null && !remainingWaitlist.isEmpty()) {
                                db.collection("events")
                                        .document(event.getEventID())
                                        .get()
                                        .addOnSuccessListener(documentSnapshot -> {
                                            if (documentSnapshot.exists()) {
                                                String eventName = documentSnapshot.getString("eventName");

                                                if (eventName != null && !eventName.isEmpty()) {
                                                    for (EntrantProfile remainingEntrant : remainingWaitlist) {
                                                        // Check if the entrant is not in the selected list
                                                        boolean isRejected = selected.stream()
                                                                .noneMatch(chosen -> chosen.getUserId().equals(remainingEntrant.getUserId()));

                                                        if (isRejected) {
                                                            EntrantNotifications entrantNotifications = new EntrantNotifications();
                                                            entrantNotifications.queueNotification(
                                                                    remainingEntrant.getUserId(),
                                                                    eventName,
                                                                    "We're sorry! You were not chosen for the event: " + eventName + ". Thank you for your interest.",
                                                                    null
                                                            );
                                                            Log.d("OrganizerRunLottery", "Notification sent to rejected entrant: " + remainingEntrant.getUserId());
                                                        }
                                                    }
                                                } else {
                                                    Log.e("OrganizerRunLottery", "Event name is null or empty for eventID: " + event.getEventID());
                                                }
                                            } else {
                                                Log.e("OrganizerRunLottery", "Event document does not exist for eventID: " + event.getEventID());
                                            }
                                        })
                                        .addOnFailureListener(e -> Log.e("OrganizerRunLottery", "Failed to fetch event details for rejection notifications", e));
                            } else {
                                Log.d("OrganizerRunLottery", "No remaining entrants in the waitlist.");
                            }
                        });


                        Toast.makeText(getContext(), "Success! " + selected.size() + "Entrants chosen.", Toast.LENGTH_SHORT).show();
                        // Navigate back to home
                        NavController navController = Navigation.findNavController(v);
                        navController.navigate(R.id.action_organizerRunLottery_to_navigation_orghome);
                    }
                });
            }
            else {
                // Somehow the user got to this page and the event did not bundle
                Toast.makeText(getContext(), "Error passing event. Please click the back arrow.", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
