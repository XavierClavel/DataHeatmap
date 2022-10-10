package com.xavierclavel.datamapping;

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
    static List<WeightedLatLng> weightedLatLngs;
    static HeatmapTileProvider provider;
    static TileOverlay tileOverlay;


    public static void addHeatMap() {
        weightedLatLngs = new ArrayList<>();
        List<LatLng> latLngs = new ArrayList<>();

        latLngs.add(new LatLng(-37.1886, 145.708));



        // Create a heat map tile provider, passing it the latlngs of the police stations.
        provider = new HeatmapTileProvider.Builder()
                .data(latLngs)
                .build();

        // Add a tile overlay to the map, using the heat map tile provider.
        tileOverlay = MapActivity.mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));


    }



    public static void updateHeatmap(LatLng latLng, double intensity) {
        weightedLatLngs.add(new WeightedLatLng(latLng, intensity));
        provider.setWeightedData(weightedLatLngs);
        tileOverlay.clearTileCache();
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
