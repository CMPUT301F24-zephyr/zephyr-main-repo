package com.example.plannet.ui.orgnotifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.plannet.databinding.FragmentNotificationsBinding;
import com.example.plannet.databinding.FragmentOrganizerSendNotificationBinding;

/**
 * class for org notification manager
 * has to do with sending all types of notifications to entrants
 */
public class NotificationsFragment extends Fragment {

    private FragmentOrganizerSendNotificationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentOrganizerSendNotificationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}