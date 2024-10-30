package com.example.plannet;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.plannet.Entrant.EntrantDBConnector;
import com.example.plannet.Entrant.EntrantManager;
import com.example.plannet.Entrant.EntrantProfile;
import com.example.plannet.Event.EventWaitlistPending;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.plannet.databinding.ActivityMainBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_events, R.id.navigation_first_time_user)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // hide the nav bar when on welcome screen
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_first_time_user) {
                navView.setVisibility(View.GONE);
            } else {
                navView.setVisibility(View.VISIBLE);
            }
        });

        // Initialize db
        db = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
        //Log.d("MainActivity", "CHECKPOINT");
        //sharedPreferences.edit().remove("unique_id").apply(); // this is just for testing..
        String uniqueID = sharedPreferences.getString("unique_id", null);

        if (uniqueID == null) {
            // run the welcome screen and when user presses "Get Started" button, generate UUID and place it in the db
            // then re-direct to home fragment
            navController.navigate(R.id.navigation_first_time_user);
            //navView.setVisibility(View.GONE);
        }

        //dummy event id for testing
        String eventId = "event_test_123";

        //c reate an instance of EventWaitlistPending for this event
        EventWaitlistPending waitlist = new EventWaitlistPending(eventId);

        //dummy entrant data
        EntrantProfile entrant = new EntrantProfile(
                "John Doe",
                "john.doe@example.com",
                "1234567890",
                "123 Main St",
                "path/to/profilePic.jpg",
                "deviceID123"
        );

        // Create EntrantManager and add entrant to waitlist
        EntrantDBConnector dbConnector = new EntrantDBConnector();
        EntrantManager entrantManager = new EntrantManager(dbConnector);

        // Join waitlist
        entrantManager.joinWaitlist(this, entrant, waitlist);

    }

}