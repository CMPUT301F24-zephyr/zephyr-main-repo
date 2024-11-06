package com.example.plannet.ui.home;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.plannet.ArrayAdapters.OrganizerEventListArrayAdapter;
import com.example.plannet.Event.Event;
import com.example.plannet.Event.EventList;
import com.example.plannet.FirebaseConnector;
import com.example.plannet.MainActivityViewModel;
import com.example.plannet.Organizer.Facility;
import com.example.plannet.Organizer.OrganizerCreatedEvents;
import com.example.plannet.QRGenerator;
import com.example.plannet.R;
import com.example.plannet.databinding.FragmentOrganizerCreateEventBinding;
import com.example.plannet.databinding.FragmentOrganizerQrcodesListBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OrganizerEventListFragment extends Fragment {
    // import data from mainActivityViewModel
    private MainActivityViewModel mainActivityViewModel;
    private EventList eventList;
    private String userID1;

    //    private String facility;
    private Facility facilityDetails;

    private FragmentOrganizerQrcodesListBinding binding;

    //private OrganizerData orgData;
    private FirebaseConnector dbConnector = new FirebaseConnector();

    private ListView eventsList;
    private OrganizerEventListArrayAdapter eventsAdapter;
    private ArrayList<Event> events;
    private TextView title;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize ViewBinding for this fragment
        binding = FragmentOrganizerQrcodesListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        userID1 = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        // checks and creates facility if needed
        checkIfFacilityDataIsValid(userID1);

        eventsList = binding.eventList;
        title = binding.title;

        if (facilityDetails != null){
            title.setText("Facility: " + facilityDetails.getFacilityName());
            // events = dbConnector.getOrganizerEventsList(userID1);  Uncomment this then find out how to get arrayList of events for an organizer
            // eventsAdapter = new OrganizerEventListArrayAdapter(this, events); For some reason this has an error... figure this out
            // eventsList.setAdapter(eventsAdapter); Also uncomment this
        }
        else{
            title.setText("NO FACILITY FOUND");
        }

        // FOR SOME REASON THE APP CRASHES WHEN YOU CLICK THIS ??
        binding.backArrow.setOnClickListener(v -> {
            // Functionality for the back arrow
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_navigation_home_to_organizerEventListFragment);
        });

        return root;
    }

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