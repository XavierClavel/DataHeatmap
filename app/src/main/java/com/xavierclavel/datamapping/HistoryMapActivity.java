package com.xavierclavel.datamapping;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
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
import com.xavierclavel.datamapping.databinding.ActivityHistoryMapBinding;
import com.xavierclavel.datamapping.databinding.ActivityMapBinding;

public class HistoryMapActivity extends FragmentActivity implements OnMapReadyCallback {

    public static GoogleMap mMap;
    private ActivityMapBinding binding;
    FusedLocationProviderClient fusedLocationClient;
    public static LatLng firstLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("history map activity", "activity started");

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

        centerMapOnLocation();

        HistoryHeatmapManager.tableRow_4G = findViewById(R.id.tableRow_4G);
        HistoryHeatmapManager.tableRow_3G = findViewById(R.id.tableRow_3G);
        HistoryHeatmapManager.tableRow_Hplus = findViewById(R.id.tableRow_Hplus);
        HistoryHeatmapManager.tableRow_H = findViewById(R.id.tableRow_H);
        HistoryHeatmapManager.tableRow_E = findViewById(R.id.tableRow_E);
        HistoryHeatmapManager.tableRow_G = findViewById(R.id.tableRow_G);
        HistoryHeatmapManager.tableRow_None = findViewById(R.id.tableRow_None);

        /*
        if (!HeatmapManager.is_4G_initialized) HeatmapManager.tableRow_4G.setVisibility(View.GONE);
        if (!HeatmapManager.is_3G_initialized) HeatmapManager.tableRow_3G.setVisibility(View.GONE);
        if (!HeatmapManager.is_Hplus_initialized) HeatmapManager.tableRow_Hplus.setVisibility(View.GONE);
        if (!HeatmapManager.is_H_initialized) HeatmapManager.tableRow_H.setVisibility(View.GONE);
        if (!HeatmapManager.is_H_initialized) HeatmapManager.tableRow_E.setVisibility(View.GONE);
         */

        HistoryHeatmapManager.initializeHeatMap();


    }

    void centerMapOnLocation() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 17));
    }


    //build heatmap
}