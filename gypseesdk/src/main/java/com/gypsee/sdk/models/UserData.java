package com.gypsee.sdk.models;

import com.google.gson.annotations.SerializedName;

public class UserData {

    @SerializedName("vehicleRegistrationNo")
    private String vehicleRegistrationNo;

    @SerializedName("customerName")
    private String customerName;

    @SerializedName("vehicleCompany")
    private String vehicleCompany;

    @SerializedName("vehicleModel")
    private String vehicleModel;

    @SerializedName("mobileNumber")
    private String mobileNumber;

    @SerializedName("purchaseDate")
    private String purchaseDate;

    public String getVehicleRegistrationNo() {
        return vehicleRegistrationNo;
    }

    public void setVehicleRegistrationNo(String vehicleRegistrationNo) {
        this.vehicleRegistrationNo = vehicleRegistrationNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getVehicleCompany() {
        return vehicleCompany;
    }

    public void setVehicleCompany(String vehicleCompany) {
        this.vehicleCompany = vehicleCompany;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}
