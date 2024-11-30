package com.example.plannet.ui.adminhome;

import android.app.AlertDialog;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.plannet.ArrayAdapters.AdminEventListAdapter;
import com.example.plannet.ArrayAdapters.AdminFacilityListAdapter;
import com.example.plannet.ArrayAdapters.AdminImageListAdapter;
import com.example.plannet.ArrayAdapters.AdminUserListAdapter;
import com.example.plannet.Organizer.Facility;
import com.example.plannet.R;
import com.example.plannet.databinding.FragmentHomeAdminBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


public class AdminHomeFragment extends Fragment {
    /**
     * admin fragment with list dialogs to show the intended tasks
     * Sources:
     * https://javascript.plainenglish.io/introduction-to-firebase-storage-2-retrieve-delete-files-3875f11e6a89
     * https://www.geeksforgeeks.org/custom-arrayadapter-with-listview-in-android/
     * https://firebase.google.com/docs/storage/web/download-files
     */

    private FragmentHomeAdminBinding binding;

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
        // Initialize View Binding
        binding = FragmentHomeAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Button click listeners
        binding.viewAllEvents.setOnClickListener(v -> {
            getAllEventsWithMapping().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Pair<ArrayList<String>, Map<String, String>> result = task.getResult();
                    // using mapping and updating both objects synchrounosly to help with
                    // deleting entries with names as fields
                    // similar to a dictionary in python
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
        binding.viewAllFacilities.setOnClickListener(v -> {
            fetchFacilitiesFromFirebase().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ArrayList<Facility> facilities = task.getResult();
                    if (!facilities.isEmpty()) {
                        showFacilitiesDialog("Facilities", facilities);
                    } else {
                        Toast.makeText(requireContext(), "No facilities found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch facilities.", Toast.LENGTH_SHORT).show();
                }
            });
        });
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
        binding.viewAllPosters.setOnClickListener(v -> {
            fetchImagesFromFirebase("profile_pictures").addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ArrayList<String> imageUrls = task.getResult();
                    if (!imageUrls.isEmpty()) {
                        showImagesDialog("Images", imageUrls);
                    } else {
                        Toast.makeText(requireContext(), "No images found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch images.", Toast.LENGTH_SHORT).show();
                }
            });
        });
        binding.viewAllProfilePics.setOnClickListener(v -> {
            fetchImagesFromFirebase("profile_pictures").addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ArrayList<String> imageUrls = task.getResult();
                    if (!imageUrls.isEmpty()) {
                        showImagesDialog("Profile Pictures", imageUrls);
                    } else {
                        Toast.makeText(requireContext(), "No profile pictures found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch profile pictures.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.viewAllPosters.setOnClickListener(v -> {
            fetchImagesFromFirebase("posters").addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ArrayList<String> imageUrls = task.getResult();
                    if (!imageUrls.isEmpty()) {
                        showImagesDialog("Posters", imageUrls);
                    } else {
                        Toast.makeText(requireContext(), "No event posters found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch event posters.", Toast.LENGTH_SHORT).show();
                }
            });
        });
        binding.viewAllProfiles.setOnClickListener(v -> {
            getAllProfiles().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ArrayList<String> users = task.getResult();
                    if (!users.isEmpty()) {
                        // show the users in a dialog or update UI
                        showUsersDialog("Users", "users", users);
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
     * Show a dialog with a list of items and a delete option (works for qr)
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
     * get userIDs and names and add to dialog
     * @param title
     * @param collection
     * @param items
     */
    private void showUsersDialog(String title, String collection, ArrayList<String> items) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title);

        // Adapter to display the list of items
        AdminUserListAdapter adapter = new AdminUserListAdapter(requireContext(), items);

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
                                Log.e("removeUser", "selectedItem value = " + selectedItem);
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
     * show a dialog with images under firebase storage
     */
    private void showImagesDialog(String title, ArrayList<String> imageUrls) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title);

        // Custom adapter to show image thumbnails
        AdminImageListAdapter adapter = new AdminImageListAdapter(requireContext(), imageUrls);
        ListView listView = new ListView(requireContext());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedImageUrl = imageUrls.get(position);

            // Ask for confirmation before deleting
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Image")
                    .setMessage("Are you sure you want to delete this image?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        deleteImageFromFirebase(selectedImageUrl, success -> {
                            if (success) {
                                imageUrls.remove(position);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(requireContext(), "Image deleted successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(), "Failed to delete image.", Toast.LENGTH_SHORT).show();
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
     * delete an image from Firebase using its imageURL
     * Source: https://javascript.plainenglish.io/introduction-to-firebase-storage-2-retrieve-delete-files-3875f11e6a89
     */
    private void deleteImageFromFirebase(String imageUrl, OnDeleteCompleteListener listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);

        imageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("DeleteImage", "Image deleted successfully.");
                    listener.onComplete(true);
                })
                .addOnFailureListener(e -> {
                    Log.e("DeleteImage", "Failed to delete image.", e);
                    listener.onComplete(false);
                });
    }

    /**
     * fetch all images from firebase storage
     */
    private Task<ArrayList<String>> fetchImagesFromFirebase(String path) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(path); // works for posters + pfp

        TaskCompletionSource<ArrayList<String>> taskCompletionSource = new TaskCompletionSource<>();

        storageRef.listAll().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<String> imageUrls = new ArrayList<>();
                List<StorageReference> items = task.getResult().getItems();

                if (items != null && !items.isEmpty()) {
                    // Fetch download URLs for each item
                    ArrayList<Task<Uri>> urlTasks = new ArrayList<>();
                    for (StorageReference item : items) {
                        urlTasks.add(item.getDownloadUrl());
                    }

                    // Wait for all URL tasks to complete
                    Tasks.whenAllComplete(urlTasks).addOnCompleteListener(urlTask -> {
                        for (Task<?> t : urlTasks) {
                            if (t.isSuccessful()) {
                                imageUrls.add(((Task<Uri>) t).getResult().toString());
                            }
                        }
                        taskCompletionSource.setResult(imageUrls);
                    });
                } else {
                    taskCompletionSource.setResult(imageUrls);
                }
            } else {
                taskCompletionSource.setException(task.getException());
            }
        });

        return taskCompletionSource.getTask();
    }

    /**
     * Show a dialog with a list of facility and a delete option (facility specific)
     * @param title
     * @param items
     */
    private void showFacilitiesDialog(String title, ArrayList<Facility> items) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title);

        //custom FacilityAdapter
        AdminFacilityListAdapter adapter = new AdminFacilityListAdapter(requireContext(), items);

        ListView listView = new ListView(requireContext());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Facility selectedFacility = items.get(position);

            String facilityName = selectedFacility.getFacilityName();
            String facilityLocation = selectedFacility.getFacilityLocation();

            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Entry")
                    .setMessage("Are you sure you want to delete " + facilityName + " at " + facilityLocation + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        getUserIdForFacility(facilityName, facilityLocation).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String userId = task.getResult();
                                if (userId != null) {
                                    deleteFacilityFromUser(userId, success -> {
                                        if (success) {
                                            items.remove(position);
                                            adapter.notifyDataSetChanged();
                                            Toast.makeText(requireContext(), "Deleted: " + facilityName + " at " + facilityLocation, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(requireContext(), "Failed to delete: " + facilityName + " at " + facilityLocation, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(requireContext(), "No matching user found for the facility.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(requireContext(), "Error finding user for facility.", Toast.LENGTH_SHORT).show();
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
     * side note: calling this method from the showUsersDialog, it will delete all the fields
     * in the document, but the main issue is the documentID to delete will stay on DB
     * as a "ghost" document. reason is the subcollections that the document carries
     * but the app/db will treat the document as non-existent
     * i.e. the deleted user will go through FirstTimeUserFragment as a new user
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

    /**
     * method to remove facility from user profile
     */
    private void deleteFacilityFromUser(String userId, OnDeleteCompleteListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users") // Collection name
                .document(userId) // User document
                .update("facility", FieldValue.delete())
                .addOnSuccessListener(aVoid -> listener.onComplete(true))
                .addOnFailureListener(e -> {
                    Log.e("DeleteFacility", "Failed to delete facility for user: " + userId, e);
                    listener.onComplete(false);
                });
    }
    /**
     * get userID for a specific facility map
     */
    private Task<String> getUserIdForFacility(String facilityName, String facilityLocation) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("users");

        TaskCompletionSource<String> taskCompletionSource = new TaskCompletionSource<>();

        usersCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();

                if (querySnapshot != null) {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Map<String, Object> facility = (Map<String, Object>) document.get("facility");

                        if (facility != null) {
                            String name = (String) facility.get("name");
                            String location = (String) facility.get("location");

                            if (facilityName.equals(name) && facilityLocation.equals(location)) {
                                // if facility is found under a user, return userID
                                taskCompletionSource.setResult(document.getId());
                                return;
                            }
                        }
                    }
                }

                // No match found
                taskCompletionSource.setResult(null);
            } else {
                // Error occurred while querying Firestore
                taskCompletionSource.setException(task.getException());
            }
        });

        return taskCompletionSource.getTask();
    }

    /**
     * fetch facility map objects from Firebase and populate into ArrayList<String>
     * @return
     */
    private Task<ArrayList<Facility>> fetchFacilitiesFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("users");

        TaskCompletionSource<ArrayList<Facility>> taskCompletionSource = new TaskCompletionSource<>();

        usersCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                ArrayList<Facility> facilities = new ArrayList<>();

                if (querySnapshot != null) {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Map<String, Object> facility = (Map<String, Object>) document.get("facility");
                        if (facility != null) {
                            String facilityName = (String) facility.get("name");
                            String facilityLocation = (String) facility.get("location");
                            if (facilityName != null && facilityLocation != null) {
                                facilities.add(new Facility(facilityName, facilityLocation));
                            }
                        }
                    }
                }
                taskCompletionSource.setResult(facilities);
            } else {
                // default empty list base case
                taskCompletionSource.setResult(new ArrayList<>());
            }
        });

        return taskCompletionSource.getTask();
    }

    /**
     * deletes an event from the database
     * @param eventName
     * @param nameToIdMap
     * @param collection
     * @param listener
     */
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

    /**
     * get events with mapping from their event name using Map function
     * Source: https://cloud.google.com/firestore/docs/query-data/queries
     * @return
     */
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
        AdminEventListAdapter adapter = new AdminEventListAdapter(requireContext(), items, nameToIdMap);

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

    /**
     * Destroys view
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Avoid memory leaks
        binding = null;
    }
}