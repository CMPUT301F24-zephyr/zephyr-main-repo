package com.example.plannet.ui.orgnotifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.plannet.databinding.FragmentOrganizerNotificationManagerBinding;
import com.example.plannet.databinding.FragmentOrganizerSendNotificationBinding;

public class NotificationManagerFragment extends Fragment {
    private FragmentOrganizerNotificationManagerBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrganizerNotificationManagerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }

}
