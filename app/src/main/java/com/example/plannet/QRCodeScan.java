package com.example.plannet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;

//Logic: onCreate -> single scan -> pass to fetch and validate -> display event
// needs an EntrantViewEvent activity that is binded to the xml for it. Wil make onClickListener button there
public class QRCodeScan extends AppCompatActivity {

    private BarcodeView barcodeView;
    private FirebaseFirestore firebaseDB;
    private View EntrantViewEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scanner); //TO DO

        barcodeView = findViewById(R.id.barcode_scanner); //TO DO
        firebaseDB = FirebaseFirestore.getInstance();

        // Start scanning?
        barcodeView.decodeSingle(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                String qrData = result.getText();
                if (qrData != null) {
                    //fetchEventDetails(qrData);  // fetch method else looks messy
                }
            }
        });
    }

    // Method to fetch event details from Firebase and start EventDetailsActivity
//    private void fetchEventDetails(String qrData) {
//        String eventId = extractEventId(qrData);
//        if (eventId != null) {
//            Intent intent = new Intent(this, EntrantViewEvent.class); //TO DO?
//            intent.putExtra("eventId", eventId);
//            startActivity(intent);
//        } else {
//            Toast.makeText(this, "Invalid QR code", Toast.LENGTH_SHORT).show();
//        }
//    }

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
