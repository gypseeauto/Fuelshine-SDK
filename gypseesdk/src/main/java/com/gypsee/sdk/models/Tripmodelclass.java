package com.gypsee.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Tripmodelclass implements Parcelable {


    private String currentLatitude, currentLongitude, currentAltitude, barometricPressure, engineCoolantTemp, fuelLevel, engineLoad, ambientAirTemp,
            engineRpm, intakeManifoldPressure, MAF, termFuelTrimBank1, fuelEconomy, longTermFuelTrimBank2, fuelType, airIntakeTemp, fuelPressure, currentSpeed, shortTermFuelBank2, shortTermFuelBank1,
            engineRunTime, throttlePos, dtcNumber, troubleCodes, timingAdvance, equivRatio, userId, vehicleId;

    public Tripmodelclass(String currentLatitude, String currentLongitude, String currentAltitude, String barometricPressure, String engineCoolantTemp,
                          String fuelLevel, String engineLoad, String ambientAirTemp, String engineRpm, String intakeManifoldPressure, String MAF, String termFuelTrimBank1, String fuelEconomy, String longTermFuelTrimBank2, String fuelType, String airIntakeTemp, String fuelPressure, String currentSpeed, String shortTermFuelBank2, String shortTermFuelBank1, String engineRunTime, String throttlePos,
                          String dtcNumber, String troubleCodes, String timingAdvance, String equivRatio, String userId, String vehicleId) {
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
        this.currentAltitude = currentAltitude;
        this.barometricPressure = barometricPressure;
        this.engineCoolantTemp = engineCoolantTemp;
        this.fuelLevel = fuelLevel;
        this.engineLoad = engineLoad;
        this.ambientAirTemp = ambientAirTemp;
        this.engineRpm = engineRpm;
        this.intakeManifoldPressure = intakeManifoldPressure;
        this.MAF = MAF;
        this.termFuelTrimBank1 = termFuelTrimBank1;
        this.fuelEconomy = fuelEconomy;
        this.longTermFuelTrimBank2 = longTermFuelTrimBank2;
        this.fuelType = fuelType;
        this.airIntakeTemp = airIntakeTemp;
        this.fuelPressure = fuelPressure;
        this.currentSpeed = currentSpeed;
        this.shortTermFuelBank2 = shortTermFuelBank2;
        this.shortTermFuelBank1 = shortTermFuelBank1;
        this.engineRunTime = engineRunTime;
        this.throttlePos = throttlePos;
        this.dtcNumber = dtcNumber;
        this.troubleCodes = troubleCodes;
        this.timingAdvance = timingAdvance;
        this.equivRatio = equivRatio;
        this.userId = userId;
        this.vehicleId = vehicleId;
    }

    protected Tripmodelclass(Parcel in) {
        currentLatitude = in.readString();
        currentLongitude = in.readString();
        currentAltitude = in.readString();
        barometricPressure = in.readString();
        engineCoolantTemp = in.readString();
        fuelLevel = in.readString();
        engineLoad = in.readString();
        ambientAirTemp = in.readString();
        engineRpm = in.readString();
        intakeManifoldPressure = in.readString();
        MAF = in.readString();
        termFuelTrimBank1 = in.readString();
        fuelEconomy = in.readString();
        longTermFuelTrimBank2 = in.readString();
        fuelType = in.readString();
        airIntakeTemp = in.readString();
        fuelPressure = in.readString();
        currentSpeed = in.readString();
        shortTermFuelBank2 = in.readString();
        shortTermFuelBank1 = in.readString();
        engineRunTime = in.readString();
        throttlePos = in.readString();
        dtcNumber = in.readString();
        troubleCodes = in.readString();
        timingAdvance = in.readString();
        equivRatio = in.readString();
        userId = in.readString();
        vehicleId = in.readString();
    }

    public static final Creator<Tripmodelclass> CREATOR = new Creator<Tripmodelclass>() {
        @Override
        public Tripmodelclass createFromParcel(Parcel in) {
            return new Tripmodelclass(in);
        }

        @Override
        public Tripmodelclass[] newArray(int size) {
            return new Tripmodelclass[size];
        }
    };

    public String getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(String currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public String getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(String currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public String getCurrentAltitude() {
        return currentAltitude;
    }

    public void setCurrentAltitude(String currentAltitude) {
        this.currentAltitude = currentAltitude;
    }

    public String getBarometricPressure() {
        return barometricPressure;
    }

    public void setBarometricPressure(String barometricPressure) {
        this.barometricPressure = barometricPressure;
    }

    public String getEngineCoolantTemp() {
        return engineCoolantTemp;
    }

    public void setEngineCoolantTemp(String engineCoolantTemp) {
        this.engineCoolantTemp = engineCoolantTemp;
    }

    public String getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(String fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public String getEngineLoad() {
        return engineLoad;
    }

    public void setEngineLoad(String engineLoad) {
        this.engineLoad = engineLoad;
    }

    public String getAmbientAirTemp() {
        return ambientAirTemp;
    }

    public void setAmbientAirTemp(String ambientAirTemp) {
        this.ambientAirTemp = ambientAirTemp;
    }

    public String getEngineRpm() {
        return engineRpm;
    }

    public void setEngineRpm(String engineRpm) {
        this.engineRpm = engineRpm;
    }

    public String getIntakeManifoldPressure() {
        return intakeManifoldPressure;
    }

    public void setIntakeManifoldPressure(String intakeManifoldPressure) {
        this.intakeManifoldPressure = intakeManifoldPressure;
    }

    public String getMAF() {
        return MAF;
    }

    public void setMAF(String MAF) {
        this.MAF = MAF;
    }

    public String getTermFuelTrimBank1() {
        return termFuelTrimBank1;
    }

    public void setTermFuelTrimBank1(String termFuelTrimBank1) {
        this.termFuelTrimBank1 = termFuelTrimBank1;
    }

    public String getFuelEconomy() {
        return fuelEconomy;
    }

    public void setFuelEconomy(String fuelEconomy) {
        this.fuelEconomy = fuelEconomy;
    }

    public String getLongTermFuelTrimBank2() {
        return longTermFuelTrimBank2;
    }

    public void setLongTermFuelTrimBank2(String longTermFuelTrimBank2) {
        this.longTermFuelTrimBank2 = longTermFuelTrimBank2;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getAirIntakeTemp() {
        return airIntakeTemp;
    }

    public void setAirIntakeTemp(String airIntakeTemp) {
        this.airIntakeTemp = airIntakeTemp;
    }

    public String getFuelPressure() {
        return fuelPressure;
    }

    public void setFuelPressure(String fuelPressure) {
        this.fuelPressure = fuelPressure;
    }

    public String getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(String currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public String getShortTermFuelBank2() {
        return shortTermFuelBank2;
    }

    public void setShortTermFuelBank2(String shortTermFuelBank2) {
        this.shortTermFuelBank2 = shortTermFuelBank2;
    }

    public String getShortTermFuelBank1() {
        return shortTermFuelBank1;
    }

    public void setShortTermFuelBank1(String shortTermFuelBank1) {
        this.shortTermFuelBank1 = shortTermFuelBank1;
    }

    public String getEngineRunTime() {
        return engineRunTime;
    }

    public void setEngineRunTime(String engineRunTime) {
        this.engineRunTime = engineRunTime;
    }

    public String getThrottlePos() {
        return throttlePos;
    }

    public void setThrottlePos(String throttlePos) {
        this.throttlePos = throttlePos;
    }

    public String getDtcNumber() {
        return dtcNumber;
    }

    public void setDtcNumber(String dtcNumber) {
        this.dtcNumber = dtcNumber;
    }

    public String getTroubleCodes() {
        return troubleCodes;
    }

    public void setTroubleCodes(String troubleCodes) {
        this.troubleCodes = troubleCodes;
    }

    public String getTimingAdvance() {
        return timingAdvance;
    }

    public void setTimingAdvance(String timingAdvance) {
        this.timingAdvance = timingAdvance;
    }

    public String getEquivRatio() {
        return equivRatio;
    }

    public void setEquivRatio(String equivRatio) {
        this.equivRatio = equivRatio;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    @Override
    public String toString() {
        String s = "{" +
                "currentLatitude:" + currentLatitude +
                ", currentLongitude:" + currentLongitude +
                ", currentAltitude:" + currentAltitude +
                ", barometricPressure:" + barometricPressure +
                ", engineCoolantTemp:" + engineCoolantTemp +
                ", fuelLevel:" + fuelLevel +
                ", engineLoad:" + engineLoad +
                ", ambientAirTemp:" + ambientAirTemp +
                ", engineRpm:" + engineRpm +
                ", intakeManifoldPressure:" + intakeManifoldPressure +
                ", MAF:" + MAF +
                ", termFuelTrimBank1:" + termFuelTrimBank1 +
                ", fuelEconomy:" + fuelEconomy +
                ", longTermFuelTrimBank2:" + longTermFuelTrimBank2 +
                ", fuelType:" + fuelType +
                ", airIntakeTemp:" + airIntakeTemp +
                ", fuelPressure:" + fuelPressure +
                ", currentSpeed:" + currentSpeed +
                ", shortTermFuelBank2:" + shortTermFuelBank2 +
                ", shortTermFuelBank1:" + shortTermFuelBank1 +
                ", engineRunTime:" + engineRunTime +
                ", throttlePos:" + throttlePos +
                ", dtcNumber:" + dtcNumber +
                ", troubleCodes:" + troubleCodes +
                ", timingAdvance:" + timingAdvance +
                ", equivRatio:" + equivRatio +
                ", userId:" + userId +
                ", vehicleId:" + vehicleId +
                "}";

     Log.e("Tripmodelclass", "To string: " + s);
        return s;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(currentLatitude);
        dest.writeString(currentLongitude);
        dest.writeString(currentAltitude);
        dest.writeString(barometricPressure);
        dest.writeString(engineCoolantTemp);
        dest.writeString(fuelLevel);
        dest.writeString(engineLoad);
        dest.writeString(ambientAirTemp);
        dest.writeString(engineRpm);
        dest.writeString(intakeManifoldPressure);
        dest.writeString(MAF);
        dest.writeString(termFuelTrimBank1);
        dest.writeString(fuelEconomy);
        dest.writeString(longTermFuelTrimBank2);
        dest.writeString(fuelType);
        dest.writeString(airIntakeTemp);
        dest.writeString(fuelPressure);
        dest.writeString(currentSpeed);
        dest.writeString(shortTermFuelBank2);
        dest.writeString(shortTermFuelBank1);
        dest.writeString(engineRunTime);
        dest.writeString(throttlePos);
        dest.writeString(dtcNumber);
        dest.writeString(troubleCodes);
        dest.writeString(timingAdvance);
        dest.writeString(equivRatio);
        dest.writeString(userId);
        dest.writeString(vehicleId);
    }
}
