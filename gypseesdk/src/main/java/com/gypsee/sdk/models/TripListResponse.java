package com.gypsee.sdk.models;

import java.util.ArrayList;

public class TripListResponse {
    private String totalRecords;

    private ArrayList<UserTrips> userTrips;

    private String message;

    private String status;

    public String getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(String totalRecords) {
        this.totalRecords = totalRecords;
    }

    public ArrayList<UserTrips> getUserTrips() {
        return userTrips;
    }

    public void setUserTrips(ArrayList<UserTrips> userTrips) {
        this.userTrips = userTrips;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ClassPojo [totalRecords = " + totalRecords + ", userTrips = " + userTrips + ", message = " + message + ", status = " + status + "]";
    }
}

