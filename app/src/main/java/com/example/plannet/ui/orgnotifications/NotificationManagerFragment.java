package com.example.plannet.ui.orgnotifications;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.plannet.ArrayAdapters.OrganizerEventListArrayAdapter;
import com.example.plannet.Event.Event;
import com.example.plannet.FirebaseConnector;
import com.example.plannet.R;
import com.example.plannet.databinding.FragmentOrganizerNotificationManagerBinding;
import com.example.plannet.databinding.FragmentOrganizerSendNotificationBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * this is where the organizer chooses one of their events to send notifications
 * to its registered entrants
 */
public class NotificationManagerFragment extends Fragment {

    private FragmentOrganizerNotificationManagerBinding binding;
    private FirebaseConnector dbConnector = new FirebaseConnector();
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrganizerNotificationManagerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        String userID1 = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Fetch created Events from Firebase using callback
        dbConnector.getOrganizerEventsList(userID1, events -> {
            // Update the adapter with fetched events
            OrganizerEventListArrayAdapter adapter = new OrganizerEventListArrayAdapter(getContext(), events);
            binding.eventList.setAdapter(adapter);
        });

        // Listener for clicking an element of the list
        binding.eventList.setOnItemClickListener((adapterView, view, i, l) -> {
            // Get the selected event
            Event clickedEvent = (Event) adapterView.getItemAtPosition(i);

            // Create a bundle to access the event from the new page
            Bundle passedEventBundle = new Bundle();
            Log.d("NotificationManager", "Bundling event with name: " + clickedEvent.getEventName());
            passedEventBundle.putSerializable("event", clickedEvent);


            // Navigate to the new fragment
            NavController navController = Navigation.findNavController(view);
            // Don't forget to pass the bundle!
            navController.navigate(R.id.action_navigation_notificationmanager_to_navigation_sendnotification, passedEventBundle);
        });

        return root;
    }

}
