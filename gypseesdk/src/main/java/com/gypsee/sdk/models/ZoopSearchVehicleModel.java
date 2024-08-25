package com.gypsee.sdk.models;

public class ZoopSearchVehicleModel {

    String makeAndModelName, regNumber, userId, resultId, fuelType, registrationDate, vehicleExists;

    public ZoopSearchVehicleModel(String makeAndModelName, String regNumber, String userId, String resultId, String fuelType, String registrationDate, String vehicleExists) {
        this.makeAndModelName = makeAndModelName;
        this.regNumber = regNumber;
        this.userId = userId;
        this.resultId = resultId;
        this.fuelType = fuelType;
        this.registrationDate = registrationDate;
        this.vehicleExists = vehicleExists;
    }

    public String getMakeAndModelName() {
        return makeAndModelName;
    }

    public void setMakeAndModelName(String makeAndModelName) {
        this.makeAndModelName = makeAndModelName;
    }

    public String getRegNumber() {
        return regNumber;
    }

    public void setRegNumber(String regNumber) {
        this.regNumber = regNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getVehicleExists() {
        return vehicleExists;
    }

    public void setVehicleExists(String vehicleExists) {
        this.vehicleExists = vehicleExists;
    }
}
