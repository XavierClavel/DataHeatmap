package com.xavierclavel.datamapping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static MainActivity instance;
    public static TextView nbBluetoothDevicesDisplay;

    public static FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("__________________________________________", "start");
        findViewById(R.id.buttonMap).setOnClickListener(this);
        instance = this;
        nbBluetoothDevicesDisplay = (TextView) instance.findViewById(R.id.nbBluetoothDevices);
        //Start foreground service that will schedule the various Jobs.
        ContextCompat.startForegroundService(this, new Intent(this, ForegroundService.class));
        Log.d("main activity", "initiated");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.buttonMap:
                Intent intent = new Intent(this, MapActivity.class);
                startActivity(intent);
                break;
        }
    }

    public static void updateDashboard(int nbDevices) {
        nbBluetoothDevicesDisplay.setText("" + nbDevices);
    }
}