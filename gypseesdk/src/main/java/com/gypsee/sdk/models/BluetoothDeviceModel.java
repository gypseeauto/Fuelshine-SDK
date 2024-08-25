package com.gypsee.sdk.models;

public class BluetoothDeviceModel {

    private String id, deviceName, bluetoothName, bluetoothType, bluetoothProfileSupported, macAddress, imageUrl, lastConnectedTime, createdOn, lastUpdatedOn, category;
    private boolean nowConnected;
    private DeviceInformationModel informationModel;

    public BluetoothDeviceModel(String id,
                                String deviceName,
                                String category,
                                String bluetoothName,
                                String bluetoothType,
                                String bluetoothProfileSupported,
                                String macAddress,
                                String imageUrl,
                                String lastConnectedTime,
                                String createdOn,
                                String lastUpdatedOn,
                                boolean nowConnected,
                                DeviceInformationModel informationModel) {
        this.id = id;
        this.deviceName = deviceName;
        this.bluetoothName = bluetoothName;
        this.bluetoothType = bluetoothType;
        this.bluetoothProfileSupported = bluetoothProfileSupported;
        this.macAddress = macAddress;
        this.imageUrl = imageUrl;
        this.lastConnectedTime = lastConnectedTime;
        this.createdOn = createdOn;
        this.lastUpdatedOn = lastUpdatedOn;
        this.nowConnected = nowConnected;
        this.informationModel = informationModel;
        this.category = category;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getBluetoothName() {
        return bluetoothName;
    }

    public void setBluetoothName(String bluetoothName) {
        this.bluetoothName = bluetoothName;
    }

    public String getBluetoothType() {
        return bluetoothType;
    }

    public void setBluetoothType(String bluetoothType) {
        this.bluetoothType = bluetoothType;
    }

    public String getBluetoothProfileSupported() {
        return bluetoothProfileSupported;
    }

    public void setBluetoothProfileSupported(String bluetoothProfileSupported) {
        this.bluetoothProfileSupported = bluetoothProfileSupported;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public boolean isNowConnected() {
        return nowConnected;
    }

    public void setNowConnected(boolean nowConnected) {
        this.nowConnected = nowConnected;
    }

    public DeviceInformationModel getInformationModel() {
        return informationModel;
    }

    public void setInformationModel(DeviceInformationModel informationModel) {
        this.informationModel = informationModel;
    }
}
