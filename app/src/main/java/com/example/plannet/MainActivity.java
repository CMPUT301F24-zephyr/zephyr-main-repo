package com.example.plannet;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.plannet.databinding.ActivityMainBinding;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {
    // view model to transfer userID
    private MainActivityViewModel mainActivityViewModel;

    private ActivityMainBinding binding;
    private FirebaseConnector db;
    // for androidID
    private static final String PREFS_NAME = "app_preferences";
    private static final String KEY_ANDROID_ID = "android_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize SharedViewModel
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = binding.navView;
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications,
                R.id.navigation_events, R.id.navigation_first_time_user)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // Hide the navigation bar on the welcome screen
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_first_time_user) {
                navView.setVisibility(View.GONE);
            } else {
                navView.setVisibility(View.VISIBLE);
            }
        });
        Log.d("MainActivity", "CHECKPOINT");


        /**
         * temporary method for UUID
         */
        // Handle unique ID and navigate to welcome screen if necessary
        SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
        String uniqueID = sharedPreferences.getString("unique_id", null);
        // this deletes your UUID -- for testing......
        //sharedPreferences.edit().remove("unique_id").apply();
        if (uniqueID == null) {
            // Navigate to the welcome screen if no unique ID is found
            navController.navigate(R.id.navigation_first_time_user);
        }
        mainActivityViewModel.setUniqueID(uniqueID);

        /**
         * production method for UUID
         */
//        // Get or store the Android ID
//        String androidID = getStoredAndroidID();
//        db = new FirebaseConnector();
//
//        // Check if the device ID exists and navigate if necessary
//        navigateIfDeviceNotInDB(androidID, navController);
    }

    private String getStoredAndroidID() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String androidID = sharedPreferences.getString(KEY_ANDROID_ID, null);

        if (androidID == null) {
            androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            sharedPreferences.edit().putString(KEY_ANDROID_ID, androidID).apply();
        }
        // Set the unique ID in ViewModel
        String uniqueID = androidID;  // Replace with actual value
        mainActivityViewModel.setUniqueID(uniqueID);
        Log.d("MainActivity", "viewmodel set");
        return androidID;
    }
    private void navigateIfDeviceNotInDB(String deviceID, NavController navController) {
        db.checkIfDeviceExists(deviceID,
                documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        Log.d("MainActivity", "Device ID not found in Firestore, navigating to FirstTimeUserFragment");
                        navController.navigate(R.id.navigation_first_time_user);
                    } else {
                        Log.d("MainActivity", "Device ID exists in Firestore");
                    }
                },
                e -> Log.e("MainActivity", "Error checking device ID in Firestore", e)
        );
    }
}

