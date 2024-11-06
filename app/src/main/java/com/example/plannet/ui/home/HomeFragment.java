package com.example.plannet.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.plannet.R;
import com.example.plannet.databinding.FragmentHomeBinding;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set up buttons with View Binding
        binding.buttonNewEvent.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_home_to_createEvent);
        });

        binding.buttonQrCodes.setOnClickListener(v -> {
            // You can replace this Toast with navigation action if needed
            // navController.navigate(R.id.action_homeFragment_to_hashedQrFragment);
        });

        binding.buttonDraw.setOnClickListener(v -> {
            // You can replace this Toast with navigation action if needed
            // navController.navigate(R.id.action_homeFragment_to_drawFragment);
        });

        // Optional: Update UI with ViewModel data (e.g., updating a text field)
        //homeViewModel.getText().observe(getViewLifecycleOwner(), binding.textHome::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}