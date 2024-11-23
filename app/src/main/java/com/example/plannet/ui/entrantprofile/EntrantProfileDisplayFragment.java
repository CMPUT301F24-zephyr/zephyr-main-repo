package com.example.plannet.ui.entrantprofile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.plannet.Entrant.EntrantDBConnector;
import com.example.plannet.R;
import com.example.plannet.databinding.EntrantProfileDisplayBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.bumptech.glide.Glide;

import java.util.Map;


public class EntrantProfileDisplayFragment extends Fragment{
    private EntrantProfileDisplayBinding binding;
    private EntrantProfileViewModel entrantProfileViewModel;
    private String userID;
    private Uri selectedImageUri; // For the selected profile picture
    private static final int PICK_IMAGE_REQUEST = 1001;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = EntrantProfileDisplayBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        userID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);


        entrantProfileViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new EntrantProfileViewModel(userID);
            }
        }).get(EntrantProfileViewModel.class);


        entrantProfileViewModel.getEntrantDetails().observe(getViewLifecycleOwner(), this::updateUI);

        //set click listener for the profile picture
        binding.imageView11.setOnClickListener(v -> openImagePicker());

        // update button
        binding.button.setOnClickListener(v -> updateUserProfile());

        return root;
    }

    private void updateUI(Map<String, Object> entrantInfo) {
        if (entrantInfo != null) {
            binding.firstNameEdit.setText((String) entrantInfo.get("firstName"));
            binding.lastNameEdit.setText((String) entrantInfo.get("lastName"));
            binding.phoneEdit.setText((String) entrantInfo.get("phone"));
            binding.emailEdit.setText((String) entrantInfo.get("email"));

            // Handle latitude and longitude safely
            Object latitudeObj = entrantInfo.get("latitude");
            Object longitudeObj = entrantInfo.get("longitude");

            if (latitudeObj instanceof Double && longitudeObj instanceof Double) {
                double latitude = (Double) latitudeObj;
                double longitude = (Double) longitudeObj;

                // Set the location field to display the coordinates
                binding.locationEdit.setText(latitude + ", " + longitude);
            } else {
                // Default message if location is unavailable
                binding.locationEdit.setText("Location not available");
            }

            String profilePictureUrl = (String) entrantInfo.get("profilePictureUrl");
            if (profilePictureUrl != null) {
                // Use a library like Glide or Picasso to load the image
                Glide.with(requireContext()).load(profilePictureUrl).into(binding.imageView11);
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            binding.imageView11.setImageURI(selectedImageUri); // Display the selected image
        }
    }

    private void updateUserProfile() {
        String firstName = binding.firstNameEdit.getText().toString();
        String lastName = binding.lastNameEdit.getText().toString();
        String phone = binding.phoneEdit.getText().toString();
        String email = binding.emailEdit.getText().toString();

        // Fetch the user's location
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        if (requireActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        if (selectedImageUri != null) {
                            // Upload the profile picture to Firebase Storage
                            String fileName = "profile_pictures/" + userID + ".jpg";
                            StorageReference storageRef = FirebaseStorage.getInstance().getReference(fileName);

                            storageRef.putFile(selectedImageUri)
                                    .addOnSuccessListener(taskSnapshot -> {
                                        // Get the download URL and save it to the database
                                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                            String profilePictureUrl = uri.toString();
                                            saveUserInfoToDatabase(firstName, lastName, phone, email, profilePictureUrl, latitude, longitude);
                                        });
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Failed to upload profile picture.", Toast.LENGTH_SHORT).show();
                                        Log.e("EntrantProfileFragment", "Error uploading profile picture", e);
                                    });
                        } else {
                            // Save user info without updating the profile picture
                            saveUserInfoToDatabase(firstName, lastName, phone, email, null, latitude, longitude);
                        }
                    } else {
                        Toast.makeText(getContext(), "Unable to retrieve location. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("EntrantProfileFragment", "Error getting location", e);
                    Toast.makeText(getContext(), "Error retrieving location.", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveUserInfoToDatabase(String firstName, String lastName, String phone, String email, String profilePictureUrl, double latitude, double longitude) {
        EntrantDBConnector entrantDBConnector = new EntrantDBConnector();
        entrantDBConnector.saveUserInfo(userID, firstName, lastName, phone, email, profilePictureUrl, latitude, longitude,
                aVoid -> {
                    Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();

                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(R.id.navigation_entranthome);
                },
                e -> {
                    Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }




}
