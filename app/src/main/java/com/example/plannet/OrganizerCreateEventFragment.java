package com.example.plannet;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.plannet.FirebaseConnector;
import com.example.plannet.QRGenerator;
import com.example.plannet.R;
import com.example.plannet.databinding.OrganizerCreateEventBinding;

public class OrganizerCreateEventFragment extends Fragment {

    private OrganizerCreateEventBinding binding;
    // event information
    private EditText nameEdit, priceEdit, maxEntrantsEdit, descriptionEdit, lastRegEdit, runtimeStartEdit, runtimeEndEdit, waitlistMaxEdit;
    private CheckBox waitlistLimitCheckbox, geolocationCheckbox;
    private ImageView qrImageView;

    private Button generateQrButton;
    private QRGenerator qrGenerator;

    FirebaseConnector db = new FirebaseConnector();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize ViewBinding for this fragment
        binding = OrganizerCreateEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize views using binding
        nameEdit = binding.nameEdit;
        priceEdit = binding.priceEdit;
        maxEntrantsEdit = binding.maxEntrantsEdit;
        descriptionEdit = binding.description;
        lastRegEdit = binding.lastRegEdit;
        runtimeStartEdit = binding.runtimeStartEdit;
        runtimeEndEdit = binding.runtimeEndEdit;
        waitlistLimitCheckbox = binding.waitlistLimitCheckbox;
        geolocationCheckbox = binding.geolocationCheckbox;

        // listener for id/generate_qr_button to generate QR and upload QR hashed data to Firebase
        generateQrButton = binding.generateQrButton;
        qrGenerator = new QRGenerator();
        generateQrButton.setOnClickListener(v -> {
            String eventData = collectEventData();
            String eventID = "uniqueEventID";  // Replace with a unique ID as needed

            if (eventData != null) {
                qrGenerator.generateAndStoreQRCode(eventData, eventID);

                // Display the QR code in qrImageView
                Bitmap qrBitmap = qrGenerator.generateQRCode(eventData);
                if (qrBitmap != null) {
                    showCustomToast(qrBitmap);
                }
            }
        });

        return root;
    }

    @NonNull
    private String collectEventData() {
        // Collect text inputs and convert to a single string
        String name = nameEdit.getText().toString();
        String price = priceEdit.getText().toString();
        String maxEntrants = maxEntrantsEdit.getText().toString();
        String description = descriptionEdit.getText().toString();
        String lastReg = lastRegEdit.getText().toString();
        String runtimeStart = runtimeStartEdit.getText().toString();
        String runtimeEnd = runtimeEndEdit.getText().toString();
        String waitlistMax = waitlistMaxEdit.getText().toString();
        // Collect checkbox values
        boolean waitlistLimit = waitlistLimitCheckbox.isChecked();
        // if waitlist limit is chosen, set maxEntrants to be the waitlistMax --- this might need clarification if waitlistLimit would also mean maxEntrants
        if (waitlistLimit) {
            maxEntrants = waitlistMax;
        }
        boolean geolocation = geolocationCheckbox.isChecked();

        // Format data into a JSON-like string (or any desired format)
        return String.format(
                "Name: %s\nPrice: %s\nMax Entrants: %s\nDescription: %s\nLast Registration: %s\nRuntime Start: %s\nRuntime End: %s\nWaitlist Limit: %b\nGeolocation: %b",
                name, price, maxEntrants, description, lastReg, runtimeStart, runtimeEnd, waitlistLimit, geolocation
        );
    }

    // method to show toast notification with QR code
    private void showCustomToast(Bitmap qrBitmap) {
        // Inflate custom toast layout
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_layout,
                (ViewGroup) binding.getRoot().findViewById(R.id.custom_toast_xml));  // Use binding.getRoot()

        // Set the QR code image in the toast layout
        ImageView toastQrImage = layout.findViewById(R.id.toast_qr_image);
        toastQrImage.setImageBitmap(qrBitmap);

        // Create and show the custom toast
        Toast toast = new Toast(requireContext());  // Use requireContext() for context in a fragment
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Nullify binding to avoid memory leaks
    }
}