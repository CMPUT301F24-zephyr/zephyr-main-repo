package com.example.plannet;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.plannet.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    // view model to transfer userID
    private MainActivityViewModel mainActivityViewModel;

    private ActivityMainBinding binding;
//    private FirebaseConnector db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize SharedViewModel
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = binding.navView;
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_orgprofile, R.id.navigation_notifications,
                R.id.navigation_events, R.id.navigation_first_time_user, R.id.organizerHashedQrListFragment, R.id.navigation_organizer_create_event, R.id.navigation_entrant_home, R.id.navigation_qr_code_scan, R.id.navigation_event_details, R.id.navigation_entrant_profile)
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
        //Log.d("MainActivity", "CHECKPOINT");

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
    }
}

