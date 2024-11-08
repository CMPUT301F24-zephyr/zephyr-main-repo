package com.example.plannet.ui.orghome;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.plannet.R;
import com.example.plannet.databinding.FragmentOrganizerQrcodesListBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to display the hashed QR codes from organizer
 */
public class OrganizerHashedQrListFragment extends Fragment {
    private ArrayList<String> qrCodeHashes = new ArrayList<>();
    private FragmentOrganizerQrcodesListBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOrganizerQrcodesListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String userID1 = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Using DocumentReference collection to retrieve DB info - lab5
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userQrRef = db.collection("users").document(userID1);

        // Fetch createdEvents from Firebase
        userQrRef.get().addOnSuccessListener(documentSnapshot -> {
            if (binding != null) {
                if (documentSnapshot.exists()) {
                    List<String> createdEvents = (List<String>) documentSnapshot.get("createdEvents");
                    if (createdEvents != null) {
                        qrCodeHashes.addAll(createdEvents);
                    }
                    // Set up ArrayAdapter and ListView
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, qrCodeHashes);
                    binding.eventList.setAdapter(adapter);

                    // Set the facility name in the title
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

        // button listener for back button
        binding.backArrow.setOnClickListener(v -> {
            // Functionality for the back arrow
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_organizerEventListFragment_to_navigation_home);
        });
        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}