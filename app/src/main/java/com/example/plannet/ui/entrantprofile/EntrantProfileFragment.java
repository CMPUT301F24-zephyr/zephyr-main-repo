package com.example.plannet.ui.entrantprofile;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.plannet.Entrant.EntrantDBConnector;
import com.example.plannet.databinding.FragmentEntrantProfileBinding;

import java.util.Map;

/**
 * Entrant profile fragment for user data collection
 */
public class EntrantProfileFragment extends Fragment {

    private EditText firstNameEdit, lastNameEdit, phoneEdit, emailEdit;
    private FragmentEntrantProfileBinding binding;
    private EntrantProfileViewModel entrantProfileViewModel;
    private String userID;

    /**
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEntrantProfileBinding.inflate(inflater, container, false);
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

        binding.buttonSave.setOnClickListener(v -> saveUserProfile());

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

    /**
     * Method for setting the binding to null when the view is destroyed to avoid errors.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
