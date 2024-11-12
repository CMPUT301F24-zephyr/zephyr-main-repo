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

/**
 * this is the home fragment for entrants, which is also set to default
 */

public class EntrantHomeFragment extends Fragment {

    private FragmentHomeEntrantBinding binding;
    private EntrantHomeViewModel entrantHomeViewModel;


    /**
     * view hierarchy - onCreate contains viewmodel configurations
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize ViewModel
        entrantHomeViewModel = new ViewModelProvider(this).get(EntrantHomeViewModel.class);
    }

    /**
     * onCreateView is 2nd up in view hierarchy which contains everything
     * for inflators/binding
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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment and set up View Binding
        binding = FragmentHomeEntrantBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * last in the sequence. this contains all misc items such as buttonlisteneres, etc..
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
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
            navController.navigate(R.id.navigation_entrant_profile_display);
        });

        // switch role button
        binding.buttonSwitch2.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_entranthome_to_orghome);
        });
    }

    /**
     * This method is for clear the binding reference when view is destroyed
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // This method is for clear the binding reference when view is destroyed
        binding = null;
    }
}