package com.example.mysteps;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.example.mysteps.databinding.ActivityMapsBinding;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SensorEventListener {

    private long steps = 0;
    TextView distanceTextView, stepsTextView, totalTime;
    Button startButton, endButton;
    ActivityController activityController;
    SensorManager sensorManager;
    Sensor stepSensor;
    boolean isAppStartedToCount = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.mysteps.databinding.ActivityMapsBinding activityMapsBinding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(activityMapsBinding.getRoot());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        initializeData();
    }

    void initializeData() {
        stepsTextView = findViewById(R.id.numberOfStepTextViewValue);
        distanceTextView = findViewById(R.id.totalDistanceTextViewValue);
        totalTime = findViewById(R.id.averageTimeTextViewValue);
        startButton = findViewById(R.id.startButton);
        endButton = findViewById(R.id.endButton);
        activityController = new ActivityController(sensorManager, stepSensor, this, this, () -> {
            initMap();
            return null;
        });
        startButton.setOnClickListener(view -> activityController.startCountSteps());
        endButton.setOnClickListener(view -> {
            activityController.stopCountSteps();
            resetCounting();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            new ActivityPermissions(this).getLocationPermission();
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(googleMap.getCameraPosition());
        googleMap.moveCamera(cameraUpdate);
        /*
        LatLng latLng = googleMap.getCameraPosition().target;
        float zoomLevel = 9.5f;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
//        googleMap.moveCamera(cameraUpdate);
         */
    }

    void  resetCounting (){
        isAppStartedToCount = false;
        steps = 0;
        stepsTextView.setText(0);
        distanceTextView.setText(0);
        totalTime.setText(0);
    }
    @Override
    protected void onResume() {
        super.onResume();
        activityController.startCountSteps();
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityController.stopCountSteps();
    }


    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        float[] values = event.values;
        int value = -1;

        if (values.length > 0) {
            value = (int) values[0];
        }

        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            steps++;
        }
        stepsTextView.setText(String.valueOf(steps));
        float totalDistanceTravelled= activityController.getEstimatedDistanceOfSteps(steps);
        distanceTextView.setText(String.valueOf(totalDistanceTravelled));
//        double averagePace =  (steps != 0) ? totalDistanceTravelled / (steps) : 0.0;
//        totalTime.setText ( String.valueOf(totalDistanceTravelled /averagePace));
//                totalTime.setText(String.valueOf(activityController.getEstimatedDistanceOfSteps(steps)));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        activityController.checkPermissions(requestCode, grantResults);
    }

}