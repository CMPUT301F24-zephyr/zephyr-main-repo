package com.example.plannet.ui.entranthome;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.plannet.Event.Event;
import com.example.plannet.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;

import java.text.SimpleDateFormat;
import java.util.Locale;

//Logic: onCreate -> single scan -> pass to fetch and validate -> display event
// needs an EntrantViewEvent activity that is binded to the xml for it. Wil make onClickListener button there
public class EntrantScanEventFragment extends Fragment {

    FirebaseFirestore firebaseDB;
    ImageView backArrow;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_code_scanner, container, false);

        // Commented this out for now -- Moe
        //BarcodeView barcodeView = view.findViewById(R.id.barcode_scanner);
        firebaseDB = FirebaseFirestore.getInstance();

        //bypass the scanning for now
        view.findViewById(R.id.bypass_scan_button).setOnClickListener(v -> {
            String testHashedData = "codesensei1732082703215";  // Replace with your actual hashed data in Firebase
            fetchEventDetails(testHashedData);
        });
        backArrow = view.findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> requireActivity().onBackPressed());

        // Commented this out for now - Moe
//        // Start scanning
//        barcodeView.decodeSingle(new BarcodeCallback() {
//            @Override
//            public void barcodeResult(BarcodeResult result) {
//                String qrData = result.getText();
//                if (qrData != null) {
//                    fetchEventDetails(qrData);
//                }
//            }
//        });
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
                                if (endTimestamp != null) event.setRegistrationDateDeadline(endTimestamp.toDate());

                                Timestamp regTimestamp = documentSnapshot.getTimestamp("LastRegDate");
                                if (regTimestamp != null) event.setEventDate(regTimestamp.toDate());

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                String startDate = (startTimestamp != null) ? dateFormat.format(startTimestamp.toDate()) : "Date not available";
                                String endDate = (endTimestamp != null) ? dateFormat.format(endTimestamp.toDate()) : "Date not available";
                                String regDate = (regTimestamp != null) ? dateFormat.format(regTimestamp.toDate()) : "Date not available";

                                Bundle eventBundle = new Bundle();
                                eventBundle.putString("eventName", event.getEventName());
                                eventBundle.putString("facility", event.getFacility());

                                //eventBundle.putString("registrationDates", regDate + " - " + startDate + " to " + endDate);

                                //for now I'm passing the codesensei test event, but we gotta change this
                                //field to the actual event ID later
                                eventBundle.putString("eventID", qrData);
                                eventBundle.putString("registrationStartDate", startDate);
                                eventBundle.putString("registrationDateDeadline", regDate);
                                eventBundle.putString("eventDate", endDate);
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

