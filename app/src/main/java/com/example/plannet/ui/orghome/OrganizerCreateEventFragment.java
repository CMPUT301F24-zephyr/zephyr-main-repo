package com.example.plannet.ui.orghome;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.plannet.Event.Event;
import com.example.plannet.FirebaseConnector;
import com.example.plannet.MainActivityViewModel;
import com.example.plannet.Organizer.Facility;
import com.example.plannet.QRGenerator;
import com.example.plannet.R;
import com.example.plannet.databinding.FragmentOrganizerCreateEventBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OrganizerCreateEventFragment extends Fragment {
    /**
     * fragment where the organizer creates their event and generate a QR code
     */
    // import data from mainActivityViewModel
    private MainActivityViewModel mainActivityViewModel;
    private String userID1;

//    private String facility;
    private Facility facilityDetails;

    private FragmentOrganizerCreateEventBinding binding;
    // event information
    private EditText nameEdit, priceEdit, maxEntrantsEdit, descriptionEdit, lastRegEdit, runtimeStartEdit, runtimeEndEdit, waitlistMaxEdit;
    private CheckBox waitlistLimitCheckbox, geolocationCheckbox;
    private ImageView qrImageView, poster;

    private Button generateQrButton;
    private Button cancelButton;

    private QRGenerator qrGenerator;

    // Poster image selector stuff
    private Uri selectedImageUri;  // For the selected poster
    private static final int PICK_IMAGE_REQUEST = 1001;  // Copied from EntrantProfileDisplayFragment.java

    //private OrganizerData orgData;
    private FirebaseConnector dbConnector = new FirebaseConnector();

    /**
     * this method is called as soon as the fragment is called
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userID1 = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

    }

    /**
     * this method sets up binding configurations
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
        // Initialize ViewBinding for this fragment
        binding = FragmentOrganizerCreateEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    /**
     * this is where buttonlisteners and other fragment interactions go
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // checks and creates facility if needed
        checkIfFacilityDataIsValid(userID1);

        initializeViews();
        // cancel button
        cancelButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_createEvent_to_home);
        });

        // generate QR button (create event)
        generateQrButton.setOnClickListener(v -> createEvent());
    }

    /**
     * method for initializing views to keep cleaner code structure
     */
    private void initializeViews() {

        nameEdit = binding.nameEdit;
        priceEdit = binding.priceEdit;
        maxEntrantsEdit = binding.maxEntrantsEdit;
        descriptionEdit = binding.description;
        lastRegEdit = binding.lastRegEdit;
        runtimeStartEdit = binding.runtimeStartEdit;
        runtimeEndEdit = binding.runtimeEndEdit;
        waitlistLimitCheckbox = binding.waitlistLimitCheckbox;
        waitlistMaxEdit = binding.waitlistMaxEdit;
        geolocationCheckbox = binding.geolocationCheckbox;
        generateQrButton = binding.generateQrButton;
        poster = binding.addPoster;
        qrGenerator = new QRGenerator();

        cancelButton = binding.cancelButton;

        lastRegEdit.setOnClickListener(view -> showDatePicker(lastRegEdit));
        runtimeStartEdit.setOnClickListener(view -> showDatePicker(runtimeStartEdit));
        runtimeEndEdit.setOnClickListener(view -> showDatePicker(runtimeEndEdit));
        poster.setOnClickListener(view -> openImagePicker());
    }

    /**
     * method for checking if facility is already set up prior to creating event
     * @param userID
     */
    private void checkIfFacilityDataIsValid(String userID) {
        dbConnector.checkIfFacilityDataIsValid(userID,
                facilityMap -> {
                    // Initialize facilityDetails if null
                    if (facilityDetails == null) {
                        facilityDetails = new Facility("", "");
                    }
                    // Set facility details from the fetched data
                    facilityDetails.setFacilityName((String) facilityMap.get("name"));
                    facilityDetails.setFacilityLocation((String) facilityMap.get("location"));

                    // Update the facility name for use in createEvent
                    String facility = facilityDetails.getFacilityName();
                    Log.d("checkIfFacilityDataIsValid", "Facility data loaded successfully: " + facility);
                },
                e -> {
                    // Handle the error or missing data
                    Log.e("checkIfFacilityDataIsValid", "Failed to fetch facility data", e);
                    Toast.makeText(getContext(), "Failed to fetch facility data", Toast.LENGTH_SHORT).show();
                    showCreateFacilityDialog();
                });
    }

    /**
     * method for creating an event if initial tests pass such as valid facility, etc..
     */
    private void createEvent() {
        if (facilityDetails.getFacilityName() == null) {
            Toast.makeText(getContext(), "Facility data is missing. Please create or select a facility.", Toast.LENGTH_SHORT).show();
            return;  // Exit the method to avoid the NullPointerException
        }
        if (lastRegEdit.getText().toString().isEmpty() ||
        runtimeEndEdit.getText().toString().isEmpty() ||
        runtimeStartEdit.getText().toString().isEmpty() ||
        nameEdit.getText().toString().isEmpty() ||
        maxEntrantsEdit.getText().toString().isEmpty() ||
                (waitlistLimitCheckbox.isChecked() && waitlistMaxEdit.getText().toString().isEmpty())) {
            Toast.makeText(getContext(), "Missing input. Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;  // Exit the method to avoid the NullPointerException
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()); // Adjust this format if needed

        // Parse date fields
        Date lastRegDate = parseDate(lastRegEdit.getText().toString(), dateFormat);
        Log.d("createEvent", "createfragment last Reg date = " + lastRegDate);
        Date runtimeStartDate = parseDate(runtimeStartEdit.getText().toString(), dateFormat);
        Date runtimeEndDate = parseDate(runtimeEndEdit.getText().toString(), dateFormat);

        Event event = new Event(
                nameEdit.getText().toString(),                     // eventName
                priceEdit.getText().toString(),                    // price
                Integer.parseInt(maxEntrantsEdit.getText().toString()), // maxEntrants
                waitlistLimitCheckbox.isChecked() ? Integer.parseInt(waitlistMaxEdit.getText().toString()) : 0,  // limitWaitlist
                runtimeStartDate,                                  // eventDate (runtime start)
                lastRegDate,                                       // registrationDateDeadline (last registration)
                runtimeEndDate,                                    // registrationStartDate (runtime end)
                descriptionEdit.getText().toString(),              // description
                geolocationCheckbox.isChecked(),                   // geolocation
                facilityDetails.getFacilityName()                  // facility
        );

        // Generate QR Code and upload
        // Proceed with creating the event to ship to DB
        Map<String, Object> eventDetails = new HashMap<>();
        eventDetails.put("eventName", nameEdit.getText().toString());
        eventDetails.put("eventPoster", event.getImage());
        eventDetails.put("eventPrice", binding.priceEdit.getText().toString());
        eventDetails.put("eventMaxEntrants", Integer.parseInt(maxEntrantsEdit.getText().toString()));
        eventDetails.put("eventLimitWaitlist", waitlistLimitCheckbox.isChecked() ? Integer.parseInt(waitlistMaxEdit.getText().toString()) : 0);
        eventDetails.put("RunTimeStartDate", runtimeStartDate);
        eventDetails.put("LastRegDate", lastRegDate);
        eventDetails.put("RunTimeEndDate", runtimeEndDate);
        eventDetails.put("description", descriptionEdit.getText().toString());
        eventDetails.put("geolocation", geolocationCheckbox.isChecked());
        eventDetails.put("facility", facilityDetails.getFacilityName());

        String eventID = event.getEventID();

        eventDetails.put("eventID", eventID);
        dbConnector.addEventToDB(userID1, eventID, eventDetails);
        if (eventID != null) {
            Bitmap qrBitmap = qrGenerator.generateQRCode(eventID);
            qrGenerator.storeQRCodeEventID(eventID);

            if (qrBitmap != null) {
                showCustomToast(qrBitmap);
                // redirect to homefragment
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_createEvent_to_home);
            } else {
                Toast.makeText(getContext(), "Error creating QR", Toast.LENGTH_SHORT).show();
            }
        }

        // Upload image to firebase Storage
        uploadPoster(event.getImage(), selectedImageUri,
                uri -> Log.d("Org Create Event", "Poster uploaded successfully!"),
                e -> Log.e("Org Create Event", "Error uploading poster to Firebase.", e));
    }

    /**
     * interactive datepicker UI for a better user experience
     * @param editText
     */
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

    /**
     * parses date in a proper format to display
     * @param dateString
     * @param dateFormat
     * @return
     */
    private Date parseDate(String dateString, SimpleDateFormat dateFormat) {
        try {
            return dateString.isEmpty() ? null : dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * method to show toast notification with QR code
     * @param qrBitmap
     */
    private void showCustomToast(Bitmap qrBitmap) {
        // Inflate custom dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_layout, null);

        // Set the QR code image in the dialog layout
        ImageView toastQrImage = layout.findViewById(R.id.toast_qr_image);

        toastQrImage.setImageBitmap(qrBitmap);

        // Build and show the dialog
        new AlertDialog.Builder(requireContext())
                .setView(layout)
                .setCancelable(false) // This prevents dismissal on outside touch or back button
                .setPositiveButton("OK!", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
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
        new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog)
                .setTitle("Create Your Facility")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    try {
                        // retrieve input values
                        String facilityName = facilityNameEdit.getText().toString().trim();
                        String facilityLocation = facilityLocationEdit.getText().toString().trim();

                        // ensure facilityDetails is initialized
                        if (facilityDetails == null) {
                            facilityDetails = new Facility("", "");
                        }

                        // set facility details
                        facilityDetails.setFacilityName(facilityName);
                        facilityDetails.setFacilityLocation(facilityLocation);

                        // add facility details to Firebase
                        dbConnector.addFacilityToDB(userID1, facilityDetails.getFacilityName(), facilityDetails.getFacilityLocation());
                        Toast.makeText(getContext(), "Facility saved!", Toast.LENGTH_SHORT).show();

                    } catch (NullPointerException e) {
                        Toast.makeText(getContext(), "Facility information is incomplete. Please enter all fields.", Toast.LENGTH_SHORT).show();
                        Log.e("OrganizerCreateEvent", "NullPointerException: Missing facility details", e);

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "An error occurred while saving facility info.", Toast.LENGTH_SHORT).show();
                        Log.e("OrganizerCreateEvent", "Exception while saving facility info", e);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    Toast.makeText(getContext(), "Cannot create event without facility info", Toast.LENGTH_SHORT).show();
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                    navController.navigate(R.id.navigation_orghome);
                })
                .show();
    }

    /**
     * Set the poster to an image from the user's library. Copied from entrant profile picture implementation.
     */
    private void openImagePicker(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Used for changing the imageView of the poster to match the selected image. Copied from entrant profile picture implementation.
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            poster.setImageURI(selectedImageUri); // Display the selected image
        }
    }

    private void uploadPoster(String eventImage, Uri uri, OnSuccessListener<Uri> success, OnFailureListener fail) {
        if (uri == null) {
            Log.d("uploadPoster", "No image selected - skipping upload");
            return; // Skip upload if no image is selected
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference path = storageRef.child(eventImage);  // event.getImage = "posters/eventID.jpg"

        path.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> path.getDownloadUrl()
                        .addOnSuccessListener(success)
                        .addOnFailureListener(fail))
                .addOnFailureListener(fail);
    }

    /**
     * destroys view to avoid any errors when exiting fragment
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}