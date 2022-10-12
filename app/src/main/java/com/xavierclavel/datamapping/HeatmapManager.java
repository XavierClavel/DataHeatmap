package com.xavierclavel.datamapping;

import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;
import java.util.List;

public class HeatmapManager {
    public static List<WeightedLatLng> weightedLatLngs;

    static HeatmapTileProvider provider_4G;
    static HeatmapTileProvider provider_3G;
    static HeatmapTileProvider provider_Hplus;
    static HeatmapTileProvider provider_H;
    static HeatmapTileProvider provider_E;

    static TileOverlay tileOverlay_4G;
    static TileOverlay tileOverlay_3G;
    static TileOverlay tileOverlay_Hplus;
    static TileOverlay tileOverlay_H;
    static TileOverlay tileOverlay_E;

    public static boolean mapReady = false;

    static LatLng locationData;
    static Integer mobileNetworkDataDownlink;
    static Integer mobileNetworkDataUplink;

    static final float minDistance = 20; //distance minimale entre deux points successifs pour éviter une concentration des points de données,
    // qui doivent être répartis pour une meilleure visualisation

    static final float[] startPoints = {
            0.2f
    };

    static final int[] color_4G = {
            Color.rgb(102, 225, 0), // green
    };

    static final int[] color_3G = {
            Color.rgb(255, 255, 0), // yellow
    };

    static final int[] color_Hplus = {
            Color.rgb(255, 102, 0), // orange
    };

    static final int[] color_H = {
            Color.rgb(255, 10, 0) //red
    };

    static final int[] color_E = {
            Color.rgb(156,39,179)   //violet
    };

    static List<LatLng> data_3G;
    static List<LatLng> data_4G;
    static List<LatLng> data_Hplus;
    static List<LatLng> data_H;
    static List<LatLng> data_E;

    public static boolean is_4G_initialized = false;
    public static boolean is_3G_initialized = false;
    public static boolean is_Hplus_initialized = false;
    public static boolean is_H_initialized = false;
    public static boolean is_E_initialized = false;

    public static TableRow tableRow_4G;
    public static TableRow tableRow_3G;
    public static TableRow tableRow_Hplus;
    public static TableRow tableRow_H;
    public static TableRow tableRow_E;


    public static void initializeHeatMap() {    //gets executed when the map is displayed
        Log.d("H+ initialized", ""+is_Hplus_initialized);
        if (is_4G_initialized) {
            InitializeTileOverlay4G();  //display the 4G heatmap
        }
        if (is_3G_initialized) {
            InitializeTileOverlay3G();  //display the 3G heatmap
        }
        if (is_Hplus_initialized) {
            InitializeTileOverlayHplus();
        }
        if (is_H_initialized) {
            InitializeTileOverlayH();
        }
        if (is_E_initialized) {
            InitializeTileOverlayE();
        }

        Log.d("heatmap", "map initialized");

        mapReady = true;
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
        tileOverlay_4G = MapActivity.mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider_4G));

        tableRow_4G.setVisibility(View.VISIBLE);   //display legend

        is_4G_initialized = true;
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
        tileOverlay_3G = MapActivity.mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider_3G));

        tableRow_3G.setVisibility(View.VISIBLE);

        is_3G_initialized = true;
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
        tileOverlay_Hplus = MapActivity.mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider_Hplus));

        tableRow_Hplus.setVisibility(View.VISIBLE);

        is_Hplus_initialized = true;
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
        tileOverlay_H = MapActivity.mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider_H));

        tableRow_H.setVisibility(View.VISIBLE);

        is_H_initialized = true;
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
        tileOverlay_E = MapActivity.mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider_E));

        tableRow_E.setVisibility(View.VISIBLE);

        is_E_initialized = true;
    }

    public static void locationDataSocket(LatLng latLng) {
        DataActivity.updateLocationDisplay(latLng);
        locationData = latLng;
        if (mobileNetworkDataDownlink != null) updateHeatmap();
    }

    public static void bluetoothDataSocket(int downSpeed, int upSpeed) {
        DataActivity.updateMobileNetworkDisplay(downSpeed, upSpeed);
        mobileNetworkDataDownlink = downSpeed;
        mobileNetworkDataUplink = upSpeed;
        MainActivity.updateDashboard(mobileNetworkDataDownlink);
        if (locationData != null) updateHeatmap();
    }



    static void updateHeatmap() {
        Log.d("heatmap manager", "new data point acquired");
        Toast.makeText(ForegroundService.instance, "Data point acquired", Toast.LENGTH_LONG).show();
        weightedLatLngs = weightedLatLngs != null ? weightedLatLngs : new ArrayList<>();
        weightedLatLngs.add(new WeightedLatLng(locationData, mobileNetworkDataDownlink));   //data set of the heatmap
        XmlManager.Memorize(locationData, mobileNetworkDataDownlink);

        if (mobileNetworkDataDownlink > 20000) {    //4G
            update_4G_heatmap();
        }
        else if (mobileNetworkDataDownlink > 14000) {    //3G
            update_3G_heatmap();
        }
        else if (mobileNetworkDataDownlink > 10000) {
            update_Hplus_heatmap();
        }
        else if (mobileNetworkDataDownlink > 3000) {
            update_H_heatmap();
        }
        else if (mobileNetworkDataDownlink > 50) {
            update_E_heatmap();
        }

        locationData = null;
        mobileNetworkDataDownlink = null;
    }

    static void update_4G_heatmap() {
        if (data_4G == null) data_4G = new ArrayList<>();
        data_4G.add(locationData);

        if (is_4G_initialized) {
            provider_4G.setData(data_4G);
            tileOverlay_4G.clearTileCache();
        } else if (mapReady) {
            InitializeTileOverlay4G();
        }
    }

    static void update_3G_heatmap() {
        if (data_3G == null) data_3G = new ArrayList<>();
        data_3G.add(locationData);

        if (is_3G_initialized) {
            provider_3G.setData(data_3G);
            tileOverlay_3G.clearTileCache();
        }
        else if (mapReady) {
            InitializeTileOverlay3G();
        }
    }

    static void update_Hplus_heatmap() {
        if (data_Hplus == null) data_Hplus = new ArrayList<>();
        data_Hplus.add(locationData);

        if (is_Hplus_initialized) {
            provider_Hplus.setData(data_Hplus);
            tileOverlay_Hplus.clearTileCache();
        }
        else if (mapReady) {
            InitializeTileOverlayHplus();
        }
    }

    static void update_H_heatmap() {
        if (data_H == null) data_H = new ArrayList<>();
        data_H.add(locationData);

        if (is_H_initialized) {
            provider_H.setData(data_H);
            tileOverlay_H.clearTileCache();
        }
        else if (mapReady) {
            InitializeTileOverlayH();
        }
    }

    static void update_E_heatmap() {
        if (data_E == null) data_E = new ArrayList<>();
        data_E.add(locationData);

        if (is_E_initialized) {
            provider_E.setData(data_E);
            tileOverlay_E.clearTileCache();
        }
        else if (mapReady) {
            InitializeTileOverlayH();
        }
    }

    static boolean areFarEnough(LatLng latLngA, LatLng latLngB) {
        Location locationA = new Location("point A");
        locationA.setLatitude(latLngA.latitude);
        locationA.setLongitude(latLngA.longitude);
        Location locationB = new Location("point B");
        locationB.setLatitude(latLngB.latitude);
        locationB.setLongitude(latLngB.longitude);

        float distance = locationA.distanceTo(locationB);

        return distance < minDistance;
    }


    public static void ParseXML() {
        /*
        // Get the data: latitude/longitude positions of police stations.
        try {
            latLngs = readItems(R.raw.police_stations);
        } catch (JSONException e) {
            Toast.makeText(context, "Problem reading list of locations.", Toast.LENGTH_LONG).show();
        }
         */
    }
}
