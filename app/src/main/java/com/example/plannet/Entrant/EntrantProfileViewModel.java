package com.example.plannet.Entrant;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.plannet.Entrant.EntrantDBConnector;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Map;

public class EntrantProfileViewModel extends ViewModel {

    private final MutableLiveData<Map<String, Object>> entrantDetails = new MutableLiveData<>();
    private final EntrantDBConnector entrantDBConnector = new EntrantDBConnector();

    public EntrantProfileViewModel(String userID) {
        loadEntrantDetails(userID);
    }

    private void loadEntrantDetails(String userID) {
        entrantDBConnector.getUserInfo(userID,
                new OnSuccessListener<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> userInfoMap) {
                        // Update LiveData with the user's information
                        entrantDetails.setValue(userInfoMap);
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    public LiveData<Map<String, Object>> getEntrantDetails() {
        return entrantDetails;
    }
}

