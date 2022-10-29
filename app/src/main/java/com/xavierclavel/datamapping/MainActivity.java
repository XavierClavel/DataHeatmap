package com.xavierclavel.datamapping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //TODO : prevent data points from being too close d > 5m to avoid having too much useless data
    //TODO : use threads to read data from xml file to prevent blocking the app in case of large measurements
    //TODO : set map in dark mode ?

    public static MainActivity instance;
    public static TextView networkDisplay;

    public static FusedLocationProviderClient fusedLocationClient;

    public static boolean appPaused = false;

    ImageButton buttonPause;
    ImageButton buttonStop;
    ImageButton buttonStart;
    ImageButton buttonDownload;
    ImageButton buttonWrite;
    static ProgressBar progressBar;
    static ProgressBar idleProgressBar;
    static ObjectAnimator animation;
    public static TextView nbMeasurementsDisplay;


    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;

    boolean isMeasurementSaved = false;

    public static boolean settings_keepData = false;
    public static int settings_idMeasurement = 0;

    public static String cityName = "";

    static ColorStateList green = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_enabled}},
            new int[] {Color.rgb(139,195,74)}
        );

    static ColorStateList red = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_enabled}},
            new int[] {Color.rgb(184,82,82)}
        );

    static ColorStateList writeStateList = new ColorStateList(
            new int[][]{
                    new int[]{android.R.attr.state_enabled},
                    new int[] {android.R.attr.state_pressed}
            },

            new int[] {
                    Color.rgb(200,200,200),
                    Color.rgb(139,195,74)
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        idleProgressBar = findViewById(R.id.progressBarIdle);
        animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 1000); // see this max value coming back here, we animate towards that value

        Log.d("__________________________________________", "start");
        buttonPause = findViewById(R.id.buttonPause);
        buttonStop = findViewById(R.id.buttonStop);
        buttonDownload = findViewById(R.id.buttonDownload);
        buttonWrite = findViewById(R.id.buttonWrite);
        progressBar = findViewById(R.id.progressBar);
        buttonStart = findViewById(R.id.buttonPlay);

        nbMeasurementsDisplay = findViewById(R.id.nbMeasurementsDisplay);
        networkDisplay = findViewById(R.id.networkDisplay);

        buttonPause.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        buttonDownload.setOnClickListener(this);
        buttonWrite.setOnClickListener(this);
        buttonStart.setOnClickListener(this);
        findViewById(R.id.buttonData).setOnClickListener(this);
        findViewById(R.id.buttonHistory).setOnClickListener(this);
        findViewById(R.id.buttonMap).setOnClickListener(this);

        buttonWrite.setBackgroundTintList(writeStateList);
        buttonWrite.setImageTintList(writeStateList);

        instance = this;
        //Start foreground service that will schedule the various Jobs.
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

    void StartMeasurements() {
        ContextCompat.startForegroundService(this, new Intent(this, ForegroundService.class));
        Animate();
    }

    public static void Animate()
    {
        animation.cancel();
        progressBar.setProgress(0);
        animation.setDuration(5000); // in milliseconds
        animation.start();
    }

    void getLocalData() {
        mPrefs = getSharedPreferences("label", 0);
        mEditor = mPrefs.edit();

        settings_idMeasurement = mPrefs.getInt("nb_measurements",0);
        Log.d("id", settings_idMeasurement+"");
        if (settings_idMeasurement == 0) mEditor.putInt("nb_measurements", 0);
        mEditor.commit();

        //settings_keepData = false;
        settings_keepData = mPrefs.getBoolean("keep_data", false);
        Log.d("keep data", settings_keepData + "");
        if (settings_keepData) {    //continue existing measurement
            buttonDownload.setBackgroundTintList(green);    //change color to reflect the parameter
            buttonDownload.setImageTintList(green);
            List<TimestampedData> timestampedDataList = XmlManager.Read(XmlManager.defaultFilename);      //access the data from the last measurement
            for (TimestampedData timestampedData: timestampedDataList) {
                Log.d("previous data", timestampedData.position + " " + timestampedData.network);
                HeatmapManager.addDataPoint(timestampedData.position, timestampedData.network); //plot data
            }
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

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.buttonPlay:
                findViewById(R.id.top_bar).setVisibility(View.VISIBLE);
                buttonStart.setVisibility(View.INVISIBLE);
                buttonStop.setVisibility(View.GONE);
                buttonPause.setVisibility(View.VISIBLE);
                networkDisplay.setVisibility(View.VISIBLE);

                appPaused = false;
                idleProgressBar.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                //TODO : set image to pause icon
                networkDisplay.setText("-");

                StartMeasurements();

                break;
            case R.id.buttonMap:
                Intent intent = new Intent(this, MapActivity.class);
                startActivity(intent);
                break;

            case R.id.buttonData:
                Intent intent2 = new Intent(this, DataActivity.class);
                startActivity(intent2);
                break;



            case R.id.buttonWrite:
                if (HeatmapManager.nbPoints == 0) break;
                Log.d("measurement already saved", isMeasurementSaved+"");
                Log.d("current id", settings_idMeasurement+"");
                List<MeasurementSummary> measurementSummaries = XmlManager.ReadHistory();
                String currentTime = Calendar.getInstance().getTime().toString();
                String filename = "measurement" + settings_idMeasurement;
                MeasurementSummary currentMeasurementSummary = new MeasurementSummary(currentTime, cityName, HeatmapManager.nbPoints+"", filename);
                if (isMeasurementSaved) {
                    measurementSummaries.set(measurementSummaries.size()-1,currentMeasurementSummary);
                }
                else {
                    measurementSummaries.add(currentMeasurementSummary);
                    mEditor.putInt("nb_measurements", settings_idMeasurement);
                    mEditor.commit();
                    Log.d("saved id",""+ mPrefs.getInt("nb_measurements", 999));
                }
                XmlManager.WriteHistory(measurementSummaries);  //write info detail about this measurement, including filename
                XmlManager.Write(filename); //write the measurement in an xml file
                isMeasurementSaved = true;
                break;

            case R.id.buttonPause:
                buttonPause.setVisibility(View.GONE);
                buttonStop.setVisibility(View.VISIBLE);
                appPaused = true;
                progressBar.setVisibility(View.INVISIBLE);  //change visible progress bar to show idle state
                idleProgressBar.setVisibility(View.VISIBLE);
                networkDisplay.setVisibility(View.INVISIBLE);

                buttonStart.setVisibility(View.VISIBLE);
                //TODO : set image to stop icon

                progressBar.setProgress(0);
                animation.cancel();

                networkDisplay.setText("");

                ForegroundService.stopService();
                ForegroundService.notificationTitle = "Scanning paused";
                ForegroundService.updateNotification();
                break;

            case R.id.buttonStop:
                buttonStop.setVisibility(View.GONE);
                buttonPause.setVisibility(View.VISIBLE);
                networkDisplay.setText("-");
                networkDisplay.setVisibility(View.INVISIBLE);
                findViewById(R.id.top_bar).setVisibility(View.INVISIBLE);
                HeatmapManager.ResetHeatmap();
                break;

            case R.id.buttonHistory:
                Intent intent3 = new Intent(this, HistoryActivity.class);
                startActivity(intent3);
                break;

            case R.id.buttonDownload:
                settings_keepData = !settings_keepData;
                mEditor.putBoolean("keep_data", settings_keepData);
                mEditor.commit();
                if (settings_keepData) {
                    buttonDownload.setBackgroundTintList(green);
                    buttonDownload.setImageTintList(green);

                    XmlManager.Write(XmlManager.defaultFilename);   //save data
                }
                else {
                    buttonDownload.setBackgroundTintList(red);
                    buttonDownload.setImageTintList(red);
                }
        }
    }

    public static void updateDashboard(int downSpeed) {
        if (!appPaused) {
            instance.checkPermission();
            TelephonyManager mTelephonyManager = (TelephonyManager) instance.getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission") int networkType = mTelephonyManager.getNetworkType();
            networkDisplay.setText(DataActivity.technologyToNetwork(networkType));//com.xavierclavel.datamapping.networkType);
        }
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
            //DataActivity.updateLocationData(addresses.get(0).toString());
        } catch (IOException e) {Log.d("address", "failure");}
    }


}