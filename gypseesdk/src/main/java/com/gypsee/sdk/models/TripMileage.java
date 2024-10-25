package com.gypsee.sdk.models;

public class TripMileage {
    private double expectedFuelCost;
    private double actualFuelCost;
    private double fuelPrice;
    private String weightagesData;
    private double tripDistanceKm;
    private double epaArAiMileage;
    private double ecoSpeedStartRange;
    private double ecoSpeedEndRange;
    private double mileageObtained;
    private double adjustedMileageWithPenalty;
    private float fuelSavingAmount;
    private double fuelConsumedInLiters;
    private double safeKmPercentage;
    private String alertTimings;
    private double co2Emission;
    private double expectedFuelConsumedInLiters;
    private double penaltyForAboveEcoSpeed;
    private double penaltyForHarshAcceleration;
    private double penaltyForHarshBraking;
    private double belowEcoSpeedMin;
    private double potentialEcoDriveTime;
    private double potentialTripMileage;
    private double mileageLossForTheTrip;
    private double potentialSavingLossForTheTrip;
    private boolean fuelSaving;

    // Constructor
    public TripMileage(double expectedFuelCost, double actualFuelCost, double fuelPrice, String weightagesData,
                       double tripDistanceKm, double epaArAiMileage, double ecoSpeedStartRange,
                       double ecoSpeedEndRange, double mileageObtained,
                       double adjustedMileageWithPenalty, float fuelSavingAmount,
                       double fuelConsumedInLiters, double safeKmPercentage,
                       String alertTimings, double co2Emission,
                       double expectedFuelConsumedInLiters, double penaltyForAboveEcoSpeed,
                       double penaltyForHarshAcceleration, double penaltyForHarshBraking,
                       double belowEcoSpeedMin, double potentialEcoDriveTime,
                       double potentialTripMileage, double mileageLossForTheTrip,
                       double potentialSavingLossForTheTrip, boolean fuelSaving) {
        this.expectedFuelCost = expectedFuelCost;
        this.actualFuelCost = actualFuelCost;
        this.fuelPrice = fuelPrice;
        this.weightagesData = weightagesData;
        this.tripDistanceKm = tripDistanceKm;
        this.epaArAiMileage = epaArAiMileage;
        this.ecoSpeedStartRange = ecoSpeedStartRange;
        this.ecoSpeedEndRange = ecoSpeedEndRange;
        this.mileageObtained = mileageObtained;
        this.adjustedMileageWithPenalty = adjustedMileageWithPenalty;
        this.fuelSavingAmount = fuelSavingAmount;
        this.fuelConsumedInLiters = fuelConsumedInLiters;
        this.safeKmPercentage = safeKmPercentage;
        this.alertTimings = alertTimings;
        this.co2Emission = co2Emission;
        this.expectedFuelConsumedInLiters = expectedFuelConsumedInLiters;
        this.penaltyForAboveEcoSpeed = penaltyForAboveEcoSpeed;
        this.penaltyForHarshAcceleration = penaltyForHarshAcceleration;
        this.penaltyForHarshBraking = penaltyForHarshBraking;
        this.belowEcoSpeedMin = belowEcoSpeedMin;
        this.potentialEcoDriveTime = potentialEcoDriveTime;
        this.potentialTripMileage = potentialTripMileage;
        this.mileageLossForTheTrip = mileageLossForTheTrip;
        this.potentialSavingLossForTheTrip = potentialSavingLossForTheTrip;
        this.fuelSaving = fuelSaving;
    }

    // Getters and Setters
    public double getExpectedFuelCost() {
        return expectedFuelCost;
    }

    public void setExpectedFuelCost(double expectedFuelCost) {
        this.expectedFuelCost = expectedFuelCost;
    }

    public double getActualFuelCost() {
        return actualFuelCost;
    }

    public void setActualFuelCost(double actualFuelCost) {
        this.actualFuelCost = actualFuelCost;
    }

    public double getFuelPrice() {
        return fuelPrice;
    }

    public void setFuelPrice(double fuelPrice) {
        this.fuelPrice = fuelPrice;
    }

    public String getWeightagesData() {
        return weightagesData;
    }

    public void setWeightagesData(String weightagesData) {
        this.weightagesData = weightagesData;
    }

    public double getTripDistanceKm() {
        return tripDistanceKm;
    }

    public void setTripDistanceKm(double tripDistanceKm) {
        this.tripDistanceKm = tripDistanceKm;
    }

    public double getEpaArAiMileage() {
        return epaArAiMileage;
    }

    public void setEpaArAiMileage(double epaArAiMileage) {
        this.epaArAiMileage = epaArAiMileage;
    }

    public double getEcoSpeedStartRange() {
        return ecoSpeedStartRange;
    }

    public void setEcoSpeedStartRange(double ecoSpeedStartRange) {
        this.ecoSpeedStartRange = ecoSpeedStartRange;
    }

    public double getEcoSpeedEndRange() {
        return ecoSpeedEndRange;
    }

    public void setEcoSpeedEndRange(double ecoSpeedEndRange) {
        this.ecoSpeedEndRange = ecoSpeedEndRange;
    }

    public double getMileageObtained() {
        return mileageObtained;
    }

    public void setMileageObtained(double mileageObtained) {
        this.mileageObtained = mileageObtained;
    }

    public double getAdjustedMileageWithPenalty() {
        return adjustedMileageWithPenalty;
    }

    public void setAdjustedMileageWithPenalty(double adjustedMileageWithPenalty) {
        this.adjustedMileageWithPenalty = adjustedMileageWithPenalty;
    }

    public float getFuelSavingAmount() {
        return fuelSavingAmount;
    }

    public void setFuelSavingAmount(float fuelSavingAmount) {
        this.fuelSavingAmount = fuelSavingAmount;
    }

    public double getFuelConsumedInLiters() {
        return fuelConsumedInLiters;
    }

    public void setFuelConsumedInLiters(double fuelConsumedInLiters) {
        this.fuelConsumedInLiters = fuelConsumedInLiters;
    }

    public double getSafeKmPercentage() {
        return safeKmPercentage;
    }

    public void setSafeKmPercentage(double safeKmPercentage) {
        this.safeKmPercentage = safeKmPercentage;
    }

    public String getAlertTimings() {
        return alertTimings;
    }

    public void setAlertTimings(String alertTimings) {
        this.alertTimings = alertTimings;
    }

    public double getCo2Emission() {
        return co2Emission;
    }

    public void setCo2Emission(double co2Emission) {
        this.co2Emission = co2Emission;
    }

    public double getExpectedFuelConsumedInLiters() {
        return expectedFuelConsumedInLiters;
    }

    public void setExpectedFuelConsumedInLiters(double expectedFuelConsumedInLiters) {
        this.expectedFuelConsumedInLiters = expectedFuelConsumedInLiters;
    }

    public double getPenaltyForAboveEcoSpeed() {
        return penaltyForAboveEcoSpeed;
    }

    public void setPenaltyForAboveEcoSpeed(double penaltyForAboveEcoSpeed) {
        this.penaltyForAboveEcoSpeed = penaltyForAboveEcoSpeed;
    }

    public double getPenaltyForHarshAcceleration() {
        return penaltyForHarshAcceleration;
    }

    public void setPenaltyForHarshAcceleration(double penaltyForHarshAcceleration) {
        this.penaltyForHarshAcceleration = penaltyForHarshAcceleration;
    }

    public double getPenaltyForHarshBraking() {
        return penaltyForHarshBraking;
    }

    public void setPenaltyForHarshBraking(double penaltyForHarshBraking) {
        this.penaltyForHarshBraking = penaltyForHarshBraking;
    }

    public double getBelowEcoSpeedMin() {
        return belowEcoSpeedMin;
    }

    public void setBelowEcoSpeedMin(double belowEcoSpeedMin) {
        this.belowEcoSpeedMin = belowEcoSpeedMin;
    }

    public double getPotentialEcoDriveTime() {
        return potentialEcoDriveTime;
    }

    public void setPotentialEcoDriveTime(double potentialEcoDriveTime) {
        this.potentialEcoDriveTime = potentialEcoDriveTime;
    }

    public double getPotentialTripMileage() {
        return potentialTripMileage;
    }

    public void setPotentialTripMileage(double potentialTripMileage) {
        this.potentialTripMileage = potentialTripMileage;
    }

    public double getMileageLossForTheTrip() {
        return mileageLossForTheTrip;
    }

    public void setMileageLossForTheTrip(double mileageLossForTheTrip) {
        this.mileageLossForTheTrip = mileageLossForTheTrip;
    }

    public double getPotentialSavingLossForTheTrip() {
        return potentialSavingLossForTheTrip;
    }

    public void setPotentialSavingLossForTheTrip(double potentialSavingLossForTheTrip) {
        this.potentialSavingLossForTheTrip = potentialSavingLossForTheTrip;
    }

    public boolean isFuelSaving() {
        return fuelSaving;
    }

    public void setFuelSaving(boolean fuelSaving) {
        this.fuelSaving = fuelSaving;
    }
}

