package com.example.plannet.ui.Fragments.notifications;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NotificationsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public NotificationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
        Log.d("NotificationsViewModel", "Text value: " + mText.getValue());
    }

    public LiveData<String> getText() {
        return mText;
    }
}