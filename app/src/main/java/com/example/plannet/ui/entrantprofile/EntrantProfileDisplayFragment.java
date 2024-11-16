package com.example.plannet.ui.entrantprofile;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.plannet.Entrant.EntrantDBConnector;
import com.example.plannet.R;
import com.example.plannet.databinding.EntrantProfileDisplayBinding;

import java.util.Map;


public class EntrantProfileDisplayFragment extends Fragment{
    private EntrantProfileDisplayBinding binding;
    private EntrantProfileViewModel entrantProfileViewModel;
    private String userID;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = EntrantProfileDisplayBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        userID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);


        entrantProfileViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new EntrantProfileViewModel(userID);
            }
        }).get(EntrantProfileViewModel.class);


        entrantProfileViewModel.getEntrantDetails().observe(getViewLifecycleOwner(), this::updateUI);


        binding.button.setOnClickListener(v -> updateUserProfile());

        return root;
    }
    private void updateUI(Map<String, Object> entrantInfo) {
        if (entrantInfo != null) {
            binding.firstNameEdit.setText((String) entrantInfo.get("firstName"));
            binding.lastNameEdit.setText((String) entrantInfo.get("lastName"));
            binding.phoneEdit.setText((String) entrantInfo.get("phone"));
            binding.emailEdit.setText((String) entrantInfo.get("email"));
            binding.locationEdit.setText((String) entrantInfo.get("location"));
        }
    }

    private void updateUserProfile() {
        String firstName = binding.firstNameEdit.getText().toString();
        String lastName = binding.lastNameEdit.getText().toString();
        String phone = binding.phoneEdit.getText().toString();
        String email = binding.emailEdit.getText().toString();
        //String location = binding.locationEdit.getText().toString();


        EntrantDBConnector entrantDBConnector = new EntrantDBConnector();
        entrantDBConnector.saveUserInfo(userID, firstName, lastName, phone, email,
                aVoid -> {
                    Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();


                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(R.id.navigation_entranthome);
                },
                e -> {
                    Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }




}
