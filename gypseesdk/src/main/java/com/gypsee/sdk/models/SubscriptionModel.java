package com.gypsee.sdk.models;

public class SubscriptionModel {

    private boolean active;
    private String couponCode, createdOn, endDate, id, lastUpdatedOn, startDate;
    private double discountAmount, subscriptionAmount, paidAmount;

    public SubscriptionModel(boolean active,
                             String couponCode,
                             String createdOn,
                             String endDate,
                             String id,
                             String lastUpdatedOn,
                             String startDate,
                             double discountAmount,
                             double subscriptionAmount,
                             double paidAmount) {
        this.active = active;
        this.couponCode = couponCode;
        this.createdOn = createdOn;
        this.endDate = endDate;
        this.id = id;
        this.lastUpdatedOn = lastUpdatedOn;
        this.startDate = startDate;
        this.discountAmount = discountAmount;
        this.subscriptionAmount = subscriptionAmount;
        this.paidAmount = paidAmount;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(String lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getSubscriptionAmount() {
        return subscriptionAmount;
    }

    public void setSubscriptionAmount(double subscriptionAmount) {
        this.subscriptionAmount = subscriptionAmount;
    }
}
