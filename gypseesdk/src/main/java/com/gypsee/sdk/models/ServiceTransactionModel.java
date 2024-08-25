package com.gypsee.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ServiceTransactionModel implements Parcelable {

    String bookingId, amount, paymentStatus, gypseeService, location, upiRefId, serviceDate, serviceTime, receiverUPIId, createdOn, lastUpdatedOn;

    public ServiceTransactionModel(String bookingId, String amount, String paymentStatus, String gypseeService, String location, String upiRefId,
                                   String serviceDate, String serviceTime,
                                   String receiverUPIId, String createdOn, String lastUpdatedOn) {
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.gypseeService = gypseeService;
        this.location = location;
        this.upiRefId = upiRefId;
        this.serviceDate = serviceDate;
        this.serviceTime = serviceTime;
        this.receiverUPIId = receiverUPIId;
        this.createdOn = createdOn;
        this.lastUpdatedOn = lastUpdatedOn;
    }


    protected ServiceTransactionModel(Parcel in) {
        bookingId = in.readString();
        amount = in.readString();
        paymentStatus = in.readString();
        gypseeService = in.readString();
        location = in.readString();
        upiRefId = in.readString();
        serviceDate = in.readString();
        serviceTime = in.readString();
        receiverUPIId = in.readString();
        createdOn = in.readString();
        lastUpdatedOn = in.readString();
    }

    public static final Creator<ServiceTransactionModel> CREATOR = new Creator<ServiceTransactionModel>() {
        @Override
        public ServiceTransactionModel createFromParcel(Parcel in) {
            return new ServiceTransactionModel(in);
        }

        @Override
        public ServiceTransactionModel[] newArray(int size) {
            return new ServiceTransactionModel[size];
        }
    };

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getGypseeService() {
        return gypseeService;
    }

    public void setGypseeService(String gypseeService) {
        this.gypseeService = gypseeService;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUpiRefId() {
        return upiRefId;
    }

    public void setUpiRefId(String upiRefId) {
        this.upiRefId = upiRefId;
    }

    public String getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(String serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(String serviceTime) {
        this.serviceTime = serviceTime;
    }

    public String getReceiverUPIId() {
        return receiverUPIId;
    }

    public void setReceiverUPIId(String receiverUPIId) {
        this.receiverUPIId = receiverUPIId;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(bookingId);
        parcel.writeString(amount);
        parcel.writeString(paymentStatus);
        parcel.writeString(gypseeService);
        parcel.writeString(location);
        parcel.writeString(upiRefId);
        parcel.writeString(serviceDate);
        parcel.writeString(serviceTime);
        parcel.writeString(receiverUPIId);
        parcel.writeString(createdOn);
        parcel.writeString(lastUpdatedOn);
    }
}
