package com.example.plannet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.plannet.Event.Event;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

//Logic: onCreate -> single scan -> pass to fetch and validate -> display event
// needs an EntrantViewEvent activity that is binded to the xml for it. Wil make onClickListener button there
public class EntrantScanEventFragment extends Fragment {

    private FirebaseFirestore firebaseDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_qr_code_scanner, container, false);
        BarcodeView barcodeView = view.findViewById(R.id.barcode_scanner);
        firebaseDB = FirebaseFirestore.getInstance();

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
    private void fetchEventDetails(String qrData) {
        firebaseDB.collection("events").document(qrData).get().addOnSuccessListener(documentSnapshot -> {
            Event event = documentSnapshot.toObject(Event.class);
            if (event != null) {
                // Create a bundle with event data
                Bundle eventBundle = new Bundle();
                eventBundle.putString("eventName", event.getEventName());
                eventBundle.putString("facility", event.getFacility());
                eventBundle.putString("registrationDates", event.getRegistrationStartDate() + " - " + event.getRegistrationDateDeadline());
                eventBundle.putInt("maxEntrants", event.getMaxEntrants());
                eventBundle.putString("price", event.getPrice());
                DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
                String dateString = dateFormat.format(event.getEventDate());
                eventBundle.putString("eventDate", dateString);
                eventBundle.putString("description", event.getDescription());

                // Instantiate the fragment and set the arguments
                EventDetailsFragment eventDetailsFragment = new EventDetailsFragment();
                eventDetailsFragment.setArguments(eventBundle);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, eventDetailsFragment)
                        .addToBackStack(null)
                        .commit();

            } else {
                Log.e("QRScan", "No event data found for this event ID.");
            }
        }).addOnFailureListener(e -> {
            Log.e("QRScan", "Error fetching event details", e);
        });
    }


    // Utility method to extract and validate event ID from QR code data
    private String extractEventId(String data) {
        return (data != null && data.matches("[a-zA-Z0-9]+")) ? data : null;
    }

    private void navigateToEventDetailsFragment(Event event) {
        // Use Navigation Component or FragmentTransaction to navigate
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", (Serializable) event);
        NavHostFragment.findNavController(this).navigate(R.id.action_qrCodeScan_to_eventDetailsFragment, bundle);
    }
}
