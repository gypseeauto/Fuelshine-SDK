package com.gypsee.sdk.models;

public class VehicleAlertModel {

    private String vehicleAlertId, troubleCode, errorDesc, dtcNumber, distanceTraveledWithMILOn, fuelLevelOnAlert, createdOn, lastUpdatedOn, distanceTraveledSinceCodeClear;

    Boolean alertFixed;
    public VehicleAlertModel(String vehicleAlertId, String troubleCode, String errorDesc, String dtcNumber, String distanceTraveledWithMILOn, String fuelLevelOnAlert, String createdOn, String lastUpdatedOn, Boolean alertFixed, String distanceTraveledSinceCodeClear) {
        this.vehicleAlertId = vehicleAlertId;
        this.troubleCode = troubleCode;
        this.errorDesc = errorDesc;
        this.dtcNumber = dtcNumber;
        this.distanceTraveledWithMILOn = distanceTraveledWithMILOn;
        this.fuelLevelOnAlert = fuelLevelOnAlert;
        this.createdOn = createdOn;
        this.lastUpdatedOn = lastUpdatedOn;
        this.alertFixed = alertFixed;
        this.distanceTraveledSinceCodeClear = distanceTraveledSinceCodeClear;
    }

    public String getVehicleAlertId() {
        return vehicleAlertId;
    }

    public String getTroubleCode() {
        return troubleCode;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public String getDtcNumber() {
        return dtcNumber;
    }

    public String getDistanceTraveledWithMILOn() {
        return distanceTraveledWithMILOn;
    }

    public String getFuelLevelOnAlert() {
        return fuelLevelOnAlert;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public String getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public Boolean getAlertFixed() {
        return alertFixed;
    }

    public String getDistanceTraveledSinceCodeClear() {
        return distanceTraveledSinceCodeClear;
    }
}
