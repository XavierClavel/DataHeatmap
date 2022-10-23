package com.xavierclavel.datamapping;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataActivity extends AppCompatActivity {
    static DataActivity instance;
    static boolean activityInitialized = false;
    static TextView locationDisplay;
    static TextView downSpeedDisplay;
    static TextView upSpeedDisplay;
    static TextView mobileNetworkDisplay;
    static TextView networkTypeDisplay;
    static TextView networkDetailDisplay;
    static TextView locationDataDisplay;
    public static LatLng lastLocation = null;
    static Integer lastDownSpeed = null;
    static Integer lastUpSpeed = null;
    static String lastMobileNetwork = null;
    static String lastNetworkType = null;
    static String lastNetworkDetail = null;
    static String lastLocationData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        instance = this;
        locationDisplay = findViewById(R.id.locationDisplay);
        downSpeedDisplay = findViewById(R.id.downSpeedDisplay);
        upSpeedDisplay = findViewById(R.id.upSpeedDisplay);
        mobileNetworkDisplay = findViewById(R.id.mobileNetworkDisplay);
        networkTypeDisplay = findViewById(R.id.networkTypeDisplay);
        networkDetailDisplay = findViewById(R.id.networkDetailDisplay);
        locationDataDisplay = findViewById(R.id.locationDataDisplay);

        activityInitialized = true;
        if (lastLocation != null) locationDisplay.setText(lastLocation.toString());
        if (lastDownSpeed != null) downSpeedDisplay.setText(lastDownSpeed + " kbps");
        if (lastUpSpeed != null) upSpeedDisplay.setText(lastUpSpeed + " kbps");
        if (lastMobileNetwork != null) mobileNetworkDisplay.setText(lastMobileNetwork);
        if (lastNetworkType != null) networkTypeDisplay.setText(lastNetworkType);
        if (lastNetworkDetail != null) networkDetailDisplay.setText(lastNetworkDetail);
        if (lastLocationData != null) locationDataDisplay.setText(lastLocationData);
    }


    static void updateLocationDisplay(LatLng location) {
        lastLocation = location;
        if (!activityInitialized) return;
        locationDisplay.setText(location.toString());
    }

    static void updateMobileNetworkDisplay(int networkType, int downSpeed, int upSpeed) {
        if (lastDownSpeed != null && Objects.equals(downSpeed, lastDownSpeed)) return; //data has not changed
        lastDownSpeed = downSpeed;
        lastUpSpeed = upSpeed;
        lastMobileNetwork = DataActivity.technologyToNetwork(networkType);
        lastNetworkType = networkTypeToString(networkType);
        lastNetworkDetail = networkTypeToStringDetail(networkType);
        if (!activityInitialized) return;
        downSpeedDisplay.setText(downSpeed + " kbps");
        upSpeedDisplay.setText(upSpeed + " kbps");
        mobileNetworkDisplay.setText(lastMobileNetwork);
        networkDetailDisplay.setText(lastNetworkDetail);

        Log.d("network technology : ", networkTypeToString(networkType));
        networkTypeDisplay.setText(networkTypeToString(networkType));
    }

    public static void updateLocationData(String data) {
        TextView locationDataDisplay = instance.findViewById(R.id.locationDataDisplay);
        locationDataDisplay.setText(data);
        lastLocationData = data;
    }

    static String networkTypeToString(int networkType) {
        switch (networkType) {
            case 0 :
                return "Unknown";
            case 1 :
                return "GPRS";
            case 2 :
                return "EDGE";
            case 3 :
                return "UMTS";
            case 4 :
                return "CDMA";
            case 5 :
                return "EVDO revision 0";
            case 6 :
                return "EVDO revision A";
            case 7 :
                return "EVD0 revision B";
            case 8 :
                return "HSDPA";
            case 9 :
                return "HSUPA";
            case 10 :
                return "HSPA";
            case 11 :
                return "iDEN";
            case 13 :
                return "LTE";
            case 14 :
                return "EHRPD";
            case 15 :
                return "HSPA+";
            case 16 :
                return "GSM";
            case 17 :
                return "TD_SCMA";
            case 18 :
                return "IWLAN";
            case 20 :
                return "NR (5G)";
        }
        return "Error";
    }

    static String networkTypeToStringDetail(int networkType) {
        switch (networkType) {
            case 0 :
                return "Unknown";
            case 1 :
                return "General Packet Radio Service"; //2G ----> G
            case 2 :
                return "Enhanced Data Rates for GSM Evolution"; //2G
            case 3 :
                return "Universal Mobile Telecommunications System"; //3G
            case 4 :
                return "Code Division Multiple Access"; //3G
            case 5 :
                return "Evolution Data Optimized revision 0"; //3G
            case 6 :
                return "Evolution Data Optimized revision A"; //3G
            case 7 :
                return "Evolution Data Optimized revision B"; //3G
            case 8 :
                return "High Speed Downlink Packet Access"; //H = 3.5G = 3G+
            case 9 :
                return "High Speed Uplink Packet Access";   //H
            case 10 :
                return "High Speed Packet Access";  //H
            case 11 :
                return "Integrated Digital Enhanced Network"; //3G
            case 13 :
                return "Long Term Evolution"; //4G
            case 15 :
                return "High Speed Packet Access +"; //H+ < 4G
            case 16 :
                return "Global System for Mobile Communication"; //2G
            case 17 :
                return "Time Division Synchronous Code Division Multiple Access"; //3G
            case 18 :
                return "Industrial Wireless Local Area Network";
            case 20 :
                return "New Radio"; //5G
        }
        return "Error";
    }

    public static String technologyToNetwork(int networkType) {
        if (networkType == 0) return "x";
        if (networkType == 1) return "G";
        if (networkType == 2) return "E";
        if (networkType <= 7 || networkType == 11 || networkType == 17) return "3G";
        if (networkType <= 10) return "H";
        if (networkType == 13) return "4G";
        if (networkType == 15) return "H+";
        if (networkType == 16) return "2G";
        if (networkType == 20) return "4G";
        return "x";
    }
}