package com.example.mysteps;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

class ActivityPermissions {
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String ACTIVITY_RECOGNITION = Manifest.permission.ACTIVITY_RECOGNITION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234, ACTIVITY_PERMISSION_CODE = 122;
    Activity activity;

    public ActivityPermissions(Activity activity) {
        this.activity = activity;
    }

    public boolean getLocationPermission() {
        Log.d("TAG", "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(activity.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(activity,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(activity,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public boolean getActivityPermission() {
        Log.d("TAG", "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACTIVITY_RECOGNITION};

        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(),
                ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(activity,
                    permissions,
                    ACTIVITY_PERMISSION_CODE);
        }
        return false;
    }
}
