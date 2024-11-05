package com.example.plannet.ui.events;
import com.example.plannet.Event.Event;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EventsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private MutableLiveData<Event> eventLiveData = new MutableLiveData<>(); // For QRCodeScan

    public EventsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Events go here!!");
        Log.d("EventsViewModel", "Text value: " + mText.getValue());
    }

    public LiveData<String> getText() {
        return mText;
    }

    // Setter method for event data for QRCodeScan
    public void setEventDetails(Event event) {
        eventLiveData.setValue(event);
    }

    // Getter method for event data from QRCodeScan
    public LiveData<Event> getEventDetails() {
        return eventLiveData;}
}