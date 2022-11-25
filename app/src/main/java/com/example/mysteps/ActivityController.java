package com.example.mysteps;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.concurrent.Callable;

class ActivityController implements ActivityActions {
    SensorManager sensorManager;
    Sensor stepSensor;
    SensorEventListener listener;
    Activity activity;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234, ACTIVITY_PERMISSION_CODE = 122;
    Callable<Void> mapCallBack;

    public ActivityController( SensorEventListener listener, Activity activity,
                              Callable<Void> mapCallBack) {
        this.listener = listener;
        this.activity = activity;
        this.mapCallBack = mapCallBack;
    }

    @Override
    public void startCountSteps() {
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        boolean isLocationEnabled, isActivityEnabled = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isActivityEnabled = new ActivityPermissions(activity).getActivityPermission();
        }
        isLocationEnabled = new ActivityPermissions(activity).getLocationPermission();
        if (isActivityEnabled && isLocationEnabled) {
            try {
                mapCallBack.call();

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
                sensorManager.registerListener(listener, stepSensor, SensorManager.SENSOR_DELAY_GAME);
            }
        }
    }

    @Override
    public void stopCountSteps() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            sensorManager.unregisterListener(listener, stepSensor);
        }
    }

    @Override
    public void getTotalTimeForSteps() {
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String dateString = formatter.format();
//        long diffDate = new Date().getTime() - new Date(Long.parseLong(String.valueOf(event.timestamp))).getTime();
//
//        int hours = (int) (diffDate / (1000 * 60 * 60));
//        int minutes = (int) (diffDate / (1000 * 60));
//        int seconds = (int) (diffDate / (1000));
//        Log.d("key",String.valueOf(event.timestamp+" " +hours +":"+minutes+":"+seconds));
    }


    @Override
    public float getEstimatedDistanceOfSteps(long steps) {
        return (float) (steps * 78) / (float) 100000;
    }



    void checkPermissions(int requestCode, @NonNull int[] grantResults) {
        Log.d("TAG", "onRequestPermissionsResult: called.");
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            Log.d("TAG", "LocationPermissions: permission failed");
                            return;
                        }
                    }
                    Log.d("TAG", "LocationPermissions: permission granted");
                    try {
                        mapCallBack.call();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            case ACTIVITY_PERMISSION_CODE: {
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            Log.d("TAG", "ActivityRecognition : permission failed");
                            return;
                        }
                    }
                    Log.d("TAG", "ActivityRecognition: permission granted");
                    startCountSteps();
                }
            }
        }
    }
}
