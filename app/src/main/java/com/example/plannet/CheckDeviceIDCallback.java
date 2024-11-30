package com.example.plannet;

public interface CheckDeviceIDCallback {
    /**
     * callback interface to display the following methods
     */
    void onDeviceIDFound();
    void onDeviceIDNotFound();
    void onError(Exception e);
}
