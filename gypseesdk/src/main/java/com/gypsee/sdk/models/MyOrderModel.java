package com.gypsee.sdk.models;

import androidx.annotation.Nullable;

public class MyOrderModel {

    private String name, image;
    private String status;

    @Nullable
    private String orderId;

    @Nullable
    private String activationCode;
    @Nullable
    private String expiry;
    @Nullable
    private String cardNumber;
    @Nullable
    private String cardPin;
    @Nullable
    private String errorInfo;
    @Nullable
    private String sku;
    @Nullable
    private String activationUrl;

    private boolean isCardActivated;
    private String amount;
    @Nullable
    private String refNo;

    public MyOrderModel(
            String name,
            String image,
            String status,
            @Nullable String activationCode,
            @Nullable String expiry,
            @Nullable String cardNumber,
            @Nullable String cardPin,
            @Nullable String errorInfo,
            @Nullable String sku,
            @Nullable String activationUrl,
            boolean isCardActivated,
            @Nullable String orderId,
            String amount,
            String refNo
            ) {
        this.name = name;
        this.image = image;
        this.status = status;
        this.activationCode = activationCode;
        this.expiry = expiry;
        this.cardNumber = cardNumber;
        this.cardPin = cardPin;
        this.errorInfo = errorInfo;
        this.sku = sku;
        this.activationUrl = activationUrl;
        this.isCardActivated = isCardActivated;
        this.orderId = orderId;
        this.amount = amount;
        this.refNo = refNo;
    }

    @Nullable
    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(@Nullable String refNo) {
        this.refNo = refNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public boolean isCardActivated() {
        return isCardActivated;
    }

    public void setCardActivated(boolean cardActivated) {
        isCardActivated = cardActivated;
    }

    @Nullable
    public String getSku() {
        return sku;
    }

    public void setSku(@Nullable String sku) {
        this.sku = sku;
    }

    @Nullable
    public String getActivationUrl() {
        return activationUrl;
    }

    public void setActivationUrl(@Nullable String activationUrl) {
        this.activationUrl = activationUrl;
    }

    @Nullable
    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(@Nullable String errorInfo) {
        this.errorInfo = errorInfo;
    }

    @Nullable
    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(@Nullable String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Nullable
    public String getCardPin() {
        return cardPin;
    }

    public void setCardPin(@Nullable String cardPin) {
        this.cardPin = cardPin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }
}
