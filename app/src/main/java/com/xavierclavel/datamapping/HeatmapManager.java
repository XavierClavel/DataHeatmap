package com.xavierclavel.datamapping;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;
import java.util.List;

public class HeatmapManager {
    public static List<WeightedLatLng> weightedLatLngs;
    static HeatmapTileProvider provider;
    static TileOverlay tileOverlay;
    static boolean mapReady = false;

    static LatLng locationData;
    static Integer bluetoothData;
    static WeightedLatLng sensorsData;


    public static void initializeHeatMap() {
        weightedLatLngs = weightedLatLngs != null ? weightedLatLngs : new ArrayList<>();
        List<LatLng> latLngs = new ArrayList<>();

        latLngs.add(new LatLng(0, 0));
        weightedLatLngs.add(new WeightedLatLng(new LatLng(0, 0), 0));



        // Create a heat map tile provider, passing it the latlngs of the police stations.
        provider = new HeatmapTileProvider.Builder()
                .data(latLngs)
                .build();

        // Add a tile overlay to the map, using the heat map tile provider.
        tileOverlay = MapActivity.mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));

        mapReady = true;

        provider.setWeightedData(weightedLatLngs);
        tileOverlay.clearTileCache();
    }

    public static void locationDataSocket(LatLng latLng) {
        locationData = latLng;
        if (bluetoothData != null) updateHeatmap();
    }

    public static void bluetoothDataSocket(int intensity) {
        bluetoothData = intensity;
        MainActivity.updateDashboard(bluetoothData);
        if (locationData != null) updateHeatmap();
    }



    static void updateHeatmap() {
        Log.d("heatmap manager", "new data point acquired");
        Toast.makeText(ForegroundService.instance, "Data point acquired", Toast.LENGTH_LONG).show();
        weightedLatLngs = weightedLatLngs != null ? weightedLatLngs : new ArrayList<>();
        weightedLatLngs.add(new WeightedLatLng(locationData, bluetoothData));   //data set of the heatmap
        XmlManager.Memorize(locationData, bluetoothData);
        locationData = null;
        bluetoothData = null;

        if (mapReady) {
            provider.setWeightedData(weightedLatLngs);
            tileOverlay.clearTileCache();
        }
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
