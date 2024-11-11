package com.example.plannet.ui.orghome;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OrganizerHomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public OrganizerHomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}