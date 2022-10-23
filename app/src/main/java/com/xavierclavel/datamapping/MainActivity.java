package com.xavierclavel.datamapping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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

    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;

    boolean isMeasurementSaved = false;

    public static boolean settings_keepData = false;
    public static int settings_idMeasurement = 0;

    public static String cityName = "";

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
        switchKeepData = findViewById(R.id.switch_keepData);

        initializeUI();

        switchKeepData.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        findViewById(R.id.buttonData).setOnClickListener(this);
        findViewById(R.id.buttonHistory).setOnClickListener(this);
        instance = this;
        nbBluetoothDevicesDisplay = (TextView) instance.findViewById(R.id.nbBluetoothDevices);
        //Start foreground service that will schedule the various Jobs.
        ContextCompat.startForegroundService(this, new Intent(this, ForegroundService.class));
        Log.d("main activity", "initiated");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        checkPermission();
        TelephonyManager mTelephonyManager = (TelephonyManager)
                getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = mTelephonyManager.getNetworkType();
        Log.d("network type : ", ""+networkType);
        Log.d("LTE : ", ""+TelephonyManager.NETWORK_TYPE_LTE);

        /*
        4G -> 13 -> LTE
        H+ -> 15 -> HSPA+
        H -> 10 -> HSPA
        3G -> 3 -> UMTS
        EDGE -> 2
        GPRS -> 1
        GSM -> 16
        NONE -> 0

         */

        getLocalData();
    }

    void getLocalData() {
        mPrefs = getSharedPreferences("label", 0);
        mEditor = mPrefs.edit();

        settings_idMeasurement = mPrefs.getInt("nb_measurements",0);

        settings_keepData = mPrefs.getBoolean("keep_data", false);
        switchKeepData.setChecked(settings_keepData);
        if (settings_keepData) {    //continue existing measurement
            XmlManager.Read();      //access the data from the last measurement
        }
        else settings_idMeasurement++;    //new measurement
    }
    void checkPermission() {
        int permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        // Check for permissions
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            Log.d("permission", "Requesting Permissions");

            // Request permissions
            ActivityCompat.requestPermissions(MainActivity.instance,
                    new String[] {
                            Manifest.permission.READ_PHONE_STATE
                    }, 565);
        }
        else Log.d("permission", "Permissions Already Granted");
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

            case R.id.buttonData:
                Intent intent2 = new Intent(this, DataActivity.class);
                startActivity(intent2);
                break;

            case R.id.buttonWrite:
                List<MeasurementSummary> measurementSummaries = XmlManager.ReadHistory();
                String currentTime = Calendar.getInstance().getTime().toString();
                String filename = "measurement" + settings_idMeasurement;
                MeasurementSummary currentMeasurementSummary = new MeasurementSummary(currentTime, cityName, HeatmapManager.nbPoints+"", filename);
                if (isMeasurementSaved) {
                    measurementSummaries.set(measurementSummaries.size()-1,currentMeasurementSummary);
                }
                else measurementSummaries.add(currentMeasurementSummary);
                XmlManager.WriteHistory(measurementSummaries);
                isMeasurementSaved = true;
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

            case R.id.buttonHistory:
                Intent intent3 = new Intent(this, HistoryActivity.class);
                startActivity(intent3);
                break;

            case R.id.switch_keepData:
                mEditor.putBoolean("keep_data", switchKeepData.isChecked()).commit();
                settings_keepData = switchKeepData.isChecked();
        }
    }

    public static void updateDashboard(int downSpeed) {
        if (!appPaused) {
            //nbBluetoothDevicesDisplay.setText(downSpeedToNetwork(downSpeed));
        }
        instance.checkPermission();
        TelephonyManager mTelephonyManager = (TelephonyManager)
                instance.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = mTelephonyManager.getNetworkType();
        nbBluetoothDevicesDisplay.setText(DataActivity.technologyToNetwork(networkType));//com.xavierclavel.datamapping.networkType);
    }

    public static void getCityName(LatLng position) {
        Geocoder geoCoder = new Geocoder(instance, Locale.getDefault()); //it is Geocoder
        StringBuilder builder = new StringBuilder();
        try {
            List<Address> addresses = geoCoder.getFromLocation(position.latitude, position.longitude,10);

            for (Address adrs : addresses) {
                if (adrs != null) {

                    String locality = adrs.getLocality();
                    if (locality != null && !locality.equals("")) {
                        cityName = locality;
                        Log.d("city", cityName);
                        HeatmapManager.cityAcquired = true;
                        break;
                    }

                }
            }


            Log.d("address", addresses.get(0).toString());
            DataActivity.updateLocationData(addresses.get(0).toString());
        } catch (IOException e) {Log.d("address", "failure");}
        catch (NullPointerException e) {Log.d("address", "failure");}
    }


}