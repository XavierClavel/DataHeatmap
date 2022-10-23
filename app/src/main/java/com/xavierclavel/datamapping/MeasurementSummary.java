package com.xavierclavel.datamapping;

public class MeasurementSummary {
    String date;
    String place;
    String nbPoints;
    String filename;

    public MeasurementSummary(String date, String place, String nbPoints, String filename) {
        this.date = date;
        this.place = place;
        this.nbPoints = nbPoints;
        this.filename = filename;
    }
}
