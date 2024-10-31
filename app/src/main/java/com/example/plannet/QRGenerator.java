package com.example.plannet;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * function to generate QR code and extract hash data to Firebase
 */
public class QRGenerator {
    FirebaseConnector fireCon;

    public QRGenerator() {
        fireCon = new FirebaseConnector();
    }

    // Generate QR Code bitmap using zxing library
    public Bitmap generateQRCode(String eventData) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            BitMatrix bitMatrix = new QRCodeWriter()
                    .encode(eventData, BarcodeFormat.QR_CODE, 400, 400);
            return barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Hash QRCode Bitmap data using SHA-256
     * @param bitmap
     * Bitmap data specifically
     * @return byte array for hashing purposes
     */
    public byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Takes in a byteArray and hashes the data using Base64 for Firebase purposes
     * @param byteArray
     * byte array from convertBitmapToByteArray(Bitmap bitmap)
     * @return String
     * Returns string that we can use to store in Firebase
     */
    public String hashBitmap(byte[] byteArray) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(byteArray);
            return Base64.encodeToString(hash, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void generateAndStoreQRCode(String eventData, String eventID) {
        Bitmap qrBitmap = generateQRCode(eventData);

        if (qrBitmap != null) {
            // Convert QR Bitmap to Byte Array and then Hash it
            byte[] byteArray = convertBitmapToByteArray(qrBitmap);
            String hashedData = hashBitmap(byteArray);

            if (hashedData != null) {
                // Prepare data for Firebase
                HashMap<String, Object> qrData = new HashMap<>();
                qrData.put("qrHash", hashedData);

                // Store hash in Firebase using FirebaseConnector
                fireCon.addData(
                        "qr_codes",                 // Collection path
                        eventID,                    // Document ID (e.g., unique event identifier)
                        qrData,                     // Data to add
                        unused -> Log.i("FirebaseConnector", "QR code hash successfully stored!"),  // Success callback
                        e -> Log.e("FirebaseConnector", "Error storing QR code hash: " + e.getMessage())  // Failure callback
                );
            }
        }
    }

}
