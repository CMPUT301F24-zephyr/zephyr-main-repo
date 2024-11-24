package com.example.plannet.ui.adminhome;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.plannet.R;
import com.example.plannet.databinding.FragmentHomeAdminBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


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
        binding.viewAllEvents.setOnClickListener(v -> {
            getAllEventsWithMapping().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Pair<ArrayList<String>, Map<String, String>> result = task.getResult();
                    ArrayList<String> eventNames = result.first;
                    Map<String, String> nameToIdMap = result.second;

                    if (!eventNames.isEmpty()) {
                        // Show the dialog with event names and the mapping
                        showEventListDialog("Events", eventNames, nameToIdMap);
                    } else {
                        Toast.makeText(requireContext(), "No events found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch events.", Toast.LENGTH_SHORT).show();
                }
            });
        });
        binding.viewAllFacilities.setOnClickListener(v -> showListDialog("Facilities", "", getAllFacilities()));
        binding.viewHashedQR.setOnClickListener(v -> {
            getAllHashes().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ArrayList<String> qrcodes = task.getResult();
                    if (!qrcodes.isEmpty()) {
                        // show the users in a dialog or update UI
                        showListDialog("QR Hashes", "qr_codes", qrcodes);
                    } else {
                        // empty list case
                        Toast.makeText(requireContext(), "No qr codes found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //error case
                    Toast.makeText(requireContext(), "Failed to fetch qr codes.", Toast.LENGTH_SHORT).show();
                }
            });
        });
        binding.viewAllImages.setOnClickListener(v -> showListDialog("Images", "", getAllImages()));
        binding.viewAllProfiles.setOnClickListener(v -> {
            getAllProfiles().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ArrayList<String> users = task.getResult();
                    if (!users.isEmpty()) {
                        // show the users in a dialog or update UI
                        showListDialog("Users", "users", users);
                    } else {
                        // empty list case
                        Toast.makeText(requireContext(), "No users found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //error case
                    Toast.makeText(requireContext(), "Failed to fetch users.", Toast.LENGTH_SHORT).show();
                }
            });
        });
        binding.buttonEntrantMode.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_adminHome_to_userMode);
        });
    }

    /**
     * Show a dialog with a list of items and a delete option (works for qr+users)
     * @param title
     * @param items
     */
    private void showListDialog(String title, String collection, ArrayList<String> items) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title);

        // Adapter to display the list of items
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, items);

        ListView listView = new ListView(requireContext());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = items.get(position);

            // ask user to confirm deletion
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Entry")
                    .setMessage("Are you sure you want to delete " + selectedItem + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        // delete from Firebase
                        deleteEntryFromDatabase(selectedItem, collection, success -> {
                            if (success) {
                                // update UI after successful deletion
                                items.remove(position);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(requireContext(), "Deleted: " + selectedItem, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(), "Failed to delete: " + selectedItem, Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        builder.setView(listView);
        builder.setNegativeButton("Close", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    /**
     * delete an entry from a collection on firebase
     */
    private void deleteEntryFromDatabase(String documentId, String collection, OnDeleteCompleteListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collection) // collection
                .document(documentId) // eventID
                .delete()
                .addOnSuccessListener(aVoid -> listener.onComplete(true))
                .addOnFailureListener(e -> {
                    Log.e("DeleteEntry", "Failed to delete document: " + documentId, e);
                    listener.onComplete(false);
                });
    }
    public interface OnDeleteCompleteListener {
        void onComplete(boolean success);
    }

    private void deleteEventFromDatabase(String eventName, Map<String, String> nameToIdMap, String collection, OnDeleteCompleteListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String documentId = nameToIdMap.get(eventName); // Get the document ID using the eventName

        if (documentId == null) {
            Log.e("DeleteEntry", "Document ID not found for eventName: " + eventName);
            listener.onComplete(false);
            return;
        }

        db.collection(collection)
                .document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("DeleteEntry", "Deleted document: " + documentId);
                    listener.onComplete(true);
                })
                .addOnFailureListener(e -> {
                    Log.e("DeleteEntry", "Failed to delete document: " + documentId, e);
                    listener.onComplete(false);
                });
    }

    /**
     * fetches all eventIDs from firebase under events
     * @return string array object
     */
    public Task<ArrayList<String>> getAllHashes() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventsCollection = db.collection("qr_codes");

        TaskCompletionSource<ArrayList<String>> taskCompletionSource = new TaskCompletionSource<>();

        eventsCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                ArrayList<String> qrCodes = new ArrayList<>();

                if (querySnapshot != null) {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        qrCodes.add(document.getId()); // adds all hashes to a list for viewing
                    }
                }

                taskCompletionSource.setResult(qrCodes);
            } else {
                // empty list if an error arises
                taskCompletionSource.setResult(new ArrayList<>());
            }
        });

        return taskCompletionSource.getTask();
    }

    public Task<Pair<ArrayList<String>, Map<String, String>>> getAllEventsWithMapping() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventsCollection = db.collection("events");

        TaskCompletionSource<Pair<ArrayList<String>, Map<String, String>>> taskCompletionSource = new TaskCompletionSource<>();

        eventsCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                ArrayList<String> eventNames = new ArrayList<>();
                Map<String, String> nameToIdMap = new HashMap<>();

                if (querySnapshot != null) {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String eventName = document.getString("eventName");
                        String documentId = document.getId();
                        if (eventName != null) {
                            eventNames.add(eventName);
                            nameToIdMap.put(eventName, documentId); // Map eventName to document ID
                        }
                    }
                }

                taskCompletionSource.setResult(new Pair<>(eventNames, nameToIdMap));
            } else {
                taskCompletionSource.setResult(new Pair<>(new ArrayList<>(), new HashMap<>()));
            }
        });

        return taskCompletionSource.getTask();
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
     * fetches all userIDs from firebase under events
     * @return string array object
     */
    public Task<ArrayList<String>> getAllProfiles() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventsCollection = db.collection("users");

        TaskCompletionSource<ArrayList<String>> taskCompletionSource = new TaskCompletionSource<>();

        eventsCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                ArrayList<String> users = new ArrayList<>();

                if (querySnapshot != null) {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        users.add(document.getId()); // Adds each document ID to the list
                    }
                }

                taskCompletionSource.setResult(users);
            } else {
                // empty list if an error arises
                taskCompletionSource.setResult(new ArrayList<>());
            }
        });

        return taskCompletionSource.getTask();
    }

    private void showEventListDialog(String title, ArrayList<String> items, Map<String, String> nameToIdMap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title);

        // Adapter to display the list of items
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, items);

        ListView listView = new ListView(requireContext());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = items.get(position);

            // Confirm deletion with a dialog
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Entry")
                    .setMessage("Are you sure you want to delete " + selectedItem + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        deleteEventFromDatabase(selectedItem, nameToIdMap, "events", success -> {
                            if (success) {
                                // call notifydatachanged after successful deletion
                                items.remove(position);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(requireContext(), "Deleted: " + selectedItem, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(), "Failed to delete: " + selectedItem, Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        builder.setView(listView);
        builder.setNegativeButton("Close", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Avoid memory leaks
        binding = null;
    }
}