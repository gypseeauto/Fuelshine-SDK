package com.gypsee.sdk.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "TripLatLongTable")
public class TripLatLong {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String tripId;
    private double latitude;
    private double longitude;
    private String   time;


    public TripLatLong(String tripId, double latitude, double longitude, String time) {
        this.tripId = tripId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId( String tripId) {
        this.tripId = tripId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String  getTime() {
        return time;
    }

    public void setTime(String  time) {
        this.time = time;
    }
}