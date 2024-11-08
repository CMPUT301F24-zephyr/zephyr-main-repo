package com.example.plannet.Entrant;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.plannet.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class EntrantProfileFragment extends Fragment {

    private EditText firstNameEditText, lastNameEditText, phoneEditText, emailEditText;
    private Button saveButton;

    private EntrantDBConnector entrantDBConnector;
    private String uniqueUserID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_time_general_signup, container, false);

        // Initialize EntrantDBConnector
        entrantDBConnector = new EntrantDBConnector();

        // Initialize UI components
        firstNameEditText = view.findViewById(R.id.first_name_edittext);
        lastNameEditText = view.findViewById(R.id.last_name_edittext);
        phoneEditText = view.findViewById(R.id.phone_edittext);
        emailEditText = view.findViewById(R.id.email_edittext);
        saveButton = view.findViewById(R.id.button_continue);

        // Set up click listener for save button
        saveButton.setOnClickListener(v -> saveUserProfile());

        return view;
    }

    private void saveUserProfile() {
        // Retrieve input from EditTexts!
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        // Check if required fields are filled, phone number is NOT required
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save user info to Firestore using EntrantDBConnector
        entrantDBConnector.saveUserInfo(uniqueUserID, firstName, lastName, phone, email,
                aVoid -> {
                    Toast.makeText(getContext(), "Profile saved successfully", Toast.LENGTH_SHORT).show();
                    Log.d("EntrantProfileFragment", "Profile saved for userID: " + uniqueUserID);
                },
                e -> {
                    Toast.makeText(getContext(), "Failed to save profile", Toast.LENGTH_SHORT).show();
                    Log.e("EntrantProfileFragment", "Error saving profile", e);
                });
    }
}
