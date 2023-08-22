package com.example.googlemap;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    GoogleMap Gmap;
    FrameLayout map;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        map = findViewById(R.id.google_map);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        checkLocationSettingsAndGetCurrentLocation();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Check if location is enabled again when the app resumes
        checkLocationSettingsAndGetCurrentLocation();
    }

    private void checkLocationSettingsAndGetCurrentLocation() {
        if (!isLocationEnabled()) {
            // If location is not enabled, show a dialog to enable it
            Toast.makeText(this, "Please enable location", Toast.LENGTH_LONG).show();
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        } else {
            Intent serviceIntent = new Intent(this, ForegroundService.class);
            startService(serviceIntent);
            getCurrentLocation();
        }
    }

    private boolean isLocationEnabled() {
        // Check if location setting is enabled
        int mode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
        return mode != Settings.Secure.LOCATION_MODE_OFF;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Gmap = googleMap;
        if (currentLocation != null) {
            LatLng pakMap = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            Gmap.addMarker(new MarkerOptions().position(pakMap).title("Current Location"));
            Gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(pakMap, 10));
            LocationHelper Helper=new LocationHelper(currentLocation.getLatitude(),currentLocation.getLongitude());
            FirebaseDatabase.getInstance().getReference("Current Location")
                    .setValue(Helper).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "Location Saved", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(MainActivity.this, "Location not saved", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {
            // Permission granted or already granted, get current location
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(@NonNull Location location) {
                    if (location != null) {
                        currentLocation = location;
                        Log.d("MapActivity", "Latitude: " + currentLocation.getLatitude() + ", Longitude: " + currentLocation.getLongitude());

                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
                        assert mapFragment != null;
                        mapFragment.getMapAsync(MainActivity.this);
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, now start the service and update the map
                    Intent serviceIntent = new Intent(this, ForegroundService.class);
                    startService(serviceIntent);
                    // Permission granted, now update the map
                    getCurrentLocation();
                }
                break;
        }
    }
}
