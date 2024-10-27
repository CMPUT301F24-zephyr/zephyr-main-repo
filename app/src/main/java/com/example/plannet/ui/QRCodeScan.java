package com.example.plannet.ui;

import android.graphics.Bitmap;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.HashMap;
import java.util.Map;

public class QRCodeScan extends AppCompatActivity {
    private static int PERMISSION_REQUEST_CAMERA = 1;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();



    public Bitmap generateQRCode(String data) {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            // Encode data as a QR code and return the Bitmap
            return barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 400, 400);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }



    public void storeHashDataInFirebase(String hashData) {
        Map<String, Object> data = new HashMap<>();
        data.put("hash", hashData);

        ///code here
    }