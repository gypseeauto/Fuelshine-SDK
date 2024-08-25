package com.gypsee.sdk.models;

public class LastSafeTripModel {

    String tripId, startLat, startLong, endLat, endLong, tripDistanceInKm, tripTimeInMin, triAlerts, createdOn, startLocationName, destinationName,tripSavedAmount,tripSavingsCommission;

    public LastSafeTripModel(String tripId, String startLat, String startLong, String endLat, String endLong, String tripDistanceInKm, String tripTimeInMin, String triAlerts, String createdOn, String startLocationName, String destinationName,String tripSavedAmount,String tripSavingsCommission) {
        this.tripId = tripId;
        this.startLat = startLat;
        this.startLong = startLong;
        this.endLat = endLat;
        this.endLong = endLong;
        this.tripDistanceInKm = tripDistanceInKm;
        this.tripTimeInMin = tripTimeInMin;
        this.triAlerts = triAlerts;
        this.createdOn = createdOn;
        this.startLocationName = startLocationName;
        this.destinationName = destinationName;
        this.tripSavedAmount = tripSavedAmount;
        this.tripSavingsCommission = tripSavingsCommission;
    }

    public String getTripSavedAmount() {
        return tripSavedAmount;
    }

    public void setTripSavedAmount(String tripSavedAmount) {
        this.tripSavedAmount = tripSavedAmount;
    }

    public String getTripId() {
        return tripId;
    }

    public String getStartLat() {
        return startLat;
    }

    public String getStartLong() {
        return startLong;
    }

    public String getEndLat() {
        return endLat;
    }

    public String getEndLong() {
        return endLong;
    }

    public String getTripDistanceInKm() {
        return tripDistanceInKm;
    }

    public String getTripTimeInMin() {
        return tripTimeInMin;
    }

    public String getTriAlerts() {
        return triAlerts;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public String getStartLocationName() {
        return startLocationName;
    }

    public String getDestinationName() {
        return destinationName;
    }
}
