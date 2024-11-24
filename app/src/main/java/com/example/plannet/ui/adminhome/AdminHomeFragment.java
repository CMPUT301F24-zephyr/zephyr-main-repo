package com.example.plannet.ui.adminhome;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.plannet.R;
import com.example.plannet.databinding.FragmentHomeAdminBinding;

import java.util.ArrayList;


public class AdminHomeFragment extends Fragment {

    private FragmentHomeAdminBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize View Binding
        binding = FragmentHomeAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Button click listeners
        binding.viewAllEvents.setOnClickListener(v -> showListDialog("Events", getAllEvents()));
        binding.viewAllFacilities.setOnClickListener(v -> showListDialog("Facilities", getAllFacilities()));
        binding.viewHashedQR.setOnClickListener(v -> showListDialog("QR Hashes", getAllHashes()));
        binding.viewAllImages.setOnClickListener(v -> showListDialog("Images", getAllImages()));
        binding.viewAllProfiles.setOnClickListener(v -> showListDialog("Profiles", getAllProfiles()));

        binding.buttonEntrantMode.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_adminHome_to_userMode);
        });
    }

    /**
     * Show a dialog with a list of items and a delete option
     * @param title
     * @param items
     */
    private void showListDialog(String title, ArrayList<String> items) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title);

        // Adapter to display the list of items
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, items);

        // Set up the ListView
        ListView listView = new ListView(requireContext());
        listView.setAdapter(adapter);

        // Handle item clicks (delete functionality)
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = items.get(position);
            Toast.makeText(requireContext(), "Deleted: " + selectedItem, Toast.LENGTH_SHORT).show();
            items.remove(position);
            adapter.notifyDataSetChanged();
        });

        builder.setView(listView);
        builder.setNegativeButton("Close", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    /**
     * Array data for events
     */
    private ArrayList<String> getAllEvents() {
        ArrayList<String> events = new ArrayList<>();

        return events;
    }

    /**
     * Array data for facilities
     */
    private ArrayList<String> getAllFacilities() {
        ArrayList<String> facilities = new ArrayList<>();
        facilities.add("Facility 1");
        facilities.add("Facility 2");
        facilities.add("Facility 3");
        return facilities;
    }

    /**
     * Array data for QR hashes
     */
    private ArrayList<String> getAllHashes() {
        ArrayList<String> hashes = new ArrayList<>();
        hashes.add("QR Hash 1");
        hashes.add("QR Hash 2");
        hashes.add("QR Hash 3");
        return hashes;
    }

    /**
     * Array data for images
     */
    private ArrayList<String> getAllImages() {
        ArrayList<String> images = new ArrayList<>();
        images.add("Image 1");
        images.add("Image 2");
        images.add("Image 3");
        return images;
    }

    /**
     * Array data for profiles
     */
    private ArrayList<String> getAllProfiles() {
        ArrayList<String> profiles = new ArrayList<>();
        profiles.add("Profile 1");
        profiles.add("Profile 2");
        profiles.add("Profile 3");
        return profiles;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Avoid memory leaks
        binding = null;
    }
}