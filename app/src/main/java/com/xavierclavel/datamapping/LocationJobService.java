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
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;

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
        Log.d("wifi job", "onStopJob id=" + params.getJobId());
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
                //ForegroundService.displayToast();
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
            MainActivity.fusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.instance, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                Log.d("location", "user location is : " + location.toString());
                            }
                        }
                    });
            return;
        }
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
