package com.example.plannet.ui.home;

import androidx.fragment.app.Fragment;
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
import com.example.plannet.databinding.HomescreenEntrantFragmentBinding;

public class EntrantHomeFragment extends Fragment {

    private HomescreenEntrantFragmentBinding binding;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = HomescreenEntrantFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set up buttons with View Binding
        binding.scanQrButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_home_to_qrCodeScan);
        });

        binding.viewEventsButton.setOnClickListener(v -> {
            //
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}