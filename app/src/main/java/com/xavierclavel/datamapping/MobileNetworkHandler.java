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

public class MobileNetworkHandler {
    HeatmapTileProvider tileProvider;
    TileOverlay tileOverlay;
    int[] color;
    public List<LatLng> data;
    public boolean initialized = false;
    public TableRow tableRow;
    static final float[] startPoints = {0.2f};
    static List<MobileNetworkHandler> networkHandlers = new ArrayList<>();
    boolean mainHeatmap;

    public MobileNetworkHandler(int[] color, boolean mainHeatmap) {
        this.color = color;
        this.mainHeatmap = mainHeatmap;
        if (mainHeatmap) networkHandlers.add(this);
        Log.d("netword handler", "created");
    }

    static void InitializeHeatmap() {
        for (MobileNetworkHandler networkHandler : networkHandlers) {
            networkHandler.InitializeTileOverlay();
        }
    }


    public void InitializeTileOverlay() {
        if (data == null || data.size() == 0) return;
        GoogleMap map = mainHeatmap ? HeatmapManager.map : HistoryHeatmapManager.map;
        if (map == null) {
            Log.d("map", "null");
            return;
        }

        Gradient gradient = new Gradient(color, startPoints);

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        tileProvider = new HeatmapTileProvider.Builder()
                .data(data)
                .radius(50)
                .gradient(gradient)
                .build();

        // Add a tile overlay to the map, using the heat map tile provider.
        tileOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));

        //tableRow.setVisibility(View.VISIBLE);   //display legend

        initialized = true;
    }

    void updateHeatmap() {
        Log.d("heatmap", "updated");
        if (data == null) data = new ArrayList<>();
        data.add(HeatmapManager.locationData);

        Log.d("size", ""+data.size());

        if (initialized) {
            tileProvider.setData(data);
            tileOverlay.clearTileCache();
        } else if (HeatmapManager.mapReady) {
            InitializeTileOverlay();
        }
    }


}
