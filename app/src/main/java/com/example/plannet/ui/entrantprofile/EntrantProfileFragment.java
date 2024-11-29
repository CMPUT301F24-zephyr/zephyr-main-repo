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
import com.example.plannet.R;
import com.example.plannet.databinding.FragmentEntrantProfileBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import android.location.Location;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

/**
 * Entrant profile fragment for user data collection
 */
public class EntrantProfileFragment extends Fragment {

    private EditText firstNameEdit, lastNameEdit, phoneEdit, emailEdit;
    private FragmentEntrantProfileBinding binding;
    private EntrantProfileViewModel entrantProfileViewModel;
    private String userID;
    private Uri selectedImageUri; //to store pfp URI
    private FusedLocationProviderClient fusedLocationClient;


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
        binding = FragmentEntrantProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        userID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        firstNameEdit = binding.firstNameEdittext;
        lastNameEdit = binding.lastNameEdittext;
        phoneEdit = binding.phoneEdittext;
        emailEdit = binding.emailEdittext;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        //https://developer.android.com/topic/libraries/architecture/viewmodel
        // Initialize ViewModel
        entrantProfileViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new EntrantProfileViewModel(userID);
            }
        }).get(EntrantProfileViewModel.class);

        //entrantProfileViewModel.getEntrantDetails().observe(getViewLifecycleOwner(), this::updateUI);

        binding.buttonLocationserv.setOnClickListener(v -> {
            // get the user location coordinates applicable with google maps API
            getUserLocation();
        });

        binding.buttonSave.setOnClickListener(v -> saveUserProfile());

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            binding.imageView5.setImageURI(selectedImageUri);
            Log.d("ProfilePictureUpload", "Selected URI: " + selectedImageUri);
        }


    }


//    private void updateUI(Map<String, Object> entrantInfo) {
//        if (entrantInfo != null) {
//            firstNameEdit.setText((String) entrantInfo.get("firstName"));
//            lastNameEdit.setText((String) entrantInfo.get("lastName"));
//            phoneEdit.setText((String) entrantInfo.get("phone"));
//            emailEdit.setText((String) entrantInfo.get("email"));
//        }
//    }

    private void saveUserProfile() {
        String firstName = firstNameEdit.getText().toString();
        String lastName = lastNameEdit.getText().toString();
        String phone = phoneEdit.getText().toString();
        String email = emailEdit.getText().toString();

        // Fetch user location
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        if (requireActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

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
                                            // Save user info with the profile picture URL and location
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
                    EntrantProfile entrantProfile = EntrantProfile.getInstance(
                            requireContext(),
                            userID,
                            firstName + " " + lastName,
                            email,
                            phone,
                            profilePictureUrl, // Includes the profile picture URL
                            true, // Assuming notifications are enabled by default
                            latitude,
                            longitude
                    );

                    Log.d("EntrantProfileFragment", "EntrantProfile initialized: " + entrantProfile.getName());
                    // Display a success message
                    Toast.makeText(getContext(), "Profile saved successfully", Toast.LENGTH_SHORT).show();

                    // Save unique ID to SharedPreferences to mark the user as registered
                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString("unique_id", userID).apply();

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
        if (requireActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission if not granted
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        // Use the location for your application
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Log.d("EntrantProfileFragment", "Latitude: " + latitude + ", Longitude: " + longitude);

                        Toast.makeText(requireContext(), "Location saved!", Toast.LENGTH_SHORT).show();

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


    /**
     * Method for setting the binding to null when the view is destroyed to avoid errors.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
