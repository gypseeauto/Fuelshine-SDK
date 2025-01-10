package com.gypsee.sdk.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "driving_alerts")
public class DrivingAlert {

    public String lon;
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String alertType;
    private String alertValue;
    private String timeInterval;
    private String gForce;
    private long timeStamp;
    private double lat;
    private double lng;
    private String impact;
    private String tripId;
    private int initialSpeed;

    public DrivingAlert(String alertType, String alertValue, String timeInterval, String gForce, long timeStamp, double lat, double lng, String impact, String tripId) {
        this.alertType = alertType;
        this.alertValue = alertValue;
        this.timeInterval = timeInterval;
        this.gForce = gForce;
        this.timeStamp = timeStamp;
        this.lat = lat;
        this.lng = lng;
        this.impact = impact;
        this.tripId = tripId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getAlertValue() {
        return alertValue;
    }

    public void setAlertValue(String alertValue) {
        this.alertValue = alertValue;
    }

    public String getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(String timeInterval) {
        this.timeInterval = timeInterval;
    }

    public String getGForce() {
        return gForce;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getImpact() {
        return impact;
    }

    public void setImpact(String impact) {
        this.impact = impact;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public int getInitialSpeed() {
        return initialSpeed;
    }

    public void setInitialSpeed(int initialSpeed) {
        this.initialSpeed = initialSpeed;
    }

    public String getgForce() {
        return gForce;
    }

    public void setgForce(String gForce) {
        this.gForce = gForce;
    }
}


