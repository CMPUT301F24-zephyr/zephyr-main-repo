package com.example.plannet;

import android.util.Log;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRCodeScan {
    private FirebaseFirestore db;

    public QRCodeScan() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Start the QR code scanning process from entrant fragment
     *
     * @param fragment entrantscanevent fragment
     */
    public void startQRCodeScanner(Fragment fragment) {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(fragment);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan a QR code");
        integrator.setCameraId(0); // camera initialization
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }
}