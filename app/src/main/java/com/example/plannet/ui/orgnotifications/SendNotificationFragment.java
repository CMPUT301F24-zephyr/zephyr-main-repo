package com.example.plannet.ui.orgnotifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.plannet.databinding.FragmentOrganizerSendNotificationBinding;

/**
 * class for org notification manager
 * has to do with sending all types of notifications to entrants
 */
public class SendNotificationFragment extends Fragment {

    private FragmentOrganizerSendNotificationBinding binding;
    String title = "PlanNet";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrganizerSendNotificationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.sendNotificationButton.setOnClickListener(v -> {
            String body = binding.messageInput.getText().toString();

            // placeholder for when we figure out waitlists in firebase
//            String recipient =
            String recipient = "some_waitlist";

            if (body.isEmpty()) {
                Toast.makeText(getContext(), "Must write a body message!", Toast.LENGTH_SHORT).show();
            } else {
                sendNotification(title, body, recipient);
            }
        });

        return root;
    }

    private void sendNotification(String title, String body, String waitlist) {
        // Send notifications to entrants in specific waitlists
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}