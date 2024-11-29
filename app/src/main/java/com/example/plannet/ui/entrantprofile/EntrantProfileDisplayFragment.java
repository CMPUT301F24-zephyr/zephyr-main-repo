package com.example.plannet.ui.entrantprofile;
import android.content.Intent;
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

import java.util.Map;

/**
 * Fragment for displaying and updating the profile of an entrant
 */
public class EntrantProfileDisplayFragment extends Fragment{
    private EntrantProfileDisplayBinding binding;
    private EntrantProfileViewModel entrantProfileViewModel;
    private String userID;
    private Uri selectedImageUri; // For the selected profile picture
    private static final int PICK_IMAGE_REQUEST = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    double latitude;
    double longitude;

    /**
     *
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = EntrantProfileDisplayBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());


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
        binding.updateButton.setOnClickListener(v -> updateUserProfile());

        // get location button
        binding.buttonGetLocation.setOnClickListener(v -> {
            // get the user location coordinates applicable with google maps API
            getUserLocation();
        });

        // remove pfp button
        binding.removePfp.setOnClickListener(v -> defaultProfilePic(userID));

        return root;
    }

    /**
     * Updates entrant info
     * @param entrantInfo
     */
    private void updateUI(Map<String, Object> entrantInfo) {
        if (entrantInfo != null) {
            binding.firstNameEdit.setText((String) entrantInfo.get("firstName"));
            binding.lastNameEdit.setText((String) entrantInfo.get("lastName"));
            binding.phoneEdit.setText((String) entrantInfo.get("phone"));
            binding.emailEdit.setText((String) entrantInfo.get("email"));

//            // Handle latitude and longitude safely
//            Object latitudeObj = entrantInfo.get("latitude");
//            Object longitudeObj = entrantInfo.get("longitude");
//
//            if (latitudeObj instanceof Double && longitudeObj instanceof Double) {
//                double latitude = (Double) latitudeObj;
//                double longitude = (Double) longitudeObj;
//
//                // Set the location field to display the coordinates
//                binding.locationEdit.setText(latitude + ", " + longitude);
//            } else {
//                // Default message if location is unavailable
//                binding.locationEdit.setText("Location not available");
//            }

            String profilePictureUrl = (String) entrantInfo.get("profilePictureUrl");
            if (profilePictureUrl != null) {
                // Use a library like Glide or Picasso to load the image
                Glide.with(requireContext()).load(profilePictureUrl).into(binding.imageView11);
                // Make "remove picture" button visible
                binding.removePfp.setVisibility(View.VISIBLE);
            }
            else {
                // set profile picture to default
                defaultProfilePic(userID);
            }
        }
    }


    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void defaultProfilePic(String username) {
        binding.removePfp.setVisibility(View.GONE);  // Cannot remove the default picture
        selectedImageUri = null;  // No image is selected, set this to null so default picture is not saved to firebase

        // Delete profile picture from firebase if it exists
        StorageReference ref = FirebaseStorage.getInstance().getReference("profile_pictures/" + username + ".jpg");

        ref.delete()
                .addOnSuccessListener(success -> Log.d("EntrantProfileFragment", "Deleted profile picture from firebase"))
                .addOnFailureListener(fail -> Log.d("EntrantProfileFragment", "No profile picture to delete on firebase"));

        Glide.with(this)
                .load("https://robohash.org/" + username + ".png")
                .placeholder(R.drawable.profile)
                .into(binding.imageView11);
    }

    /**
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            binding.imageView11.setImageURI(selectedImageUri); // Display the selected image
            // Make "remove picture" button visible
            binding.removePfp.setVisibility(View.VISIBLE);
        }
    }

    /**
     * get the user location
     */
    private void getUserLocation() {
        if (requireActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission if not granted
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        // Use the location for your application
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.d("EntrantProfileFragment", "Latitude: " + latitude + ", Longitude: " + longitude);

                        Toast.makeText(requireContext(), "Location updated!", Toast.LENGTH_SHORT).show();

                        // You can now pass these coordinates to Google Maps API or store them in the database
                    } else {
                        Toast.makeText(requireContext(), "Unable to retrieve location. Try again.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("EntrantProfileFragment", "Error getting location", e);
                    Toast.makeText(requireContext(), "Error retrieving location.", Toast.LENGTH_SHORT).show();
                });
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
