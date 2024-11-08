package com.example.plannet.Entrant;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.plannet.databinding.FirstTimeGeneralSignupBinding;
import com.example.plannet.Entrant.EntrantProfileViewModel;

import java.util.Map;

public class EntrantProfileFragment extends Fragment {

    private EditText firstNameEdit, lastNameEdit, phoneEdit, emailEdit;
    private FirstTimeGeneralSignupBinding binding;
    private EntrantProfileViewModel entrantProfileViewModel;
    private String userID;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FirstTimeGeneralSignupBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        userID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        firstNameEdit = binding.firstNameEdittext;
        lastNameEdit = binding.lastNameEdittext;
        phoneEdit = binding.phoneEdittext;
        emailEdit = binding.emailEdittext;

        //https://developer.android.com/topic/libraries/architecture/viewmodel
        // Initialize ViewModel
        entrantProfileViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new EntrantProfileViewModel(userID);
            }
        }).get(EntrantProfileViewModel.class);

        entrantProfileViewModel.getEntrantDetails().observe(getViewLifecycleOwner(), this::updateUI);

        binding.buttonContinue.setOnClickListener(v -> saveUserProfile());

        return root;
    }

    private void updateUI(Map<String, Object> entrantInfo) {
        if (entrantInfo != null) {
            firstNameEdit.setText((String) entrantInfo.get("firstName"));
            lastNameEdit.setText((String) entrantInfo.get("lastName"));
            phoneEdit.setText((String) entrantInfo.get("phone"));
            emailEdit.setText((String) entrantInfo.get("email"));
        }
    }

    private void saveUserProfile() {
        String firstName = firstNameEdit.getText().toString();
        String lastName = lastNameEdit.getText().toString();
        String phone = phoneEdit.getText().toString();
        String email = emailEdit.getText().toString();

        EntrantDBConnector entrantDBConnector = new EntrantDBConnector();
        entrantDBConnector.saveUserInfo(userID, firstName, lastName, phone, email,
                aVoid -> Toast.makeText(getContext(), "Profile saved successfully", Toast.LENGTH_SHORT).show(),
                e -> {
                    Toast.makeText(getContext(), "Failed to save profile", Toast.LENGTH_SHORT).show();
                    Log.e("EntrantProfileFragment", "Error saving profile");
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
