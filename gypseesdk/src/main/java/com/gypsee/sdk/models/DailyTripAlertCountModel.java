package com.gypsee.sdk.models;

public class DailyTripAlertCountModel {

    int totalTrips, totalHarshBreakingAlerts, totalOverSpeedAlerts, totalHarshAccelerationAlerts, totalHighRPMAlerts, totalDistanceTravelledInKm;
    String date, dayName;
    boolean dataLoading, enabled;

    public DailyTripAlertCountModel(int totalTrips, int totalHarshBreakingAlerts, int totalOverSpeedAlerts, int totalHarshAccelerationAlerts, int totalHighRPMAlerts, int totalDistanceTravelledInKm, String date, String dayName, boolean dataLoading, boolean enabled) {
        this.totalTrips = totalTrips;
        this.totalHarshBreakingAlerts = totalHarshBreakingAlerts;
        this.totalOverSpeedAlerts = totalOverSpeedAlerts;
        this.totalHarshAccelerationAlerts = totalHarshAccelerationAlerts;
        this.totalHighRPMAlerts = totalHighRPMAlerts;
        this.totalDistanceTravelledInKm = totalDistanceTravelledInKm;
        this.date = date;
        this.dayName = dayName;
        this.dataLoading = dataLoading;
        this.enabled = enabled;
    }

    public int getTotalTrips() {
        return totalTrips;
    }

    public void setTotalTrips(int totalTrips) {
        this.totalTrips = totalTrips;
    }

    public int getTotalHarshBreakingAlerts() {
        return totalHarshBreakingAlerts;
    }

    public void setTotalHarshBreakingAlerts(int totalHarshBreakingAlerts) {
        this.totalHarshBreakingAlerts = totalHarshBreakingAlerts;
    }

    public int getTotalOverSpeedAlerts() {
        return totalOverSpeedAlerts;
    }

    public void setTotalOverSpeedAlerts(int totalOverSpeedAlerts) {
        this.totalOverSpeedAlerts = totalOverSpeedAlerts;
    }

    public int getTotalHarshAccelerationAlerts() {
        return totalHarshAccelerationAlerts;
    }

    public void setTotalHarshAccelerationAlerts(int totalHarshAccelerationAlerts) {
        this.totalHarshAccelerationAlerts = totalHarshAccelerationAlerts;
    }

    public int getTotalHighRPMAlerts() {
        return totalHighRPMAlerts;
    }

    public void setTotalHighRPMAlerts(int totalHighRPMAlerts) {
        this.totalHighRPMAlerts = totalHighRPMAlerts;
    }

    public int getTotalDistanceTravelledInKm() {
        return totalDistanceTravelledInKm;
    }

    public void setTotalDistanceTravelledInKm(int totalDistanceTravelledInKm) {
        this.totalDistanceTravelledInKm = totalDistanceTravelledInKm;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public boolean isDataLoading() {
        return dataLoading;
    }

    public void setDataLoading(boolean dataLoading) {
        this.dataLoading = dataLoading;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
