package com.example.projectapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class HomePage extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;


    private GoogleMap gMap;
    private Location location;
    private static final int CALL_PHONE_REQUEST_CODE = 101; // Define your request code here


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Create location request
        locationRequest = LocationRequest
                .create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000);  // 5 seconds

        // Initialize location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with current location
                    updateUI(location);
                }
            }
        };

        // Check location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Request location updates
            requestLocationUpdates();
        }

        // Set up the initial fragment
        Map_Fragment mapFragment = Map_Fragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.MY_MAP, mapFragment);
        transaction.commit();

        // Obtain the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment2 = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.MY_MAP);
        mapFragment2.getMapAsync(this);

        // Set up click listeners for emergency buttons
        Button btnCallPolice = findViewById(R.id.btnPolice);
        btnCallPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPolice();
            }
        });
        Button btnCallAmbulance = findViewById(R.id.btnAmbulance);
        btnCallAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAmbulance();
            }
        });
        Button btnCallFire = findViewById(R.id.btnFire);
        btnCallFire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFire();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    private void requestLocationUpdates() {
        // Check for permission before requesting location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, so request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission is granted, so request location updates
            try {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } catch (SecurityException e) {
                // Handle the case where the permission is denied by the user
                e.printStackTrace(); // For demonstration purposes; handle this exception according to your app's requirements
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        // Add markers, set camera position, etc.
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void updateUI(Location location) {
        TextView tvLocation = findViewById(R.id.tvLocation);
        tvLocation.setText("Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());

        // Move the camera to the user's location
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
    }

    // Method to handle button clicks for calling emergency services
    private void callPolice() {
        String phoneNumber = "0760622184"; // replace with the actual emergency number for your country
        callEmergencyNumber(phoneNumber);
    }

    private void callAmbulance() {
        String phoneNumber = "123456789"; // replace with the actual ambulance number for your area
        callEmergencyNumber(phoneNumber);
    }

    private void callFire() {
        String phoneNumber = "987654321"; // replace with the actual fire department number for your area
        callEmergencyNumber(phoneNumber);
    }

    private void callEmergencyNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // Request the CALL_PHONE permission if it's not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_REQUEST_CODE);
        } else {
            // Permission is granted, so make the call
            startActivity(intent);
        }
    }

    // Method to update the user's location manually
    public void updateLocation(View view) {
        // Check if location permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Get the last known location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // Update UI with current location
                            updateUI(location);
                        } else {
                            // Handle null location
                            Toast.makeText(HomePage.this, "Failed to get location, please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure to get location
                        Toast.makeText(HomePage.this, "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}