package com.example.plannet;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {
    private final MutableLiveData<String> uniqueID = new MutableLiveData<>();

    public void setUniqueID(String id) {
        uniqueID.setValue(id);
    }

    public LiveData<String> getUniqueID() {
        return uniqueID;
    }
}