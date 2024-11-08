package com.example.plannet.ui.orghome;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.plannet.R;
import com.example.plannet.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        // Using DocumentReference collection to retrieve DB info - lab5
        String userID1 = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userQrRef = db.collection("users").document(userID1);


        // Set up buttons with View Binding
        binding.buttonNewEvent.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_home_to_createEvent);
        });

        binding.buttonQrCodes.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_navigation_home_to_organizerEventListFragment);
        });

        binding.buttonDraw.setOnClickListener(v -> {
            // for future reference
            // navController.navigate(R.id.action_homeFragment_to_drawFragment);
        });
        // Setting Facility title if available
        userQrRef.get().addOnSuccessListener(documentSnapshot -> {
            if (binding != null) {
                if (documentSnapshot.exists()) {
                    String facilityName = documentSnapshot.getString("facility.name");
                    if (facilityName != null) {
                        binding.title.setText("Facility: " + facilityName);
                    } else {
                        binding.title.setText("Facility: null");
                    }
                }
            }
        }).addOnFailureListener(e -> {
            // Make a toast of error?
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}