package com.example.plannet;

/**
 * callback interface to display the following methods
 */
public interface CheckDeviceIDCallback {

    void onDeviceIDFound();
    void onDeviceIDNotFound();
    void onError(Exception e);
}
