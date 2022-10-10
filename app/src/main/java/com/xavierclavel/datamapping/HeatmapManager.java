package com.xavierclavel.datamapping;

import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;

public class HeatmapManager {

    public static void addHeatMap() {
        List<LatLng> latLngs = new ArrayList<>();
        latLngs.add(new LatLng(-37.1886, 145.708));
        latLngs.add(new LatLng(-37.8361, 144.845));
        latLngs.add(new LatLng(-38.4034, 144.192));
        latLngs.add(new LatLng(-38.7597, 143.67));
        latLngs.add(new LatLng(-36.9672, 141.083));

        /*
        // Get the data: latitude/longitude positions of police stations.
        try {
            latLngs = readItems(R.raw.police_stations);
        } catch (JSONException e) {
            Toast.makeText(context, "Problem reading list of locations.", Toast.LENGTH_LONG).show();
        }
         */

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
                .data(latLngs)
                .build();

        // Add a tile overlay to the map, using the heat map tile provider.
        TileOverlay overlay = MapActivity.mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
    }
}
