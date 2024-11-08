package com.example.plannet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.plannet.Event.Event;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Locale;

//Logic: onCreate -> single scan -> pass to fetch and validate -> display event
// needs an EntrantViewEvent activity that is binded to the xml for it. Wil make onClickListener button there
public class EntrantScanEventFragment extends Fragment {

    FirebaseFirestore firebaseDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_code_scanner, container, false);
        BarcodeView barcodeView = view.findViewById(R.id.barcode_scanner);
        firebaseDB = FirebaseFirestore.getInstance();

        view.findViewById(R.id.bypass_scan_button).setOnClickListener(v -> {
            String testHashedData = "codesensei1731040010535";  // Replace with your actual hashed data in Firebase
            fetchEventDetails(testHashedData);
        });

        // Start scanning
        barcodeView.decodeSingle(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                String qrData = result.getText();
                if (qrData != null) {
                    fetchEventDetails(qrData);
                }
            }
        });
        return view;
    }

    // Method to fetch event details from Firebase and start EventDetailsActivity
    void fetchEventDetails(String qrData) {
        firebaseDB.collection("events").document(qrData).get().addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                // Manually create an Event object to handle mismatched field names
                                Event event = new Event();

                                // Set individual fields using document data due to naming incompatibility -- fix this later
                                event.setEventName(documentSnapshot.getString("eventName"));
                                event.setFacility(documentSnapshot.getString("facility"));
                                event.setPrice(documentSnapshot.getString("eventPrice"));
                                event.setMaxEntrants(documentSnapshot.getLong("eventMaxEntrants").intValue());
                                event.setLimitWaitlist(documentSnapshot.getLong("eventLimitWaitlist").intValue());
                                event.setDescription(documentSnapshot.getString("description"));
                                event.setGeolocation(documentSnapshot.getBoolean("geolocation"));

                                // Convert Timestamps for date fields and set them
                                Timestamp startTimestamp = documentSnapshot.getTimestamp("RunTimeStartDate");
                                if (startTimestamp != null) event.setRegistrationStartDate(startTimestamp.toDate());

                                Timestamp endTimestamp = documentSnapshot.getTimestamp("RunTimeEndDate");
                                if (endTimestamp != null) event.setEventDate(endTimestamp.toDate());

                                Timestamp regTimestamp = documentSnapshot.getTimestamp("LastRegDate");
                                if (regTimestamp != null) event.setRegistrationDateDeadline(regTimestamp.toDate());


                                Bundle eventBundle = new Bundle();
                                eventBundle.putString("eventName", event.getEventName());
                                eventBundle.putString("facility", event.getFacility());

                                DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
                                String startDate = (startTimestamp != null) ? dateFormat.format(startTimestamp.toDate()) : "Date not available";
                                String endDate = (endTimestamp != null) ? dateFormat.format(endTimestamp.toDate()) : "Date not available";
                                String regDate = (regTimestamp != null) ? dateFormat.format(regTimestamp.toDate()) : "Date not available";

                                eventBundle.putString("registrationDates", regDate + " - " + startDate + " to " + endDate);
                                eventBundle.putInt("maxEntrants", event.getMaxEntrants());
                                eventBundle.putString("price", event.getPrice());
                                eventBundle.putString("description", event.getDescription());
                                // Navigate to EventDetailsFragment with the event data
                                NavHostFragment.findNavController(EntrantScanEventFragment.this)
                                        .navigate(R.id.action_qrCodeScan_to_eventDetailsFragment, eventBundle);

                            } else {
                                Log.e("QRScan", "No event data found for this event ID.");
                            }
                        }).addOnFailureListener(e -> {
                            Log.e("QRScan", "Error fetching event details", e);
                        });
                    }

                }
