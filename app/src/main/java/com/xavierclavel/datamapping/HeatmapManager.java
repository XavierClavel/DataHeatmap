package com.xavierclavel.datamapping;

import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;

public class HeatmapManager {

    /*
    The problem with using heatmaps in this context is that heatmaps represent density of points, and not a field of value.
    For example, they are not meant to display the temperature over a geographical area.
    The workaround I used is to have multiple heatmaps without any gradient, just a single color. Each heatlmap contain the values for a
    telecommunication network.
     */

    public static boolean mapReady = false;

    static LatLng locationData;
    static Integer mobileNetworkDataDownlink;
    static Integer mobileNetworkDataUplink;
    static Integer mobileNetworkDataType;

    static final int[] color_5G = {Color.rgb(32,218,190)};      //cyan
    static final int[] color_4G = {Color.rgb(102, 225, 0)};     //green
    static final int[] color_Hplus = {Color.rgb(255, 255, 0)};  //yellow
    static final int[] color_H = {Color.rgb(255, 102, 0)};      //orange
    static final int[] color_3G = {Color.rgb(255, 10, 0)};      //red
    static final int[] color_E = {Color.rgb(156,39,179)};       //violet
    static final int[] color_G = {Color.rgb(99, 99, 99)};       //light grey
    static  final int[] color_None = {Color.rgb(0, 0, 0)};      //black

    public static int nbPoints = 0;
    static boolean cityAcquired = false;

    public static GoogleMap map;
    static final int minDistance = 5; //distance minimale entre deux points successifs

    static LatLng lastPos = null;

    public static MobileNetworkHandler networkHandler_5G = new MobileNetworkHandler(color_5G, true);
    public static MobileNetworkHandler networkHandler_4G = new MobileNetworkHandler(color_4G, true);
    public static MobileNetworkHandler  networkHandler_3G = new MobileNetworkHandler(color_3G, true);
    public static MobileNetworkHandler networkHandler_Hplus = new MobileNetworkHandler(color_Hplus, true);
    public static MobileNetworkHandler networkHandler_H = new MobileNetworkHandler(color_H, true);
    public static MobileNetworkHandler networkHandler_E = new MobileNetworkHandler(color_E, true);
    public static MobileNetworkHandler networkHandler_G = new MobileNetworkHandler(color_G, true);
    public static MobileNetworkHandler networkHandler_None = new MobileNetworkHandler(color_None, true);


    public static void initializeHeatMap() {    //gets executed when the map is displayed
        map = MapActivity.mMap;

        MobileNetworkHandler.InitializeHeatmap();

        Log.d("heatmap", "map initialized");

        mapReady = true;


    }

    public static void locationDataSocket(LatLng latLng) {
        if (!cityAcquired) {
            MainActivity.getCityName(latLng);
        }
        DataActivity.updateLocationDisplay(latLng);
        locationData = latLng;
        if (mobileNetworkDataDownlink != null) updateHeatmap();
    }

    public static void mobileNetworkDataSocket(int networkType, int downSpeed, int upSpeed) {
        DataActivity.updateMobileNetworkDisplay(networkType, downSpeed, upSpeed);
        mobileNetworkDataType = networkType;
        mobileNetworkDataDownlink = downSpeed;
        mobileNetworkDataUplink = upSpeed;
        MainActivity.updateDashboard(mobileNetworkDataDownlink);    //A modifier

        if (locationData != null) updateHeatmap();
    }

    public static void addDataPoint(LatLng location, int networkType) {

        locationData = location;
        mobileNetworkDataType = networkType;

        nbPoints ++;
        if (DataActivity.nbPointsDisplay != null) DataActivity.nbPointsDisplay.setText(""+nbPoints);

        Log.d("heatmap manager", "new data point acquired");

        technologyToHeatmap(mobileNetworkDataType);
        //networkHandler_None.updateHeatmap();

        locationData = null;
        mobileNetworkDataDownlink = null;
    }

    public static void allDataPointAdded() {
        MainActivity.nbMeasurementsDisplay.setText(nbPoints + " measurements");
    }

    static void updateHeatmap() {

        if (areFarEnough(locationData, lastPos)) {
            lastPos = locationData;
            return;
        }

        lastPos = locationData;

        nbPoints ++;
        MainActivity.nbMeasurementsDisplay.setText(nbPoints + " measurements");
        if (DataActivity.nbPointsDisplay != null) DataActivity.nbPointsDisplay.setText(""+nbPoints);
        ForegroundService.updateNotification();

        Log.d("heatmap manager", "new data point acquired");
        //Toast.makeText(ForegroundService.instance, "Data point acquired", Toast.LENGTH_LONG).show();
        XmlManager.Memorize(locationData, mobileNetworkDataType);

        technologyToHeatmap(mobileNetworkDataType);

        locationData = null;
        mobileNetworkDataDownlink = null;
    }

    public static void technologyToHeatmap(int networkType) {
        Log.d("network type", networkType + "");

        if (networkType == 0) networkHandler_None.updateHeatmap();
        else if (networkType == 1) networkHandler_G.updateHeatmap();
        else if (networkType == 2) networkHandler_E.updateHeatmap();
        else if (networkType <= 7 || networkType == 11 || networkType == 17) networkHandler_3G.updateHeatmap();
        else if (networkType <= 10) networkHandler_H.updateHeatmap();
        else if (networkType == 13) networkHandler_4G.updateHeatmap();
        else if (networkType == 15) networkHandler_Hplus.updateHeatmap();
        else if (networkType == 20) networkHandler_5G.updateHeatmap();
    }

    public static void ResetHeatmap() {
        MobileNetworkHandler.networkHandlers = new ArrayList<>();
        networkHandler_5G = new MobileNetworkHandler(color_5G, true);
        networkHandler_4G = new MobileNetworkHandler(color_4G, true);
        networkHandler_3G = new MobileNetworkHandler(color_3G, true);
        networkHandler_Hplus = new MobileNetworkHandler(color_Hplus, true);
        networkHandler_H = new MobileNetworkHandler(color_H, true);
        networkHandler_E = new MobileNetworkHandler(color_E, true);
        networkHandler_G = new MobileNetworkHandler(color_G, true);
        networkHandler_None = new MobileNetworkHandler(color_None, true);

        nbPoints = 0;
        MainActivity.nbMeasurementsDisplay.setText(" ");
    }

    static boolean areFarEnough(LatLng latLngA, LatLng latLngB) {
        if (latLngA == null || latLngB == null) return true;

        Location locationA = new Location("point A");
        locationA.setLatitude(latLngA.latitude);
        locationA.setLongitude(latLngA.longitude);
        Location locationB = new Location("point B");
        locationB.setLatitude(latLngB.latitude);
        locationB.setLongitude(latLngB.longitude);

        float distance = locationA.distanceTo(locationB);
        Log.d("distance", ""+distance);
        Log.d("are far enough", "" + (distance > minDistance));

        return distance > minDistance;
    }

}
