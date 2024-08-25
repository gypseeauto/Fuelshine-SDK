package com.gypsee.sdk.models;

public class CarServiceTypeModel {

    String id, serviceName, description, amount, gst, totalAmount, locations, timeSlots, createdOn,
            lastUpdatedOn;

    boolean active,timeSlotInfoRequired;

    public CarServiceTypeModel(String id, String serviceName, String description, String amount, String gst, String totalAmount, String locations, String timeSlots, String createdOn, String lastUpdatedOn, boolean active, boolean timeSlotInfoRequired) {
        this.id = id;
        this.serviceName = serviceName;
        this.description = description;
        this.amount = amount;
        this.gst = gst;
        this.totalAmount = totalAmount;
        this.locations = locations;
        this.timeSlots = timeSlots;
        this.createdOn = createdOn;
        this.lastUpdatedOn = lastUpdatedOn;
        this.active = active;
        this.timeSlotInfoRequired = timeSlotInfoRequired;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getLocations() {
        return locations;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }

    public String getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(String timeSlots) {
        this.timeSlots = timeSlots;
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

    public boolean isTimeSlotInfoRequired() {
        return timeSlotInfoRequired;
    }
}
