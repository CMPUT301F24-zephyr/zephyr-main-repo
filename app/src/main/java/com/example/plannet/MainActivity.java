package com.example.plannet;

import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.navigation.NavigationBarView;


public class MainActivity extends AppCompatActivity {
    // view model to transfer userID
    private MainActivityViewModel mainActivityViewModel;

    private ActivityMainBinding binding;
//    private FirebaseConnector db;
    private boolean check = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize SharedViewModel
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView navView = binding.navView;
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_orghome, R.id.navigation_orgprofile, R.id.navigation_sendnotification, R.id.navigation_first_time_user, R.id.organizerHashedQrListFragment,
                R.id.navigation_organizer_create_event, R.id.navigation_entranthome, R.id.navigation_qr_code_scan,
                R.id.navigation_event_details, R.id.navigation_entrantprofile, R.id.navigation_entrantnotifications, R.id.navigation_notificationmanager,
                R.id.navigation_entrantprofile, R.id.navigation_event_details, R.id.navigation_entrant_profile_display, R.id.navigation_event_list,
                R.id.organizerViewEventFragment, R.id.organizerViewEntrantsFragment, R.id.organizerViewEntrantInfoFragment)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // Hide the navigation bar on the welcome screen
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_first_time_user|| destination.getId() == R.id.navigation_entrantprofile) {
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

        // Handle unique ID and navigate to welcome screen if necessary
        SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
        String uniqueID = sharedPreferences.getString("unique_id", null);

        // this deletes your locally cached UUID -- for testing...... (must also delete UUID from DB if wanting to start over)
        //sharedPreferences.edit().remove("unique_id").apply();
        // check your local device ID (just for testing)
        //Log.e("MainActivity", "Device ID = " + uniqueID);

        if (uniqueID == null) {
            // Navigate to the welcome screen if no unique ID is found
            navController.navigate(R.id.navigation_first_time_user);
        }
        mainActivityViewModel.setUniqueID(uniqueID);
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

