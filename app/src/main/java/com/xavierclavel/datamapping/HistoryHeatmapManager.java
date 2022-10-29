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

    static List<LatLng> data_3G;
    static List<LatLng> data_4G;
    static List<LatLng> data_Hplus;
    static List<LatLng> data_H;
    static List<LatLng> data_E;
    static List<LatLng> data_G;
    static List<LatLng> data_None;

    static HeatmapTileProvider provider_4G;
    static HeatmapTileProvider provider_3G;
    static HeatmapTileProvider provider_Hplus;
    static HeatmapTileProvider provider_H;
    static HeatmapTileProvider provider_E;
    static HeatmapTileProvider provider_G;
    static HeatmapTileProvider provider_None;

    static TileOverlay tileOverlay_4G;
    static TileOverlay tileOverlay_3G;
    static TileOverlay tileOverlay_Hplus;
    static TileOverlay tileOverlay_H;
    static TileOverlay tileOverlay_E;
    static TileOverlay tileOverlay_G;
    static TileOverlay tileOverlay_None;

    static final float[] startPoints = {0.2f};

    static final int[] color_4G = {Color.rgb(102, 225, 0)};     //green
    static final int[] color_Hplus = {Color.rgb(255, 255, 0)};  //yellow
    static final int[] color_H = {Color.rgb(255, 102, 0)};      //orange
    static final int[] color_3G = {Color.rgb(255, 10, 0)};      //red
    static final int[] color_E = {Color.rgb(156,39,179)};       //violet
    static final int[] color_G = {Color.rgb(99, 99, 99)};       //light grey
    static  final int[] color_None = {Color.rgb(0, 0, 0)};      //black

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

        data_4G = new ArrayList<>();    //reset all the data
        data_3G = new ArrayList<>();
        data_Hplus = new ArrayList<>();
        data_H = new ArrayList<>();
        data_E = new ArrayList<>();
        data_G = new ArrayList<>();
        data_None = new ArrayList<>();

        addData();
    }

    static void addData() {

        for (TimestampedData dataPoint: data) {
            addDataPoint(dataPoint.position, dataPoint.network);
        }

        if (data_4G.size() != 0) InitializeTileOverlay4G();  //display the 4G heatmap
        if (data_3G.size() != 0) InitializeTileOverlay3G();  //display the 3G heatmap
        if (data_Hplus.size() != 0) InitializeTileOverlayHplus();
        if (data_H.size() != 0) InitializeTileOverlayH();
        if (data_E.size() != 0) InitializeTileOverlayE();
        if (data_G.size() != 0) InitializeTileOverlayG();
        if (data_None.size() != 0) InitializeTileOverlayNone();
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
        if (networkType == 0) data_None.add(locationData);
        else if (networkType == 1) data_G.add(locationData);
        else if (networkType == 2) data_E.add(locationData);
        else if (networkType <= 7 || networkType == 11 || networkType == 17) data_3G.add(locationData);
        else if (networkType <= 10) data_H.add(locationData);
        else if (networkType == 13 || networkType == 20) data_4G.add(locationData);
        else if (networkType == 15) data_Hplus.add(locationData);
        //else if (networkType == 16) return "2G";
        //TODO : 5G
    }

    public static void InitializeTileOverlay4G() {
        Gradient gradient_4G = new Gradient(color_4G, startPoints);

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        provider_4G = new HeatmapTileProvider.Builder()
                .data(data_4G)
                .radius(50)
                .gradient(gradient_4G)
                .build();

        // Add a tile overlay to the map, using the heat map tile provider.
        tileOverlay_4G = map.addTileOverlay(new TileOverlayOptions().tileProvider(provider_4G));

        tableRow_4G.setVisibility(View.VISIBLE);   //display legend
    }

    public static void InitializeTileOverlay3G() {
        Gradient gradient_3G = new Gradient(color_3G, startPoints);

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        provider_3G = new HeatmapTileProvider.Builder()
                .data(data_3G)
                .radius(50)
                .gradient(gradient_3G)
                .build();

        // Add a tile overlay to the map, using the heat map tile provider.
        tileOverlay_3G = map.addTileOverlay(new TileOverlayOptions().tileProvider(provider_3G));

        tableRow_3G.setVisibility(View.VISIBLE);
    }

    public static void InitializeTileOverlayHplus() {
        Gradient gradient_Hplus = new Gradient(color_Hplus, startPoints);

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        provider_Hplus = new HeatmapTileProvider.Builder()
                .data(data_Hplus)
                .radius(50)
                .gradient(gradient_Hplus)
                .build();

        // Add a tile overlay to the map, using the heat map tile provider.
        tileOverlay_Hplus = map.addTileOverlay(new TileOverlayOptions().tileProvider(provider_Hplus));

        tableRow_Hplus.setVisibility(View.VISIBLE);
    }

    public static void InitializeTileOverlayH() {
        Gradient gradient_H = new Gradient(color_H, startPoints);

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        provider_H = new HeatmapTileProvider.Builder()
                .data(data_H)
                .radius(50)
                .gradient(gradient_H)
                .build();

        // Add a tile overlay to the map, using the heat map tile provider.
        tileOverlay_H = map.addTileOverlay(new TileOverlayOptions().tileProvider(provider_H));

        tableRow_H.setVisibility(View.VISIBLE);
    }

    public static void InitializeTileOverlayE() {
        Gradient gradient_E = new Gradient(color_E, startPoints);

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        provider_E = new HeatmapTileProvider.Builder()
                .data(data_E)
                .radius(50)
                .gradient(gradient_E)
                .build();

        // Add a tile overlay to the map, using the heat map tile provider.
        tileOverlay_E = map.addTileOverlay(new TileOverlayOptions().tileProvider(provider_E));

        tableRow_E.setVisibility(View.VISIBLE);
    }

    public static void InitializeTileOverlayG() {
        Gradient gradient_G = new Gradient(color_G, startPoints);

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        provider_G = new HeatmapTileProvider.Builder()
                .data(data_G)
                .radius(50)
                .gradient(gradient_G)
                .build();

        // Add a tile overlay to the map, using the heat map tile provider.
        tileOverlay_G = map.addTileOverlay(new TileOverlayOptions().tileProvider(provider_G));

        tableRow_G.setVisibility(View.VISIBLE);
    }

    public static void InitializeTileOverlayNone() {
        Gradient gradient_None = new Gradient(color_None, startPoints);

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        provider_None = new HeatmapTileProvider.Builder()
                .data(data_None)
                .radius(50)
                .gradient(gradient_None)
                .build();

        // Add a tile overlay to the map, using the heat map tile provider.
        tileOverlay_None = map.addTileOverlay(new TileOverlayOptions().tileProvider(provider_None));

        tableRow_None.setVisibility(View.VISIBLE);
    }
}
