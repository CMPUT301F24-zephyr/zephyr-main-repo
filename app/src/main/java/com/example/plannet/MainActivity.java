package com.example.plannet;

import android.content.Intent;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.example.plannet.Notification.NotificationService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.plannet.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;


public class MainActivity extends AppCompatActivity {
    /**
     * the main activity of the app (the heart of the app)
     * where everything that constantly runs is here
     */
    // view model to transfer userID
    private MainActivityViewModel mainActivityViewModel;
    FirebaseConnector db = new FirebaseConnector();
    private ActivityMainBinding binding;
//    private FirebaseConnector db;
    private boolean check = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize SharedViewModel
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        // Start the notification service
        Intent serviceIntent = new Intent(this, NotificationService.class);
        startService(serviceIntent);

        Log.d("MainActivity", "NotificationHandler service started.");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView navView = binding.navView;
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(navView, navController);

        // Hide the navigation bar on the welcome screen
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_first_time_user|| destination.getId() == R.id.navigation_entrantprofile || destination.getId() == R.id.navigation_adminhome) {
                navView.setVisibility(View.GONE);
            } else {
                navView.setVisibility(View.VISIBLE);
            }

            // Nav tabs configuration depending on which role (later add admin as well)
            if (destination.getId() == R.id.navigation_entranthome) {
                if (check != true) {
                    // Customize tabs for EntrantHomeFragment
                    showEntrantTabs();
                    check = true;
                }
            } else if (destination.getId() == R.id.navigation_orghome) {
                if (check != false) {
                    // Show Org navTabs
                    showOrgTabs();
                    check = false;
                }
            }
        });
        //Log.d("MainActivity", "CHECKPOINT");

        String uniqueID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        db.checkIfDeviceIDinDB("users", uniqueID, new CheckDeviceIDCallback() {
            @Override
            public void onDeviceIDFound() {
                Log.d("MainActivity", "Device ID found in database.");
                mainActivityViewModel.setUniqueID(uniqueID);
                navController.navigate(R.id.navigation_entranthome);
            }

            @Override
            public void onDeviceIDNotFound() {
                Log.d("MainActivity", "Device ID not found in database. Redirecting to first-time user screen.");
                navController.navigate(R.id.navigation_first_time_user);
            }

            @Override
            public void onError(Exception e) {
                Log.e("MainActivity", "Error checking Device ID in database.", e);
                Toast.makeText(MainActivity.this, "Error verifying user. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * method to enable entrant XML menu
     */
    private void showEntrantTabs() {
        // Show Entrant menu xml
        BottomNavigationView bottomNav = binding.navView;
        bottomNav.getMenu().clear(); // Clear current menu
        bottomNav.inflateMenu(R.menu.bottom_nav_menu_entrant);
        bottomNav.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);

    }

    /**
     * method to enable org XML menu
     */
    private void showOrgTabs() {
        // Show Org menu xml
        BottomNavigationView bottomNav = binding.navView;
        bottomNav.getMenu().clear(); // Clear current menu
        bottomNav.inflateMenu(R.menu.bottom_nav_menu_org);
        bottomNav.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);

    }
}

