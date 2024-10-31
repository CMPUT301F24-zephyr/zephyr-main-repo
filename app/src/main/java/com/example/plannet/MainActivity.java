package com.example.plannet;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.plannet.Entrant.EntrantDBConnector;
import com.example.plannet.Entrant.EntrantManager;
import com.example.plannet.Entrant.EntrantProfile;
import com.example.plannet.Event.EventWaitlistPending;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
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
    // QR code initialization
    private Button generateQrButton;
    private QRGenerator qrGenerator;

    // event information
    private EditText nameEdit, priceEdit, maxEntrantsEdit, descriptionEdit, lastRegEdit, runtimeStartEdit, runtimeEndEdit, waitlistMaxEdit;
    private CheckBox waitlistLimitCheckbox, geolocationCheckbox;
    private ImageView qrImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /**
         * initializations for event details
         * should add to a class later
         */
        nameEdit = findViewById(R.id.name_edit);
        priceEdit = findViewById(R.id.price_edit);
        maxEntrantsEdit = findViewById(R.id.max_entrants_edit);
        descriptionEdit = findViewById(R.id.description);
        lastRegEdit = findViewById(R.id.last_reg_edit);
        runtimeStartEdit = findViewById(R.id.runtime_start_edit);
        runtimeEndEdit = findViewById(R.id.runtime_end_edit);
        waitlistLimitCheckbox = findViewById(R.id.waitlist_limit_checkbox);
        geolocationCheckbox = findViewById(R.id.geolocation_checkbox);
        qrImageView = findViewById(R.id.toast_qr_image);
        generateQrButton = findViewById(R.id.generate_qr_button);


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

        // listener for id/generate_qr_button to generate QR and upload QR hashed data to Firebase
        generateQrButton = findViewById(R.id.generate_qr_button);
        qrGenerator = new QRGenerator();

        generateQrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collect data from input fields
                String eventData = collectEventData();
                if (eventData != null) {
                    Bitmap qrBitmap = qrGenerator.generateQRCode(eventData);
                    if (qrBitmap != null) {
                        qrImageView.setImageBitmap(qrBitmap);
                        showCustomToast(qrBitmap);
                    }
                }
            }
        });



        if (uniqueID == null) {
            // run the welcome screen and when user presses "Get Started" button, generate UUID and place it in the db
            // then re-direct to home fragment
            navController.navigate(R.id.navigation_first_time_user);
            //navView.setVisibility(View.GONE);
        }

        /**
         * commented out below sequence so app runs!
         */
//        //dummy event id for testing
//        String eventId = "event_test_123";
//
//        //c reate an instance of EventWaitlistPending for this event
//        EventWaitlistPending waitlist = new EventWaitlistPending(eventId);
//
//        //dummy entrant data
//        EntrantProfile entrant = new EntrantProfile(
//                "John Doe",
//                "john.doe@example.com",
//                "1234567890",
//                "123 Main St",
//                "path/to/profilePic.jpg",
//                "deviceID123"
//        );
//
//        // Create EntrantManager and add entrant to waitlist
//        EntrantDBConnector dbConnector = new EntrantDBConnector();
//        EntrantManager entrantManager = new EntrantManager(dbConnector);
//
//        // Join waitlist
//        entrantManager.joinWaitlist(this, entrant, waitlist);

    }
    @NonNull
    private String collectEventData() {
        // Collect text inputs and convert to a single string
        String name = nameEdit.getText().toString();
        String price = priceEdit.getText().toString();
        String maxEntrants = maxEntrantsEdit.getText().toString();
        String description = descriptionEdit.getText().toString();
        String lastReg = lastRegEdit.getText().toString();
        String runtimeStart = runtimeStartEdit.getText().toString();
        String runtimeEnd = runtimeEndEdit.getText().toString();
        String waitlistMax = waitlistMaxEdit.getText().toString();
        // Collect checkbox values
        boolean waitlistLimit = waitlistLimitCheckbox.isChecked();
        // if waitlist limit is chosen, set maxEntrants to be the waitlistMax --- this might need clarification if waitlistLimit would also mean maxEntrants
        if (waitlistLimit) {
            maxEntrants = waitlistMax;
        }
        boolean geolocation = geolocationCheckbox.isChecked();

        // Format data into a JSON-like string (or any desired format)
        return String.format(
                "Name: %s\nPrice: %s\nMax Entrants: %s\nDescription: %s\nLast Registration: %s\nRuntime Start: %s\nRuntime End: %s\nWaitlist Limit: %b\nGeolocation: %b",
                name, price, maxEntrants, description, lastReg, runtimeStart, runtimeEnd, waitlistLimit, geolocation
        );
    }

    // method to show toast notification with QR code
    // method to show toast notification with QR code
    private void showCustomToast(Bitmap qrBitmap) {
        // Inflate custom toast layout
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_layout,
                (ViewGroup) findViewById(R.id.custom_toast_xml));

        // Set the QR code image in the toast layout
        ImageView toastQrImage = layout.findViewById(R.id.toast_qr_image);
        toastQrImage.setImageBitmap(qrBitmap);

        // Create and show the custom toast
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

}