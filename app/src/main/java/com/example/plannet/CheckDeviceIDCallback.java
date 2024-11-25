package com.example.plannet;

public interface CheckDeviceIDCallback {
    void onDeviceIDFound();
    void onDeviceIDNotFound();
    void onError(Exception e);
}
