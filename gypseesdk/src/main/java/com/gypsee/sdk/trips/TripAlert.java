package com.gypsee.sdk.trips;

public class TripAlert {

    private String alertType, alertValue, timeInterval, gForce;

    private long timeStamp;
    private double lat, lng;
    private String impact;
    int initialSpeed;

    public int getChangeInSpeed() {
        return changeInSpeed;
    }

    public void setChangeInSpeed(int changeInSpeed) {
        this.changeInSpeed = changeInSpeed;
    }

    public TripAlert(String alertType, String alertValue, String timeInterval, String gForce, long timeStamp, double lat, double lng, String impact) {
        this.alertType = alertType;
        this.alertValue = alertValue;
        this.timeInterval = timeInterval;
        this.gForce = gForce;
        this.timeStamp = timeStamp;
        this.lat = lat;
        this.lng = lng;
        this.impact = impact;
    }

    private int changeInSpeed;

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

    public String getgForce() {
        return gForce;
    }

    public void setgForce(String gForce) {
        this.gForce = gForce;
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

    public int getInitialSpeed() {
        return initialSpeed;
    }

    public void setInitialSpeed(int initialSpeed) {
        this.initialSpeed = initialSpeed;
    }
}
