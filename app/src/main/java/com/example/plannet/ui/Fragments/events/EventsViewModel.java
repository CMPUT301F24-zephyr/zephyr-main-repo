package com.example.plannet.ui.Fragments.events;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EventsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public EventsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Events go here!!");
        Log.d("EventsViewModel", "Text value: " + mText.getValue());
    }

    public LiveData<String> getText() {
        return mText;
    }
}