package com.gypsee.sdk.models;

public class TripFileNameModel {

    private long id;
    private String tripId, csvFileName;

    public TripFileNameModel(String tripId, String csvFileName) {
        this.tripId = tripId;
        this.csvFileName = csvFileName;
    }

    public TripFileNameModel(long id, String tripId, String csvFileName) {
        this.id = id;
        this.tripId = tripId;
        this.csvFileName = csvFileName;
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

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getCsvFileName() {
        return csvFileName;
    }

    public void setCsvFileName(String csvFileName) {
        this.csvFileName = csvFileName;
    }
}
