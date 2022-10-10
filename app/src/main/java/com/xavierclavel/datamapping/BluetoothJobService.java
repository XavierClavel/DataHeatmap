package com.xavierclavel.datamapping;


import android.Manifest;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
    IntentFilter filter;
    BluetoothAdapter bluetoothAdapter;
    static Context receiverContext;

    // Create a BroadcastReceiver for ACTION_FOUND.
    public static BroadcastReceiver receiver;

    // Méthode appelée quand la tâche est lancée
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("bluetooth job", "onStartJob id=" + params.getJobId());
        // ***** Lancer ici la mesure dans un thread à part *****

        try {
            checkPermission();
            if (receiver == null) {
                initializeReceiver();
            } else if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }

            instance = this;
            bluetoothJobParameters = params;
            bluetoothJob = bluetoothJob == null ? new BluetoothJob() : bluetoothJob;
            bluetoothJob.doInBackground();
        } catch (Exception e) {
            Log.d("bluetooth job", "failed on start");
        }
        return true;
    }

    void initializeReceiver() {
        Log.d("bluetooth job", "initializing receiver");
        receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                receiverContext = context;
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

                if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                    Log.d("discovery", "started");
                    ForegroundService.instance.unregisterReceiver(BluetoothJobService.receiver);
                }

                if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    Log.d("discovery", "completed");
                    ForegroundService.instance.unregisterReceiver(BluetoothJobService.receiver);
                }
            }
        };


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        //unregisterReceiver(receiver);
        registerReceiver(receiver, filter);
    }

    // Méthode appelée quand la tâche est arrêtée par le scheduler
// Retourne vrai si le scheduler doit relancer la tâche
    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("bluetooth job", "onStopJob id=" + params.getJobId());
// ***** Arrêter le thread du job ici ******
        //receiverContext.unregisterReceiver(receiver);
        return shouldReschedule;
    }

     void checkPermission() {
        /*if (ActivityCompat.checkSelfPermission(MainActivity.instance, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Bluetooth permission", "Refused");
            ActivityCompat.requestPermissions(MainActivity.instance, new String[] {Manifest.permission.BLUETOOTH_SCAN}, 564);
        }*/
         if (ActivityCompat.checkSelfPermission(MainActivity.instance, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
             Log.d("Bluetooth permission", "Refused");
         ActivityCompat.requestPermissions(MainActivity.instance, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 564);
         }
         if (ActivityCompat.checkSelfPermission(MainActivity.instance, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
             Log.d("Bluetooth permission", "Refused");
             ActivityCompat.requestPermissions(MainActivity.instance, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 564);
         }
    }





    class BluetoothJob extends AsyncTask<String, Integer, String> {
        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {
            Log.d("bluetooth job", "bluetooth job started");
            try {
                int nbBluetoothDevices = getBluetoothData();
                ForegroundService.displayToast();
            } catch (Exception e) {
                Log.d("bluetooth job", "failed to read bluetooth data");
            } finally {
                BluetoothJobService.instance.jobFinished(BluetoothJobService.bluetoothJobParameters, BluetoothJobService.shouldReschedule);
                //if (shouldReschedule) ForegroundService.scheduleJobBluetooth();
            }
            return "Done";
        }

        int getBluetoothData() {
            /*if (ActivityCompat.checkSelfPermission(instance, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.d("Connection", "Refused");
                ActivityCompat.requestPermissions(MainActivity.instance, new String[] {Manifest.permission.BLUETOOTH_CONNECT,}, 564);
            }*/

            //Set<BluetoothDevice> bondedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
            checkPermission();
            try {
                bluetoothAdapter.startDiscovery();
            } catch (Exception e) {
                Log.d("bluetooth discovery", "failure");
            }
            //Log.d("nb bluetooth devices","" + bondedDevices.size());
            return 0;
        }


    }



}
