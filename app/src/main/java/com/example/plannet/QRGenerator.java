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

public class QRGenerator {
    //FirebaseConnector fireCon;
    FirebaseStorage storage = FirebaseStorage.getInstance();
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
                    .encode(data, BarcodeFormat.QR_CODE, 400, 400);
            return barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Store QR Bitmap in database for admin reasons and maybe retrieval reasons
     */
    public void uploadQRCodeBitmap(Bitmap qrBitmap, String eventID) {
        // Step 1: Convert Bitmap to Byte Array (PNG format)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);  // Compress the bitmap as PNG
        byte[] data = baos.toByteArray();

        // Step 2: Define Storage Path and Upload Byte Array
        StorageReference storageRef = storage.getReference().child("qr_codes/" + eventID + ".png"); // e.g., "qr_codes/event123.png"
        UploadTask uploadTask = storageRef.putBytes(data);

        // Step 3: Get the Download URL and Store in Firestore
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String qrCodeUrl = uri.toString();

                // Save the URL in Firestore under the specific event
                db.collection("events").document(eventID)
                        .update("qrCodeUrl", qrCodeUrl)
                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "QR code URL saved successfully!"))
                        .addOnFailureListener(e -> Log.w("Firestore", "Error saving QR code URL", e));
            });
        }).addOnFailureListener(e -> {
            Log.w("Storage", "Error uploading QR code", e);
        });
    }

//    public void storeQRCode(Bitmap qrBitmap, String eventID) {
//        // Generate QR code using the event ID directly
////        Bitmap qrBitmap = generateQRCode(eventID);
//
//        if (qrBitmap != null) {
//            // Prepare data for Firebase
//            HashMap<String, Object> qrData = new HashMap<>();
//            qrData.put("qrCode", eventID); // instead of eventID, the hashed QR code should be here
//
//            // Store the event ID in Firebase using FirebaseConnector
//            fireCon.addData(
//                    "qr_codes",               // Collection path
//                    eventID,                  // Document ID (event ID itself)
//                    qrData,                   // Data to add
//                    unused -> Log.i("FirebaseConnector", "QR code data successfully stored!"),  // Success callback
//                    e -> Log.e("FirebaseConnector", "Error storing QR code data: " + e.getMessage())  // Failure callback
//            );
//        }
//    }
}