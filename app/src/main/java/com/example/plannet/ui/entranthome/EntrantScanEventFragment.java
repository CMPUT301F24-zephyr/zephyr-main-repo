package com.example.plannet.ui.entranthome;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.plannet.Event.Event;
import com.example.plannet.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class EntrantScanEventFragment extends Fragment {

    private FirebaseFirestore firebaseDB;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private DecoratedBarcodeView barcodeView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_code_scanner, container, false);
        firebaseDB = FirebaseFirestore.getInstance();

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            initializeScanner(view);
        }

        // Bypass scanning for testing
        view.findViewById(R.id.bypass_scan_button).setOnClickListener(v -> {
            String testHashedData = "codesensei1732082703215";  // Replace with your actual hashed data in Firebase
            fetchEventDetails(testHashedData);
        });

        return view;
    }

    // Initialize the BarcodeView for scanning
    private void initializeScanner(View view) {
        barcodeView = view.findViewById(R.id.barcode_scanner);
        Log.d("QRScan", "BarcodeView initialized.");

        barcodeView.decodeSingle(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result != null && result.getText() != null) {
                    Log.d("QRScan", "Detected barcode: " + result.getText());
                    fetchEventDetails(result.getText());
                } else {
                    Log.d("QRScan", "No valid barcode detected.");
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
                Log.d("QRScan", "Result points: " + resultPoints);
            }
        });

    }

    // fetch event details from Firebase and navigate
    private void fetchEventDetails(String qrData) {
        firebaseDB.collection("events").document(qrData).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Event event = new Event();

                // Firestore document to Event object
                event.setEventName(documentSnapshot.getString("eventName"));
                event.setFacility(documentSnapshot.getString("facility"));
                event.setPrice(documentSnapshot.getString("eventPrice"));
                event.setMaxEntrants(documentSnapshot.getLong("eventMaxEntrants").intValue());
                event.setLimitWaitlist(documentSnapshot.getLong("eventLimitWaitlist").intValue());
                event.setDescription(documentSnapshot.getString("description"));
                event.setGeolocation(documentSnapshot.getBoolean("geolocation"));

                // Convert Timestamps for date fields and set them and check for null
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
                Toast.makeText(requireContext(), "No event found for this QR code.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e("QRScan", "Error fetching event details", e);
            Toast.makeText(requireContext(), "Error fetching event details.", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (barcodeView != null) {
            barcodeView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (barcodeView != null) {
            barcodeView.pause();
        }
    }
}
