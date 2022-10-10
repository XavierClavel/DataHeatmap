package com.xavierclavel.datamapping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static MainActivity instance;
    public static TextView nbBluetoothDevicesDisplay;

    public static FusedLocationProviderClient fusedLocationClient;

    public static boolean appPaused = false;
    Button buttonStop;
    ProgressBar progressBar;
    Switch switchKeepData;
    ColorStateList green;
    ColorStateList red;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 500); // see this max value coming back here, we animate towards that value
        animation.setDuration(5000); // in milliseconds
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        Log.d("__________________________________________", "start");
        findViewById(R.id.buttonMap).setOnClickListener(this);
        findViewById(R.id.buttonWrite).setOnClickListener(this);
        buttonStop = findViewById(R.id.buttonStop);
        progressBar = findViewById(R.id.progressBar);
        switchKeepData = findViewById(R.id.switch1);

        initializeUI();

        buttonStop.setOnClickListener(this);
        findViewById(R.id.buttonData).setOnClickListener(this);
        instance = this;
        nbBluetoothDevicesDisplay = (TextView) instance.findViewById(R.id.nbBluetoothDevices);
        //Start foreground service that will schedule the various Jobs.
        ContextCompat.startForegroundService(this, new Intent(this, ForegroundService.class));
        Log.d("main activity", "initiated");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    void initializeUI() {
        green = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_enabled}},
                new int[] {Color.rgb(139,195,74)}
        );

        red = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_enabled}},
                new int[] {Color.rgb(184,82,82)}
        );

        ColorStateList buttonStates = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        Color.rgb(139,195,74),
                        Color.rgb(184,82,82)
                }
        );
        switchKeepData.getThumbDrawable().setTintList(buttonStates);
        switchKeepData.getTrackDrawable().setTintList(buttonStates);

    }

    public void onClick(View view) {




        switch(view.getId()) {
            case R.id.buttonMap:
                Intent intent = new Intent(this, MapActivity.class);
                startActivity(intent);
                break;

            case R.id.buttonWrite:
                XmlManager.Write();
                break;

            case R.id.buttonStop:
                appPaused = !appPaused;
                if (appPaused) {
                    buttonStop.setBackgroundTintList(green);
                    nbBluetoothDevicesDisplay.setText("x");
                    buttonStop.setText("start");
                    //progressBar.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                    ForegroundService.stopService();
                }
                else {
                    buttonStop.setBackgroundTintList(red);
                    nbBluetoothDevicesDisplay.setText("-");
                    buttonStop.setText("stop");
                    ContextCompat.startForegroundService(this, new Intent(this, ForegroundService.class));
                }
                break;

            case R.id.buttonData:
                //
                break;
        }
    }

    public static void updateDashboard(int nbDevices) {
        if (!appPaused) nbBluetoothDevicesDisplay.setText("" + nbDevices);
    }
}