package com.example.plannet.ui.entrantprofile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.plannet.Entrant.EntrantDBConnector;
import com.example.plannet.Entrant.EntrantProfile;
import com.example.plannet.FirebaseConnector;
import com.example.plannet.R;
import com.example.plannet.databinding.FragmentEntrantProfileBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

/**
 * Entrant profile fragment for user data collection
 */
public class EntrantProfileFragment extends Fragment {

    private EditText firstNameEdit, lastNameEdit, phoneEdit, emailEdit;
    private FragmentEntrantProfileBinding binding;
    private EntrantProfileViewModel entrantProfileViewModel;
    private String userID;
    private FusedLocationProviderClient fusedLocationClient;
    double latitude;
    double longitude;
    private FirebaseConnector db = new FirebaseConnector();
    private Uri selectedImageUri; // To store profile picture URI

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here
     * @return The View for the fragment's UI
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEntrantProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        userID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        firstNameEdit = binding.firstNameEdittext;
        lastNameEdit = binding.lastNameEdittext;
        phoneEdit = binding.phoneEdittext;
        emailEdit = binding.emailEdittext;

        // Initialize ViewModel
        entrantProfileViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new EntrantProfileViewModel(userID);
            }
        }).get(EntrantProfileViewModel.class);

        binding.buttonSave.setOnClickListener(v -> saveUserProfile());

        // get location button
        binding.buttonLocationserv.setOnClickListener(v -> getUserLocation());

        binding.imageView5.setOnClickListener(v -> openImagePicker());

        return root;
    }

    private static final int PICK_IMAGE_REQUEST = 1001;

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Handles the result of the image picker intent.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult()
     * @param resultCode  The integer result code returned by the child activity through its setResult()
     * @param data        An Intent, which can return result data to the caller
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            binding.imageView5.setImageURI(selectedImageUri);
            Log.d("ProfilePictureUpload", "Selected URI: " + selectedImageUri);
        }
    }

    private void saveUserProfile() {
        String firstName = firstNameEdit.getText().toString();
        String lastName = lastNameEdit.getText().toString();
        String phone = phoneEdit.getText().toString();
        String email = emailEdit.getText().toString();

        // Check if a profile picture has been selected
        if (selectedImageUri != null) {
            try {
                getContext().getContentResolver().openInputStream(selectedImageUri).close();
                Log.d("ProfilePictureUpload", "URI is accessible: " + selectedImageUri);
            } catch (Exception e) {
                Log.e("ProfilePictureUpload", "URI is not accessible", e);
                Toast.makeText(getContext(), "Unable to access the selected image.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Upload the image to Firebase Storage
            String fileName = "profile_pictures/" + userID + ".jpg";
            Log.d("ProfilePictureUpload", "File name for upload: " + fileName);
            StorageReference storageRef = FirebaseStorage.getInstance().getReference(fileName);

            storageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the download URL of the uploaded image
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String profilePictureUrl = uri.toString();
                            // Save user info with the profile picture URL
                            saveUserInfoToDatabase(firstName, lastName, phone, email, profilePictureUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to upload profile picture.", Toast.LENGTH_SHORT).show();
                        Log.e("EntrantProfileFragment", "Error uploading profile picture", e);
                    });
        } else {
            // Save user info without updating the profile picture
            saveUserInfoToDatabase(firstName, lastName, phone, email, null);
        }
    }

    private void saveUserInfoToDatabase(String firstName, String lastName, String phone, String email, String profilePictureUrl) {
        EntrantDBConnector entrantDBConnector = new EntrantDBConnector();
        entrantDBConnector.saveUserInfo(userID, firstName, lastName, phone, email, profilePictureUrl,
                aVoid -> {
                    EntrantProfile entrantProfile = EntrantProfile.getInstance(
                            requireContext(),
                            userID,
                            firstName + " " + lastName,
                            email,
                            phone,
                            profilePictureUrl, // Includes the profile picture URL
                            true
                    );

                    Log.d("EntrantProfileFragment", "EntrantProfile initialized: " + entrantProfile.getName());
                    // Display a success message
                    Toast.makeText(getContext(), "Profile saved successfully", Toast.LENGTH_SHORT).show();

                    // Navigate to Entrant Home
                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(R.id.action_entrantProfile_to_home);
                },
                e -> {
                    Toast.makeText(getContext(), "Failed to save profile", Toast.LENGTH_SHORT).show();
                    Log.e("EntrantProfileFragment", "Error saving profile", e);
                });
    }
    /**
     * get the user location
     */
    private void getUserLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        if (requireActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission if not granted
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(requireActivity(), location -> {
                    //Log.e("EntrantProfileLoca", "location = " + location);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.d("EntrantProfileFragment", "Latitude: " + latitude + ", Longitude: " + longitude);
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("latitude", latitude);
                        userData.put("longitude", longitude);
                        //Log.e("EntrantProfileLoca", "Checking location permissions");

                        db.updateUserLocation(
                                userID,
                                userData,
                                success -> {
                                    Log.d("UpdateLocation", "User location updated successfully!");
                                },
                                failure -> {
                                    Log.e("UpdateLocation", "Failed to update user location", failure);
                                }
                        );
                        Toast.makeText(requireContext(), "Location updated!", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(requireContext(), "Unable to retrieve location. Try again.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("EntrantProfileFragment", "Error getting location", e);
                    Toast.makeText(requireContext(), "Error retrieving location.", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Method for setting the binding to null when the view is destroyed to avoid errors.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}