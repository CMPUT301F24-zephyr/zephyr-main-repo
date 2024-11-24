package com.example.plannet;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class QRGenerator {
    //FirebaseConnector fireCon;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public QRGenerator() {

    }

    //

    /**
     * Generate QR Code bitmap using zxing library
     * @param data
     * data is the eventID value from Event class
     * @return Bitmap
     * returns a QR bitmap
     */
    public Bitmap generateQRCode(String data) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            BitMatrix bitMatrix = new QRCodeWriter()
                    .encode(data, BarcodeFormat.QR_CODE, 1000, 1000);
            return barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Add hashed eventID in database
     */
    public void storeQRCodeEventID(String eventID) {
        if (eventID == null || eventID.isEmpty()) {
            Log.e("QRGenerator", "Event ID is null or empty, cannot store.");
            return;
        }
        // Create a new document in the "qr_codes" collection using the eventID as the document ID
        db.collection("qr_codes").document(eventID)
                .set(new HashMap<String, Object>() {{
                    put("eventID", eventID); // Store the event ID as a field in the document
                }})
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Event ID stored successfully in qr_codes collection!"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error storing event ID", e));
    }

}