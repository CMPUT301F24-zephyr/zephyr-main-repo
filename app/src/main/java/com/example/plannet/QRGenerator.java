package com.example.plannet;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.HashMap;

public class QRGenerator {
    FirebaseConnector fireCon;

    public QRGenerator() {
        fireCon = new FirebaseConnector();
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

    public void storeQRCode(Bitmap qrBitmap, String eventID) {
        // Generate QR code using the event ID directly
//        Bitmap qrBitmap = generateQRCode(eventID);

        if (qrBitmap != null) {
            // Prepare data for Firebase
            HashMap<String, Object> qrData = new HashMap<>();
            qrData.put("qrCode", eventID); // Store eventID directly as QR code data

            // Store the event ID in Firebase using FirebaseConnector
            fireCon.addData(
                    "qr_codes",               // Collection path
                    eventID,                  // Document ID (event ID itself)
                    qrData,                   // Data to add
                    unused -> Log.i("FirebaseConnector", "QR code data successfully stored!"),  // Success callback
                    e -> Log.e("FirebaseConnector", "Error storing QR code data: " + e.getMessage())  // Failure callback
            );
        }
    }
}