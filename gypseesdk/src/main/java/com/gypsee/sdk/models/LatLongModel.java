package com.gypsee.sdk.models;

public class LatLongModel {
    double lat, longitude;
    String time;

    public LatLongModel(double lat, double longitude, String time) {
        this.lat = lat;
        this.longitude = longitude;
        this.time = time;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
