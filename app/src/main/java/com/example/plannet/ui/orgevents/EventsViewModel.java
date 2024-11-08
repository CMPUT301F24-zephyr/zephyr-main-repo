package com.example.plannet.ui.orgevents;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.plannet.Event.Event;

public class EventsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private MutableLiveData<Event> eventLiveData = new MutableLiveData<>();

    public EventsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Events go here!!");
        Log.d("EventsViewModel", "Text value: " + mText.getValue());
    }

    public LiveData<String> getText() {
        return mText;
    }
}