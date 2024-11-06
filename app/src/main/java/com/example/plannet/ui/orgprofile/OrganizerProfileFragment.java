package com.example.plannet.ui.orgprofile;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.Nullable;

import com.example.plannet.FirebaseConnector;
import com.example.plannet.databinding.FragmentOrganizerProfileBinding;

public class OrganizerProfileFragment extends Fragment {

    private EditText facilityEdit, locationEdit;
    private FragmentOrganizerProfileBinding binding;
    private OrganizerProfileViewModel organizerProfileViewModel;
    private FirebaseConnector db = new FirebaseConnector();
    private String userID;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrganizerProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        userID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        facilityEdit = binding.facilityEdit;
        locationEdit = binding.locationEdit;

        // Set up listener for Save button and call addFacilityToDB()
        binding.buttonContinue.setOnClickListener(v -> {
            db.addFacilityToDB(userID, facilityEdit.getText().toString(), locationEdit.getText().toString());
            //Toast saying successful
            Toast.makeText(getContext(), "Facility saved!", Toast.LENGTH_SHORT).show();
        });


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        organizerProfileViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new OrganizerProfileViewModel(userID);
            }
        }).get(OrganizerProfileViewModel.class);

        // Observe facilityDetails LiveData and update UI
        organizerProfileViewModel.getFacilityDetails().observe(getViewLifecycleOwner(), facility -> {
            if (facility != null) {
                Log.d("OrganizerProfileFragment", "facilityDetails updated: " + facility);
                facilityEdit.setText(facility.getFacilityName());
                locationEdit.setText(facility.getFacilityLocation());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}