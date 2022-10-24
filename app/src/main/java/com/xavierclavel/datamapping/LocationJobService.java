package com.xavierclavel.datamapping;


import android.Manifest;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;

import java.util.Random;
import java.util.Set;

public class LocationJobService extends JobService {

    public static boolean shouldReschedule = true;
    LocationJob locationJob;
    public static LocationJobService instance;
    public static JobParameters locationJobParameters;

    // Méthode appelée quand la tâche est lancée
    @Override
    public boolean onStartJob(JobParameters params) {
        //shouldReschedule = true;
        Log.d("location job", "onStartJob id=" + params.getJobId());
        // ***** Lancer ici la mesure dans un thread à part *****
        instance = this;
        locationJobParameters = params;
        locationJob = locationJob == null ? new LocationJob() : locationJob;
        locationJob.doInBackground();
        return true;
    }

    // Méthode appelée quand la tâche est arrêtée par le scheduler
// Retourne vrai si le scheduler doit relancer la tâche
    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("location job", "onStopJob id=" + params.getJobId());
// ***** Arrêter le thread du job ici ******
        return shouldReschedule;
    }





    class LocationJob extends AsyncTask<String, Integer, String> {
        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {
            Log.d("location job", "location job started");
            try {
                getLocationData();
            } catch (Exception e) {
                Log.d("wifi job", "failed to read location data");
            } finally {
                LocationJobService.instance.jobFinished(LocationJobService.locationJobParameters, LocationJobService.shouldReschedule);
                if (shouldReschedule) ForegroundService.scheduleJobLocation();
            }
            return "Done";
        }

        void getLocationData() {
            checkPermission();

            MainActivity.fusedLocationClient.getCurrentLocation(LocationRequest.QUALITY_HIGH_ACCURACY, new CancellationToken() {
                @Override
                public boolean isCancellationRequested() {
                    return false;
                }

                @NonNull
                @Override
                public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                    return null;
                }
            }).addOnSuccessListener(MainActivity.instance, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Get current location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                        Log.d("location", "user location is : " + location.toString());
                        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                        Log.d("location job", "__________________________________________________________");
                        HeatmapManager.locationDataSocket(currentPosition);
                    }
                }
            });
            return;
        }
    }

    void checkPermission() {
        int permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permission3 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);

        // Check for permissions
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            Log.d("permission", "Requesting Permissions");

            // Request permissions
            ActivityCompat.requestPermissions(MainActivity.instance,
                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 565);
        }

        if (permission2 != PackageManager.PERMISSION_GRANTED) {
            Log.d("permission", "Requesting Permissions");

            // Request permissions
            ActivityCompat.requestPermissions(MainActivity.instance,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 567);
        }

        if (permission3 != PackageManager.PERMISSION_GRANTED) {
            Log.d("permission", "Requesting Permissions");

            // Request permissions
            ActivityCompat.requestPermissions(MainActivity.instance,
                    new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 566);
        }
        else Log.d("permission", "Permissions Already Granted");
    }



}
