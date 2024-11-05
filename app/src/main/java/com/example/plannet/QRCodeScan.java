package com.example.plannet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.plannet.Event.Event;
import com.example.plannet.ui.events.EventsViewModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;

//Logic: onCreate -> single scan -> pass to fetch and validate -> display event
// needs an EntrantViewEvent activity that is binded to the xml for it. Wil make onClickListener button there
public class QRCodeScan extends AppCompatActivity {

    private BarcodeView barcodeView;
    FirebaseFirestore firebaseDB;
    private View EntrantViewEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_qr_code_scanner); //TO DO

        barcodeView = findViewById(R.id.barcode_scanner); //TO DO
        firebaseDB = FirebaseFirestore.getInstance();

        // Start scanning?
        barcodeView.decodeSingle(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                String qrData = result.getText();
                if (qrData != null) {
                    fetchEventDetails(qrData);  // fetch method else looks messy
                }
            }
        });
    }

    // Method to fetch event details from Firebase and start EventDetailsActivity
    void fetchEventDetails(String qrData) {
        firebaseDB.collection("events").document(qrData).get().addOnSuccessListener(documentSnapshot -> {
            Event event = documentSnapshot.toObject(Event.class);  // Assuming you have an Event class mapped to your data structure
            if (event != null) {
                //EventsViewModel viewModel = new ViewModelProvider(this).get(EventsViewModel.class);
                //viewModel.setEventDetails(event);
                Log.d("QRScan", "Event Retrieved: " + event.getEventName());
                Log.d("QRScan", "Facility: " + event.getFacility());
                Log.d("QRScan", "Event Date: " + event.getEventDate());
                Log.d("QRScan", "Price: " + event.getPrice());
                Log.d("QRScan", "Max Entrants: " + event.getMaxEntrants());
                Log.d("QRScan", "Description: " + event.getDescription());
            } else {
                Log.e("QRScan", "No event data found for this event ID.");

            }
        }).addOnFailureListener(e -> {
            Log.e("QRScan", "Error fetching event details", e);// Handle error
        });
    }

    // Utility method to extract and validate event ID from QR code data
    private String extractEventId(String data) {
        return (data != null && data.matches("[a-zA-Z0-9]+")) ? data : null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }
}
