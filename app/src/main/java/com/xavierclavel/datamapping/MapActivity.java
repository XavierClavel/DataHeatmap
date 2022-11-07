package com.xavierclavel.datamapping;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.xavierclavel.datamapping.databinding.ActivityMapBinding;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    public static GoogleMap mMap;
    private ActivityMapBinding binding;
    FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("map activity", "activity started");

        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("map", "STOPPED_________________________");
        HeatmapManager.mapReady = false;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("map activity", "on map ready");
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        centerMapOnUser();

        /*HeatmapManager.tableRow_4G = findViewById(R.id.tableRow_4G);
        HeatmapManager.tableRow_3G = findViewById(R.id.tableRow_3G);
        HeatmapManager.tableRow_Hplus = findViewById(R.id.tableRow_Hplus);
        HeatmapManager.tableRow_H = findViewById(R.id.tableRow_H);
        HeatmapManager.tableRow_E = findViewById(R.id.tableRow_E);
        HeatmapManager.tableRow_G = findViewById(R.id.tableRow_G);
        HeatmapManager.tableRow_None = findViewById(R.id.tableRow_None);*/

        /*
        if (!HeatmapManager.is_4G_initialized) HeatmapManager.tableRow_4G.setVisibility(View.GONE);
        if (!HeatmapManager.is_3G_initialized) HeatmapManager.tableRow_3G.setVisibility(View.GONE);
        if (!HeatmapManager.is_Hplus_initialized) HeatmapManager.tableRow_Hplus.setVisibility(View.GONE);
        if (!HeatmapManager.is_H_initialized) HeatmapManager.tableRow_H.setVisibility(View.GONE);
        if (!HeatmapManager.is_H_initialized) HeatmapManager.tableRow_E.setVisibility(View.GONE);
         */

        HeatmapManager.initializeHeatMap();


    }

    @SuppressLint("MissingPermission")
    void centerMapOnUser() {
        checkPermission();
        LocationJobService.checkPermission();
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(location.getLatitude(),
                                            location.getLongitude()), 17));
                        }
                    }
                });
        Log.d("map", "successfully acquired user location");
    }


    void checkPermission() {
        int permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        // Check for permissions
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            Log.d("permission", "Requesting Permissions");

            // Request permissions
            ActivityCompat.requestPermissions(MainActivity.instance,
                    new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                    }, 564);
        }
        else Log.d("permission", "Permissions Already Granted");
    }

}