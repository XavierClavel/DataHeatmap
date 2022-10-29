package com.xavierclavel.datamapping;

import android.Manifest;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MobileNetworkJobService extends JobService  {
    public static boolean shouldReschedule = true;
    MobileNetworkJob wiFiJob;
    public static MobileNetworkJobService instance;
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
        wiFiJob = wiFiJob == null ? new MobileNetworkJob() : wiFiJob;
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





    class MobileNetworkJob extends AsyncTask<String, Integer, String> {
        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {
            Log.d("wifi job", "wifi job started");
            try {
                int networkType = getMobileNetwork();

                ConnectivityManager cm = (ConnectivityManager) MainActivity.instance.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                //should check null because in airplane mode it will be null
                NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
                int downSpeed = nc.getLinkDownstreamBandwidthKbps();
                int upSpeed = nc.getLinkUpstreamBandwidthKbps();

                Log.d("network job ", "network type is " + networkType);
                Log.d("network job ", "________________________________________________________________________");

                HeatmapManager.mobileNetworkDataSocket(networkType, downSpeed, upSpeed);

            } catch (Exception e) {
                Log.d("wifi job", "failed to read wifi data");
            } finally {
                MobileNetworkJobService.instance.jobFinished(MobileNetworkJobService.wifiJobParameters, MobileNetworkJobService.shouldReschedule);
                //if (shouldReschedule) ForegroundService.scheduleJobWiFi();
                ForegroundService.JobMobileNetworkOver();
            }
            return "Done";
        }
    }

    int getMobileNetwork() {
        checkMobileNetworkPermission();
        TelephonyManager mTelephonyManager = (TelephonyManager)
                getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyManager.getNetworkType();
    }

    void checkMobileNetworkPermission() {
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

}
