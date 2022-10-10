package com.xavierclavel.datamapping;

import com.google.android.gms.maps.model.LatLng;

public class TimestampedData {
    public String timestamp;
    public LatLng position;
    public int nbBluetoothDevices;

    public TimestampedData(String timestamp, LatLng position, int nbBluetoothDevices) {
        this.timestamp = timestamp;
        this.position = position;
        this.nbBluetoothDevices = nbBluetoothDevices;
    }
}
