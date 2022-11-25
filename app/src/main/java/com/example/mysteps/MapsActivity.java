package com.example.mysteps;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.example.mysteps.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SensorEventListener, LocationListener {

    private long steps = 0;
    private TextView distanceTextView, stepsTextView;//, totalTime;
    private ActivityController activityController;
    private GoogleMap currentGoogleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationManager locationManager;
    private ArrayList<LatLng> points;
    Polyline line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.mysteps.databinding.ActivityMapsBinding activityMapsBinding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(activityMapsBinding.getRoot());
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        initMap();
        initializeData();
    }

    void initializeData() {
        points = new ArrayList<>();
        stepsTextView = findViewById(R.id.numberOfStepTextViewValue);
        distanceTextView = findViewById(R.id.totalDistanceTextViewValue);
//      totalTime = findViewById(R.id.averageTimeTextViewValue);
        Button startButton = findViewById(R.id.startButton);
        Button endButton = findViewById(R.id.endButton);
        activityController = new ActivityController(this, this, () -> {
            points.clear();
            initMap();
            return null;
        });
        startButton.setOnClickListener(view -> activityController.startCountSteps());
        endButton.setOnClickListener(view -> {
            activityController.stopCountSteps();
            resetCounting();
            resetDisplayData();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            new ActivityPermissions(this).getLocationPermission();
            return;
        }
        currentGoogleMap = googleMap;
        currentGoogleMap.setMyLocationEnabled(true);
        currentGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng newCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
                currentGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newCoordinates, 17));
            }
        });
    }

    void resetCounting() {
        steps = 0;
        points.clear();
    }

    void resetDisplayData() {
        stepsTextView.setText(String.valueOf(0));
        distanceTextView.setText(String.valueOf(0));
//        totalTime.setText(0);
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
        handleDisplayData(steps);
    }

    void handleDisplayData(long steps) {
        stepsTextView.setText(String.valueOf(steps));
        float totalDistanceTravelled = steps > 2 ? activityController.getEstimatedDistanceOfSteps(steps) : 0;
        distanceTextView.setText(String.format("%s KM", totalDistanceTravelled));
//        totalTime.setText ( String.valueOf(totalDistanceTravelled /averagePace));
//        totalTime.setText(String.valueOf(activityController.getEstimatedDistanceOfSteps(steps)));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        activityController.checkPermissions(requestCode, grantResults);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        LatLng newCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
        currentGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newCoordinates, 17));
        points.add(newCoordinates);
        redrawLine();
    }

    private void redrawLine() {
        currentGoogleMap.clear();
        PolylineOptions options = new PolylineOptions().width(15).color(Color.BLUE).geodesic(true);
        for (int counter = 0; counter < points.size(); counter++) {
            LatLng point = points.get(counter);
            options.add(point);
        }

        line = currentGoogleMap.addPolyline(options);
    }

}