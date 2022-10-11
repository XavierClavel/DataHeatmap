package com.xavierclavel.datamapping;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataActivity extends AppCompatActivity {
    static DataActivity instance;
    static boolean activityInitialized = false;
    static TextView locationDisplay;
    static TextView downSpeedDisplay;
    static TextView upSpeedDisplay;
    static TextView mobileNetworkDisplay;
    static LatLng lastLocation = null;
    static Integer lastDownSpeed = null;
    static Integer lastUpSpeed = null;
    static String lastMobileNetwork = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        instance = this;
        locationDisplay = findViewById(R.id.locationDisplay);
        downSpeedDisplay = findViewById(R.id.downSpeedDisplay);
        upSpeedDisplay = findViewById(R.id.upSpeedDisplay);
        mobileNetworkDisplay = findViewById(R.id.mobileNetworkDisplay);

        activityInitialized = true;
        if (lastLocation != null) locationDisplay.setText(lastLocation.toString());
        if (lastDownSpeed != null) downSpeedDisplay.setText(""+lastDownSpeed);
        if (lastUpSpeed != null) upSpeedDisplay.setText(""+lastUpSpeed);
        if (lastMobileNetwork != null) mobileNetworkDisplay.setText(lastMobileNetwork);
    }


    static void updateLocationDisplay(LatLng location) {
        lastLocation = location;
        if (!activityInitialized) return;
        locationDisplay.setText(location.toString());
    }

    static void updateMobileNetworkDisplay(int downSpeed, int upSpeed) {
        if (lastDownSpeed != null && Objects.equals(downSpeed, lastDownSpeed)) return; //data has not changed
        lastDownSpeed = downSpeed;
        lastUpSpeed = upSpeed;
        if (!activityInitialized) return;
        downSpeedDisplay.setText(""+downSpeed);
        upSpeedDisplay.setText(""+upSpeed);
        lastMobileNetwork = MainActivity.downSpeedToNetwork(downSpeed);
        mobileNetworkDisplay.setText(lastMobileNetwork);


    }
}