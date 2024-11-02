package com.example.plannet;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.plannet.Event.Event;
import com.example.plannet.Event.EventList;
import com.example.plannet.Organizer.OrganizerData;
import com.example.plannet.databinding.FragmentOrganizerCreateEventBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class OrganizerCreateEventFragment extends Fragment {
    private EventList eventList;

    private FragmentOrganizerCreateEventBinding binding;
    // event information
    private EditText nameEdit, priceEdit, maxEntrantsEdit, descriptionEdit, lastRegEdit, runtimeStartEdit, runtimeEndEdit, waitlistMaxEdit;
    private CheckBox waitlistLimitCheckbox, geolocationCheckbox;
    private ImageView qrImageView;

    private Button generateQrButton;
    private QRGenerator qrGenerator;

    private FirebaseConnector dbConnector = new FirebaseConnector();
    private FirebaseAuth mAuth;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize ViewBinding for this fragment
        binding = FragmentOrganizerCreateEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mAuth = FirebaseAuth.getInstance();
        Log.d("OrganizerCreateEventFragment", String.valueOf(mAuth.getCurrentUser()));
//        // Initialize views using binding
//        nameEdit = binding.nameEdit;
//        priceEdit = binding.priceEdit;
//        maxEntrantsEdit = binding.maxEntrantsEdit;
//        descriptionEdit = binding.description;
//        binding.lastRegEdit.setOnClickListener(view -> showDatePicker(binding.lastRegEdit));
//        binding.runtimeStartEdit.setOnClickListener(view -> showDatePicker(binding.runtimeStartEdit));
//        binding.runtimeEndEdit.setOnClickListener(view -> showDatePicker(binding.runtimeEndEdit));
//        waitlistLimitCheckbox = binding.waitlistLimitCheckbox;
//        geolocationCheckbox = binding.geolocationCheckbox;
//
//        // Initialize EventList
//        eventList = new EventList();
//
//        // listener for id/generate_qr_button to generate QR and upload QR hashed data to Firebase
//        generateQrButton = binding.generateQrButton;
//        qrGenerator = new QRGenerator();
        initializeViews();
        eventList = new EventList();
//        SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
//        String userID = sharedPreferences.getString("unique_id", null);

        // TO DO: figure out how to pass userID from MainActivity through viewmodels
        generateQrButton.setOnClickListener(v -> fetchFacilityDataAndCreateEvent(userID));

        return root;
    }

    // Method for initializing views used for cleaner code
    private void initializeViews() {
        nameEdit = binding.nameEdit;
        priceEdit = binding.priceEdit;
        maxEntrantsEdit = binding.maxEntrantsEdit;
        descriptionEdit = binding.description;
        lastRegEdit = binding.lastRegEdit;
        runtimeStartEdit = binding.runtimeStartEdit;
        runtimeEndEdit = binding.runtimeEndEdit;
        waitlistLimitCheckbox = binding.waitlistLimitCheckbox;
        geolocationCheckbox = binding.geolocationCheckbox;
        generateQrButton = binding.generateQrButton;
        qrGenerator = new QRGenerator();

        lastRegEdit.setOnClickListener(view -> showDatePicker(lastRegEdit));
        runtimeStartEdit.setOnClickListener(view -> showDatePicker(runtimeStartEdit));
        runtimeEndEdit.setOnClickListener(view -> showDatePicker(runtimeEndEdit));
    }

    private void fetchFacilityDataAndCreateEvent(String userID) {
        dbConnector.db.collection("users").document(userID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String facility = documentSnapshot.getString("facility");
                        if (facility == null) {
                            Toast.makeText(getContext(), "Need to create facility first!", Toast.LENGTH_SHORT).show();
                            showCreateFacilityDialog();
                        } else {
                            createEvent(facility);
                        }
                    } else {
                        Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to fetch facility data", Toast.LENGTH_SHORT).show());
    }

    private void createEvent(String facility) {
        String eventData = collectEventData();
        Event event = new Event(
                nameEdit.getText().toString(),       // eventName
                null,                                // image (assuming null for now)
                Integer.parseInt(priceEdit.getText().toString()),  // price
                Integer.parseInt(maxEntrantsEdit.getText().toString()),  // maxEntrants
                waitlistLimitCheckbox.isChecked() ? Integer.parseInt(waitlistMaxEdit.getText().toString()) : 0,  // limitWaitlist
                new Date(),                          // eventDate (use actual date value)
                new Date(),                          // registrationDateDeadline (use actual date value)
                new Date(),                          // registrationStartDate (use actual date value)
                descriptionEdit.getText().toString(), // description
                geolocationCheckbox.isChecked(),      // geolocation
                facility                             // facility fetched from Firestore
        );
        String eventID = event.getEventID();
        if (eventID != null) {
            Bitmap qrBitmap = qrGenerator.generateQRCode(eventID);
            qrGenerator.uploadQRCodeBitmap(qrBitmap, eventID);

            if (qrBitmap != null) {
                showCustomToast(qrBitmap);
                // Navigate to my events for organizer!
                //Navigation.findNavController(view).navigate(R.id.action_OrganizerCreateEventFragment_to_MyEventsFragment);
            } else {
                Toast.makeText(getContext(), "Error creating QR", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDatePicker(EditText editText) {
        // Get the current date as default
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                    // Set selected date to the EditText
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    editText.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    @NonNull
    private String collectEventData() {
        // Collect text inputs and convert to a single string
        String name = binding.nameEdit.getText().toString();
        String price = binding.priceEdit.getText().toString();

        // If price is empty, set to "Free"
        if (price.isEmpty()) {
            price = "Free";
        }

        String maxEntrants = binding.maxEntrantsEdit.getText().toString();
        String description = binding.description.getText().toString();
        String lastReg = binding.lastRegEdit.getText().toString();
        String runtimeStart = binding.runtimeStartEdit.getText().toString();
        String runtimeEnd = binding.runtimeEndEdit.getText().toString();
        String waitlistMax = binding.waitlistMaxEdit.getText().toString();

        // Collect checkbox values
        boolean waitlistLimit = binding.waitlistLimitCheckbox.isChecked();

        // If waitlist limit is chosen, set maxEntrants to the waitlistMax value
        if (waitlistLimit && !waitlistMax.isEmpty()) {
            maxEntrants = waitlistMax;
        }

        boolean geolocation = binding.geolocationCheckbox.isChecked();

        // Format data into a JSON-like string (or any desired format)
        return String.format(
                "Name: %s\nPrice: %s\nMax Entrants: %s\nDescription: %s\nLast Registration: %s\nRuntime Start: %s\nRuntime End: %s\nWaitlist Limit: %b\nGeolocation: %b",
                name, price, maxEntrants, description, lastReg, runtimeStart, runtimeEnd, waitlistLimit, geolocation
        );
    }

    // method to show toast notification with QR code
    private void showCustomToast(Bitmap qrBitmap) {
        // Inflate custom toast layout
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_layout,
                (ViewGroup) binding.getRoot().findViewById(R.id.custom_toast_xml));  // Use binding.getRoot()

        // Set the QR code image in the toast layout
        ImageView toastQrImage = layout.findViewById(R.id.toast_qr_image);
        toastQrImage.setImageBitmap(qrBitmap);

        // Create and show the custom toast
        Toast toast = new Toast(requireContext());  // Use requireContext() for context in a fragment
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    /**
     * Dialog for inputting facility information
     */
    private void showCreateFacilityDialog() {
        // Inflate the custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_create_facility, null);

        // Find the EditText fields in the dialog layout
        EditText facilityNameEdit = dialogView.findViewById(R.id.facility_name_edit);
        EditText facilityLocationEdit = dialogView.findViewById(R.id.facility_location_edit);

        // Build and display the AlertDialog
        new AlertDialog.Builder(getContext())
                .setTitle("Create Your Facility")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    // Retrieve input values
                    String facilityName = facilityNameEdit.getText().toString().trim();
                    String facilityLocation = facilityLocationEdit.getText().toString().trim();

                    if (!facilityName.isEmpty() && !facilityLocation.isEmpty()) {
                        // Save facility data
                        saveFacilityData(facilityName, facilityLocation);
                    } else {
                        Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void saveFacilityData(String facilityName, String location) {
        String userID = "user123";  // Replace with actual user ID
        HashMap<String, Object> facilityData = new HashMap<>();
        facilityData.put("facility", facilityName);
        facilityData.put("location", location);

        dbConnector.addData("users", userID, facilityData,
                aVoid -> Toast.makeText(getContext(), "Facility saved!", Toast.LENGTH_SHORT).show(),
                e -> Toast.makeText(getContext(), "Error saving facility", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Nullify binding to avoid memory leaks
    }
}