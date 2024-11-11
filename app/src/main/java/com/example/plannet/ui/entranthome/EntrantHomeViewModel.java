package com.example.plannet.ui.entranthome;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EntrantHomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public EntrantHomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is entrant home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}