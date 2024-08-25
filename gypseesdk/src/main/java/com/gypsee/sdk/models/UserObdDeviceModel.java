package com.gypsee.sdk.models;

public class UserObdDeviceModel {

    String id, macAddress, deviceStats, lastConnectedTime, createdOn, lastUpdatedOn;
    boolean active;

    public UserObdDeviceModel(String id, String macAddress, String deviceStats, String lastConnectedTime, String createdOn, String lastUpdatedOn, boolean active) {
        this.id = id;
        this.macAddress = macAddress;
        this.deviceStats = deviceStats;
        this.lastConnectedTime = lastConnectedTime;
        this.createdOn = createdOn;
        this.lastUpdatedOn = lastUpdatedOn;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getDeviceStats() {
        return deviceStats;
    }

    public void setDeviceStats(String deviceStats) {
        this.deviceStats = deviceStats;
    }

    public String getLastConnectedTime() {
        return lastConnectedTime;
    }

    public void setLastConnectedTime(String lastConnectedTime) {
        this.lastConnectedTime = lastConnectedTime;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(String lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
