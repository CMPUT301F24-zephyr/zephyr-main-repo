package com.example.plannet.Notification;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class NotificationPermissionActivity extends AppCompatActivity {

    private static final int REQUEST_NOTIFICATION_PERMISSION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
            } else {
                Toast.makeText(this, "Notifications already enabled", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "No need to request notification permissions on this version of Android", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            SharedPreferences preferences = getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Update SharedPreferences to reflect the enabled state
                editor.putBoolean("notificationsEnabled", true);
                editor.apply();

                Toast.makeText(this, "Notifications enabled successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Update SharedPreferences to reflect the disabled state
                editor.putBoolean("notificationsEnabled", false);
                editor.apply();

                Toast.makeText(this, "Notifications permission denied", Toast.LENGTH_SHORT).show();
            }
            finish(); // Close the activity after handling
        }
    }
}