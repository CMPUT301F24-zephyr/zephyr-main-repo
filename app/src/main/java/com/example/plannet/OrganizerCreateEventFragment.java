package com.example.plannet;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.plannet.Event.Event;
import com.example.plannet.Event.EventList;
import com.example.plannet.Organizer.OrganizerData;
import com.example.plannet.databinding.FragmentOrganizerCreateEventBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OrganizerCreateEventFragment extends Fragment {
    // import data from mainActivityViewModel
    private MainActivityViewModel mainActivityViewModel;
    private EventList eventList;
    private String userID1;

    private String facility;
    private FragmentOrganizerCreateEventBinding binding;
    // event information
    private EditText nameEdit, priceEdit, maxEntrantsEdit, descriptionEdit, lastRegEdit, runtimeStartEdit, runtimeEndEdit, waitlistMaxEdit;
    private CheckBox waitlistLimitCheckbox, geolocationCheckbox;
    private ImageView qrImageView;

    private Button generateQrButton;
    private QRGenerator qrGenerator;

    //private OrganizerData orgData;
    private FirebaseConnector dbConnector = new FirebaseConnector();
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize ViewBinding for this fragment
        binding = FragmentOrganizerCreateEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Get SharedViewModel instance
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        mainActivityViewModel.getUniqueID().observe(getViewLifecycleOwner(), userID -> {
            if (userID != null) {
                Log.d("OrganizerCreateEventFragment", "Received userID: " + userID);
                userID1 = userID;

                // checks and creates facility if needed
                checkIfFacilityDataIsValid(userID);
            }
        });


        initializeViews();
        eventList = new EventList();

        generateQrButton.setOnClickListener(v -> createEvent(facility));

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

    private void checkIfFacilityDataIsValid(String userID) {
        dbConnector.db.collection("users").document(userID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Check if facility is a String or a Map on DB
                        Object facilityObj = documentSnapshot.get("facility");
                        if (facilityObj instanceof Map) {
                            Map<String, Object> facilityMap = (Map<String, Object>) facilityObj;
                            facility = (String) facilityMap.get("name");
                        } else {
                            Toast.makeText(getContext(), "Need to create facility first!", Toast.LENGTH_SHORT).show();
                            showCreateFacilityDialog();
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to fetch facility data", Toast.LENGTH_SHORT).show());
    }

    private void createEvent(String facility) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()); // Adjust this format if needed

        // Parse date fields
        Date lastRegDate = parseDate(lastRegEdit.getText().toString(), dateFormat);
        Date runtimeStartDate = parseDate(runtimeStartEdit.getText().toString(), dateFormat);
        Date runtimeEndDate = parseDate(runtimeEndEdit.getText().toString(), dateFormat);

        // Check if any required fields are null, showing a toast to inform the user
        if (lastRegDate == null || runtimeStartDate == null || runtimeEndDate == null) {
            Toast.makeText(getContext(), "Please enter all required dates in the correct format (dd/MM/yyyy)", Toast.LENGTH_SHORT).show();
            return; // Exit early if parsing failed
        }

        Event event = new Event(
                nameEdit.getText().toString(),                     // eventName
                null,                                              // image (assuming null for now)
                binding.priceEdit.getText().toString(),            // price
                Integer.parseInt(maxEntrantsEdit.getText().toString()), // maxEntrants
                waitlistLimitCheckbox.isChecked() ? Integer.parseInt(waitlistMaxEdit.getText().toString()) : 0,  // limitWaitlist
                runtimeStartDate,                                  // eventDate (runtime start)
                lastRegDate,                                       // registrationDateDeadline (last registration)
                runtimeEndDate,                                    // registrationStartDate (runtime end)
                descriptionEdit.getText().toString(),              // description
                geolocationCheckbox.isChecked(),                   // geolocation
                facility                                           // facility
        );

        // Generate QR Code and upload
        // Proceed with creating the event to ship to DB
        Map<String, Object> eventDetails = new HashMap<>();
        eventDetails.put("eventName", nameEdit.getText().toString());
        eventDetails.put("eventPoster", null); // null for now!
        eventDetails.put("eventPrice", binding.priceEdit.getText().toString());
        eventDetails.put("eventMaxEntrants", Integer.parseInt(maxEntrantsEdit.getText().toString()));
        eventDetails.put("eventLimitWaitlist", waitlistLimitCheckbox.isChecked() ? Integer.parseInt(waitlistMaxEdit.getText().toString()) : 0);
        eventDetails.put("RunTimeStartDate", runtimeStartDate);
        eventDetails.put("LastRegDate", lastRegDate);
        eventDetails.put("RunTimeEndDate", runtimeEndDate);
        eventDetails.put("description", descriptionEdit.getText().toString());
        eventDetails.put("geolocation", geolocationCheckbox.isChecked());
        eventDetails.put("facility", facility);

        String eventID = event.getEventID();

        eventDetails.put("eventID", eventID);
        dbConnector.addEventToDB(userID1, eventID, eventDetails);
        if (eventID != null) {
            Bitmap qrBitmap = qrGenerator.generateQRCode(eventID);
            qrGenerator.storeQRCodeEventID(eventID);

            if (qrBitmap != null) {
                showCustomToast(qrBitmap);
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

    private Date parseDate(String dateString, SimpleDateFormat dateFormat) {
        try {
            return dateString.isEmpty() ? null : dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // method to show toast notification with QR code
    private void showCustomToast(Bitmap qrBitmap) {
        // Inflate custom toast layout
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_layout,
                (ViewGroup) binding.getRoot().findViewById(R.id.custom_toast_xml));

        // Set the QR code image in the toast layout
        ImageView toastQrImage = layout.findViewById(R.id.toast_qr_image);
        toastQrImage.setImageBitmap(qrBitmap);

        // Create and show the custom toast
        Toast toast = new Toast(requireContext());
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

        //dialogView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.backgroundColor));

        // Build and display the AlertDialog
        new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog)
                .setTitle("Create Your Facility")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    // Retrieve input values
                    String facilityName = facilityNameEdit.getText().toString().trim();
                    String facilityLocation = facilityLocationEdit.getText().toString().trim();

                    if (!facilityName.isEmpty() && !facilityLocation.isEmpty()) {
                        // Save facility data
                        //saveFacilityData(facilityName, facilityLocation);
                        //checkIfFacilityDataIsValid(userID1);

                        // add facility to firebase
                        dbConnector.addFacilityToDB(userID1, facilityName, facilityLocation);
                    } else {
                        Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    Toast.makeText(getContext(), "Cannot create event without facility info", Toast.LENGTH_SHORT).show();
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                    navController.navigate(R.id.navigation_home);
                })
                .show();
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}