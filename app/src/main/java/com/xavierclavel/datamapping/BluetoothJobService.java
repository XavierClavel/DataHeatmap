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
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.Set;

public class BluetoothJobService extends JobService {

    public static boolean shouldReschedule = true;
    BluetoothJob bluetoothJob;
    public static BluetoothJobService instance;
    public static JobParameters bluetoothJobParameters;

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                try {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    Log.d("bluetooth device", deviceName);
                } catch (SecurityException e) {
                    Log.d("bluetooth discovery", "security issue");
                }
            }
        }
    };

    // Méthode appelée quand la tâche est lancée
    @Override
    public boolean onStartJob(JobParameters params) {
        //shouldReschedule = true;
        Log.d("wifi job", "onStartJob id=" + params.getJobId());
        // ***** Lancer ici la mesure dans un thread à part *****
        instance = this;
        bluetoothJobParameters = params;
        bluetoothJob = bluetoothJob == null ? new BluetoothJob() : bluetoothJob;
        bluetoothJob.doInBackground();
        return true;
    }

    // Méthode appelée quand la tâche est arrêtée par le scheduler
// Retourne vrai si le scheduler doit relancer la tâche
    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("wifi job", "onStopJob id=" + params.getJobId());
        shouldReschedule = false;
// ***** Arrêter le thread du job ici ******
        return shouldReschedule;
    }





    class BluetoothJob extends AsyncTask<String, Integer, String> {
        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {
            Log.d("wifi job", "wifi job started");
            try {
                int nbBluetoothDevices = getBluetoothData();
                MainActivity.updateDashboard(nbBluetoothDevices);
                ForegroundService.displayToast();
            } catch (Exception e) {
                Log.d("wifi job", "failed to read wifi data");
            } finally {
                BluetoothJobService.instance.jobFinished(BluetoothJobService.bluetoothJobParameters, BluetoothJobService.shouldReschedule);
                ForegroundService.scheduleJobBluetooth();
            }
            return "Done";
        }

        int getBluetoothData() {
            /*if (ActivityCompat.checkSelfPermission(instance, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.d("Connection", "Refused");
                ActivityCompat.requestPermissions(MainActivity.instance, new String[] {Manifest.permission.BLUETOOTH_CONNECT,}, 564);
            }*/
            if (ActivityCompat.checkSelfPermission(instance, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                Log.d("Connection", "Refused");
                ActivityCompat.requestPermissions(MainActivity.instance, new String[] {Manifest.permission.BLUETOOTH_CONNECT,}, 564);
            }
            //Set<BluetoothDevice> bondedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
            BluetoothAdapter.getDefaultAdapter().startDiscovery();
            //Log.d("nb bluetooth devices","" + bondedDevices.size());
            return 0;//bondedDevices.size();
        }
    }



}
