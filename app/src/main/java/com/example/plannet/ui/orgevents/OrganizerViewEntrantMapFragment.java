package com.example.plannet.ui.orgevents;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.plannet.R;
import com.example.plannet.databinding.FragmentOrganizerViewEntrantMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;

public class OrganizerViewEntrantMapFragment extends Fragment {

    private FragmentOrganizerViewEntrantMapBinding binding;
    private double latitude;
    private double longitude;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOrganizerViewEntrantMapBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            latitude = getArguments().getDouble("latitude", 0.0);
            longitude = getArguments().getDouble("longitude", 0.0);
        }
        if (latitude == 0.0 || longitude == 0.0) {
            Toast.makeText(getContext(), "Invalid geolocation", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        }

        // Initialize the Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                // Add a marker and move the camera to the specified location
                LatLng entrantLocation = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(entrantLocation).title("Entrant Location"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(entrantLocation, 15));
            });
        }

        binding.backArrow.setOnClickListener(v -> requireActivity().onBackPressed());

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // in case of memory leaks
    }
}