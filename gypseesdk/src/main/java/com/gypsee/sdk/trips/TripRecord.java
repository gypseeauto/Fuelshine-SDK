package com.gypsee.sdk.trips;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TripRecord implements Parcelable {

    /// record id for database use (primary key)
    private String id;
    private int safeKm;

    /// the date the trip started
    private String startDate, endDate;


    private Integer engineRpmMax = 0;

    private Integer speed = 0;

    private String engineRuntime;
    private String distanceCovered = "RESTRICTED";

    private int alertCount;

    private String startLat, startLong, endLat, endLong, startLocationName, destinationName, lastUpdatedOn,tripSavedAmount,tripSavingsCommission;
    private double vhsScore, mileage;
    private long tripDuration;


    public String getTripSavingsCommission() {
        return tripSavingsCommission;
    }

    public void setTripSavingsCommission(String tripSavingsCommission) {
        this.tripSavingsCommission = tripSavingsCommission;
    }

    public TripRecord(String id, String startDate, String endDate, Integer engineRpmMax, Integer speed, String engineRuntime, String distanceCovered, int alertCount, String startLat, String startLong, String endLat, String endLong, String startLocationName, String destinationName, double vhsScore, double mileage, long tripDuration, int safeKm, String lastUpdatedOn, String tripSavedAmount, String tripSavingsCommission) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.engineRpmMax = engineRpmMax;
        this.speed = speed;
        this.engineRuntime = engineRuntime;
        this.distanceCovered = distanceCovered;
        this.alertCount = alertCount;

        this.startLat = startLat;
        this.startLong = startLong;
        this.endLat = endLat;
        this.endLong = endLong;
        this.startLocationName = startLocationName;
        this.destinationName = destinationName;
        this.vhsScore = vhsScore;
        this.mileage = mileage;
        this.tripDuration = tripDuration;
        this.safeKm = safeKm;
        this.lastUpdatedOn = lastUpdatedOn;
        this.tripSavedAmount = tripSavedAmount;
        this.tripSavingsCommission = tripSavingsCommission;
    }

    public String getTripSavedAmount() {
        return tripSavedAmount;
    }

    public void setTripSavedAmount(String tripSavedAmount) {
        this.tripSavedAmount = tripSavedAmount;
    }

    protected TripRecord(Parcel in) {
        id = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        if (in.readByte() == 0) {
            engineRpmMax = null;
        } else {
            engineRpmMax = in.readInt();
        }
        if (in.readByte() == 0) {
            speed = null;
        } else {
            speed = in.readInt();
        }
        engineRuntime = in.readString();
        distanceCovered = in.readString();
        alertCount = in.readInt();
        startLat = in.readString();
        startLong = in.readString();
        endLat = in.readString();
        endLong = in.readString();
        startLocationName = in.readString();
        destinationName = in.readString();
        vhsScore = in.readDouble();
        mileage = in.readDouble();
        tripDuration = in.readLong();
        safeKm = in.readInt();
        lastUpdatedOn = in.readString();
        tripSavedAmount = in.readString();
        tripSavingsCommission = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(startDate);
        dest.writeString(endDate);
        if (engineRpmMax == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(engineRpmMax);
        }
        if (speed == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(speed);
        }
        dest.writeString(engineRuntime);
        dest.writeString(distanceCovered);
        dest.writeInt(alertCount);
        dest.writeString(startLat);
        dest.writeString(startLong);
        dest.writeString(endLat);
        dest.writeString(endLong);
        dest.writeString(startLocationName);
        dest.writeString(destinationName);
        dest.writeDouble(vhsScore);
        dest.writeDouble(mileage);
        dest.writeLong(tripDuration);
        dest.writeInt(safeKm);
        dest.writeString(lastUpdatedOn);
        dest.writeString(tripSavedAmount);
        dest.writeString(tripSavingsCommission);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TripRecord> CREATOR = new Creator<TripRecord>() {
        @Override
        public TripRecord createFromParcel(Parcel in) {
            return new TripRecord(in);
        }

        @Override
        public TripRecord[] newArray(int size) {
            return new TripRecord[size];
        }
    };

    public int getSafeKm() {
        return safeKm;
    }

    public void setSafeKm(int safeKm) {
        this.safeKm = safeKm;
    }

    public int getAlertCount() {
        return alertCount;
    }

    public void setAlertCount(int alertCount) {
        this.alertCount = alertCount;
    }

    public String getDistanceCovered() {
        return distanceCovered;
    }

    public String getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(String value){
        this.lastUpdatedOn = value;
    }

    public void setDistanceCovered(String distanceCovered) {
        this.distanceCovered = distanceCovered;
    }

    public Integer getSpeedMax() {
        return speed;
    }

    public void setSpeedMax(int value) {
        if (this.speed < value)
            speed = value;
    }

    public long getTripDuration() {
        return tripDuration;
    }

    public void setTripDuration(long tripDuration) {
        this.tripDuration = tripDuration;
    }

    public void setSpeedMax(String value) {
        setSpeedMax(Integer.parseInt(value));
    }

    /**
     * DESCRIPTION:
     * Getter method for the id attribute.
     *
     * @return Integer - the id value.
     */


    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * DESCRIPTION:
     * Getter method for the date attribute.
     *
     * @return Date - the start date value
     */


    public Integer getEngineRpmMax() {
        return this.engineRpmMax;
    }

    public void setEngineRpmMax(Integer value) {
        if (this.engineRpmMax < value) {
            this.engineRpmMax = value;
        }
    }

    public double getVhsScore() {
        return vhsScore;
    }

    public void setVhsScore(double vhsScore) {
        this.vhsScore = vhsScore;
    }

    public double getMileage() {
        return mileage;
    }

    public void setMileage(double mileage) {
        this.mileage = mileage;
    }

    public void setEngineRpmMax(String value) {
        setEngineRpmMax(Integer.parseInt(value));
    }

    /**
     * DESCRIPTION:
     * Getter method for the date attribute as a String value.
     *
     * @return String - the date value (MM/dd/yyyy).
     */
    public String getStartDateString() {
        //todo
        //return dateFormatter.format(this.startDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        return sdf.format(this.startDate);
    }

    public String getEngineRuntime() {
        return engineRuntime;
    }

    public void setEngineRuntime(String value) {
        if (!value.equals("00:00:00")) {
            this.engineRuntime = value;
        }
    }

    public String getId() {
        return id;
    }

    public Integer getSpeed() {
        return speed;
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

    public void setId(String id) {
        this.id = id;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public void setStartLat(String startLat) {
        this.startLat = startLat;
    }

    public void setStartLong(String startLong) {
        this.startLong = startLong;
    }

    public void setEndLat(String endLat) {
        this.endLat = endLat;
    }

    public void setEndLong(String endLong) {
        this.endLong = endLong;
    }

    @Override
    public String toString() {
        return "TripRecord{" +
                "id='" + id + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", engineRpmMax=" + engineRpmMax +
                ", speed=" + speed +
                ", engineRuntime='" + engineRuntime + '\'' +
                ", distanceCovered='" + distanceCovered + '\'' +
                ", alertCount=" + alertCount +
                ", startLat='" + startLat + '\'' +
                ", startLong='" + startLong + '\'' +
                ", endLat='" + endLat + '\'' +
                ", endLong='" + endLong + '\'' +
                ", safeKm='" + safeKm + '\'' +
                ", lastUpdatedOn='" + lastUpdatedOn + '\'' +
                ", tripSavedAmount='" + tripSavedAmount + '\'' +
                ", tripSavingsCommission='" + tripSavingsCommission + '\'' +
                '}';
    }

    public String getStartLocationName() {
        return startLocationName;
    }

    public String getDestinationName() {
        return destinationName;
    }
}
