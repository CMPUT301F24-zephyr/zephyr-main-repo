package com.example.plannet.ui.organizerProfile;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.plannet.FirebaseConnector;
import com.example.plannet.Organizer.Facility;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Map;

public class OrganizerProfileViewModel extends ViewModel {

    private final MutableLiveData<Facility> facilityDetails = new MutableLiveData<>();
    private final FirebaseConnector firebaseConnector = new FirebaseConnector();

    public OrganizerProfileViewModel(String userID) {
        loadFacilityDetails(userID);
    }

    private void loadFacilityDetails(String userID) {
        firebaseConnector.checkIfFacilityDataIsValid(userID,
                new OnSuccessListener<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> facilityMap) {
                        // Extract facility data from the map and set it in LiveData
                        String facilityName = (String) facilityMap.get("name");
                        String facilityLocation = (String) facilityMap.get("location");

                        Facility facility = new Facility(facilityName, facilityLocation);
                        facilityDetails.setValue(facility);
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors here, such as logging
                        e.printStackTrace();
                    }
                }
        );
    }

    public LiveData<Facility> getFacilityDetails() {
        return facilityDetails;
    }
}