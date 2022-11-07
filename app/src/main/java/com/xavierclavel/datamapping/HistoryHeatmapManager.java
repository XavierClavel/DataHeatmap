package com.xavierclavel.datamapping;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TableRow;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;

public class HistoryHeatmapManager {

    public static List<TimestampedData> data;

    static LatLng locationData;
    static Integer mobileNetworkDataType;

    public static MobileNetworkHandler networkHandler_5G = new MobileNetworkHandler(HeatmapManager.color_5G, false);
    public static MobileNetworkHandler networkHandler_4G = new MobileNetworkHandler(HeatmapManager.color_4G, false);
    public static MobileNetworkHandler networkHandler_3G = new MobileNetworkHandler(HeatmapManager.color_3G, false);
    public static MobileNetworkHandler networkHandler_Hplus = new MobileNetworkHandler(HeatmapManager.color_Hplus, false);
    public static MobileNetworkHandler networkHandler_H = new MobileNetworkHandler(HeatmapManager.color_H, false);
    public static MobileNetworkHandler networkHandler_E = new MobileNetworkHandler(HeatmapManager.color_E, false);
    public static MobileNetworkHandler networkHandler_G = new MobileNetworkHandler(HeatmapManager.color_G, false);
    public static MobileNetworkHandler networkHandler_None = new MobileNetworkHandler(HeatmapManager.color_None, false);

    public static TableRow tableRow_4G;
    public static TableRow tableRow_3G;
    public static TableRow tableRow_Hplus;
    public static TableRow tableRow_H;
    public static TableRow tableRow_E;
    public static TableRow tableRow_G;
    public static TableRow tableRow_None;

    public static GoogleMap map;

    //TODO : use the MobileNetworkHandler class

    public static void initializeHeatMap() {    //gets executed when the map is displayed
        map = HistoryMapActivity.mMap;

        addData();
    }

    static void addData() {
        networkHandler_5G.data = new ArrayList<>();
        networkHandler_4G.data = new ArrayList<>();
        networkHandler_3G.data = new ArrayList<>();
        networkHandler_Hplus.data = new ArrayList<>();
        networkHandler_H.data = new ArrayList<>();
        networkHandler_E.data = new ArrayList<>();
        networkHandler_G.data = new ArrayList<>();
        networkHandler_None.data = new ArrayList<>();

        for (TimestampedData dataPoint : data) {
            addDataPoint(dataPoint.position, dataPoint.network);
        }

        if (networkHandler_5G.data.size() != 0) networkHandler_5G.InitializeTileOverlay();
        if (networkHandler_4G.data.size() != 0)
            networkHandler_4G.InitializeTileOverlay();  //display the 4G heatmap
        if (networkHandler_3G.data.size() != 0) networkHandler_3G.InitializeTileOverlay();
        if (networkHandler_Hplus.data.size() != 0) networkHandler_Hplus.InitializeTileOverlay();
        if (networkHandler_H.data.size() != 0) networkHandler_H.InitializeTileOverlay();
        if (networkHandler_E.data.size() != 0) networkHandler_E.InitializeTileOverlay();
        if (networkHandler_G.data.size() != 0) networkHandler_G.InitializeTileOverlay();
        if (networkHandler_None.data.size() != 0) networkHandler_None.InitializeTileOverlay();

    }

    public static void addDataPoint(LatLng location, int networkType) {
        locationData = location;
        mobileNetworkDataType = networkType;
        updateHeatmap();
    }

    static void updateHeatmap() {

        Log.d("heatmap manager", "new data point acquired");

        technologyToHeatmap(mobileNetworkDataType);

        locationData = null;
        mobileNetworkDataType = null;
    }

    public static void technologyToHeatmap(int networkType) {
        if (networkType == 0) networkHandler_None.data.add(locationData);
        else if (networkType == 1) networkHandler_G.data.add(locationData);
        else if (networkType == 2) networkHandler_E.data.add(locationData);
        else if (networkType <= 7 || networkType == 11 || networkType == 17) networkHandler_3G.data.add(locationData);
        else if (networkType <= 10) networkHandler_H.data.add(locationData);
        else if (networkType == 13) networkHandler_4G.data.add(locationData);
        else if (networkType == 15) networkHandler_Hplus.data.add(locationData);
        else if (networkType == 20) networkHandler_5G.data.add(locationData);
        //else if (networkType == 16) return "2G";
        //TODO : 5G
    }
}

