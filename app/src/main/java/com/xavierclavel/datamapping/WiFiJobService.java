package com.xavierclavel.datamapping;

import android.Manifest;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.xavierclavel.datamapping.ForegroundService;
import com.xavierclavel.datamapping.MainActivity;

import java.util.List;

public class WiFiJobService extends JobService  {
    public static boolean shouldReschedule = true;
    WiFiJob wiFiJob;
    public static WiFiJobService instance;
    public static JobParameters wifiJobParameters;
    WifiManager wifiMan;

    // Méthode appelée quand la tâche est lancée
    @Override
    public boolean onStartJob(JobParameters params) {
        //shouldReschedule = true;
        Log.d("wifi job", "onStartJob id=" + params.getJobId());
        // ***** Lancer ici la mesure dans un thread à part *****
        wifiMan = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        instance = this;
        wifiJobParameters = params;
        wiFiJob = wiFiJob == null ? new WiFiJob() : wiFiJob;
        wiFiJob.doInBackground();
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





    class WiFiJob extends AsyncTask<String, Integer, String> {
        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {
            Log.d("wifi job", "wifi job started");
            try {
                wifiMan = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                int permission1 = ContextCompat.checkSelfPermission(WiFiJobService.instance, Manifest.permission.ACCESS_COARSE_LOCATION);

                // Check for permissions
                if (permission1 != PackageManager.PERMISSION_GRANTED) {
                    Log.d("permission", "Requesting Permissions");

                    // Request permissions
                    ActivityCompat.requestPermissions(MainActivity.instance,
                            new String[] {
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_WIFI_STATE,
                                    Manifest.permission.ACCESS_NETWORK_STATE
                            }, 564);
                }
                else Log.d("permission", "Permissions Already Granted");

                List<ScanResult> scanResults = wifiMan.getScanResults();

                Log.d("Debug____________", "");
                Log.d("wifi job", "successfully read wifi data");
                Log.d("wifi job", "amount of data : " + scanResults.size());
                HeatmapManager.bluetoothDataSocket(scanResults.size());

            } catch (Exception e) {
                Log.d("wifi job", "failed to read wifi data");
            } finally {
                WiFiJobService.instance.jobFinished(WiFiJobService.wifiJobParameters, WiFiJobService.shouldReschedule);
                if (shouldReschedule) ForegroundService.scheduleJobWiFi();
            }
            return "Done";
        }
    }

}
