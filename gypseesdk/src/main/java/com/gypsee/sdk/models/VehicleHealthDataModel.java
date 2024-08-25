package com.gypsee.sdk.models;

public class VehicleHealthDataModel {

    String code, title, value, thresholdValue;
    boolean dataNotSupported, error;

    public VehicleHealthDataModel(String code, String title, String value, String thresholdValue, boolean dataNotSupported, boolean error) {
        this.code = code;
        this.title = title;
        this.value = value;
        this.thresholdValue = thresholdValue;
        this.dataNotSupported = dataNotSupported;
        this.error = error;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(String thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public boolean getDataNotSupported() {
        return dataNotSupported;
    }

    public void setDataNotSupported(boolean dataNotSupported) {
        this.dataNotSupported = dataNotSupported;
    }

    public boolean getError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
