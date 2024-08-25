package com.gypsee.sdk.models;

public class UserOrderModel {

    private String orderId, refNo, status, errorInfo, sku, amount;

    public UserOrderModel(String orderId, String refNo, String status, String errorInfo, String sku, String amount) {
        this.orderId = orderId;
        this.refNo = refNo;
        this.status = status;
        this.errorInfo = errorInfo;
        this.sku = sku;
        this.amount = amount;
    }


    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }
}
