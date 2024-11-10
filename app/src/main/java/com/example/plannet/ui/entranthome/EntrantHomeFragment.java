package com.example.plannet.ui.entranthome;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.plannet.R;
import com.example.plannet.databinding.FragmentHomeEntrantBinding;
import com.example.plannet.ui.entranthome.EntrantHomeViewModel;

public class EntrantHomeFragment extends Fragment {

    private FragmentHomeEntrantBinding binding;
    private EntrantHomeViewModel entrantHomeViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize ViewModel
        entrantHomeViewModel = new ViewModelProvider(this).get(EntrantHomeViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment and set up View Binding
        binding = FragmentHomeEntrantBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.scanQrButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_home_to_qrCodeScan);
        });

        binding.viewEventsButton.setOnClickListener(v -> {
            // Add any action for this button here
        });

        binding.viewProfileButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_home_to_profile);
        });

        // switch role button
        binding.buttonSwitch2.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_entranthome_to_orghome);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // This method is for clear the binding reference when view is destroyed
        binding = null;
    }
}